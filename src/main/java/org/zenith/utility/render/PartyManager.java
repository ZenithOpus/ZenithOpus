package org.zenith.utility.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.ServerType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.KeyEvent;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.CloudStateEvent;
import org.zenith.client.events.EntityDeathEvent;
import org.zenith.client.events.EntityStatusEvent;
import org.zenith.client.events.Event10;
import org.zenith.client.events.Event11;
import org.zenith.client.events.TickEvent;
import org.zenith.client.modules.impl.render.PartyMarksModule;
import org.zenith.client.screens.nlgui.chat.ChatCompose;
import org.zenith.client.settings.Setting;
import org.zenith.utility.api.CloudChatClient;
import org.zenith.utility.api.CloudDmClient;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.api.Share;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.misc.ServerNameUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;

public final class PartyManager implements MinecraftAccess {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cloud");
   private static final long CONST_longValue = 1500L;
   private static final long CONST_longValue2 = 15000L;
   private static final long CONST_longValue3 = 6000L;
   private static final long CONST_longValue4 = 60000L;
   private static final long CONST_longValue5 = 4000L;
   private static final long CONST_longValue6 = 90000L;
   private static final long CONST_longValue7 = 10000L;
   private final Cloud cloud;
   private final Share share;
   private long longValue = System.currentTimeMillis();
   private double lastX;
   private double lastY;
   private double doubleValue;
   private float floatValue;
   private float floatValue2;
   private long longValue2;
   private String text = "";
   private long longValue3;
   private long longValue4 = System.currentTimeMillis();
   private String lastStatus = "";
   private int intValue = -1;
   private final Map<Integer, Long> items = new HashMap<>();
   private final Map<Long, Long> items2 = new HashMap<>();
   private List<PartyMember.Data11> items3 = List.of();
   private long longValue5;
   private boolean flag;
   private long longValue6;
   private int intValue2;
   private int intValue3;
   private long longValue7 = System.currentTimeMillis();
   private int intValue4 = -1;
   private int intValue5 = -1;
   private String text2 = "";
   private String text3 = "";
   private long longValue8;
   private PartyMember.Data6 data6;
   private PartyManager.Data data;
   private String text4 = "";
   private boolean flag2;
   private int intValue6;

   public PartyManager(Cloud var1, Share var2) {
      this.cloud = var1;
      this.share = var2;
      EventManager.register(this);
   }

   public static PartyManager method17() {
      Zenith var0 = Zenith.getInstance();
      return var0 == null ? null : var0.getPartyManager();
   }

   private PartyMember method18() {
      return this.cloud.getPartyMember();
   }

   @EventTarget
   private void method19(CloudStateEvent var1) {
      if (var1.method6858()) {
         this.text = "";
         this.intValue4 = -1;
         this.intValue5 = -1;
         this.data6 = null;
      }
   }

   @EventTarget
   private void setLongValue(KeyEvent var1) {
      this.longValue = System.currentTimeMillis();
   }

   @EventTarget
   private void method20(AttackEvent var1) {
      if (var1.getType4() == AttackEvent.Type.PRE) {
         Entity var2 = var1.getEntity();
         if (var2 instanceof PlayerEntity && var2 != mc.player) {
            long var3 = System.currentTimeMillis();
            this.longValue2 = var3;
            this.longValue = var3;
            this.items.put(var2.getId(), var3);
         }
      }
   }

   @EventTarget
   private void method21(EntityDeathEvent var1) {
      if (mc.isOnThread()) {
         if (var1.getLivingEntity() == mc.player) {
            this.method29();
         }
      }
   }

   @EventTarget
   private void method22(EntityStatusEvent var1) {
      if (var1.getEntityStatusS2CPacket().getStatus() == 3 && mc.world != null && !this.items.isEmpty()) {
         if (var1.getEntityStatusS2CPacket().getEntity(mc.world) instanceof PlayerEntity var2 && var2 != mc.player) {
            Long var4 = this.items.remove(var2.getId());
            if (var4 != null && System.currentTimeMillis() - var4 < 6000L) {
               this.intValue2++;
            }
         }
      }
   }

   private void method23() {
      CloudChatClient.getInstance().method2165(60000L);
      if (this.share.notifyMessages.isEnabled()) {
         CloudDmClient.getInstance().method4860();
      } else {
         CloudDmClient.getInstance().method4861(60000L);
      }
   }

   private void method24() {
      if (++this.intValue6 >= 40) {
         this.intValue6 = 0;
         this.method23();
         JsonObject var1 = new JsonObject();

         for (Setting var3 : this.share.method946()) {
            var3.safe(var1);
         }

         String var4 = var1.toString();
         if (!this.text4.isEmpty() && !var4.equals(this.text4)) {
            this.share.save();
         }

         this.text4 = var4;
      }
   }

   @EventTarget
   private void onTick(TickEvent var1) {
      long var2 = System.currentTimeMillis();
      this.method24();
      this.method25(var2);
      this.method26(var2);
      this.method27();
      this.method28();
      this.method30(var2);
      this.items.values().removeIf(var2x -> var2 - var2x > 6000L);
      if (this.cloud.method7862()) {
         PartyMember var4 = this.method18();
         if (var4.version() != this.intValue) {
            this.intValue = var4.version();
            this.method37(var4);
         }

         this.method34(var2);
         this.method35();
         this.method36(var2);
         this.method46(var2);
      }
   }

   private void method25(long var1) {
      if (mc.player != null) {
         double var3 = mc.player.getX() - this.lastX;
         double var5 = mc.player.getZ() - this.doubleValue;
         double var7 = mc.player.getY() - this.lastY;
         if (var3 * var3 + var7 * var7 + var5 * var5 > 0.01
            || Math.abs(mc.player.getYaw() - this.floatValue) > 1.0F
            || Math.abs(mc.player.getPitch() - this.floatValue2) > 1.0F) {
            this.longValue = var1;
         }

         this.lastX = mc.player.getX();
         this.lastY = mc.player.getY();
         this.doubleValue = mc.player.getZ();
         this.floatValue = mc.player.getYaw();
         this.floatValue2 = mc.player.getPitch();
      }
   }

   private void method26(long var1) {
      if (mc.player != null && mc.player.hurtTime > 0) {
         DamageSource var3 = mc.player.getRecentDamageSource();
         if (var3 != null && var3.getAttacker() instanceof PlayerEntity var4 && var4 != mc.player) {
            this.longValue2 = var1;
         }
      }
   }

   private void method27() {
      if (mc.player != null && !mc.player.isDead() && !(mc.player.getHealth() <= 0.0F)) {
         PlayerInventory var1 = mc.player.getInventory();
         int var2 = Math.min(var1.size(), 64);
         long var3 = 1469598103934665603L;

         for (int var5 = 0; var5 < var2; var5++) {
            ItemStack var6 = var1.getStack(var5);
            var3 = (var3 ^ (var6.isEmpty() ? 0 : System.identityHashCode(var6.getItem()))) * 1099511628211L;
            var3 = (var3 ^ var6.getCount()) * 1099511628211L;
            var3 = (var3 ^ var6.getDamage()) * 1099511628211L;
         }

         if (var3 != this.longValue5) {
            this.longValue5 = var3;
            ArrayList var10 = new ArrayList();

            for (int var11 = 0; var11 < var2; var11++) {
               ItemStack var7 = var1.getStack(var11);
               if (!var7.isEmpty()) {
                  var10.add(
                     new PartyMember.Data11(
                        Registries.ITEM.getId(var7.getItem()).toString(), Math.min(var7.getCount(), 127), Math.max(0, var7.getDamage()), var11, var7.hasGlint()
                     )
                  );
               }
            }

            this.items3 = List.copyOf(var10);
         }
      }
   }

   private void method28() {
      if (mc.player == null) {
         this.flag = false;
      } else {
         boolean var1 = mc.player.isDead() || mc.player.getHealth() <= 0.0F || mc.currentScreen instanceof DeathScreen;
         if (var1 && !this.flag) {
            this.method29();
         }

         this.flag = var1;
      }
   }

   private void method29() {
      long var1 = System.currentTimeMillis();
      if (mc.player != null && var1 - this.longValue6 >= 3000L) {
         this.longValue6 = var1;
         this.intValue3++;
         this.data = new PartyManager.Data(
            this.cloud.method7911(),
            this.cloud.method7913(),
            mc.player.getX(),
            mc.player.getY(),
            mc.player.getZ(),
            this.items3,
            mc.player.experienceLevel,
            var1
         );
      }
   }

   private void method30(long var1) {
      PartyManager.Data var3 = this.data;
      if (var3 != null) {
         String var4 = "";
         if (mc.currentScreen instanceof DeathScreen var5 && var5.message != null) {
            var4 = var5.message.getString();
         } else if (var1 - var3.time() < 1500L) {
            return;
         }

         this.data = null;
         if (!this.flag2) {
            this.flag2 = true;
            PartyMarksModule.INSTANCE.setToggled(true);
         }

         if (this.share.deaths.isEnabled() && this.cloud.method7860()) {
            this.method18()
               .method1162(
                  var3.server(),
                  var3.dimension(),
                  var3.getDoubleValue(),
                  var3.getDoubleValue2(),
                  var3.getDoubleValue3(),
                  var4,
                  var3.getItems(),
                  var3.getIntValue()
               );
         } else {
            this.method18()
               .method1163(
                  var3.server(),
                  var3.dimension(),
                  var3.getDoubleValue(),
                  var3.getDoubleValue2(),
                  var3.getDoubleValue3(),
                  var4,
                  var3.getItems(),
                  var3.getIntValue()
               );
         }
      }
   }

   public String method31() {
      long var1 = System.currentTimeMillis();
      String var3 = this.share.method947();
      if ("dnd".equals(var3)) {
         return "dnd";
      } else {
         float var4 = this.share.autoAfk.getValue3();
         if (var4 > 0.0F && (float)(var1 - this.longValue) > var4 * 60000.0F) {
            return "afk";
         } else if (this.share.autoPvp.isEnabled() && var1 - this.longValue2 < 15000L) {
            return "pvp";
         } else if (!"auto".equals(var3)) {
            return var3;
         } else {
            return mc.world == null ? "menu" : "online";
         }
      }
   }

   public boolean method32() {
      return "dnd".equals(this.method31());
   }

   public String method33() {
      if (this.share.allowJoin.isEnabled() && this.share.server.isEnabled()) {
         ServerInfo var1 = mc.getCurrentServerEntry();
         return var1 != null && !var1.isRealm() && var1.address != null && !mc.isInSingleplayer() ? var1.address.trim() : "";
      } else {
         return "";
      }
   }

   private void method34(long var1) {
      String var3 = this.method31();
      if (!var3.equals(this.lastStatus)) {
         this.lastStatus = var3;
         this.longValue4 = var1;
      }

      String var4 = this.share.server.isEnabled() ? this.cloud.method7911() : "";
      String var5 = this.share.server.isEnabled() && mc.world != null ? ChatCompose.mode() : "";
      String var6 = this.share.text.getValue() == null ? "" : this.share.text.getValue().strip();
      String var7 = this.method33();
      String var8 = var3 + "|" + var6 + "|" + var7 + "|" + var5 + "|" + var4;
      if (!var8.equals(this.text) && var1 - this.longValue3 >= 1500L && this.cloud.method7860()) {
         this.text = var8;
         this.longValue3 = var1;
         this.method18().method1165(new PartyMember.Data2(var3, var6, var7, var5, var4, this.longValue4));
      }
   }

   private void method35() {
      Wardrobe var1 = Wardrobe.method2488();
      if (var1 != null && this.cloud.method7860()) {
         int var2 = this.share.cosmeticsVisibility.getIndex();
         if (var1.version() != this.intValue4 || var2 != this.intValue5) {
            this.intValue4 = var1.version();
            this.intValue5 = var2;
            ArrayList var3 = new ArrayList();
            if (!this.share.method949()) {
               var var4 = var1.method2504();
               var3.addAll(var4.values());
            }

            this.method18().method1166(var3);
            logger.info(
               "Гардероб: образ отправлен узлу — {} вещей, ник {}{}",
               new Object[]{var3.size(), this.cloud.method7871(), this.share.method949() ? " (скрыт настройкой видимости косметики)" : ""}
            );
         }
      }
   }

   private void method36(long var1) {
      if (var1 - this.longValue7 >= 60000L && this.cloud.method7860()) {
         this.longValue7 = var1;
         if (!this.share.stats.isEnabled()) {
            this.intValue2 = 0;
            this.intValue3 = 0;
         } else {
            if (this.intValue2 > 0 || this.intValue3 > 0) {
               this.method18().method1169(Math.min(this.intValue2, 20), Math.min(this.intValue3, 10));
               this.intValue2 = Math.max(0, this.intValue2 - 20);
               this.intValue3 = Math.max(0, this.intValue3 - 10);
            }
         }
      }
   }

   private void method37(PartyMember var1) {
      PartyMember.Data6 var2 = var1.getData6();
      PartyMember.Data6 var3 = this.data6;
      this.data6 = var2;
      if (var2 != null && var3 == null && !this.flag2) {
         this.flag2 = true;
         PartyMarksModule.INSTANCE.setToggled(true);
      }

      if (var3 != null && var2 != null && var3.getLongValue() == var2.getLongValue() && !this.method32()) {
         for (PartyMember.Data3 var5 : var2.getItems()) {
            if (var5.uid() != this.cloud.getLongValue()) {
               PartyMember.Data3 var6 = var3.method1216(var5.uid());
               if (var6 == null) {
                  method53(String.format(method54("cloud.party.toast.joined"), var5.username()));
               } else if (var6.isFlag() != var5.isFlag() && !this.cloud.prefs().method3315(var5.username())) {
                  method53(String.format(method54(var5.isFlag() ? "cloud.party.toast.online" : "cloud.party.toast.offline"), var5.username()));
               }
            }
         }

         for (PartyMember.Data3 var8 : var3.getItems()) {
            if (var8.uid() != this.cloud.getLongValue() && var2.method1216(var8.uid()) == null) {
               method53(String.format(method54("cloud.party.toast.left"), var8.username()));
            }
         }
      }
   }

   @EventTarget
   private void method38(Event10 var1) {
      CloudPackets.Data18 var2 = var1.getData18();
      boolean var3 = this.method32() || !this.share.notifyInvites.isEnabled();
      if (!var3 && method39(var2.kind()) && !this.method40(var2)) {
         var3 = true;
      }

      switch (var2.kind()) {
         case 12:
            long var8 = var2.getJsonObject().has("uid") ? var2.getJsonObject().get("uid").getAsLong() : 0L;
            if (var8 != 0L && var8 != this.cloud.getLongValue() && !this.method32() && !this.cloud.prefs().method3316(var2.username())) {
               method53(
                  String.format(
                     method54("cloud.death.toast.mate"),
                     var2.username(),
                     (int)Math.floor(var2.getJsonObject().get("x").getAsDouble()),
                     (int)Math.floor(var2.getJsonObject().get("y").getAsDouble()),
                     (int)Math.floor(var2.getJsonObject().get("z").getAsDouble())
                  )
               );
            }
            break;
         case 21:
            if (!var3 && !this.cloud.prefs().method3316(var2.username())) {
               String var7 = ServerNameUtils.method5643(
                  PartyMember.method1209(var2.getJsonObject(), "server"), PartyMember.method1209(var2.getJsonObject(), "mode")
               );
               method53(
                  String.format(
                     method54("cloud.join.toast.invited"), var2.username(), var7.isEmpty() ? PartyMember.method1209(var2.getJsonObject(), "join") : var7
                  )
               );
            }
            break;
         case 41:
            if (!var3 && !this.cloud.prefs().method3316(var2.username())) {
               method53(String.format(method54("cloud.party.toast.invited"), var2.username(), PartyMember.method1209(var2.getJsonObject(), "name")));
            }
            break;
         case 46:
            if (!var3 && !this.cloud.prefs().method3316(var2.username())) {
               method53(String.format(method54("cloud.room.toast.invited"), var2.username(), PartyMember.method1209(var2.getJsonObject(), "name")));
            }
            break;
         case 49:
            String var6 = PartyMember.method1209(var2.getJsonObject(), "code");
            if (!var6.isEmpty() && !"mate_gone".equals(var6)) {
               method53(method54("cloud.notice." + var6));
            }
            break;
         case 51:
            if (this.share.notifyMessages.isEnabled() && !this.method32() && !this.cloud.prefs().method3316(var2.username())) {
               String var4 = PartyMember.method1209(var2.getJsonObject(), "text");
               method53(String.format(method54("cloud.dm.toast"), var2.username(), var4.length() > 60 ? var4.substring(0, 60) + "…" : var4));
            }
      }
   }

   private static boolean method39(int var0) {
      return var0 == 41 || var0 == 46 || var0 == 21;
   }

   private boolean method40(CloudPackets.Data18 var1) {
      long var2 = System.currentTimeMillis();
      this.items2.values().removeIf(var2x -> var2 - var2x > 10000L);
      return this.items2.putIfAbsent(var1.uid() * 64L + var1.kind(), var2) == null;
   }

   @EventTarget
   private void method41(Event11 var1) {
      CloudPackets.Data13 var2 = var1.getData13();
      if (!var1.isFlag()
         && var2.getIntValue() == 1
         && this.share.notifyMessages.isEnabled()
         && !this.method32()
         && !this.cloud.prefs().method3316(var2.username())) {
         String var3 = var2.text().length() > 60 ? var2.text().substring(0, 60) + "…" : var2.text();
         method53(var2.username() + ": " + var3);
      }
   }

   public boolean method42(long var1) {
      PartyMember.Data2 var3 = this.method18().method1141(var1);
      return !var3.getText3().isEmpty();
   }

   public void method43(long var1) {
      PartyMember.Data2 var3 = this.method18().method1141(var1);
      if (!var3.getText3().isEmpty()) {
         this.method45(var3.getText3(), var3.server(), var3.mode());
      }
   }

   public void method44(PartyMember.Data13 var1) {
      this.method18().method1178(var1);
      if (!var1.getText3().isEmpty()) {
         this.method45(var1.getText3(), var1.server(), var1.mode());
      }
   }

   public void method45(String var1, String var2, String var3) {
      String var4 = var1.trim();
      if (!var4.isEmpty()) {
         String var5 = this.cloud.method7911();
         String var6 = var2.isEmpty() ? Cloud.method7912(var4.toLowerCase(Locale.ROOT)) : var2;
         this.text2 = var6;
         this.text3 = var3;
         this.longValue8 = System.currentTimeMillis();
         if (var5.isEmpty() || !var5.equals(var6) || mc.world == null) {
            mc.execute(() -> {
               if (mc.world != null) {
                  mc.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
               }

               ServerInfo var2x = new ServerInfo(ServerNameUtils.display(var6), var4, ServerType.OTHER);
               ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), mc, ServerAddress.parse(var4), var2x, false, null);
            });
         }
      }
   }

   private void method46(long var1) {
      if (!this.text2.isEmpty() && mc.world != null && mc.player != null) {
         if (var1 - this.longValue8 > 90000L) {
            this.text2 = "";
         } else if (var1 - this.longValue8 >= 4000L && this.text2.equals(this.cloud.method7911())) {
            String var3 = this.text3;
            String var4 = this.text2;
            this.text2 = "";
            if (!var3.isEmpty()) {
               String var5 = ChatCompose.mode();
               if (var5.isEmpty() || !ServerNameUtils.method5641(var5).equalsIgnoreCase(ServerNameUtils.method5641(var3))) {
                  String var6 = ServerNameUtils.method5642(var4, var3);
                  if (var6.isEmpty()) {
                     method53(String.format(method54("cloud.join.toast.mode"), ServerNameUtils.method5641(var3)));
                  } else {
                     method53(String.format(method54("cloud.join.toast.command"), ServerNameUtils.method5641(var3), var6));
                     if (mc.currentScreen == null) {
                        mc.setScreen(new ChatScreen(var6, false));
                     }
                  }
               }
            }
         }
      }
   }

   public boolean method47() {
      return !this.method33().isEmpty() && this.cloud.method7860();
   }

   public void method48(long var1) {
      String var3 = this.method33();
      if (!var3.isEmpty()) {
         String var4 = mc.world == null ? "" : ChatCompose.mode();
         this.method18().method1177(var1, var3, var4, this.cloud.method7911(), "");
         method53(String.format(method54("cloud.join.toast.sent"), this.method18().method1149(var1)));
      }
   }

   public boolean method49(String var1, String var2, int var3) {
      if (mc.player != null && this.method18().method1136()) {
         this.method18()
            .method1160(
               0L,
               this.cloud.method7911(),
               this.cloud.method7913(),
               Math.floor(mc.player.getX()) + 0.5,
               Math.floor(mc.player.getY()),
               Math.floor(mc.player.getZ()) + 0.5,
               var1,
               var2,
               var3
            );
         return true;
      } else {
         return false;
      }
   }

   public static String method50(String var0) {
      return method54("cloud.presence." + (var0 != null && !var0.isEmpty() ? var0 : "online"));
   }

   public static ZenithColor method51(String var0) {
      String var1 = var0 == null ? "" : var0;

      return switch (var1) {
         case "pvp" -> new ZenithColor(-495247);
         case "farm" -> new ZenithColor(-11870592);
         case "build" -> new ZenithColor(-278748);
         case "trade" -> new ZenithColor(-13058568);
         case "afk" -> new ZenithColor(-7035976);
         case "dnd" -> new ZenithColor(-1096636);
         case "menu" -> new ZenithColor(-5796870);
         default -> new ZenithColor(-14498466);
      };
   }

   public static String method52(PartyMember.Data2 var0) {
      String var1 = method50(var0.getText());
      return var0.text().isEmpty() ? var1 : var1 + " · " + var0.text();
   }

   private static void method53(String var0) {
      Cloud.method7914(var0);
   }

   public static String method54(String var0) {
      Zenith var1 = Zenith.getInstance();
      return var1 == null ? var0 : var1.getLocalization().translate(var0);
   }

   private static final class Data {
      private final String text;
      private final String text2;
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final List<PartyMember.Data11> items;
      private final int intValue;
      private final long longValue;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public List<PartyMember.Data11> items() {
         return this.items;
      }

      public int intValue() {
         return this.intValue;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, doubleValue, doubleValue2, doubleValue3, items, intValue, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "items=" + this.items + ", " + "intValue=" + this.intValue + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(String var1, String var2, double var3, double var5, double var7, List<PartyMember.Data11> var9, int var10, long var11) {
         this.text = var1;
         this.text2 = var2;
         this.doubleValue = var3;
         this.doubleValue2 = var5;
         this.doubleValue3 = var7;
         this.items = var9;
         this.intValue = var10;
         this.longValue = var11;
      }

      public String server() {
         return this.text;
      }

      public String dimension() {
         return this.text2;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public List<PartyMember.Data11> getItems() {
         return this.items;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long time() {
         return this.longValue;
      }
   }
}
