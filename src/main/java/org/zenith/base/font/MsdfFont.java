package org.zenith.base.font;

import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.utility.render.display.base.GlowColor;

public final class MsdfFont implements MinecraftAccess {
   private static final int WIDTH_CACHE_LIMIT = 4096;
   private final String name;
   private final AbstractTexture texture;
   private final FontData.AtlasData atlas;
   private final FontData.MetricsData metrics;
   private final MsdfGlyph[] glyphTable;
   private final int glyphOffset;
   private final Int2ObjectMap<Int2FloatMap> kernings;
   private final Map<String, Float> unitWidthCache = new ConcurrentHashMap<>();
   private final String hintedFace;
   private final float ascent;
   private final float baseline;

   private MsdfFont(
      String var1,
      AbstractTexture var2,
      FontData.AtlasData var3,
      FontData.MetricsData var4,
      MsdfGlyph[] var5,
      int var6,
      Int2ObjectMap<Int2FloatMap> var7,
      String var8,
      float var9,
      float var10
   ) {
      this.ascent = var9;
      this.baseline = var10;
      this.name = var1;
      this.texture = var2;
      this.atlas = var3;
      this.metrics = var4;
      this.glyphTable = var5;
      this.glyphOffset = var6;
      this.kernings = var7;
      this.hintedFace = var8;
   }

   public float kerning(int var1, int var2) {
      Int2FloatMap var3 = (Int2FloatMap)this.kernings.get(var1);
      return var3 == null ? 0.0F : var3.get(var2);
   }

   public int getTextureId() {
      return System.identityHashCode(this.texture);
   }

   public GpuTextureView getTextureView() {
      return this.texture.getGlTextureView();
   }

   private MsdfGlyph glyph(int var1) {
      int var2 = var1 - this.glyphOffset;
      return var2 >= 0 && var2 < this.glyphTable.length ? this.glyphTable[var2] : null;
   }

   public boolean hasGlyph(int var1) {
      return this.glyph(var1) != null;
   }

   public MsdfGlyph getGlyph(int var1) {
      return this.glyph(var1);
   }

   public void applyGlyphs(Matrix4f var1, VertexConsumer var2, String var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10) {
      int var11 = -1;
      boolean var12 = false;

      for (int var13 = 0; var13 < var3.length(); var13++) {
         char var14 = var3.charAt(var13);
         if (var12) {
            var12 = false;
         } else if (var14 == 167) {
            var12 = true;
         } else {
            MsdfGlyph var15 = this.glyph(var14);
            if (var15 == null) {
               char var16 = normalizeGlyphChar(var14);
               var15 = var16 == var14 ? null : this.glyph(var16);
               if (var15 == null) {
                  continue;
               }

               var14 = var16;
            }

            Int2FloatMap var17 = (Int2FloatMap)this.kernings.get(var11);
            if (var17 != null) {
               var7 += var17.get(var14) * var4;
            }

            var7 += var15.apply(var1, var2, var4, var7, var8, var9, var10) + var5 + var6;
            var11 = var14;
         }
      }
   }

   public void applyGlyphs(
      Matrix4f var1, VertexConsumer var2, String var3, float var4, float var5, float var6, float var7, float var8, float var9, GlowColor var10
   ) {
      int var11 = -1;
      boolean var12 = false;

      for (int var13 = 0; var13 < var3.length(); var13++) {
         char var14 = var3.charAt(var13);
         if (var12) {
            var12 = false;
         } else if (var14 == 167) {
            var12 = true;
         } else {
            MsdfGlyph var15 = this.glyph(var14);
            if (var15 == null) {
               char var16 = normalizeGlyphChar(var14);
               var15 = var16 == var14 ? null : this.glyph(var16);
               if (var15 == null) {
                  continue;
               }

               var14 = var16;
            }

            Int2FloatMap var17 = (Int2FloatMap)this.kernings.get(var11);
            if (var17 != null) {
               var7 += var17.get(var14) * var4;
            }

            var7 += var15.apply(var1, var2, var4, var7, var8, var9, var10) + var5 + var6;
            var11 = var14;
         }
      }
   }

   public float getWidth(String var1, float var2) {
      if (var1 != null && !var1.isEmpty()) {
         var1 = NameProtectModule.method5149(var1);
         Float var3 = this.unitWidthCache.get(var1);
         if (var3 == null) {
            var3 = this.computeUnitWidth(var1);
            if (this.unitWidthCache.size() >= 4096) {
               this.unitWidthCache.clear();
            }

            this.unitWidthCache.put(var1, var3);
         }

         return var3 * var2;
      } else {
         return 0.0F;
      }
   }

   private float computeUnitWidth(String var1) {
      int var2 = -1;
      float var3 = 0.0F;
      boolean var4 = false;

      for (int var5 = 0; var5 < var1.length(); var5++) {
         char var6 = var1.charAt(var5);
         if (var4) {
            var4 = false;
         } else if (var6 == 167) {
            var4 = true;
         } else {
            MsdfGlyph var7 = this.glyph(var6);
            if (var7 == null) {
               char var8 = normalizeGlyphChar(var6);
               var7 = var8 == var6 ? null : this.glyph(var8);
               if (var7 == null) {
                  continue;
               }

               var6 = var8;
            }

            Int2FloatMap var9 = (Int2FloatMap)this.kernings.get(var2);
            if (var9 != null) {
               var3 += var9.get(var6);
            }

            var3 += var7.getWidth(1.0F);
            var2 = var6;
         }
      }

      return var3;
   }

   public int fitPrefix(String var1, float var2, float var3, float var4) {
      int var5 = -1;
      float var6 = 0.0F;
      boolean var7 = false;
      int var8 = 0;

      for (int var9 = 0; var9 < var1.length(); var9++) {
         char var10 = var1.charAt(var9);
         if (var7) {
            var7 = false;
         } else if (var10 == 167) {
            var7 = true;
         } else {
            MsdfGlyph var11 = this.glyph(var10);
            if (var11 == null) {
               char var12 = normalizeGlyphChar(var10);
               var11 = var12 == var10 ? null : this.glyph(var12);
               if (var11 != null) {
                  var10 = var12;
               }
            }

            if (var11 != null) {
               Int2FloatMap var13 = (Int2FloatMap)this.kernings.get(var5);
               if (var13 != null) {
                  var6 += var13.get(var10);
               }

               var6 += var11.getWidth(1.0F);
               var5 = var10;
            }
         }

         if (!(var6 * var2 + var3 > var4)) {
            var8 = var9 + 1;
         }
      }

      return var8;
   }

   public static char normalizeGlyphChar(char var0) {
      return switch (var0) {
         case '«', '»' -> '"';
         case '·' -> '•';
         case '×' -> 'x';
         case 'ǫ', 'ʠ' -> 'Q';
         case 'ɢ' -> 'G';
         case 'ɪ' -> 'I';
         case 'ɴ' -> 'N';
         case 'ʀ' -> 'R';
         case 'ʏ' -> 'Y';
         case 'ʙ' -> 'B';
         case 'ʜ' -> 'H';
         case 'ʟ' -> 'L';
         case 'ᴀ' -> 'A';
         case 'ᴄ' -> 'C';
         case 'ᴅ' -> 'D';
         case 'ᴇ' -> 'E';
         case 'ᴊ' -> 'J';
         case 'ᴋ' -> 'K';
         case 'ᴍ' -> 'M';
         case 'ᴏ' -> 'O';
         case 'ᴘ' -> 'P';
         case 'ᴛ' -> 'T';
         case 'ᴜ' -> 'U';
         case 'ᴠ' -> 'V';
         case 'ᴡ' -> 'W';
         case 'ᴢ' -> 'Z';
         case '–', '—', '−' -> '-';
         case '…' -> '.';
         case 'ꜰ' -> 'F';
         case 'ꜱ' -> 'S';
         default -> var0;
      };
   }

   public static String normalizeSmallCaps(String var0) {
      int var1 = var0.length();

      for (int var2 = 0; var2 < var1; var2++) {
         char var3 = var0.charAt(var2);
         if (normalizeGlyphChar(var3) != var3) {
            StringBuilder var4 = new StringBuilder(var1);
            var4.append(var0, 0, var2);

            for (int var5 = var2; var5 < var1; var5++) {
               var4.append(normalizeGlyphChar(var0.charAt(var5)));
            }

            return var4.toString();
         }
      }

      return var0;
   }

   public float getTextWidth(Text var1, float var2) {
      return this.getTextWidthWithVanillaFallback(var1, var2);
   }

   private float getTextWidthWithVanillaFallback(Text var1, float var2) {
      TextRenderer var3 = mc.textRenderer;
      float var4 = 0.0F;

      for (FormattedTextProcessor.TextSegment var6 : FormattedTextProcessor.processText(var1, -1)) {
         var4 += this.getWidthWithVanillaFallback(var6.text(), var2, var3);
      }

      return var4;
   }

   private float getWidthWithVanillaFallback(String var1, float var2, TextRenderer var3) {
      var1 = this.normalizeTextForMsdf(var1);
      StringBuilder var4 = new StringBuilder();
      boolean var5 = false;
      boolean var6 = false;
      float var7 = 0.0F;
      int var8 = 0;

      while (var8 < var1.length()) {
         int var9 = var1.codePointAt(var8);
         int var10 = var8 + Character.charCount(var9);
         if (var9 == 167) {
            var8 = this.skipFormattingCode(var1, var10);
         } else {
            boolean var11 = this.canRenderWithMsdf(var9);
            if (var5 && var6 != var11) {
               var7 += this.getRunWidth(var4.toString(), var6, var2, var3);
               var4.setLength(0);
            }

            var4.append(var1, var8, var10);
            var5 = true;
            var6 = var11;
            var8 = var10;
         }
      }

      if (var5) {
         var7 += this.getRunWidth(var4.toString(), var6, var2, var3);
      }

      return var7;
   }

   private float getRunWidth(String var1, boolean var2, float var3, TextRenderer var4) {
      return var2 ? this.getWidth(var1, var3) : var4.getWidth(var1) * this.vanillaTextScale(var4, var3);
   }

   private float vanillaTextScale(TextRenderer var1, float var2) {
      return var2 / Math.max(1.0F, 9.0F);
   }

   private boolean canRenderWithMsdf(int var1) {
      return var1 <= 65535 && this.hasGlyph(var1);
   }

   private int skipFormattingCode(String var1, int var2) {
      return var2 >= var1.length() ? var2 : var2 + Character.charCount(var1.codePointAt(var2));
   }

   private String normalizeTextForMsdf(String var1) {
      return normalizeSmallCaps(var1);
   }

   public Font getFont(float var1) {
      return new Font(this, var1);
   }

   public static MsdfFont.Builder builder() {
      return new MsdfFont.Builder();
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public FontData.AtlasData getAtlas() {
      return this.atlas;
   }

   @Generated
   public FontData.MetricsData getMetrics() {
      return this.metrics;
   }

   @Generated
   public String getHintedFace() {
      return this.hintedFace;
   }

   @Generated
   public float getAscent() {
      return this.ascent;
   }

   @Generated
   public float getBaseline() {
      return this.baseline;
   }

   public static class Builder {
      private String name = "?";
      private Identifier dataIdentifer;
      private Identifier atlasIdentifier;
      private String hintedFace;
      private boolean capCentered;

      private Builder() {
      }

      public MsdfFont.Builder hinted(String var1) {
         this.hintedFace = var1;
         return this;
      }

      public MsdfFont.Builder capCentered() {
         this.capCentered = true;
         return this;
      }

      public MsdfFont.Builder name(String var1) {
         this.name = var1;
         return this;
      }

      public MsdfFont.Builder data(String var1) {
         this.dataIdentifer = Zenith.method1501("fonts/msdf/" + var1 + ".json");
         return this;
      }

      public MsdfFont.Builder data(Identifier var1) {
         this.dataIdentifer = var1;
         return this;
      }

      public MsdfFont.Builder atlas(String var1) {
         this.atlasIdentifier = Zenith.method1501("fonts/msdf/" + var1 + ".png");
         return this;
      }

      public MsdfFont.Builder atlas(Identifier var1) {
         this.atlasIdentifier = var1;
         return this;
      }

      public MsdfFont build() {
         FontData var1 = ResourceProvider.fromJsonToInstance(this.dataIdentifer, FontData.class);
         AbstractTexture var2 = MinecraftAccess.mc.getTextureManager().getTexture(this.atlasIdentifier);
         if (var1 == null) {
            throw new RuntimeException(
               "Failed to read font data file: "
                  + this.dataIdentifer.toString()
                  + "; Are you sure this is json file? Try to check the correctness of its syntax."
            );
         } else {
            float var3 = var1.atlas().width();
            float var4 = var1.atlas().height();
            int var5 = Integer.MAX_VALUE;
            int var6 = Integer.MIN_VALUE;

            for (FontData.GlyphData var8 : var1.glyphs()) {
               int var9 = var8.unicode();
               if (var9 < var5) {
                  var5 = var9;
               }

               if (var9 > var6) {
                  var6 = var9;
               }
            }

            MsdfGlyph[] var15;
            int var16;
            if (var5 > var6) {
               var15 = new MsdfGlyph[0];
               var16 = 0;
            } else {
               var16 = var5;
               var15 = new MsdfGlyph[var6 - var5 + 1];

               for (FontData.GlyphData var10 : var1.glyphs()) {
                  var15[var10.unicode() - var16] = new MsdfGlyph(var10, var3, var4);
               }
            }

            Int2ObjectOpenHashMap var18 = new Int2ObjectOpenHashMap();
            var1.kernings().forEach(var1x -> {
               Int2FloatMap var2x = (Int2FloatMap)var18.computeIfAbsent(var1x.leftChar(), var0x -> new Int2FloatOpenHashMap());
               var2x.put(var1x.rightChar(), var1x.advance());
            });
            float var19 = var1.metrics().ascender() > 0.0F ? var1.metrics().ascender() : 0.7F;
            float var11 = var19;
            if (this.capCentered) {
               for (FontData.GlyphData var13 : var1.glyphs()) {
                  if (var13.unicode() == 72 && var13.planeBounds() != null) {
                     float var14 = var13.planeBounds().top() + var13.planeBounds().bottom();
                     if (var14 > 0.0F && var14 < var19) {
                        var11 = (var19 + var14) / 2.0F;
                     }
                     break;
                  }
               }
            }

            return new MsdfFont(this.name, var2, var1.atlas(), var1.metrics(), var15, var16, var18, this.hintedFace, var19, var11);
         }
      }
   }
}
