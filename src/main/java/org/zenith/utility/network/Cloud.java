package org.zenith.utility.network;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.MinecraftAccess;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.client.events.CloudStateEvent;
import org.zenith.client.events.CloudWaypointEvent;
import org.zenith.client.events.Event11;
import org.zenith.client.events.PartyUpdateEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.utility.api.CloudJsonIo;
import org.zenith.utility.api.CloudTicket;
import org.zenith.utility.api.CloudWebSocket;
import org.zenith.utility.api.Cosmetics;
import org.zenith.utility.api.Friends;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.api.Prefs;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.api.Util18;
import org.zenith.utility.misc.CloudConnectionState;
import org.zenith.utility.misc.Data1;
import org.zenith.utility.misc.HudHolder;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.ShareCodec;
import org.zenith.utility.misc.StoreEntry;

public final class Cloud implements MinecraftAccess, Friends.Handler {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cloud");
   private static final String text = "nexus-cloud.json";
   private static final int CONST_intValue = 200;
   private static final int CONST_intValue2 = 2;
   private static final String text2 = System.getProperty("nexus.cloud.secret", "bJjl3_Q0sUH26wj4XWcgGeWjXY9nwEoPcgOFjBsC-r-SUcXWY66UDDmB_D7YqhJj");
   private static final int CONST_intValue3 = 1;
   private static final int CONST_intValue4 = 2;
   private static final int CONST_intValue5 = 3;
   private static final int CONST_intValue6 = 4;
   private static final int CONST_intValue7 = 5;
   private static final int CONST_intValue8 = 6;
   private static final long[] CONST_longValues = new long[]{2000L, 4000L, 8000L, 15000L, 30000L};
   private static final long CONST_longValue = 45000L;
   private static final long CONST_longValue2 = 12000L;
   private static final long CONST_longValue3 = 300000L;
   private static final int CONST_intValue9 = 3;
   private static final long CONST_longValue4 = 600000L;
   private static final long[] CONST_longValues2 = new long[]{60000L, 120000L, 300000L, 600000L};
   private static final int CONST_intValue10 = 6;
   private static final int CONST_intValue11 = 3;
   private static final long CONST_longValue5 = 2000L;
   private static final long CONST_longValue6 = 15000L;
   private static final long CONST_longValue7 = 15000L;
   private final Path path;
   private final Friends friends;
   private final Prefs prefs;
   private final CloudConnectionState cloudConnectionState = new CloudConnectionState();
   private final PartyMember partyMember;
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private String text3 = "";
   private volatile String url = RegionUtil.method2040().get(0);
   private volatile String text4;
   private int intValue;
   private volatile boolean flag;
   private volatile String text5;
   private volatile CloudWebSocket cloudWebSocket;
   private volatile long longValue;
   private volatile String text6 = "";
   private boolean flag2;
   private int intValue2;
   private long longValue2;
   private int intValue3;
   private long longValue3 = CONST_longValues[0];
   private int intValue4;
   private int intValue5;
   private long longValue4;
   private final Object obj = new Object();
   private volatile int intValue6;
   private volatile Boolean booleanValue;
   private volatile boolean flag3;
   private volatile Cloud.Handler handler;
   private final Set<Long> items = new HashSet<>();
   private long longValue5;
   private volatile Cloud.Data data;
   private volatile Cloud.Data data2;
   private volatile Cloud.Data data3;

   public Cloud(Path var1, Friends var2) {
      this.path = var1.resolve("nexus-cloud.json");
      this.friends = var2;
      this.prefs = new Prefs(var1);
      this.partyMember = new PartyMember(this, var1);
      this.load();
      var2.setHandler(this);
      EventManager.register(this);
      RegionUtil.method2042(() -> mc.execute(() -> {
         if (!this.cloudConnectionState.method8035()) {
            this.text4 = null;
            this.intValue = 0;
            if (this.cloudWebSocket == null) {
               this.url = this.method7869();
            }
         }
      }));
   }

   public CloudConnectionState state() {
      return this.cloudConnectionState;
   }

   public Friends getFriends() {
      return this.friends;
   }

   public Prefs prefs() {
      return this.prefs;
   }

   public PartyMember getPartyMember() {
      return this.partyMember;
   }

   public boolean method7860() {
      return this.cloudConnectionState.method8035() && (this.intValue6 & 2) != 0;
   }

   public void method7861(int var1, JsonObject var2) {
      if (this.method7860()) {
         ByteBuffer var3;
         try {
            var3 = CloudPackets.method1799(var1, var2.toString());
         } catch (RuntimeException var5) {
            logger.warn("Облако: соц-кадр {} не отправлен: {}", var1, CloudWebSocket.method1956(var5));
            return;
         }

         this.method7896(var3, 0);
      }
   }

   public boolean method7862() {
      return this.cloudConnectionState.method8035();
   }

   public boolean method7863() {
      return this.cloudConnectionState.method8035() && (this.intValue6 & 1) != 0;
   }

   public int method7864() {
      CloudWebSocket var1 = this.cloudWebSocket;
      return var1 != null && this.cloudConnectionState.method8035() ? var1.getIntValue() : -1;
   }

   public void method7865() {
      CloudWebSocket var1 = this.cloudWebSocket;
      if (var1 != null && this.cloudConnectionState.method8035()) {
         var1.ping();
      }
   }

   public void setHandler(Cloud.Handler var1) {
      this.handler = var1;
   }

   public String url() {
      return this.url;
   }

   public boolean method7866() {
      return !this.text3.isEmpty();
   }

   public void method7867(String var1) {
      String var2 = var1.trim();

      while (var2.endsWith("/")) {
         var2 = var2.substring(0, var2.length() - 1);
      }

      if (!var2.isEmpty() && !var2.endsWith("/ws")) {
         int var3 = var2.indexOf(47, var2.indexOf("//") + 2);
         var2 = (var3 < 0 ? var2 : var2.substring(0, var3)) + "/ws";
      }

      this.text3 = var2;
      this.text5 = null;
      this.url = this.method7869();
      this.save();
   }

   public void method7868() {
      this.text3 = "";
      this.text5 = null;
      this.url = this.method7869();
      this.save();
   }

   private String method7869() {
      if (!this.text3.isEmpty()) {
         return this.text3;
      } else {
         var var1 = RegionUtil.method2040();
         String var2 = this.text4;
         return var2 != null && var1.contains(var2) ? var2 : (String)var1.get(0);
      }
   }

   public String method7870() {
      if (!this.text3.isEmpty()) {
         return this.text3;
      } else {
         String var1 = this.text5;
         return var1 != null && RegionUtil.method2040().contains(var1) ? var1 : RegionUtil.method2040().get(0);
      }
   }

   public long getLongValue() {
      return this.longValue;
   }

   public String method7871() {
      return mc.getSession() == null ? "" : mc.getSession().getUsername();
   }

   public static long accountUid() {
      Zenith var0 = Zenith.getInstance();
      if (var0 != null && var0.getEntry() != null) {
         String var1 = var0.getEntry().getText2();
         if (var1 != null && !var1.isBlank()) {
            try {
               return Long.parseLong(var1.trim());
            } catch (NumberFormatException var3) {
               return 0L;
            }
         } else {
            return 0L;
         }
      } else {
         return 0L;
      }
   }

   public static String accountName() {
      Zenith var0 = Zenith.getInstance();
      if (var0 != null && var0.getEntry() != null) {
         String var1 = var0.getEntry().getUsername();
         return var1 == null ? "" : var1;
      } else {
         return "";
      }
   }

   public static String getText2() {
      return text2;
   }

   public void method7872() {
      this.flag2 = true;
      if (accountUid() <= 0L) {
         logger.info("Облако: аккаунт ещё не готов, подключение отложено");
         this.intValue2 = 1;
         this.method7873();
      } else {
         this.open();
      }
   }

   private void method7873() {
      int var1 = Math.max(0, Math.min(this.intValue2, CONST_longValues.length) - 1);
      long var2 = this.longValue2 > 0L ? this.longValue2 : CONST_longValues[var1];
      this.longValue3 = method7874(var2);
      this.stopWatch.reset();
   }

   public static long method7874(long var0) {
      double var2 = 0.8 + ThreadLocalRandom.current().nextDouble() * 0.4;
      return Math.max(250L, (long)(var0 * var2));
   }

   private void method7875() {
      this.intValue5++;
      String var1 = method7876();
      if (this.intValue5 >= 3) {
         logger.warn("Облако: uid аккаунта не распознан: «{}» — подключение остановлено", var1);
         this.flag2 = false;
         this.intValue2 = 0;
         this.cloudConnectionState.method8037("uid аккаунта не распознан: " + (var1.isEmpty() ? "пусто" : var1));
         this.method7882(CloudConnectionState.Type.FAILED);
      } else {
         logger.debug("Облако: аккаунт ещё не готов (попытка {})", this.intValue5);
         this.intValue2++;
         this.method7873();
      }
   }

   private static String method7876() {
      Zenith var0 = Zenith.getInstance();
      if (var0 != null && var0.getEntry() != null && var0.getEntry().getText2() != null) {
         String var1 = var0.getEntry().getText2().trim();
         return var1.length() > 40 ? var1.substring(0, 40) + "…" : var1;
      } else {
         return "";
      }
   }

   public void connect() {
      if (this.cloudConnectionState.getType() != CloudConnectionState.Type.CONNECTING && !this.cloudConnectionState.method8035()) {
         this.intValue5 = 0;
         this.intValue4 = 0;
         if (accountUid() <= 0L) {
            this.flag2 = true;
            this.longValue2 = 0L;
            this.intValue3 = 0;
            this.intValue2 = 1;
            this.method7873();
            this.cloudConnectionState.method8037("аккаунт ещё не готов — подключусь сам");
         } else {
            this.flag2 = true;
            this.intValue2 = 0;
            this.longValue2 = 0L;
            this.intValue3 = 0;
            this.open();
         }
      }
   }

   public void disconnect() {
      this.flag2 = false;
      this.intValue2 = 0;
      this.method7877("клиент отключился");
      this.cloudConnectionState.method8037("отключено вручную");
      this.method7882(CloudConnectionState.Type.OFFLINE);
   }

   public void shutdown() {
      this.prefs.method3325();
      this.flag2 = false;
      this.intValue2 = 0;
      EventManager.unregister(this);
      this.method7877("клиент закрывается");
   }

   private void open() {
      if (accountUid() <= 0L) {
         this.method7875();
      } else {
         this.intValue5 = 0;
         CloudTicket.method1374();
         this.cloudConnectionState.method8036("");
         this.url = this.method7869();
         this.flag = false;

         URI var1;
         try {
            var1 = new URI(this.url);
         } catch (URISyntaxException var3) {
            logger.error("Облако: неверный адрес {}", this.url);
            this.flag2 = false;
            this.intValue2 = 0;
            this.cloudConnectionState.method8037("неверный адрес узла: " + this.url);
            this.method7882(CloudConnectionState.Type.FAILED);
            return;
         }

         this.method7877("переподключение");
         this.method7882(CloudConnectionState.Type.CONNECTING);
         this.stopWatch2.reset();
         logger.info("Облако: подключение к {} как {}#{} ({}), протокол {}", new Object[]{this.url, accountName(), accountUid(), this.method7871(), 5});
         CloudWebSocket var2 = new CloudWebSocket(var1, this::method7883, () -> logger.debug("Облако: сокет открыт, ждём CHALLENGE"), this::method7878);
         this.cloudWebSocket = var2;
         var2.connect();
      }
   }

   private void method7877(String var1) {
      this.prefs.method3308();
      CloudWebSocket var2;
      synchronized (this.obj) {
         var2 = this.cloudWebSocket;
         this.cloudWebSocket = null;
         this.longValue = 0L;
         this.intValue6 = 0;
         this.flag3 = false;
         this.cloudConnectionState.method8064();
      }

      if (var2 != null) {
         var2.method1954(var1);
      }

      this.partyMember.method1151();
   }

   @EventTarget
   private void onTick(TickEvent var1) {
      CloudWebSocket var2 = this.cloudWebSocket;
      if (var2 != null && this.cloudConnectionState.method8035() && var2.method1947() > 45000L) {
         var2.method1950("узел не отвечает 45 с");
      } else if (var2 != null && this.cloudConnectionState.getType() == CloudConnectionState.Type.CONNECTING && this.stopWatch2.method7546(15000L)) {
         var2.method1950("узел не ответил за 15 с");
      } else {
         this.prefs.flush();
         if (var2 != null && this.cloudConnectionState.method8035() && (var2.method1948() > 12000L || var2.method1947() > 12000L && var2.method1949() > 3000L)) {
            var2.ping();
         }

         String var3 = this.method7871();
         if (this.cloudConnectionState.method8035() && !var3.isEmpty() && !this.text6.equals(var3)) {
            logger.info("Облако: ник сменился {} → {}, перезаход", this.text6, var3);
            this.open();
         } else {
            if (this.intValue3 > 0
               && this.cloudConnectionState.method8035()
               && this.longValue4 != 0L
               && System.currentTimeMillis() - this.longValue4 >= 600000L) {
               this.intValue3 = 0;
            }

            if (this.intValue2 != 0 && this.flag2) {
               if (var2 == null || this.cloudConnectionState.getType() != CloudConnectionState.Type.CONNECTING) {
                  if (this.stopWatch.method7546(this.longValue3)) {
                     this.longValue2 = 0L;
                     this.stopWatch.reset();
                     logger.info("Облако: повтор подключения #{} к {}", this.intValue2, this.method7869());
                     this.open();
                  }
               }
            }
         }
      }
   }

   private void method7878(CloudWebSocket var1, String var2) {
      mc.execute(() -> {
         synchronized (this.obj) {
            if (var1 != this.cloudWebSocket) {
               logger.debug("Облако: старое соединение закрылось ({})", var2);
               return;
            }

            this.longValue = 0L;
            this.intValue6 = 0;
            this.cloudConnectionState.method8064();
            if (this.flag3) {
               this.flag3 = false;
               this.booleanValue = Boolean.FALSE;
               logger.info("Облако: узел оборвал вход с билетом сайта — следующий вход без билета");
            }
         }

         this.longValue4 = 0L;
         this.partyMember.method1151();
         String var6 = this.cloudConnectionState.getLastError();
         this.method7882(var6.isEmpty() ? CloudConnectionState.Type.OFFLINE : CloudConnectionState.Type.FAILED);
         logger.info("Облако: {}", var2);
         if (this.flag2 && this.longValue2 == 0L) {
            this.cloudConnectionState.method8037(var6.isEmpty() ? var2 : var6);
         }

         if (!this.flag && var6.isEmpty() && this.longValue2 == 0L) {
            this.method7879();
         }

         if (this.flag2) {
            this.intValue2++;
            this.method7873();
         }
      });
   }

   private void method7879() {
      if (this.text3.isEmpty() && ++this.intValue >= 2) {
         this.intValue = 0;
         var var1 = RegionUtil.method2040();
         int var2 = var1.indexOf(this.url);
         this.text4 = (String)var1.get(var2 < 0 ? 0 : (var2 + 1) % var1.size());
         if (!this.text4.equals(this.url)) {
            logger.info("Облако: {} не пускает {} раза подряд — дальше {}", new Object[]{this.url, 2, this.text4});
         }
      }
   }

   private void method7880() {
      this.intValue3++;
      if (this.intValue3 >= 3) {
         this.flag2 = false;
         this.longValue2 = 0L;
         this.cloudConnectionState.method8037("под аккаунтом играет второй клиент — закрой его и нажми «Переподключиться»");
         this.method7882(CloudConnectionState.Type.FAILED);
         logger.warn("Облако: второй клиент под тем же аккаунтом, автоповтор выключен");
         method7914("Облако выключено: под аккаунтом играет второй клиент");
      } else {
         this.longValue2 = 300000L;
         this.cloudConnectionState.method8037("под аккаунтом зашли с другого клиента — вернусь через 5 мин");
         logger.info("Облако: выбиты вторым клиентом, повтор через {} мин", 5L);
      }
   }

   private void method7881(String var1) {
      CloudTicket.invalidate();
      this.intValue4++;
      if (this.intValue4 >= 6) {
         this.flag2 = false;
         this.longValue2 = 0L;
         this.cloudConnectionState.method8037("узел не пускает: " + var1 + " — нажми «Переподключиться»");
         this.method7882(CloudConnectionState.Type.FAILED);
         logger.warn("Облако: {} отказов во входе подряд, автоповтор выключен", this.intValue4);
      } else {
         long var2 = CONST_longValues2[Math.min(this.intValue4, CONST_longValues2.length) - 1];
         this.longValue2 = var2;
         this.cloudConnectionState.method8037("узел не пустил: " + var1 + " — повтор через " + var2 / 60000L + " мин");
         logger.info("Облако: отказ во входе #{}, повтор через {} мин", this.intValue4, var2 / 60000L);
      }
   }

   private void method7882(CloudConnectionState.Type var1) {
      this.cloudConnectionState.setType(var1);
      if (var1 == CloudConnectionState.Type.ONLINE) {
         this.cloudConnectionState.method8036("");
         this.cloudConnectionState.method8037("");
      }

      EventManager.call(new CloudStateEvent(var1));
   }

   private void method7883(CloudWebSocket var1, ByteBuffer var2) {
      if (var1 == this.cloudWebSocket) {
         PacketByteBuf var3 = CloudPackets.method1818(var2);
         if (var3.isReadable()) {
            short var4 = var3.readUnsignedByte();
            if ((var4 & 128) != 0 && (var4 & 'ｿ') >= 16) {
               try {
                  this.method7884(var1, var4, var3);
               } catch (RuntimeException var7) {
                  logger.debug("Облако: битый кадр 0x{} пропущен: {}", Integer.toHexString(var4), CloudWebSocket.method1956(var7));
               }
            } else {
               synchronized (this.obj) {
                  if (var1 == this.cloudWebSocket) {
                     this.method7886(var1, var4, var3);
                  }
               }
            }
         }
      }
   }

   private void method7884(CloudWebSocket var1, int var2, PacketByteBuf var3) {
      if (var2 == 163) {
         this.method7885(CloudPackets.method1802(var3));
      } else if (var2 == 165) {
         CloudPackets.Data18 var7 = CloudPackets.method1804(var3);
         mc.execute(() -> {
            if (var1 == this.cloudWebSocket) {
               this.partyMember.method1180(var7);
            }
         });
      } else {
         synchronized (this.obj) {
            if (var1 == this.cloudWebSocket) {
               this.method7886(var1, var2, var3);
            }
         }
      }
   }

   private void method7885(CloudPackets.Data16 var1) {
      PartyPlayerInfo var2 = this.cloudConnectionState.method8040(var1.uid());
      Cloud.Handler var3 = this.handler;
      boolean var4 = var2 != null ? !this.prefs.method3316(var2.username()) : this.partyMember.method1148(var1.uid());
      if (var4 && var3 != null) {
         try {
            var3.onVoice(var1);
         } catch (LinkageError | RuntimeException var6) {
            logger.debug("Облако: голос не принят: {}", var6.toString());
         }
      }
   }

   private void method7886(CloudWebSocket var1, int var2, PacketByteBuf var3) {
      switch (var2) {
         case 128:
            this.method7887(var1, CloudPackets.method1800(var3));
            break;
         case 129:
            this.method7889(CloudPackets.method1801(var3));
            break;
         case 130:
            CloudPackets.Data var14 = CloudPackets.method1805(var3);
            this.cloudConnectionState.method8036(var14.getText());
            this.flag3 = false;
            if (var14.getIntValue() == 2 && Boolean.FALSE.equals(this.booleanValue)) {
               this.booleanValue = null;
            }

            if (var14.getIntValue() == 1) {
               mc.execute(() -> {
                  this.flag2 = false;
                  this.cloudConnectionState.method8037(var14.getText());
               });
            } else if (var14.getIntValue() == 2) {
               mc.execute(() -> this.method7881(var14.getText()));
            } else if (var14.getIntValue() == 3) {
               mc.execute(this::method7880);
            }

            logger.warn("Облако: отказ {} — {}", var14.getIntValue(), var14.getText());
            break;
         case 131:
            this.method7891(CloudPackets.method1806(var3));
            break;
         case 132:
            this.method7892(CloudPackets.method1807(var3));
            break;
         case 133:
         case 134:
            this.method7893(CloudPackets.method1806(var3));
            break;
         case 135:
            this.method7894(CloudPackets.method1808(var3));
            break;
         case 136:
            this.method7895(CloudPackets.method1809(var3));
            break;
         case 137:
         case 138:
         case 139:
         case 140:
         case 141:
         case 142:
         case 143:
         case 149:
         case 150:
         case 151:
         case 152:
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 159:
         case 163:
         default:
            logger.debug("Облако: кадр 0x{} не обрабатывается", Integer.toHexString(var2));
            break;
         case 144:
            CloudPackets.Data5 var13 = CloudPackets.method1810(var3);
            PartyPlayerInfo var19 = this.cloudConnectionState.method8040(var13.uid());
            if (var19 != null) {
               var19.method3295(var13);
            }
            break;
         case 145:
            CloudPackets.Data2 var12 = CloudPackets.method1811(var3);
            PartyPlayerInfo var18 = this.cloudConnectionState.method8040(var12.uid());
            if (var18 != null) {
               var18.method3296(var12);
            }
            break;
         case 146:
            CloudPackets.Data3 var11 = CloudPackets.method1812(var3);
            PartyPlayerInfo var17 = this.cloudConnectionState.method8040(var11.uid());
            if (var17 != null) {
               var17.method3297(var11);
            }
            break;
         case 147:
            CloudPackets.Data11 var10 = CloudPackets.method1813(var3);
            PartyPlayerInfo var16 = this.cloudConnectionState.method8040(var10.uid());
            if (var16 != null) {
               var16.method3298(var10);
            }
            break;
         case 148:
            CloudPackets.Data15 var9 = CloudPackets.method1814(var3);
            this.cloudConnectionState.method8059(var9.uid(), var9.getText(), var9.getItems());
            PartyPlayerInfo var15 = this.cloudConnectionState.method8040(var9.uid());
            if (var15 != null) {
               var15.method3299(var9);
            }

            mc.execute(() -> this.method7888(var9));
            break;
         case 160:
            CloudPackets.Data13 var8 = CloudPackets.method1815(var3);
            mc.execute(() -> {
               this.cloudConnectionState.method8050(var8);
               EventManager.call(new Event11(var8, var8.uid() == this.longValue));
            });
            break;
         case 161:
            CloudPackets.Data10 var7 = CloudPackets.method1817(var3);
            mc.execute(() -> EventManager.call(new CloudWaypointEvent(var7, var7.uid() == this.longValue)));
            break;
         case 162:
            long var6 = CloudPackets.method1816(var3);
            mc.execute(() -> this.cloudConnectionState.method8051(var6));
            break;
         case 164:
            CloudPackets.Data6 var4 = CloudPackets.method1803(var3);
            PartyPlayerInfo var5 = this.cloudConnectionState.method8040(var4.uid());
            if (var5 != null) {
               var5.setIntValue2(var4.getIntValue());
            }
      }
   }

   private void method7887(CloudWebSocket var1, CloudPackets.Data4 var2) {
      if (var2.protocol() != 5) {
         String var12 = var2.protocol() > 5
            ? "узел новее клиента (протокол " + var2.protocol() + " против 5) — обнови сборку"
            : "узел старее клиента (протокол " + var2.protocol() + " против 5) — обнови узел";
         this.cloudConnectionState.method8036(var12);
         logger.error("Облако: {}", var12);
         mc.execute(() -> {
            this.flag2 = false;
            this.cloudConnectionState.method8037(var12);
            if (this.cloudWebSocket == var1) {
               this.method7877("протокол");
            }

            this.method7882(CloudConnectionState.Type.FAILED);
            method7914("Облако: " + var12);
         });
         var1.method1954("протокол");
      } else {
         long var3 = accountUid();
         String var5 = accountName();
         String var6 = this.method7871();
         UUID var7 = mc.getSession() == null ? null : mc.getSession().getUuidOrNull();
         byte[] var8 = method7915(var3, var5, var6, var2.getText());
         String var9 = var7 == null ? "" : var7.toString();
         this.text6 = var6;
         var var10 = CloudTicket.method1374().completeOnTimeout(null, 2000L, TimeUnit.MILLISECONDS).exceptionally(var0 -> null);
         CompletableFuture var11;
         if (!var2.isFlag()) {
            var11 = CompletableFuture.completedFuture(null);
         } else {
            var11 = CompletableFuture.runAsync(() -> {
               try {
                  mc.getApiServices().sessionService().joinServer(var7, mc.getSession().getAccessToken(), var2.getText());
               } catch (Exception var3x) {
                  logger.warn("Облако: Mojang не подтвердил сессию: {}", CloudWebSocket.method1956(var3x));
               }
            }, Util.getIoWorkerExecutor()).exceptionally(var0 -> null);
         }

         var10.<Void, String>thenCombine(var11, (var0, var1x) -> (String)var0).whenComplete((var8x, var9x) -> {
            if (var1 == this.cloudWebSocket) {
               String var10x = var9x == null && !Boolean.FALSE.equals(this.booleanValue) ? (String)var8x : null;

               ByteBuffer var11x;
               try {
                  var11x = CloudPackets.method1783(var3, var5, var6, var9, "3.5", var8, var10x);
               } catch (RuntimeException var14) {
                  String var13 = "вход не собран: " + CloudWebSocket.method1956(var14);
                  logger.error("Облако: {}", var13);
                  this.cloudConnectionState.method8036(var13);
                  var1.method1950(var13);
                  return;
               }

               this.flag3 = var10x != null;
               logger.debug("Облако: HELLO {}", var10x != null ? "с билетом сайта" : "без билета");
               var1.method1951(var11x, 0);
            }
         });
      }
   }

   private void method7888(CloudPackets.Data15 var1) {
      Zenith var2 = Zenith.getInstance();
      Cosmetics var3 = var2 == null ? null : var2.getCosmetics();
      if (var3 != null && var1.getText() != null && !var1.getText().isBlank()) {
         List<Data1> var4 = new ArrayList<>(var1.getItems().size());

         for (CloudPackets.Data9 var6 : var1.getItems()) {
            if (var6.getText() != null && !var6.getText().isBlank() && !ShareCodec.method6723(var6.getText())) {
               var4.add(new Data1(var6.getText(), var6.method1822().name(), var6.getIntValue2()));
            }
         }

         var3.method2962(new StoreEntry(Long.toString(var1.uid()), var1.getText(), "PUBLIC", 0, System.currentTimeMillis(), var4), true);
      }
   }

   private void method7889(CloudPackets.Data12 var1) {
      String var2 = this.url;
      this.flag = true;
      this.text5 = var2;
      if (this.text3.isEmpty()) {
         this.text4 = var2;
      }

      this.longValue = var1.uid();
      this.intValue6 = var1.getIntValue2();
      this.flag3 = false;
      this.booleanValue = (var1.getIntValue2() & 4) != 0;
      this.cloudConnectionState.setIntValue(var1.getIntValue());
      Util18.method4490(var1.getLongValue2());
      logger.info(
         "Облако: вошли как {}#{}, всего {} онлайн{}",
         new Object[]{var1.username(), var1.uid(), var1.getIntValue(), (var1.getIntValue2() & 4) != 0 ? ", узел проверяет билеты сайта" : ""}
      );
      mc.execute(() -> {
         this.intValue2 = 0;
         this.intValue = 0;
         this.longValue2 = 0L;
         this.intValue4 = 0;
         this.intValue5 = 0;
         long var1x = System.currentTimeMillis();
         this.longValue4 = var1x;
         this.longValue5 = var1x;
         this.method7882(CloudConnectionState.Type.ONLINE);
         this.method7896(CloudPackets.method1784(this.friends.all()), 0);
         this.method7890();
         this.partyMember.method1150();
      });
   }

   private void method7890() {
      StoreApi var1 = Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
      if (var1 != null) {
         var1.method1965(200).whenComplete((var1x, var2) -> {
            if (var2 != null) {
               logger.warn("Облако: история чата не пришла: {}", CloudWebSocket.method1956(var2));
            } else if (!var1x.isEmpty()) {
               List<CloudPackets.Data13> var3 = new ArrayList<>(var1x.size());

               for (StoreApi.Data3 var5 : var1x) {
                  var3.add(new CloudPackets.Data13(var5.getLongValue2(), var5.getText(), 0, var5.time(), var5.text(), var5.getLongValue()));
               }

               mc.execute(() -> this.cloudConnectionState.method8052(var3));
            }
         });
      }
   }

   private void method7891(CloudPackets.Data8 var1) {
      boolean var2 = this.cloudConnectionState.method8039(var1.uid()) == null;
      PartyPlayerInfo var3 = this.cloudConnectionState.method8046(var1);
      if (var2) {
         mc.execute(() -> {
            this.cloudConnectionState.method8060(var1.username());
            this.prefs.method3306(var3.username());
            this.prefs.method3312(var3.username(), var1.uid());
            this.prefs.method3314(var3.username(), var3.getText2());
            boolean var3x = !this.items.add(var1.uid()) && System.currentTimeMillis() - this.longValue5 < 15000L;
            if (!var3x && !this.prefs.method3315(var3.username())) {
               method7914(var3.username() + " в сети");
            }

            EventManager.call(new PartyUpdateEvent(var3, true));
         });
      }
   }

   private void method7892(long var1) {
      PartyPlayerInfo var3 = this.cloudConnectionState.method8047(var1);
      if (var3 != null) {
         mc.execute(() -> {
            this.items.remove(var1);
            this.prefs.method3307(var3.username());
            this.prefs.save();
            if (!this.prefs.method3315(var3.username())) {
               method7914(var3.username() + " вышел");
            }

            EventManager.call(new PartyUpdateEvent(var3, false));
         });
      }
   }

   private void method7893(CloudPackets.Data8 var1) {
      mc.execute(() -> {
         if (!this.friends.method6442(var1.username())) {
            if (this.cloudConnectionState.method8057(var1)) {
               method7914(var1.username() + " (" + var1.getText2() + ") просится в друзья");
            }
         }
      });
   }

   private void method7894(List<CloudPackets.Data14> var1) {
      if (var1 == null) {
         logger.warn("Облако: негодный FRIEND_STATUS, список друзей оставлен как есть");
      } else {
         mc.execute(() -> {
            this.cloudConnectionState.method8063(var1);
            List<String> var2 = new ArrayList<>(var1.size());

            for (CloudPackets.Data14 var4 : var1) {
               var2.add(var4.username());
            }

            if (this.friends.method6444(var2)) {
               this.friends.save();
            }
         });
      }
   }

   private void method7895(CloudPackets.Data17 var1) {
      logger.info("Облако: {} — {}", var1.getText(), var1.getIntValue() == 1 ? "это ты сам" : "сейчас не в сети");
   }

   private void method7896(ByteBuffer var1, int var2) {
      CloudWebSocket var3 = this.cloudWebSocket;
      if (var3 != null && this.cloudConnectionState.method8035()) {
         var3.method1951(var1, var2);
      }
   }

   public void method7897(String var1, String var2) {
      this.method7896(CloudPackets.method1789(var1, var2), 4);
   }

   public void method7898(double var1, double var3, double var5, float var7, float var8) {
      this.method7896(CloudPackets.method1790(var1, var3, var5, var7, var8), 1);
   }

   public void method7899(float var1, float var2, float var3, int var4) {
      this.method7896(CloudPackets.method1791(var1, var2, var3, var4), 2);
   }

   public void method7900(List<CloudPackets.Data7> var1) {
      this.method7896(CloudPackets.method1792(var1), 3);
   }

   public void method7901(List<CloudPackets.Data9> var1) {
      this.method7896(CloudPackets.method1793(this.method7871(), var1), 5);
   }

   public void method7902(int var1, int var2, int var3, byte[] var4, int var5) {
      if (this.method7863()) {
         this.method7896(CloudPackets.method1797(var1, var2, var3, var4, var5), -1);
      }
   }

   public void method7903(int var1) {
      if (this.method7863()) {
         this.method7896(CloudPackets.method1798(var1), 6);
      }
   }

   public boolean method7904(int var1, String var2) {
      String var3 = var2.strip();
      if (var3.isEmpty() || var3.length() > 32) {
         return false;
      } else if (this.friends.isFull()) {
         return false;
      } else if (var1 == 0) {
         if (this.friends.method6442(var3)) {
            return false;
         } else {
            this.friends.add(var3);
            this.friends.save();
            return true;
         }
      } else if (!this.method7862()) {
         return false;
      } else {
         this.method7896(CloudPackets.method1785(var1, var3), 0);
         return true;
      }
   }

   public boolean method7905(String var1) {
      if (!this.cloudConnectionState.method8056()) {
         return false;
      } else if (this.friends.isFull() && !this.friends.method6442(var1)) {
         return false;
      } else {
         this.cloudConnectionState.method8060(var1);
         if (this.friends.add(var1)) {
            this.friends.save();
         }

         this.method7896(CloudPackets.method1787(var1), 0);
         return true;
      }
   }

   public boolean method7906(String var1) {
      this.cloudConnectionState.method8060(var1);
      this.method7896(CloudPackets.method1788(var1), 0);
      return true;
   }

   public boolean method7907(String var1) {
      boolean var2 = this.friends.method6443(var1);
      if (var2) {
         this.friends.save();
      }

      return var2;
   }

   public boolean method7908(int var1, String var2) {
      String var3 = var2.strip();
      if (var3.isEmpty()) {
         return false;
      } else if (!this.method7862()) {
         return false;
      } else {
         if (var3.length() > 256) {
            var3 = var3.substring(0, 256);
         }

         this.method7896(CloudPackets.method1794(var1, var3), 0);
         return true;
      }
   }

   public void method7909(long var1) {
      if (var1 > 0L && this.method7862()) {
         this.method7896(CloudPackets.method1795(var1), 0);
      }
   }

   public void method7910(double var1, double var3, double var5, String var7, int var8, String var9) {
      if (this.method7862()) {
         String var10 = var7.length() > 32 ? var7.substring(0, 32) : var7;
         this.method7896(CloudPackets.method1796(this.method7911(), this.method7913(), var1, var3, var5, var10, var8, var9 == null ? "" : var9), 0);
      }
   }

   @Override
   public void method6445(String var1) {
      this.cloudConnectionState.method8060(var1);
      this.method7896(CloudPackets.method1785(0, var1), 0);
   }

   @Override
   public void method6446(String var1) {
      this.method7896(CloudPackets.method1786(var1), 0);
   }

   @Override
   public void method6447() {
      this.method7896(CloudPackets.method1784(this.friends.all()), 0);
   }

   public String method7911() {
      ServerInfo var1 = mc.getCurrentServerEntry();
      if (var1 != null && var1.isRealm()) {
         return "realms";
      } else {
         String var2 = var1 == null ? null : var1.address;
         if (var2 != null && !var2.isBlank()) {
            Cloud.Data var7 = this.data;
            if (var7 != null && var7.getObj() == var2) {
               return var7.value();
            } else {
               String var4 = var2.trim().toLowerCase(Locale.ROOT);
               if (var4.endsWith(":25565")) {
                  var4 = var4.substring(0, var4.length() - 6);
               }

               String var8 = method7912(var4);
               this.data = new Cloud.Data(var2, 0L, var8);
               return var8;
            }
         } else if (!mc.isInSingleplayer()) {
            return "";
         } else {
            long var3 = accountUid();
            Cloud.Data var5 = this.data2;
            if (var5 != null && var5.uid() == var3) {
               return var5.value();
            } else {
               String var6 = "singleplayer:" + var3;
               this.data2 = new Cloud.Data(null, var3, var6);
               return var6;
            }
         }
      }
   }

   public static String method7912(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         boolean var1 = true;

         for (int var2 = 0; var2 < var0.length(); var2++) {
            char var3 = var0.charAt(var2);
            if (var3 != '.' && (var3 < '0' || var3 > '9')) {
               var1 = false;
               break;
            }
         }

         if (var1) {
            return var0;
         } else {
            int var4 = var0.lastIndexOf(46);
            if (var4 <= 0) {
               return var0;
            } else {
               int var5 = var0.lastIndexOf(46, var4 - 1);
               return var5 < 0 ? var0 : var0.substring(var5 + 1);
            }
         }
      } else {
         return "";
      }
   }

   public String method7913() {
      ClientWorld var1 = mc.world;
      if (var1 == null) {
         return "";
      } else {
         RegistryKey var2 = var1.getRegistryKey();
         Cloud.Data var3 = this.data3;
         if (var3 != null && var3.getObj() == var2) {
            return var3.value();
         } else {
            String var4 = var2.getValue().toString();
            this.data3 = new Cloud.Data(var2, 0L, var4);
            return var4;
         }
      }
   }

   public static void method7914(String var0) {
      Zenith var1 = Zenith.getInstance();
      HudHolder var2 = var1 == null ? null : var1.getHudHolder();
      if (var2 != null) {
         var2.method6972("0", Text.literal(" " + var0));
      }
   }

   private static byte[] method7915(long var0, String var2, String var3, String var4) {
      try {
         Mac var5 = Mac.getInstance("HmacSHA256");
         var5.init(new SecretKeySpec(text2.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
         return var5.doFinal((var0 + "\n" + var2 + "\n" + var3 + "\n" + var4).getBytes(StandardCharsets.UTF_8));
      } catch (GeneralSecurityException var6) {
         throw new IllegalStateException("HmacSHA256 недоступен", var6);
      }
   }

   private void load() {
      JsonObject var1 = CloudJsonIo.method5992(this.path);

      try {
         if (var1 != null && var1.has("url") && var1.has("custom") && var1.get("custom").getAsBoolean()) {
            this.method7867(var1.get("url").getAsString());
         }
      } catch (RuntimeException var3) {
         logger.warn("Облако: {} испорчен, адрес — встроенный: {}", "nexus-cloud.json", CloudWebSocket.method1956(var3));
      }
   }

   private void save() {
      JsonObject var1 = new JsonObject();
      if (!this.text3.isEmpty()) {
         var1.addProperty("url", this.text3);
         var1.addProperty("custom", true);
      }

      CloudJsonIo.method5993(this.path, var1);
   }

   private static final class Data {
      private final Object obj;
      private final long longValue;
      private final String text;

      public Object obj() {
         return this.obj;
      }

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.obj, other.obj) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(obj, longValue, text);
      }

      @Override
      public String toString() {
         return "Data[" + "obj=" + this.obj + ", " + "longValue=" + this.longValue + ", " + "text=" + this.text + "]";
      }

      private Data(Object var1, long var2, String var4) {
         this.obj = var1;
         this.longValue = var2;
         this.text = var4;
      }

      public Object getObj() {
         return this.obj;
      }

      public long uid() {
         return this.longValue;
      }

      public String value() {
         return this.text;
      }
   }

   public interface Handler {
      void onVoice(CloudPackets.Data16 var1);
   }
}
