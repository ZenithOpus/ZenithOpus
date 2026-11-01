package org.zenith;

import project.weye.Session;
import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.comand.CommandManager;
import org.zenith.base.filemanager.impl.way.WayManager;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.style.StyleManager;
import org.zenith.client.screens.override.main.MainMenuScreen;
import org.zenith.client.screens.override.onboarding.OnboardingScreen;
import org.zenith.client.screens.override.particle.MenuParticleRenderer;
import org.zenith.client.scripting.Storage;
import org.zenith.utility.api.Cosmetics;
import org.zenith.utility.api.HolyWorldEventsFetcher;
import org.zenith.utility.api.JointCodebook;
import org.zenith.utility.api.KitManager;
import org.zenith.utility.api.MacroManager;
import org.zenith.utility.api.Manager;
import org.zenith.utility.api.OnboardingUtil;
import org.zenith.utility.api.PetManager;
import org.zenith.utility.api.PlayTimeTracker;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.api.Share;
import org.zenith.utility.api.SiteLicenseService;
import org.zenith.utility.api.SortManager;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.api.Task;
import org.zenith.utility.api.View;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.config.FiguraManager;
import org.zenith.utility.discord.DiscordManager;
import org.zenith.utility.game.HolyWorldItems;
import org.zenith.utility.game.ServicesTicker;
import org.zenith.utility.misc.Accounts;
import org.zenith.utility.misc.ClientSounds;
import org.zenith.utility.misc.EmoteManager;
import org.zenith.utility.misc.Friends;
import org.zenith.utility.misc.HolyWorldServerTracker;
import org.zenith.utility.misc.HudHolder;
import org.zenith.utility.misc.Localization;
import org.zenith.utility.misc.ModuleManager;
import org.zenith.utility.misc.StaffName;
import org.zenith.utility.misc.TaskScheduler;
import org.zenith.utility.misc.TimedBlockPos;
import org.zenith.utility.network.ChatBridge;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.Names;
import org.zenith.utility.network.Proxies;
import org.zenith.utility.render.CrystalAnimation;
import org.zenith.utility.render.HeldItemAnimator;
import org.zenith.utility.render.HeldItemPoseController;
import org.zenith.utility.render.KawaseBlur;
import org.zenith.utility.render.PartyManager;
import org.zenith.utility.render.ServerDetector;
import org.zenith.utility.render.Voice;
import org.zenith.utility.render.WardrobePreview;

public final class Zenith {
   public static Zenith INSTANCE = null;
   public static final String text = "Zenith";
   public static final String text2 = "3.5";
   private static final String text3 = "Zenith".toLowerCase();
   public static final File file = new File(MinecraftClient.getInstance().runDirectory, "Zenith");
   private Localization localization;
   private ModuleManager moduleManager;
   private KitManager kitManager;
   private SortManager sortManager;
   private StyleManager styleManager;
   private ClientSounds clientSounds;
   private FiguraManager figuraManager;
   private EmoteManager emoteManager;
   private PetManager petManager;
   private NLMenuScreen nLMenuScreen;
   private DiscordManager discordManager;
   private MainMenuScreen mainMenuScreen;
   private MenuParticleRenderer menuParticleRenderer;
   private TaskScheduler taskScheduler;
   private Storage storage;
   private ServerDetector serverDetector;
   private Friends friends;
   private StaffName staffName;
   private WayManager wayManager;
   private MacroManager macroManager;
   private TimedBlockPos timedBlockPos;
   private JointCodebook jointCodebook;
   private RotationManager rotationManager;
   private HolyWorldEventsFetcher holyWorldEventsFetcher;
   private HolyWorldItems holyWorldItems;
   private HudHolder hudHolder;
   private CommandManager commandManager;
   private Names names;
   private ChatBridge chatBridge;
   private Manager manager;
   private HolyWorldServerTracker holyWorldServerTracker;
   private org.zenith.utility.api.Friends friends2;
   private Cloud cloud;
   private ServicesTicker servicesTicker;
   private PartyManager partyManager;
   private StoreApi storeApi;
   private Voice voice;
   private Cosmetics cosmetics;
   private Wardrobe wardrobe;
   private View view;
   private Share share;
   private Proxies proxies;
   private Accounts accounts;
   private PlayTimeTracker playTimeTracker;
   private final Zenith.Entry entry;
   private KawaseBlur kawaseBlur;

   public Zenith() {
      INSTANCE = this;
      this.entry = new Zenith.Entry();
      if (this.entry.method1504() != Zenith.Entry.Type.ADMIN) {
      }
   }

   public void init() {
      RegionUtil.method2045(file.toPath());
      this.localization = new Localization();
      this.wayManager = new WayManager();
      this.macroManager = new MacroManager();
      this.friends = new Friends();
      this.kitManager = new KitManager();
      this.sortManager = new SortManager();
      this.staffName = new StaffName();
      this.hudHolder = new HudHolder();
      this.serverDetector = new ServerDetector();
      this.holyWorldServerTracker = new HolyWorldServerTracker();
      this.styleManager = new StyleManager();
      this.moduleManager = new ModuleManager();
      this.jointCodebook = new JointCodebook();
      this.rotationManager = new RotationManager();
      this.holyWorldEventsFetcher = new HolyWorldEventsFetcher();
      this.holyWorldItems = new HolyWorldItems();
      this.commandManager = new CommandManager();
      this.timedBlockPos = new TimedBlockPos();
      this.taskScheduler = new TaskScheduler();
      this.emoteManager = new EmoteManager();
      this.figuraManager = new FiguraManager();
      this.petManager = new PetManager();
      this.nLMenuScreen = new NLMenuScreen();
      this.mainMenuScreen = new MainMenuScreen();
      this.menuParticleRenderer = new MenuParticleRenderer();
      this.clientSounds = new ClientSounds();
      this.share = new Share();
      this.share.load();
      this.proxies = new Proxies();
      this.accounts = new Accounts();
      this.playTimeTracker = new PlayTimeTracker();
      this.names = new Names();
      this.chatBridge = new ChatBridge();
      this.manager = new Manager();

      try {
         this.storage = new Storage();
      } catch (Throwable var2) {
         var2.printStackTrace();
      }

      HeadlessBots.loadPersistentState();
      this.kawaseBlur = new KawaseBlur();
      this.nLMenuScreen.initialize();
      this.friends2 = new org.zenith.utility.api.Friends(file.toPath());
      this.cloud = new Cloud(file.toPath(), this.friends2);
      this.storeApi = new StoreApi(this.cloud);
      this.cloud.method7872();
      SiteLicenseService.start();
      this.cosmetics = new Cosmetics(file.toPath());
      this.cosmetics.method2959();
      this.view = new View(this.cosmetics, file.toPath());
      this.servicesTicker = new ServicesTicker(this.cloud, this.cosmetics, this.share);
      this.partyManager = new PartyManager(this.cloud, this.share);
      this.voice = new Voice();
      this.wardrobe = new Wardrobe(file.toPath());
      new WardrobePreview();
      new HeldItemPoseController();
      new CrystalAnimation();
      new HeldItemAnimator();
      Task.method5982(file.toPath());
      this.discordManager = new DiscordManager();
      this.discordManager.init();
   }

   public void shutdown() {
      if (this.storage != null) {
         try {
            this.storage.shutdown();
         } catch (Throwable var16) {
         }
      }

      if (this.cosmetics != null) {
         try {
            this.cosmetics.shutdown();
         } catch (Throwable var15) {
            method1500("cosmeticManager", var15);
         }
      }

      if (this.voice != null) {
         try {
            this.voice.shutdown();
         } catch (Throwable var14) {
            method1500("voiceManager", var14);
         }
      }

      if (this.cloud != null) {
         try {
            this.cloud.shutdown();
         } catch (Throwable var13) {
            method1500("cloudManager", var13);
         }
      }

      if (this.friends2 != null) {
         try {
            this.friends2.save();
         } catch (Throwable var12) {
            method1500("cloudFriends", var12);
         }
      }

      if (this.emoteManager != null) {
         try {
            this.emoteManager.close();
         } catch (Throwable var11) {
            method1500("emoteManager", var11);
         }
      }

      try {
         this.friends.save();
      } catch (Throwable var10) {
         method1500("friendManager", var10);
      }

      try {
         this.wayManager.save();
      } catch (Throwable var9) {
         method1500("wayManager", var9);
      }

      try {
         this.staffName.save();
      } catch (Throwable var8) {
         method1500("staffManager", var8);
      }

      try {
         this.styleManager.save();
      } catch (Throwable var7) {
         method1500("styleManager", var7);
      }

      try {
         this.playTimeTracker.save();
      } catch (Throwable var6) {
         method1500("playTimeManager", var6);
      }

      try {
         this.macroManager.save();
      } catch (Throwable var5) {
         method1500("macroManager", var5);
      }

      try {
         this.manager.save();
      } catch (Throwable var4) {
         method1500("configManager", var4);
      }

      try {
         HeadlessBots.savePersistentState();
      } catch (Throwable var3) {
         method1500("bots state", var3);
      }

      try {
         HeadlessBots.disconnectAll();
      } catch (Throwable var2) {
         method1500("bots disconnect", var2);
      }
   }

   private static void method1500(String var0, Throwable var1) {
      System.err.println("[Zenith] shutdown: " + var0 + " failed");
      var1.printStackTrace();
   }

   public static Identifier method1501(String var0) {
      return Identifier.of(text3, var0);
   }

   public static boolean method1502() {
      return System.getProperty("DevMode") != null;
   }

   public static Zenith getInstance() {
      return INSTANCE;
   }

   public Screen method1503() {
      return (Screen)(OnboardingUtil.method2705() ? this.mainMenuScreen : new OnboardingScreen());
   }

   public HolyWorldServerTracker getHolyWorldServerTracker() {
      return this.holyWorldServerTracker;
   }

   @Generated
   public Localization getLocalization() {
      return this.localization;
   }

   @Generated
   public ModuleManager getModuleManager() {
      return this.moduleManager;
   }

   @Generated
   public KitManager getKitManager() {
      return this.kitManager;
   }

   @Generated
   public SortManager getSortManager() {
      return this.sortManager;
   }

   @Generated
   public StyleManager getStyleManager() {
      return this.styleManager;
   }

   @Generated
   public ClientSounds getClientSounds() {
      return this.clientSounds;
   }

   @Generated
   public FiguraManager getFiguraManager() {
      return this.figuraManager;
   }

   @Generated
   public EmoteManager getEmoteManager() {
      return this.emoteManager;
   }

   @Generated
   public PetManager getPetManager() {
      return this.petManager;
   }

   @Generated
   public NLMenuScreen getNLMenuScreen() {
      return this.nLMenuScreen;
   }

   @Generated
   public DiscordManager getDiscordManager() {
      return this.discordManager;
   }

   @Generated
   public MainMenuScreen getMainMenuScreen() {
      return this.mainMenuScreen;
   }

   @Generated
   public MenuParticleRenderer getMenuParticleRenderer() {
      return this.menuParticleRenderer;
   }

   @Generated
   public TaskScheduler getTaskScheduler() {
      return this.taskScheduler;
   }

   @Generated
   public Storage getStorage() {
      return this.storage;
   }

   @Generated
   public ServerDetector getServerDetector() {
      return this.serverDetector;
   }

   @Generated
   public Friends getFriends() {
      return this.friends;
   }

   @Generated
   public StaffName getStaffName() {
      return this.staffName;
   }

   @Generated
   public WayManager getWayManager() {
      return this.wayManager;
   }

   @Generated
   public MacroManager getMacroManager() {
      return this.macroManager;
   }

   @Generated
   public TimedBlockPos getTimedBlockPos() {
      return this.timedBlockPos;
   }

   @Generated
   public JointCodebook getJointCodebook() {
      return this.jointCodebook;
   }

   @Generated
   public RotationManager getRotationManager() {
      return this.rotationManager;
   }

   @Generated
   public HolyWorldEventsFetcher getHolyWorldEventsFetcher() {
      return this.holyWorldEventsFetcher;
   }

   @Generated
   public HolyWorldItems getHolyWorldItems() {
      return this.holyWorldItems;
   }

   @Generated
   public HudHolder getHudHolder() {
      return this.hudHolder;
   }

   @Generated
   public CommandManager getCommandManager() {
      return this.commandManager;
   }

   @Generated
   public Names getNames() {
      return this.names;
   }

   @Generated
   public ChatBridge getChatBridge() {
      return this.chatBridge;
   }

   @Generated
   public Manager getManager() {
      return this.manager;
   }

   @Generated
   public org.zenith.utility.api.Friends getFriends2() {
      return this.friends2;
   }

   @Generated
   public Cloud getCloud() {
      return this.cloud;
   }

   @Generated
   public ServicesTicker getServicesTicker() {
      return this.servicesTicker;
   }

   @Generated
   public PartyManager getPartyManager() {
      return this.partyManager;
   }

   @Generated
   public StoreApi getStoreApi() {
      return this.storeApi;
   }

   @Generated
   public Voice getVoice() {
      return this.voice;
   }

   @Generated
   public Cosmetics getCosmetics() {
      return this.cosmetics;
   }

   @Generated
   public Wardrobe getWardrobe() {
      return this.wardrobe;
   }

   @Generated
   public View getView() {
      return this.view;
   }

   @Generated
   public Share getShare() {
      return this.share;
   }

   @Generated
   public Proxies getProxies() {
      return this.proxies;
   }

   @Generated
   public Accounts getAccounts() {
      return this.accounts;
   }

   @Generated
   public PlayTimeTracker getPlayTimeTracker() {
      return this.playTimeTracker;
   }

   @Generated
   public Zenith.Entry getEntry() {
      return this.entry;
   }

   @Generated
   public KawaseBlur getKawaseBlur() {
      return this.kawaseBlur;
   }

   public static class Entry {
      public static final long CONST_longValue = -1L;
      public static final long CONST_longValue2 = Long.MAX_VALUE;
      private static final long CONST_longValue3 = 10L;
      private static final long CONST_longValue4 = 1825L;
      private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      private static final DateTimeFormatter[] dateTimeFormatters = new DateTimeFormatter[]{
         DateTimeFormatter.ofPattern("dd.MM.yyyy"),
         DateTimeFormatter.ofPattern("d.M.yyyy"),
         DateTimeFormatter.ISO_LOCAL_DATE,
         DateTimeFormatter.ofPattern("dd/MM/yyyy")
      };
      private final String text;
      private final String text2;
      private final String text3;
      private final Zenith.Entry.Type type;
      private volatile Zenith.Entry.Data data;
      private volatile Zenith.Entry.Data2 data2;
      private volatile int intValue;

      public Entry() {
         if (System.getProperty("DevMode") == null) {
            Session.loadWEye();

            Zenith.Entry.Type var1;
            try {
               var1 = Zenith.Entry.Type.method1514(Session.getRole());
            } catch (Exception var3) {
               var1 = Zenith.Entry.Type.method1514(null);
            }

            this.type = var1;
            this.text = Session.getUsername();
            this.text2 = Session.getUid();
            this.text3 = Session.getExpirationDate();
         } else {
            this.type = Zenith.Entry.Type.method1514(System.getProperty("DevRole", "CRACK"));
            this.text = System.getProperty("DevName", "HACKKK");
            this.text3 = "01.01.2048";
            this.text2 = System.getProperty("DevUid", "1488");
         }
      }

      public Zenith.Entry.Type method1504() {
         Zenith.Entry.Data var1 = this.data;
         if (this.type != Zenith.Entry.Type.ADMIN && (var1 == null || !var1.isFlag())) {
            return var1 != null && var1.getType() != null ? var1.getType() : this.type;
         } else {
            return Zenith.Entry.Type.ADMIN;
         }
      }

      public void method1505(Zenith.Entry.Type var1, long var2, boolean var4) {
         this.data = new Zenith.Entry.Data(var1, var2, LocalDate.now(), var4);
         this.intValue++;
      }

      public long method1506() {
         return this.method1511().getLongValue2();
      }

      public boolean method1507() {
         return this.method1506() == Long.MAX_VALUE;
      }

      public boolean method1508() {
         return this.method1506() == 0L;
      }

      public String method1509() {
         LocalDate var1 = this.method1511().getLocalDate();
         return var1 == null ? "-" : dateTimeFormatter.format(var1);
      }

      public long method1510() {
         return this.intValue;
      }

      private Zenith.Entry.Data2 method1511() {
         Zenith.Entry.Data var1 = this.data;
         LocalDate var2 = LocalDate.now();
         long var3 = var2.toEpochDay();
         Zenith.Entry.Data2 var5 = this.data2;
         if (var5 != null && var5.getLongValue() == var3 && var5.getData() == var1) {
            return var5;
         } else {
            LocalDate var6;
            long var7;
            if (var1 == null || var1.getType() == null || var1.getLongValue() < 0L) {
               var6 = method1513(this.text3);
               var7 = var6 == null ? -1L : (var6.isAfter(var2.plusYears(10L)) ? Long.MAX_VALUE : method1512(var2, var6));
            } else if (var1.getLongValue() > 1825L) {
               var6 = null;
               var7 = Long.MAX_VALUE;
            } else {
               var6 = var1.getLocalDate().plusDays(var1.getLongValue());
               var7 = method1512(var2, var6);
            }

            Zenith.Entry.Data2 var9 = new Zenith.Entry.Data2(var1, var3, var7, var7 == Long.MAX_VALUE ? null : var6);
            this.data2 = var9;
            return var9;
         }
      }

      private static long method1512(LocalDate var0, LocalDate var1) {
         return var0.isAfter(var1) ? 0L : ChronoUnit.DAYS.between(var0, var1) + 1L;
      }

      public static LocalDate method1513(String var0) {
         if (var0 == null) {
            return null;
         } else {
            String var1 = var0.trim();
            if (var1.isEmpty()) {
               return null;
            } else if (var1.chars().allMatch(Character::isDigit)) {
               try {
                  long var10 = Long.parseLong(var1);
                  return Instant.ofEpochMilli(var1.length() > 11 ? var10 : var10 * 1000L).atZone(ZoneId.systemDefault()).toLocalDate();
               } catch (RuntimeException var8) {
                  return null;
               }
            } else {
               int var2;
               if ((var2 = var1.indexOf(84)) < 0 && var1.length() > 10 && var1.charAt(4) == '-' && var1.charAt(10) == ' ') {
                  var2 = 10;
               }

               if (var2 > 0) {
                  var1 = var1.substring(0, var2);
               }

               for (DateTimeFormatter var6 : dateTimeFormatters) {
                  try {
                     return LocalDate.parse(var1, var6);
                  } catch (DateTimeParseException var9) {
                  }
               }

               return null;
            }
         }
      }

      @Generated
      public String getUsername() {
         return this.text;
      }

      @Generated
      public String getText2() {
         return this.text2;
      }

      @Generated
      public String getText3() {
         return this.text3;
      }

      private static final class Data {
         private final Zenith.Entry.Type type;
         private final long longValue;
         private final LocalDate localDate;
         private final boolean flag;

         public Zenith.Entry.Type type() {
            return this.type;
         }

         public long longValue() {
            return this.longValue;
         }

         public LocalDate localDate() {
            return this.localDate;
         }

         public boolean flag() {
            return this.flag;
         }

         @Override
         public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Data other)) return false;
            return java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.localDate, other.localDate) && java.util.Objects.equals(this.flag, other.flag);
         }

         @Override
         public int hashCode() {
            return java.util.Objects.hash(type, longValue, localDate, flag);
         }

         @Override
         public String toString() {
            return "Data[" + "type=" + this.type + ", " + "longValue=" + this.longValue + ", " + "localDate=" + this.localDate + ", " + "flag=" + this.flag + "]";
         }

         private Data(Zenith.Entry.Type var1, long var2, LocalDate var4, boolean var5) {
            this.type = var1;
            this.longValue = var2;
            this.localDate = var4;
            this.flag = var5;
         }

         public Zenith.Entry.Type getType() {
            return this.type;
         }

         public long getLongValue() {
            return this.longValue;
         }

         public LocalDate getLocalDate() {
            return this.localDate;
         }

         public boolean isFlag() {
            return this.flag;
         }
      }

      private static final class Data2 {
         private final Zenith.Entry.Data data;
         private final long longValue;
         private final long longValue2;
         private final LocalDate localDate;

         public Zenith.Entry.Data data() {
            return this.data;
         }

         public long longValue() {
            return this.longValue;
         }

         public long longValue2() {
            return this.longValue2;
         }

         public LocalDate localDate() {
            return this.localDate;
         }

         @Override
         public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Data2 other)) return false;
            return java.util.Objects.equals(this.data, other.data) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.localDate, other.localDate);
         }

         @Override
         public int hashCode() {
            return java.util.Objects.hash(data, longValue, longValue2, localDate);
         }

         @Override
         public String toString() {
            return "Data2[" + "data=" + this.data + ", " + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "localDate=" + this.localDate + "]";
         }

         private Data2(Zenith.Entry.Data var1, long var2, long var4, LocalDate var6) {
            this.data = var1;
            this.longValue = var2;
            this.longValue2 = var4;
            this.localDate = var6;
         }

         public Zenith.Entry.Data getData() {
            return this.data;
         }

         public long getLongValue() {
            return this.longValue;
         }

         public long getLongValue2() {
            return this.longValue2;
         }

         public LocalDate getLocalDate() {
            return this.localDate;
         }
      }

      public static enum Type {
         ADMIN("Admin"),
         PRIVATE("Private"),
         OPUS("Opus"),
         BUILD("Opus/Private"),
         USER("User");

         public final String text;

         public static Zenith.Entry.Type method1514(String var0) {
            if (var0 == null) {
               return BUILD;
            } else {
               String var1 = var0.trim().toLowerCase(Locale.ROOT);
               if (var1.isEmpty()) {
                  return BUILD;
               } else {
                  switch (var1) {
                     case "admin":
                     case "administrator":
                        return ADMIN;
                     case "user":
                        return USER;
                     case "opus":
                     case "zenith opus":
                     case "recode":
                     case "zenith recode":
                        return OPUS;
                     case "private":
                     case "zenith private":
                     case "zenith recode 1.21.11":
                        return PRIVATE;
                     default:
                        return !var1.startsWith("3.0") && !var1.contains("beta") ? BUILD : PRIVATE;
                  }
               }
            }
         }

         @Generated
         public String getName() {
            return this.text;
         }

         @Generated
         private Type(String var3) {
            this.text = var3;
         }
      }
   }
}
