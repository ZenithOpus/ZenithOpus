package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.Generated;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.misc.FreeCamModule;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.client.modules.impl.misc.StreamerModeModule;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.PetManager;
import org.zenith.utility.game.ArmorSlots;
import org.zenith.utility.game.Util14;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.TextUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;
import org.zenith.utility.network.Names;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.Jebriksheet;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.ShapeRenderer;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;
import org.zenith.utility.render.display.base.QueuedCustomDrawContext;

@ModuleInfo(
   name = "EntityESP",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.entityesp.description"
)
public final class EntityESPModule extends Module {
   public static final EntityESPModule INSTANCE = new EntityESPModule();
   private static final long CONST_longValue = 10000L;
   private final MultiSelectSetting targets = MultiSelectSetting.method5224(
      "module.entityESP.targets",
      "module.entityESP.targets.desc",
      Arrays.asList(
         "module.entityESP.players", "module.entityESP.noArmor", "module.entityESP.friends", "module.entityESP.droppedItems", "module.entityESP.mobs", "TNT"
      )
   );
   private final SliderSetting removeHiddenPlayerDelay = new SliderSetting(
      "module.entityESP.removeHiddenPlayerDelay",
      1.0F,
      0.0F,
      40.0F,
      1.0F,
      "module.entityESP.removeHiddenPlayerDelay.desc",
      "t",
      () -> this.targets.method5227(0),
      null
   );
   private final MultiSelectSetting elements = method554(
      MultiSelectSetting.method5224(
            "module.entityESP.elements",
            "module.entityESP.elements.desc",
            Arrays.asList(
               "module.entityESP.names",
               "module.entityESP.armor",
               "module.entityESP.armorText",
               "module.entityESP.ping",
               "module.entityESP.triangles",
               "module.entityESP.boxes",
               "module.entityESP.hands",
               "Tracers"
            )
         )
         .method5220("module.entityESP.triangles", "module.entityESP.boxes"),
      "module.entityESP.armorText"
   );
   private final BooleanSetting gradient = new BooleanSetting(
      "module.entityESP.gradient", "module.entityESP.gradientDesc", true, () -> this.elements.method5226("module.entityESP.boxes")
   );
   private final SliderSetting scaleSetting = new SliderSetting(
      "module.entityESP.scaleSetting", 0.7F, 0.5F, 1.0F, 0.1F, "module.entityESP.scaleSetting.desc", "x"
   );
   private final SliderSetting tagMinScale = new SliderSetting(
      "module.entityESP.tagMinScale", 0.5F, 0.2F, 1.0F, 0.05F, "module.entityESP.tagMinScale.desc", "x"
   );
   private final BooleanSetting blur = new BooleanSetting("module.entityESP.blur", "module.entityESP.blurDesc", false, InterfaceModule.INSTANCE::method6817);
   private final BooleanSetting glow = new BooleanSetting("module.entityESP.glow", "module.entityESP.glowDesc", false, InterfaceModule.INSTANCE::method6819);
   private static final float CONST_floatValue = 8.0F;
   private static final String text = "module.entityESP.names";
   private static final String text2 = "module.entityESP.armor";
   private static final String text3 = "module.entityESP.armorText";
   private static final String text4 = "module.entityESP.ping";
   private static final String text5 = "module.entityESP.triangles";
   private static final String text6 = "module.entityESP.boxes";
   private static final String text7 = "module.entityESP.hands";
   private static final String text8 = "Tracers";
   private static final float CONST_floatValue2 = 0.6F;
   private static final float CONST_floatValue3 = 1.8F;
   private static final float CONST_floatValue4 = 18.0F;
   private static final float CONST_floatValue5 = 28.0F;
   private final List<ItemStack> items = new ArrayList<>();
   private final List<ItemStack> items2 = new ArrayList<>();
   private float previewAlpha = 1.0F;
   private final TextSetting tagOrder = method556(new TextSetting("module.entityESP.tagOrder", "HP,PING,GEAR", ""));
   private final BooleanSetting gearReversed = method556(new BooleanSetting("module.entityESP.gearReversed", "", false));
   private List<EntityESPModule.Type> items3;
   private String text9;
   private boolean flag;
   private final Map<EntityESPModule.Type, float[]> items4 = new EnumMap<>(EntityESPModule.Type.class);
   private float floatValue = 1.0F;
   private float floatValue2;
   private float floatValue3;
   private double doubleValue = Double.NaN;
   private double doubleValue2 = Double.NaN;
   private EntityESPModule.Type type;
   private EntityESPModule.Type type2;
   private float floatValue4;
   private final Map<EntityESPModule.Type, Float> items5 = new EnumMap<>(EntityESPModule.Type.class);
   private final Map<EntityESPModule.Type, Float> items6 = new EnumMap<>(EntityESPModule.Type.class);
   private final Map<EntityESPModule.Type, Float> items7 = new EnumMap<>(EntityESPModule.Type.class);
   private static final float CONST_floatValue6 = 0.35F;
   private static final float CONST_floatValue7 = 0.2F;
   private static final float CONST_floatValue8 = 160.0F;
   private final Map<String, EntityESPModule.Data4> items8 = new HashMap<>();
   private List<EntityESPModule.Data4> items9 = List.of();
   private long longValue = Long.MIN_VALUE;
   private final Jebriksheet jebriksheet = new Jebriksheet();
   private int intValue;
   private Vec3d vec3d;
   private static final float CONST_floatValue9 = 20.0F;
   private static final Map<EntityESPModule.Data2, EntityESPModule.Data3> items10 = new HashMap<>();
   private int intValue2;
   private static final float CONST_floatValue10 = 14.0F;
   private static final float CONST_floatValue11 = 4.5F;
   private static final float CONST_floatValue12 = 9.0F;
   private static final float CONST_floatValue13 = 11.0F;
   private static final float CONST_floatValue14 = 3.5F;
   private static final float CONST_floatValue15 = 9.5F;
   private static final float CONST_floatValue16 = 1.5F;
   private static final float CONST_floatValue17 = 2.0F;
   private static final float CONST_floatValue18 = 4.0F;
   private static final float CONST_floatValue19 = 9.0F;
   private static final float CONST_floatValue20 = 2.5F;
   private static final float CONST_floatValue21 = 3.5F;
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 4;
   private static final Map<String, String> items11 = Map.ofEntries(
      Map.entry("drill", "Бур"),
      Map.entry("exp", "Опыт"),
      Map.entry("foundry", "Плавка"),
      Map.entry("internal", "Внутр"),
      Map.entry("impenetrable", "Нераз"),
      Map.entry("luck", "Удача"),
      Map.entry("vampire", "Вампир"),
      Map.entry("poison", "Яд"),
      Map.entry("freeze", "Мороз"),
      Map.entry("lightning", "Молния")
   );
   private static final Map<String, String> items12 = Map.ofEntries(
      Map.entry("efficiency", "Eff"),
      Map.entry("fortune", "Fort"),
      Map.entry("silk_touch", "Silk"),
      Map.entry("unbreaking", "Unb"),
      Map.entry("mending", "Mend"),
      Map.entry("sharpness", "Sharp"),
      Map.entry("smite", "Smite"),
      Map.entry("bane_of_arthropods", "Bane"),
      Map.entry("fire_aspect", "Fire"),
      Map.entry("looting", "Loot"),
      Map.entry("knockback", "Kb"),
      Map.entry("sweeping_edge", "Sweep"),
      Map.entry("protection", "Prot"),
      Map.entry("projectile_protection", "Proj"),
      Map.entry("blast_protection", "Blast"),
      Map.entry("fire_protection", "FireProt"),
      Map.entry("feather_falling", "Feather"),
      Map.entry("depth_strider", "Depth"),
      Map.entry("thorns", "Thorns"),
      Map.entry("respiration", "Resp"),
      Map.entry("aqua_affinity", "Aqua"),
      Map.entry("soul_speed", "Soul"),
      Map.entry("swift_sneak", "Swift"),
      Map.entry("power", "Power"),
      Map.entry("punch", "Punch"),
      Map.entry("flame", "Flame"),
      Map.entry("infinity", "Inf"),
      Map.entry("loyalty", "Loyal"),
      Map.entry("riptide", "Rip"),
      Map.entry("channeling", "Chan"),
      Map.entry("impaling", "Imp"),
      Map.entry("multishot", "Multi"),
      Map.entry("quick_charge", "Quick"),
      Map.entry("piercing", "Pierce"),
      Map.entry("density", "Dens"),
      Map.entry("breach", "Breach"),
      Map.entry("wind_burst", "Wind"),
      Map.entry("luck_of_the_sea", "Luck"),
      Map.entry("lure", "Lure"),
      Map.entry("vanishing_curse", "Vanish"),
      Map.entry("binding_curse", "Bind")
   );
   private static final String[] texts = new String[]{"_helmet", "_chestplate", "_leggings", "_boots"};
   private static final String text10 = "V";
   private static final String text11 = "H";
   public static final float CONST_floatValue22 = 6.5F;
   public static final float CONST_floatValue23 = 0.65F;
   public static final float CONST_floatValue24 = 10.4F;
   private static final float CONST_floatValue25 = 2.0F;

   public float getSize() {
      return this.scaleSetting.getValue3();
   }

   private float method552(EntityESPModule.Entry var1) {
      return var1 != null && var1.getBox() != null ? this.method553(var1.getBox().getCenter()) : 1.0F;
   }

   public float method553(Vec3d var1) {
      float var2 = this.tagMinScale.getValue3();
      if (!(var2 >= 0.999F) && var1 != null && mc.gameRenderer.getCamera() != null) {
         Vec3d var3 = mc.gameRenderer.getCamera().getCameraPos();
         double var4 = Math.sqrt(var1.squaredDistanceTo(var3));
         return var4 <= 8.0 ? 1.0F : (float)MathHelper.clamp(Math.sqrt(8.0 / var4), (double)var2, 1.0);
      } else {
         return 1.0F;
      }
   }

   private static MultiSelectSetting method554(MultiSelectSetting var0, String... var1) {
      for (String var5 : var1) {
         MultiSelectSetting.Entry var6 = var0.method5222(var5);
         if (var6 != null) {
            var6.setEnabled(false);
         }
      }

      return var0;
   }

   private ZenithColor method555(ZenithColor var1) {
      return var1 != null && !(this.previewAlpha >= 0.999F) ? var1.method7294(this.previewAlpha) : var1;
   }

   private static <T extends Setting> T method556(T var0) {
      var0.setVisible(() -> false);
      return (T)var0;
   }

   private List<EntityESPModule.Type> method557() {
      String var1 = this.tagOrder.getValue();
      if (this.items3 == null || !Objects.equals(var1, this.text9)) {
         this.text9 = var1;
         this.items3 = method558(var1);
      }

      return this.items3;
   }

   private static List<EntityESPModule.Type> method558(String var0) {
      List<Type> var1 = new ArrayList<>(EntityESPModule.Type.values().length);
      if (var0 != null) {
         for (String var5 : var0.split(",")) {
            try {
               EntityESPModule.Type var6 = EntityESPModule.Type.valueOf(var5.strip().toUpperCase(Locale.ROOT));
               if (!var1.contains(var6)) {
                  var1.add(var6);
               }
            } catch (IllegalArgumentException var7) {
            }
         }
      }

      for (EntityESPModule.Type var11 : EntityESPModule.Type.values()) {
         if (!var1.contains(var11)) {
            var1.add(var11);
         }
      }

      return List.copyOf(var1);
   }

   private float method559(EntityESPModule.Type var1, float var2) {
      Float var3 = this.items6.get(var1);
      if (var3 != null && !(Math.abs(var3 - var2) > 160.0F)) {
         float var4 = MathHelper.lerp(0.35F, var3, var2);
         if (Math.abs(var4 - var2) < 0.2F) {
            var4 = var2;
         }

         this.items6.put(var1, var4);
         return var4;
      } else {
         this.items6.put(var1, var2);
         return var2;
      }
   }

   private EntityESPModule.Type method560(double var1, double var3) {
      if (!this.items4.isEmpty() && !Double.isNaN(var1) && !Double.isNaN(var3)) {
         float var5 = this.floatValue2 + (float)(var1 - this.floatValue2) / this.floatValue;
         float var6 = this.floatValue3 + (float)(var3 - this.floatValue3) / this.floatValue;

         for (Map.Entry var8 : this.items4.entrySet()) {
            float[] var9 = (float[])var8.getValue();
            if (var5 >= var9[0] && var5 <= var9[0] + var9[2] && var6 >= var9[1] && var6 <= var9[1] + 14.0F) {
               return (EntityESPModule.Type)var8.getKey();
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public boolean method561(double var1, double var3, Enum3 var5) {
      EntityESPModule.Type var6 = this.method560(var1, var3);
      if (var6 == null) {
         return false;
      } else if (var5 == Enum3.RIGHT) {
         if (var6 != EntityESPModule.Type.GEAR) {
            return false;
         } else {
            this.gearReversed.toggle();
            return true;
         }
      } else {
         this.type = var6;
         return true;
      }
   }

   public boolean method562(double var1, double var3) {
      if (this.type == null) {
         return false;
      } else {
         EntityESPModule.Type var5 = this.method560(var1, var3);
         if (var5 != null && var5 != this.type) {
            var var6 = new ArrayList<>(this.method557());
            int var7 = var6.indexOf(this.type);
            int var8 = var6.indexOf(var5);
            if (var7 >= 0 && var8 >= 0) {
               var6.set(var7, var5);
               var6.set(var8, this.type);
               this.method566(var6);
            }
         }

         return true;
      }
   }

   public void method563() {
      this.type = null;
   }

   private void method564(CustomDrawContext var1, float var2, float var3) {
      if (!(this.floatValue4 <= 0.01F)) {
         ZenithStyle var4 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         ZenithColor var5 = var4.getPrimaryColor().getZenithColor();

         for (Map.Entry var7 : this.items4.entrySet()) {
            float[] var8 = (float[])var7.getValue();
            boolean var9 = var7.getKey() == this.type2;
            float var10 = MathHelper.lerp(0.25F, this.items5.getOrDefault(var7.getKey(), 0.0F), var9 ? 1.0F : 0.35F);
            this.items5.put((EntityESPModule.Type)var7.getKey(), var10);
            float var11 = var10 * this.floatValue4 * this.previewAlpha;
            if (!(var11 <= 0.01F)) {
               float var12 = this.items7.getOrDefault(var7.getKey(), var8[0]);
               var1.drawRect(var12 + 3.5F, var2 + 14.0F - 1.0F, Math.max(2.0F, var8[2] - 3.5F), 0.8F, var5.method7294(var11));
               this.method565(var1, var12 + var8[2] / 2.0F, var3 - 1.5F - 1.5F * var10, var5.method7294(var11));
            }
         }
      }
   }

   private void method565(CustomDrawContext var1, float var2, float var3, ZenithColor var4) {
      float var5 = 0.8F;
      float var6 = 1.5F;

      for (int var7 = 0; var7 < 2; var7++) {
         for (int var8 = 0; var8 < 3; var8++) {
            var1.drawRect(var2 - var6 / 2.0F - var5 / 2.0F + var7 * var6, var3 - (2 - var8) * var6 - var5, var5, var5, var4);
         }
      }
   }

   private void method566(List<EntityESPModule.Type> var1) {
      StringBuilder var2 = new StringBuilder();

      for (EntityESPModule.Type var4 : var1) {
         if (!var2.isEmpty()) {
            var2.append(',');
         }

         var2.append(var4.name());
      }

      this.tagOrder.setValue(var2.toString());
   }

   @Override
   public ModulePreviewStage.Kind previewKind() {
      return ModulePreviewStage.Kind.ENTITY;
   }

   private EntityESPModule() {
   }

   public boolean method567() {
      return this.gradient.isEnabled();
   }

   public boolean method568() {
      return this.isEnabled() && this.elements.method5226("module.entityESP.names");
   }

   private void pushCenteredScale(CustomDrawContext var1, float var2, float var3, float var4, float var5) {
      this.method569(var1, var2, var3, var4, var5, 1.0F);
   }

   private void method569(CustomDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      if (var1 != null) {
         try {
            float var7 = this.scaleSetting.getValue3() * var6;
            var1.pushMatrix();
            var1.getMatrices().translate(var2, var3);
            var1.getMatrices().scale(var4 * var7, var5 * var7);
            var1.getMatrices().translate(-var2, -var3);
         } catch (Exception var8) {
            System.out.println("Строка  82 " + var8.getMessage());
         }
      }
   }

   private boolean method570(float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = this.scaleSetting.getValue3() * var7;
      float var9 = var5 + (var1 - 20.0F - var5) * var8;
      float var10 = var5 + (var3 + 20.0F - var5) * var8;
      float var11 = var6 + (var2 - 20.0F - var6) * var8;
      float var12 = var6 + (var4 + 20.0F - var6) * var8;
      return var10 < 0.0F || var12 < 0.0F || var9 > mc.getWindow().getScaledWidth() || var11 > mc.getWindow().getScaledHeight();
   }

   private void pop(CustomDrawContext var1) {
      if (var1 != null) {
         try {
            var1.popMatrix();
         } catch (Exception var3) {
            System.out.println("Строка  91 " + var3.getMessage());
         }
      }
   }

   @EventTarget
   public void method571(WorldRenderEvent var1) {
      boolean var2 = this.elements.method5226("module.entityESP.boxes");
      boolean var3 = this.elements.method5226("Tracers");
      if (var2 || var3) {
         try {
            Vec3d var4 = var3
               ? new Vec3d(0.0, 0.0, 75.0)
                  .rotateX(-((float)Math.toRadians(mc.gameRenderer.getCamera().getPitch())))
                  .rotateY(-((float)Math.toRadians(mc.gameRenderer.getCamera().getYaw())))
                  .add(FreeCamModule.INSTANCE.method1660())
               : null;

            for (EntityESPModule.Data4 var6 : this.method574()) {
               EntityESPModule.Entry var7 = var6.getEntry();
               if ((var7.isFlag2() || var7.isFlag3()) && (!var7.isFlag2() || !var7.isFlag7())) {
                  try {
                     if (var2) {
                        if (var7.isFlag()) {
                           GlowUtil.method2230(
                              var7.getBox(), Zenith.getInstance().getStyleManager().getCurrentStyle().getFriendColor().getZenithColor().method7287(), 1.0F
                           );
                        } else {
                           GlowUtil.method2231(
                              var7.getBox(),
                              Zenith.getInstance().getStyleManager().getClientColor(0).method7294(0.7F).method7287(),
                              Zenith.getInstance().getStyleManager().getClientColor(180).method7287(),
                              1.0F
                           );
                        }
                     }

                     if (var3) {
                        GlowUtil.method2245(var4, var7.getBox().getCenter(), Zenith.getInstance().getStyleManager().getClientColor(0).method7287(), 1.5F, false);
                     }
                  } catch (Exception var9) {
                     System.out.println("ESP 3D render error: " + var9.getMessage());
                  }
               }
            }
         } catch (Exception var10) {
            System.out.println("ESP 3D loop error: " + var10.getMessage());
         }
      }
   }

   @EventTarget
   public void method572(DrawEvent var1) {
      if (mc.player != null && mc.world != null && mc.getEntityRenderDispatcher().camera != null) {
         CustomDrawContext var2 = var1.getCustomDrawContext();
         if (var2 != null) {
            this.intValue++;
            this.vec3d = mc.gameRenderer != null && mc.gameRenderer.getCamera() != null ? this.method615() : null;

            try {
               ShapeRenderer.method7157(var2);
               QueuedCustomDrawContext var3 = QueuedCustomDrawContext.of(var2);
               Jebriksheet.method2346(this.jebriksheet);

               try {
                  for (EntityESPModule.Data4 var5 : this.method574()) {
                     try {
                        EntityESPModule.Entry var6 = var5.getEntry();
                        if (!var6.isFlag2() && !var6.isFlag3()) {
                           if (var6.isFlag4()) {
                              if (this.targets.method5226("module.entityESP.droppedItems")) {
                                 this.method607(var3, var6);
                              }
                           } else if (var6.isFlag5() && this.elements.method5226("module.entityESP.names")) {
                              this.method585(var3, var6);
                           }
                        } else {
                           if (this.elements.method5226("module.entityESP.hands")) {
                              Vector4d var7 = this.method616(var6);
                              if (var7 != null) {
                                 this.method583(var3, var6, var7);
                              }
                           }

                           if (this.elements.method5226("module.entityESP.triangles")) {
                              this.method606(var3, var6);
                           }

                           if (this.elements.method5226("module.entityESP.names")) {
                              this.method585(var3, var6);
                           }
                        }
                     } catch (Exception var18) {
                        System.out.println("ESP 2D render error: " + var18.getMessage());
                     }
                  }

                  this.jebriksheet.flush();
               } finally {
                  Jebriksheet.method2347(this.jebriksheet);
               }
            } catch (Exception var20) {
               System.out.println("Строка  145 " + var20.getMessage());
            } finally {
               this.vec3d = null;
            }
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      this.method576();
      if (++this.intValue2 >= 20) {
         this.intValue2 = 0;
         items10.clear();
      }
   }

   private EntityESPModule.Data3 method573(ItemStack var1) {
      EntityESPModule.Data2 var2 = EntityESPModule.Data2.method630(var1);
      EntityESPModule.Data3 var3 = items10.get(var2);
      if (var3 == null) {
         String var4 = this.method593(var1);
         if (var4 == null) {
            var4 = Names.method4516(var1);
         }

         var3 = new EntityESPModule.Data3(var4, this.method594(var1));
         if (items10.size() > 512) {
            items10.clear();
         }

         items10.put(var2, var3);
      }

      return var3;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.isEnabled()) {
         this.method576();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.items8.clear();
      this.items9 = List.of();
   }

   private List<EntityESPModule.Data4> method574() {
      if (mc.world == null) {
         if (!this.items9.isEmpty()) {
            this.items8.clear();
            this.items9 = List.of();
         }

         return this.items9;
      } else {
         long var1 = this.method575();
         if (var1 != this.longValue) {
            this.longValue = var1;
            int var3 = 0;

            for (int var4 = this.items9.size(); var3 < var4; var3++) {
               this.items9.get(var3).getEntry().method631();
            }
         }

         return this.items9;
      }
   }

   private long method575() {
      long var1 = mc.world == null ? 0L : mc.world.getTime();
      long var3 = Float.floatToIntBits(mc.getRenderTickCounter().getTickProgress(false)) & 4294967295L;
      return var1 << 32 ^ var3;
   }

   private void method576() {
      long var1 = System.currentTimeMillis();
      if (mc.world != null) {
         for (Entity var4 : mc.world.getEntities()) {
            if (var4 instanceof PlayerEntity var5) {
               if (!PetManager.method4557(var5.getId())) {
                  EntityESPModule.Entry var9 = this.method577(var5);
                  if (var9 != null) {
                     this.items8.put(var9.key(), new EntityESPModule.Data4(var9, var1));
                  }
               }
            } else if (var4 instanceof MobEntity var6) {
               EntityESPModule.Entry var11 = this.method578(var6);
               if (var11 != null) {
                  this.items8.put(var11.key(), new EntityESPModule.Data4(var11, var1));
               }
            } else if (var4 instanceof TntEntity var7) {
               EntityESPModule.Entry var12 = this.method579(var7);
               if (var12 != null) {
                  this.items8.put(var12.key(), new EntityESPModule.Data4(var12, var1));
               }
            } else if (var4 instanceof ItemEntity var8) {
               EntityESPModule.Entry var13 = this.method580(var8);
               if (var13 != null) {
                  this.items8.put(var13.key(), new EntityESPModule.Data4(var13, var1));
               }
            }
         }
      }

      this.method581(var1);
      long var10 = (long)(this.removeHiddenPlayerDelay.getValue3() * 50.0F);
      this.items8.values().removeIf(var4x -> var1 - var4x.getLongValue() > (var4x.entry.flag2 ? var10 : 200L));
      this.items9 = new ArrayList<>(this.items8.values());
      this.longValue = Long.MIN_VALUE;
   }

   private EntityESPModule.Entry method577(PlayerEntity var1) {
      boolean var2 = Zenith.getInstance().getFriends().method7627(var1.getName().getString());
      if (var2 && !this.targets.method5226("module.entityESP.friends")) {
         return null;
      } else {
         if (!var2) {
            boolean var3 = UseItemUtils.method6529(var1) == 0.0F;
            if (var3 && !this.targets.method5226("module.entityESP.noArmor")) {
               return null;
            }

            if (!var3 && !this.targets.method5226("module.entityESP.players")) {
               return null;
            }
         }

         Vec3d var10 = MathUtils.method2656(var1);
         Box var4 = var1.getBoundingBox().offset(var10.subtract(var1.getEntityPos()));
         float var5 = UseItemUtils.method6527(var1);
         ArrayList var6 = new ArrayList();
         ArmorSlots.method5385(var1).forEach(var6::add);
         ArrayList<ItemStack> var7 = new ArrayList();
         var7.add(var1.getOffHandStack());
         var7.addAll(ArmorSlots.method5384(var1));
         var7.add(var1.getMainHandStack());

         for (Object var9_o : (Iterable<?>)(Object)(var7)) {
            ItemStack var9 = (ItemStack)var9_o;
            Util14.method2637(var1, var9);
         }

         return new EntityESPModule.Entry(
            "player:" + var1.getUuidAsString(),
            StreamerModeModule.INSTANCE.method6292(var1.getDisplayName() != null ? var1.getDisplayName() : var1.getName(), var1.getGameProfile().name()),
            var2,
            true,
            false,
            false,
            false,
            false,
            var1 == mc.player,
            var4,
            var5,
            0,
            var6,
            var7,
            null,
            var1,
            null
         );
      }
   }

   private EntityESPModule.Entry method578(MobEntity var1) {
      if (!this.targets.method5226("module.entityESP.mobs")) {
         return null;
      } else {
         Vec3d var2 = MathUtils.method2656(var1);
         Box var3 = var1.getBoundingBox().offset(var2.subtract(var1.getEntityPos()));
         float var4 = UseItemUtils.method6527(var1);
         return new EntityESPModule.Entry(
            "mob:" + var1.getUuidAsString(),
            var1.getDisplayName() != null ? var1.getDisplayName() : var1.getName(),
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            var3,
            var4,
            0,
            List.of(),
            List.of(),
            null,
            var1,
            null
         );
      }
   }

   private EntityESPModule.Entry method579(TntEntity var1) {
      if (!this.targets.method5226("TNT")) {
         return null;
      } else {
         Vec3d var2 = MathUtils.method2656(var1);
         Box var3 = var1.getBoundingBox().offset(var2.subtract(var1.getEntityPos()));
         return new EntityESPModule.Entry(
            "tnt:" + var1.getUuidAsString(),
            var1.getDisplayName(),
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            var3,
            -1.0F,
            var1.getFuse(),
            List.of(),
            List.of(),
            null,
            var1,
            null
         );
      }
   }

   private EntityESPModule.Entry method580(ItemEntity var1) {
      if (var1.getStack().isEmpty()) {
         return null;
      } else {
         Names.method4515(var1.getStack());
         if (!this.targets.method5226("module.entityESP.droppedItems")) {
            return null;
         } else {
            Vec3d var2 = MathUtils.method2656(var1);
            Box var3 = var1.getBoundingBox().offset(var2.subtract(var1.getEntityPos()));
            return new EntityESPModule.Entry(
               "item:" + var1.getUuidAsString(),
               var1.getStack().getCustomName() != null ? var1.getStack().getCustomName() : var1.getName(),
               false,
               false,
               false,
               true,
               false,
               false,
               false,
               var3,
               -1.0F,
               0,
               List.of(),
               List.of(),
               var1.getStack().copy(),
               var1,
               null
            );
         }
      }
   }

   private void method581(long var1) {
      this.items8.entrySet().removeIf(var0 -> var0.getValue().getEntry().isFlag6());
      if (this.targets.method5226("module.entityESP.friends") && mc.world != null && mc.getNetworkHandler() != null) {
         Cloud var3 = Zenith.getInstance().getCloud();
         if (var3 != null && var3.method7862()) {
            String var4 = var3.method7911();
            String var5 = var3.method7913();
            HashSet var6 = null;

            for (PartyPlayerInfo var8 : var3.state().method8038()) {
               if (var8.method3291(var4, var5) && var1 - var8.getLongValue2() <= 10000L) {
                  String var9 = var8.getText2();
                  if (var9 != null && !var9.isBlank() && !var3.prefs().method3316(var8.username())) {
                     if (var6 == null) {
                        var6 = new HashSet();

                        for (PlayerEntity var11 : mc.world.getPlayers()) {
                           var6.add(var11.getGameProfile().name().toLowerCase(Locale.ROOT));
                        }
                     }

                     if (!var6.contains(var9.toLowerCase(Locale.ROOT))) {
                        Vec3d var13 = var8.method3294();
                        if (var13 != null) {
                           Box var14 = new Box(var13.x - 0.3, var13.y, var13.z - 0.3, var13.x + 0.3, var13.y + 1.8, var13.z + 0.3);
                           EntityESPModule.Entry var12 = new EntityESPModule.Entry(
                              "cloud-player:" + var8.uid(),
                              Text.literal(var9),
                              true,
                              true,
                              false,
                              false,
                              false,
                              true,
                              false,
                              var14,
                              var8.getFloatValue3(),
                              0,
                              List.of(),
                              method582(var8),
                              null,
                              null,
                              var8
                           );
                           this.items8.put(var12.key(), new EntityESPModule.Data4(var12, var1));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static List<ItemStack> method582(PartyPlayerInfo var0) {
      CloudPackets.Data7[] var1 = var0.getData7s();
      if (var1.length <= 40) {
         return List.of();
      } else {
         ArrayList var2 = new ArrayList(5);
         var2.add(stackOf(var1[40]));

         for (int var3 = 36; var3 <= 39; var3++) {
            var2.add(stackOf(var1[var3]));
         }

         return var2;
      }
   }

   private static ItemStack stackOf(CloudPackets.Data7 var0) {
      if (var0 != null && !var0.isEmpty()) {
         Identifier var1 = Identifier.tryParse(var0.getText());
         if (var1 != null && Registries.ITEM.containsId(var1)) {
            Item var2 = Registries.ITEM.get(var1);
            if (var2 == Items.AIR) {
               return ItemStack.EMPTY;
            } else {
               ItemStack var3 = new ItemStack(var2, Math.max(1, Math.min(127, var0.count())));
               if (var0.getIntValue2() > 0) {
                  var3.setDamage(var0.getIntValue2());
               }

               return var3;
            }
         } else {
            return ItemStack.EMPTY;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   private void method583(CustomDrawContext var1, EntityESPModule.Entry var2, Vector4d var3) {
      if (var2 != null && var1 != null && var3 != null) {
         if (!var2.isFlag7() || !mc.options.getPerspective().isFirstPerson()) {
            if (!ProjectionUtils.method304(var3)) {
               if (!var2.getItems().isEmpty()) {
                  try {
                     float var4 = this.method552(var2);
                     float var5 = (float)ProjectionUtils.method305(var3);
                     ZenithStyle var6 = Zenith.getInstance().getStyleManager().getCurrentStyle();
                     Font var7 = Fonts.NEW_MEDIUM.getFont(6.5F);
                     float var8 = var7.height() + GuiStyle.PADDING.intValue() + 1.0F;
                     float var9 = this.scaleSetting.getValue3() * var4;
                     float var10 = (var8 + 2.0F) * var9;
                     int var11 = 0;

                     for (ItemStack var13 : var2.getItems()) {
                        if (var13 != null && !var13.isEmpty()) {
                           var11++;
                        }
                     }

                     float var32 = mc.getWindow().getScaledHeight() - var11 * var10 - 2.0F;
                     double var33 = Math.min(var3.w + 2.0, (double)var32);
                     var var15 = var2.getItems();
                     EntityESPModule.Data[] var16 = var2.datas;
                     if (var16 == null || var16.length != var15.size()) {
                        var16 = var2.datas = new EntityESPModule.Data[var15.size()];
                     }

                     for (int var17 = 0; var17 < var15.size(); var17++) {
                        ItemStack var18 = (ItemStack)var15.get(var17);
                        if (var18 != null && !var18.isEmpty()) {
                           try {
                              EntityESPModule.Data var19 = var16[var17];
                              if (var19 == null || var19.getItemStack() != var18) {
                                 var19 = var16[var17] = this.method584(var18);
                              }

                              String var21 = var19.getText2();
                              MutableText var20;
                              if (var21 != null) {
                                 var20 = Text.literal(var21)
                                    .setStyle(Style.EMPTY.withColor(Zenith.getInstance().getStyleManager().getClientColor(90).method7287()));
                              } else {
                                 var20 = var19.name().copy();
                              }

                              String var22 = var19.getText3();
                              if (var22 != null) {
                                 var20.append(
                                    Text.of(" " + var22).copy().setStyle(Style.EMPTY.withColor(var6.getTextSecondary().getZenithColor().method7287()))
                                 );
                              }

                              boolean var23 = !(var2.getEntity() instanceof PlayerEntity var24 && !Util14.method2638(var24));
                              if ((!Zenith.getInstance().getServerDetector().method1642() || var2.flag7) && var23 && var18.getCount() > 1) {
                                 var20.append(
                                    Text.of(" x" + var18.getCount())
                                       .copy()
                                       .setStyle(Style.EMPTY.withColor(var6.getPrimaryColor().getZenithColor().method7287()))
                                 );
                              }

                              ZenithColor var34 = var6.getHudBackground().getZenithColor();
                              float var35 = 19.5F + var7.width(var20);
                              float var27 = var5 - var35 / 2.0F;
                              float var28 = (float)var33;
                              var33 += var10;
                              if (!this.method570(var27, var28, var27 + var35, var28 + var8, var5, var28, var4)) {
                                 this.method569(var1, var5, var28, 1.0F, 1.0F, var4);
                                 ProxyDotRadiusData var29 = ProxyDotRadiusData.method8069(InterfaceModule.method6801() * 0.6F);
                                 var1.drawBlurHudBooleanCheck(
                                    var27,
                                    var28,
                                    var35,
                                    var8,
                                    22.0F,
                                    var29,
                                    ZenithColor.INSTANCE,
                                    this.blur.isEnabled() && this.blur.isVisible(),
                                    this.glow.isEnabled() && this.glow.isVisible()
                                 );
                                 var1.drawRoundedRect(var27, var28, var35, var8, var29, var34);
                                 var1.pushMatrix();
                                 var1.getMatrices().translate(var27 + 4.0F, var28 + (var8 - 9.0F) / 2.0F);
                                 var1.getMatrices().scale(0.5625F, 0.5625F);
                                 var1.drawItem(var18, 0, 0);
                                 var1.popMatrix();
                                 var1.pushMatrix();
                                 var1.getMatrices().translate(var27 + 4.0F + 9.0F + 2.5F, var28 + (var8 - 1.0F - var7.height()) / 2.0F);
                                 var1.drawText(var7, var20, 0.0F, 0.0F, var6.getTextEnable().getZenithColor().method7287());
                                 var1.popMatrix();
                                 this.pop(var1);
                              }
                           } catch (Exception var30) {
                              System.out.println("drawHands err: " + var30.getMessage());
                           }
                        }
                     }
                  } catch (Exception var31) {
                     System.out.println("drawHands outer err: " + var31.getMessage());
                  }
               }
            }
         }
      }
   }

   private EntityESPModule.Data method584(ItemStack var1) {
      EntityESPModule.Data3 var2 = this.method573(var1);
      String var3 = var2.getText();
      MutableText var4 = null;
      if (var3 == null) {
         MutableText var5 = var1.getName().copy();
         String var6 = var5.getString();
         if (!var6.isEmpty() && var6.endsWith(" ")) {
            var5 = TextUtils.method2116(var5, "");
         }

         var4 = var5;
      }

      return new EntityESPModule.Data(var1, var4, var3, var2.getText2());
   }

   private void method585(CustomDrawContext var1, EntityESPModule.Entry var2) {
      if (var2 != null && var1 != null) {
         if (!var2.isFlag7() || !mc.options.getPerspective().isFirstPerson()) {
            try {
               Box var3 = var2.getBox();
               Vec3d var4 = new Vec3d(var3.getCenter().x, var3.maxY + 0.15F, var3.getCenter().z);
               if (!this.method614(var4)) {
                  return;
               }

               Vec3d var5 = ProjectionUtils.method301(var4);
               if (var5 == null || var5.z <= 0.0) {
                  return;
               }

               ZenithStyle var6 = Zenith.getInstance().getStyleManager().getCurrentStyle();
               boolean var7 = var2.isFlag();
               Text var8 = var2.name() != null ? var2.name() : Text.of("unknown");
               Object var9 = (!var7 || !NameProtectModule.INSTANCE.isEnabled() || !NameProtectModule.INSTANCE.method5140()) && !var2.flag7
                  ? var8
                  : Text.literal(NameProtectModule.method5141());
               float var10 = var2.getFloatValue();
               if (!var2.flag10) {
                  var2.text4 = var2.isFlag5() ? this.method618(var2.getIntValue()) : (var10 >= 0.0F ? UseItemUtils.method6532(var10) : null);
                  var2.flag10 = true;
               }

               String var11 = var2.text4;
               ZenithColor var12 = var2.isFlag5()
                  ? var6.getPrimaryColor().getZenithColor()
                  : (var10 >= 0.0F ? this.method617(var10) : var6.getTextEnable().getZenithColor());
               ZenithColor var13 = var7 ? var6.getFriendColor().getZenithColor().method7296(0.5F) : var6.getHudBackground().getZenithColor();
               float var14 = this.method552(var2);
               this.method587(
                  var1,
                  (float)var5.x,
                  (float)var5.y - 1.0F,
                  (Text)var9,
                  this.method604(var2),
                  var11,
                  var12,
                  this.elements.method5226("module.entityESP.ping") ? this.method605(var2) : null,
                  this.elements.method5226("module.entityESP.armor") ? this.method603(method602(var2.getItems2())) : List.of(),
                  var13,
                  var14,
                  this.elements.method5226("module.entityESP.armorText") ? this.method600(var2) : null
               );
            } catch (Exception var15) {
               System.out.println("renderNameTag err: " + var15.getMessage());
            }
         }
      }
   }

   private float method586(
      CustomDrawContext var1,
      float var2,
      float var3,
      Text var4,
      Identifier var5,
      String var6,
      ZenithColor var7,
      String var8,
      List<ItemStack> var9,
      ZenithColor var10
   ) {
      return this.method587(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, 1.0F, null);
   }

   private float method587(
      CustomDrawContext var1,
      float var2,
      float var3,
      Text var4,
      Identifier var5,
      String var6,
      ZenithColor var7,
      String var8,
      List<ItemStack> var9,
      ZenithColor var10,
      float var11,
      Text var12
   ) {
      ZenithStyle var13 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      Font var14 = Fonts.NEW_MEDIUM.getFont(6.5F);
      Font var15 = Fonts.NEW_MEDIUM.getFont(6.0F);
      Font var16 = Fonts.NEW_ICONS.getFont(5.0F);
      List<EntityESPModule.Type> var17 = this.method557();
      float var18 = 4.5F;
      if (var5 != null) {
         var18 += 9.0F + GuiStyle.PADDING.intValue() * 0.75F;
      }

      var18 += var14.width(var4);

      for (Object var20_o : (Iterable<?>)(Object)(var17)) {
         EntityESPModule.Type var20 = (EntityESPModule.Type)var20_o;
         var18 += this.method589(var20, var16, var15, var6, var8, var9);
      }

      var18 += 4.5F;
      float var30 = var2 - var18 / 2.0F;
      float var31 = var3 - 14.0F;
      if (!this.flag) {
         float var21 = var18 / 2.0F;
         float var22 = var31;
         if (var12 != null) {
            var21 = Math.max(var21, (Fonts.NEW_MEDIUM.getFont(5.5F).width(var12) + 7.0F) / 2.0F);
            var22 = var31 - 1.5F - 9.5F;
         }

         if (this.method570(var2 - var21, var22, var2 + var21, var3, var2, var3, var11)) {
            float var38 = var12 == null ? 14.0F : 25.0F;
            return var3 - var38 * this.scaleSetting.getValue3() * var11;
         }
      }

      this.method569(var1, var2, var3, 1.0F, 1.0F, var11);
      if (this.flag) {
         this.floatValue = Math.max(1.0E-4F, this.scaleSetting.getValue3() * var11);
         this.floatValue2 = var2;
         this.floatValue3 = var3;
      }

      if (var12 != null) {
         Font var32 = Fonts.NEW_MEDIUM.getFont(5.5F);
         float var34 = var32.width(var12) + 7.0F;
         float var23 = var2 - var34 / 2.0F;
         float var24 = var31 - 1.5F - 9.5F;
         ProxyDotRadiusData var25 = ProxyDotRadiusData.method8069(Math.min(InterfaceModule.method6801(), 4.75F));
         var1.drawBlurHudBooleanCheck(
            var23, var24, var34, 9.5F, 22.0F, var25, ZenithColor.INSTANCE, this.method627(), this.glow.isEnabled() && this.glow.isVisible()
         );
         var1.drawRoundedRect(var23, var24, var34, 9.5F, var25, var10);
         var1.drawText(var32, var12, var23 + 3.5F, var24 + (9.5F - var32.height()) / 2.0F, this.method555(ZenithColor.INSTANCE).method7287());
      }

      ProxyDotRadiusData var33 = ProxyDotRadiusData.method8069(Math.min(InterfaceModule.method6801(), 7.0F));
      var1.drawBlurHudBooleanCheck(
         var30, var31, var18, 14.0F, 22.0F, var33, this.method555(ZenithColor.INSTANCE), this.method627(), this.glow.isEnabled() && this.glow.isVisible()
      );
      var1.drawRoundedRect(var30, var31, var18, 14.0F, var33, this.method555(var10));
      float var35 = var30 + 4.5F;
      if (var5 != null) {
         var1.drawPlayerHeadWithRoundedShader(var5, var35, var31 + 2.5F, 9.0F, ProxyDotRadiusData.method8069(4.5F), this.method555(ZenithColor.INSTANCE));
         var35 += 9.0F + GuiStyle.PADDING.intValue() * 0.75F;
      }

      var1.drawText(var14, var4, var35, var31 + (14.0F - var14.height()) / 2.0F, this.method555(var13.getTextEnable().getZenithColor()).method7287());
      var35 += var14.width(var4);
      if (this.flag) {
         this.items4.clear();
      }

      float var37 = var35;

      for (Object var41_o : (Iterable<?>)(Object)(var17)) {
         EntityESPModule.Type var41 = (EntityESPModule.Type)var41_o;
         float var26 = this.method589(var41, var16, var15, var6, var8, var9);
         if (!(var26 <= 0.0F)) {
            float var27 = var35;
            if (this.flag) {
               var27 = var37 + this.method559(var41, var35 - var37);
               this.items7.put(var41, var27);
            }

            switch (var41) {
               case HP:
                  this.method591(var1, var27, var31, var16, var15, "V", var6, var7, var13);
                  break;
               case PING:
                  this.method591(var1, var27, var31, var16, var15, "H", var8, var13.getTextSecondary().getZenithColor(), var13);
                  break;
               case GEAR:
                  this.method588(var1, var27, var31, var9, var13);
            }

            if (this.flag) {
               this.items4.put(var41, new float[]{var35, var31, var26});
            }

            var35 += var26;
         }
      }

      if (this.flag) {
         this.type2 = this.type != null ? this.type : this.method560(this.doubleValue, this.doubleValue2);
         this.floatValue4 = MathHelper.lerp(0.25F, this.floatValue4, this.type2 != null ? 1.0F : 0.0F);
         this.method564(var1, var31, var12 == null ? var31 : var31 - 1.5F - 9.5F);
      }

      this.pop(var1);
      float var40 = var12 == null ? 14.0F : 25.0F;
      return var3 - var40 * this.scaleSetting.getValue3() * var11;
   }

   private float method588(CustomDrawContext var1, float var2, float var3, List<ItemStack> var4, ZenithStyle var5) {
      var2 += 3.5F;
      this.method592(var1, var2, var3, var5);
      var2 += 4.5F;
      boolean var6 = this.previewAlpha < 0.999F;
      if (var6) {
         RenderState.method1883(1.0F, 1.0F, 1.0F, this.previewAlpha);
      }

      try {
         for (ItemStack var8 : var4) {
            var1.pushMatrix();
            var1.getMatrices().translate(var2, var3 + 1.5F);
            var1.getMatrices().scale(0.6875F, 0.6875F);
            var1.drawItem(var8, 0, 0);
            var1.popMatrix();
            var2 += 11.0F;
         }
      } finally {
         if (var6) {
            RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      return var2;
   }

   private float method589(EntityESPModule.Type var1, Font var2, Font var3, String var4, String var5, List<ItemStack> var6) {
      return switch (var1) {
         case HP -> var4 == null ? 0.0F : this.method590(var2, var3, "V", var4);
         case PING -> var5 == null ? 0.0F : this.method590(var2, var3, "H", var5);
         case GEAR -> var6.isEmpty() ? 0.0F : 8.0F + var6.size() * 11.0F;
      };
   }

   private float method590(Font var1, Font var2, String var3, String var4) {
      return 8.0F + var1.width(var3) + 2.0F + var2.width(var4);
   }

   private float method591(CustomDrawContext var1, float var2, float var3, Font var4, Font var5, String var6, String var7, ZenithColor var8, ZenithStyle var9) {
      var2 += 3.5F;
      this.method592(var1, var2, var3, var9);
      var2 += 4.5F;
      var1.drawText(var4, var6, var2, var3 + (14.0F - var4.height()) / 2.0F, this.method555(var8));
      var2 += var4.width(var6) + 2.0F;
      var1.drawText(var5, var7, var2, var3 + (14.0F - var5.height()) / 2.0F, this.method555(var8));
      return var2 + var5.width(var7);
   }

   private void method592(CustomDrawContext var1, float var2, float var3, ZenithStyle var4) {
      float var5 = 7.0F;
      var1.drawRect(var2, var3 + (14.0F - var5) / 2.0F, 1.0F, var5, this.method555(var4.getTextTertiary().getZenithColor().method7294(0.45F)));
   }

   private String method593(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         NbtComponent var2 = var1.get(DataComponentTypes.CUSTOM_DATA);
         if (var2 == null) {
            return null;
         } else {
            NbtCompound var3 = var2.copyNbt();
            if (!var3.contains("itemServiceId")) {
               return null;
            } else {
               String var4 = var3.getCompoundOrEmpty("itemServiceId").getString("name", "").replace("_", " ");
               if (var4.isEmpty()) {
                  return null;
               } else {
                  if (var1.getItem() == Items.TOTEM_OF_UNDYING) {
                     var4 = "Талисман Eternity";
                  }

                  var4 = Character.toUpperCase(var4.charAt(0)) + var4.substring(1);
                  var4 = var4.replace("2", " 2").replace("1", " 1");
                  if (var4.equals("Мифическая сфера 1")) {
                     var4 = "Мифка у3 б2";
                  } else if (var4.equals("Мифическая сфера 2")) {
                     var4 = "Мифка б3 у2";
                  }

                  return var4;
               }
            }
         }
      } else {
         return null;
      }
   }

   private String method594(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         ItemEnchantmentsComponent var2 = var1.get(DataComponentTypes.ENCHANTMENTS);
         if (var2 != null && !var2.isEmpty()) {
            ArrayList<RegistryEntry> var3 = new ArrayList<>(var2.getEnchantments());
            var3.sort(Comparator.<RegistryEntry>comparingInt(e -> var2.getLevel(e)).reversed());
            StringBuilder var4 = new StringBuilder();
            int var5 = 0;

            for (Object var7_o : (Iterable<?>)(Object)(var3)) {
               RegistryEntry var7 = (RegistryEntry)var7_o;
               if (var5 == 4) {
                  var4.append(" +").append(var3.size() - var5);
                  break;
               }

               if (var5 > 0) {
                  var4.append(' ');
               }

               var4.append(method597(var7)).append(var2.getLevel(var7));
               var5++;
            }

            for (String var9 : method595(var1)) {
               var4.append(' ').append(var9);
            }

            return var4.isEmpty() ? null : var4.toString();
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static List<String> method595(ItemStack var0) {
      ArrayList<String> var1 = new ArrayList<>();

      for (String var3 : Names.method4519(var0)) {
         int var4 = var3.lastIndexOf(58);
         if (var4 > 0) {
            String var5 = var3.substring(0, var4);
            if (!var5.startsWith("minecraft:")) {
               int var6 = 1;

               try {
                  var6 = Integer.parseInt(var3.substring(var4 + 1));
               } catch (NumberFormatException var8) {
               }

               var1.add(method596(var5) + (var6 > 1 ? String.valueOf(var6) : ""));
            }
         }
      }

      return var1;
   }

   private static String method596(String var0) {
      String var1 = var0.contains(":") ? var0.substring(var0.indexOf(58) + 1) : var0;
      if (var1.endsWith("-enchant-custom")) {
         var1 = var1.substring(0, var1.length() - "-enchant-custom".length());
      }

      String var2 = items11.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         var1 = var1.replace('-', ' ').replace('_', ' ');
         return var1.isEmpty() ? "?" : Character.toUpperCase(var1.charAt(0)) + var1.substring(1);
      }
   }

   private static String method597(RegistryEntry<Enchantment> var0) {
      String var1 = var0.getKey().map(var0x -> var0x.getValue().getPath()).orElse("?");
      String var2 = items12.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         String var3 = var1.replace("_", "");
         String var4 = var3.length() <= 4 ? var3 : var3.substring(0, 4);
         return var4.isEmpty() ? "?" : Character.toUpperCase(var4.charAt(0)) + var4.substring(1);
      }
   }

   private String method598(ItemStack var1) {
      Text var2 = var1.get(DataComponentTypes.CUSTOM_NAME);
      if (var2 == null) {
         return null;
      } else {
         String var3 = var2.getString().replaceAll("§.", "").trim();
         return var3.isEmpty() ? null : var3;
      }
   }

   private static String method599(ItemStack var0) {
      String var1 = Registries.ITEM.getId(var0.getItem()).getPath();

      for (String var5 : texts) {
         if (var1.endsWith(var5)) {
            var1 = var1.substring(0, var1.length() - var5.length());
            break;
         }
      }

      var1 = var1.replace('_', ' ').trim();
      return var1.isEmpty() ? null : Character.toUpperCase(var1.charAt(0)) + var1.substring(1);
   }

   private Text method600(EntityESPModule.Entry var1) {
      if (var1 != null && var1.isFlag2()) {
         if (!var1.flag8) {
            this.method601(var1);
            var1.flag8 = true;
         }

         if (var1.text3 == null) {
            return null;
         } else {
            ZenithColor var2 = !var1.flag9
               ? Zenith.getInstance().getStyleManager().getCurrentStyle().getTextSecondary().getZenithColor()
               : Zenith.getInstance().getStyleManager().getClientColor(90);
            return Text.literal(var1.text3).setStyle(Style.EMPTY.withColor(var2.method7287()));
         }
      } else {
         return null;
      }
   }

   private void method601(EntityESPModule.Entry var1) {
      var1.text3 = null;
      var1.flag9 = false;
      var var2 = var1.getItems2();
      if (var2 != null && var2.size() >= 5) {
         LinkedHashSet var3 = new LinkedHashSet();
         LinkedHashSet var4 = new LinkedHashSet();

         for (int var5 = 1; var5 <= 4; var5++) {
            ItemStack var6 = (ItemStack)var2.get(var5);
            if (var6 != null && !var6.isEmpty()) {
               String var7 = this.method593(var6);
               if (var7 == null) {
                  var7 = this.method598(var6);
               }

               if (var7 == null) {
                  var7 = Names.method4516(var6);
               }

               if (var7 != null) {
                  var3.add(var7);
               } else {
                  String var9 = method599(var6);
                  if (var9 != null) {
                     var4.add(var9);
                  }
               }
            }
         }

         LinkedHashSet<String> var14 = var3.isEmpty() ? var4 : var3;
         if (!var14.isEmpty()) {
            StringBuilder var15 = new StringBuilder();
            int var16 = 0;

            for (Object var18_o : (Iterable<?>)(Object)(var14)) {
               String var18 = (String)var18_o;
               if (var16 == 2) {
                  var15.append(" …");
                  break;
               }

               if (var16 > 0) {
                  var15.append(" · ");
               }

               var15.append(var18);
               var16++;
            }

            int var17 = 0;

            for (int var19 = 1; var19 <= 4; var19++) {
               ItemStack var10 = (ItemStack)var2.get(var19);
               if (var10 != null && !var10.isEmpty()) {
                  ItemEnchantmentsComponent var11 = var10.get(DataComponentTypes.ENCHANTMENTS);
                  if (var11 != null && !var11.isEmpty()) {
                     for (RegistryEntry var13 : var11.getEnchantments()) {
                        if (var13.getKey().map(var0 -> ((net.minecraft.registry.RegistryKey<?>)var0).getValue().getPath()).orElse("").equals("protection")) {
                           var17 += var11.getLevel(var13);
                        }
                     }
                  }
               }
            }

            if (var17 > 0) {
               var15.append(" · Prot").append(var17);
            }

            var1.text3 = var15.toString();
            var1.flag9 = !var3.isEmpty();
         }
      }
   }

   private static List<ItemStack> method602(List<ItemStack> var0) {
      return var0 != null && var0.size() >= 5 ? var0.subList(1, 5) : List.of();
   }

   private List<ItemStack> method603(List<ItemStack> var1) {
      ArrayList<ItemStack> var2 = new ArrayList<>();

      for (ItemStack var4 : var1) {
         if (var4 != null && !var4.isEmpty()) {
            var2.add(var4);
         }
      }

      if (this.gearReversed.isEnabled()) {
         Collections.reverse(var2);
      }

      return var2;
   }

   private Identifier method604(EntityESPModule.Entry var1) {
      if (var1.getEntity() instanceof AbstractClientPlayerEntity var2 && var2.getSkin() != null) {
         return var2.getSkin().body().texturePath();
      } else {
         return var1.isFlag2() ? DefaultSkinHelper.getSteve().body().texturePath() : null;
      }
   }

   private String method605(EntityESPModule.Entry var1) {
      if (var1.isFlag2() && mc.getNetworkHandler() != null) {
         if (var1.getEntity() instanceof PlayerEntity var2) {
            PlayerListEntry var5 = mc.getNetworkHandler().getPlayerListEntry(var2.getUuid());
            if (var5 == null) {
               return null;
            } else {
               int var4 = Math.max(0, var5.getLatency());
               if (var1.pingText == null || var1.intValue2 != var4) {
                  var1.intValue2 = var4;
                  var1.pingText = var4 + "ms";
               }

               return var1.pingText;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private void method606(CustomDrawContext var1, EntityESPModule.Entry var2) {
      if (var2 != null && var1 != null) {
         if (!var2.isFlag7() || !mc.options.getPerspective().isFirstPerson()) {
            try {
               Vector4d var3 = this.method616(var2);
               if (var3 == null) {
                  return;
               }

               this.method610(var1, var2.isFlag(), var3);
            } catch (Exception var4) {
               System.out.println("renderEntityBox err: " + var4.getMessage());
            }
         }
      }
   }

   private void method607(CustomDrawContext var1, EntityESPModule.Entry var2) {
      if (var2 != null && var1 != null && var2.getItemStack() != null && !var2.getItemStack().isEmpty()) {
         try {
            Vec3d[] var3 = this.method612(var2.getBox());
            Vector4d var4 = null;

            for (Vec3d var8 : var3) {
               try {
                  if (this.method614(var8)) {
                     Vec3d var9 = ProjectionUtils.method301(var8);
                     if (var9 != null && var9.z > 0.0) {
                        if (var4 == null) {
                           var4 = new Vector4d(var9.x, var9.y, var9.x, var9.y);
                        }

                        var4.x = Math.min(var4.x, var9.x);
                        var4.y = Math.min(var4.y, var9.y);
                        var4.z = Math.max(var4.z, var9.x);
                        var4.w = Math.max(var4.w, var9.y);
                     }
                  }
               } catch (Exception var11) {
               }
            }

            if (var4 == null) {
               return;
            }

            MutableText var13 = var2.getItemStack().getName().copy();
            if (var13.getString().endsWith(" ")) {
               var13 = TextUtils.method2116(var13, "");
            }

            if (var2.getItemStack().getCount() > 1) {
               var13 = var13.copy()
                  .append(
                     Text.of(" x" + var2.getItemStack().getCount())
                        .copy()
                        .setStyle(
                           Style.EMPTY.withColor(Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287())
                        )
                  );
            }

            float var14 = (float)var4.x;
            float var15 = (float)var4.z;
            float var16 = (float)var4.y;
            float var17 = this.method552(var2);
            float var10 = var16 - 2.0F * this.scaleSetting.getValue3() * var17;
            this.method609(var1, var14 + (var15 - var14) / 2.0F, var10, var2.getItemStack(), var13, var17);
         } catch (Exception var12) {
            System.out.println("renderItemTarget err: " + var12.getMessage());
         }
      }
   }

   public static float method608() {
      return Math.max(Fonts.NEW_MEDIUM.getFont(6.5F).height(), 10.4F) + GuiStyle.PADDING.intValue();
   }

   public void method609(CustomDrawContext var1, float var2, float var3, ItemStack var4, Text var5, float var6) {
      if (var1 != null && var5 != null) {
         ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         Font var8 = Fonts.NEW_MEDIUM.getFont(6.5F);
         boolean var9 = var4 != null && !var4.isEmpty();
         float var10 = GuiStyle.PADDING.intValue();
         float var11 = method608();
         float var12 = var10 + (var9 ? 10.4F + var10 : 0.0F) + var8.width(var5) + var10;
         float var13 = var2 - var12 / 2.0F;
         float var14 = var3 - var11;
         if (!this.method570(var13, var14, var13 + var12, var3, var2, var3, var6)) {
            this.method569(var1, var2, var3, 1.0F, 1.0F, var6);
            ProxyDotRadiusData var15 = ProxyDotRadiusData.method8069(InterfaceModule.method6801() / 2.0F);
            var1.drawBlurHudBooleanCheck(
               var13,
               var14,
               var12,
               var11,
               22.0F,
               var15,
               ZenithColor.INSTANCE,
               this.blur.isEnabled() && this.blur.isVisible(),
               this.glow.isEnabled() && this.glow.isVisible()
            );
            var1.drawRoundedRect(var13, var14, var12, var11, var15, var7.getHudBackground().getZenithColor());
            var1.drawText(
               var8,
               var5,
               var13 + var10 + (var9 ? 10.4F + var10 : 0.0F),
               var14 + (var11 - var8.height()) / 2.0F,
               var7.getTextEnable().getZenithColor().method7287()
            );
            if (var9) {
               var1.pushMatrix();
               var1.getMatrices().translate(var13 + var10, var14 + (var11 - 10.4F) / 2.0F);
               var1.getMatrices().scale(0.65F, 0.65F);
               var1.drawItem(var4, 0, 0);
               var1.popMatrix();
            }

            this.pop(var1);
         }
      }
   }

   private void method610(CustomDrawContext var1, boolean var2, Vector4d var3) {
      if (var3 != null) {
         try {
            int[] var4 = var2
               ? new int[]{
                  Zenith.getInstance().getStyleManager().getCurrentStyle().getFriendColor().getZenithColor().method7287(),
                  Zenith.getInstance().getStyleManager().getCurrentStyle().getFriendColor().getZenithColor().method7287(),
                  Zenith.getInstance().getStyleManager().getCurrentStyle().getFriendColor().getZenithColor().method7287(),
                  Zenith.getInstance().getStyleManager().getCurrentStyle().getFriendColor().getZenithColor().method7287()
               }
               : new int[]{
                  Zenith.getInstance().getStyleManager().getClientColor(0).method7287(),
                  Zenith.getInstance().getStyleManager().getClientColor(90).method7287(),
                  Zenith.getInstance().getStyleManager().getClientColor(180).method7287(),
                  Zenith.getInstance().getStyleManager().getClientColor(270).method7287()
               };
            float var5 = (float)var3.x;
            float var6 = (float)var3.y;
            float var7 = (float)var3.z;
            float var8 = (float)var3.w;
            float var9 = (var7 - var5) / 3.0F;
            var1.drawRect(var5 - 1.0F, var6 - 1.0F, var9, 1.0F, this.method555(ZenithColor.method7291(var4[0])));
            var1.drawRect(var5 - 1.0F, var6, 1.0F, var9 + 1.0F, this.method555(ZenithColor.method7291(var4[0])));
            var1.drawRect(var5 - 1.0F, var8 - var9 - 1.0F, 1.0F, var9, this.method555(ZenithColor.method7291(var4[1])));
            var1.drawRect(var5 - 1.0F, var8 - 1.0F, var9, 1.0F, this.method555(ZenithColor.method7291(var4[1])));
            var1.drawRect(var7 - var9 + 2.0F, var6 - 1.0F, var9, 1.0F, this.method555(ZenithColor.method7291(var4[2])));
            var1.drawRect(var7 + 1.0F, var6, 1.0F, var9 + 1.0F, this.method555(ZenithColor.method7291(var4[2])));
            var1.drawRect(var7 + 1.0F, var8 - var9 - 1.0F, 1.0F, var9, this.method555(ZenithColor.method7291(var4[3])));
            var1.drawRect(var7 - var9 + 2.0F, var8 - 1.0F, var9, 1.0F, this.method555(ZenithColor.method7291(var4[3])));
         } catch (Exception var10) {
            System.out.println("drawFlatBox err: " + var10.getMessage());
         }
      }
   }

   private Vector4d method611(Box var1) {
      Vec3d[] var2 = this.method612(var1);
      Vector4d var3 = null;

      for (Vec3d var7 : var2) {
         try {
            if (this.method614(var7)) {
               Vec3d var8 = ProjectionUtils.method301(var7);
               if (var8 != null && var8.z > 0.0) {
                  if (var3 == null) {
                     var3 = new Vector4d(var8.x, var8.y, var8.x, var8.y);
                  }

                  var3.x = Math.min(var3.x, var8.x);
                  var3.y = Math.min(var3.y, var8.y);
                  var3.z = Math.max(var3.z, var8.x);
                  var3.w = Math.max(var3.w, var8.y);
               }
            }
         } catch (Exception var9) {
         }
      }

      return var3;
   }

   private Vec3d[] method612(Box var1) {
      try {
         return new Vec3d[]{
            new Vec3d(var1.minX, var1.minY, var1.minZ),
            new Vec3d(var1.minX, var1.maxY, var1.minZ),
            new Vec3d(var1.maxX, var1.minY, var1.minZ),
            new Vec3d(var1.maxX, var1.maxY, var1.minZ),
            new Vec3d(var1.minX, var1.minY, var1.maxZ),
            new Vec3d(var1.minX, var1.maxY, var1.maxZ),
            new Vec3d(var1.maxX, var1.minY, var1.maxZ),
            new Vec3d(var1.maxX, var1.maxY, var1.maxZ)
         };
      } catch (Exception var3) {
         System.out.println("getPoints err: " + var3.getMessage());
         return new Vec3d[8];
      }
   }

   private float method613(Box var1) {
      return (float)var1.getLengthY();
   }

   private boolean method614(Vec3d var1) {
      if (mc.gameRenderer != null && mc.gameRenderer.getCamera() != null) {
         Vec3d var2 = mc.gameRenderer.getCamera().getCameraPos();
         Vec3d var3 = this.vec3d;
         if (var3 == null) {
            var3 = this.method615();
         }

         return (var1.x - var2.x) * var3.x + (var1.y - var2.y) * var3.y + (var1.z - var2.z) * var3.z > 0.0;
      } else {
         return false;
      }
   }

   private Vec3d method615() {
      float var1 = mc.gameRenderer.getCamera().getPitch();
      float var2 = mc.gameRenderer.getCamera().getYaw();
      double var3 = Math.toRadians(var1);
      double var5 = Math.toRadians(var2);
      return new Vec3d(-Math.sin(var5) * Math.cos(var3), -Math.sin(var3), Math.cos(var5) * Math.cos(var3));
   }

   private Vector4d method616(EntityESPModule.Entry var1) {
      if (var1.intValue3 != this.intValue || var1.box2 != var1.getBox()) {
         var1.intValue3 = this.intValue;
         var1.box2 = var1.getBox();
         var1.vector4d = this.method611(var1.getBox());
      }

      return var1.vector4d;
   }

   private ZenithColor method617(float var1) {
      try {
         if (var1 <= 7.0F) {
            return new ZenithColor(255, 0, 0, 255);
         } else {
            return var1 <= 15.0F ? new ZenithColor(255, 255, 0, 255) : new ZenithColor(0, 255, 0, 255);
         }
      } catch (Exception var3) {
         return ZenithColor.INSTANCE;
      }
   }

   private String method618(int var1) {
      return String.format("%.1fs", Math.max(var1, 0) / 20.0F);
   }

   public void method619(CustomDrawContext var1, float var2, float var3, float var4, float var5, String var6, float var7, double var8, double var10) {
      if (var1 != null) {
         this.previewAlpha = Math.clamp(var7, 0.0F, 1.0F);
         this.doubleValue = var8;
         this.doubleValue2 = var10;
         if (!this.elements.method5226("module.entityESP.names")) {
            this.items4.clear();
            this.items6.clear();
            this.items7.clear();
            this.type = null;
         }

         try {
            float var12 = 1.8F * var4;
            float var13 = 0.3F * var4;
            float var14 = var3 - var12;
            Vector4d var15 = new Vector4d(var2 - var13, var14, var2 + var13, var3);
            if (this.elements.method5226("Tracers")) {
               this.method626(
                  var1, var2, var5, var2, var3 - var12 / 2.0F, 1.2F, this.method555(Zenith.getInstance().getStyleManager().getClientColor(0).method7294(0.9F))
               );
            }

            if (this.elements.method5226("module.entityESP.boxes")) {
               this.method625(var1, var2, var3, var4);
            }

            if (this.elements.method5226("module.entityESP.triangles")) {
               this.method610(var1, false, var15);
            }

            if (this.elements.method5226("module.entityESP.hands")) {
               this.method624(var1, var2, var3);
            }

            if (this.elements.method5226("module.entityESP.names")) {
               this.method621(var1, var2, var14 - this.method620(var4), var6);
            }
         } catch (Exception var19) {
            System.out.println("ESP preview error: " + var19.getMessage());
         } finally {
            this.previewAlpha = 1.0F;
         }
      }
   }

   public float method620(float var1) {
      return 0.15F * var1 + 1.0F;
   }

   private float method621(CustomDrawContext var1, float var2, float var3, String var4) {
      ZenithStyle var5 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.flag = true;

      float var6;
      try {
         var6 = this.method587(
            var1,
            var2,
            var3,
            Text.literal(var4),
            this.method623(),
            UseItemUtils.method6532(20.0F),
            this.method617(20.0F),
            this.elements.method5226("module.entityESP.ping") ? "12ms" : null,
            this.elements.method5226("module.entityESP.armor") ? this.method603(method602(this.method628())) : List.of(),
            var5.getHudBackground().getZenithColor(),
            1.0F,
            this.elements.method5226("module.entityESP.armorText") ? this.method622() : null
         );
      } finally {
         this.flag = false;
      }

      return var6;
   }

   private Text method622() {
      ZenithColor var1 = Zenith.getInstance().getStyleManager().getCurrentStyle().getTextSecondary().getZenithColor();
      return Text.literal("Diamond · Prot16").setStyle(Style.EMPTY.withColor(var1.method7287()));
   }

   private Identifier method623() {
      ClientPlayerEntity var2 = mc.player;
      return var2 instanceof AbstractClientPlayerEntity && var2.getSkin() != null
         ? var2.getSkin().body().texturePath()
         : DefaultSkinHelper.getSteve().body().texturePath();
   }

   private void method624(CustomDrawContext var1, float var2, float var3) {
      ZenithStyle var4 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      Font var5 = Fonts.NEW_MEDIUM.getFont(6.5F);
      double var6 = var3 - 10.0F;

      for (ItemStack var9 : this.method629()) {
         Text var10 = var9.getName();
         var6 += (var5.height() / 2.0F + 12.0F + 2.0F) * this.scaleSetting.getValue3();
         float var11 = var2 - var5.width(var10) / 2.0F;
         float var12 = var5.width(var10) + 8.0F;
         float var13 = var5.height() + GuiStyle.PADDING.intValue() + 1.0F;
         float var14 = var11 - 4.0F;
         float var15 = (float)var6;
         this.pushCenteredScale(var1, var14 + var12 / 2.0F, var15 + var13 / 2.0F, 1.0F, 1.0F);
         ProxyDotRadiusData var16 = ProxyDotRadiusData.method8069(InterfaceModule.method6801() * 0.6F);
         var1.drawBlurHudBooleanCheck(
            var14, var15, var12, var13, 22.0F, var16, this.method555(ZenithColor.INSTANCE), this.method627(), this.glow.isEnabled() && this.glow.isVisible()
         );
         var1.drawRoundedRect(var14, var15, var12, var13, var16, this.method555(var4.getHudBackground().getZenithColor()));
         var1.pushMatrix();
         var1.getMatrices().translate(var11, var15 + (var13 - 1.0F - var5.height()) / 2.0F);
         var1.drawText(var5, var10, 0.0F, 0.0F, this.method555(var4.getTextEnable().getZenithColor()).method7287());
         var1.popMatrix();
         this.pop(var1);
      }
   }

   private void method625(CustomDrawContext var1, float var2, float var3, float var4) {
      float var5 = (float)Math.toRadians(28.0);
      float var6 = (float)Math.toRadians(18.0);
      float var7 = (float)Math.cos(var5);
      float var8 = (float)Math.sin(var5);
      float var9 = (float)Math.cos(var6);
      float var10 = (float)Math.sin(var6);
      float var11 = 0.3F;
      float[][] var12 = new float[][]{
         {-var11, 0.0F, -var11},
         {var11, 0.0F, -var11},
         {0.3F, 0.0F, 0.3F},
         {-var11, 0.0F, var11},
         {-var11, 1.8F, -var11},
         {var11, 1.8F, -var11},
         {0.3F, 1.8F, 0.3F},
         {-var11, 1.8F, var11}
      };
      float[][] var13 = new float[8][2];

      for (int var14 = 0; var14 < 8; var14++) {
         float var15 = var12[var14][0] * var7 + var12[var14][2] * var8;
         float var16 = -var12[var14][0] * var8 + var12[var14][2] * var7;
         var13[var14][0] = var2 + var15 * var4;
         var13[var14][1] = var3 - (var12[var14][1] * var9 - var16 * var10) * var4;
      }

      ZenithColor var19 = this.method555(Zenith.getInstance().getStyleManager().getClientColor(0).method7294(0.7F));
      ZenithColor var20 = this.gradient.isEnabled() ? this.method555(Zenith.getInstance().getStyleManager().getClientColor(180).method7294(0.7F)) : var19;
      ZenithColor var21 = this.gradient.isEnabled() ? this.method555(Zenith.getInstance().getStyleManager().getClientColor(90).method7294(0.7F)) : var19;

      for (int var17 = 0; var17 < 4; var17++) {
         int var18 = (var17 + 1) % 4;
         this.method626(var1, var13[var17][0], var13[var17][1], var13[var18][0], var13[var18][1], 1.0F, var19);
         this.method626(var1, var13[var17 + 4][0], var13[var17 + 4][1], var13[var18 + 4][0], var13[var18 + 4][1], 1.0F, var20);
         this.method626(var1, var13[var17][0], var13[var17][1], var13[var17 + 4][0], var13[var17 + 4][1], 1.0F, var21);
      }
   }

   private void method626(CustomDrawContext var1, float var2, float var3, float var4, float var5, float var6, ZenithColor var7) {
      float var8 = var4 - var2;
      float var9 = var5 - var3;
      float var10 = (float)Math.hypot(var8, var9);
      if (!(var10 < 0.01F)) {
         var1.pushMatrix();
         var1.getMatrices().translate(var2, var3);
         var1.getMatrices().rotate((float)Math.atan2(var9, var8));
         var1.drawRect(0.0F, -var6 / 2.0F, var10, var6, var7);
         var1.popMatrix();
      }
   }

   private boolean method627() {
      return this.blur.isEnabled() && this.blur.isVisible();
   }

   private List<ItemStack> method628() {
      if (this.items.isEmpty()) {
         this.items.add(new ItemStack(Items.TOTEM_OF_UNDYING));
         this.items.add(new ItemStack(Items.DIAMOND_BOOTS));
         this.items.add(new ItemStack(Items.DIAMOND_LEGGINGS));
         this.items.add(new ItemStack(Items.DIAMOND_CHESTPLATE));
         this.items.add(new ItemStack(Items.DIAMOND_HELMET));
         this.items.add(new ItemStack(Items.DIAMOND_SWORD));
      }

      return this.items;
   }

   private List<ItemStack> method629() {
      if (this.items2.isEmpty()) {
         this.items2.add(new ItemStack(Items.DIAMOND_SWORD));
         this.items2.add(new ItemStack(Items.TOTEM_OF_UNDYING));
      }

      return this.items2;
   }

   @Generated
   public BooleanSetting getBlur() {
      return this.blur;
   }

   @Generated
   public BooleanSetting getGlow() {
      return this.glow;
   }

   private static final class Data {
      private final ItemStack itemStack;
      private final Text text;
      private final String text2;
      private final String text3;

      public ItemStack itemStack() {
         return this.itemStack;
      }

      public Text text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.itemStack, other.itemStack) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(itemStack, text, text2, text3);
      }

      @Override
      public String toString() {
         return "Data[" + "itemStack=" + this.itemStack + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + "]";
      }

      private Data(ItemStack var1, Text var2, String var3, String var4) {
         this.itemStack = var1;
         this.text = var2;
         this.text2 = var3;
         this.text3 = var4;
      }

      public ItemStack getItemStack() {
         return this.itemStack;
      }

      public Text name() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }
   }

   private static final class Data2 {
      private final Item item;
      private final Object obj;
      private final Object obj2;
      private final Object obj3;
      private final Object obj4;
      private final Object obj5;

      public Item item() {
         return this.item;
      }

      public Object obj() {
         return this.obj;
      }

      public Object obj2() {
         return this.obj2;
      }

      public Object obj3() {
         return this.obj3;
      }

      public Object obj4() {
         return this.obj4;
      }

      public Object obj5() {
         return this.obj5;
      }

      @Override
      public String toString() {
         return "Data2[" + "item=" + this.item + ", " + "obj=" + this.obj + ", " + "obj2=" + this.obj2 + ", " + "obj3=" + this.obj3 + ", " + "obj4=" + this.obj4 + ", " + "obj5=" + this.obj5 + "]";
      }

      private Data2(Item var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
         this.item = var1;
         this.obj = var2;
         this.obj2 = var3;
         this.obj3 = var4;
         this.obj4 = var5;
         this.obj5 = var6;
      }

      public static EntityESPModule.Data2 method630(ItemStack var0) {
         return new EntityESPModule.Data2(
            var0.getItem(),
            var0.get(DataComponentTypes.CUSTOM_DATA),
            var0.get(DataComponentTypes.ENCHANTMENTS),
            var0.get(DataComponentTypes.CUSTOM_NAME),
            var0.get(DataComponentTypes.ITEM_MODEL),
            var0.get(DataComponentTypes.CUSTOM_MODEL_DATA)
         );
      }

      @Override
      public boolean equals(Object var1) {
         return var1 instanceof EntityESPModule.Data2 var2
            && var2.item == this.item
            && var2.obj == this.obj
            && var2.obj2 == this.obj2
            && var2.obj3 == this.obj3
            && var2.obj4 == this.obj4
            && var2.obj5 == this.obj5;
      }

      @Override
      public int hashCode() {
         int var1 = System.identityHashCode(this.item);
         var1 = 31 * var1 + System.identityHashCode(this.obj);
         var1 = 31 * var1 + System.identityHashCode(this.obj2);
         var1 = 31 * var1 + System.identityHashCode(this.obj3);
         var1 = 31 * var1 + System.identityHashCode(this.obj4);
         return 31 * var1 + System.identityHashCode(this.obj5);
      }

      public Item getItem() {
         return this.item;
      }

      public Object getObj() {
         return this.obj;
      }

      public Object getObj2() {
         return this.obj2;
      }

      public Object getObj3() {
         return this.obj3;
      }

      public Object getObj4() {
         return this.obj4;
      }

      public Object getObj5() {
         return this.obj5;
      }
   }

   private static final class Data3 {
      private final String text;
      private final String text2;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2);
      }

      @Override
      public String toString() {
         return "Data3[" + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      private Data3(String var1, String var2) {
         this.text = var1;
         this.text2 = var2;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }
   }

   private static final class Data4 {
      private final EntityESPModule.Entry entry;
      private final long longValue;

      public EntityESPModule.Entry entry() {
         return this.entry;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.entry, other.entry) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(entry, longValue);
      }

      @Override
      public String toString() {
         return "Data4[" + "entry=" + this.entry + ", " + "longValue=" + this.longValue + "]";
      }

      private Data4(EntityESPModule.Entry var1, long var2) {
         this.entry = var1;
         this.longValue = var2;
      }

      public EntityESPModule.Entry getEntry() {
         return this.entry;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Entry {
      private final String text;
      private final Text text2;
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;
      private final boolean flag4;
      private final boolean flag5;
      private final boolean flag6;
      private final boolean flag7;
      private final float floatValue;
      private final int intValue;
      private final List<ItemStack> items;
      private final List<ItemStack> items2;
      private final ItemStack itemStack;
      private final Entity entity;
      private final PartyPlayerInfo partyPlayerInfo;
      private Box box;
      private EntityESPModule.Data[] datas;
      private boolean flag8;
      private String text3;
      private boolean flag9;
      private boolean flag10;
      private String text4;
      private int intValue2;
      private String pingText;
      private int intValue3 = -1;
      private Box box2;
      private Vector4d vector4d;

      private Entry(
         String var1,
         Text var2,
         boolean var3,
         boolean var4,
         boolean var5,
         boolean var6,
         boolean var7,
         boolean var8,
         boolean var9,
         Box var10,
         float var11,
         int var12,
         List<ItemStack> var13,
         List<ItemStack> var14,
         ItemStack var15,
         Entity var16,
         PartyPlayerInfo var17
      ) {
         this.text = var1;
         this.text2 = var2;
         this.flag = var3;
         this.flag2 = var4;
         this.flag3 = var5;
         this.flag4 = var6;
         this.flag5 = var7;
         this.flag6 = var8;
         this.flag7 = var9;
         this.box = var10;
         this.floatValue = var11;
         this.intValue = var12;
         this.items = var13;
         this.items2 = var14;
         this.itemStack = var15;
         this.entity = var16;
         this.partyPlayerInfo = var17;
      }

      private void method631() {
         if (this.entity != null) {
            if (!this.entity.isRemoved()) {
               Vec3d var2 = MathUtils.method2656(this.entity);
               this.box = this.entity.getBoundingBox().offset(var2.subtract(this.entity.getEntityPos()));
            }
         } else {
            if (this.partyPlayerInfo != null) {
               Vec3d var1 = this.partyPlayerInfo.method3294();
               if (var1 == null) {
                  return;
               }

               this.box = new Box(var1.x - 0.3, var1.y, var1.z - 0.3, var1.x + 0.3, var1.y + 1.8, var1.z + 0.3);
            }
         }
      }

      private String key() {
         return this.text;
      }

      private Text name() {
         return this.text2;
      }

      private boolean isFlag() {
         return this.flag;
      }

      private boolean isFlag2() {
         return this.flag2;
      }

      private boolean isFlag3() {
         return this.flag3;
      }

      private boolean isFlag4() {
         return this.flag4;
      }

      private boolean isFlag5() {
         return this.flag5;
      }

      private boolean isFlag6() {
         return this.flag6;
      }

      private boolean isFlag7() {
         return this.flag7;
      }

      private Box getBox() {
         return this.box;
      }

      private float getFloatValue() {
         return this.floatValue;
      }

      private int getIntValue() {
         return this.intValue;
      }

      private List<ItemStack> getItems() {
         return this.items;
      }

      private List<ItemStack> getItems2() {
         return this.items2;
      }

      private ItemStack getItemStack() {
         return this.itemStack;
      }

      private Entity getEntity() {
         return this.entity;
      }
   }

   public static enum Type {
      HP,
      PING,
      GEAR;
   }
}
