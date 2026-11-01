package org.zenith.utility.render;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.Arrays;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.utility.misc.ColorUtils;

public final class TargetEspTextures {
   public static final int CONST_intValue = 0;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   public static final int CONST_intValue4 = 3;
   public static final int CONST_intValue5 = 4;
   private static final Identifier identifier = method5053("soft_64");
   private static final Identifier identifier2 = method5053("heart_atlas_128");
   private static final Identifier identifier3 = method5053("heart_atlas_48");
   private static final Identifier identifier4 = method5053("star_128");
   private static final Identifier identifier5 = method5053("star_48");
   private static final Identifier identifier6 = method5053("dot_64");
   private static final Identifier identifier7 = method5053("dot_24");
   private static final float CONST_floatValue = (float) (Math.PI * 2);
   private static final float CONST_floatValue2 = (float) Math.PI;
   private static final int CONST_intValue6 = -1;
   private static final int CONST_intValue7 = 12;
   private static final int CONST_intValue8 = 4;
   private static final int CONST_intValue9 = 6;
   private static final int CONST_intValue10 = 40;
   private static final int CONST_intValue11 = 72;
   private static final int CONST_intValue12 = 48;
   private static final int CONST_intValue13 = 64;
   private static final int CONST_intValue14 = 24;
   private static final int CONST_intValue15 = 3;
   private final TargetEspTextures.Entry entry = new TargetEspTextures.Entry();
   private final TargetEspTextures.Entry entry2 = new TargetEspTextures.Entry();
   private final TargetEspTextures.Entry entry3 = new TargetEspTextures.Entry();
   private final TargetEspTextures.Entry entry4 = new TargetEspTextures.Entry();
   private final Matrix4f matrix4f = new Matrix4f();
   private final Vector3f vector3f = new Vector3f();
   private Integer[] integers = new Integer[64];
   private float[] floatValues = new float[64];
   private float floatValue;

   private static Identifier method5053(String var0) {
      Identifier var1 = Zenith.method1501("visuals/targetesp/" + var0 + ".png");
      TextureFilterUtils.smooth(var1);
      return var1;
   }

   public void method5054(
      int var1,
      Matrix4f var2,
      double var3,
      double var5,
      double var7,
      float var9,
      float var10,
      float var11,
      int[] var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17,
      boolean var18,
      float var19
   ) {
      if (!(var13 <= 0.01F)) {
         this.entry.count = 0;
         this.entry2.count = 0;
         this.entry3.count = 0;
         this.entry4.count = 0;
         this.floatValue = var16;
         float var20 = easeOut(var13);
         float var21 = var15 * (0.7F + 0.3F * var20);
         float var22 = (var9 + 0.1F) * var21;
         switch (var1) {
            case 0:
               this.method5055(var3, var5, var7, var22, var10, var11, var12, var13, var14, var21);
               break;
            case 1:
               this.method5058(var3, var5, var7, var22, var10, var11, var12, var13, var14, var21);
               break;
            case 2:
               this.method5059(var3, var5, var7, var22, var10, var11, var12, var13, var14, var21);
               break;
            case 3:
               this.method5060(var3, var5, var7, var22, var10, var11, var12, var13, var14, var21);
               break;
            default:
               this.method5061(var3, var5, var7, var22, var10, var11, var12, var13, var14, var21);
         }

         boolean var23 = var19 * 0.3F * var15 > 40.0F;
         this.method5067(var2, var16, var17, var18, var23);
      }
   }

   private void method5055(double var1, double var3, double var5, float var7, float var8, float var9, int[] var10, float var11, float var12, float var13) {
      float var14 = var7 * 1.05F;
      float var15 = 1.0F + 0.3F * var12;

      for (int var16 = 0; var16 < 3; var16++) {
         int var17 = var10[var16 % var10.length];
         float var18 = method5056(var9, var16);

         for (int var19 = 12; var19 >= 1; var19--) {
            float var20 = var18 - var19 * 0.05F;
            float var21 = 1.0F - var19 / 13.0F;
            this.entry
               .method5077(
                  var1 + MathHelper.cos(var20) * var14,
                  method5057(var3, var8, var9 - var19 * 0.04F, var16),
                  var5 + MathHelper.sin(var20) * var14,
                  (0.05F + 0.12F * var21) * var13,
                  0.0F,
                  0.0F,
                  method5075(var17, var11 * var21 * var21 * 0.3F)
               );
         }

         for (int var35 = 0; var35 < 4; var35++) {
            float var37 = method5072(var9 * 0.55F + var35 / 4.0F + var16 * 0.13F);
            float var39 = var9 - var37 / 0.55F;
            float var22 = method5056(var39, var16);
            float var23 = 1.0F + var37 * 0.25F;
            double var24 = var1 + MathHelper.cos(var22) * var14 * var23;
            double var26 = method5057(var3, var8, var39, var16) + var37 * 0.5F * var13;
            double var28 = var5 + MathHelper.sin(var22) * var14 * var23;
            float var30 = var37 < 0.15F ? method5074(var37 / 0.15F) : 1.0F - 0.35F * (var37 - 0.15F) / 0.85F;
            float var31 = 1.0F - var37;
            this.method5065(
               var24,
               var26,
               var28,
               0.11F * var13 * var30,
               MathHelper.sin(var37 * 7.0F + var35 * 2.0F) * 0.3F,
               this.method5066(var22),
               var17,
               var11 * var31 * var31 * 0.95F
            );
         }

         double var36 = var1 + MathHelper.cos(var18) * var14;
         double var40 = method5057(var3, var8, var9, var16);
         double var42 = var5 + MathHelper.sin(var18) * var14;
         float var25 = 0.3F * var13 * method5070(var9 / 1.1F + var16 * 0.31F) * var15;
         this.entry.method5077(var36, var40, var42, var25 * 1.6F, 0.0F, 0.0F, method5075(var17, var11 * (0.2F + 0.25F * var12)));
         this.method5065(var36, var40, var42, var25, MathHelper.sin(var9 * 1.3F + var16 * 2.0F) * 0.12F, this.method5066(var18), var17, var11);
      }

      if (var12 > 0.01F) {
         float var32 = easeOut(1.0F - var12);
         float var33 = var7 * (0.35F + var32 * 1.2F);
         double var34 = var3 + var8 * 0.62F + var32 * 0.35F * var13;

         for (int var38 = 0; var38 < 6; var38++) {
            float var41 = var38 / 6.0F * (float) (Math.PI * 2) + 0.4F;
            this.method5065(
               var1 + MathHelper.cos(var41) * var33,
               var34,
               var5 + MathHelper.sin(var41) * var33,
               0.14F * var13 * (1.0F - 0.35F * var32),
               (var38 - 2.5F) * 0.1F,
               this.method5066(var41),
               var10[1],
               var12 * var11
            );
         }
      }
   }

   private static float method5056(float var0, int var1) {
      return var0 * 1.25F + var1 * (float) (Math.PI * 2) / 3.0F;
   }

   private static double method5057(double var0, float var2, float var3, int var4) {
      return var0 + var2 * 0.56F + MathHelper.sin(var3 * 1.4F + var4 * 2.1F) * var2 * 0.1F;
   }

   private void method5058(double var1, double var3, double var5, float var7, float var8, float var9, int[] var10, float var11, float var12, float var13) {
      double var14 = var3 + var8 * 0.55F;
      float var16 = var7 * 1.05F * (1.0F + 0.12F * var12);
      float var17 = 1.05F;
      float var18 = MathHelper.cos(var17);
      float var19 = MathHelper.sin(var17);

      for (int var20 = 0; var20 < 3; var20++) {
         float var21 = var20 * (float) (Math.PI * 2) / 3.0F + var9 * 0.25F;
         float var22 = MathHelper.cos(var21);
         float var23 = MathHelper.sin(var21);
         double var24 = -var23 * var18;
         double var26 = var19;
         double var28 = var22 * var18;
         int var30 = var10[var20 % var10.length];

         for (int var31 = 0; var31 < 72; var31++) {
            float var32 = var31 / 72.0F * (float) (Math.PI * 2);
            float var33 = MathHelper.cos(var32) * var16;
            float var34 = MathHelper.sin(var32) * var16;
            this.entry
               .method5077(
                  var1 + var22 * var33 + var24 * var34,
                  var14 + var26 * var34,
                  var5 + var23 * var33 + var28 * var34,
                  0.05F * var13,
                  0.0F,
                  0.0F,
                  method5075(var30, var11 * 0.07F)
               );
         }

         float var43 = var9 * 2.2F + var20 * 2.1F;

         for (int var44 = 40; var44 >= 0; var44--) {
            float var45 = var43 - var44 * 0.04F;
            float var46 = MathHelper.cos(var45) * var16;
            float var35 = MathHelper.sin(var45) * var16;
            double var36 = var1 + var22 * var46 + var24 * var35;
            double var38 = var14 + var26 * var35;
            double var40 = var5 + var23 * var46 + var28 * var35;
            if (var44 == 0) {
               this.entry.method5077(var36, var38, var40, 0.4F * var13, 0.0F, 0.0F, method5075(var30, var11 * 0.3F * (1.0F + 0.8F * var12)));
               this.entry3.method5077(var36, var38, var40, 0.1F * var13 * (1.0F + 0.4F * var12), 0.0F, 0.0F, method5075(method5076(var30, -1, 0.35F), var11));
            } else {
               float var42 = 1.0F - var44 / 41.0F;
               this.entry
                  .method5077(var36, var38, var40, MathHelper.lerp(var42, 0.03F, 0.2F) * var13, 0.0F, 0.0F, method5075(var30, var11 * var42 * var42 * 0.6F));
            }
         }
      }
   }

   private void method5059(double var1, double var3, double var5, float var7, float var8, float var9, int[] var10, float var11, float var12, float var13) {
      float var14 = var7 * 0.9F;
      float var15 = var11 * 1.2F;

      for (int var16 = 0; var16 <= 48; var16++) {
         float var17 = var16 / 48.0F;
         float var18 = MathHelper.clamp((var15 - var17) * 8.0F, 0.0F, 1.0F);
         if (var18 <= 0.0F) {
            break;
         }

         float var19 = Math.min(1.0F, Math.min(var17, 1.0F - var17) * 6.0F + 0.2F) * var18;
         float var20 = 0.5F + 0.5F * MathHelper.sin(var17 * (float) (Math.PI * 2) * 1.5F - var9 * 2.6F);
         float var21 = method5073(MathHelper.clamp((var20 - 0.7F) / 0.3F, 0.0F, 1.0F));
         float var22 = var17 * (float) (Math.PI * 2) * 1.5F + var9 * 1.4F;
         float var23 = var14 * (1.0F + 0.12F * var12 * MathHelper.sin(var17 * (float) Math.PI));
         double var24 = var3 + var17 * var8 * 1.05F - 0.02F;
         double var26 = 0.0;
         double var28 = 0.0;
         double var30 = 0.0;
         double var32 = 0.0;

         for (int var34 = 0; var34 < 2; var34++) {
            float var35 = var22 + var34 * (float) Math.PI;
            double var36 = var1 + MathHelper.cos(var35) * var23;
            double var38 = var5 + MathHelper.sin(var35) * var23;
            int var40 = var10[var34 * 2 % var10.length];
            this.entry.method5077(var36, var24, var38, (0.07F + 0.05F * var20) * var13, 0.0F, 0.0F, method5075(var40, var11 * var19 * (0.22F + 0.4F * var20)));
            if (var21 > 0.01F) {
               this.entry3.method5077(var36, var24, var38, 0.065F * var13 * var21, 0.0F, 0.0F, method5075(method5076(var40, -1, 0.3F), var11 * var19 * var21));
            }

            if (var34 == 0) {
               var26 = var36;
               var28 = var38;
            } else {
               var30 = var36;
               var32 = var38;
            }
         }

         if (var16 % 8 == 4) {
            for (int var41 = 1; var41 <= 5; var41++) {
               float var42 = var41 / 6.0F;
               this.entry
                  .method5077(
                     var26 + (var30 - var26) * var42,
                     var24,
                     var28 + (var32 - var28) * var42,
                     0.04F * var13,
                     0.0F,
                     0.0F,
                     method5075(method5076(var10[0], var10[2 % var10.length], var42), var11 * var19 * 0.18F)
                  );
            }
         }
      }
   }

   private void method5060(double var1, double var3, double var5, float var7, float var8, float var9, int[] var10, float var11, float var12, float var13) {
      double var14 = var3 + var8 * 0.58F + MathHelper.sin(var9 * 1.2F) * 0.04F * var13;
      float var16 = var9 * 0.3F;
      float var17 = MathHelper.cos(var16);
      float var18 = MathHelper.sin(var16);

      for (int var19 = 0; var19 < 2; var19++) {
         float var20 = var7 * (1.15F + var19 * 0.18F) * (1.0F + 0.18F * var12);
         float var21 = var19 == 0 ? 0.38F : -0.38F;
         float var22 = MathHelper.cos(var21);
         float var23 = MathHelper.sin(var21);
         float var24 = var9 * (var19 == 0 ? 1.1F : -0.9F);
         int var25 = var10[var19 * 2 % var10.length];

         for (int var26 = 0; var26 <= 64; var26++) {
            boolean var27 = var26 == 64;
            float var28 = var27 ? var24 : var26 / 64.0F * (float) (Math.PI * 2);
            float var29 = MathHelper.cos(var28) * var20;
            float var30 = MathHelper.sin(var28) * var20;
            float var31;
            float var32;
            float var33;
            if (var19 == 0) {
               var31 = var29;
               var32 = -var30 * var23;
               var33 = var30 * var22;
            } else {
               var31 = var29 * var22;
               var32 = var29 * var23;
               var33 = var30;
            }

            double var34 = var1 + var31 * var17 - var33 * var18;
            double var36 = var5 + var31 * var18 + var33 * var17;
            if (var27) {
               this.entry.method5077(var34, var14 + var32, var36, 0.34F * var13, 0.0F, 0.0F, method5075(var25, var11 * 0.3F * (1.0F + 0.6F * var12)));
               this.entry3.method5077(var34, var14 + var32, var36, 0.08F * var13, 0.0F, 0.0F, method5075(method5076(var25, -1, 0.35F), var11));
            } else {
               float var38 = method5071(var28, var24);
               this.entry
                  .method5077(
                     var34,
                     var14 + var32,
                     var36,
                     (0.05F + 0.1F * var38) * var13,
                     0.0F,
                     0.0F,
                     method5075(method5076(var25, -1, var38 * 0.25F), var11 * (0.16F + 0.6F * var38))
                  );
            }
         }
      }
   }

   private void method5061(double var1, double var3, double var5, float var7, float var8, float var9, int[] var10, float var11, float var12, float var13) {
      float var14 = var7 * 1.1F;

      for (int var15 = 0; var15 < 4; var15++) {
         int var16 = var10[var15 % var10.length];

         for (int var17 = 24; var17 >= 1; var17--) {
            float var18 = var17 * 0.045F;
            float var19 = 1.0F - var17 / 25.0F;
            this.entry
               .method5077(
                  method5062(var1, var14, var9 - var18, var15),
                  method5063(var3, var8, var9 - var18, var15) - var18 * var18 * 0.9F,
                  method5064(var5, var14, var9 - var18, var15),
                  (0.03F + 0.1F * var19) * var13,
                  0.0F,
                  0.0F,
                  method5075(var16, var11 * var19 * var19 * 0.5F)
               );
         }

         for (int var24 = 1; var24 <= 3; var24++) {
            float var26 = var24 * 0.25F;
            float var27 = 1.0F - var26;
            float var20 = 0.75F + 0.25F * MathHelper.sin(var9 * 3.0F + var15 + var26 * 5.0F);
            this.entry2
               .method5077(
                  method5062(var1, var14, var9 - var26, var15),
                  method5063(var3, var8, var9 - var26, var15) - var26 * var26 * 0.9F,
                  method5064(var5, var14, var9 - var26, var15),
                  0.1F * var13 * var27,
                  var26 * 3.0F + var15,
                  0.0F,
                  method5075(var16, var11 * var27 * 0.7F * var20)
               );
         }

         double var25 = method5062(var1, var14, var9, var15);
         double var28 = method5063(var3, var8, var9, var15);
         double var21 = method5064(var5, var14, var9, var15);
         float var23 = 0.34F * var13 * (0.9F + 0.1F * MathHelper.sin(var9 * 3.0F + var15 * 2.0F)) * (1.0F + 0.4F * var12);
         this.entry.method5077(var25, var28, var21, var23 * 1.5F, 0.0F, 0.0F, method5075(var16, var11 * 0.3F));
         this.entry2.method5077(var25, var28, var21, var23, var9 * 0.6F + var15, 0.0F, method5075(method5076(var16, -1, 0.2F + 0.4F * var12), var11));
         this.entry3.method5077(var25, var28, var21, var23 * 0.2F, 0.0F, 0.0F, method5075(method5076(var16, -1, 0.5F), var11));
      }
   }

   private static double method5062(double var0, float var2, float var3, int var4) {
      return var0 + MathHelper.cos(var3 * 1.1F + var4 * (float) (Math.PI * 2) / 4.0F) * var2;
   }

   private static double method5063(double var0, float var2, float var3, int var4) {
      return var0 + var2 * 0.55F + MathHelper.sin(var3 * 1.5F + var4 * 1.7F) * var2 * 0.16F;
   }

   private static double method5064(double var0, float var2, float var3, int var4) {
      return var0 + MathHelper.sin(var3 * 1.1F + var4 * (float) (Math.PI * 2) / 4.0F) * var2;
   }

   private void method5065(double var1, double var3, double var5, float var7, float var8, float var9, int var10, float var11) {
      if (!(var11 < 0.01F) && !(var7 < 0.002F)) {
         this.entry4.method5078(var1, var3, var5, var7, var8, var9, method5075(var10, var11), 0.0F, 0.5F);
         this.entry4.method5078(var1, var3, var5, var7, var8, var9, method5075(-1, var11 * 0.9F), 0.5F, 1.0F);
      }
   }

   private float method5066(float var1) {
      float var2 = (float)Math.atan2(MathHelper.cos(var1), MathHelper.sin(var1)) + this.floatValue;
      return 0.35F * MathHelper.sin(2.0F * var2);
   }

   private void method5067(Matrix4f var1, float var2, float var3, boolean var4, boolean var5) {
      RenderState.method1871();
      if (var4) {
         RenderState.method1878();
      } else {
         RenderState.method1879();
      }

      RenderState.method1882(false);
      RenderState.method1877();
      RenderState.method1898("core/position_tex_color");

      try {
         RenderState.method1875(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
         this.method5068(this.entry, identifier, var1, var2, var3, false);
         this.method5068(this.entry2, var5 ? identifier4 : identifier5, var1, var2, var3, false);
         RenderState.method1875(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
         this.method5068(this.entry3, var5 ? identifier6 : identifier7, var1, var2, var3, false);
         this.method5068(this.entry4, var5 ? identifier2 : identifier3, var1, var2, var3, true);
      } finally {
         RenderState.method1882(true);
         RenderState.method1878();
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
      }
   }

   private void method5068(TargetEspTextures.Entry var1, Identifier var2, Matrix4f var3, float var4, float var5, boolean var6) {
      int var7 = var1.count;
      if (var7 != 0) {
         if (var6) {
            this.method5069(var1, var3);
         }

         RenderState.method1890(0, var2);
         BufferBuilder var8 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);

         for (int var9 = 0; var9 < var7; var9++) {
            int var10 = var6 ? this.integers[var9] : var9;
            int var11 = var10 * 8;
            float[] var12 = var1.floatValues;
            float var13 = var12[var11 + 3] * 0.5F;
            float var14 = var12[var11 + 6];
            float var15 = var12[var11 + 7];
            int var16 = var1.intValues[var10];
            this.matrix4f
               .set(var3)
               .translate(var12[var11], var12[var11 + 1], var12[var11 + 2])
               .rotateY(-var4 + var12[var11 + 5])
               .rotateX(var5)
               .rotateZ(var12[var11 + 4]);
            var8.vertex(this.matrix4f, -var13, var13, 0.0F).texture(var14, 0.0F).color(var16);
            var8.vertex(this.matrix4f, -var13, -var13, 0.0F).texture(var14, 1.0F).color(var16);
            var8.vertex(this.matrix4f, var13, -var13, 0.0F).texture(var15, 1.0F).color(var16);
            var8.vertex(this.matrix4f, var13, var13, 0.0F).texture(var15, 0.0F).color(var16);
         }

         BuiltBuffer var17 = var8.endNullable();
         if (var17 != null) {
            ImmediateRenderer.method8015(var17);
         }
      }
   }

   private void method5069(TargetEspTextures.Entry var1, Matrix4f var2) {
      int var3 = var1.count;
      if (this.integers.length < var3) {
         this.integers = new Integer[var3 * 2];
         this.floatValues = new float[var3 * 2];
      }

      for (int var4 = 0; var4 < var3; var4++) {
         int var5 = var4 * 8;
         var2.transformPosition(var1.floatValues[var5], var1.floatValues[var5 + 1], var1.floatValues[var5 + 2], this.vector3f);
         this.floatValues[var4] = this.vector3f.z + (var4 & 1) * 1.0E-4F;
         this.integers[var4] = var4;
      }

      float[] var6 = this.floatValues;
      Arrays.sort(this.integers, 0, var3, (var1x, var2x) -> Float.compare(var6[var1x], var6[var2x]));
   }

   private static float method5070(float var0) {
      float var1 = var0 - (float)Math.floor(var0);
      float var2 = (var1 - 0.1F) / 0.09F;
      float var3 = (var1 - 0.33F) / 0.1F;
      return 1.0F + 0.09F * (float)Math.exp(-var2 * var2) + 0.05F * (float)Math.exp(-var3 * var3);
   }

   private static float method5071(float var0, float var1) {
      float var2 = var0 - var1;
      var2 -= (float) (Math.PI * 2) * (float)Math.floor(var2 / (float) (Math.PI * 2) + 0.5F);
      return (float)Math.exp(-var2 * var2 * 2.0F);
   }

   private static float method5072(float var0) {
      return var0 - (float)Math.floor(var0);
   }

   private static float method5073(float var0) {
      return var0 * var0 * (3.0F - 2.0F * var0);
   }

   private static float easeOut(float var0) {
      float var1 = 1.0F - var0;
      return 1.0F - var1 * var1 * var1;
   }

   private static float method5074(float var0) {
      if (var0 <= 0.0F) {
         return 0.0F;
      } else {
         float var1 = var0 - 1.0F;
         return 1.0F + 2.70158F * var1 * var1 * var1 + 1.70158F * var1 * var1;
      }
   }

   private static int method5075(int var0, float var1) {
      return ColorUtils.method6439(var0, MathHelper.clamp(var1, 0.0F, 1.0F));
   }

   private static int method5076(int var0, int var1, float var2) {
      int var3 = MathHelper.lerp(var2, var0 >> 16 & 0xFF, var1 >> 16 & 0xFF);
      int var4 = MathHelper.lerp(var2, var0 >> 8 & 0xFF, var1 >> 8 & 0xFF);
      int var5 = MathHelper.lerp(var2, var0 & 0xFF, var1 & 0xFF);
      return var0 & 0xFF000000 | var3 << 16 | var4 << 8 | var5;
   }

   private static final class Entry {
      public static final int CONST_intValue = 8;
      public float[] floatValues = new float[4096];
      public int[] intValues = new int[512];
      public int count;

      public void method5077(double var1, double var3, double var5, float var7, float var8, float var9, int var10) {
         this.method5078(var1, var3, var5, var7, var8, var9, var10, 0.0F, 1.0F);
      }

      public void method5078(double var1, double var3, double var5, float var7, float var8, float var9, int var10, float var11, float var12) {
         if (var10 >>> 24 != 0 && !(var7 <= 0.001F)) {
            if (this.count == this.intValues.length) {
               this.floatValues = Arrays.copyOf(this.floatValues, this.floatValues.length * 2);
               this.intValues = Arrays.copyOf(this.intValues, this.intValues.length * 2);
            }

            int var13 = this.count * 8;
            this.floatValues[var13] = (float)var1;
            this.floatValues[var13 + 1] = (float)var3;
            this.floatValues[var13 + 2] = (float)var5;
            this.floatValues[var13 + 3] = var7;
            this.floatValues[var13 + 4] = var8;
            this.floatValues[var13 + 5] = var9;
            this.floatValues[var13 + 6] = var11;
            this.floatValues[var13 + 7] = var12;
            this.intValues[this.count++] = var10;
         }
      }
   }
}
