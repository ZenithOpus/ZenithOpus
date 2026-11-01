package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.Class27;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "ClickAction",
   description = "module.clickaction.description",
   category = ModuleCategory.MISC
)
public final class ClickActionModule extends Module {
   private final BindSetting friendBind = new BindSetting("module.clickAction.friendBind", "module.clickAction.friendBind.desc");
   private final BindSetting expBind = new BindSetting("module.clickAction.expBind", "module.clickAction.expBind.desc");
   private final List<ClickActionModule.Data> items = new ArrayList<>();
   private final StopWatch stopWatch = new StopWatch();
   public static final ClickActionModule INSTANCE = new ClickActionModule();
   private Slot slot = null;
   private boolean flag = false;

   private ClickActionModule() {
      this.items
         .add(
            new ClickActionModule.Data(Items.ENDER_PEARL, new BindSetting("module.clickAction.enderPearl", "module.clickAction.enderPearl.desc"), new Class27())
         );
      this.items
         .add(
            new ClickActionModule.Data(Items.WIND_CHARGE, new BindSetting("module.clickAction.windCharge", "module.clickAction.windCharge.desc"), new Class27())
         );
   }

   @Override
   public List<Setting> getSettings() {
      ArrayList<Setting> var1 = new ArrayList<>();
      var1.add(this.expBind);
      var1.add(this.friendBind);
      var1.addAll(this.items.stream().map(ClickActionModule.Data::getBindSetting).toList());
      return var1;
   }

   @EventTarget
   public void method5483(KeyEvent var1) {
      if (var1.isPress(this.friendBind.getKeyCode()) && mc.player != null && mc.world != null) {
         EntityHitResult var2 = TargetUtils.method3211(
            new Rotation(mc.player.getYaw(), mc.player.getPitch()), var0 -> var0 instanceof PlayerEntity && var0 != mc.player
         );
         if (var2 != null && var2.getEntity() instanceof PlayerEntity var3) {
            String var6 = var3.getGameProfile().name();
            if (Zenith.getInstance().getFriends().getItems().contains(var6)) {
               Zenith.getInstance().getFriends().method7629(var6);
            } else {
               Zenith.getInstance().getFriends().add(var6);
            }

            Zenith.getInstance().getFriends().save();
         }
      }

      this.items
         .stream()
         .filter(var1x -> var1.isPress(var1x.bindSetting.getKeyCode()) && InventoryUtils.method906(var1x.item) != null)
         .forEach(var0 -> var0.class27.setFlag(true));
      this.items.stream().filter(var1x -> var1.isRelease(var1x.bindSetting.getKeyCode())).forEach(var0 -> {
         InventoryUtils.method927(var0.item);
         var0.class27.setFlag(false);
      });
      if (var1.isPress(this.expBind.getKeyCode())) {
         Slot var5 = InventoryUtils.method906(Items.EXPERIENCE_BOTTLE);
         if (var5 == null) {
            Zenith.getInstance()
               .getHudHolder()
               .method6972(
                  "M",
                  Text.of(
                     Items.EXPERIENCE_BOTTLE
                        .getName()
                        .copy()
                        .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
                        .append(
                           Text.of("не найден")
                              .copy()
                              .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()))
                        )
                  )
               );
            return;
         }
      }
   }

   @EventTarget
   public void method5484(WorldRenderEvent var1) {
      PredictionsModule.INSTANCE
         .method2902(var1.getMatrixStack(), this.items.stream().filter(var0 -> var0.class27.isValue()).map(var0 -> var0.item.getDefaultStack()).toList());
   }

   @EventTarget(4)
   public void method5485(HandleInputEvent var1) {
      if (!var1.isCancelled() && mc.player != null) {
         if (this.flag) {
            this.flag = false;
         } else {
            boolean var2 = mc.player.getMainHandStack().getItem().equals(Items.EXPERIENCE_BOTTLE);
            Slot var3 = InventoryUtils.method906(Items.EXPERIENCE_BOTTLE);
            if (UseItemUtils.method6521(this.expBind) && var3 != null) {
               PlayerSimulation var4 = PlayerSimulation.method6031(3);
               Rotation var5 = new Rotation(mc.player.getYaw(), RotationUtils.method6913(var4.box.getCenter()).getFloatValue2());
               rotationManager.method7849(
                  new RotationRequest(var5, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var5), bcCameraTestnerv.getRotationConfig()),
                  5,
                  this
               );
               if (!var2) {
                  if (KeyBindUtils.method2630(ClickActionModule.class)) {
                     KeyBindUtils.method2627(ClickActionModule.class, () -> {
                        if (this.slot == null) {
                           this.slot = var3;
                        }

                        InventoryUtils.method900(var3, Hand.MAIN_HAND, true);
                        this.flag = true;
                     });
                  }
               } else if (this.stopWatch.method7546(70L) && rotationManager.method7844().method3131(var5).method5330(180.0F, 10.0F)) {
                  UseItemUtils.useItem(Hand.MAIN_HAND);
                  var1.cancel();
                  this.stopWatch.reset();
               }
            } else if (this.slot != null) {
               KeyBindUtils.method2627(ClickActionModule.class, () -> {
                  if (!UseItemUtils.method6521(this.expBind)) {
                     InventoryUtils.method900(this.slot, Hand.MAIN_HAND, true);
                     this.slot = null;
                  }
               });
            }
         }
      }
   }

   public List<ClickActionModule.Data> method5486() {
      return Collections.unmodifiableList(this.items);
   }

   public BindSetting getExpBind() {
      return this.expBind;
   }

   public static final class Data {
      private final Item item;
      private final BindSetting bindSetting;
      private final Class27 class27;

      public Item item() {
         return this.item;
      }

      public BindSetting bindSetting() {
         return this.bindSetting;
      }

      public Class27 class27() {
         return this.class27;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.item, other.item) && java.util.Objects.equals(this.bindSetting, other.bindSetting) && java.util.Objects.equals(this.class27, other.class27);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(item, bindSetting, class27);
      }

      @Override
      public String toString() {
         return "Data[" + "item=" + this.item + ", " + "bindSetting=" + this.bindSetting + ", " + "class27=" + this.class27 + "]";
      }

      public Data(Item var1, BindSetting var2, Class27 var3) {
         this.item = var1;
         this.bindSetting = var2;
         this.class27 = var3;
      }

      public Item getItem() {
         return this.item;
      }

      public BindSetting getBindSetting() {
         return this.bindSetting;
      }

      public Class27 getClass27() {
         return this.class27;
      }
   }
}
