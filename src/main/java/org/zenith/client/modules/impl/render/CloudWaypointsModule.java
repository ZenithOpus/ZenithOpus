package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.KeyEvent;
import org.zenith.MatrixUtils;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.CloudWaypointEvent;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "CloudWaypoints",
   category = ModuleCategory.RENDER,
   description = "module.cloudwaypoints.description"
)
public final class CloudWaypointsModule extends Module {
   public static final CloudWaypointsModule INSTANCE = new CloudWaypointsModule();
   private static final float CONST_floatValue = 3000.0F;
   private static final long CONST_longValue = 280L;
   private static final int CONST_intValue = 64;
   private static final int CONST_intValue2 = 256;
   private final BindSetting markBind = new BindSetting("module.cloudWaypoints.markBind", "module.cloudWaypoints.markBind.desc", -1);
   private final TextSetting label = new TextSetting("module.cloudWaypoints.label", "module.cloudWaypoints.label.desc", "", "module.cloudWaypoints.label.empty");
   private final SliderSetting ttl = new SliderSetting("module.cloudWaypoints.ttl", 30.0F, 5.0F, 300.0F, 5.0F, "module.cloudWaypoints.ttl.desc", " с");
   private final SliderSetting range = new SliderSetting("module.cloudWaypoints.range", 120.0F, 10.0F, 400.0F, 10.0F, "module.cloudWaypoints.range.desc", " м");
   private final ColorSetting colorSetting = new ColorSetting("module.cloudWaypoints.color", "module.cloudWaypoints.color.desc", new ZenithColor(-680437));
   private final SliderSetting markerScale = new SliderSetting(
      "module.cloudWaypoints.markerScale", 100.0F, 50.0F, 200.0F, 5.0F, "module.cloudWaypoints.markerScale.desc", " %"
   );
   private final BooleanSetting compass = new BooleanSetting("module.cloudWaypoints.compass", "module.cloudWaypoints.compass.desc", true);
   private final SliderSetting compassRadius = new SliderSetting(
      "module.cloudWaypoints.compassRadius", 38.0F, 16.0F, 120.0F, 2.0F, "module.cloudWaypoints.compassRadius.desc", " px"
   );
   private final BooleanSetting follow = new BooleanSetting("module.cloudWaypoints.follow", "module.cloudWaypoints.follow.desc", true);
   private final BooleanSetting showOwn = new BooleanSetting("module.cloudWaypoints.showOwn", "module.cloudWaypoints.showOwn.desc", true);
   private final BooleanSetting sound = new BooleanSetting("module.cloudWaypoints.sound", "module.cloudWaypoints.sound.desc", true);
   private final List<CloudWaypointsModule.Entry> items = new ArrayList<>();
   private static final long CONST_longValue2 = 50000000L;

   private CloudWaypointsModule() {
   }

   public BindSetting getMarkBind() {
      return this.markBind;
   }

   private static Cloud cloud() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getCloud();
   }

   @Override
   public void onDisable() {
      this.items.clear();
      super.onDisable();
   }

   @EventTarget
   public void method6400(KeyEvent var1) {
      if (this.markBind.getKeyCode() > 0 && var1.isPress(this.markBind.getKeyCode())) {
         this.method6401();
      }
   }

   private void method6401() {
      Cloud var1 = cloud();
      if (var1 != null && var1.method7862() && mc.player != null && mc.world != null) {
         double var2 = this.range.getValue3();
         Vec3d var4 = mc.player.getEyePos();
         Rotation var5 = new Rotation(mc.player.getYaw(), mc.player.getPitch());
         BlockHitResult var6 = TargetUtils.method3205(var4, var2, var5, false);
         double var7 = var6 != null && var6.getType() != Type.MISS ? var4.distanceTo(var6.getPos()) : var2;
         EntityHitResult var9 = TargetUtils.method3210(var7, var5, var0 -> var0 != mc.player && var0.isAlive() && !var0.isInvisible());
         String var11 = "";
         Vec3d var10;
         if (var9 != null) {
            var10 = var9.getEntity().getBoundingBox().getCenter();
            var11 = var9.getEntity().getUuidAsString();
         } else if (var6 != null && var6.getType() != Type.MISS) {
            var10 = var6.getPos();
         } else {
            var10 = var4.add(var5.method3135().multiply(var2));
         }

         String var12 = this.label.getValue() == null ? "" : this.label.getValue().strip();
         var1.method7910(var10.x, var10.y, var10.z, var12, this.colorSetting.method1445(), var11);
      }
   }

   public String method6402(double var1, double var3, double var5, String var7, String var8, String var9, String var10) {
      Cloud var11 = cloud();
      if (var11 == null) {
         return "gui.chat.mark.noCloud";
      } else if (mc.world == null || mc.player == null) {
         return "gui.chat.mark.notInWorld";
      } else if (!method6407(var1, var3, var5)) {
         return "gui.chat.attachBroken";
      } else if (!var9.isEmpty() && !method6403(var9, var11.method7911())) {
         return "gui.chat.mark.otherServer";
      } else if (!var10.isEmpty() && !method6405(var10, var11.method7913())) {
         return "gui.chat.mark.otherDimension";
      } else {
         this.setToggled(true);
         long var12 = System.currentTimeMillis();
         CloudPackets.Data10 var14 = new CloudPackets.Data10(
            0L, var7, var11.method7911(), var11.method7913(), var1, var3, var5, var8, this.colorSetting.method1445(), ""
         );
         this.method6409(new CloudWaypointsModule.Entry(var14, var12, var12 + (long)this.ttl.getValue3() * 1000L));
         if (this.sound.isEnabled()) {
            mc.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 0.6F, 1.6F);
         }

         return "";
      }
   }

   private static boolean method6403(String var0, String var1) {
      return var0.equalsIgnoreCase(var1) || var0.startsWith("singleplayer") && var1.startsWith("singleplayer");
   }

   private static boolean method6404(String var0, String var1) {
      return !var1.isEmpty() && var0.equalsIgnoreCase(var1);
   }

   private static boolean method6405(String var0, String var1) {
      return method6406(var0).equalsIgnoreCase(method6406(var1));
   }

   private static String method6406(String var0) {
      int var1 = var0.indexOf(58);
      return var1 < 0 ? var0 : var0.substring(var1 + 1);
   }

   private static boolean method6407(double var0, double var2, double var4) {
      return Double.isFinite(var0)
         && Double.isFinite(var2)
         && Double.isFinite(var4)
         && Math.abs(var0) <= 3.0E7
         && Math.abs(var4) <= 3.0E7
         && Math.abs(var2) <= 100000.0;
   }

   @EventTarget
   public void method6408(CloudWaypointEvent var1) {
      if (!var1.isFlag() || this.showOwn.isEnabled()) {
         CloudPackets.Data10 var2 = var1.getData10();
         Cloud var3 = cloud();
         if (var3 != null && method6407(var2.getDoubleValue(), var2.getDoubleValue2(), var2.getDoubleValue3())) {
            if (!var3.prefs().method3316(var2.username())) {
               if (method6404(var2.server(), var3.method7911()) && method6405(var2.dimension(), var3.method7913())) {
                  long var4 = System.currentTimeMillis();
                  long var6 = var4 + (long)this.ttl.getValue3() * 1000L;

                  for (CloudWaypointsModule.Entry var9 : this.items) {
                     if (var9.method6414(var2)) {
                        var9.longValue2 = var6;
                        return;
                     }
                  }

                  this.method6409(new CloudWaypointsModule.Entry(var2, var4, var6));
                  if (this.sound.isEnabled() && mc.player != null) {
                     mc.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 0.6F, var1.isFlag() ? 1.8F : 1.4F);
                  }
               }
            }
         }
      }
   }

   private void method6409(CloudWaypointsModule.Entry var1) {
      String var2 = var1.data10.username();
      int var3 = 0;
      int var4 = -1;

      for (int var5 = 0; var5 < this.items.size(); var5++) {
         if (this.items.get(var5).data10.username().equalsIgnoreCase(var2)) {
            if (var4 < 0) {
               var4 = var5;
            }

            var3++;
         }
      }

      if (var3 >= 64 && var4 >= 0) {
         this.items.remove(var4);
      }

      while (this.items.size() >= 256) {
         this.items.remove(0);
      }

      this.items.add(var1);
   }

   @EventTarget
   public void method6410(DrawEvent var1) {
      if (!this.items.isEmpty() && mc.world != null && mc.player != null) {
         Cloud var2 = cloud();
         if (var2 != null) {
            long var3 = System.currentTimeMillis();
            String var5 = var2.method7911();
            String var6 = var2.method7913();
            CustomDrawContext var7 = var1.getCustomDrawContext();
            ZenithStyle var8 = Zenith.getInstance().getStyleManager().getCurrentStyle();
            if (var8 != null) {
               Vec3d var9 = mc.getEntityRenderDispatcher().camera.getCameraPos();
               float var10 = this.markerScale.getValue3() / 100.0F;
               float var11 = mc.getWindow().getScaledWidth() / 2.0F;
               float var12 = mc.getWindow().getScaledHeight() / 2.0F;

               for (int var13 = this.items.size() - 1; var13 >= 0; var13--) {
                  CloudWaypointsModule.Entry var14 = this.items.get(var13);
                  if (var14.longValue2 <= var3) {
                     this.items.remove(var13);
                  } else {
                     CloudPackets.Data10 var15 = var14.data10;
                     boolean var16 = var15.server().equals(var5) || method6404(var15.server(), var5);
                     if (var16 && method6405(var15.dimension(), var6)) {
                        Vec3d var17 = this.follow.isEnabled() ? this.method6413(var14, var1.getTickDelta()) : null;
                        Vec3d var18 = var17 == null ? new Vec3d(var15.getDoubleValue(), var15.getDoubleValue2(), var15.getDoubleValue3()) : var17;
                        float var19 = Math.min(1.0F, (float)(var14.longValue2 - var3) / 3000.0F);
                        float var20 = Math.min(1.0F, (float)(var3 - var14.longValue) / 280.0F);
                        double var21 = var9.distanceTo(var18);
                        String var23 = var14.text;
                        String var24 = var14.meters(var21);
                        ZenithColor var25 = new ZenithColor(var15.getIntValue()).method7294(var19);
                        if (ProjectionUtils.method302(var18)) {
                           Vec3d var26 = ProjectionUtils.method301(var18);
                           this.method6411(var7, var8, (float)var26.x, (float)var26.y, var23, var24, var25, var21, var10, var19, var20);
                        } else if (this.compass.isEnabled()) {
                           this.method6412(var7, var8, var11, var12, var18, var9, var24, var25, var10);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void method6411(
      CustomDrawContext var1,
      ZenithStyle var2,
      float var3,
      float var4,
      String var5,
      String var6,
      ZenithColor var7,
      double var8,
      float var10,
      float var11,
      float var12
   ) {
      Font var13 = Fonts.NEW_MEDIUM.getFont(7.0F);
      float var14 = (1.0F - var12) * 6.0F;
      float var15 = var11 * (0.25F + 0.75F * var12);
      float var16 = MathHelper.clamp((float)((var8 - 6.0) / 140.0), 0.0F, 1.0F);
      float var17 = (1.0F - 0.18F * var16) * var10;
      float var18 = GuiStyle.PADDING.intValue();
      float var19 = 3.0F;
      float var20 = var13.height() + var18;
      float var21 = var18 + var19 + 3.0F + var13.width(var5) + var18;
      float var22 = var18 + var13.width(var6) + var18;
      float var23 = var21 + var22;
      float var24 = 10.0F * var17;
      float var25 = var3 - var23 / 2.0F;
      float var26 = var4 - var24 - var20 + var14;
      var1.pushMatrix();
      var1.getMatrices().translate(var3, var26 + var20 / 2.0F);
      var1.getMatrices().scale(var17, var17);
      var1.getMatrices().translate(-var3, -(var26 + var20 / 2.0F));
      ProxyDotRadiusData var27 = ProxyDotRadiusData.method8069(InterfaceModule.method6801() * 0.6F);
      RenderShaders.method5287(
         MatrixUtils.method664(var1.getMatrices()),
         var25,
         var26,
         var23,
         var20,
         22.0F,
         var27,
         ZenithColor.INSTANCE,
         EntityESPModule.INSTANCE.getBlur().isEnabled() && EntityESPModule.INSTANCE.getBlur().isVisible(),
         EntityESPModule.INSTANCE.getGlow().isEnabled() && EntityESPModule.INSTANCE.getGlow().isVisible()
      );
      var1.drawRoundedRect(var25, var26, var23, var20, var27, var2.getHudBackground().getZenithColor().method7294(var15));
      var1.drawRoundedRect(var25, var26, var21, var20, var27, var2.getHudBackground().getZenithColor().method7294(var15));
      float var28 = var26 + (var20 - var13.height()) / 2.0F;
      var1.drawRoundedRect(var25 + var18, var26 + (var20 - var19) / 2.0F, var19, var19, ProxyDotRadiusData.method8069(var19 / 2.0F), var7.method7294(var15));
      var1.drawText(var13, var5, var25 + var18 + var19 + 3.0F, var28, var2.getTextEnable().getZenithColor().method7294(var15));
      var1.drawText(var13, var6, var25 + var21 + var18, var28, var2.getTextTertiary().getZenithColor().method7294(var15));
      var1.drawRect(var3 - 0.5F, var26 + var20, 1.0F, var24 - 2.0F, var2.getFieldBorder().getZenithColor().method7294(var15 * 2.0F));
      float var29 = 3.0F;
      var1.drawRoundedRect(var3 - var29 / 2.0F, var4 - var29 / 2.0F + var14, var29, var29, ProxyDotRadiusData.method8069(var29 / 2.0F), var7.method7294(var15));
      var1.popMatrix();
   }

   private void method6412(CustomDrawContext var1, ZenithStyle var2, float var3, float var4, Vec3d var5, Vec3d var6, String var7, ZenithColor var8, float var9) {
      double var10 = var5.x - var6.x;
      double var12 = var5.z - var6.z;
      float var14 = (float)Math.toDegrees(Math.atan2(var12, var10)) - 90.0F;
      float var15 = MathHelper.wrapDegrees(var14 - mc.getEntityRenderDispatcher().camera.getYaw());
      double var16 = Math.toRadians(var15);
      float var18 = this.compassRadius.getValue3();
      float var19 = var3 + (float)Math.sin(var16) * var18;
      float var20 = var4 - (float)Math.cos(var16) * var18;
      Font var21 = Fonts.NEW_REGULAR.getFont(6.0F);
      float var22 = var8.getIntValue4() / 255.0F;
      float var23 = GuiStyle.PADDING.intValue() * 0.75F;
      float var24 = 3.0F;
      float var25 = var21.height() + var23;
      float var26 = var23 + var24 + 3.0F + var21.width(var7) + var23;
      float var27 = var19 - var26 / 2.0F;
      float var28 = var20 - var25 / 2.0F;
      var1.drawRoundedRect(var27, var28, var26, var25, ProxyDotRadiusData.method8069(var25 / 2.0F), var2.getHudBackground().getZenithColor().method7294(var22));
      var1.drawRoundedRect(var27 + var23, var28 + (var25 - var24) / 2.0F, var24, var24, ProxyDotRadiusData.method8069(var24 / 2.0F), var8);
      var1.drawText(
         var21, var7, var27 + var23 + var24 + 3.0F, var28 + (var25 - var21.height()) / 2.0F, var2.getTextSecondary().getZenithColor().method7294(var22)
      );
   }

   private Vec3d method6413(CloudWaypointsModule.Entry var1, float var2) {
      String var3 = var1.data10.getText5();
      if (var3.isEmpty()) {
         return null;
      } else {
         if (var1.entity != null && (var1.entity.isRemoved() || var1.entity.getEntityWorld() != mc.world)) {
            var1.entity = null;
         }

         if (var1.entity == null) {
            if (!var1.flag) {
               var1.flag = true;

               try {
                  var1.uUID = UUID.fromString(var3);
               } catch (IllegalArgumentException var9) {
                  var1.uUID = null;
               }
            }

            UUID var4 = var1.uUID;
            if (var4 == null) {
               return null;
            }

            long var5 = System.nanoTime();
            if (var1.longValue3 != 0L && var5 - var1.longValue3 < 50000000L) {
               return null;
            }

            var1.longValue3 = var5;

            for (Entity var8 : mc.world.getEntities()) {
               if (var8.getUuid().equals(var4)) {
                  var1.entity = var8;
                  break;
               }
            }
         }

         return var1.entity == null ? null : var1.entity.getLerpedPos(var2).add(0.0, var1.entity.getBoundingBox().getLengthY() * 0.5, 0.0);
      }
   }

   private static final class Entry {
      private final CloudPackets.Data10 data10;
      private final long longValue;
      private long longValue2;
      private Entity entity;
      private UUID uUID;
      private boolean flag;
      private long longValue3;
      private final String text;
      private long longValue4 = Long.MIN_VALUE;
      private String text2 = "";

      private Entry(CloudPackets.Data10 var1, long var2, long var4) {
         this.data10 = var1;
         this.longValue = var2;
         this.longValue2 = var4;
         this.text = var1.username() + (var1.label().isEmpty() ? "" : ": " + var1.label());
      }

      private String meters(double var1) {
         long var3 = Math.round(var1);
         if (var3 != this.longValue4) {
            this.longValue4 = var3;
            this.text2 = var3 + " м";
         }

         return this.text2;
      }

      private boolean method6414(CloudPackets.Data10 var1) {
         return this.data10.username().equalsIgnoreCase(var1.username())
            && Math.floor(this.data10.getDoubleValue()) == Math.floor(var1.getDoubleValue())
            && Math.floor(this.data10.getDoubleValue2()) == Math.floor(var1.getDoubleValue2())
            && Math.floor(this.data10.getDoubleValue3()) == Math.floor(var1.getDoubleValue3());
      }
   }
}
