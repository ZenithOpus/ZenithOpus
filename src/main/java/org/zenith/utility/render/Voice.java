package org.zenith.utility.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.KeyEvent;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.CloudStateEvent;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.hud.WatermarkIslandHud;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.api.Prefs;
import org.zenith.utility.api.Share;
import org.zenith.utility.api.VoiceDeviceSetting;
import org.zenith.utility.misc.AudioDevices;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.VoiceState;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.Island;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class Voice implements MinecraftAccess {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Voice");
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private static final double CONST_doubleValue = 4.0;
   private static final double CONST_doubleValue2 = 32.0;
   private static final double CONST_doubleValue3 = 1.62;
   private static final int[] CONST_intValues = new int[]{16, 48, 96};
   public final BooleanSetting enabled = new BooleanSetting("gui.voice.enabled", "gui.voice.enabled.desc", false);
   public final ModeSetting activation = new ModeSetting(
      "gui.voice.activation", "gui.voice.activation.desc", "gui.voice.activation.ptt", "gui.voice.activation.voice"
   );
   public final BindSetting talkKey = new BindSetting("gui.voice.talkKey", "gui.voice.talkKey.desc", 86, () -> this.activation.isSelected(0));
   public final BindSetting radioKey = new BindSetting("gui.voice.radioKey", "gui.voice.radioKey.desc", -1);
   public final SliderSetting sensitivity = new SliderSetting("gui.voice.sensitivity", 60.0F, 0.0F, 100.0F, 5.0F, "gui.voice.sensitivity.desc", " %", null);
   public final SliderSetting volume = new SliderSetting("gui.voice.volume", 100.0F, 0.0F, 200.0F, 5.0F, "gui.voice.volume.desc", " %", null);
   public final SliderSetting radioVolume = new SliderSetting("gui.voice.radioVolume", 80.0F, 0.0F, 200.0F, 5.0F, "gui.voice.radioVolume.desc", " %", null);
   public final ModeSetting range = new ModeSetting(
      "gui.voice.range", "gui.voice.range.desc", "gui.voice.range.whisper", "gui.voice.range.normal", "gui.voice.range.loud"
   );
   public final SliderSetting micGain = new SliderSetting("gui.voice.micGain", 100.0F, 0.0F, 300.0F, 5.0F, "gui.voice.micGain.desc", " %", null);
   public final VoiceDeviceSetting input = new VoiceDeviceSetting("gui.voice.input", "gui.voice.input.desc", "gui.voice.device.default", AudioDevices::display);
   public final VoiceDeviceSetting output = new VoiceDeviceSetting(
      "gui.voice.output", "gui.voice.output.desc", "gui.voice.device.default", AudioDevices::display
   );
   public final BooleanSetting denoise = new BooleanSetting("gui.voice.denoise", "gui.voice.denoise.desc", true);
   public final BooleanSetting acceptRadio = new BooleanSetting("gui.voice.acceptRadio", "gui.voice.acceptRadio.desc", true);
   public final BooleanSetting spatial = new BooleanSetting("gui.voice.spatial", "gui.voice.spatial.desc", true);
   public final BooleanSetting hrtf = new BooleanSetting("gui.voice.hrtf", "gui.voice.hrtf.desc", false, this.spatial::isEnabled);
   public final ModeSetting quality = new ModeSetting(
      "gui.voice.quality", "gui.voice.quality.desc", "gui.voice.quality.low", "gui.voice.quality.normal", "gui.voice.quality.high"
   );
   public final BooleanSetting overlay = new BooleanSetting("gui.voice.overlay", "gui.voice.overlay.desc", true);
   public final BooleanSetting heads = new BooleanSetting("gui.voice.heads", "gui.voice.heads.desc", true);
   public final BindSetting muteKey = new BindSetting("gui.voice.muteKey", "gui.voice.muteKey.desc", -1);
   public final BindSetting deafenKey = new BindSetting("gui.voice.deafenKey", "gui.voice.deafenKey.desc", -1);
   public final BindSetting partyKey = new BindSetting("gui.voice.partyKey", "gui.voice.partyKey.desc", -1);
   public final BindSetting roomKey = new BindSetting("gui.voice.roomKey", "gui.voice.roomKey.desc", -1);
   public final BindSetting whisperKey = new BindSetting("gui.voice.whisperKey", "gui.voice.whisperKey.desc", -1);
   public final ModeSetting defaultChannel = new ModeSetting(
      "gui.voice.defaultChannel", "gui.voice.defaultChannel.desc", "gui.voice.channel.near", "gui.voice.channel.party", "gui.voice.channel.room"
   );
   public final BooleanSetting rnnoise = new BooleanSetting("gui.voice.rnnoise", "gui.voice.rnnoise.desc", true, this.denoise::isEnabled);
   public final BooleanSetting muffle = new BooleanSetting("gui.voice.muffle", "gui.voice.muffle.desc", true);
   public final SliderSetting channelVolume = new SliderSetting(
      "gui.voice.channelVolume", 100.0F, 0.0F, 200.0F, 5.0F, "gui.voice.channelVolume.desc", " %", null
   );
   private final Map<Long, Voice.Entry> items = new HashMap<>();
   private Map<Long, Float> items2;
   private final File file;
   private String text;
   private int intValue;
   private final VoiceState voiceState;
   private int intValue2;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private long longValue;
   private static final long CONST_longValue = 1500L;
   private String text2;
   private String text3;
   public static final int CONST_intValue = 124;

   public Voice() {
      this.file = new File(Zenith.file, "voice.json");
      this.text = "";
      this.voiceState = new VoiceState(new VoiceState.Handler() {
         @Override
         public boolean method377() {
            Cloud var1 = Voice.cloud();
            return Voice.this.enabled.isEnabled() && var1 != null && var1.method7863();
         }

         @Override
         public void method378(int var1, int var2, int var3, byte[] var4, int var5) {
            Cloud var6 = Voice.cloud();
            if (var6 != null) {
               var6.method7902(var1, var2, var3, var4, var5);
            }
         }
      });
      this.intValue2 = -1;
      this.text2 = "";
      this.text3 = "";
      this.range.set("gui.voice.range.normal");
      this.quality.set("gui.voice.quality.normal");
      this.load();
      this.text = this.method374().toString();
      EventManager.register(this);
   }

   private static Cloud cloud() {
      Zenith var0 = Zenith.getInstance();
      return var0 == null ? null : var0.getCloud();
   }

   public VoiceState getVoiceState() {
      return this.voiceState;
   }

   public List<Setting> method332() {
      return List.of(
         this.enabled,
         this.activation,
         this.talkKey,
         this.radioKey,
         this.sensitivity,
         this.volume,
         this.radioVolume,
         this.range,
         this.micGain,
         this.input,
         this.output,
         this.denoise,
         this.acceptRadio,
         this.spatial,
         this.hrtf,
         this.quality,
         this.overlay,
         this.heads,
         this.muteKey,
         this.deafenKey,
         this.partyKey,
         this.roomKey,
         this.whisperKey,
         this.defaultChannel,
         this.rnnoise,
         this.muffle,
         this.channelVolume
      );
   }

   public boolean isEnabled() {
      return this.enabled.isEnabled();
   }

   public void setEnabled(boolean var1) {
      if (this.enabled.isEnabled() != var1) {
         this.enabled.setEnabled(var1);
      }

      this.apply();
   }

   private void apply() {
      boolean var1 = this.enabled.isEnabled() || this.flag3;
      if (var1 && !this.voiceState.method5360()) {
         if (!this.flag) {
            this.method335();
         }

         this.voiceState.method5359(this.method342());
      }

      Cloud var2 = cloud();
      if (this.enabled.isEnabled() && this.voiceState.method5360() && !this.flag2) {
         if (var2 != null) {
            var2.setHandler(this.voiceState::method5371);
         }

         this.flag2 = true;
         this.intValue2 = -1;
         this.method349();
      } else if (!this.enabled.isEnabled() && this.flag2) {
         if (var2 != null) {
            var2.setHandler(null);
            var2.method7903(0);
         }

         this.flag2 = false;
         this.intValue2 = -1;
      }

      if (!var1 && this.voiceState.method5360()) {
         this.voiceState.stop();
      }
   }

   public void shutdown() {
      this.flag3 = false;
      this.method376();
      if (this.voiceState.method5360()) {
         this.voiceState.stop();
      }
   }

   public void method333(boolean var1) {
      if (var1 != this.flag3) {
         this.flag3 = var1;
         this.longValue = System.currentTimeMillis();
         if (var1) {
            this.apply();
            this.voiceState.method5363(true);
         } else {
            this.voiceState.method5363(false);
            this.apply();
         }
      }
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public void method334() {
      this.longValue = System.currentTimeMillis();
   }

   public void method335() {
      this.input.method9(AudioDevices.method3219());
      this.output.method9(AudioDevices.method3220());
      this.text2 = AudioDevices.method3221();
      this.text3 = AudioDevices.method3222();
      this.flag = true;
   }

   public String inputName() {
      String var1 = method337(this.voiceState.getText3());
      String var2 = this.input.method11();
      return AudioDevices.display(!var1.isEmpty() ? var1 : (!var2.isEmpty() ? var2 : this.text2));
   }

   public String method336() {
      String var1 = method337(this.voiceState.getText4());
      String var2 = this.output.method11();
      return AudioDevices.display(!var1.isEmpty() ? var1 : (!var2.isEmpty() ? var2 : this.text3));
   }

   private static String method337(String var0) {
      return var0 != null && !var0.equals("OpenAL Soft") ? var0 : "";
   }

   public String method338() {
      return AudioDevices.display(this.text2);
   }

   public String method339() {
      return AudioDevices.display(this.text3);
   }

   public boolean isFlag() {
      return this.flag;
   }

   public int method340() {
      int var1 = this.range.getIndex();
      return CONST_intValues[var1 >= 0 && var1 < CONST_intValues.length ? var1 : 1];
   }

   public float method341() {
      return -10.0F - this.sensitivity.getValue3() * 0.6F;
   }

   private VoiceState.Data method342() {
      char var1 = switch (this.quality.getIndex()) {
         case 0 -> '丠';
         case 2 -> '뮀';
         default -> '紀';
      };
      this.input.method12();
      this.output.method12();
      return new VoiceState.Data(
         this.activation.isSelected(0),
         this.method341(),
         this.micGain.getValue3() / 100.0F,
         var1,
         this.method340(),
         this.spatial.isEnabled() && this.hrtf.isEnabled(),
         this.input.method11(),
         this.output.method11(),
         this.denoise.isEnabled(),
         this.rnnoise.isEnabled(),
         this.method347()
      );
   }

   @EventTarget
   public void onTick(TickEvent var1) {
      this.method375();
      if (this.flag3 && System.currentTimeMillis() - this.longValue > 1500L) {
         this.method333(false);
      }

      this.apply();
      if (!this.voiceState.method5360()) {
         this.items2 = null;
      } else {
         VoiceState.Data var2 = this.method342();
         if (!var2.equals(this.voiceState.getData())) {
            this.voiceState.setData(var2);
         }

         this.voiceState.setFloatValue(this.volume.getValue3() / 100.0F);
         this.voiceState.setFlag(this.acceptRadio.isEnabled());
         this.items2 = this.method350();
         this.voiceState.setItems(this.items2);
         this.method345();
         this.method349();
      }
   }

   @EventTarget
   public void method343(CloudStateEvent var1) {
      this.intValue2 = -1;
      if (var1.method6858() && this.voiceState.method5360()) {
         this.method349();
      }
   }

   @EventTarget
   public void method344(KeyEvent var1) {
      if (this.voiceState.method5360()) {
         if (this.muteKey.getKeyCode() > 0 && var1.isPress(this.muteKey.getKeyCode())) {
            this.voiceState.setFlag3(!this.voiceState.isFlag3());
         }

         if (this.deafenKey.getKeyCode() > 0 && var1.isPress(this.deafenKey.getKeyCode())) {
            boolean var2 = !this.voiceState.isFlag4();
            this.voiceState.method5362(var2);
            this.voiceState.setFlag3(var2);
            this.intValue2 = -1;
         }
      }
   }

   private void method345() {
      boolean var1 = this.activation.isSelected(0) && UseItemUtils.method6521(this.talkKey);
      this.voiceState.method5361(var1, this.method346());
   }

   private int method346() {
      Cloud var1 = cloud();
      PartyMember var2 = var1 == null ? null : var1.getPartyMember();
      if (var2 != null && var2.getLongValue() != 0L && UseItemUtils.method6521(this.whisperKey)) {
         return 16;
      } else if (var2 != null && var2.getData() != null && UseItemUtils.method6521(this.roomKey)) {
         return 8;
      } else if (var2 != null && var2.method1136() && UseItemUtils.method6521(this.partyKey)) {
         return 4;
      } else {
         return UseItemUtils.method6521(this.radioKey) ? 1 : 0;
      }
   }

   public int method347() {
      Cloud var1 = cloud();
      PartyMember var2 = var1 == null ? null : var1.getPartyMember();
      if (var2 == null) {
         return 0;
      } else {
         return switch (this.defaultChannel.getIndex()) {
            case 1 -> var2.method1136() ? 4 : 0;
            case 2 -> var2.getData() != null ? 8 : 0;
            default -> 0;
         };
      }
   }

   public String method348(long var1) {
      Cloud var3 = cloud();
      if (var3 == null) {
         return "#" + var1;
      } else {
         PartyPlayerInfo var4 = var3.state().method8040(var1);
         if (var4 != null) {
            return method367(var4);
         } else {
            PartyMember.Data var5 = var3.getPartyMember().getData();
            if (var5 != null) {
               for (PartyMember.Data3 var7 : var5.getItems()) {
                  if (var7.uid() == var1) {
                     return var7.getText2().isEmpty() ? var7.username() : var7.getText2();
                  }
               }
            }

            return var3.getPartyMember().method1149(var1);
         }
      }
   }

   private void method349() {
      Cloud var1 = cloud();
      if (var1 != null && var1.method7863()) {
         int var2 = this.voiceState.isFlag4() ? 0 : 1 | (this.acceptRadio.isEnabled() ? 2 : 0);
         if (var2 != this.intValue2) {
            var1.method7903(var2);
            this.intValue2 = var2;
         }
      }
   }

   private Map<Long, Float> method350() {
      Cloud var1 = cloud();
      if (var1 == null) {
         return Map.of();
      } else {
         Prefs var2 = var1.prefs();
         HashMap var3 = new HashMap();

         for (PartyPlayerInfo var5 : var1.state().method8038()) {
            if (!var2.method3321(var5.username()) && !var2.method3316(var5.username())) {
               var3.put(var5.uid(), var2.method3323(var5.username()) / 100.0F);
            }
         }

         for (PartyPlayerInfo var9 : var1.state().method8041()) {
            if (!var3.containsKey(var9.uid()) && !var2.method3321(var9.username()) && !var2.method3316(var9.username())) {
               var3.put(var9.uid(), var2.method3323(var9.username()) / 100.0F);
            }
         }

         PartyMember.Data var8 = var1.getPartyMember().getData();
         if (var8 != null) {
            for (PartyMember.Data3 var6 : var8.getItems()) {
               if (var6.uid() != var1.getLongValue() && !var3.containsKey(var6.uid()) && !var2.method3321(var6.username()) && !var2.method3316(var6.username())
                  )
                {
                  var3.put(var6.uid(), var2.method3323(var6.username()) / 100.0F);
               }
            }
         }

         return var3;
      }
   }

   private void method351(float var1) {
      Set<Long> var2 = this.voiceState.method5364();
      if (var2.isEmpty()) {
         this.voiceState.setItems2(Map.of());
      } else {
         Cloud var3 = cloud();
         var var4 = this.items2 != null ? this.items2 : this.method350();
         float var5 = this.volume.getValue3() / 100.0F;
         float var6 = this.radioVolume.getValue3() / 100.0F;
         HashMap var7 = new HashMap();
         Camera var8 = mc.gameRenderer == null ? null : mc.gameRenderer.getCamera();
         Vec3d var9 = var8 == null ? null : var8.getCameraPos();
         Vec3d var10 = mc.player == null ? null : mc.player.getCameraPosVec(var1);
         double var11 = var8 == null ? 0.0 : Math.toRadians(var8.getYaw());
         double var13 = var8 == null ? 0.0 : Math.toRadians(var8.getPitch());
         Vec3d var15 = new Vec3d(-Math.sin(var11) * Math.cos(var13), -Math.sin(var13), Math.cos(var11) * Math.cos(var13));
         Vec3d var16 = new Vec3d(-Math.cos(var11), 0.0, -Math.sin(var11));
         Vec3d var17 = var16.crossProduct(var15);

         for (Object var19_o : (Iterable<?>)(Object)(var2)) {
            long var19 = (Long)var19_o;
            Float var21 = (Float)var4.get(var19);
            if (var21 != null && var3 != null) {
               int var22 = this.voiceState.method5368(var19);
               if ((var22 & 1) != 0) {
                  var7.put(var19, new VoiceState.Data2(0.0F, 0.0F, 0.0F, var6 * var21));
               } else if ((var22 & 28) != 0) {
                  float var29 = (var22 & 16) != 0 ? 1.1F : 1.0F;
                  var7.put(var19, new VoiceState.Data2(0.0F, 0.0F, 0.0F, var5 * var21 * var29 * this.channelVolume.getValue3() / 100.0F));
               } else {
                  PartyPlayerInfo var23 = var3.state().method8040(var19);
                  Vec3d var24 = var23 != null && var10 != null ? this.method354(var23, var10, var1, var3) : null;
                  if (var24 != null && var9 != null) {
                     float var25 = method356(var24.distanceTo(var10), this.voiceState.method5370(var19));
                     if (!(var25 <= 0.0F)) {
                        float var26 = var5 * var21 * var25;
                        float var27 = this.muffle.isEnabled() ? this.method352(var19, var10, var24) : 0.0F;
                        if (!this.spatial.isEnabled()) {
                           var7.put(var19, new VoiceState.Data2(0.0F, 0.0F, 0.0F, var26, var27));
                        } else {
                           Vec3d var28 = var24.subtract(var9);
                           var7.put(
                              var19,
                              new VoiceState.Data2(
                                 (float)var28.dotProduct(var16), (float)var28.dotProduct(var17), (float)(-var28.dotProduct(var15)), var26, var27
                              )
                           );
                        }
                     }
                  }
               }
            }
         }

         this.voiceState.setItems2(var7);
      }
   }

   private float method352(long var1, Vec3d var3, Vec3d var4) {
      Voice.Entry var5 = this.items.computeIfAbsent(var1, var0 -> new Voice.Entry());
      long var6 = System.currentTimeMillis();
      if (var6 - var5.longValue > 150L) {
         var5.target = this.method353(var3, var4);
         var5.longValue = var6;
      }

      var5.value = var5.value + (var5.target - var5.value) * 0.25F;
      return var5.value;
   }

   private float method353(Vec3d var1, Vec3d var2) {
      if (mc.world != null && mc.player != null) {
         RaycastContext var3 = new RaycastContext(var1, var2, ShapeType.COLLIDER, FluidHandling.NONE, mc.player);
         BlockHitResult var4 = mc.world.raycast(var3);
         if (var4 != null && var4.getType() != HitResult.Type.MISS) {
            RaycastContext var5 = new RaycastContext(var2, var1, ShapeType.COLLIDER, FluidHandling.NONE, mc.player);
            BlockHitResult var6 = mc.world.raycast(var5);
            double var7 = var6 != null && var6.getType() != HitResult.Type.MISS ? Math.max(1.0, var4.getPos().distanceTo(var6.getPos())) : 1.0;
            return (float)Math.min(1.0, 0.55 + var7 * 0.12);
         } else {
            return 0.0F;
         }
      } else {
         return 0.0F;
      }
   }

   private Vec3d method354(PartyPlayerInfo var1, Vec3d var2, float var3, Cloud var4) {
      PlayerEntity var5 = this.method355(var1.getText2());
      if (var5 != null) {
         return var5.getCameraPosVec(var3);
      } else if (var1.method3291(var4.method7911(), var4.method7913()) && var1.method3292()) {
         Vec3d var6 = var1.method3294().add(0.0, 1.62, 0.0);
         return var6.distanceTo(var2) < 32.0 ? null : var6;
      } else {
         return null;
      }
   }

   private PlayerEntity method355(String var1) {
      if (mc.world != null && var1 != null && !var1.isEmpty()) {
         for (PlayerEntity var3 : mc.world.getPlayers()) {
            if (var3 != mc.player && var1.equalsIgnoreCase(var3.getGameProfile().name())) {
               return var3;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static float method356(double var0, int var2) {
      if (var0 <= 4.0) {
         return 1.0F;
      } else {
         double var3 = (var0 - 4.0) / Math.max(1.0, var2 - 4.0);
         return var3 >= 1.0 ? 0.0F : (float)Math.pow(1.0 - var3, 1.6);
      }
   }

   public Voice.Type method357() {
      if (this.flag3) {
         return Voice.Type.TESTING;
      } else if (!this.voiceState.method5360()) {
         return Voice.Type.OFF;
      } else {
         Cloud var1 = cloud();
         if (!this.voiceState.getText().isEmpty()) {
            return Voice.Type.MIC_ERROR;
         } else if (!this.voiceState.getText2().isEmpty()) {
            return Voice.Type.OUTPUT_ERROR;
         } else if (var1 == null || !var1.method7862()) {
            return Voice.Type.OFFLINE;
         } else if (!var1.method7863()) {
            return Voice.Type.UNSUPPORTED;
         } else if (this.voiceState.isFlag4()) {
            return Voice.Type.DEAFENED;
         } else if (this.voiceState.isFlag3()) {
            return Voice.Type.MUTED;
         } else if (this.voiceState.isFlag6()) {
            int var3 = this.voiceState.getIntValue() != 0 ? this.voiceState.getIntValue() : this.method347();
            if ((var3 & 16) != 0) {
               return Voice.Type.WHISPER;
            } else if ((var3 & 8) != 0) {
               return Voice.Type.ROOM;
            } else if ((var3 & 4) != 0) {
               return Voice.Type.PARTY;
            } else {
               return (var3 & 1) != 0 ? Voice.Type.RADIO : Voice.Type.TALKING;
            }
         } else {
            Share var2 = Zenith.getInstance().getShare();
            return var2 == null || var2.position.isEnabled() && var2.server.isEnabled() ? Voice.Type.READY : Voice.Type.NO_POSITION;
         }
      }
   }

   public boolean method358() {
      if (!this.isEnabled()) {
         return false;
      } else if (this.voiceState.isFlag6()) {
         return true;
      } else {
         for (long var2 : this.voiceState.method5364()) {
            if (var2 != -1L && this.voiceState.method5365(var2)) {
               return true;
            }
         }

         return false;
      }
   }

   public String method359() {
      Voice.Type var1 = this.method357();
      String var2 = method373("gui.voice.status." + var1.name().toLowerCase(Locale.ROOT));
      if (var1 == Voice.Type.MIC_ERROR) {
         return var2 + ": " + this.voiceState.getText();
      } else {
         return var1 == Voice.Type.OUTPUT_ERROR ? var2 + ": " + this.voiceState.getText2() : var2;
      }
   }

   public ZenithColor method360() {
      return switch (this.method357()) {
         case OFF, DEAFENED, MUTED -> new ZenithColor(-495247);
         case TESTING, RADIO, TALKING, PARTY, ROOM, WHISPER -> new ZenithColor(-11870592);
         default -> new ZenithColor(-680437);
         case READY -> new ZenithColor(-10443270);
      };
   }

   public int method361() {
      Cloud var1 = cloud();
      return var1 == null ? -1 : var1.method7864();
   }

   public void method362() {
      Cloud var1 = cloud();
      if (var1 != null) {
         var1.method7865();
      }
   }

   public int latencyMs() {
      int var1 = this.method361();
      return var1 < 0 ? -1 : var1 + 124;
   }

   public int method363() {
      return this.method342().getIntValue() / 1000;
   }

   public static String method364(BindSetting var0) {
      int var1 = var0.getKeyCode();
      if (var1 < 0) {
         return "—";
      } else {
         String var2 = KeyName.method6701(var1);
         return var2 != null && !var2.isEmpty() ? var2 : String.valueOf(var1);
      }
   }

   public List<PartyPlayerInfo> method365() {
      Cloud var1 = cloud();
      ArrayList<PartyPlayerInfo> var2 = new ArrayList<>();
      if (var1 != null) {
         for (PartyPlayerInfo var4 : var1.state().method8038()) {
            if (var4.method3289()) {
               var2.add(var4);
            }
         }

         for (PartyPlayerInfo var6 : var1.state().method8041()) {
            if (var6.method3289() && var1.state().method8039(var6.uid()) == null) {
               var2.add(var6);
            }
         }
      }

      var2.sort((var0, var1x) -> method367(var0).compareToIgnoreCase(method367(var1x)));
      return var2;
   }

   public String method366(PartyPlayerInfo var1) {
      Cloud var2 = cloud();
      if (var2 == null || !var1.method3291(var2.method7911(), var2.method7913())) {
         return method373("gui.voice.friend.elsewhere");
      } else if (mc.player != null && var1.method3292()) {
         double var3 = var1.method3294().distanceTo(mc.player.getEntityPos());
         return var3 <= this.method340() ? method373("gui.voice.friend.near") : method373("gui.voice.friend.far");
      } else {
         return method373("gui.voice.friend.sameServer");
      }
   }

   public static String method367(PartyPlayerInfo var0) {
      return var0.getText2() != null && !var0.getText2().isEmpty() ? var0.getText2() : var0.username();
   }

   @EventTarget
   public void method368(DrawEvent var1) {
      if (this.voiceState.method5360()) {
         this.method345();
         this.method351(var1.getTickDelta());
         ZenithStyle var2 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var2 != null) {
            if (this.heads.isEnabled()) {
               this.method372(var1.getCustomDrawContext(), var2, var1.getTickDelta());
            }

            if (this.overlay.isEnabled()) {
               this.method369(var1.getCustomDrawContext(), var2);
            }
         }
      }
   }

   private void method369(CustomDrawContext var1, ZenithStyle var2) {
      ArrayList<Voice.Data> var3 = new ArrayList();
      Voice.Type var4 = this.method357();
      if (var4 != Voice.Type.READY) {
         boolean var5 = var4 == Voice.Type.TALKING
            || var4 == Voice.Type.RADIO
            || var4 == Voice.Type.TESTING
            || var4 == Voice.Type.PARTY
            || var4 == Voice.Type.ROOM
            || var4 == Voice.Type.WHISPER;
         var3.add(new Voice.Data(this.method359(), this.method360(), var5 ? this.voiceState.getFloatValue2() : -100.0F, false, true));
      }

      Cloud var17 = cloud();
      if (var17 != null) {
         for (long var7 : this.voiceState.method5364()) {
            if (this.voiceState.method5365(var7) && var7 != -1L) {
               boolean var9 = this.voiceState.method5369(var7);
               String var10 = this.method348(var7);
               int var11 = this.voiceState.method5368(var7);
               if ((var11 & 16) != 0) {
                  var10 = var10 + " · " + method373("gui.voice.tag.whisper");
               } else if ((var11 & 4) != 0) {
                  var10 = var10 + " · " + method373("gui.voice.tag.party");
               } else if ((var11 & 8) != 0) {
                  var10 = var10 + " · " + method373("gui.voice.tag.room");
               }

               var3.add(
                  new Voice.Data(
                     var10, var9 ? new ZenithColor(-10443270) : var2.getPrimaryColor().getZenithColor(), this.voiceState.method5366(var7), var9, false
                  )
               );
            }
         }
      }

      if (WatermarkIslandHud.method5668()) {
         this.method370(var4, var3);
      } else if (!var3.isEmpty()) {
         Font var18 = Fonts.NEW_MEDIUM.getFont(7.0F);
         float var19 = GuiStyle.PADDING.intValue();
         float var8 = var18.height() + var19;
         float var20 = 6.0F;
         float var21 = mc.getWindow().getScaledHeight() * 0.38F;
         ProxyDotRadiusData var22 = ProxyDotRadiusData.method8069(InterfaceModule.method6801() * 0.6F);

         for (Object var13_o : (Iterable<?>)(Object)(var3)) {
            Voice.Data var13 = (Voice.Data)var13_o;
            String var14 = var13.flag ? var13.text + " · " + method373("gui.voice.radioTag") : var13.text;
            float var15 = var19 + 8.0F + var18.width(var14) + 5.0F + 12.0F + var19;
            var1.drawRoundedRect(var20, var21, var15, var8, var22, var2.getHudBackground().getZenithColor());
            var1.drawRoundedRect(var20 + var19, var21 + (var8 - 4.0F) / 2.0F, 4.0F, 4.0F, ProxyDotRadiusData.method8069(2.0F), var13.zenithColor);
            float var16 = var20 + var19 + 8.0F;
            var1.drawText(var18, var14, var16, var21 + (var8 - var18.height()) / 2.0F, var2.getTextEnable().getZenithColor());
            method371(var1, var16 + var18.width(var14) + 5.0F, var21 + var8 / 2.0F, var13.floatValue, var13.zenithColor);
            var21 += var8 + 2.0F;
         }
      }
   }

   private void method370(Voice.Type var1, List<Voice.Data> var2) {
      ArrayList<Voice.Data> var3 = new ArrayList();
      Voice.Data var4 = null;

      for (Voice.Data var6 : var2) {
         if (var6.flag2) {
            var4 = var6;
         } else {
            var3.add(var6);
         }
      }

      if (!var3.isEmpty()) {
         StringBuilder var11 = new StringBuilder();
         float var12 = -100.0F;
         boolean var7 = false;

         for (Object var9_o : (Iterable<?>)(Object)(var3)) {
            Voice.Data var9 = (Voice.Data)var9_o;
            if (!var11.isEmpty()) {
               var11.append(", ");
            }

            var11.append(var9.text);
            var12 = Math.max(var12, var9.floatValue);
            var7 |= var9.flag;
         }

         String var13 = var7 ? var11 + " · " + method373("gui.voice.radioTag") : var11.toString();
         Island.method2873("voice", var13, ((Voice.Data)var3.get(0)).zenithColor, 75, var12);
      } else if (var4 == null) {
         Island.drop("voice");
      } else {
         boolean var10 = var1 == Voice.Type.TALKING || var1 == Voice.Type.RADIO || var1 == Voice.Type.TESTING;
         Island.method2873("voice", var4.text, var4.zenithColor, var10 ? 75 : 35, var10 ? var4.floatValue : Float.NaN);
      }
   }

   public static void method371(CustomDrawContext var0, float var1, float var2, float var3, ZenithColor var4) {
      float var5 = MathHelper.clamp((var3 + 60.0F) / 45.0F, 0.0F, 1.0F);
      long var6 = System.currentTimeMillis();

      for (int var8 = 0; var8 < 3; var8++) {
         float var9 = 0.55F + 0.45F * (float)Math.sin(var6 / 90.0 + var8 * 1.7);
         float var10 = 2.0F + 6.0F * var5 * (var8 == 1 ? 1.0F : 0.7F) * (var5 > 0.0F ? var9 : 0.0F);
         var0.drawRoundedRect(
            var1 + var8 * 4.0F, var2 - var10 / 2.0F, 2.5F, var10, ProxyDotRadiusData.method8069(1.2F), var4.method7294(var5 > 0.0F ? 1.0F : 0.35F)
         );
      }
   }

   private void method372(CustomDrawContext var1, ZenithStyle var2, float var3) {
      Cloud var4 = cloud();
      if (var4 != null && mc.world != null) {
         for (long var6 : this.voiceState.method5364()) {
            if (var6 != -1L && this.voiceState.method5365(var6)) {
               PartyPlayerInfo var8 = var4.state().method8040(var6);
               PlayerEntity var9 = var8 == null ? null : this.method355(var8.getText2());
               if (var9 != null) {
                  Vec3d var10 = var9.getLerpedPos(var3).add(0.0, var9.getHeight() + 0.75, 0.0);
                  if (ProjectionUtils.method302(var10)) {
                     Vec3d var11 = ProjectionUtils.method301(var10);
                     float var12 = 22.0F;
                     float var13 = 11.0F;
                     float var14 = (float)var11.x - var12 / 2.0F;
                     float var15 = (float)var11.y - var13;
                     ZenithColor var16 = this.voiceState.method5367(var6) ? new ZenithColor(-10443270) : var2.getPrimaryColor().getZenithColor();
                     var1.drawRoundedRect(var14, var15, var12, var13, ProxyDotRadiusData.method8069(var13 / 2.0F), var2.getHudBackground().getZenithColor());
                     method371(var1, var14 + 6.0F, var15 + var13 / 2.0F, this.voiceState.method5366(var6), var16);
                  }
               }
            }
         }
      }
   }

   private static String method373(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   private JsonObject method374() {
      JsonObject var1 = new JsonObject();

      for (Setting var3 : this.method332()) {
         var3.safe(var1);
      }

      return var1;
   }

   private void method375() {
      if (++this.intValue % 20 == 0) {
         JsonObject var1 = this.method374();
         String var2 = var1.toString();
         if (!var2.equals(this.text)) {
            this.text = var2;
            String var3 = gson.toJson(var1);
            Util.getIoWorkerExecutor().execute(() -> this.write(var3));
         }
      }
   }

   private void method376() {
      JsonObject var1 = this.method374();
      this.text = var1.toString();
      this.write(gson.toJson(var1));
   }

   private synchronized void write(String var1) {
      try {
         this.file.getParentFile().mkdirs();
         Files.writeString(this.file.toPath(), var1, StandardCharsets.UTF_8);
      } catch (Exception var3) {
         logger.warn("Голос: настройки не записаны: {}", var3.toString());
      }
   }

   private void load() {
      if (this.file.exists()) {
         try {
            JsonObject var1 = JsonParser.parseString(Files.readString(this.file.toPath(), StandardCharsets.UTF_8)).getAsJsonObject();

            for (Setting var3 : this.method332()) {
               if (var1.has(var3.getKey())) {
                  var3.load(var1);
               }
            }
         } catch (Exception var4) {
            logger.warn("Голос: voice.json не прочитан: {}", var4.toString());
         }
      }
   }

   private static final class Data {
      private final String text;
      private final ZenithColor zenithColor;
      private final float floatValue;
      private final boolean flag;
      private final boolean flag2;

      public String text() {
         return this.text;
      }

      public ZenithColor zenithColor() {
         return this.zenithColor;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.zenithColor, other.zenithColor) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, zenithColor, floatValue, flag, flag2);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "zenithColor=" + this.zenithColor + ", " + "floatValue=" + this.floatValue + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + "]";
      }

      private Data(String var1, ZenithColor var2, float var3, boolean var4, boolean var5) {
         this.text = var1;
         this.zenithColor = var2;
         this.floatValue = var3;
         this.flag = var4;
         this.flag2 = var5;
      }

      public ZenithColor getZenithColor() {
         return this.zenithColor;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }
   }

   private static final class Entry {
      public float value;
      public float target;
      public long longValue;
   }

   public static enum Type {
      OFF,
      TESTING,
      MIC_ERROR,
      OUTPUT_ERROR,
      OFFLINE,
      UNSUPPORTED,
      DEAFENED,
      MUTED,
      RADIO,
      TALKING,
      PARTY,
      ROOM,
      WHISPER,
      NO_POSITION,
      READY;
   }
}
