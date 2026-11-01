package org.zenith.utility.render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import org.zenith.MinecraftAccess;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.AnimationCache;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.Class2;
import org.zenith.utility.misc.Handler4;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.WardrobeCategory;
import org.zenith.utility.misc.WardrobeSetInfo;

public final class WardrobeFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> implements MinecraftAccess {
   private static final int CONST_intValue = -1;
   private static final Map<String, String> items = new HashMap<>();
   private static final Map<String, Object> items2 = new HashMap<>();
   private static final Object obj = new Object();

   public WardrobeFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> var1) {
      super(var1);
   }

   public void render(MatrixStack var1, OrderedRenderCommandQueue var2, int var3, PlayerEntityRenderState var4, float var5, float var6) {
      Wardrobe var7 = Wardrobe.method2488();
      if (var7 != null && !var4.invisible) {
         List<HoveredData> var8 = method4444(var4);
         if (!var8.isEmpty()) {
            PlayerEntityModel var9 = this.getContextModel();
            int var10 = LivingEntityRenderer.getOverlay(var4, 0.0F);
            HoveredData var11 = var7.method2499(var8, FocusSlotEnum.CHARACTER);
            WardrobeSetInfo var12 = var11 == null ? var7.method2500(var8) : null;
            Util3.Entry var13 = null;
            long var14 = System.nanoTime();

            for (Object var17_o : (Iterable<?>)(Object)(var8)) {
               HoveredData var17 = (HoveredData)var17_o;
               if (var17.method1546() != WardrobeCategory.PET
                  && !var17.getFocusSlotEnum().method543()
                  && (var11 == null || var17 == var11 || var17.method1546() == WardrobeCategory.ACCESSORY)) {
                  Class15 var18 = var7.getRegistry().method1427(var17);
                  if (var18 != null) {
                     AnimationCache.Entry var19 = AnimationCache.method7621(var4.id, var17, var7.isFlag());
                     float var20 = var19.method7624(var14);
                     float[] var21 = method4441(var18, var19, var4, var9, var20);
                     if (var17 == var11 && !var19.animationState.isFlag()) {
                        var19.class16.method2140(var18);
                     }

                     boolean var22 = var4.squaredDistanceToCamera < var18.floatValue * var18.floatValue;
                     boolean var23 = var17.getRarity().isFlag();
                     float[] var24 = null;

                     for (int var25 = 0; var25 < 6; var25++) {
                        if (var18.method2033(var25)) {
                           if (var24 == null && var21 != null) {
                              var24 = (float[])var21.clone();
                           }

                           ModelPart var26 = Util19.method5047(var9, var25);
                           var1.push();

                           try {
                              var9.getRootPart().applyTransform(var1);
                              var26.applyTransform(var1);
                              if (var17 != var11) {
                                 if (var13 == null) {
                                    var13 = Util3.method826(var4, var7, var8, var11);
                                 }

                                 var13.method846(var1, var17, var25);
                              }

                              BoneMatrices.method4788(var18, var24, var25, var1, var2, var3, var10, var22, var23, -1);
                           } finally {
                              var1.pop();
                           }
                        }
                     }

                     Util19.method5044(var18, var19, var4, var9, var21, var20, var19.class2.flag3, var19.class2.flag2, var12, var18.items3);
                  }
               }
            }

            if (var12 != null) {
               AnimationCache.Entry var30 = AnimationCache.method7622(var4.id, method4439(var12), var7.isFlag());
               Util19.method5045(var12, var4, var30, var30.method7624(var14), method4440(var12));
            }
         }
      }
   }

   private static String method4439(WardrobeSetInfo var0) {
      return items.computeIfAbsent(var0.getText(), var0x -> "set:" + var0x);
   }

   private static ParticleEffect method4440(WardrobeSetInfo var0) {
      String var1 = var0.getText2();
      if (var1 == null) {
         return null;
      } else {
         Object var2 = items2.computeIfAbsent(var1, var0x -> {
            ParticleEffect var1x = ModelJsonParser.method120(var0x);
            return var1x == null ? obj : var1x;
         });
         return var2 == obj ? null : (ParticleEffect)var2;
      }
   }

   private static float[] method4441(Class15 var0, AnimationCache.Entry var1, PlayerEntityRenderState var2, PlayerEntityModel var3, float var4) {
      Class2 var5 = var1.class2;
      var5.floatValue = var2.field_53536;
      var5.floatValue2 = var2.field_53537;
      var5.floatValue3 = var2.field_53538;
      var5.floatValue4 = var3.leftLeg.pitch;
      var5.floatValue5 = var3.rightLeg.pitch;
      var5.floatValue6 = var2.limbSwingAmplitude;
      var5.floatValue8 = var4;
      var5.flag = var2.isInSneakingPose;
      var5.flag3 = var2.limbSwingAmplitude > 0.15F;
      var5.flag2 = method4442(var2);
      float var6 = var2.bodyYaw;
      if (!Float.isNaN(var1.floatValue2) && !(var4 <= 0.0F)) {
         float var7 = var6 - var1.floatValue2;

         while (var7 > 180.0F) {
            var7 -= 360.0F;
         }

         while (var7 < -180.0F) {
            var7 += 360.0F;
         }

         var5.floatValue7 = var7 / var4;
      } else {
         var5.floatValue7 = 0.0F;
      }

      var1.floatValue2 = var6;
      float var9 = var2.age / 20.0F;
      String var8 = var2.isGliding
         ? "glide"
         : (var5.flag2 ? "fly" : (var2.isInSneakingPose ? "sneak" : (var2.limbSwingAmplitude > 0.6F ? "run" : (var5.flag3 ? "walk" : "idle"))));
      var1.animationState.method6539(var0, var8, var9);
      var1.animationState.setFloatValue6(!var8.equals("walk") && !var8.equals("run") ? 1.0F : 0.6F + Math.min(1.2F, var2.limbSwingAmplitude));
      return var1.class16.method2139(var0, var1.animationState, var9, var5);
   }

   private static boolean method4442(PlayerEntityRenderState var0) {
      if (var0.isGliding) {
         return true;
      } else if (mc.world == null) {
         return false;
      } else {
         Entity var1 = mc.world.getEntityById(var0.id);
         return var1 != null && !var1.isOnGround() && !var1.hasVehicle() && !var1.isTouchingWater();
      }
   }

   public static boolean method4443(PlayerEntityRenderState var0) {
      return mc.player != null && var0.id == mc.player.getId();
   }

   public static List<HoveredData> method4444(PlayerEntityRenderState var0) {
      List var1 = ((Handler4)var0).zenith$wardrobeLoadout();
      if (var1 != null) {
         return var1;
      } else {
         Wardrobe var2 = Wardrobe.method2488();
         if (var2 == null) {
            return List.of();
         } else {
            boolean var3 = method4443(var0);
            return (var3 ? var2.showSelf.isEnabled() : var2.showOthers.isEnabled()) ? List.copyOf(var2.method2498(Handler4.method3142(var0), var3)) : List.of();
         }
      }
   }

   public static HoveredData method4445(PlayerEntityRenderState var0) {
      Wardrobe var1 = Wardrobe.method2488();
      return var1 == null ? null : var1.method2499(method4444(var0), FocusSlotEnum.CHARACTER);
   }

   public static void method4446(PlayerEntityRenderState var0) {
      ((Handler4)var0).zenith$setWardrobeLoadout(null);
      Wardrobe var1 = Wardrobe.method2488();
      if (var1 != null) {
         List<HoveredData> var2 = method4444(var0);
         ((Handler4)var0).zenith$setWardrobeLoadout(var2);
         if (!var2.isEmpty()) {
            if (var1.method2499(var2, FocusSlotEnum.CHARACTER) != null) {
               var0.capeVisible = false;
            } else {
               for (Object var4_o : (Iterable<?>)(Object)(var2)) {
                  HoveredData var4 = (HoveredData)var4_o;
                  if (var4.method1546() == WardrobeCategory.CLOTHING
                     || var4.method1546() == WardrobeCategory.CAPE
                     || var4.method1546() == WardrobeCategory.ACCESSORY) {
                     Class15 var5 = var1.getRegistry().method1427(var4);
                     if (var5 != null) {
                        int var6 = var5.intValue4;
                        if ((var6 & 1) != 0) {
                           var0.hatVisible = false;
                        }

                        if ((var6 & 2) != 0) {
                           var0.jacketVisible = false;
                        }

                        if ((var6 & 4) != 0) {
                           var0.leftSleeveVisible = false;
                           var0.rightSleeveVisible = false;
                        }

                        if ((var6 & 8) != 0) {
                           var0.leftPantsLegVisible = false;
                           var0.rightPantsLegVisible = false;
                        }

                        if ((var6 & 16) != 0 || var4.getFocusSlotEnum() == FocusSlotEnum.BACK || var4.getFocusSlotEnum() == FocusSlotEnum.CAPE) {
                           var0.capeVisible = false;
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
