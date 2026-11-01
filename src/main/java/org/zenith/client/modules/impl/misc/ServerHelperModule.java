package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import kotlin.collections.EmptyMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ExplosionSmokeParticle.Factory;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import org.apache.commons.lang3.StringUtils;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.zenith.ChatLogger;
import org.zenith.KeyEvent;
import org.zenith.MatrixUtils;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.RenderState;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.ChatSendEvent;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.events.HandleInputPostEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.Class27;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.TaskScheduler;
import org.zenith.utility.mixin.accessors.MinecraftClientAccessor;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.Island;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "ServerHelper",
   category = ModuleCategory.MISC,
   description = "module.serverhelper.description"
)
public final class ServerHelperModule extends Module {
   private static final long CONST_longValue = 2000L;
   private final LinkedHashMap<BlockPos, ServerHelperModule.Data5> linkedHashMap = new LinkedHashMap<>();
   private final List<ServerHelperModule.Data2> items = new ArrayList<>();
   private final List<ServerHelperModule.Data4> items2 = new ArrayList<>();
   private final List<ServerHelperModule.Data3> items3 = new ArrayList<>();
   private final Map<Integer, ServerHelperModule.Data> items4 = new ConcurrentHashMap<>();
   private final Map<BlockPos, Boolean> items5 = new HashMap<>();
   private final Map<BlockPos, Boolean> items6 = new HashMap<>();
   private static final long CONST_longValue2 = 1000L;
   private final Map<BlockPos, Long> items7 = new HashMap<>();
   private final Map<BlockPos, Long> items8 = new HashMap<>();
   public static final ServerHelperModule INSTANCE = new ServerHelperModule();
   private final ModeSetting serverMode = new ModeSetting(
      "module.serverHelper.serverMode", "module.serverHelper.serverMode.desc", "Auto", "HolyWorld", "FunTime", "ReallyWorld"
   );
   private final BooleanSetting consumablesSetting = new BooleanSetting(
      "module.serverHelper.consumablesSetting", "module.serverHelper.consumablesSetting.desc", true, () -> this.method5182() || this.method5183()
   );
   private final BooleanSetting autoPointSetting = new BooleanSetting(
      "module.serverHelper.autoPointSetting", "module.serverHelper.autoPointSetting.desc", true, this::method5182
   );
   private final BooleanSetting hitDisplaySetting = new BooleanSetting(
      "module.serverHelper.hitDisplaySetting", "module.serverHelper.hitDisplaySetting.desc", true, this::method5183
   );
   private final BindSetting shulkerKey = new BindSetting("module.serverHelper.shulkerKey", "module.serverHelper.shulkerKey.desc", -1, this::method5183);
   private final BooleanSetting closeMenu = new BooleanSetting("module.serverHelper.closeMenu", "module.serverHelper.closeMenu.desc", false, this::method5181);
   private final BooleanSetting chatFilter = new BooleanSetting(
      "module.serverHelper.chatFilter", "module.serverHelper.chatFilter.desc", false, this::method5181
   );
   private final BooleanSetting autoFixAll = new BooleanSetting(
      "module.serverHelper.autoFixAll", "module.serverHelper.autoFixAll.desc", false, this::method5181
   );
   private final BooleanSetting antiFlyRadius = new BooleanSetting(
      "module.serverHelper.antiFlyRadius", "module.serverHelper.antiFlyRadius.desc", false, this::method5181
   );
   private static final String text = "ꈁꀀꈂꌁꈂꀁ§0ꈃꄀ";
   private static final float CONST_floatValue = 16.0F;
   private static final long CONST_longValue3 = 2000L;
   private static final Pattern pattern = Pattern.compile(
      "(?<![\\p{L}\\d])(?:акриен(?:а|у|ом|е|чик)?|рич(?:а|у|ом|ей|е)?|ньюкод(?:ом|а|у|ами|ик|е)?|экспенсив(?:ом|а|у|ами|е)?|импакт(?:ом|а|у|ами|ик|е)?|экселлент(?:ом|а|у|ами|ик|е)?|экселент(?:ом|а|у|ами|ик)?|катлаван(?:ом|а|у|ами|чик)?|катлован(?:ом|а|у|ами|чик)?|целестиал(?:ом|а|у|ами|е)?|целк(?:ой|а|у|ами|очка|е)?|матикс(?:ом|а|у|ами|е)?|инерти(?:я|ей|ю|ями|е)?|эксп(?:а|ой|ою|у|уличка|е)?|флюгер(?:ом|а|у|ами)?|рикер(?:а|у|ом|очек)?|фанпе(?:й|ю|я|ем|е|йчик)?|вексайд(?:ом|а|у|ами|ик|е)?|нурсултан(?:а|у|е|ом|чик)?|нурик(?:а|у|ом|е)?|нурлан(?:а|у|ом|чик|е)?|векс(?:ом|у|а|ами|ик|е)?|релейк(?:ом|у|а|ами|е)?|арбуз(?:ом|а|у|ами|ик|е)?|вилд(?:ом|у|а|ами|ик|е)?|фантайм(?:е|а|у)?|холик(?:е|а|у)?|холиворлд(?:а|у|е)?|рокстар(?:ом|а|у|ами|чик|е)?|рогалик(?:а|у|ом|е)?|тандерхак(?:ом|у|и|ами|а|е)?|ликвидбаунс(?:а|у|ами|е)?|expensive|celestial|newcode|arbuz|akrien|nursultan|relake|wild|wurst|catlovan|excellent|rockstar|catlavan|impact|matix|inertia|wexside|wex|nurik|nurlan|rich|funpay|fluger|riker|funtime|holyworld|wwe|hvh|rogalik|thunderhack|liquidbounce)(?![\\p{L}\\d])",
      66
   );
   private final StopWatch stopWatch = new StopWatch();
   private boolean flag;
   private Slot slot = null;
   private final StopWatch stopWatch2 = new StopWatch();

   private ServerHelperModule() {
      this.initialize();
   }

   public boolean method5181() {
      return this.serverMode.isSelected(0) ? Zenith.getInstance().getServerDetector().method1641() : this.serverMode.isSelected(3);
   }

   public boolean method5182() {
      return this.serverMode.isSelected(0) ? Zenith.getInstance().getServerDetector().method1639() : this.serverMode.isSelected(2);
   }

   public boolean method5183() {
      return this.serverMode.isSelected(0) ? Zenith.getInstance().getServerDetector().method1642() : this.serverMode.isSelected(1);
   }

   public void initialize() {
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.FIREWORK_STAR, new BindSetting("module.serverHelper.antiFly", "module.serverHelper.antiFly.desc", this::method5181), 0.0F, new Class27()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.FLOWER_BANNER_PATTERN,
               new BindSetting("module.serverHelper.scrollExp", "module.serverHelper.scrollExp.desc", this::method5181),
               0.0F,
               new Class27()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.HEART_OF_THE_SEA,
               new BindSetting("module.serverHelper.enderTrap", "module.serverHelper.enderTrap.desc", this::method5181),
               0.0F,
               new Class27(),
               var0 -> var0.getStack().getName().getString().equals("Эндер Ловушка")
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.HEART_OF_THE_SEA,
               new BindSetting("module.serverHelper.reallyWorldTrap", "module.serverHelper.reallyWorldTrap.desc", this::method5181),
               0.0F,
               new Class27(),
               var0 -> var0.getStack().getName().getString().equals("Ловушка")
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.PRISMARINE_SHARD,
               new BindSetting("module.serverHelper.explosiveTrap", "module.serverHelper.explosiveTrap.desc", this::method5183),
               5.0F,
               new Class27()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.POPPED_CHORUS_FRUIT,
               new BindSetting("module.serverHelper.normalTrap", "module.serverHelper.normalTrap.desc", this::method5183),
               0.0F,
               new Class27()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.NETHER_STAR, new BindSetting("module.serverHelper.stun", "module.serverHelper.stun.desc", this::method5183), 30.0F, new Class27()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.FIRE_CHARGE,
               new BindSetting("module.serverHelper.explosiveThing", "module.serverHelper.explosiveThing.desc", this::method5183),
               0.0F,
               new Class27()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.SNOWBALL,
               new BindSetting("module.serverHelper.snowball", "module.serverHelper.snowball.desc", () -> this.method5182() || this.method5183()),
               0.0F,
               new Class27()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.PHANTOM_MEMBRANE,
               new BindSetting("module.serverHelper.holyAura", "module.serverHelper.holyAura.desc", this::method5182),
               0.0F,
               new Class27()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.NETHERITE_SCRAP, new BindSetting("module.serverHelper.trap", "module.serverHelper.trap.desc", this::method5182), 0.0F, new Class27()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.DRIED_KELP, new BindSetting("module.serverHelper.plast", "module.serverHelper.plast.desc", this::method5182), 0.0F, new Class27()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.SUGAR, new BindSetting("module.serverHelper.clearDust", "module.serverHelper.clearDust.desc", this::method5182), 10.0F, new Class27()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.FIRE_CHARGE,
               new BindSetting("module.serverHelper.fireTornado", "module.serverHelper.fireTornado.desc", this::method5182),
               10.0F,
               new Class27()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.ENDER_EYE, new BindSetting("module.serverHelper.disorient", "module.serverHelper.disorient.desc", this::method5182), 10.0F, new Class27()
            )
         );
   }

   @Override
   public List<Setting> getSettings() {
      var var1 = new ArrayList<>(
         List.of(
            this.serverMode,
            this.consumablesSetting,
            this.autoPointSetting,
            this.hitDisplaySetting,
            this.shulkerKey,
            this.closeMenu,
            this.chatFilter,
            this.autoFixAll,
            this.antiFlyRadius
         )
      );
      var1.addAll(this.items3.stream().map(ServerHelperModule.Data3::getBindSetting).toList());
      return var1;
   }

   @EventTarget
   public void method5184(KeyEvent var1) {
      this.items3
         .stream()
         .filter(var1x -> var1.isPress(var1x.bindSetting.getKeyCode()) && var1x.bindSetting.isVisible() && var1x.method5218() != null)
         .forEach(var0 -> var0.class27.setFlag(true));
      this.items3.stream().filter(var1x -> var1.isRelease(var1x.bindSetting.getKeyCode()) && var1x.bindSetting.isVisible()).forEach(var1x -> {
         if (this.method5208(var1x.floatValue)) {
            InventoryUtils.method928(var1x.getItem(), var1x.method5219());
         }

         var1x.class27.setFlag(false);
      });
      if (var1.isRelease(this.shulkerKey.getKeyCode())) {
         Slot var2 = InventoryUtils.method910(var0 -> var0.getStack().getItem() instanceof BlockItem var1x && var1x.getBlock() instanceof ShulkerBoxBlock);
         if (var2 == null) {
            Zenith.getInstance()
               .getHudHolder()
               .method6972(
                  "M",
                  Text.of(
                     Text.of("Шалкер")
                        .copy()
                        .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getPrimaryColor().method1445()))
                        .append(
                           Text.of(" не найден")
                              .copy()
                              .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()))
                        )
                  )
               );
            return;
         }

         this.slot = var2;
         TaskScheduler.Entry2 var3 = new TaskScheduler.Entry2(ServerHelperModule.class);
         var3.method5120(
            HandleInputPostEvent.class,
            var1x -> {
               if (!mc.player.lastPlayerInput.jump()
                  && !mc.player.isSprinting()
                  && !mc.player.lastPlayerInput.forward()
                  && !mc.player.lastPlayerInput.backward()
                  && !mc.player.lastPlayerInput.left()
                  && !mc.player.lastPlayerInput.right()) {
                  InventoryUtils.method900(var2, Hand.MAIN_HAND, false);
                  InventoryUtils.closeScreen();
                  return true;
               } else {
                  return false;
               }
            }
         );
         var3.method5120(HandleInputPostEvent.class, var0 -> true);
         var3.method5120(
            HandleInputEvent.class,
            var1x -> {
               if (var1x.isCancelled()) {
                  return false;
               } else if (!mc.player.lastPlayerInput.jump()
                  && !mc.player.isSprinting()
                  && !mc.player.lastPlayerInput.forward()
                  && !mc.player.lastPlayerInput.backward()
                  && !mc.player.lastPlayerInput.left()
                  && !mc.player.lastPlayerInput.right()) {
                  UseItemUtils.useItem(Hand.MAIN_HAND);
                  var1x.cancel();
                  this.stopWatch2.reset();
                  return true;
               } else {
                  return false;
               }
            }
         );
         var3.method5120(
            HandleInputPostEvent.class,
            var2x -> {
               if (!mc.player.lastPlayerInput.jump()
                  && !mc.player.isSprinting()
                  && !mc.player.lastPlayerInput.forward()
                  && !mc.player.lastPlayerInput.backward()
                  && !mc.player.lastPlayerInput.left()
                  && !mc.player.lastPlayerInput.right()) {
                  if (mc.player.currentScreenHandler instanceof PlayerScreenHandler) {
                     if (this.stopWatch2.method7546(1000L)) {
                        InventoryUtils.method900(var2, Hand.MAIN_HAND, true);
                        InventoryUtils.closeScreen();
                        return true;
                     }
                  } else {
                     this.stopWatch2.setLongValue(0L);
                  }

                  return false;
               } else {
                  return false;
               }
            }
         );
         var3.method5123(MoveInputEvent.class, var0 -> {
            var0.method3044();
            return true;
         });
         Zenith.getInstance().getTaskScheduler().method5110(var3);
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.autoFixAll.isEnabled() && this.method5181()) {
         if (Zenith.getInstance().getServerDetector().method1635()) {
            this.flag = true;
            this.stopWatch.reset();
         } else if (this.flag && this.stopWatch.method7546(2000L)) {
            this.flag = false;
            mc.player.networkHandler.sendChatCommand("fix all");
         }
      } else {
         this.flag = false;
      }
   }

   @EventTarget
   public void method5185(ChatSendEvent var1) {
      if (var1.method4603() && this.chatFilter.isEnabled() && this.method5181()) {
         String var2 = var1.getMessage();
         if (var2 != null && !var2.startsWith(".")) {
            if (pattern.matcher(var2).find()) {
               var1.cancel();
               ChatLogger.method2021("В сообщении запретное для RW слово — отправка отменена");
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()
         && var1.getPacket3() instanceof OpenScreenS2CPacket var2
         && this.closeMenu.isEnabled()
         && this.method5181()
         && mc.player != null
         && mc.player.age < 100
         && var2.getName().getString().contains("ꈁꀀꈂꌁꈂꀁ§0ꈃꄀ")) {
         var1.cancel();
         int var24 = var2.getSyncId();
         mc.execute(() -> {
            if (mc.getNetworkHandler() != null) {
               mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(var24));
            }
         });
      } else {
         this.method5190(var1);
         if (this.consumablesSetting.isEnabled()) {
            if (Zenith.getInstance().getServerDetector().method1639() && var1.getPacket3() instanceof ChunkDeltaUpdateS2CPacket var14) {
               mc.execute(() -> this.method5186(var14));
            }

            if (var1.getPacket3() instanceof PlaySoundS2CPacket var15
               && Zenith.getInstance().getServerDetector().method1642()
               && var15.getSound().getKey().isPresent()
               && var15.getSound().getKey().get().getValue().getPath().equals("block.beacon.deactivate")) {
               this.method5206(Items.NETHER_STAR, new Vec3d(var15.getX(), var15.getY(), var15.getZ()), System.currentTimeMillis() + 15000L);
            }

            if (Zenith.getInstance().getServerDetector().method1642() && var1.getPacket3() instanceof ParticleS2CPacket var16) {
               Class var21 = ((ParticleFactory)((MinecraftClientAccessor)(Object)mc)
                     .zenith$particleSpriteManager()
                     .getParticleFactories()
                     .get(Registries.PARTICLE_TYPE.getRawId(var16.getParameters().getType())))
                  .getClass();
               if (var21 == Factory.class) {
                  this.method5206(Items.PRISMARINE_SHARD, new Vec3d(var16.getX(), var16.getY(), var16.getZ()), System.currentTimeMillis() + 11000L);
               }
            }
         }

         if (var1.getPacket3() instanceof GameMessageS2CPacket var17 && this.autoPointSetting.isEnabled() && this.autoPointSetting.getVisible().get()) {
            Text var23 = var17.content();
            String var4 = var23.toString();
            String var5 = var23.getString();
            String var6 = StringUtils.substringBetween(var5, "|||   [", "]   ");
            if (var6 != null) {
               String var7 = StringUtils.substringBetween(var4, "value='/gps ", "'");
               String var8 = StringUtils.substringBetween(var5, "Уровень лута: ", "\n ║");
               String var9 = StringUtils.substringBetween(var5, "Призван игроком: ", "\n ║");
               if (var7 != null) {
                  String[] var10 = var7.split(" ");
                  Vec3d var11 = BlockPos.ofFloored(Integer.parseInt(var10[0]), Integer.parseInt(var10[1]), Integer.parseInt(var10[2])).toCenterPos();
                  switch (var6) {
                     case "Мистический сундук":
                        this.method5205(var6, var8, var9, var11, "overworld", 300, 0);
                        break;
                     case "Вулкан":
                        this.method5205(var6, var8, var9, var11, "overworld", 300, 120);
                        break;
                     case "Метеоритный дождь":
                     case "Маяк убийца":
                     case "Мистический Алтарь":
                        this.method5205(var6, var8, var9, var11, "overworld", 360, 0);
                        break;
                     case "Загадочный маяк":
                        this.method5205(var6, var8, var9, var11, "overworld", 60, 180);
                  }
               } else {
                  switch (var6) {
                     case "Сундук смерти":
                        this.method5205(var6, var8, var9, BlockPos.ofFloored(-155.0, 64.0, 205.0).toCenterPos(), "lobby", 300, 0);
                        break;
                     case "Адская резня":
                        this.method5205(var6, var8, var9, BlockPos.ofFloored(48.0, 87.0, 73.0).toCenterPos(), "lobby", 180, 120);
                  }
               }
            }
         }
      }
   }

   private void method5186(ChunkDeltaUpdateS2CPacket var1) {
      long var2 = System.currentTimeMillis();
      this.method5187(var2);
      var1.visitUpdates((var3, var4) -> {
         BlockPos var5 = var3.toImmutable();
         this.linkedHashMap.remove(var5);
         this.linkedHashMap.put(var5, new ServerHelperModule.Data5(var4, var2));
      });
      var1.visitUpdates((var1x, var2x) -> {
         Vec3d var3 = var1x.add(0, 0, 0).toCenterPos();
         if (this.linkedHashMap.size() > 50 && this.linkedHashMap.size() < 600) {
            if (this.method5209(var1x.up(2))) {
               this.method5206(Items.NETHERITE_SCRAP, var3, System.currentTimeMillis() + 15000L);
            } else if (this.method5211(var1x.up(3))) {
               this.method5206(Items.NETHERITE_SCRAP, var3, System.currentTimeMillis() + 30000L);
            }
         }
      });
   }

   private void method5187(long var1) {
      var var3 = this.linkedHashMap.values().iterator();

      while (var3.hasNext() && var1 - ((ServerHelperModule.Data5)var3.next()).time() > 2000L) {
         var3.remove();
      }
   }

   private BlockState method5188(BlockPos var1) {
      ServerHelperModule.Data5 var2 = this.linkedHashMap.get(var1);
      return var2 == null ? null : var2.state();
   }

   @EventTarget
   public void method5189(WorldRenderEvent var1) {
      if (System.currentTimeMillis() % 5000L < 16L) {
         this.method5214();
      }

      BlockPos var4 = mc.player.getBlockPos();
      Vec3d var5 = Vec3d.ZERO;
      MatrixStack var6 = var1.getMatrixStack();
      this.items3.stream().filter(var0 -> var0.class27.isValue()).forEach(var4x -> {
         String var5x = var4x.bindSetting.getKey();
         switch (var5x) {
            case "module.serverHelper.trap":
            case "module.serverHelper.normalTrap":
               this.method5197(var4, var5, 1.99F, Zenith.getInstance().getStyleManager().getClientColor(90).method7287());
               break;
            case "module.serverHelper.disorient":
            case "module.serverHelper.fireTornado":
            case "module.serverHelper.clearDust":
               this.method5198(var6, var4x.floatValue, ColorUtils.CONST_intValue);
               break;
            case "module.serverHelper.explosiveThing":
               this.method5198(var6, 5.0F, Zenith.getInstance().getStyleManager().getClientColor(90).method7287());
               break;
            case "module.serverHelper.plast":
               float var7 = MathHelper.wrapDegrees(mc.player.getYaw());
               if (Math.abs(mc.player.getPitch()) > 60.0F) {
                  BlockPos var8 = var4.up().offset(mc.player.getFacing(), 3);
                  Vec3d var9 = Vec3d.of(var8.east(3).south(3).down()).add(var5);
                  Vec3d var10 = Vec3d.of(var8.west(2).north(2).up()).add(var5);
                  GlowUtil.method2232(new Box(var9, var10), Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 3.0F, true, true, true);
               } else if (var7 <= -157.5F || var7 >= 157.5F) {
                  BlockPos var14 = var4.north(3).up();
                  Vec3d var18 = Vec3d.of(var14.down(2).east(3)).add(var5);
                  Vec3d var22 = Vec3d.of(var14.up(3).west(2).south(2)).add(var5);
                  GlowUtil.method2232(new Box(var18, var22), Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 3.0F, true, true, true);
               } else if (var7 <= -112.5F) {
                  this.method5200(var4.east(5).south().down(), var5, Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), -1, true);
               } else if (var7 <= -67.5F) {
                  BlockPos var11 = var4.east(2).up();
                  Vec3d var15 = Vec3d.of(var11.down(2).south(3)).add(var5);
                  Vec3d var19 = Vec3d.of(var11.up(3).north(2).east(2)).add(var5);
                  GlowUtil.method2232(new Box(var15, var19), Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 3.0F, true, true, true);
               } else if (var7 <= -22.5F) {
                  this.method5200(var4.east(5).down(), var5, Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 1, false);
               } else if (var7 >= -22.5 && var7 <= 22.5) {
                  BlockPos var13 = var4.south(2).up();
                  Vec3d var17 = Vec3d.of(var13.down(2).east(3)).add(var5);
                  Vec3d var21 = Vec3d.of(var13.up(3).west(2).south(2)).add(var5);
                  GlowUtil.method2232(new Box(var17, var21), Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 3.0F, true, true, true);
               } else if (var7 <= 67.5F) {
                  this.method5200(var4.west(4).down(), var5, Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 1, true);
               } else if (var7 <= 112.5F) {
                  BlockPos var12 = var4.west(3).up();
                  Vec3d var16 = Vec3d.of(var12.down(2).south(3)).add(var5);
                  Vec3d var20 = Vec3d.of(var12.up(3).north(2).east(2)).add(var5);
                  GlowUtil.method2232(new Box(var16, var20), Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 3.0F, true, true, true);
               } else if (var7 <= 157.5F) {
                  this.method5200(var4.west(4).south().down(), var5, Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), -1, false);
               }
               break;
            case "module.serverHelper.explosiveTrap":
               this.method5197(var4, var5, 3.99F, ColorUtils.CONST_intValue);
               break;
            case "module.serverHelper.stun":
               this.method5197(var4, var5, 15.01F, ColorUtils.CONST_intValue);
               break;
            case "module.serverHelper.snowball":
               PredictionsModule.INSTANCE.method2902(var6, PredictionsModule.items12);
         }
      });
      if (this.antiFlyRadius.isEnabled()
         && this.method5181()
         && (mc.player.getMainHandStack().isOf(Items.FIREWORK_STAR) || mc.player.getOffHandStack().isOf(Items.FIREWORK_STAR))) {
         this.method5198(var6, 16.0F, ColorUtils.CONST_intValue);
      }

      this.items2.forEach(var2 -> {
         if (var2.item == Items.NETHER_STAR) {
            this.method5197(BlockPos.ofFloored(var2.vec3d), var5, 15.01F, Zenith.getInstance().getStyleManager().getClientColor(90).method7287());
         }
      });
   }

   @EventTarget
   public void onDraw(DrawEvent var1) {
      CustomDrawContext var2 = var1.getCustomDrawContext();
      MatrixUtils.method666(var2.getMatrices());
      if (this.hitDisplaySetting.isEnabled() && this.hitDisplaySetting.getVisible().get()) {
         this.method5194(var2);
      }

      this.items2
         .forEach(
            var1x -> {
               double var2x = (var1x.doubleValue - System.currentTimeMillis()) / 1000.0;
               Vec3d var4 = ProjectionUtils.method301(var1x.vec3d);
               String var5 = MathUtils.method2652(var2x, 0.1F) + "с";
               Font var6 = Fonts.MEDIUM.getFont(10.0F);
               float var7 = var6.width(var5);
               float var8 = (float)(var4.x - var7 / 2.0F);
               float var9 = (float)var4.y;
               if (ProjectionUtils.method302(var1x.vec3d)
                  && var1x.intValue == Zenith.getInstance().getServerDetector().getAnarchy()
                  && Zenith.getInstance().getServerDetector().method1638().equals(var1x.text)) {
                  RenderShaders.method5286(
                     MatrixUtils.method664(var1.getCustomDrawContext().getMatrices()),
                     var8 - 4.0F,
                     var9 - 4.0F,
                     16.8F + var6.width(var5) + 8.0F,
                     20.8F,
                     22.0F,
                     ProxyDotRadiusData.method8069(4.0F),
                     ZenithColor.INSTANCE
                  );
                  var1.getCustomDrawContext()
                     .drawRoundedRect(
                        var8 - 4.0F,
                        var9 - 4.0F,
                        16.8F + var6.width(var5) + 8.0F,
                        20.8F,
                        ProxyDotRadiusData.method8069(4.0F),
                        Zenith.getInstance().getStyleManager().getCurrentStyle().getHudBackground().getZenithColor()
                     );
                  RenderShaders.method5278(
                     MatrixUtils.method664(var1.getCustomDrawContext().getMatrices()),
                     var8 - 4.0F,
                     var9 - 4.0F,
                     16.8F + var6.width(var5) + 8.0F,
                     20.8F,
                     0.1F,
                     10.0F,
                     Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor(),
                     ProxyDotRadiusData.method8069(4.0F)
                  );
                  var1.getCustomDrawContext()
                     .drawText(
                        var6,
                        var5,
                        var8 + 12.8F + 4.0F,
                        var9 + 2.5F,
                        Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor()
                     );
                  var1.getCustomDrawContext().getMatrices().pushMatrix();
                  var1.getCustomDrawContext().getMatrices().translate(var8, var9);
                  var1.getCustomDrawContext().getMatrices().scale(0.8F, 0.8F);
                  var1.getCustomDrawContext().drawItem(var1x.item.getDefaultStack(), 0, 0);
                  var1.getCustomDrawContext().getMatrices().popMatrix();
               }
            }
         );
      this.items2.removeIf(var0 -> var0.doubleValue - System.currentTimeMillis() <= 0.0);
      this.items.removeIf(var0 -> var0.doubleValue2 + 90000.0 - System.currentTimeMillis() <= 0.0);
   }

   private void method5190(PacketEvent var1) {
      if (this.hitDisplaySetting.isEnabled() && this.hitDisplaySetting.getVisible().get() && mc.world != null) {
         long var2 = System.currentTimeMillis();
         if (var1.getPacket3() instanceof EntityTrackerUpdateS2CPacket var4) {
            this.method5191(var4, var2);
         }
      } else {
         if (this.items4 != null && this.items4.getClass() == EmptyMap.class ? false : !this.items4.isEmpty()) {
            this.items4.clear();
         }
      }
   }

   private void method5191(EntityTrackerUpdateS2CPacket var1, long var2) {
      for (SerializedEntry var5 : var1.trackedValues()) {
         if (var5.id() == 10) {
            if (this.method5192(var5.value())) {
               ServerHelperModule.Data var6 = this.items4.get(var1.id());
               if (var6 == null || var6.getLongValue2() <= var2) {
                  this.items4.put(var1.id(), new ServerHelperModule.Data(var2, var2 + 10000L));
               }
            }

            return;
         }
      }
   }

   private boolean method5192(Object var1) {
      return var1 instanceof List var2
         ? var2.stream().filter(TintedParticleEffect.class::isInstance).map(TintedParticleEffect.class::cast).anyMatch(x -> this.method5193((TintedParticleEffect)x))
         : false;
   }

   private boolean method5193(TintedParticleEffect var1) {
      return Math.abs(var1.getRed() - 0.29411766F) <= 1.0E-4F
         && Math.abs(var1.getGreen() - 0.34509805F) <= 1.0E-4F
         && Math.abs(var1.getBlue() - 0.4117647F) <= 1.0E-4F;
   }

   private void method5194(CustomDrawContext var1) {
      if (mc.player != null && mc.world != null) {
         long var2 = System.currentTimeMillis();
         this.items4
            .entrySet()
            .removeIf(var2x -> var2x.getValue().getLongValue2() <= var2 || !(mc.world.getEntityById(var2x.getKey()) instanceof PlayerEntity));

         for (PlayerEntity var5 : mc.world.getPlayers()) {
            if (var5 != mc.player) {
               ServerHelperModule.Data var6 = this.items4.get(var5.getId());
               if (var6 != null && var6.getLongValue2() > var2) {
                  Vec3d var7 = MathUtils.method2656(var5).add(0.0, var5.getHeight() * 0.5, 0.0);
                  if (ProjectionUtils.method302(var7)) {
                     Vec3d var8 = ProjectionUtils.method301(var7);
                     if (!(var8.z <= 0.0) && !(var8.z >= 1.0)) {
                        float var9 = 18.0F;
                        float var10 = MathHelper.clamp((float)(var6.getLongValue2() - var2) / 10000.0F, 0.0F, 1.0F);
                        float var11 = this.method5195(var6, var2);
                        float var12 = (float)var8.x - var9 / 2.0F;
                        float var13 = (float)var8.y - var9 / 2.0F;
                        this.method5196(var1, var12, var13, var9, var10, var11);
                     }
                  }
               }
            }
         }
      }
   }

   private float method5195(ServerHelperModule.Data var1, long var2) {
      float var4 = MathHelper.clamp((float)(var2 - var1.getLongValue()) / 250.0F, 0.0F, 1.0F);
      float var5 = MathHelper.clamp((float)(var1.getLongValue2() - var2) / 250.0F, 0.0F, 1.0F);
      return Math.min(var4, var5);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void method5196(CustomDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      RenderState.method1883(1.0F, 1.0F, 1.0F, var6);

      try {
         var1.drawArcBorder(var2, var3, var4, var4, 1.0F, 360.0F, 0.5F, var7.getFieldBorder().getZenithColor());
         var1.drawArcBorder(var2, var3, var4, var4, 1.0F, 360.0F * var5, 0.5F, var7.getPrimaryColor().getZenithColor());
         float var8 = 0.65F;
         float var9 = (var4 - 16.0F * var8) / 2.0F;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(var2 + var9, var3 + var9);
         var1.getMatrices().scale(var8, var8);
         var1.drawItem(Items.SNOWBALL.getDefaultStack(), 0, 0);
         var1.getMatrices().popMatrix();
      } catch (Throwable var12) {
         RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
         throw var12;
      }

      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void method5197(BlockPos var1, Vec3d var2, float var3, int var4) {
      Box var5 = new Box(var1.up()).offset(var2).contract(0.0, 0.2F, 0.0).expand(var3);
      boolean var6 = mc.world
         .getPlayers()
         .stream()
         .anyMatch(
            var1x -> var1x != mc.player
               && var5.intersects(var1x.getBoundingBox())
               && !Zenith.getInstance().getFriends().method7627(var1x.getGameProfile().name())
         );
      GlowUtil.method2232(
         var5, var6 ? Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287() : var4, 3.0F, true, true, true
      );
   }

   private void method5198(MatrixStack var1, float var2, int var3) {
      float var4 = mc.player.getWidth() / 2.0F;
      int var5 = this.method5208(var2) ? Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287() : var3;
      Vec3d var6 = MathUtils.method2656(mc.player).add(var4, 0.02, var4);
      Vec3d var7 = var6.subtract(mc.getEntityRenderDispatcher().camera.getCameraPos());
      GL11.glEnable(2881);
      RenderState.method1871();
      RenderState.method1878();
      RenderState.method1882(false);
      RenderState.method1877();
      RenderState.method1874(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA);
      RenderState.method1898("core/position_color");
      BufferBuilder var8 = Tessellator.getInstance().begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
      int var9 = 0;

      for (byte var10 = 90; var9 <= var10; var9++) {
         Vec3d var11 = MathUtils.method2653(var9, var10, var2);
         Vec3d var12 = MathUtils.method2653(var9 + 1, var10, var2);
         GlowUtil.method2235(
            var1, var8, var7.add(var11), var7.add(var11.x, var11.y + 2.0, var11.z), ColorUtils.method6439(var5, 0.2F), ColorUtils.method6439(var5, 0.0F)
         );
         GlowUtil.method2245(var6.add(var11), var6.add(var12), var5, 2.0F, true);
      }

      var9 = 0;

      for (byte var14 = 90; var9 <= var14; var9++) {
         Vec3d var15 = MathUtils.method2653(var9, var14, var2);
         GlowUtil.method2235(
            var1, var8, var7.add(var15), var7.add(var15.x, var15.y - 2.0, var15.z), ColorUtils.method6439(var5, 0.2F), ColorUtils.method6439(var5, 0.0F)
         );
      }

      ImmediateRenderer.method8015(var8.end());
      RenderState.method1879();
      RenderState.method1882(true);
      RenderState.method1872();
      GL11.glDisable(2881);
   }

   private void method5199(MatrixStack var1, Font var2, List<String> var3, Vec3d var4) {
      float var5 = 0.0F;

      for (int var6 = 0; var6 < var3.size(); var6++) {
         String var7 = (String)var3.get(var6);
         float var8 = var2.width(var7);
         float var10000 = (float)(var4.x - var8 / 2.0F);
         var5 += 10.0F;
      }
   }

   public void method5200(BlockPos var1, Vec3d var2, int var3, int var4, boolean var5) {
      Vec3d var6 = Vec3d.of(var1).add(var2);
      float var7 = 2.0F;
      int var8 = ColorUtils.method6439(var3, 0.15F);
      this.method5201(var6, var3, var7, var4, var5);
      this.method5201(var6, var3, var7, var4, var5);
      this.method5202(var6, var3, var7, var4, var5);
      this.method5203(var6, var8, var4, var5);
      this.method5203(var6, var8, var4, var5);
      this.method5204(var6, var8, var4, var5);
   }

   private void method5201(Vec3d var1, int var2, float var3, int var4, boolean var5) {
      float var6 = var5 ? var4 : -var4;
      Vec3d var7;
      GlowUtil.method2245(var1, var7 = var1.add(var6, 0.0, 0.0), var2, var3, true);

      for (int var8 = 0; var8 < 4; var8++) {
         Vec3d var9;
         GlowUtil.method2245(var7, var9 = var7.add(0.0, 0.0, var4), var2, var3, true);
         GlowUtil.method2245(var9, var7 = var9.add(var6, 0.0, 0.0), var2, var3, true);
      }

      Vec3d var10;
      GlowUtil.method2245(var7, var10 = var7.add(0.0, 0.0, var4), var2, var3, true);
      GlowUtil.method2245(var10, var7 = var10.add(var6 * -2.0F, 0.0, 0.0), var2, var3, true);

      for (int var13 = 0; var13 < 3; var13++) {
         Vec3d var12;
         GlowUtil.method2245(var7, var12 = var7.add(0.0, 0.0, var4 * -1), var2, var3, true);
         GlowUtil.method2245(var12, var7 = var12.add(var6 * -1.0F, 0.0, 0.0), var2, var3, true);
      }

      GlowUtil.method2245(var7, var7.add(0.0, 0.0, var4 * -2), var2, var3, true);
   }

   private void method5202(Vec3d var1, int var2, float var3, int var4, boolean var5) {
      float var6 = var5 ? var4 : -var4;
      GlowUtil.method2245(var1, var1.add(0.0, 5.0, 0.0), var2, var3, true);
      Vec3d var8;
      GlowUtil.method2245(var8 = var1.add(var6, 0.0, 0.0), var8.add(0.0, 5.0, 0.0), var2, var3, true);

      for (int var7 = 0; var7 < 4; var7++) {
         GlowUtil.method2245(var8 = var8.add(var6, 0.0, var4), var8.add(0.0, 5.0, 0.0), var2, var3, true);
      }

      GlowUtil.method2245(var1 = var8.add(0.0, 0.0, var4), var1.add(0.0, 5.0, 0.0), var2, var3, true);
      Vec3d var10;
      GlowUtil.method2245(var10 = var1.add(var6 * -2.0F, 0.0, 0.0), var10.add(0.0, 5.0, 0.0), var2, var3, true);

      for (int var11 = 0; var11 < 3; var11++) {
         GlowUtil.method2245(var10 = var10.add(var6 * -1.0F, 0.0, var4 * -1), var10.add(0.0, 5.0, 0.0), var2, var3, true);
      }
   }

   private void method5203(Vec3d var1, int var2, int var3, boolean var4) {
      var1 = var1.add(0.0, 0.001, 0.0);
      float var5 = var4 ? var3 : -var3;
      GlowUtil.method2247(var1, var1.add(var5, 0.0, 0.0), var1.add(var5, 0.0, var3 * 2), var1.add(0.0, 0.0, var3 * 2), var2, true);

      for (int var6 = 0; var6 < 3; var6++) {
         GlowUtil.method2247(
            var1 = var1.add(var5, 0.0, var3), var1.add(var5, 0.0, 0.0), var1.add(var5, 0.0, var3 * 2), var1.add(0.0, 0.0, var3 * 2), var2, true
         );
      }

      Vec3d var8;
      GlowUtil.method2247(var8 = var1.add(var5, 0.0, var3), var8.add(var5, 0.0, 0.0), var8.add(var5, 0.0, var3), var8.add(0.0, 0.0, var3), var2, true);
   }

   private void method5204(Vec3d var1, int var2, int var3, boolean var4) {
      float var5 = var4 ? var3 : -var3;
      GlowUtil.method2247(var1, var1.add(var5, 0.0, 0.0), var1.add(var5, 5.0, 0.0), var1.add(0.0, 5.0, 0.0), var2, true);

      for (int var6 = 0; var6 < 4; var6++) {
         Vec3d var7;
         GlowUtil.method2247(var7 = var1.add(var5, 0.0, 0.0), var7.add(0.0, 0.0, var3), var7.add(0.0, 5.0, var3), var7.add(0.0, 5.0, 0.0), var2, true);
         GlowUtil.method2247(var1 = var7.add(0.0, 0.0, var3), var1.add(var5, 0.0, 0.0), var1.add(var5, 5.0, 0.0), var1.add(0.0, 5.0, 0.0), var2, true);
      }

      Vec3d var8;
      GlowUtil.method2247(var8 = var1.add(var5, 0.0, 0.0), var8.add(0.0, 0.0, var3), var8.add(0.0, 5.0, var3), var8.add(0.0, 5.0, 0.0), var2, true);
      GlowUtil.method2247(
         var1 = var8.add(0.0, 0.0, var3), var1.add(var5 * -2.0F, 0.0, 0.0), var1.add(var5 * -2.0F, 5.0, 0.0), var1.add(0.0, 5.0, 0.0), var2, true
      );
      var1 = var1.add(var5 * -1.0F, 0.0, 0.0);

      for (int var13 = 0; var13 < 3; var13++) {
         Vec3d var11;
         GlowUtil.method2247(
            var11 = var1.add(var5 * -1.0F, 0.0, 0.0), var11.add(0.0, 0.0, var3 * -1), var11.add(0.0, 5.0, var3 * -1), var11.add(0.0, 5.0, 0.0), var2, true
         );
         GlowUtil.method2247(
            var1 = var11.add(0.0, 0.0, var3 * -1), var1.add(var5 * -1.0F, 0.0, 0.0), var1.add(var5 * -1.0F, 5.0, 0.0), var1.add(0.0, 5.0, 0.0), var2, true
         );
      }

      Vec3d var12;
      GlowUtil.method2247(
         var12 = var1.add(var5 * -1.0F, 0.0, 0.0), var12.add(0.0, 0.0, var3 * -2), var12.add(0.0, 5.0, var3 * -2), var12.add(0.0, 5.0, 0.0), var2, true
      );
   }

   private void method5205(String var1, String var2, String var3, Vec3d var4, String var5, int var6, int var7) {
      if (!mc.isOnThread()) {
         mc.execute(() -> this.method5205(var1, var2, var3, var4, var5, var6, var7));
      } else {
         if (this.items.stream().noneMatch(var1x -> var1x.vec3d.equals(var4))) {
            Island.method2880(var1, var6 * 1000L);
            long var8;
            long var10 = (var8 = System.currentTimeMillis() + var6 * 1000L) + var7 * 1000L;
            this.items.add(new ServerHelperModule.Data2(var1, var2, var3, var4, var5, Zenith.getInstance().getServerDetector().getAnarchy(), var8, var10));
         }
      }
   }

   private void method5206(Item var1, Vec3d var2, double var3) {
      if (!mc.isOnThread()) {
         mc.execute(() -> this.method5206(var1, var2, var3));
      } else {
         if (this.items2.stream().noneMatch(var1x -> var1x.vec3d.equals(var2))) {
            long var5 = (long)(var3 - System.currentTimeMillis());
            String var7 = var1 == Items.NETHER_STAR ? "beacon" : (var1 == Items.PRISMARINE_SHARD ? "blast" : (var5 > 20000L ? "bigTrap" : "trap"));
            Island.method2879(var7, var5);
            this.items2
               .add(
                  new ServerHelperModule.Data4(
                     var1, var2, Zenith.getInstance().getServerDetector().method1638(), Zenith.getInstance().getServerDetector().getAnarchy(), var3
                  )
               );
         }
      }
   }

   private Vector4f method5207(Font var1, List<String> var2, int var3, float var4) {
      if (var3 == 0) {
         return var1.width((String)var2.get(var3 + 1)) >= var4 ? new Vector4f(2.0F, 0.0F, 2.0F, 0.0F) : new Vector4f(2.0F);
      } else if (var3 == var2.size() - 1) {
         return var1.width((String)var2.get(var3 - 1)) >= var4 ? new Vector4f(0.0F, 2.0F, 0.0F, 2.0F) : new Vector4f(2.0F);
      } else {
         float var5 = var1.width((String)var2.get(var3 - 1));
         float var6 = var1.width((String)var2.get(var3 + 1));
         return var5 >= var4 ? (var6 >= var4 ? new Vector4f() : new Vector4f(0.0F, 2.0F, 0.0F, 2.0F)) : new Vector4f(2.0F);
      }
   }

   private boolean method5208(float var1) {
      return var1 == 0.0F
         || mc.world
            .getPlayers()
            .stream()
            .anyMatch(
               var1x -> var1x != mc.player
                  && !Zenith.getInstance().getFriends().method7627(var1x.getGameProfile().name())
                  && mc.player.distanceTo(var1x) <= var1
            );
   }

   private boolean method5209(BlockPos var1) {
      long var2;
      boolean var10000;
      label20: {
         var2 = System.currentTimeMillis();
         if (this.items7 != null) {
            if (this.items7.getClass() == EmptyMap.class) {
               var10000 = false;
               break label20;
            }
         }

         var10000 = this.items7.containsKey(var1);
      }

      if (var10000 && var2 - this.items7.get(var1) < 1000L) {
         return this.items5.get(var1);
      } else {
         boolean var4 = this.method5210(var1);
         this.items5.put(var1, var4);
         this.items7.put(var1, var2);
         return var4;
      }
   }

   private boolean method5210(BlockPos var1) {
      int var2 = 0;

      for (BlockPos var4 : UseItemUtils.method6505(var1, 2.0F)) {
         if (MathUtils.method2662(var4.toCenterPos(), var1.toCenterPos()) < 2.0) {
            BlockState var5 = this.method5188(var4);
            if (var5 != null && !var5.isAir()) {
               var2++;
            }
         } else if (!var4.equals(var1.up(2).north().east())
            && !var4.equals(var1.up(2).north().west())
            && !var4.equals(var1.up(2).south().east())
            && !var4.equals(var1.up(2).south().west())) {
            BlockState var6 = this.method5188(var4);
            if (var6 == null || var6.isAir()) {
               var2++;
            }
         }

         if (var2 > 1) {
            return false;
         }
      }

      return true;
   }

   private boolean method5211(BlockPos var1) {
      long var2;
      boolean var10000;
      label20: {
         var2 = System.currentTimeMillis();
         if (this.items8 != null) {
            if (this.items8.getClass() == EmptyMap.class) {
               var10000 = false;
               break label20;
            }
         }

         var10000 = this.items8.containsKey(var1);
      }

      if (var10000 && var2 - this.items8.get(var1) < 1000L) {
         return this.items6.get(var1);
      } else {
         boolean var4 = this.method5212(var1);
         this.items6.put(var1, var4);
         this.items8.put(var1, var2);
         return var4;
      }
   }

   private boolean method5212(BlockPos var1) {
      int var2 = 0;

      for (BlockPos var4 : UseItemUtils.method6505(var1, 3.0F)) {
         if (Math.abs(var4.getX() - var1.getX()) <= 2 && Math.abs(var4.getY() - var1.getY()) <= 2 && Math.abs(var4.getZ() - var1.getZ()) <= 2) {
            BlockState var6 = this.method5188(var4);
            if (var6 != null && !var6.isAir()) {
               var2++;
            }
         } else if (!var4.equals(var1.up(3))) {
            BlockState var5 = this.method5188(var4);
            if (var5 == null || var5.isAir()) {
               var2++;
            }
         }

         if (var2 > 1) {
            return false;
         }
      }

      return true;
   }

   private static boolean method5213(BlockState var0) {
      return var0 != null && !var0.isAir();
   }

   private void method5214() {
      long var1 = System.currentTimeMillis();
      this.items7.entrySet().removeIf(var2 -> var1 - var2.getValue() > 1000L);
      this.items5.entrySet().removeIf(var1x -> {
         var var10000 = this.items7;
         Object var10001 = var1x.getKey();
         if (var10000 != null) {
            if (var10000.isEmpty()) {
               return true;
            }
         }

         return !var10000.containsKey(var10001);
      });
      this.items8.entrySet().removeIf(var2 -> var1 - var2.getValue() > 1000L);
      this.items6.entrySet().removeIf(var1x -> {
         var var10000 = this.items8;
         Object var10001 = var1x.getKey();
         if (var10000 != null) {
            if (var10000.isEmpty()) {
               return true;
            }
         }

         return !var10000.containsKey(var10001);
      });
      if (this.items5.size() > 1000) {
         this.items5.clear();
         this.items7.clear();
      }

      if (this.items6.size() > 1000) {
         this.items6.clear();
         this.items8.clear();
      }
   }

   private boolean method5215(BlockPos var1, byte[][][] var2, int var3, int var4) {
      int var5 = 0;
      Mutable var6 = new Mutable();

      for (int var7 = -var3; var7 <= var3; var7++) {
         for (int var8 = -var3; var8 <= var3; var8++) {
            for (int var9 = -var3; var9 <= var3; var9++) {
               byte var10;
               if ((var10 = var2[var7 + var3][var8 + var3][var9 + var3]) != -1) {
                  var6.set(var1.getX() + var9, var1.getY() + var7, var1.getZ() + var8);
                  BlockState var11 = this.method5188(var6);
                  boolean var12 = method5213(var11);
                  if (var10 != 0 && !var12 || var10 == 0 && var12) {
                     if (++var5 > var4) {
                        return false;
                     }
                  }
               }
            }
         }
      }

      return true;
   }

   public List<ServerHelperModule.Data3> method5216() {
      return Collections.unmodifiableList(this.items3);
   }

   public BindSetting getShulkerKey() {
      return this.shulkerKey;
   }

   public Slot method5217() {
      return InventoryUtils.method910(var0 -> var0.getStack().getItem() instanceof BlockItem var1 && var1.getBlock() instanceof ShulkerBoxBlock);
   }

   private static final class Data {
      private final long longValue;
      private final long longValue2;

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + "]";
      }

      private Data(long var1, long var3) {
         this.longValue = var1;
         this.longValue2 = var3;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public long getLongValue2() {
         return this.longValue2;
      }
   }

   public static final class Data2 {
      private final String text;
      private final String text2;
      private final String text3;
      private final Vec3d vec3d;
      private final String text4;
      private final int intValue;
      private final double doubleValue;
      private final double doubleValue2;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public Vec3d vec3d() {
         return this.vec3d;
      }

      public String text4() {
         return this.text4;
      }

      public int intValue() {
         return this.intValue;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3, vec3d, text4, intValue, doubleValue, doubleValue2);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "vec3d=" + this.vec3d + ", " + "text4=" + this.text4 + ", " + "intValue=" + this.intValue + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + "]";
      }

      public Data2(String var1, String var2, String var3, Vec3d var4, String var5, int var6, double var7, double var9) {
         this.text = var1;
         this.text2 = var2;
         this.text3 = var3;
         this.vec3d = var4;
         this.text4 = var5;
         this.intValue = var6;
         this.doubleValue = var7;
         this.doubleValue2 = var9;
      }

      public String name() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public Vec3d getVec3d() {
         return this.vec3d;
      }

      public String getText4() {
         return this.text4;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }
   }

   public static final class Data3 {
      private final Item item;
      private final BindSetting bindSetting;
      private final float floatValue;
      private final Class27 class27;
      private final Predicate<Slot> predicate;

      public Item item() {
         return this.item;
      }

      public BindSetting bindSetting() {
         return this.bindSetting;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public Class27 class27() {
         return this.class27;
      }

      public Predicate<Slot> predicate() {
         return this.predicate;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.item, other.item) && java.util.Objects.equals(this.bindSetting, other.bindSetting) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.class27, other.class27) && java.util.Objects.equals(this.predicate, other.predicate);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(item, bindSetting, floatValue, class27, predicate);
      }

      @Override
      public String toString() {
         return "Data3[" + "item=" + this.item + ", " + "bindSetting=" + this.bindSetting + ", " + "floatValue=" + this.floatValue + ", " + "class27=" + this.class27 + ", " + "predicate=" + this.predicate + "]";
      }

      public Data3(Item var1, BindSetting var2, float var3, Class27 var4) {
         this(var1, var2, var3, var4, null);
      }

      public Data3(Item var1, BindSetting var2, float var3, Class27 var4, Predicate<Slot> var5) {
         this.item = var1;
         this.bindSetting = var2;
         this.floatValue = var3;
         this.class27 = var4;
         this.predicate = var5;
      }

      public Slot method5218() {
         return this.predicate != null ? InventoryUtils.method907(this.item, this.predicate) : InventoryUtils.method906(this.item);
      }

      public Predicate<Slot> method5219() {
         return this.predicate != null ? this.predicate : var0 -> true;
      }

      public Item getItem() {
         return this.item;
      }

      public BindSetting getBindSetting() {
         return this.bindSetting;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public Class27 getClass27() {
         return this.class27;
      }

      public Predicate<Slot> getPredicate() {
         return this.predicate;
      }
   }

   public static final class Data4 {
      private final Item item;
      private final Vec3d vec3d;
      private final String text;
      private final int intValue;
      private final double doubleValue;

      public Item item() {
         return this.item;
      }

      public Vec3d vec3d() {
         return this.vec3d;
      }

      public String text() {
         return this.text;
      }

      public int intValue() {
         return this.intValue;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.item, other.item) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.doubleValue, other.doubleValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(item, vec3d, text, intValue, doubleValue);
      }

      @Override
      public String toString() {
         return "Data4[" + "item=" + this.item + ", " + "vec3d=" + this.vec3d + ", " + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "doubleValue=" + this.doubleValue + "]";
      }

      public Data4(Item var1, Vec3d var2, String var3, int var4, double var5) {
         this.item = var1;
         this.vec3d = var2;
         this.text = var3;
         this.intValue = var4;
         this.doubleValue = var5;
      }

      public Item getItem() {
         return this.item;
      }

      public Vec3d getVec3d() {
         return this.vec3d;
      }

      public String getText() {
         return this.text;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public double time() {
         return this.doubleValue;
      }
   }

   private static final class Data5 {
      private final BlockState blockState;
      private final long longValue;

      public BlockState blockState() {
         return this.blockState;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.blockState, other.blockState) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockState, longValue);
      }

      @Override
      public String toString() {
         return "Data5[" + "blockState=" + this.blockState + ", " + "longValue=" + this.longValue + "]";
      }

      private Data5(BlockState var1, long var2) {
         this.blockState = var1;
         this.longValue = var2;
      }

      public BlockState state() {
         return this.blockState;
      }

      public long time() {
         return this.longValue;
      }
   }
}
