package org.zenith.base.font;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FreeType;
import org.slf4j.Logger;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.utility.render.ImmediateRenderer;

public final class HintedText {
   private static final float MAX_EM_PX = 24.0F;
   private static final float MIN_EM_PX = 5.0F;
   private static final int SUBPIXEL_BINS = 4;
   private static final int ATLAS_SIZE = 1024;
   private static final int PADDING = 1;
   private static final float GAMMA = 0.8F;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int LOAD_TARGET_LIGHT = 65536;
   private static final byte[] GAMMA_LUT = new byte[256];
   private static long library;
   private static boolean broken;
   private static final Map<String, HintedText.Face> FACES;
   private static NativeImage atlasImage;
   private static NativeImageBackedTexture atlasTexture;
   private static int dirtyX0;
   private static int dirtyY0;
   private static int dirtyX1;
   private static int dirtyY1;
   private static int shelfX;
   private static int shelfY;
   private static int shelfHeight;
   private static final Long2ObjectOpenHashMap<HintedText.Glyph> GLYPHS;
   private static int nextFaceId;
   private static BufferAllocator allocator;
   private static BufferBuilder builder;
   private static final float[] batchShaderColor;
   private static final long STILL_NANOS = 300000000L;
   private static final long FORGET_NANOS = 5000000000L;
   private static final long PRUNE_NANOS = 1000000000L;
   private static final Map<String, ArrayList<HintedText.Placement>> PLACEMENTS;
   private static final Object2IntOpenHashMap<String> OCCURRENCES;
   private static long lastPrune;

   private HintedText() {
   }

   public static boolean render(MsdfFont var0, String var1, float var2, int var3, Matrix4f var4, float var5, float var6, float var7) {
      String var8 = var0.getHintedFace();
      if (var2 > 0.0F && var2 <= 5.0F && ImmediateRenderer.method8005() && var8 != null && !broken && !var1.isEmpty()) {
         float var9 = var4.m00();
         if (!(var9 <= 0.0F)
            && !(Math.abs(var4.m01()) > 1.0E-4F)
            && !(Math.abs(var4.m10()) > 1.0E-4F)
            && !(Math.abs(var4.m11() - var9) > 1.0E-4F * Math.max(1.0F, var9))) {
            float var10 = MinecraftClient.getInstance().getWindow().getScaleFactor();
            float var11 = var2 * var9 * var10;
            if (inMotion(var1, var11)) {
               return false;
            } else if (!(var11 < 5.0F) && !(var11 > 24.0F)) {
               HintedText.Face var12 = face(var8);
               if (var12 == null) {
                  return false;
               } else {
                  int var13 = Math.round(var11 * 4.0F);
                  float var14 = Math.round((var4.m11() * var6 + var4.m31()) * var10);
                  float var15 = var4.m22() * var7 + var4.m32();
                  BufferBuilder var16 = begin();
                  int var17 = -1;
                  boolean var18 = false;
                  float var19 = var5;

                  for (int var20 = 0; var20 < var1.length(); var20++) {
                     char var21 = var1.charAt(var20);
                     if (var18) {
                        var18 = false;
                     } else if (var21 == 167) {
                        var18 = true;
                     } else {
                        MsdfGlyph var22 = var0.getGlyph(var21);
                        if (var22 == null) {
                           char var23 = MsdfFont.normalizeGlyphChar(var21);
                           var22 = var23 == var21 ? null : var0.getGlyph(var23);
                           if (var22 == null) {
                              continue;
                           }

                           var21 = var23;
                        }

                        var19 += var0.kerning(var17, var21) * var2;
                        float var32 = (var9 * var19 + var4.m30()) * var10;
                        float var24 = (float)Math.floor(var32);
                        int var25 = Math.round((var32 - var24) * 4.0F);
                        if (var25 == 4) {
                           var24++;
                           var25 = 0;
                        }

                        HintedText.Glyph var26 = glyph(var12, var21, var13, var25);
                        if (builder != var16) {
                           var16 = begin();
                        }

                        if (var26 != null && var26.width > 0) {
                           float var27 = (var24 + var26.left) / var10;
                           float var28 = (var14 - var26.top) / var10;
                           float var29 = var27 + var26.width / var10;
                           float var30 = var28 + var26.height / var10;
                           var16.vertex(var27, var28, var15).texture(var26.u0, var26.v0).color(var3);
                           var16.vertex(var27, var30, var15).texture(var26.u0, var26.v1).color(var3);
                           var16.vertex(var29, var30, var15).texture(var26.u1, var26.v1).color(var3);
                           var16.vertex(var29, var28, var15).texture(var26.u1, var26.v0).color(var3);
                        }

                        var19 += var22.getWidth(var2);
                        var17 = var21;
                     }
                  }

                  return true;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean inMotion(String var0, float var1) {
      long var2 = System.nanoTime();
      int var4 = OCCURRENCES.addTo(var0, 1);
      var var5 = PLACEMENTS.computeIfAbsent(var0, var0x -> new ArrayList<>(1));
      HintedText.Placement var6;
      if (var4 < var5.size()) {
         var6 = (HintedText.Placement)var5.get(var4);
         if (Math.abs(var6.em - var1) > 0.001F) {
            var6.movedAt = var2;
         }
      } else {
         var6 = new HintedText.Placement();
         var6.movedAt = var2 - 300000000L;
         var5.add(var6);
      }

      var6.em = var1;
      var6.seenAt = var2;
      return var2 - var6.movedAt < 300000000L;
   }

   public static void endFrame() {
      OCCURRENCES.clear();
      long var0 = System.nanoTime();
      if (var0 - lastPrune >= 1000000000L) {
         lastPrune = var0;
         PLACEMENTS.values().removeIf(var2 -> {
            var2.removeIf(var2x -> var0 - var2x.seenAt > 5000000000L);
            return var2.isEmpty();
         });
      }
   }

   public static boolean pending() {
      return builder != null;
   }

   public static void flush() {
      if (builder != null) {
         BufferBuilder var0 = builder;
         builder = null;
         BuiltBuffer var1 = var0.endNullable();
         if (var1 == null) {
            allocator.clear();
         } else {
            if (dirtyX1 > dirtyX0 && dirtyY1 > dirtyY0) {
               uploadDirty();
            }

            float[] var2 = RenderState.method1885();
            boolean var3 = var2[0] != batchShaderColor[0] || var2[1] != batchShaderColor[1] || var2[2] != batchShaderColor[2] || var2[3] != batchShaderColor[3];
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1877();
            if (var3) {
               RenderState.method1883(batchShaderColor[0], batchShaderColor[1], batchShaderColor[2], batchShaderColor[3]);
            }

            RenderState.method1888(0, atlasTexture.getGlTextureView(), FilterMode.NEAREST);
            RenderState.method1898("core/position_tex_color");

            try {
               ImmediateRenderer.method8015(var1);
            } finally {
               RenderState.method1893(0);
               if (var3) {
                  RenderState.method1883(var2[0], var2[1], var2[2], var2[3]);
               }

               RenderState.method1876();
               RenderState.method1872();
               allocator.clear();
            }
         }
      }
   }

   private static BufferBuilder begin() {
      MsdfRenderer.flushMsdfPending();
      float[] var0 = RenderState.method1885();
      if (builder != null
         && (var0[0] != batchShaderColor[0] || var0[1] != batchShaderColor[1] || var0[2] != batchShaderColor[2] || var0[3] != batchShaderColor[3])) {
         flush();
      }

      if (builder == null) {
         if (allocator == null) {
            allocator = new BufferAllocator(32768);
         }

         ensureAtlas();
         builder = new BufferBuilder(allocator, DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         System.arraycopy(var0, 0, batchShaderColor, 0, 4);
      }

      return builder;
   }

   private static HintedText.Face face(String var0) {
      HintedText.Face var1 = FACES.get(var0);
      if (var1 != null) {
         return var1.handle == null ? null : var1;
      } else {
         var1 = new HintedText.Face();
         FACES.put(var0, var1);

         try {
            if (library == 0L) {
               MemoryStack var2 = MemoryStack.stackPush();

               try {
                  PointerBuffer var3 = var2.mallocPointer(1);
                  check(FreeType.FT_Init_FreeType(var3), "FT_Init_FreeType");
                  library = var3.get(0);
               } catch (Throwable var12) {
                  if (var2 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var9) {
                        var12.addSuppressed(var9);
                     }
                  }

                  throw var12;
               }

               if (var2 != null) {
                  var2.close();
               }
            }

            Identifier var15 = Zenith.method1501("fonts/ttf/" + var0 + ".otf");

            byte[] var16;
            try (InputStream var4 = MinecraftClient.getInstance().getResourceManager().getResourceOrThrow(var15).getInputStream()) {
               var16 = var4.readAllBytes();
            }

            var1.data = MemoryUtil.memAlloc(var16.length).put(var16).flip();
            MemoryStack var17 = MemoryStack.stackPush();

            try {
               PointerBuffer var5 = var17.mallocPointer(1);
               check(FreeType.FT_New_Memory_Face(library, var1.data, 0L, var5), "FT_New_Memory_Face " + var0);
               var1.handle = FT_Face.create(var5.get(0));
            } catch (Throwable var10) {
               if (var17 != null) {
                  try {
                     var17.close();
                  } catch (Throwable var7) {
                     var10.addSuppressed(var7);
                  }
               }

               throw var10;
            }

            if (var17 != null) {
               var17.close();
            }

            var1.id = nextFaceId++;
            return var1;
         } catch (Throwable var13) {
            LOGGER.warn("Hinted text disabled for {}: {}", var0, var13.toString());
            if (library == 0L) {
               broken = true;
            }

            return null;
         }
      }
   }

   private static HintedText.Glyph glyph(HintedText.Face var0, char var1, int var2, int var3) {
      long var4 = (long)var0.id << 40 | (long)var2 << 24 | (long)var3 << 16 | var1;
      HintedText.Glyph var6 = (HintedText.Glyph)GLYPHS.get(var4);
      if (var6 != null) {
         return var6;
      } else {
         HintedText.Glyph var7 = rasterize(var0, var1, var2, var3);
         if (var7 == null) {
            var7 = HintedText.Glyph.EMPTY;
         }

         GLYPHS.put(var4, var7);
         return var7;
      }
   }

   private static HintedText.Glyph rasterize(HintedText.Face var0, char var1, int var2, int var3) {
      FT_Face var4 = var0.handle;
      if (var0.sizeQuarter != var2) {
         if (FreeType.FT_Set_Char_Size(var4, 0L, var2 * 16L, 72, 72) != 0) {
            return null;
         }

         var0.sizeQuarter = var2;
      }

      int var5 = FreeType.FT_Get_Char_Index(var4, var1);
      if (var5 == 0) {
         return null;
      } else if (FreeType.FT_Load_Glyph(var4, var5, 65544) != 0) {
         return null;
      } else {
         FT_GlyphSlot var6 = var4.glyph();
         if (var6 == null) {
            return null;
         } else {
            if (var3 != 0) {
               FreeType.FT_Outline_Translate(var6.outline(), var3 * 16L, 0L);
            }

            if (FreeType.FT_Render_Glyph(var6, 1) != 0) {
               return null;
            } else {
               FT_Bitmap var7 = var6.bitmap();
               int var8 = var7.width();
               int var9 = var7.rows();
               HintedText.Glyph var10 = new HintedText.Glyph();
               var10.left = var6.bitmap_left();
               var10.top = var6.bitmap_top();
               if (var8 != 0 && var9 != 0) {
                  int var11 = var7.pitch();
                  ByteBuffer var12 = var7.buffer(Math.abs(var11) * var9);
                  if (var12 == null) {
                     return var10;
                  } else {
                     if (!reserve(var8, var9)) {
                        resetAtlas();
                        if (!reserve(var8, var9)) {
                           return null;
                        }
                     }

                     int var13 = shelfX - var8 - 1;
                     int var14 = shelfY;

                     for (int var15 = 0; var15 < var9; var15++) {
                        int var16 = var11 >= 0 ? var15 * var11 : (var9 - 1 - var15) * -var11;

                        for (int var17 = 0; var17 < var8; var17++) {
                           int var18 = GAMMA_LUT[var12.get(var16 + var17) & 255] & 255;
                           atlasImage.setColorArgb(var13 + var17, var14 + var15, var18 << 24 | 16777215);
                        }
                     }

                     markDirty(var13, var14, var8, var9);
                     var10.width = var8;
                     var10.height = var9;
                     var10.u0 = var13 / 1024.0F;
                     var10.v0 = var14 / 1024.0F;
                     var10.u1 = (var13 + var8) / 1024.0F;
                     var10.v1 = (var14 + var9) / 1024.0F;
                     return var10;
                  }
               } else {
                  return var10;
               }
            }
         }
      }
   }

   private static boolean reserve(int var0, int var1) {
      if (var0 + 1 <= 1024 && var1 + 1 <= 1024) {
         if (shelfX + var0 + 1 > 1024) {
            shelfY = shelfY + shelfHeight + 1;
            shelfX = 0;
            shelfHeight = 0;
         }

         if (shelfY + var1 + 1 > 1024) {
            return false;
         } else {
            shelfX += var0 + 1;
            shelfHeight = Math.max(shelfHeight, var1);
            return true;
         }
      } else {
         return false;
      }
   }

   private static void ensureAtlas() {
      if (atlasTexture == null) {
         atlasImage = new NativeImage(1024, 1024, true);
         atlasImage.fillRect(0, 0, 1024, 1024, 16777215);
         atlasTexture = new NativeImageBackedTexture(() -> "zenith hinted text", atlasImage);
         markDirty(0, 0, 1024, 1024);
      }
   }

   private static void markDirty(int var0, int var1, int var2, int var3) {
      dirtyX0 = Math.min(dirtyX0, var0);
      dirtyY0 = Math.min(dirtyY0, var1);
      dirtyX1 = Math.max(dirtyX1, var0 + var2);
      dirtyY1 = Math.max(dirtyY1, var1 + var3);
   }

   private static void uploadDirty() {
      int var0 = dirtyX0;
      int var1 = dirtyY0;
      int var2 = dirtyX1 - dirtyX0;
      int var3 = dirtyY1 - dirtyY0;
      dirtyX0 = 1024;
      dirtyY0 = 1024;
      dirtyX1 = 0;
      dirtyY1 = 0;
      GpuTexture var4 = atlasTexture.getGlTexture();
      if (var4 != null && (var0 != 0 || var1 != 0 || var2 != 1024 || var3 != 1024)) {
         RenderSystem.getDevice().createCommandEncoder().writeToTexture(var4, atlasImage, 0, 0, var0, var1, var2, var3, var0, var1);
      } else {
         atlasTexture.upload();
      }
   }

   private static void resetAtlas() {
      if (builder != null) {
         flush();
      }

      GLYPHS.clear();
      atlasImage.fillRect(0, 0, 1024, 1024, 16777215);
      shelfX = 0;
      shelfY = 0;
      shelfHeight = 0;
      markDirty(0, 0, 1024, 1024);
   }

   private static void check(int var0, String var1) {
      if (var0 != 0) {
         throw new IllegalStateException(var1 + " failed: " + var0);
      }
   }

   static {
      for (int var0 = 0; var0 < 256; var0++) {
         GAMMA_LUT[var0] = (byte)Math.round(255.0 * Math.pow(var0 / 255.0, 0.8F));
      }

      FACES = new HashMap<>();
      dirtyX0 = 1024;
      dirtyY0 = 1024;
      GLYPHS = new Long2ObjectOpenHashMap();
      batchShaderColor = new float[4];
      PLACEMENTS = new HashMap<>();
      OCCURRENCES = new Object2IntOpenHashMap();
   }

   private static final class Face {
      public FT_Face handle;
      public ByteBuffer data;
      public int id;
      public int sizeQuarter = -1;
   }

   private static final class Glyph {
      public static final HintedText.Glyph EMPTY = new HintedText.Glyph();
      public int width;
      public int height;
      public int left;
      public int top;
      public float u0;
      public float v0;
      public float u1;
      public float v1;
   }

   private static final class Placement {
      public float em;
      public long movedAt;
      public long seenAt;
   }
}
