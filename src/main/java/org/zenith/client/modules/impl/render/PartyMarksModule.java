package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "PartyMarks",
   category = ModuleCategory.RENDER,
   description = "module.partymarks.description"
)
public final class PartyMarksModule extends Module {
   public static final PartyMarksModule INSTANCE = new PartyMarksModule();
   public static final List<PartyMarksModule.Data2> items = List.of(
      new PartyMarksModule.Data2("base", MenuGlyph.STORAGE, -10443270),
      new PartyMarksModule.Data2("spawner", MenuGlyph.PARTICLES, -757066),
      new PartyMarksModule.Data2("mine", MenuGlyph.CRAFT, -6052957),
      new PartyMarksModule.Data2("farm", MenuGlyph.HAND, -11870592),
      new PartyMarksModule.Data2("portal", MenuGlyph.WORLD, -5796870),
      new PartyMarksModule.Data2("loot", MenuGlyph.CHESTPLATE, -278748),
      new PartyMarksModule.Data2("danger", MenuGlyph.CRIT, -495247),
      new PartyMarksModule.Data2("pin", MenuGlyph.PIN, -13058568)
   );
   private static final ZenithColor zenithColor = new ZenithColor(-1096636);
   private final BooleanSetting showMarks = new BooleanSetting("module.partyMarks.showMarks", "module.partyMarks.showMarks.desc", true);
   private final BooleanSetting showDeaths = new BooleanSetting("module.partyMarks.showDeaths", "module.partyMarks.showDeaths.desc", true);
   private final SliderSetting deathMinutes = new SliderSetting(
      "module.partyMarks.deathMinutes", 30.0F, 0.0F, 240.0F, 5.0F, "module.partyMarks.deathMinutes.desc", " min", null, null
   );
   private final SliderSetting maxDistance = new SliderSetting(
      "module.partyMarks.maxDistance", 2000.0F, 50.0F, 10000.0F, 50.0F, "module.partyMarks.maxDistance.desc", " m", null, null
   );
   private final SliderSetting scale = new SliderSetting(
      "module.partyMarks.scale", 100.0F, 50.0F, 200.0F, 5.0F, "module.partyMarks.scale.desc", " %", null, null
   );
   private final BooleanSetting compass = new BooleanSetting("module.partyMarks.compass", "module.partyMarks.compass.desc", true);
   private final SliderSetting compassRadius = new SliderSetting(
      "module.partyMarks.compassRadius", 46.0F, 16.0F, 140.0F, 2.0F, "module.partyMarks.compassRadius.desc", " px", null, null
   );
   public final BindSetting markKey = new BindSetting("module.partyMarks.markKey", "module.partyMarks.markKey.desc", -1);
   public final TextSetting markName = new TextSetting(
      "module.partyMarks.markName", "module.partyMarks.markName.desc", "", "module.partyMarks.markName.empty", TextSetting.Entry.method2125(32)
   );
   private volatile long longValue;
   private volatile long longValue2;
   private long longValue3;

   private PartyMarksModule() {
   }

   public static PartyMarksModule.Data2 method5419(String var0) {
      for (PartyMarksModule.Data2 var2 : items) {
         if (var2.key().equals(var0)) {
            return var2;
         }
      }

      return items.get(items.size() - 1);
   }

   public void method5420(long var1) {
      this.longValue = var1;
      this.longValue2 = 0L;
      this.setToggled(true);
   }

   public void method5421(long var1) {
      this.longValue2 = var1;
      this.longValue = 0L;
      this.setToggled(true);
   }

   public long getLongValue5() {
      return this.longValue;
   }

   public long getLongValue22() {
      return this.longValue2;
   }

   public void clearFocus() {
      this.longValue = 0L;
      this.longValue2 = 0L;
   }

   private static Cloud cloud() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getCloud();
   }

   @EventTarget
   public void method5422(KeyEvent var1) {
      if (this.markKey.getKeyCode() > 0 && var1.isPress(this.markKey.getKeyCode())) {
         this.method5424();
      } else {
         int var2 = CloudWaypointsModule.INSTANCE.getMarkBind().getKeyCode();
         if (var2 > 0 && var1.isPress(var2)) {
            long var3 = System.currentTimeMillis();
            if (var3 - this.longValue3 < 350L) {
               this.longValue3 = 0L;
               this.method5424();
            } else {
               this.longValue3 = var3;
            }
         }
      }
   }

   public PartyMarksModule.Data method5423(BlockHitResult var1) {
      if (var1 != null && var1.getType() != Type.MISS && mc.world != null) {
         Block var2 = mc.world.getBlockState(var1.getBlockPos()).getBlock();
         String var3 = Registries.BLOCK.getId(var2).getPath();
         String var4;
         if (var3.contains("spawner")) {
            var4 = "spawner";
         } else if (var3.contains("chest") || var3.equals("barrel") || var3.contains("shulker_box") || var3.equals("hopper")) {
            var4 = "loot";
         } else if (var3.contains("portal") || var3.equals("end_gateway") || var3.contains("obsidian")) {
            var4 = "portal";
         } else if (var3.endsWith("_ore") || var3.equals("ancient_debris") || var3.contains("amethyst")) {
            var4 = "mine";
         } else if (var3.equals("farmland")
            || var3.equals("wheat")
            || var3.equals("carrots")
            || var3.equals("potatoes")
            || var3.equals("beetroots")
            || var3.contains("melon")
            || var3.contains("pumpkin")
            || var3.equals("sugar_cane")
            || var3.equals("nether_wart")
            || var3.equals("cactus")
            || var3.equals("bamboo")) {
            var4 = "farm";
         } else if (var3.endsWith("_bed")
            || var3.equals("crafting_table")
            || var3.contains("furnace")
            || var3.equals("enchanting_table")
            || var3.equals("anvil")
            || var3.equals("respawn_anchor")) {
            var4 = "base";
         } else if (!var3.equals("lava") && !var3.equals("magma_block") && !var3.equals("tnt") && !var3.contains("sculk")) {
            var4 = "pin";
         } else {
            var4 = "danger";
         }

         String var5 = var4.equals("pin") ? var2.getName().getString() : method5431("cloud.mark.icon." + var4);
         return new PartyMarksModule.Data(var4, var5.length() > 32 ? var5.substring(0, 32) : var5);
      } else {
         return new PartyMarksModule.Data("pin", method5431("cloud.mark.icon.pin"));
      }
   }

   public boolean method5424() {
      Cloud var1 = cloud();
      if (var1 != null && mc.player != null && mc.world != null && var1.getPartyMember().method1136()) {
         Vec3d var2 = mc.player.getEyePos();
         Rotation var3 = new Rotation(mc.player.getYaw(), mc.player.getPitch());
         BlockHitResult var4 = TargetUtils.method3205(var2, 256.0, var3, false);
         Vec3d var5 = var4 != null && var4.getType() != Type.MISS ? Vec3d.ofBottomCenter(var4.getBlockPos().up()) : mc.player.getEntityPos();
         PartyMarksModule.Data var6 = this.method5423(var4);
         PartyMarksModule.Data2 var7 = method5419(var6.getText());
         String var8 = this.markName.getValue() != null && !this.markName.getValue().isBlank() ? this.markName.getValue().strip() : var6.name();
         var1.getPartyMember().method1160(0L, var1.method7911(), var1.method7913(), var5.x, var5.y, var5.z, var8, var7.key(), var7.color());
         if (mc.player != null) {
            mc.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), 0.6F, 1.4F);
         }

         return true;
      } else {
         return false;
      }
   }

   @EventTarget
   public void method5425(DrawEvent var1) {
      Cloud var2 = cloud();
      if (var2 != null && mc.world != null && mc.player != null) {
         ZenithStyle var3 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var3 != null) {
            CustomDrawContext var4 = var1.getCustomDrawContext();
            MenuDrawContext var5 = MenuDrawContext.method8080(var4, 0, 0, var1.getTickDelta());
            String var6 = var2.method7911();
            String var7 = var2.method7913();
            Vec3d var8 = mc.getEntityRenderDispatcher().camera.getCameraPos();
            float var9 = this.scale.getValue3() / 100.0F;
            double var10 = this.maxDistance.getValue3();
            PartyMember var12 = var2.getPartyMember();
            if (this.showMarks.isEnabled()) {
               for (PartyMember.Data12 var14 : var12.method1138()) {
                  if (var14.server().equals(var6)
                     && var14.dimension().equals(var7)
                     && method5429(var14.getDoubleValue(), var14.getDoubleValue2(), var14.getDoubleValue3())) {
                     Vec3d var15 = new Vec3d(var14.getDoubleValue(), var14.getDoubleValue2() + 0.5, var14.getDoubleValue3());
                     double var16 = var8.distanceTo(var15);
                     boolean var18 = var14.getLongValue() == this.longValue;
                     if (!(var16 > var10) || var18) {
                        PartyMarksModule.Data2 var19 = method5419(var14.getText4());
                        ZenithColor var20 = new ZenithColor(var14.color() == -1 ? var19.color() : var14.color());
                        this.method5426(var5, var3, var15, var8, var19.glyph(), method5430(var14.name()), meters(var16), var20, var16, var9, var18);
                     }
                  }
               }
            }

            if (this.showDeaths.isEnabled()) {
               long var22 = this.deathMinutes.getValue3() <= 0.0F ? 0L : System.currentTimeMillis() - (long)this.deathMinutes.getValue3() * 60000L;

               for (PartyMember.Data4 var24 : var12.method1139()) {
                  boolean var17 = var24.getLongValue() == this.longValue2;
                  if (var24.server().equals(var6)
                     && var24.dimension().equals(var7)
                     && (var24.time() >= var22 || var17)
                     && method5429(var24.getDoubleValue(), var24.getDoubleValue2(), var24.getDoubleValue3())) {
                     Vec3d var25 = new Vec3d(var24.getDoubleValue(), var24.getDoubleValue2() + 0.5, var24.getDoubleValue3());
                     double var26 = var8.distanceTo(var25);
                     if (!(var26 > var10) || var17) {
                        String var21 = (var24.uid() == var2.getLongValue() ? method5431("cloud.death.mine") : method5430(var24.name()))
                           + " · "
                           + var24.getItems().size();
                        this.method5426(
                           var5,
                           var3,
                           var25,
                           var8,
                           MenuGlyph.CLOSE,
                           var21,
                           meters(var26),
                           zenithColor,
                           var26,
                           var9,
                           var17 || var24.uid() == var2.getLongValue() && System.currentTimeMillis() - var24.time() < 600000L
                        );
                     }
                  }
               }
            }
         }
      }
   }

   private void method5426(
      MenuDrawContext var1,
      ZenithStyle var2,
      Vec3d var3,
      Vec3d var4,
      MenuGlyph var5,
      String var6,
      String var7,
      ZenithColor var8,
      double var9,
      float var11,
      boolean var12
   ) {
      if (ProjectionUtils.method302(var3)) {
         Vec3d var13 = ProjectionUtils.method301(var3);
         this.method5427(var1, var2, (float)var13.x, (float)var13.y, var5, var6, var7, var8, var9, var11);
      } else if (var12 && this.compass.isEnabled()) {
         this.method5428(var1, var2, var3, var4, var5, var7, var8, var11);
      }
   }

   private void method5427(
      MenuDrawContext var1, ZenithStyle var2, float var3, float var4, MenuGlyph var5, String var6, String var7, ZenithColor var8, double var9, float var11
   ) {
      Font var12 = Fonts.NEW_MEDIUM.getFont(6.5F);
      float var13 = MathHelper.clamp((float)((var9 - 8.0) / 200.0), 0.0F, 1.0F);
      float var14 = (1.0F - 0.25F * var13) * var11;
      float var15 = GuiStyle.PADDING.intValue();
      float var16 = 9.0F;
      float var17 = Math.max(var12.height() + var15, var16 + 3.0F);
      float var18 = var15 + var16 + 3.0F + var12.width(var6) + var15;
      float var19 = var15 + var12.width(var7) + var15;
      float var20 = var18 + var19;
      float var21 = 9.0F;
      float var22 = var3 - var20 / 2.0F;
      float var23 = var4 - var21 - var17;
      var1.pushMatrix();
      var1.getMatrices().translate(var3, var4);
      var1.getMatrices().scale(var14, var14);
      var1.getMatrices().translate(-var3, -var4);
      ProxyDotRadiusData var24 = ProxyDotRadiusData.method8069(var17 / 2.0F);
      var1.drawRoundedRect(var22, var23, var20, var17, var24, var2.getHudBackground().getZenithColor());
      var1.drawRoundedRect(var22 + 1.5F, var23 + (var17 - var16) / 2.0F, var16, var16, ProxyDotRadiusData.method8069(var16 / 2.0F), var8.method7294(0.9F));
      var5.draw(var1, var22 + 1.5F + (var16 - 6.0F) / 2.0F, var23 + (var17 - 6.0F) / 2.0F, new ZenithColor(255, 255, 255));
      float var25 = var23 + (var17 - var12.height()) / 2.0F;
      var1.drawText(var12, var6, var22 + 1.5F + var16 + 4.0F, var25, var2.getTextEnable().getZenithColor());
      var1.drawText(var12, var7, var22 + var18 + var15 * 0.5F, var25, var2.getTextTertiary().getZenithColor());
      var1.drawRect(var3 - 0.5F, var23 + var17, 1.0F, var21 - 2.0F, var8.method7294(0.8F));
      var1.drawRoundedRect(var3 - 1.5F, var4 - 1.5F, 3.0F, 3.0F, ProxyDotRadiusData.method8069(1.5F), var8);
      var1.popMatrix();
   }

   private void method5428(MenuDrawContext var1, ZenithStyle var2, Vec3d var3, Vec3d var4, MenuGlyph var5, String var6, ZenithColor var7, float var8) {
      double var9 = var3.x - var4.x;
      double var11 = var3.z - var4.z;
      float var13 = (float)Math.toDegrees(Math.atan2(var11, var9)) - 90.0F;
      float var14 = MathHelper.wrapDegrees(var13 - mc.getEntityRenderDispatcher().camera.getYaw());
      double var15 = Math.toRadians(var14);
      float var17 = this.compassRadius.getValue3() * var8;
      float var18 = mc.getWindow().getScaledWidth() / 2.0F + (float)Math.sin(var15) * var17;
      float var19 = mc.getWindow().getScaledHeight() / 2.0F - (float)Math.cos(var15) * var17;
      Font var20 = Fonts.NEW_REGULAR.getFont(6.0F);
      float var21 = 8.0F;
      float var22 = var21 + 3.0F;
      float var23 = 1.5F + var21 + 3.0F + var20.width(var6) + 4.0F;
      float var24 = var18 - var23 / 2.0F;
      float var25 = var19 - var22 / 2.0F;
      var1.drawRoundedRect(var24, var25, var23, var22, ProxyDotRadiusData.method8069(var22 / 2.0F), var2.getHudBackground().getZenithColor());
      var1.drawRoundedRect(var24 + 1.5F, var25 + 1.5F, var21, var21, ProxyDotRadiusData.method8069(var21 / 2.0F), var7);
      var5.draw(var1, var24 + 1.5F + (var21 - 6.0F) / 2.0F, var25 + 1.5F + (var21 - 6.0F) / 2.0F, new ZenithColor(255, 255, 255));
      var1.drawText(var20, var6, var24 + 1.5F + var21 + 3.0F, var25 + (var22 - var20.height()) / 2.0F, var2.getTextSecondary().getZenithColor());
   }

   private static String meters(double var0) {
      return var0 >= 1000.0 ? Math.round(var0 / 100.0) / 10.0 + "k" : Math.round(var0) + "m";
   }

   private static boolean method5429(double var0, double var2, double var4) {
      return Double.isFinite(var0)
         && Double.isFinite(var2)
         && Double.isFinite(var4)
         && Math.abs(var0) <= 3.0E7
         && Math.abs(var4) <= 3.0E7
         && Math.abs(var2) <= 100000.0;
   }

   private static String method5430(String var0) {
      return var0 == null ? "" : (var0.length() > 32 ? var0.substring(0, 32) : var0);
   }

   private static String method5431(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   public static final class Data {
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
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      public Data(String var1, String var2) {
         this.text = var1;
         this.text2 = var2;
      }

      public String getText() {
         return this.text;
      }

      public String name() {
         return this.text2;
      }
   }

   public static final class Data2 {
      private final String text;
      private final MenuGlyph menuGlyph;
      private final int intValue;

      public String text() {
         return this.text;
      }

      public MenuGlyph menuGlyph() {
         return this.menuGlyph;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.menuGlyph, other.menuGlyph) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, menuGlyph, intValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "menuGlyph=" + this.menuGlyph + ", " + "intValue=" + this.intValue + "]";
      }

      public Data2(String var1, MenuGlyph var2, int var3) {
         this.text = var1;
         this.menuGlyph = var2;
         this.intValue = var3;
      }

      public String key() {
         return this.text;
      }

      public MenuGlyph glyph() {
         return this.menuGlyph;
      }

      public int color() {
         return this.intValue;
      }
   }
}
