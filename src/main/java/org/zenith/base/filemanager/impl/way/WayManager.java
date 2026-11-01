package org.zenith.base.filemanager.impl.way;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.zenith.ChatLogger;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.ChatReceiveEvent;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.modules.impl.render.EntityESPModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.JsonListStorage;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class WayManager extends JsonListStorage<Way> implements MinecraftAccess {
   private static final Pattern CHAT_COORDS_PATTERN = Pattern.compile("(?<!\\d)(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)(?!\\d)");
   private int pendingEventMessages;

   public WayManager() {
      super("way", "", (new TypeToken<ArrayList<Way>>() {}).getType(), ArrayList::new);
      EventManager.register(this);
   }

   @Override
   public Gson createGson() {
      return new GsonBuilder().registerTypeAdapter(Way.class, new WayAdapter()).create();
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public void addWay(Way var1) {
      this.items.add(var1);
   }

   public void addWay(String var1, BlockPos var2, String var3) {
      this.items.add(new Way(var1, var2, var3));
   }

   public Way getWay(String var1) {
      return this.items.stream().filter(var1x -> var1x.name().equalsIgnoreCase(var1)).findAny().orElse(null);
   }

   public boolean hasWay(String var1) {
      return this.items.stream().anyMatch(var1x -> var1x.name().equalsIgnoreCase(var1));
   }

   public void deleteWay(String var1) {
      this.items.removeIf(var1x -> var1x.name().equalsIgnoreCase(var1));
   }

   public void clearList() {
      if (!this.isEmpty()) {
         this.items.clear();
      }
   }

   public void startEventCapture() {
      this.pendingEventMessages = 2;
   }

   @EventTarget
   public void onChatReceive(ChatReceiveEvent var1) {
      if (this.pendingEventMessages > 0) {
         this.pendingEventMessages--;
         this.onCheckMessage(var1.getText().getString());
      }
   }

   public void onCheckMessage(String var1) {
      Matcher var2 = CHAT_COORDS_PATTERN.matcher(var1);
      if (var2.find()) {
         int var3 = Integer.parseInt(var2.group(1));
         int var4 = Integer.parseInt(var2.group(2));
         int var5 = Integer.parseInt(var2.group(3));
         this.deleteWay("event");
         this.addWay(new Way("event", new BlockPos(var3, var4, var5), this.getCurrentServer()));
         ChatLogger.method2019("Точка event добавлена: " + var3 + " " + var4 + " " + var5);
         this.pendingEventMessages = 0;
      }
   }

   private String getCurrentServer() {
      return mc.getNetworkHandler() != null && mc.getNetworkHandler().getServerInfo() != null ? mc.getNetworkHandler().getServerInfo().address : "VANILLA";
   }

   @EventTarget
   public void onDraw(DrawEvent var1) {
      if (!this.isEmpty() && mc.world != null) {
         this.items
            .forEach(
               var2 -> {
                  Vec3d var3 = var2.pos().toCenterPos();
                  Vec3d var4 = ProjectionUtils.method301(var3);
                  if (ProjectionUtils.method302(var3)
                     && var2.server()
                        .equalsIgnoreCase(
                           mc.getNetworkHandler() != null && mc.getNetworkHandler().getServerInfo() != null
                              ? mc.getNetworkHandler().getServerInfo().address
                              : "VANILLA"
                        )) {
                     String var5 = (int)MathUtils.method2652(mc.getEntityRenderDispatcher().camera.getCameraPos().distanceTo(var3), 1.0) + "m";
                     ZenithStyle var6 = Zenith.getInstance().getStyleManager().getCurrentStyle();
                     Font var7 = Fonts.NEW_MEDIUM.getFont(8.0F);
                     float var8 = GuiStyle.PADDING.intValue();
                     float var9 = var7.height() + GuiStyle.PADDING.intValue() + 1.0F;
                     float var10 = var8 + var7.width(var2.name()) + var8 + var7.width(var5) + var8 + var8 / 2.0F;
                     float var11 = (float)(var4.getX() - var10 / 2.0F);
                     float var12 = (float)(var4.getY() - var9 / 2.0F);
                     this.pushCenteredScale(var1.getCustomDrawContext(), (float)var4.x, (float)var4.y, 1.0F, 1.0F);
                     ProxyDotRadiusData var13 = ProxyDotRadiusData.method8069(InterfaceModule.method6801() * 0.6F);
                     RenderShaders.method5287(
                        MatrixUtils.method664(var1.getCustomDrawContext().getMatrices()),
                        var11,
                        var12,
                        var10,
                        var9,
                        22.0F,
                        var13,
                        ZenithColor.INSTANCE,
                        EntityESPModule.INSTANCE.getBlur().isEnabled() && EntityESPModule.INSTANCE.getBlur().isVisible(),
                        EntityESPModule.INSTANCE.getGlow().isEnabled() && EntityESPModule.INSTANCE.getGlow().isVisible()
                     );
                     var1.getCustomDrawContext().drawRoundedRect(var11, var12, var10, var9, var13, var6.getHudBackground().getZenithColor());
                     float var14 = var12 + (var9 - 1.0F - var7.height()) / 2.0F;
                     var1.getCustomDrawContext()
                        .drawRoundedRect(var11, var12, var8 + var7.width(var2.name()) + var8, var9, var13, var6.getHudBackground().getZenithColor());
                     var1.getCustomDrawContext().drawText(var7, var2.name(), var11 + var8, var14, var6.getTextEnable().getZenithColor());
                     var1.getCustomDrawContext()
                        .drawText(var7, var5, var11 + var8 + var7.width(var2.name()) + var8 + var8 / 2.0F, var14, var6.getTextEnable().getZenithColor());
                     this.pop(var1.getCustomDrawContext());
                  }
               }
            );
      }
   }

   private void pushCenteredScale(CustomDrawContext var1, float var2, float var3, float var4, float var5) {
      var1.pushMatrix();
      var1.getMatrices().translate(var2, var3);
      var1.getMatrices().scale(var4 * EntityESPModule.INSTANCE.getSize(), var5 * EntityESPModule.INSTANCE.getSize());
      var1.getMatrices().translate(-var2, -var3);
   }

   private void pop(CustomDrawContext var1) {
      var1.popMatrix();
   }
}
