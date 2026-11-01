package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.screens.shulker.ShulkerTooltipComponent;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;

@ModuleInfo(
   name = "ShulkerPreview",
   category = ModuleCategory.MISC,
   description = "module.shulkerpreview.description"
)
public class ShulkerPreviewModule extends Module {
   public static final ShulkerPreviewModule INSTANCE = new ShulkerPreviewModule();
   private final BooleanSetting alwaysShow = new BooleanSetting("module.shulkerPreview.alwaysShow", "module.shulkerPreview.alwaysShow.desc", false);
   private final BindSetting previewKey = new BindSetting(
      "module.shulkerPreview.previewKey", "module.shulkerPreview.previewKey.desc", -1, () -> !this.alwaysShow.isEnabled()
   );
   private final BooleanSetting worldDisplay = new BooleanSetting("module.shulkerPreview.worldDisplay", "module.shulkerPreview.worldDisplay.desc", true);
   private final SliderSetting worldScale = new SliderSetting(
      "module.shulkerPreview.worldScale", 0.7F, 0.3F, 1.5F, 0.05F, "module.shulkerPreview.worldScale.desc", "x", this.worldDisplay::isEnabled, null
   );
   private final Map<ItemEntity, ShulkerPreviewModule.Data> items = new WeakHashMap<>();

   private ShulkerPreviewModule() {
   }

   public static Optional<TooltipData> method1387(ItemStack var0) {
      if (INSTANCE.isEnabled() && INSTANCE.method1389()) {
         if (var0.getItem() instanceof BlockItem var1 && var1.getBlock() instanceof ShulkerBoxBlock) {
            DefaultedList var4 = DefaultedList.ofSize(27, ItemStack.EMPTY);
            ContainerComponent var3 = var0.get(DataComponentTypes.CONTAINER);
            if (var3 != null) {
               var3.copyTo(var4);
            }

            return Optional.of(new ShulkerPreviewModule.Entry(var4));
         } else {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   public static TooltipComponent method1388(TooltipData var0) {
      return var0 instanceof ShulkerPreviewModule.Entry var1 ? new ShulkerTooltipComponent(var1.getDefaultedList()) : null;
   }

   private boolean method1389() {
      if (this.alwaysShow.isEnabled()) {
         return true;
      } else {
         int var1 = this.previewKey.getKeyCode();
         return var1 == -1 ? false : this.previewKey.isVisible() && UseItemUtils.method6522(UseItemUtils.method6509(var1), var1);
      }
   }

   @EventTarget
   public void method1390(DrawEvent var1) {
      if (this.worldDisplay.isEnabled() && this.method1389() && mc.player != null && mc.world != null && mc.getEntityRenderDispatcher().camera != null) {
         CustomDrawContext var2 = var1.getCustomDrawContext();
         if (var2 != null) {
            for (Entity var4 : mc.world.getEntities()) {
               if (var4 instanceof ItemEntity var5) {
                  ItemStack var6 = var5.getStack();
                  if (!var6.isEmpty() && method1396(var6)) {
                     this.method1391(var2, var5, var6);
                  }
               }
            }
         }
      }
   }

   private void method1391(CustomDrawContext var1, ItemEntity var2, ItemStack var3) {
      Vec3d var4 = MathUtils.method2656(var2);
      Box var5 = var2.getBoundingBox().offset(var4.subtract(var2.getEntityPos()));
      Vector4d var6 = this.method1393(var5);
      if (var6 != null) {
         ShulkerTooltipComponent var7 = this.method1392(var2, var3);
         float var8 = this.worldScale.getValue3();
         float var9 = var7.getWidth(mc.textRenderer);
         float var10 = var7.getHeight(mc.textRenderer);
         float var11 = ((float)var6.x + (float)var6.z) / 2.0F;
         float var12 = (float)var6.y - 4.0F - var10 * var8 / 2.0F;
         float var13 = var11 - var9 / 2.0F;
         float var14 = var12 - var10 / 2.0F;
         this.pushCenteredScale(var1, var11, var12, var8, var8);
         var7.drawItems(mc.textRenderer, Math.round(var13), Math.round(var14), var7.getWidth(mc.textRenderer), var7.getHeight(mc.textRenderer), var1);
         var1.popMatrix();
      }
   }

   private ShulkerTooltipComponent method1392(ItemEntity var1, ItemStack var2) {
      ContainerComponent var3 = var2.get(DataComponentTypes.CONTAINER);
      ShulkerPreviewModule.Data var4 = this.items.get(var1);
      if (var4 == null || var4.getContainerComponent() != var3) {
         var4 = new ShulkerPreviewModule.Data(var3, new ShulkerTooltipComponent(method1397(var2)));
         this.items.put(var1, var4);
      }

      return var4.getShulkerTooltipComponent();
   }

   private void pushCenteredScale(CustomDrawContext var1, float var2, float var3, float var4, float var5) {
      var1.pushMatrix();
      var1.getMatrices().translate(var2, var3);
      var1.getMatrices().scale(var4, var5);
      var1.getMatrices().translate(-var2, -var3);
   }

   private Vector4d method1393(Box var1) {
      Vector4d var2 = null;

      for (Vec3d var6 : this.method1394(var1)) {
         if (this.method1395(var6)) {
            Vec3d var7 = ProjectionUtils.method301(var6);
            if (var7 != null && !(var7.z <= 0.0)) {
               if (var2 == null) {
                  var2 = new Vector4d(var7.x, var7.y, var7.x, var7.y);
               } else {
                  var2.x = Math.min(var2.x, var7.x);
                  var2.y = Math.min(var2.y, var7.y);
                  var2.z = Math.max(var2.z, var7.x);
                  var2.w = Math.max(var2.w, var7.y);
               }
            }
         }
      }

      return var2;
   }

   private Vec3d[] method1394(Box var1) {
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
   }

   private boolean method1395(Vec3d var1) {
      if (mc.gameRenderer != null && mc.gameRenderer.getCamera() != null) {
         Vec3d var2 = mc.gameRenderer.getCamera().getCameraPos();
         Vec3d var3 = var1.subtract(var2);
         float var4 = mc.gameRenderer.getCamera().getPitch();
         float var5 = mc.gameRenderer.getCamera().getYaw();
         double var6 = Math.toRadians(var4);
         double var8 = Math.toRadians(var5);
         Vec3d var10 = new Vec3d(-Math.sin(var8) * Math.cos(var6), -Math.sin(var6), Math.cos(var8) * Math.cos(var6));
         return var3.dotProduct(var10) > 0.0;
      } else {
         return false;
      }
   }

   private static boolean method1396(ItemStack var0) {
      return var0.getItem() instanceof BlockItem var1 && var1.getBlock() instanceof ShulkerBoxBlock;
   }

   private static DefaultedList<ItemStack> method1397(ItemStack var0) {
      DefaultedList var1 = DefaultedList.ofSize(27, ItemStack.EMPTY);
      ContainerComponent var2 = var0.get(DataComponentTypes.CONTAINER);
      if (var2 != null) {
         var2.copyTo(var1);
      }

      return var1;
   }

   private static final class Data {
      private final ContainerComponent containerComponent;
      private final ShulkerTooltipComponent shulkerTooltipComponent;

      public ContainerComponent containerComponent() {
         return this.containerComponent;
      }

      public ShulkerTooltipComponent shulkerTooltipComponent() {
         return this.shulkerTooltipComponent;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.containerComponent, other.containerComponent) && java.util.Objects.equals(this.shulkerTooltipComponent, other.shulkerTooltipComponent);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(containerComponent, shulkerTooltipComponent);
      }

      @Override
      public String toString() {
         return "Data[" + "containerComponent=" + this.containerComponent + ", " + "shulkerTooltipComponent=" + this.shulkerTooltipComponent + "]";
      }

      private Data(ContainerComponent var1, ShulkerTooltipComponent var2) {
         this.containerComponent = var1;
         this.shulkerTooltipComponent = var2;
      }

      public ContainerComponent getContainerComponent() {
         return this.containerComponent;
      }

      public ShulkerTooltipComponent getShulkerTooltipComponent() {
         return this.shulkerTooltipComponent;
      }
   }

   public static final class Entry implements TooltipData {
      private final DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(27, ItemStack.EMPTY);

      public Entry(DefaultedList<ItemStack> var1) {
         for (int var2 = 0; var2 < 27 && var2 < var1.size(); var2++) {
            ItemStack var3 = (ItemStack)var1.get(var2);
            this.defaultedList.set(var2, var3.isEmpty() ? ItemStack.EMPTY : var3.copy());
         }
      }

      public DefaultedList<ItemStack> getDefaultedList() {
         return this.defaultedList;
      }
   }
}
