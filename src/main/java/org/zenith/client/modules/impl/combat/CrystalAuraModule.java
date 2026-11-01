package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import kotlin.collections.EmptyList;
import kotlin.collections.EmptyMap;
import kotlin.collections.EmptySet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.events.Event5;
import org.zenith.client.events.Event9;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.PostMotionEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.misc.FreeCamModule;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.config.CombatLogger;
import org.zenith.utility.game.Class35;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.Class33;
import org.zenith.utility.network.PlacementQueue;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(
   name = "CrystalAura",
   description = "module.crystalaura.description",
   category = ModuleCategory.COMBAT
)
public final class CrystalAuraModule extends Module {
   public static final CrystalAuraModule INSTANCE = new CrystalAuraModule();
   private static final double[] CONST_doubleValues = new double[]{0.5, 0.3, 0.7, 0.15, 0.85};
   private static final double[] CONST_doubleValues2 = new double[]{0.5, 0.2, 0.8};
   private static final EquipmentSlot[] equipmentSlots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private static final int CONST_intValue = 3;
   private static final int CONST_intValue2 = 12;
   private static final int CONST_intValue3 = 10;
   private static final int CONST_intValue4 = 3;
   private static final int CONST_intValue5 = 10;
   private static final int CONST_intValue6 = 6;
   private static final int CONST_intValue7 = 3;
   private static final double CONST_doubleValue = 0.42;
   private static final int CONST_intValue8 = 18;
   private static final float CONST_floatValue = 0.8F;
   private static final float CONST_floatValue2 = 4.0F;
   private static final float CONST_floatValue3 = 4.0F;
   private static final int CONST_intValue9 = 10;
   private static final int CONST_intValue10 = 10;
   private static final int CONST_intValue11 = 5;
   private static final int CONST_intValue12 = 4;
   private static final int CONST_intValue13 = 60;
   private static final float CONST_floatValue4 = 3.0F;
   private static final int CONST_intValue14 = 40;
   private static final int CONST_intValue15 = 4;
   private static final int CONST_intValue16 = 4;
   private static final float CONST_floatValue5 = 3.0F;
   private static final int CONST_intValue17 = 12;
   private static final double CONST_doubleValue2 = 3.0;
   private static final double CONST_doubleValue3 = 2.3;
   private static final int CONST_intValue18 = 3;
   private static final int CONST_intValue19 = 10;
   private static final int CONST_intValue20 = 20;
   private static final float[][] CONST_floatValues = new float[][]{{0.0F, 0.0F}, {0.25F, 0.0F}, {-0.25F, 0.0F}, {0.0F, 0.25F}, {0.0F, -0.25F}};
   private static final long CONST_longValue = 400L;
   private static final float CONST_floatValue6 = 0.03F;
   private final ModeSetting preset = new ModeSetting(
      "module.crystalAura.preset",
      "module.crystalAura.preset.desc",
      "module.crystalAura.presetCustom",
      "Grim",
      "ReallyWorld",
      "DonutSMP",
      "module.crystalAura.presetNone"
   ) {
      @Override
      public void set(String var1) {
         super.set(this.getValues().stream().anyMatch(var1x -> var1x.getKey().equals(var1)) ? var1 : "Grim");
      }
   };
   private final SliderSetting targetRange = new SliderSetting(
      "module.crystalAura.targetRange", 10.0F, 4.0F, 16.0F, 0.5F, "module.crystalAura.targetRange.desc", "b"
   );
   private final SliderSetting minDamage = new SliderSetting("module.crystalAura.minDamage", 6.0F, 1.0F, 20.0F, 0.5F, "module.crystalAura.minDamage.desc", "hp");
   private final SliderSetting maxSelfDamage = new SliderSetting(
      "module.crystalAura.maxSelfDamage", 8.0F, 0.0F, 20.0F, 0.5F, "module.crystalAura.maxSelfDamage.desc", "hp"
   );
   private final SliderSetting faceplace = new SliderSetting("module.crystalAura.faceplace", 8.0F, 0.0F, 36.0F, 1.0F, "module.crystalAura.faceplace.desc", "hp");
   private final SliderSetting armorBreak = new SliderSetting(
      "module.crystalAura.armorBreak", 15.0F, 0.0F, 50.0F, 5.0F, "module.crystalAura.armorBreak.desc", "%"
   );
   private final SliderSetting predictTicks = new SliderSetting(
      "module.crystalAura.predictTicks", 1.0F, 0.0F, 6.0F, 1.0F, "module.crystalAura.predictTicks.desc", "t"
   );
   private final MultiSelectSetting features = new MultiSelectSetting("module.crystalAura.features", "module.crystalAura.features.desc");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.features, "module.crystalAura.prePlace", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.timing", true);
   private final MultiSelectSetting.Entry entry3 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.anchors", false);
   private final MultiSelectSetting.Entry entry4 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.autoObsidian", true);
   private final MultiSelectSetting.Entry entry5 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.autoDig", false);
   private final MultiSelectSetting.Entry entry6 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.fromInventory", true);
   private final MultiSelectSetting.Entry entry7 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.shield", false);
   private final MultiSelectSetting.Entry entry8 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.antiWeakness", true);
   private final MultiSelectSetting.Entry entry9 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.antiSuicide", true);
   private final MultiSelectSetting.Entry entry10 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.pauseOnUse", true);
   private final MultiSelectSetting.Entry entry11 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.yieldWebTrap", true);
   private final MultiSelectSetting.Entry entry12 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.render", true);
   private final MultiSelectSetting.Entry entry13 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.knockup", true);
   private final MultiSelectSetting.Entry entry14 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.cityMine", true);
   private final MultiSelectSetting.Entry entry15 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.bomber", false);
   private final MultiSelectSetting.Entry entry16 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.antiTotem", true);
   private final MultiSelectSetting.Entry entry17 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.fallback", true);
   private final MultiSelectSetting.Entry entry18 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.slowPlace", false);
   private final MultiSelectSetting.Entry entry19 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.explosionSync", true);
   private final MultiSelectSetting.Entry entry20 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.combatLog", true);
   private final MultiSelectSetting.Entry entry21 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.antiMine", true);
   private final MultiSelectSetting.Entry entry22 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.crystalDefense", true).method5230();
   private final MultiSelectSetting.Entry entry23 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.webTrap", true).method5230();
   private final BindSetting surroundKey = new BindSetting("module.crystalAura.surroundKey", "module.crystalAura.surroundKey.desc");
   private final ModeSetting surroundMode = new ModeSetting(
      "module.crystalAura.surroundMode",
      "module.crystalAura.surroundMode.desc",
      "module.crystalAura.surroundNormal",
      "module.crystalAura.surroundDouble",
      "module.crystalAura.surroundCity",
      "module.crystalAura.surroundBunker"
   );
   private final SliderSetting mineDamage = new SliderSetting(
      "module.crystalAura.mineDamage",
      10.0F,
      4.0F,
      36.0F,
      0.5F,
      "module.crystalAura.mineDamage.desc",
      "hp",
      () -> this.entry14.isEnabled() || this.entry15.isEnabled() || this.entry5.isEnabled(),
      null
   );
   private final SliderSetting mineTime = new SliderSetting(
      "module.crystalAura.mineTime",
      3.0F,
      0.5F,
      8.0F,
      0.5F,
      "module.crystalAura.mineTime.desc",
      "s",
      () -> this.entry14.isEnabled() || this.entry15.isEnabled() || this.entry5.isEnabled(),
      null
   );
   private final SliderSetting shieldDamage = new SliderSetting(
      "module.crystalAura.shieldDamage", 6.0F, 1.0F, 36.0F, 0.5F, "module.crystalAura.shieldDamage.desc", "hp", this.entry7::isEnabled, null
   );
   private final ModeSetting mode = new ModeSetting("module.crystalAura.mode", "module.crystalAura.mode.desc", this::method5954, "Vanilla", "ReallyWorld");
   private final SliderSetting placeRange = new SliderSetting(
      "module.crystalAura.placeRange", 4.5F, 2.0F, 6.0F, 0.1F, "module.crystalAura.placeRange.desc", "b", this::method5954, null
   );
   private final SliderSetting breakRange = new SliderSetting(
      "module.crystalAura.breakRange", 3.0F, 2.0F, 6.0F, 0.1F, "module.crystalAura.breakRange.desc", "b", this::method5954, null
   );
   private final SliderSetting actions = new SliderSetting(
      "module.crystalAura.actions", 1.0F, 1.0F, 3.0F, 1.0F, "module.crystalAura.actions.desc", "", this::method5954, null
   );
   private final SliderSetting placeDelay = new SliderSetting(
      "module.crystalAura.placeDelay", 0.0F, 0.0F, 500.0F, 10.0F, "module.crystalAura.placeDelay.desc", "ms", this::method5954, null
   );
   private final SliderSetting breakDelay = new SliderSetting(
      "module.crystalAura.breakDelay", 0.0F, 0.0F, 500.0F, 10.0F, "module.crystalAura.breakDelay.desc", "ms", this::method5954, null
   );
   private final SliderSetting rotationSpeed = new SliderSetting(
      "module.crystalAura.rotationSpeed", 180.0F, 10.0F, 180.0F, 5.0F, "module.crystalAura.rotationSpeed.desc", "°", this::method5954, null
   );
   private final ModeSetting swap = new ModeSetting(
      "module.crystalAura.swap",
      "module.crystalAura.swap.desc",
      this::method5954,
      "module.crystalAura.swapKeep",
      "module.crystalAura.swapSilent",
      "module.crystalAura.swapOff"
   );
   private final BooleanSetting instantBreak = new BooleanSetting(
      "module.crystalAura.instantBreak", "module.crystalAura.instantBreak.desc", false, this::method5954
   );
   private final BooleanSetting instantPlace = new BooleanSetting(
      "module.crystalAura.instantPlace", "module.crystalAura.instantPlace.desc", false, () -> this.method5954() && this.instantBreak.isEnabled()
   );
   private final BooleanSetting varyRotation = new BooleanSetting(
      "module.crystalAura.varyRotation", "module.crystalAura.varyRotation.desc", true, this::method5954
   );
   private final BooleanSetting throughWalls = new BooleanSetting(
      "module.crystalAura.throughWalls", "module.crystalAura.throughWalls.desc", false, this::method5954
   );
   private final Class35 class35 = new Class35();
   private final PlacementQueue placementQueue = new PlacementQueue();
   private final Class33 class33 = new Class33();
   private final Map<Long, Integer> items = new HashMap<>();
   private final Map<Long, Integer> items2 = new HashMap<>();
   private final Map<Long, Integer> items3 = new HashMap<>();
   private CrystalAuraModule.Type2 type2;
   private int intValue = -32768 << 16 | 0;
   private final Map<Long, Integer> items4 = new HashMap<>();
   private final Map<Long, Integer> items5 = new HashMap<>();
   private final Map<Integer, Integer> items6 = new HashMap<>();
   private final Map<Integer, Double> items7 = new HashMap<>();
   private double doubleValue = 3.0;
   private int intValue2 = -32768 << 16 | 0;
   private final Map<Long, Integer> items8 = new HashMap<>();
   private final Map<Long, Item> items9 = new HashMap<>();
   private final Set<Integer> items10 = new HashSet<>();
   private final Map<Long, Integer> items11 = new HashMap<>();
   private final Map<Integer, Integer> items12 = new HashMap<>();
   private final Map<Long, Integer> items13 = new HashMap<>();
   private CrystalAuraModule.Type type;
   private BlockPos blockPos;
   private int intValue3;
   private final Map<Integer, CrystalAuraModule.Entry> items14 = new HashMap<>();
   private final Queue<CrystalAuraModule.Data7> queue = new ConcurrentLinkedQueue<>();
   private final List<CrystalAuraModule.Data6> items15 = new ArrayList<>();
   private final List<CrystalAuraModule.Data> items16 = new ArrayList<>();
   private final List<CrystalAuraModule.Data> items17 = new ArrayList<>();
   private org.zenith.utility.misc.Data3 data3 = org.zenith.utility.misc.Data3.INSTANCE;
   private List<CrystalAuraModule.Data5> items18 = List.of();
   private List<PlayerEntity> items19 = List.of();
   private PlayerEntity playerEntity;
   private CrystalAuraModule.Data data;
   private CrystalAuraModule.Data data2;
   private CrystalAuraModule.Data data4;
   private Rotation rotation;
   private Rotation rotation2;
   private Rotation rotation3;
   private BlockPos blockPos2;
   private BlockPos blockPos3;
   private CrystalAuraModule.Data8 data8;
   private Vec3d vec3d = Vec3d.ZERO;
   private boolean flag;
   private Box box;
   private Box box2;
   private float floatValue;
   private double doubleValue2;
   private double doubleValue3;
   private int intValue4 = -32768 << 16 | 0;
   private int intValue5 = -32768 << 16 | 0;
   private int intValue6 = -32768 << 16 | 0;
   private int intValue7 = -32768 << 16 | 0;
   private int intValue8;
   private int intValue9;
   private int intValue10;
   private int intValue11;
   private int intValue12 = -1;
   private int intValue13 = -1;
   private int intValue14 = -1;
   private int intValue15;
   private int intValue16 = -32768 << 16 | 0;
   private int intValue17 = -32768 << 16 | 0;
   private int intValue18 = -32768 << 16 | 0;
   private boolean flag2;
   private boolean flag3;
   private final Queue<Integer> queue2 = new ConcurrentLinkedQueue<>();
   private final Queue<CrystalAuraModule.Data9> queue3 = new ConcurrentLinkedQueue<>();
   private final Queue<CrystalAuraModule.Data2> queue4 = new ConcurrentLinkedQueue<>();
   private final Map<Long, Integer> items20 = new HashMap<>();
   private long longValue = Long.MIN_VALUE;
   private PlayerEntity playerEntity2;
   private int intValue19;
   private Item item = Items.OBSIDIAN;
   private int intValue20 = -32768 << 16 | 0;
   private float floatValue2;
   private final Map<Long, Integer> items21 = new HashMap<>();
   private final Map<Long, Integer> items22 = new HashMap<>();
   private Rotation rotation4;
   private long longValue2 = Long.MIN_VALUE;
   private int intValue21 = -32768 << 16 | 0;
   private BlockPos blockPos4;
   private CrystalAuraModule.Type type3;
   private CrystalAuraModule.Data data5;
   private int intValue22 = -32768 << 16 | 0;
   private final CombatLogger combatLogger = new CombatLogger();
   private final Queue<Packet<?>> queue5 = new ConcurrentLinkedQueue<>();
   private final Map<Integer, Boolean> items23 = new ConcurrentHashMap<>();
   private int intValue23;
   private int intValue24;
   private int intValue25;
   private String text;
   private String text2;
   private JsonObject jsonObject;
   private JsonArray jsonArray;
   private JsonArray jsonArray2;
   private final Map<String, Integer> items24 = new LinkedHashMap<>();
   private String text3;
   private float floatValue3;
   private String text4;
   private int intValue26;
   private String text5;
   private volatile float floatValue4 = -1.0F;
   private Object obj;
   private RegistryEntry<Enchantment> registryEntry;
   private RegistryEntry<Enchantment> registryEntry2;
   private RegistryEntry<Enchantment> registryEntry3;
   private PlayerEntity playerEntity3;
   private Rotation rotation5;
   private float floatValue5;
   private EndCrystalEntity endCrystalEntity;
   private Box box3;
   private boolean flag4;
   private int intValue27 = -1;
   private int intValue28 = -1;
   private int intValue29 = -1;
   private int intValue30 = -32768 << 16 | 0;
   private int intValue31 = -1;
   private int intValue32 = -32768 << 16 | 0;
   private BlockPos blockPos5;
   private BlockPos blockPos6;
   private int intValue33 = -32768 << 16 | 0;
   private String text6 = "-";
   private BlockPos blockPos7;
   private BlockPos blockPos8;
   private int intValue34 = -32768 << 16 | 0;
   private BlockPos blockPos9;
   private static final int CONST_intValue21 = 60;
   private static final double CONST_doubleValue4 = 0.15;

   private CrystalAuraModule() {
      this.preset.set("Grim");
   }

   @Override
   public void onEnable() {
      this.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (mc.player != null) {
         this.method5863();
         this.method5860();
         this.method5861();
         this.method5886(true);
      }

      this.method5939();
      if (this.combatLogger.active()) {
         this.combatLogger.method4612("AURA", "аура выключена — журнал пишет AutoTotem, закроется через 10 с");
      }

      this.reset();
      super.onDisable();
   }

   public PlayerEntity getPlayerEntity() {
      return this.playerEntity;
   }

   public float method5742() {
      if (this.items18 != null && this.items18.getClass() == EmptyList.class ? true : this.items18.isEmpty()) {
         return 0.0F;
      } else {
         float var1 = this.data == null ? 0.0F : this.data.getFloatValue2();
         if (this.data4 != null && method5957(this.data4.getType())) {
            var1 = Math.max(var1, this.data4.getFloatValue2());
         }

         return Math.max(var1, this.floatValue2);
      }
   }

   public boolean method5743() {
      return this.isEnabled() && this.blockPos3 != null;
   }

   public boolean method5744() {
      return this.isEnabled() && this.entry13.isEnabled();
   }

   public boolean method5745(Entity var1) {
      if (this.isEnabled() && this.entry13.isEnabled() && this.longValue != Long.MIN_VALUE && var1 == this.playerEntity2 && mc.player != null) {
         var var10000 = this.items;
         Long var10001 = this.longValue;
         if (var10000 != null) {
            if (var10000.isEmpty()) {
               return false && mc.player.age - this.intValue19 <= this.intValue15 + 4;
            }
         }

         return var10000.containsKey(var10001) && mc.player.age - this.intValue19 <= this.intValue15 + 4;
      } else {
         return false;
      }
   }

   @EventTarget
   public void method5746(KeyEvent var1) {
      if (mc.player != null && this.surroundKey.getKeyCode() != -1 && var1.isPress(this.surroundKey.getKeyCode())) {
         this.method5748();
      }
   }

   public void method5747(int var1) {
      this.surroundMode.set(this.surroundMode.getValues().get(var1).getKey());
   }

   public void method5748() {
      if (mc.player != null) {
         this.blockPos9 = mc.player.getBlockPos();
         this.intValue34 = mc.player.age + 60;
         if (this.method5925()) {
            this.combatLogger.method4612("SURROUND", "бинд: застраиваемся в обсу вокруг %s", method5948(this.blockPos9));
         }
      }
   }

   @EventTarget
   public void method5749(Event5 var1) {
      if (!UseItemUtils.method6526() && mc.interactionManager != null) {
         int var2 = mc.player.age;
         if (this.intValue5 != (-32768 << 16 | 0) && var2 < this.intValue5) {
            this.reset();
         }

         if (this.intValue5 != var2) {
            this.intValue5 = var2;
            this.box3 = null;
            this.method5756(var2);
         }

         Rotation var3 = this.rotation;
         if (var3 != null) {
            rotationManager.method7849(
               new RotationRequest(var3, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var3), bcCameraTestnerv.getRotationConfig()),
               8,
               this
            );
         }
      }
   }

   @EventTarget
   public void method5750(Event9 var1) {
      if (mc.player != null && mc.interactionManager != null && !this.data3.isFlag() && this.intValue5 == mc.player.age) {
         int var2 = this.intValue8;
         this.method5760(this.intValue5);
         if (this.intValue8 > var2) {
            this.method5751(this.intValue5);
         }
      }
   }

   private void method5751(int var1) {
      Vec3d var2 = mc.player.getEyePos();
      Rotation var3 = this.method5897();
      this.flag4 = true;

      try {
         this.items16.removeIf(var1x -> {
            if (var1x.getEndCrystalEntity().isAlive()) {
               boolean var2x;
               label20: {
                  var var10000 = this.items6;
                  Integer var10001 = var1x.getEndCrystalEntity().getId();
                  if (var10000 != null) {
                     if (var10000.isEmpty()) {
                        var2x = false;
                        break label20;
                     }
                  }

                  var2x = var10000.containsKey(var10001);
               }

               if (!var2x) {
                  return false;
               }
            }

            return true;
         });
         this.data = (this.items16 != null && this.items16.getClass() == EmptyList.class ? true : this.items16.isEmpty()) ? null : this.items16.getFirst();
         this.items17.clear();
         this.data4 = this.method5771(var2, var3);
         Rotation var4 = this.method5758(var2, var3);
         if (var4 != null) {
            this.rotation = this.method5876(var4);
            this.rotation2 = this.rotation;
            this.intValue4 = var1;
            boolean var5 = this.data4 != null && var4 == this.data4.rotation();
            this.blockPos4 = var5 ? this.data4.pos() : null;
            this.type3 = var5 ? this.data4.getType() : null;
            Rotation var6 = this.rotation;
            rotationManager.method7849(
               new RotationRequest(var6, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var6), bcCameraTestnerv.getRotationConfig()),
               8,
               this
            );
            return;
         }
      } finally {
         this.flag4 = false;
      }
   }

   @EventTarget
   public void method5752(PostMotionEvent var1) {
      if (mc.player != null && mc.interactionManager != null) {
         this.intValue8 = 0;
         this.intValue9 = 0;
         if (this.flag2) {
            this.flag2 = false;
            this.method5760(this.intValue5);
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         this.placementQueue.method2815(var1.getPacket3());
         ClientPlayerEntity var2 = mc.player;
         if (var2 != null && this.combatLogger.active()) {
            this.method5928(var1.getPacket3(), var2);
         }

         if (var2 != null && !var2.isDead()) {
            ClientWorld var3 = mc.world;
            if (var1.getPacket3() instanceof BlockUpdateS2CPacket var4) {
               this.queue.add(new CrystalAuraModule.Data7(var4.getPos().asLong(), var4.getState(), var3 == null ? null : var3.getBlockState(var4.getPos())));
            } else if (var1.getPacket3() instanceof ChunkDeltaUpdateS2CPacket var5) {
               var5.visitUpdates(
                  (var2x, var3x) -> this.queue.add(new CrystalAuraModule.Data7(var2x.asLong(), var3x, var3 == null ? null : var3.getBlockState(var2x)))
               );
            } else if (var1.getPacket3() instanceof EntityStatusS2CPacket var6 && var6.getStatus() == 35) {
               this.queue2.add(var6.entityId);
            } else if (var1.getPacket3() instanceof ExplosionS2CPacket var7) {
               this.queue3.add(new CrystalAuraModule.Data9(var7.center(), var7.radius()));
            } else if (var1.getPacket3() instanceof BlockBreakingProgressS2CPacket var8 && var8.getEntityId() != var2.getId()) {
               this.queue4.add(new CrystalAuraModule.Data2(var8.getPos().asLong(), var8.getProgress()));
            }
         }
      }
   }

   @EventTarget
   public void method5753(AttackEvent var1) {
      if (var1.getType4() == AttackEvent.Type.PRE && var1.getEntity() instanceof PlayerEntity var2 && mc.player != null && mc.world != null && mc.isOnThread()) {
         CrystalAuraModule.Entry var5 = this.items14.get(var2.getId());
         if (var5 != null) {
            float var4 = this.method5892();
            var5.method5967(mc.player.age, var4);
            var5.method5968(mc.player.age + this.method5818(), var4);
         }

         Vec3d var6 = this.entry13.isEnabled() && var5 != null ? this.method5879(var2, var5.method5971()) : null;
         if (this.method5925()) {
            this.combatLogger.intValue6++;
            this.combatLogger
               .method4612(
                  "SWORD",
                  "удар по %s (hp %.1f) в %.2f б, спринт %s, кулдаун %.2f%s",
                  method5947(var2),
                  method5902(var2),
                  var2.distanceTo(mc.player),
                  mc.player.isSprinting() ? "да" : "нет",
                  mc.player.getAttackCooldownProgress(0.5F),
                  var6 == null
                     ? ", подлёта нет (цель не на земле)"
                     : String.format(Locale.ROOT, ", подлёт vy=%.2f, пинг цели %d т", var6.y, this.method5883(var2))
               );
         }

         if (var6 != null) {
            var5.method5965(mc.player.age, this.method5903(var2), var6, this.method5883(var2));
         }
      }
   }

   @EventTarget
   public void method5754(EntityAddEvent var1) {
      if (var1.getEntity() instanceof EndCrystalEntity var2 && !this.flag3 && !UseItemUtils.method6526() && mc.interactionManager != null) {
         BlockPos var16 = var2.getBlockPos().down();
         boolean var4;
         if (var4 = this.items.remove(var16.asLong()) != null) {
            this.items10.add(var2.getId());
         }

         if (var4) {
            this.placementQueue.method2813(method5958(var16, 0));
            if (this.entry12.isEnabled()) {
               this.items15.add(new CrystalAuraModule.Data6(var16.toImmutable(), System.currentTimeMillis(), false));
            }
         }

         if (this.method5925() && var2.getEntityPos().isInRange(mc.player.getEntityPos(), 12.0)) {
            float var5 = this.class35.method5452(var2.getEntityPos(), 6.0F, mc.player, mc.player.getBoundingBox(), null, null);
            if (!var4) {
               this.combatLogger.intValue9++;
            }

            this.combatLogger
               .method4612(
                  var4 ? "SPAWN" : "ENEMY",
                  "%s кристалл #%d на %s, до нас %.1f б, урон по нам %.1f",
                  var4 ? "наш" : "чужой",
                  var2.getId(),
                  method5948(var16),
                  var2.getEntityPos().distanceTo(mc.player.getEntityPos()),
                  var5
               );
         }

         if (!var4 && var2.getEntityPos().isInRange(mc.player.getEntityPos(), 12.0)) {
            this.intValue2 = mc.player.age;
            this.items4.put(var16.asLong(), mc.player.age);
         }

         if (var16.asLong() == this.longValue) {
            this.longValue = Long.MIN_VALUE;
         }

         if (this.data3.isFlag2()
            && (this.items18 != null && this.items18.getClass() == EmptyList.class ? false : !this.items18.isEmpty())
            && this.intValue7 != mc.player.age
            && !this.method5870()
            && this.method5917()
            && this.method5920()
            && this.method5919()) {
            int var17 = mc.player.age;
            Vec3d var6 = mc.player.getEyePos();
            Rotation var7 = this.method5897();
            if (this.method5835(var6, var7, var2) != null) {
               CrystalAuraModule.Data12 var8 = this.method5821(var2.getEntityPos(), 6.0F, null, null, true);
               if (var8 != null && var8.isFlag() && this.method5871(var8.getFloatValue2()) && !this.method5894(var8, var2.getEntityPos())) {
                  this.flag3 = true;

                  try {
                     if (!this.method5842(var2, var17)) {
                        return;
                     }

                     if (this.method5925()) {
                        this.combatLogger.intValue3++;
                        this.combatLogger
                           .method4612(
                              "BREAK",
                              "мгновенно по спавну #%d: урон %.1f по %s, по нам %.1f, до кристалла %.2f б",
                              var2.getId(),
                              var8.getFloatValue(),
                              method5947(var8.getPlayerEntity()),
                              var8.getFloatValue2(),
                              Math.sqrt(method5962(var6, var2.getBoundingBox()))
                           );
                     }

                     if (!this.data3.isFlag3()
                        || !this.method5921()
                        || !this.method5919()
                        || this.method5923(var16)
                        || this.method5908(var16)
                        || !method5956(mc.world.getBlockState(var16))
                        || !this.method5865(var16, var2)
                        || this.data3.isFlag4() && method5899(var7, this.rotation3)) {
                        return;
                     }

                     CrystalAuraModule.Data12 var9 = this.method5821(method5959(var16), 6.0F, null, null, false);
                     if (var9 != null && var9.isFlag()) {
                        CrystalAuraModule.Data var10 = new CrystalAuraModule.Data(
                           CrystalAuraModule.Type.CRYSTAL,
                           null,
                           var16.toImmutable(),
                           var7,
                           var9.getFloatValue(),
                           var9.getFloatValue2(),
                           var9.value(),
                           true,
                           var9.getPlayerEntity()
                        );
                        BlockHitResult var11 = this.method5832(var10, var6, var7);
                        if (var11 != null) {
                           this.method5844(var10, var11, var7, var17);
                        }

                        return;
                     }
                  } finally {
                     this.flag3 = false;
                  }
               }
            }
         }
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent var1) {
      this.class33.onMoveInput(var1);
      if (mc.player == null || this.blockPos9 == null || !this.method5773(var1)) {
         if (this.rotation != null && mc.player != null) {
            MovementInputUtils.method7426(var1, rotationManager.method7844().getFloatValue(), mc.player.getYaw());
         }
      }
   }

   @EventTarget
   public void method5755(WorldRenderEvent var1) {
      if (this.entry12.isEnabled() && mc.player != null) {
         int var2 = zenith.getStyleManager().getClientColor(0).method7287();
         CrystalAuraModule.Data var3 = this.data4;
         if (var3 != null) {
            if (var3.getType() == CrystalAuraModule.Type.DIG) {
               GlowUtil.method2230(method5964(var3.pos(), 0.4 + 0.6 * this.floatValue), ColorUtils.method6439(-1 << 16 | 23100, 0.6F), 1.5F);
            } else {
               int var4 = method5957(var3.getType()) ? -77 << 16 | 27647 : (var3.isFlag() ? var2 : -15812);
               GlowUtil.method2230(new Box(var3.pos()), ColorUtils.method6439(var4, 0.35F), 1.5F);
            }
         }

         CrystalAuraModule.Data var11 = this.data2;
         if (var11 != null && this.data == null) {
            GlowUtil.method2230(new Box(var11.pos()), ColorUtils.method6439(-15812, 0.3F), 1.5F);
         }

         long var5 = System.currentTimeMillis();
         var var7 = this.items15.iterator();

         while (var7.hasNext()) {
            CrystalAuraModule.Data6 var8 = (CrystalAuraModule.Data6)var7.next();
            float var9;
            if ((var9 = (float)(var5 - var8.time()) / 400.0F) >= 1.0F) {
               var7.remove();
            } else {
               int var10 = var8.isFlag() ? -77 << 16 | 27647 : var2;
               GlowUtil.method2230(method5964(var8.pos(), 1.0 - 0.4 * var9), ColorUtils.method6439(var10, 1.0F - var9), 1.5F);
            }
         }
      } else {
         this.items15.clear();
      }
   }

   private void method5756(int var1) {
      this.data = null;
      this.data2 = null;
      this.data4 = null;
      this.items16.clear();
      this.items17.clear();
      this.rotation = null;
      this.flag2 = false;
      this.data3 = this.method5953();
      this.intValue15 = Math.round(this.method5910() / 50.0F);
      this.floatValue2 = 0.0F;
      this.combatLogger.setIntValue(var1);
      this.method5931(var1);
      this.class35.reset();
      this.placementQueue.method2816(var1);
      this.method5924(var1);
      this.method5951(var1);
      this.method5929();
      this.method5952(var1);
      String var2 = this.method5918();
      if (var2 != null) {
         if (this.method5925()) {
            this.combatLogger.method4615("ПАУЗА: " + var2);
         }

         if (this.jsonObject != null) {
            this.jsonObject.addProperty("pause", var2);
         }

         this.class33.cancel();
         this.method5863();
         this.method5955();
         this.method5862();
      } else {
         if (!this.method5869() && this.class33.method5304()) {
            this.intValue7 = var1;
         }

         Vec3d var3 = mc.player.getEyePos();
         this.vec3d = var3.add(Entity.adjustMovementForCollisions(mc.player, mc.player.getVelocity(), mc.player.getBoundingBox(), mc.world, List.of()));
         this.items18 = this.method5829() ? this.method5900(var3, var1) : List.of();
         if (this.entry20.isEnabled()) {
            this.combatLogger
               .method4611(
                  this.items18 != null && this.items18.getClass() == EmptyList.class ? false : !this.items18.isEmpty(),
                  (this.items18 != null && this.items18.getClass() == EmptyList.class ? true : this.items18.isEmpty()) ? "" : this.method5757()
               );
         } else if (this.combatLogger.active()) {
            this.combatLogger.method4616("журнал выключен");
         }

         this.method5932();
         if (this.items18 != null && this.items18.getClass() == EmptyList.class ? true : this.items18.isEmpty()) {
            this.class33.cancel();
            this.method5863();
            this.method5955();
            CrystalAuraModule.Data var8 = this.method5775(this.method5897());
            if (var8 != null) {
               this.items17.add(var8);
               this.data4 = var8;
               this.rotation = this.method5876(var8.rotation());
               this.rotation2 = this.rotation;
               this.intValue4 = var1;
               this.blockPos4 = var8.pos();
               this.type3 = var8.getType();
               this.method5938();
            } else {
               this.method5862();
            }
         } else {
            this.items19 = this.method5905(var3);
            this.method5913();
            this.box = this.method5820(this.method5818());
            this.box2 = this.method5820(this.method5819());
            Rotation var4 = this.method5897();
            if (this.method5920()) {
               this.method5763(var3, var4);
            }

            this.data4 = this.method5771(var3, var4);
            this.method5885();
            this.method5886(false);
            if (this.blockPos3 != null && (var1 - this.intValue20 > 10 || mc.world.getBlockState(this.blockPos3).isReplaceable())) {
               boolean var5;
               if (var5 = mc.world.getBlockState(this.blockPos3).isReplaceable()) {
                  this.combatLogger.intValue5++;
               }

               if (this.method5925()) {
                  this.combatLogger
                     .method4612(var5 ? "MINED" : "DIG-STOP", "%s %s", method5948(this.blockPos3), var5 ? "выкопан" : "брошен: блок больше не нужен");
               }

               this.method5863();
            }

            if (this.data == null
               && this.data4 == null
               && this.data2 == null
               && (this.items != null && this.items.getClass() == EmptyMap.class ? true : this.items.isEmpty())) {
               this.method5862();
            } else {
               this.intValue10 = 0;
            }

            CrystalAuraModule.Data var9 = this.data != null ? this.data : (this.data4 != null ? this.data4 : this.data2);
            this.playerEntity = var9 == null ? this.items18.getFirst().getPlayerEntity() : var9.getPlayerEntity();
            Rotation var6 = this.method5758(var3, var4);
            boolean var7 = var6 != null && this.data4 != null && var6 == this.data4.rotation();
            this.blockPos4 = var7 ? this.data4.pos() : null;
            this.type3 = var7 ? this.data4.getType() : null;
            if (var6 != null) {
               this.rotation = this.method5876(var6);
               this.rotation2 = this.rotation;
               this.intValue4 = var1;
            } else if (this.rotation2 != null && var1 - this.intValue4 <= 6) {
               this.rotation = this.method5876(this.rotation2);
            }

            this.flag2 = this.data3.isFlag();
            this.method5930();
            this.method5938();
         }
      }
   }

   private String method5757() {
      CrystalAuraModule.Data5 var1 = this.items18.getFirst();
      return String.format(
         Locale.ROOT,
         "цель %s в %.1f б; пресет %s, дальность установки %.2f / удара %.2f, действий %d, пинг %d тиков",
         method5947(var1.getPlayerEntity()),
         var1.getVec3d().distanceTo(mc.player.getEntityPos()),
         this.preset.get(),
         this.method5878(),
         this.method5877(),
         this.data3.getIntValue(),
         this.intValue15
      );
   }

   private Rotation method5758(Vec3d var1, Rotation var2) {
      if (this.data == null || !this.data3.isFlag() && this.method5835(var1, var2, this.data.getEndCrystalEntity()) != null) {
         if (this.data4 != null && this.data4.getType() == CrystalAuraModule.Type.SHIELD && this.data4.isFlag()) {
            return this.data4.rotation();
         } else if (this.rotation4 != null) {
            return this.rotation4;
         } else if (this.rotation5 != null && this.data == null) {
            return this.rotation5;
         } else {
            Rotation var4 = this.method5759(var1);
            if (var4 != null) {
               return var4;
            } else if (this.data4 != null && this.data4.isFlag()) {
               return this.data4.rotation();
            } else if (this.data2 != null) {
               return this.data2.rotation();
            } else if (this.data4 != null) {
               return this.data4.rotation();
            } else if (this.data == null && this.data5 != null) {
               return this.data5.rotation();
            } else {
               return this.data == null ? null : this.data.rotation();
            }
         }
      } else {
         Rotation var3 = this.method5837(this.data4, this.data.getEndCrystalEntity());
         return var3 != null ? var3 : this.data.rotation();
      }
   }

   private Rotation method5759(Vec3d var1) {
      if (this.rotation3 != null && (this.items != null && this.items.getClass() == EmptyMap.class ? false : !this.items.isEmpty())) {
         if (this.data4 != null && this.data4.isFlag()) {
            Rotation var2 = this.method5897();
            var var3 = this.items.keySet().iterator();

            while (var3.hasNext()) {
               long var4;
               if ((var4 = (Long)var3.next()) != this.data4.pos().asLong()) {
                  this.box3 = method5961(BlockPos.fromLong(var4));

                  Rotation var6;
                  try {
                     var6 = this.method5831(this.data4.getType(), this.data4.pos(), this.vec3d, var2);
                  } finally {
                     this.box3 = null;
                  }

                  if (var6 != null) {
                     return var6;
                  }
               }
            }

            if (this.method5896(this.intValue16) > this.intValue15 + 1 || method5957(this.data4.getType())) {
               return null;
            }
         }

         Vec3d var11 = var1.add(this.rotation3.method3135().multiply(this.method5877()));
         var var12 = this.items.keySet().iterator();

         while (var12.hasNext()) {
            if (method5961(BlockPos.fromLong((Long)var12.next())).raycast(var1, var11).isPresent()) {
               return this.rotation3;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private void method5760(int var1) {
      if (this.intValue6 != var1) {
         this.intValue6 = var1;
         CrystalAuraModule.Data var2 = this.data4 != null && this.data4.isFlag() && method5761(this.data4.getType()) ? this.data4 : null;
         boolean var3 = this.method5869();
         String var4 = this.intValue7 == var1
            ? "пополняем хотбар из инвентаря"
            : (this.method5870() ? "AutoTotem кликает по инвентарю" : (this.method5872() ? "уступаем AutoTrap" : null));
         if (var4 != null) {
            if (this.data != null && this.method5925()) {
               this.combatLogger.method4612("MISS", "break %s не сделали: %s", this.method5944(this.data), var4);
            }

            if (var2 != null) {
               this.method5943(var2, var4);
            } else if (this.data4 != null && this.method5925() && !this.data4.pos().equals(this.blockPos8)) {
               this.blockPos8 = this.data4.pos();
               this.combatLogger.method4612("MISS", "%s %s не сделали: %s", this.data4.getType(), method5948(this.data4.pos()), var4);
            }
         } else {
            Vec3d var5 = mc.player.getEyePos();
            Rotation var6 = this.method5897();
            String var7 = null;
            this.flag = var3;
            this.method5890(var5, var6);

            for (CrystalAuraModule.Data var9 : this.items16) {
               if (!this.method5919()) {
                  var7 = "бюджет действий кончился";
                  break;
               }

               EndCrystalEntity var10 = var9.getEndCrystalEntity();
               if (var10.isAlive()) {
                  boolean var21;
                  label302: {
                     var var10000 = this.items6;
                     Integer var10001 = var10.getId();
                     if (var10000 != null) {
                        if (var10000.isEmpty()) {
                           var21 = false;
                           break label302;
                        }
                     }

                     var21 = var10000.containsKey(var10001);
                  }

                  if (!var21) {
                     if (!this.method5871(var9.getFloatValue2())) {
                        var7 = String.format(Locale.ROOT, "ждём тотем в руке (урон по нам %.1f)", var9.getFloatValue2());
                     } else {
                        if (this.method5835(var5, var6, var10) != null) {
                           if (this.method5842(var10, var1) && this.method5925()) {
                              this.combatLogger.intValue3++;
                              this.combatLogger
                                 .method4612(
                                    "BREAK",
                                    "кристалл #%d %s: урон %.1f по %s (hp %.1f), по нам %.1f, до кристалла %.2f б, цель в %.2f б",
                                    var10.getId(),
                                    method5949(var10.getEntityPos()),
                                    var9.getFloatValue(),
                                    method5947(var9.getPlayerEntity()),
                                    var9.getPlayerEntity() == null ? 0.0F : method5902(var9.getPlayerEntity()),
                                    var9.getFloatValue2(),
                                    Math.sqrt(method5962(var5, var10.getBoundingBox())),
                                    var9.getPlayerEntity() == null ? -1.0 : var9.getPlayerEntity().distanceTo(mc.player)
                                 );
                           }

                           var7 = null;
                           break;
                        }

                        var7 = "луч сервера не попадает в кристалл (поворот ещё не довёл)";
                     }
                  }
               }
            }

            if (var7 != null && this.data != null && this.method5925()) {
               this.combatLogger.method4612("MISS", "break %s не сделали: %s", this.method5944(this.data), var7);
            }

            boolean var17 = false;
            this.text = null;
            String var18 = null;

            try {
               for (int var19 = 0; var19 < this.items17.size(); var19++) {
                  if (!this.method5919()) {
                     this.text = this.text == null ? "бюджет действий потрачен на break" : this.text;
                     break;
                  }

                  CrystalAuraModule.Data var11 = this.items17.get(var19);
                  if (var19 <= 0 || var11.isFlag()) {
                     if (method5957(var11.getType()) && !this.method5871(var11.getFloatValue2())) {
                        this.text = "якорь: ждём тотем в руке";
                     } else if (this.method5762(var11, var5, var6, var1)) {
                        var17 = true;
                        break;
                     }

                     if (var19 == 0) {
                        var18 = this.text;
                     }

                     if ((var11.getType() == CrystalAuraModule.Type.SHIELD || var11.getType() == CrystalAuraModule.Type.DIG)
                        && var19 == 0
                        && this.text != null
                        && this.method5925()
                        && !var11.pos().equals(this.blockPos8)) {
                        this.blockPos8 = var11.pos();
                        this.combatLogger.method4612("MISS", "%s %s не сделали: %s", var11.getType(), method5948(var11.pos()), this.text);
                     }
                  }
               }

               if (var2 != null) {
                  String var20 = var18 != null ? var18 : this.text;
                  this.method5943(var2, var17 ? null : (var20 == null ? "неизвестно" : var20));
               }

               if (!var17 && this.data5 != null && this.method5919()) {
                  this.method5762(this.data5, var5, var6, var1);
               }
            } finally {
               this.flag = false;
            }

            this.method5860();
         }
      }
   }

   private static boolean method5761(CrystalAuraModule.Type var0) {
      return var0 == CrystalAuraModule.Type.CRYSTAL
         || var0 == CrystalAuraModule.Type.OBSIDIAN
         || var0 == CrystalAuraModule.Type.ANCHOR_PLACE
         || var0 == CrystalAuraModule.Type.ANCHOR_CHARGE
         || var0 == CrystalAuraModule.Type.ANCHOR_EXPLODE;
   }

   private boolean method5762(CrystalAuraModule.Data var1, Vec3d var2, Rotation var3, int var4) {
      CrystalAuraModule.Type var5 = var1.getType();

      boolean var6;
      for (var6 = false;
         var5 != null && this.method5919();
         var5 = var5 == CrystalAuraModule.Type.ANCHOR_CHARGE && var1.isFlag() ? CrystalAuraModule.Type.ANCHOR_EXPLODE : null
      ) {
         CrystalAuraModule.Data var7 = var5 == var1.getType()
            ? var1
            : new CrystalAuraModule.Data(
               var5, null, var1.pos(), var3, var1.getFloatValue(), var1.getFloatValue2(), var1.getFloatValue3(), var1.isFlag(), var1.getPlayerEntity()
            );
         BlockHitResult var8 = this.method5832(var7, var2, var3);
         if (var8 == null) {
            this.text = "луч сервера не попадает в нужную грань (поворот ещё не довёл / закрыто)";
            return var6;
         }

         if (this.method5908(var7.getType() == CrystalAuraModule.Type.CRYSTAL ? var7.pos() : var8.getBlockPos())) {
            this.text = "блок под действие поставлен в этом же тике";
            return var6;
         }

         if (method5761(var5) && var5 != CrystalAuraModule.Type.ANCHOR_CHARGE && var5 != CrystalAuraModule.Type.ANCHOR_EXPLODE && !this.method5921()) {
            this.text = "задержка установки";
            return var6;
         }

         if (var5 == CrystalAuraModule.Type.CRYSTAL && !this.method5895(var1, var4)) {
            this.text = "медленная установка на мелком уроне";
            return var6;
         }

         if (var5 == CrystalAuraModule.Type.CRYSTAL && this.data3.isFlag4() && method5899(var3, this.rotation3)) {
            this.text = "тот же поворот, что у прошлой установки (разброс поворота)";
            return var6;
         }

         if (var5 == CrystalAuraModule.Type.CRYSTAL && !this.method5865(var1.pos(), null)) {
            this.text = "место занято сущностью / над основанием не воздух";
            return var6;
         }

         this.text = "нет предмета в руке или смена слота запрещена (свитч)";

         boolean var10000 = switch (var5) {
            case BREAK -> false;
            case CRYSTAL -> this.method5844(var7, var8, var3, var4);
            case OBSIDIAN -> this.method5921() && this.method5845(var7, var8, Items.OBSIDIAN, var3, var4);
            case DIG -> this.method5848(var7, var8);
            case SHIELD -> this.method5921() && this.method5845(var7, var8, this.items9.getOrDefault(var7.pos().asLong(), this.item), var3, var4);
            case ANCHOR_PLACE -> {
               boolean var10;
               if (var10 = this.method5921() && this.method5845(var7, var8, Items.RESPAWN_ANCHOR, var3, var4)) {
                  this.items11.put(var1.pos().asLong(), var4 + 100);
               }

               yield var10;
            }
            case ANCHOR_CHARGE -> this.method5846(var7, var8, var4);
            case ANCHOR_EXPLODE -> this.method5847(var7, var8, var4);
         };

         if (!var10000) {
            return var6;
         }

         var6 = true;
      }

      return var6;
   }

   private void method5763(Vec3d var1, Rotation var2) {
      double var3 = this.method5877();

      for (EndCrystalEntity var7 : mc.world.getEntitiesByClass(EndCrystalEntity.class, mc.player.getBoundingBox().expand(var3 + 1.0), var4 -> {
         if (var4.isAlive()) {
            boolean var5;
            label22: {
               var var10000 = this.items6;
               Integer var10001 = var4.getId();
               if (var10000 != null) {
                  if (var10000.isEmpty()) {
                     var5 = false;
                     break label22;
                  }
               }

               var5 = var10000.containsKey(var10001);
            }

            if (!var5 && method5962(var1, var4.getBoundingBox()) <= var3 * var3) {
               return true;
            }
         }

         return false;
      })) {
         CrystalAuraModule.Data12 var8 = this.method5821(var7.getEntityPos(), 6.0F, null, null, true);
         this.method5935(var7, var8, var8 == null ? this.text2 : null);
         if (var8 != null && (var8.isFlag() || this.data2 == null || !(var8.value() <= this.data2.getFloatValue3()))) {
            boolean var9;
            if ((var9 = var8.isFlag() && !this.method5894(var8, var7.getEntityPos()))
               && var7.getBlockPos().down().equals(this.blockPos3)
               && var8.getFloatValue() < this.mineDamage.getValue3()) {
               var9 = false;
            }

            Rotation var10 = this.method5835(this.vec3d, var2, var7) != null ? var2 : this.method5834(this.vec3d, var2, var7);
            if (var10 != null) {
               if (!var9) {
                  int var11 = this.items12.computeIfAbsent(var7.getId(), var0 -> mc.player.age);
                  if (mc.player.age - var11 > 20) {
                     float var12;
                     if ((var12 = this.method5770(var7.getEntityPos())) <= 0.0F || var8.getPlayerEntity() == null) {
                        continue;
                     }

                     float var14;
                     var8 = new CrystalAuraModule.Data12(var8.getPlayerEntity(), var12, var14 = var8.getFloatValue2(), var12 - var14 * 1.2F, true);
                     var9 = true;
                  }
               }

               CrystalAuraModule.Data var15 = new CrystalAuraModule.Data(
                  CrystalAuraModule.Type.BREAK,
                  var7,
                  var7.getBlockPos().down(),
                  var10,
                  var8.getFloatValue(),
                  var8.getFloatValue2(),
                  var8.value(),
                  var9,
                  var8.getPlayerEntity()
               );
               if (var9) {
                  this.items16.add(var15);
               } else {
                  this.data2 = var15;
               }
            }
         }
      }

      this.items12.keySet().removeIf(var0 -> mc.world.getEntityById(var0) == null);
      this.items16.sort(Comparator.comparingDouble(var0 -> -var0.getFloatValue3()));
      this.data = (this.items16 != null && this.items16.getClass() == EmptyList.class ? true : this.items16.isEmpty()) ? null : this.items16.getFirst();
   }

   private CrystalAuraModule.Data method5764(Rotation var1) {
      Box var2 = mc.player.getBoundingBox().expand(0.3, 0.0, 0.3);
      Mutable var3 = new Mutable();
      BlockPos var4 = null;
      double var5 = Double.MAX_VALUE;

      for (int var7 = MathHelper.floor(var2.minX); var7 <= MathHelper.floor(var2.maxX); var7++) {
         for (int var8 = MathHelper.floor(var2.minZ); var8 <= MathHelper.floor(var2.maxZ); var8++) {
            for (int var9 = MathHelper.floor(var2.minY); var9 <= MathHelper.floor(var2.minY + 1.0); var9++) {
               var3.set(var7, var9, var8);
               double var10;
               if (mc.world.getBlockState(var3).isIn(BlockTags.FIRE)
                  && var2.intersects(new Box(var3))
                  && (var10 = mc.player.getEntityPos().squaredDistanceTo(Vec3d.ofCenter(var3))) < var5) {
                  var5 = var10;
                  var4 = var3.toImmutable();
               }
            }
         }
      }

      if (var4 == null) {
         return null;
      } else {
         Rotation var12 = this.method5836(var4, null, this.vec3d, var1);
         if (var12 == null) {
            return null;
         } else {
            if (this.method5925() && !var4.equals(this.blockPos5)) {
               this.blockPos5 = var4;
               this.combatLogger.method4612("FIRE", "огонь у ног в %s — гасим", method5948(var4));
            }

            return new CrystalAuraModule.Data(CrystalAuraModule.Type.DIG, null, var4, var12, 0.0F, 0.0F, 30.0F, true, this.items18.getFirst().getPlayerEntity());
         }
      }
   }

   private CrystalAuraModule.Data method5765(Vec3d var1, Rotation var2, CrystalAuraModule.Data var3) {
      if (this.method5921() && (this.items18 != null && this.items18.getClass() == EmptyList.class ? false : !this.items18.isEmpty())) {
         Item var4 = this.method5767();
         if (var4 == null) {
            return null;
         } else {
            this.items8.values().removeIf(var0 -> var0 < mc.player.age);
            Box var5 = this.box2 != null ? this.box2 : mc.player.getBoundingBox();
            float var6 = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            BlockPos var7 = BlockPos.ofFloored((var5.minX + var5.maxX) * 0.5, var5.minY + 0.001, (var5.minZ + var5.maxZ) * 0.5);
            Vec3d var8 = this.method5804(var7);
            CrystalAuraModule.Data var9 = null;
            float var10 = 0.0F;
            int var11 = 0;

            for (int var12 = -4; var12 <= 4; var12++) {
               for (int var13 = -4; var13 <= 4; var13++) {
                  for (int var14 = -2; var14 <= 1; var14++) {
                     BlockPos var15 = var7.add(var12, var14, var13);
                     if (method5956(mc.world.getBlockState(var15))) {
                        BlockPos var16;
                        boolean var27;
                        label172: {
                           var16 = var15.up();
                           var var10000 = this.items8;
                           Long var10001 = var15.asLong();
                           if (var10000 != null) {
                              if (var10000.isEmpty()) {
                                 var27 = false;
                                 break label172;
                              }
                           }

                           var27 = var10000.containsKey(var10001);
                        }

                        boolean var17 = var27;
                        if (var27
                           || Math.abs(var16.getX() - var7.getX()) > 1
                           || Math.abs(var16.getZ() - var7.getZ()) > 1
                           || var16.getY() < var7.getY()
                           || var16.getY() > var7.getY() + 1) {
                           if (!var17 && var8 != null && var16.getSquaredDistance(var7) <= 9.0) {
                              double var18 = var16.getX() - var7.getX();
                              double var20 = var16.getZ() - var7.getZ();
                              double var22;
                              if ((var22 = Math.sqrt(var18 * var18 + var20 * var20)) > 0.0 && (var18 * var8.x + var20 * var8.z) / var22 > 0.3) {
                                 continue;
                              }
                           }

                           if (mc.world.isAir(var16)
                              && !this.method5923(var16)
                              && this.method5867(var16)
                              && !this.method5777(var16)
                              && this.method5811(var15)
                              && (var17 || this.method5812(var15))) {
                              float var24 = this.class35.method5452(method5959(var15), 6.0F, mc.player, var5, null, null);
                              float var19 = var17 ? 3.0F : Math.max(this.shieldDamage.getValue3(), var6 * 0.4F);
                              float var25;
                              if (!(var24 < var19)
                                 && (!(var24 < var6 - 1.0F) || !this.method5766(var16))
                                 && (var3 == null || !var3.pos().equals(var15))
                                 && !this.method5803(var15)
                                 && !this.method5805(var16, var1, var3)
                                 && !((var25 = var24 + (var17 ? 8.0F : 0.0F)) <= var10)
                                 && var11++ <= 8) {
                                 Rotation var21 = this.method5831(CrystalAuraModule.Type.SHIELD, var16, this.vec3d, var2);
                                 if (var21 != null) {
                                    var10 = var25;
                                    boolean var26 = var17 || this.method5806() && var24 >= Math.min(var6 - 1.0F, var6 * 0.6F);
                                    var9 = new CrystalAuraModule.Data(
                                       CrystalAuraModule.Type.SHIELD,
                                       null,
                                       var16.toImmutable(),
                                       var21,
                                       0.0F,
                                       var24,
                                       var25,
                                       var26,
                                       this.items18.getFirst().getPlayerEntity()
                                    );
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (var9 != null) {
               this.items9.put(var9.pos().asLong(), var4);
               if (this.method5925() && !var9.pos().equals(this.blockPos6)) {
                  CombatLogger var29;
                  boolean var30;
                  Object[] var10003;
                  label107: {
                     this.blockPos6 = var9.pos();
                     var29 = this.combatLogger;
                     var10003 = new Object[]{method5948(var9.pos()), method5950(var4), var9.getFloatValue2(), null};
                     var var10006 = this.items8;
                     Long var10007 = var9.pos().down().asLong();
                     if (var10006 != null) {
                        if (var10006.isEmpty()) {
                           var30 = false;
                           break label107;
                        }
                     }

                     var30 = var10006.containsKey(var10007);
                  }

                  var10003[3] = var30 ? ", обсу только что поставил враг" : "";
                  var29.method4612("CDEF", "закрываем основание под %s (%s): кристалл там дал бы по нам %.1f%s", var10003);
               }
            }

            return var9;
         }
      } else {
         return null;
      }
   }

   private boolean method5766(BlockPos var1) {
      Vec3d var2 = Vec3d.ofCenter(var1);

      for (CrystalAuraModule.Data5 var4 : this.items18) {
         if (method5962(var2, var4.getPlayerEntity().getBoundingBox()) <= 2.25) {
            return true;
         }
      }

      return false;
   }

   private Item method5767() {
      return this.method5915(var0 -> var0.isOf(Items.COBWEB)) ? Items.COBWEB : this.method5810();
   }

   private CrystalAuraModule.Data method5768(Rotation var1) {
      if (this.method5921() && this.method5915(var0 -> var0.isOf(Items.COBWEB)) && this.method5896(this.intValue33) >= 20) {
         for (CrystalAuraModule.Data5 var3 : this.items18) {
            PlayerEntity var4 = var3.getPlayerEntity();
            BlockPos var5 = BlockPos.ofFloored(var4.getX(), var4.getY() + 0.1, var4.getZ());
            if (!mc.world.getBlockState(var5).isOf(Blocks.COBWEB)
               && mc.world.getBlockState(var5).isReplaceable()
               && var4.isOnGround()
               && !(method5962(this.vec3d, new Box(var5)) > this.method5878() * this.method5878())
               && !this.method5923(var5)) {
               boolean var7;
               label50: {
                  var var10000 = this.items2;
                  Long var10001 = var5.asLong();
                  if (var10000 != null) {
                     if (var10000.isEmpty()) {
                        var7 = false;
                        break label50;
                     }
                  }

                  var7 = var10000.containsKey(var10001);
               }

               if (!var7) {
                  Rotation var6 = this.method5831(CrystalAuraModule.Type.SHIELD, var5, this.vec3d, var1);
                  if (var6 != null) {
                     this.items9.put(var5.asLong(), Items.COBWEB);
                     if (this.method5925() && !var5.equals(this.blockPos7)) {
                        this.blockPos7 = var5;
                        this.combatLogger.method4612("WEB", "паутина под ноги %s в %s", method5947(var4), method5948(var5));
                     }

                     return new CrystalAuraModule.Data(CrystalAuraModule.Type.SHIELD, null, var5.toImmutable(), var6, 0.0F, 0.0F, 1.0F, true, var4);
                  }

                  this.text6 = "не навестись на " + method5948(var5);
                  continue;
               }
            }

            this.text6 = "клетка "
               + method5948(var5)
               + ": "
               + mc.world.getBlockState(var5).getBlock()
               + " земля "
               + var4.isOnGround()
               + " дист "
               + String.format(Locale.ROOT, "%.2f", Math.sqrt(method5962(this.vec3d, new Box(var5))));
         }

         return null;
      } else {
         this.text6 = !this.method5921() ? "задержка установки" : (this.method5896(this.intValue33) < 20 ? "пауза" : "нет паутины в хотбаре");
         return null;
      }
   }

   public boolean method5769() {
      return this.isEnabled() && (this.entry22.isEnabled() || this.entry23.isEnabled());
   }

   private float method5770(Vec3d var1) {
      float var2 = 0.0F;

      for (CrystalAuraModule.Data5 var4 : this.items18) {
         var2 = Math.max(var2, this.method5823(var4, var1, 6.0F, var4.getBox(), null, null, this.method5818()));
      }

      return var2;
   }

   private CrystalAuraModule.Data method5771(Vec3d var1, Rotation var2) {
      this.data5 = null;
      this.rotation4 = null;
      CrystalAuraModule.Data var3 = this.method5775(var2);
      if (var3 != null) {
         this.items17.add(var3);
         return var3;
      } else {
         CrystalAuraModule.Data var4 = this.entry21.isEnabled() ? this.method5776(var2) : null;
         if (var4 != null && var4.isFlag()) {
            this.items17.add(var4);
            return var4;
         } else {
            CrystalAuraModule.Data3 var5 = this.entry7.isEnabled() ? this.method5801() : null;
            if (var5 != null && var5.isFlag()) {
               CrystalAuraModule.Data var6 = this.method5781(var1, var2);
               CrystalAuraModule.Data var7 = this.method5802(var5, var2, var6);
               if (var7 != null) {
                  this.items17.addFirst(var7);
                  return var7;
               }

               if (var6 != null) {
                  return var6;
               }
            }

            CrystalAuraModule.Data var10 = this.method5764(var2);
            if (var10 != null) {
               this.items17.add(var10);
               return var10;
            } else {
               CrystalAuraModule.Data var11 = this.method5781(var1, var2);
               CrystalAuraModule.Data var8 = this.entry22.isEnabled() ? this.method5765(var1, var2, var11) : null;
               if (var8 != null && var8.isFlag()) {
                  this.items17.addFirst(var8);
                  return var8;
               } else {
                  if (var8 != null && this.data5 == null) {
                     this.data5 = var8;
                  }

                  if (var11 == null && this.entry23.isEnabled()) {
                     CrystalAuraModule.Data var9 = this.method5768(var2);
                     if (var9 != null) {
                        this.items17.add(var9);
                        return var9;
                     }
                  }

                  if (var5 != null && !var5.isFlag() && this.method5896(this.intValue22) >= 4) {
                     this.data5 = this.method5802(var5, var2, var11);
                  }

                  if (this.data5 == null && var4 != null) {
                     this.data5 = var4;
                  }

                  return var11;
               }
            }
         }
      }
   }

   private Set<BlockPos> method5772() {
      Box var1 = mc.player.getBoundingBox();
      double var2 = var1.minY + 0.5;
      HashSet<BlockPos> var4 = new HashSet();
      var4.add(BlockPos.ofFloored(var1.minX + 0.001, var2, var1.minZ + 0.001));
      var4.add(BlockPos.ofFloored(var1.maxX - 0.001, var2, var1.minZ + 0.001));
      var4.add(BlockPos.ofFloored(var1.minX + 0.001, var2, var1.maxZ - 0.001));
      var4.add(BlockPos.ofFloored(var1.maxX - 0.001, var2, var1.maxZ - 0.001));
      LinkedHashSet var5 = new LinkedHashSet();

      for (Object var7_o : (Iterable<?>)(Object)(var4)) {
         BlockPos var7 = (BlockPos)var7_o;
         for (Direction var9 : Direction.Type.HORIZONTAL) {
            BlockPos var10 = var7.offset(var9);
            if (!var4.contains(var10)) {
               var5.add(var10);
            }
         }
      }

      return var5;
   }

   private boolean method5773(MoveInputEvent var1) {
      if (!mc.player.isOnGround()) {
         return false;
      } else {
         Vec3d var2 = mc.player.getVelocity();
         double var3 = this.blockPos9.getX() + 0.5 - mc.player.getX();
         double var5 = this.blockPos9.getZ() + 0.5 - mc.player.getZ();
         double var7 = var3 - var2.x * 2.2;
         double var9 = var5 - var2.z * 2.2;
         if (Math.abs(var7) <= 0.15 && Math.abs(var9) <= 0.15) {
            var1.method3044();
            return true;
         } else {
            double var11 = Math.abs(var7) > 0.15 ? var7 : 0.0;
            double var13 = Math.abs(var9) > 0.15 ? var9 : 0.0;
            float var15 = (float)Math.toDegrees(Math.atan2(-var11, var13));
            float var16 = this.rotation != null ? rotationManager.method7844().getFloatValue() : mc.player.getYaw();
            float var17 = 0.0F;
            float var18 = 0.0F;
            double var19 = Double.MAX_VALUE;

            for (float var21 = -1.0F; var21 <= 1.0F; var21++) {
               for (float var22 = -1.0F; var22 <= 1.0F; var22++) {
                  double var25;
                  if ((var21 != 0.0F || var22 != 0.0F)
                     && (var25 = Math.abs(MathHelper.wrapDegrees(Math.toDegrees(MovementInputUtils.method7427(var16, var21, var22)) - var15))) < var19) {
                     var19 = var25;
                     var17 = var21;
                     var18 = var22;
                  }
               }
            }

            var1.method3044();
            var1.method3042(var17, var18);
            return true;
         }
      }
   }

   private List<BlockPos> method5774() {
      ArrayList<BlockPos> var1 = new ArrayList<>();
      BlockPos var2 = this.blockPos9;

      for (Direction var4 : Direction.Type.HORIZONTAL) {
         var1.add(var2.offset(var4));
      }

      int var6;
      if ((var6 = this.surroundMode.getIndex()) == 1 || var6 == 3) {
         for (Direction var5 : Direction.Type.HORIZONTAL) {
            var1.add(var2.up().offset(var5));
         }
      }

      if (var6 == 2) {
         for (Direction var9 : Direction.Type.HORIZONTAL) {
            var1.add(var2.offset(var9, 2));
         }
      }

      if (var6 == 3) {
         var1.add(var2.up(2));
         var1.add(var2.down());
      }

      return var1;
   }

   private CrystalAuraModule.Data method5775(Rotation var1) {
      if (this.blockPos9 == null) {
         return null;
      } else {
         BlockPos var2 = mc.player.getBlockPos();
         boolean var3 = var2.getX() != this.blockPos9.getX() || var2.getZ() != this.blockPos9.getZ() || Math.abs(var2.getY() - this.blockPos9.getY()) > 1;
         if (mc.player.age <= this.intValue34 && !var3 && this.method5915(var0 -> var0.isOf(Items.OBSIDIAN))) {
            boolean var4 = false;

            for (BlockPos var6 : this.method5774()) {
               if (mc.world.getBlockState(var6).isReplaceable()) {
                  boolean var10;
                  label108: {
                     var4 = true;
                     var var10000 = this.items2;
                     Long var10001 = var6.asLong();
                     if (var10000 != null) {
                        if (var10000.isEmpty()) {
                           var10 = false;
                           break label108;
                        }
                     }

                     var10 = var10000.containsKey(var10001);
                  }

                  if (!var10 && !new Box(var6).intersects(mc.player.getBoundingBox().stretch(mc.player.getVelocity().multiply(2.0)))) {
                     BlockPos var7 = var6;
                     if (!this.method5867(var6)) {
                        if ((this.items18 != null && this.items18.getClass() == EmptyList.class ? false : !this.items18.isEmpty()) && this.data == null) {
                           this.method5780(var6, var1);
                        }

                        BlockPos var8 = var6.down();
                        if (!mc.world.getBlockState(var8).isReplaceable() || !this.method5867(var8)) {
                           continue;
                        }

                        var7 = var8;
                     }

                     Rotation var9 = this.method5831(CrystalAuraModule.Type.SHIELD, var7, this.vec3d, var1);
                     if (var9 != null) {
                        this.items9.put(var7.asLong(), Items.OBSIDIAN);
                        return new CrystalAuraModule.Data(
                           CrystalAuraModule.Type.SHIELD,
                           null,
                           var7.toImmutable(),
                           var9,
                           0.0F,
                           0.0F,
                           200.0F,
                           true,
                           (this.items18 != null && this.items18.getClass() == EmptyList.class ? true : this.items18.isEmpty())
                              ? null
                              : this.items18.getFirst().getPlayerEntity()
                        );
                     }
                  }
               }
            }

            if (!var4) {
               if (this.method5925()) {
                  this.combatLogger.method4612("SURROUND", "застроились: %s закрыт со всех сторон", method5948(this.blockPos9));
               }

               this.blockPos9 = null;
            }

            return null;
         } else {
            if (this.method5925()) {
               this.combatLogger
                  .method4612(
                     "SURROUND",
                     "застройка %s: %s",
                     method5948(this.blockPos9),
                     mc.player.age > this.intValue34 ? "время вышло" : (var3 ? "ушли из клетки" : "обсидиана нет")
                  );
            }

            this.blockPos9 = null;
            return null;
         }
      }
   }

   private CrystalAuraModule.Data method5776(Rotation var1) {
      Item var2 = this.method5810();
      if (var2 == null) {
         return null;
      } else {
         Set<BlockPos> var3 = this.method5772();

         for (Object var5_o : (Iterable<?>)(Object)(var3)) {
            BlockPos var5 = (BlockPos)var5_o;
            boolean var16;
            label114: {
               var var10000 = this.items22;
               Long var10001 = var5.asLong();
               if (var10000 != null) {
                  if (var10000.isEmpty()) {
                     var16 = false;
                     break label114;
                  }
               }

               var16 = var10000.containsKey(var10001);
            }

            if (var16 && mc.world.getBlockState(var5).isReplaceable() && !this.method5777(var5)) {
               if (!this.method5867(var5)) {
                  this.method5780(var5, var1);
               } else {
                  Rotation var6 = this.method5831(CrystalAuraModule.Type.SHIELD, var5, this.vec3d, var1);
                  if (var6 != null) {
                     this.item = var2;
                     return new CrystalAuraModule.Data(
                        CrystalAuraModule.Type.SHIELD, null, var5.toImmutable(), var6, 0.0F, 0.0F, 100.0F, true, this.items18.getFirst().getPlayerEntity()
                     );
                  }
               }
            }
         }

         CrystalAuraModule.Data var13 = null;

         for (Object var15_o : (Iterable<?>)(Object)(var3)) {
            BlockPos var15 = (BlockPos)var15_o;
            Integer var7 = this.items21.get(var15.asLong());
            if (var7 != null && !mc.world.getBlockState(var15).isReplaceable()) {
               if (var7 >= 6 && this.rotation4 == null) {
                  this.rotation4 = this.method5779(var15);
               }

               for (Direction var9 : Direction.Type.HORIZONTAL) {
                  BlockPos var10 = var15.offset(var9);
                  if (!var3.contains(var10)
                     && this.method5778(var15.offset(var9.getOpposite()))
                     && mc.world.isAir(var10)
                     && method5956(mc.world.getBlockState(var10.down()))
                     && !this.method5923(var10)
                     && this.method5867(var10)
                     && !this.method5777(var10)) {
                     Rotation var11 = this.method5831(CrystalAuraModule.Type.SHIELD, var10, this.vec3d, var1);
                     if (var11 != null) {
                        boolean var12 = var7 >= 7;
                        if (var13 == null || var12 && !var13.isFlag()) {
                           this.item = var2;
                           var13 = new CrystalAuraModule.Data(
                              CrystalAuraModule.Type.SHIELD,
                              null,
                              var10.toImmutable(),
                              var11,
                              0.0F,
                              0.0F,
                              50.0F + var7.intValue(),
                              var12,
                              this.items18.getFirst().getPlayerEntity()
                           );
                        }
                     }
                  }
               }
            }
         }

         if (var13 != null && this.method5925() && var13.pos().asLong() != this.longValue2) {
            this.longValue2 = var13.pos().asLong();
            this.combatLogger.method4612("ANTIMINE", "закрываем city-спот %s%s", method5948(var13.pos()), var13.isFlag() ? " срочно" : "");
         }

         return var13;
      }
   }

   private boolean method5777(BlockPos var1) {
      Box var2 = mc.player.getBoundingBox();
      return new Box(var1).intersects(var2.stretch(mc.player.getVelocity().multiply(2.0)).expand(0.1));
   }

   private boolean method5778(BlockPos var1) {
      Box var2 = mc.player.getBoundingBox();
      return var2.intersects(new Box(var1).contract(0.001))
         && var1.getY() == BlockPos.ofFloored(var2.getCenter().x, var2.minY + 0.5, var2.getCenter().z).getY();
   }

   private Rotation method5779(BlockPos var1) {
      BlockPos var2 = var1.down();
      double var3 = this.method5878();
      Rotation var5 = null;
      double var6 = Double.MAX_VALUE;
      Rotation var8 = this.method5897();

      for (double var12 : CONST_doubleValues) {
         for (double var17 : CONST_doubleValues) {
            Vec3d var19 = new Vec3d(var2.getX() + var12, var2.getY() + 1.0, var2.getZ() + var17);
            if (!(this.vec3d.squaredDistanceTo(var19) > var3 * var3)) {
               Rotation var20 = method5898(this.vec3d, var19);
               Vec3d var21 = this.vec3d.add(var20.method3135().multiply(var3));
               BlockHitResult var22 = BlockView.raycast(this.vec3d, var21, var1, (var2x, var3x) -> {
                  if (var3x.equals(var2x)) {
                     return null;
                  } else {
                     BlockState var4 = mc.world.getBlockState(var3x);
                     return var4.getOutlineShape(mc.world, var3x).raycast(this.vec3d, var21, var3x);
                  }
               }, var0 -> null);
               double var23;
               if (var22 != null && var22.getBlockPos().equals(var2) && var22.getSide() == Direction.UP && (var23 = var8.method3131(var20).method5327()) < var6
                  )
                {
                  var6 = var23;
                  var5 = var20;
               }
            }
         }
      }

      return var5;
   }

   private void method5780(BlockPos var1, Rotation var2) {
      if (this.data == null && this.method5920()) {
         for (EndCrystalEntity var4 : mc.world.getEntitiesByClass(EndCrystalEntity.class, new Box(var1), var1x -> {
            if (var1x.isAlive()) {
               boolean var2x;
               label20: {
                  var var10000 = this.items6;
                  Integer var10001 = var1x.getId();
                  if (var10000 != null) {
                     if (var10000.isEmpty()) {
                        var2x = false;
                        break label20;
                     }
                  }

                  var2x = var10000.containsKey(var10001);
               }

               if (!var2x) {
                  return true;
               }
            }

            return false;
         })) {
            Box var5 = this.box != null ? this.box : mc.player.getBoundingBox();
            float var6;
            if (!((var6 = this.class35.method5452(var4.getEntityPos(), 6.0F, mc.player, var5, null, null)) >= this.method5830())
               && this.method5871(var6)
               && !(method5962(mc.player.getEyePos(), var4.getBoundingBox()) > this.method5877() * this.method5877())) {
               Rotation var7 = this.method5835(this.vec3d, var2, var4) != null ? var2 : this.method5834(this.vec3d, var2, var4);
               if (var7 != null) {
                  this.data = new CrystalAuraModule.Data(
                     CrystalAuraModule.Type.BREAK, var4, var4.getBlockPos().down(), var7, 0.0F, var6, 100.0F, true, this.items18.getFirst().getPlayerEntity()
                  );
                  this.items16.addFirst(this.data);
                  if (this.method5925()) {
                     this.combatLogger.method4612("ANTIMINE", "в дыре %s кристалл #%d — сбиваем, урон по нам %.1f", method5948(var1), var4.getId(), var6);
                  }

                  return;
               }
            }
         }
      }
   }

   private CrystalAuraModule.Data method5781(Vec3d var1, Rotation var2) {
      this.intValue23 = 0;
      this.intValue24 = 0;
      this.intValue25 = 0;
      EndCrystalEntity var3 = this.data == null ? null : this.data.getEndCrystalEntity();
      boolean var4 = this.method5915(var0 -> var0.isOf(Items.END_CRYSTAL));
      boolean var5 = this.entry4.isEnabled() && this.method5915(var0 -> var0.isOf(Items.OBSIDIAN));
      boolean var6;
      boolean var7 = (var6 = this.method5873()) && this.method5915(var0 -> var0.isOf(Items.GLOWSTONE));
      boolean var8 = var6 && var7 && this.method5915(var0 -> var0.isOf(Items.RESPAWN_ANCHOR));
      boolean var9 = this.method5921();
      boolean var10 = this.entry.isEnabled() && this.data2 == null && this.data == null;
      boolean var11 = this.method5782();
      boolean var12 = this.entry14.isEnabled() || this.entry15.isEnabled();
      double var13 = this.method5878();
      double var15 = this.method5877();
      BlockPos var17 = this.blockPos3 == null
            || !(mc.interactionManager.currentBreakingProgress >= 0.8F)
            || !mc.player.getOffHandStack().isOf(Items.END_CRYSTAL) && this.data3.getType() != org.zenith.utility.misc.Data3.Type.SILENT
         ? null
         : this.blockPos3;
      HashSet var18 = new HashSet();
      var var19 = this.entry14.isEnabled() ? this.method5793(var18) : Set.of();
      var var20 = this.entry15.isEnabled() ? this.method5796() : Set.of();
      ArrayList<CrystalAuraModule.Data13> var21 = new ArrayList();
      ArrayList var22 = new ArrayList();

      for (BlockPos var24 : this.method5816(var1, var13)) {
         if (this.method5923(var24)) {
            this.method5937("спот в чёрном списке (сервер отказал / кристалл не появился)");
         } else {
            BlockState var25 = mc.world.getBlockState(var24);
            if (var25.isOf(Blocks.RESPAWN_ANCHOR)) {
               if (var6) {
                  int var45;
                  CrystalAuraModule.Type var48 = (var45 = var25.get(RespawnAnchorBlock.CHARGES)) > 0
                     ? CrystalAuraModule.Type.ANCHOR_EXPLODE
                     : (var7 ? CrystalAuraModule.Type.ANCHOR_CHARGE : null);
                  if (var48 != null) {
                     this.method5814(var21, var24, var48, Vec3d.ofCenter(var24), 5.0F, var24, null, var45 > 0 ? 3.0F : 1.5F);
                  }
               }
            } else if (!var9) {
               this.method5937("задержка установки");
            } else {
               if (var4 && var19.contains(var24.asLong())) {
                  this.method5794(var21, var24, var25, var18, var3, var1, var15);
               }

               boolean var26 = method5962(var1, method5961(var24)) <= var15 * var15;
               if (!method5956(var25)) {
                  if (var20.contains(var24.asLong())
                     && var5
                     && this.method5867(var24)
                     && mc.world.isAir(var24.up())
                     && this.method5850(var24, Blocks.OBSIDIAN.getDefaultState()) > 0) {
                     this.method5814(var21, var24, CrystalAuraModule.Type.OBSIDIAN, method5959(var24), 6.0F, var24, null, -6.0F);
                  } else {
                     if (var5 && var26 && this.method5867(var24) && mc.world.isAir(var24.up()) && this.method5866(method5960(var24), var3)) {
                        this.method5814(
                           var21, var24, CrystalAuraModule.Type.OBSIDIAN, method5959(var24), 6.0F, null, var24, var24.equals(this.blockPos2) ? -2.5F : -4.0F
                        );
                     }

                     if (var8 && this.method5867(var24)) {
                        this.method5814(var21, var24, CrystalAuraModule.Type.ANCHOR_PLACE, Vec3d.ofCenter(var24), 5.0F, var24, null, -3.0F);
                     }
                  }
               } else if (!var4) {
                  this.method5937("нет кристаллов");
               } else if (!var26) {
                  this.method5937("основание: кристалл был бы дальше дальности удара");
               } else {
                  boolean var70;
                  label578: {
                     var var10000 = this.items;
                     Long var10001 = var24.asLong();
                     if (var10000 != null) {
                        if (var10000.isEmpty()) {
                           var70 = false;
                           break label578;
                        }
                     }

                     var70 = var10000.containsKey(var10001);
                  }

                  if (var70) {
                     this.method5937("основание: уже ставим, ждём подтверждения");
                  } else {
                     BlockPos var27 = var24.up();
                     BlockState var28 = mc.world.getBlockState(var27);
                     if (var28.isAir()) {
                        if (!this.method5865(var24, var3)) {
                           this.method5937("основание: место занято сущностью или целью");
                           EndCrystalEntity var54 = this.entry17.isEnabled() ? this.method5800(var24, var3) : null;
                           if (var54 != null) {
                              var22.add(new CrystalAuraModule.Data11(var24.toImmutable(), var54));
                           }
                        } else {
                           BlockPos var53 = var17 != null && var17.isWithinDistance(var27, 2.5) ? var17 : null;
                           this.method5814(
                              var21, var24, CrystalAuraModule.Type.CRYSTAL, method5959(var24), 6.0F, var53, null, var24.equals(this.blockPos2) ? 1.5F : 0.0F
                           );
                           if (var20.contains(var24.asLong())) {
                              this.method5814(var21, var24, CrystalAuraModule.Type.CRYSTAL, method5959(var24), 6.0F, var24, null, -3.0F);
                           }
                        }
                     } else if (!this.entry5.isEnabled()) {
                        this.method5937("основание: над ним блок");
                     } else {
                        int var29;
                        if (this.method5866(method5960(var24), var3) && (var29 = this.method5850(var27, var28)) > 0) {
                           this.method5815(var21, var27, CrystalAuraModule.Type.DIG, method5959(var24), 6.0F, var27, null, this.method5798(var27, var29), var29);
                        }
                     }
                  }
               }
            }
         }
      }

      if (var9 && var12) {
         this.method5797(var21, var1, var13);
      }

      this.intValue23 = var21.size();
      if (var21.isEmpty()) {
         return null;
      } else {
         var21.sort(Comparator.comparingDouble(var0 -> -var0.getFloatValue3()));
         ArrayList<CrystalAuraModule.Data14> var42 = new ArrayList();
         ArrayList<CrystalAuraModule.Data14> var43 = new ArrayList();
         EnumMap var44 = new EnumMap<>(CrystalAuraModule.Type.class);
         this.items13.values().removeIf(var0 -> var0 < mc.player.age);

         for (Object var49_o : (Iterable<?>)(Object)(var21)) {
            CrystalAuraModule.Data13 var49 = (CrystalAuraModule.Data13)var49_o;
            boolean var51;
            if (!(var51 = var49.getType() == this.type3 && var49.pos().equals(this.blockPos4) || this.method5789(var49))) {
               boolean var72;
               label530: {
                  var var71 = this.items13;
                  Long var79 = var49.pos().asLong() * 8L + var49.getType().ordinal();
                  if (var71 != null) {
                     if (var71.isEmpty()) {
                        var72 = false;
                        break label530;
                     }
                  }

                  var72 = var71.containsKey(var79);
               }

               if (var72) {
                  this.method5937(var49.getType() + ": грань не видна из глаз (недавно проверяли)");
                  continue;
               }
            }

            if ((var51 ? 0 : (Integer)var44.merge(var49.getType(), 1, (a, b) -> (Integer)a + (Integer)b)) > method5792(var49.getType())) {
               this.method5937(var49.getType() + ": за пределами квоты точного счёта (" + method5792(var49.getType()) + ")");
            } else {
               boolean var30 = var49.getType() == CrystalAuraModule.Type.ANCHOR_CHARGE || var49.getType() == CrystalAuraModule.Type.ANCHOR_EXPLODE;
               CrystalAuraModule.Data12 var31 = this.method5822(
                  var49.getVec3d(),
                  var49.getFloatValue(),
                  var49.getBlockPos2(),
                  var49.getBlockPos3(),
                  var30,
                  var49.getType() == CrystalAuraModule.Type.DIG ? Math.max(0, var49.getIntValue()) : 0
               );
               if (var31 == null) {
                  this.method5934(var49, null, 0.0F, this.text2);
               } else if (var49.getType() == CrystalAuraModule.Type.DIG && var49.getIntValue() > 2 && !this.method5795(var31.getPlayerEntity())) {
                  this.method5934(var49, var31, 0.0F, "цель двигается — копать бессмысленно");
               } else {
                  boolean var32 = !var31.isFlag();
                  boolean var33 = var49.getType() == CrystalAuraModule.Type.CRYSTAL || var49.getType() == CrystalAuraModule.Type.OBSIDIAN;
                  if (var32 && var11 && method5783(var49.getType()) == CrystalAuraModule.Type2.CRYSTALS) {
                     this.method5934(var49, var31, 0.0F, "рано, а к этому окну уже стоит наш якорь");
                  } else if (!var32 || var10 && var33) {
                     if (var49.getIntValue() > 10 && var31.getFloatValue() < this.mineDamage.getValue3()) {
                        this.method5934(
                           var49,
                           var31,
                           0.0F,
                           String.format(
                              Locale.ROOT,
                              "копать %d т ради %.1f < порога копания %.1f",
                              var49.getIntValue(),
                              var31.getFloatValue(),
                              this.mineDamage.getValue3()
                           )
                        );
                        if (var31.isFlag() && (var31.getFloatValue() >= Math.max(6.0F, this.minDamage.getValue3()) || this.method5790(var31))) {
                           var43.add(new CrystalAuraModule.Data14(var49, var31, var31.value() + var49.getFloatValue2()));
                        }
                     } else {
                        float var34 = var31.value() + var49.getFloatValue2() - (var32 ? 6.0F : 0.0F);
                        boolean var35 = !var31.isFlag()
                           && var49.getType() != CrystalAuraModule.Type.OBSIDIAN
                           && this.method5788(var31.getPlayerEntity())
                           && var34 + 6.0F > 0.0F;
                        if (!(var34 <= 0.0F) || var35 || var31.isFlag() && this.method5790(var31)) {
                           var34 -= this.method5785(var49, var31, var34);
                           if (var49.getType() == CrystalAuraModule.Type.ANCHOR_EXPLODE) {
                              boolean var74;
                              label480: {
                                 var var73 = this.items11;
                                 Long var80 = var49.pos().asLong();
                                 if (var73 != null) {
                                    if (var73.isEmpty()) {
                                       var74 = false;
                                       break label480;
                                    }
                                 }

                                 var74 = var73.containsKey(var80);
                              }

                              if (var74) {
                                 var34 += 0.8F * var31.getFloatValue2() * this.method5828();
                              }
                           }

                           if (var51) {
                              var34 += Math.max(3.0F, 0.2F * Math.abs(var34));
                           }

                           this.method5934(var49, var31, var34, null);
                           var42.add(new CrystalAuraModule.Data14(var49, var31, var34));
                        } else {
                           this.method5934(var49, var31, var34, "в минус: по нам больше, чем по цели");
                        }
                     }
                  } else {
                     this.method5934(
                        var49,
                        var31,
                        0.0F,
                        var10 ? "рано, а предустановка только для кристалла/обсы/якоря" : "рано, предустановка выключена или уже есть ожидание/break"
                     );
                  }
               }
            }
         }

         var42.sort(Comparator.comparingDouble(var0 -> -var0.value()));
         this.intValue24 = var42.size();
         CrystalAuraModule.Data var47 = null;
         int var50 = 0;
         HashMap var52 = new HashMap();

         for (Object var59_o : (Iterable<?>)(Object)(var42)) {
            CrystalAuraModule.Data14 var59 = (CrystalAuraModule.Data14)var59_o;
            CrystalAuraModule.Data13 var63;
            long var67;
            boolean var75;
            label453: {
               var63 = var59.getData13();
               var67 = var63.pos().asLong() * 8L + var63.getType().ordinal();
               Long var81 = var67;
               if (var52 != null) {
                  if (var52.isEmpty()) {
                     var75 = false;
                     break label453;
                  }
               }

               var75 = var52.containsKey(var81);
            }

            Rotation var69;
            if (var75) {
               var69 = (Rotation)var52.get(var67);
            } else {
               label446: {
                  var var76 = this.items13;
                  Long var82 = var67;
                  if (var76 != null) {
                     if (var76.isEmpty()) {
                        var75 = false;
                        break label446;
                     }
                  }

                  var75 = var76.containsKey(var82);
               }

               if (var75 && !this.method5789(var63)) {
                  continue;
               }

               if (var50++ >= 12) {
                  break;
               }

               var69 = this.method5831(var63.getType(), var63.pos(), this.vec3d, var2);
               var52.put(var67, var69);
               if (var69 == null) {
                  this.items13.put(var67, mc.player.age + 3);
               }
            }

            if (var69 != null) {
               if (var63.getType() == CrystalAuraModule.Type.CRYSTAL || var63.getType() == CrystalAuraModule.Type.OBSIDIAN) {
                  this.blockPos2 = var63.pos();
               }

               if (var63.getType() == CrystalAuraModule.Type.DIG && var63.pos().equals(this.blockPos3)) {
                  this.intValue20 = mc.player.age;
               }

               CrystalAuraModule.Data12 var38;
               var47 = new CrystalAuraModule.Data(
                  var63.getType(),
                  null,
                  var63.pos(),
                  var69,
                  (var38 = var59.getData12()).getFloatValue(),
                  var38.getFloatValue2(),
                  var59.value(),
                  var38.isFlag(),
                  var38.getPlayerEntity()
               );
               break;
            }

            this.intValue25++;
            this.method5936(
               String.format(
                  Locale.ROOT,
                  "%s %s: урон %.1f по %s, но грань не видна из глаз / дальше %.2f б",
                  var63.getType(),
                  method5948(var63.pos()),
                  var59.getData12().getFloatValue(),
                  method5947(var59.getData12().getPlayerEntity()),
                  this.method5878()
               ),
               var59.getData12().getFloatValue()
            );
            if (this.jsonArray2 != null) {
               this.jsonArray2.add(method5940("aimFail", method5948(var63.pos()), "kind", var63.getType().name()));
            }
         }

         if (var47 == null && this.data == null && (!var43.isEmpty())) {
            var43.sort(Comparator.comparingDouble(var0 -> -var0.value()));

            for (Object var60_o : (Iterable<?>)(Object)(var43)) {
               CrystalAuraModule.Data14 var60 = (CrystalAuraModule.Data14)var60_o;
               Rotation var64 = this.method5831(CrystalAuraModule.Type.DIG, var60.getData13().pos(), this.vec3d, var2);
               if (var64 != null) {
                  if (var60.getData13().pos().equals(this.blockPos3)) {
                     this.intValue20 = mc.player.age;
                  }

                  CrystalAuraModule.Data12 var39;
                  var47 = new CrystalAuraModule.Data(
                     CrystalAuraModule.Type.DIG,
                     null,
                     var60.getData13().pos(),
                     var64,
                     (var39 = var60.getData12()).getFloatValue(),
                     var39.getFloatValue2(),
                     var60.value(),
                     true,
                     var39.getPlayerEntity()
                  );
                  break;
               }
            }
         }

         if (var47 != null) {
            this.method5787(var47);
            if (var47.getType() != this.type || !var47.pos().equals(this.blockPos)) {
               this.type = var47.getType();
               this.blockPos = var47.pos().toImmutable();
            }

            this.intValue3 = mc.player.age;
         } else if (this.blockPos != null && mc.player.age - this.intValue3 > 10) {
            this.blockPos = null;
            this.type = null;
         }

         if (!var22.isEmpty()) {
            this.method5799(var22, var47 == null ? 0.0F : var47.getFloatValue3(), var2);
         }

         if (var47 == null) {
            return null;
         } else {
            this.items17.add(var47);
            if (var47.isFlag() && var47.getType() != CrystalAuraModule.Type.DIG) {
               for (Object var61_o : (Iterable<?>)(Object)(var42)) {
                  CrystalAuraModule.Data14 var61 = (CrystalAuraModule.Data14)var61_o;
                  if (this.items17.size() >= 10) {
                     break;
                  }

                  CrystalAuraModule.Data13 var65 = var61.getData13();
                  if (var61.getData12().isFlag()
                     && var65.getType() != CrystalAuraModule.Type.DIG
                     && !var65.pos().equals(var47.pos())
                     && !this.method5786(var65, var61.getData12(), var47)) {
                     CrystalAuraModule.Data12 var40;
                     this.items17
                        .add(
                           new CrystalAuraModule.Data(
                              var65.getType(),
                              null,
                              var65.pos(),
                              null,
                              (var40 = var61.getData12()).getFloatValue(),
                              var40.getFloatValue2(),
                              var61.value(),
                              true,
                              var40.getPlayerEntity()
                           )
                        );
                  }
               }
            }

            if (this.blockPos4 != null && !var47.pos().equals(this.blockPos4)) {
               for (Object var62_o : (Iterable<?>)(Object)(var42)) {
                  CrystalAuraModule.Data14 var62 = (CrystalAuraModule.Data14)var62_o;
                  CrystalAuraModule.Data13 var66 = var62.getData13();
                  if (var66.getType() == this.type3
                     && var66.pos().equals(this.blockPos4)
                     && var62.getData12().isFlag()
                     && !this.method5786(var66, var62.getData12(), var47)) {
                     this.items17.removeIf(var1x -> var1x.getType() == var66.getType() && var1x.pos().equals(var66.pos()));
                     CrystalAuraModule.Data12 var41;
                     this.items17
                        .add(
                           1,
                           new CrystalAuraModule.Data(
                              var66.getType(),
                              null,
                              var66.pos(),
                              null,
                              (var41 = var62.getData12()).getFloatValue(),
                              var41.getFloatValue2(),
                              var62.value(),
                              true,
                              var41.getPlayerEntity()
                           )
                        );
                     break;
                  }
               }
            }

            return var47;
         }
      }
   }

   private boolean method5782() {
      double var1 = this.method5878();
      var var3 = this.items11.keySet().iterator();

      while (var3.hasNext()) {
         BlockPos var6 = BlockPos.fromLong((Long)var3.next());
         if (mc.world.getBlockState(var6).isOf(Blocks.RESPAWN_ANCHOR) && method5962(this.vec3d, new Box(var6)) <= var1 * var1) {
            return true;
         }
      }

      return false;
   }

   private static CrystalAuraModule.Type2 method5783(CrystalAuraModule.Type var0) {
      return switch (var0) {
         case CRYSTAL, OBSIDIAN, DIG -> CrystalAuraModule.Type2.CRYSTALS;
         default -> null;
         case ANCHOR_PLACE, ANCHOR_CHARGE, ANCHOR_EXPLODE -> CrystalAuraModule.Type2.ANCHOR;
      };
   }

   private CrystalAuraModule.Type2 method5784() {
      return this.type2 != null && this.method5896(this.intValue) <= 40 ? this.type2 : null;
   }

   private float method5785(CrystalAuraModule.Data13 var1, CrystalAuraModule.Data12 var2, float var3) {
      CrystalAuraModule.Type2 var4 = this.method5784();
      CrystalAuraModule.Type2 var5 = method5783(var1.getType());
      if (var4 != null && var5 != null && var5 != var4 && (!var2.isFlag() || !this.method5790(var2))) {
         if (var1.getType() == CrystalAuraModule.Type.ANCHOR_CHARGE || var1.getType() == CrystalAuraModule.Type.ANCHOR_EXPLODE) {
            boolean var6;
            label25: {
               var var10000 = this.items11;
               Long var10001 = var1.pos().asLong();
               if (var10000 != null) {
                  if (var10000.isEmpty()) {
                     var6 = false;
                     break label25;
                  }
               }

               var6 = var10000.containsKey(var10001);
            }

            if (var6) {
               return 0.0F;
            }
         }

         return Math.max(3.0F, 0.25F * Math.abs(var3));
      } else {
         return 0.0F;
      }
   }

   private boolean method5786(CrystalAuraModule.Data13 var1, CrystalAuraModule.Data12 var2, CrystalAuraModule.Data var3) {
      CrystalAuraModule.Type2 var4 = method5783(var3.getType());
      CrystalAuraModule.Type2 var5 = method5783(var1.getType());
      return var4 != null && var5 != null && var4 != var5 && !this.method5790(var2);
   }

   private void method5787(CrystalAuraModule.Data var1) {
      CrystalAuraModule.Type2 var2 = method5783(var1.getType());
      if (var2 != null) {
         CrystalAuraModule.Type2 var3 = this.method5784();
         if (var2 != var3 && this.method5925() && var3 != null) {
            this.combatLogger
               .method4612(
                  "WEAPON",
                  "%s -> %s: %s %s, ценность %.1f по %s",
                  var3 == CrystalAuraModule.Type2.CRYSTALS ? "кристаллы" : "якорь",
                  var2 == CrystalAuraModule.Type2.CRYSTALS ? "кристаллы" : "якорь",
                  var1.getType(),
                  method5948(var1.pos()),
                  var1.getFloatValue3(),
                  method5947(var1.getPlayerEntity())
               );
         }

         this.type2 = var2;
         this.intValue = mc.player.age;
      }
   }

   private boolean method5788(PlayerEntity var1) {
      if (var1 == null) {
         return false;
      } else if (var1.timeUntilRegen > 10) {
         return true;
      } else {
         CrystalAuraModule.Entry var2 = this.items14.get(var1.getId());
         return var2 != null && var2.method5969(mc.player.age + this.method5818());
      }
   }

   private boolean method5789(CrystalAuraModule.Data13 var1) {
      return this.blockPos != null && var1.pos().equals(this.blockPos)
         ? var1.getType() == this.type
            || this.type == CrystalAuraModule.Type.OBSIDIAN && var1.getType() == CrystalAuraModule.Type.CRYSTAL
            || this.type == CrystalAuraModule.Type.ANCHOR_PLACE
               && (var1.getType() == CrystalAuraModule.Type.ANCHOR_CHARGE || var1.getType() == CrystalAuraModule.Type.ANCHOR_EXPLODE)
            || this.type == CrystalAuraModule.Type.ANCHOR_CHARGE && var1.getType() == CrystalAuraModule.Type.ANCHOR_EXPLODE
         : false;
   }

   private boolean method5790(CrystalAuraModule.Data12 var1) {
      PlayerEntity var2 = var1.getPlayerEntity();
      return var2 != null && var1.getFloatValue() >= method5902(var2) && !method5791(var2);
   }

   private static boolean method5791(PlayerEntity var0) {
      return var0.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) || var0.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING);
   }

   private static int method5792(CrystalAuraModule.Type var0) {
      return switch (var0) {
         case BREAK, SHIELD -> 0;
         case CRYSTAL -> 40;
         case OBSIDIAN -> 16;
         case DIG -> 16;
         case ANCHOR_PLACE -> 28;
         case ANCHOR_CHARGE, ANCHOR_EXPLODE -> 8;
      };
   }

   private Set<Long> method5793(Set<Long> var1) {
      HashSet<Long> var2 = new HashSet<>();

      for (CrystalAuraModule.Data5 var4 : this.items18) {
         Box var5 = var4.getBox();

         for (int var6 = 0; var6 <= 1; var6++) {
            double var7 = var5.minY + 0.001 + var6;
            var1.add(BlockPos.ofFloored(var5.minX + 0.001, var7, var5.minZ + 0.001).asLong());
            var1.add(BlockPos.ofFloored(var5.maxX - 0.001, var7, var5.minZ + 0.001).asLong());
            var1.add(BlockPos.ofFloored(var5.minX + 0.001, var7, var5.maxZ - 0.001).asLong());
            var1.add(BlockPos.ofFloored(var5.maxX - 0.001, var7, var5.maxZ - 0.001).asLong());
         }
      }

      var var11 = var1.iterator();

      while (var11.hasNext()) {
         BlockPos var12 = BlockPos.fromLong((Long)var11.next());

         for (Direction var8 : Direction.Type.HORIZONTAL) {
            long var9 = var12.offset(var8).asLong();
            if (!var1.contains(var9)) {
               var2.add(var9);
            }
         }
      }

      return var2;
   }

   private void method5794(List<CrystalAuraModule.Data13> var1, BlockPos var2, BlockState var3, Set<Long> var4, EndCrystalEntity var5, Vec3d var6, double var7) {
      if (!var3.isReplaceable()) {
         boolean var16;
         label49: {
            var var10000 = this.items2;
            Long var10001 = var2.asLong();
            if (var10000 != null) {
               if (var10000.isEmpty()) {
                  var16 = false;
                  break label49;
               }
            }

            var16 = var10000.containsKey(var10001);
         }

         if (!var16) {
            int var9;
            if ((var9 = this.method5850(var2, var3)) <= 0) {
               return;
            }

            float var10 = this.method5798(var2, var9);
            BlockPos var11 = var2.down();
            if (method5956(mc.world.getBlockState(var11)) && this.method5866(method5960(var11), var5) && method5962(var6, method5961(var11)) <= var7 * var7) {
               this.method5815(var1, var2, CrystalAuraModule.Type.DIG, method5959(var11), 6.0F, var2, null, var10, var9);
            }

            for (Direction var13 : Direction.Type.HORIZONTAL) {
               BlockPos var14 = var2.offset(var13);
               if (!var4.contains(var14.asLong())) {
                  BlockPos var15 = var14.down();
                  if (method5956(mc.world.getBlockState(var15))
                     && mc.world.isAir(var14)
                     && this.method5865(var15, var5)
                     && method5962(var6, method5961(var15)) <= var7 * var7) {
                     this.method5815(var1, var2, CrystalAuraModule.Type.DIG, method5959(var15), 6.0F, var2, null, var10 - 1.0F, var9);
                  }
               }
            }

            return;
         }
      }
   }

   private boolean method5795(PlayerEntity var1) {
      if (var1 == null) {
         return false;
      } else if (this.method5945(this.method5903(var1))) {
         return true;
      } else {
         CrystalAuraModule.Entry var2 = this.items14.get(var1.getId());
         return var2 != null && var1.isOnGround() && var2.method5971().horizontalLengthSquared() <= 0.0064;
      }
   }

   private Set<Long> method5796() {
      HashSet<Long> var1 = new HashSet<>();

      for (CrystalAuraModule.Data5 var3 : this.items18) {
         CrystalAuraModule.Entry var4 = this.items14.get(var3.getPlayerEntity().getId());
         if (var4 != null && !(var4.method5971().horizontalLengthSquared() > 0.0064) && var3.getPlayerEntity().isOnGround()) {
            Box var5 = var3.getBox();
            var1.add(BlockPos.ofFloored((var5.minX + var5.maxX) * 0.5, var5.minY + 2.0 + 0.001, (var5.minZ + var5.maxZ) * 0.5).asLong());
         }
      }

      return var1;
   }

   private void method5797(List<CrystalAuraModule.Data13> var1, Vec3d var2, double var3) {
      HashSet<Integer> var5 = new HashSet<>();

      for (CrystalAuraModule.Data5 var7 : this.items18) {
         for (EndCrystalEntity var9 : mc.world.getEntitiesByClass(EndCrystalEntity.class, var7.getBox().expand(3.0), var1x -> {
            if (var1x.isAlive()) {
               boolean var2x;
               label20: {
                  var var10000 = this.items6;
                  Integer var10001 = var1x.getId();
                  if (var10000 != null) {
                     if (var10000.isEmpty()) {
                        var2x = false;
                        break label20;
                     }
                  }

                  var2x = var10000.containsKey(var10001);
               }

               if (!var2x) {
                  return true;
               }
            }

            return false;
         })) {
            if (var5.add(var9.getId())) {
               BlockPos var10 = var9.getBlockPos().down();
               BlockState var11 = mc.world.getBlockState(var10);
               int var12;
               if (!var11.isReplaceable()
                  && !this.method5923(var10)
                  && !(method5962(var2, new Box(var10)) > var3 * var3)
                  && (var12 = this.method5850(var10, var11)) > 0) {
                  this.method5815(var1, var10, CrystalAuraModule.Type.DIG, var9.getEntityPos(), 6.0F, var10, null, this.method5798(var10, var12) + 1.0F, var12);
               }
            }
         }
      }
   }

   private float method5798(BlockPos var1, int var2) {
      float var3 = -3.0F - var2 * 0.08F;
      if (var1.equals(this.blockPos3)) {
         var3 += 4.0F + 8.0F * mc.interactionManager.currentBreakingProgress;
      }

      return var3;
   }

   private void method5799(List<CrystalAuraModule.Data11> var1, float var2, Rotation var3) {
      if (this.data == null && this.method5920()) {
         double var4 = this.method5877();
         Vec3d var6 = mc.player.getEyePos();

         for (int var7 = 0; var7 < var1.size() && var7 < 6; var7++) {
            CrystalAuraModule.Data11 var8 = (CrystalAuraModule.Data11)var1.get(var7);
            CrystalAuraModule.Data12 var9 = this.method5821(method5959(var8.pos()), 6.0F, null, null, false);
            if (var9 != null && var9.isFlag() && !(var9.value() - var2 < 4.0F)) {
               EndCrystalEntity var10 = var8.getEndCrystalEntity();
               if (!var10.getBlockPos().down().equals(var8.pos())
                  && !(method5962(var6, var10.getBoundingBox()) > var4 * var4)
                  && (!this.items10.contains(var10.getId()) || this.method5821(var10.getEntityPos(), 6.0F, null, null, true) == null)) {
                  Box var11 = this.box != null ? this.box : mc.player.getBoundingBox();
                  float var12;
                  if (!((var12 = this.class35.method5452(var10.getEntityPos(), 6.0F, mc.player, var11, null, null)) > this.maxSelfDamage.getValue3())
                     && !(var12 >= this.method5830())) {
                     Rotation var13 = this.method5835(this.vec3d, var3, var10) != null ? var3 : this.method5834(this.vec3d, var3, var10);
                     if (var13 != null) {
                        this.data = new CrystalAuraModule.Data(
                           CrystalAuraModule.Type.BREAK,
                           var10,
                           var10.getBlockPos().down(),
                           var13,
                           0.0F,
                           var12,
                           var9.value() - 4.0F,
                           true,
                           var9.getPlayerEntity()
                        );
                        this.items16.addFirst(this.data);
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   private EndCrystalEntity method5800(BlockPos var1, Entity var2) {
      Box var3 = method5960(var1);

      for (CrystalAuraModule.Data5 var5 : this.items18) {
         if (var5.getBox().intersects(var3)) {
            return null;
         }
      }

      EndCrystalEntity var8 = null;

      for (Entity var6 : mc.world.getOtherEntities(null, var3, var2x -> {
         if (!var2x.isSpectator() && var2x != var2 && !this.method5868(var2x)) {
            if (!(var2x instanceof EndCrystalEntity)) {
               return true;
            }

            boolean var3x;
            label33: {
               var var10000 = this.items6;
               Integer var10001 = var2x.getId();
               if (var10000 != null) {
                  if (var10000.isEmpty()) {
                     var3x = false;
                     break label33;
                  }
               }

               var3x = var10000.containsKey(var10001);
            }

            if (!var3x) {
               return true;
            }
         }

         return false;
      })) {
         if (!(var6 instanceof EndCrystalEntity var7) || var8 != null) {
            return null;
         }

         var8 = var7;
      }

      return var8;
   }

   private CrystalAuraModule.Data3 method5801() {
      Box var1 = this.box2 != null ? this.box2 : mc.player.getBoundingBox();
      float var2 = mc.player.getHealth() + mc.player.getAbsorptionAmount();
      BlockPos var3 = BlockPos.ofFloored((var1.minX + var1.maxX) * 0.5, var1.minY + 0.001, (var1.minZ + var1.maxZ) * 0.5);
      ArrayList<CrystalAuraModule.Data10> var4 = new ArrayList();

      for (int var5 = -5; var5 <= 5; var5++) {
         for (int var6 = -5; var6 <= 5; var6++) {
            for (int var7 = -3; var7 <= 2; var7++) {
               BlockPos var8 = var3.add(var5, var7, var6);
               if (method5956(mc.world.getBlockState(var8))) {
                  BlockPos var9 = var8.up();
                  BlockPos var10 = null;
                  if (!mc.world.isAir(var9)) {
                     boolean var28;
                     label148: {
                        var var10000 = this.items20;
                        Long var10001 = var9.asLong();
                        if (var10000 != null) {
                           if (var10000.isEmpty()) {
                              var28 = false;
                              break label148;
                           }
                        }

                        var28 = var10000.containsKey(var10001);
                     }

                     if (!var28) {
                        continue;
                     }

                     var10 = var9;
                  } else if (!this.method5811(var8)) {
                     continue;
                  }

                  if (this.method5812(var8)) {
                     Vec3d var11 = method5959(var8);
                     float var12 = this.class35.method5452(var11, 6.0F, mc.player, var1, var10, null);
                     if (var10 == null) {
                        BlockPos var13 = this.method5813(var11);
                        if (var13 != null) {
                           var12 = Math.max(var12, this.class35.method5452(var11, 6.0F, mc.player, var1, var13, null) * 0.9F);
                        }
                     }

                     if (!(var12 < 1.0F)) {
                        this.floatValue2 = Math.max(this.floatValue2, var12);
                        boolean var27 = this.items4.getOrDefault(var8.asLong(), -32768 << 16 | 0) >= mc.player.age - 60;
                        var4.add(new CrystalAuraModule.Data10(var8.toImmutable(), var11, var10, var12, 6.0F, var27));
                     }
                  }
               }
            }
         }
      }

      this.method5808(var4, var1, var3, this.method5807());

      for (EndCrystalEntity var16 : mc.world.getEntitiesByClass(EndCrystalEntity.class, var1.expand(6.0), var1x -> {
         if (var1x.isAlive() && !this.items10.contains(var1x.getId())) {
            boolean var2x;
            label21: {
               var var10000x = this.items6;
               Integer var10001x = var1x.getId();
               if (var10000x != null) {
                  if (var10000x.isEmpty()) {
                     var2x = false;
                     break label21;
                  }
               }

               var2x = var10000x.containsKey(var10001x);
            }

            if (!var2x) {
               return true;
            }
         }

         return false;
      })) {
         Vec3d var18 = var16.getEntityPos();
         float var20;
         if (!((var20 = this.class35.method5452(var18, 6.0F, mc.player, var1, null, null)) < 1.0F)) {
            this.floatValue2 = Math.max(this.floatValue2, var20);
            var4.add(new CrystalAuraModule.Data10(var16.getBlockPos().down().toImmutable(), var18, null, var20, 6.0F, true));
         }
      }

      Item var15 = this.method5810();
      if (var15 != null && this.method5921() && (!var4.isEmpty())) {
         var4.sort(Comparator.comparingDouble(var0 -> -var0.getFloatValue()));
         float var17 = ((CrystalAuraModule.Data10)var4.getFirst()).getFloatValue();
         float var19 = 0.0F;

         for (Object var23_o : (Iterable<?>)(Object)(var4)) {
            CrystalAuraModule.Data10 var23 = (CrystalAuraModule.Data10)var23_o;
            if (var23.isFlag()) {
               var19 = Math.max(var19, var23.getFloatValue());
            }
         }

         float var22 = 0.0F;

         for (Object var26_o : (Iterable<?>)(Object)(var4)) {
            CrystalAuraModule.Data10 var26 = (CrystalAuraModule.Data10)var26_o;
            if (!var26.isFlag()) {
               var22 = Math.max(var22, var26.getFloatValue());
            }
         }

         boolean var25 = this.method5806() && var22 >= var2 - 1.0F || var19 >= Math.max(this.shieldDamage.getValue3(), var2 * 0.4F) || var19 >= var2 - 1.0F;
         this.method5933(var4, var25);
         return !var25 && var17 < this.shieldDamage.getValue3()
            ? null
            : new CrystalAuraModule.Data3(var4, var1, var3, var15, var25, var19 >= Math.max(this.shieldDamage.getValue3(), var2 * 0.4F));
      } else {
         return null;
      }
   }

   private CrystalAuraModule.Data method5802(CrystalAuraModule.Data3 var1, Rotation var2, CrystalAuraModule.Data var3) {
      var var4 = var1.getItems();
      if (var1.isFlag2()) {
         var4 = var4.stream().filter(CrystalAuraModule.Data10::isFlag).toList();
      }

      List<CrystalAuraModule.Data10> var5 = var4.subList(0, Math.min(4, var4.size()));
      float var6 = ((CrystalAuraModule.Data10)var4.getFirst()).getFloatValue();
      float var7 = var4.size() > 4 ? ((CrystalAuraModule.Data10)var4.get(4)).getFloatValue() : 0.0F;
      boolean var8 = var1.isFlag();
      Box var9 = var1.getBox();
      Vec3d var10 = mc.player.getEyePos();
      LinkedHashSet<BlockPos> var11 = new LinkedHashSet();

      for (Object var13_o : (Iterable<?>)(Object)(var5)) {
         CrystalAuraModule.Data10 var13 = (CrystalAuraModule.Data10)var13_o;
         if (var13.getBlockPos2() == null && (var8 || !this.method5803(var13.getBlockPos()))) {
            var11.add(var13.getBlockPos().up());
         }
      }

      Vec3d var26 = this.method5804(var1.getBlockPos());
      BlockPos var27 = var1.getBlockPos().up();

      for (Direction var15 : Direction.Type.HORIZONTAL) {
         if (var8 || var26 == null || !(var15.getOffsetX() * var26.x + var15.getOffsetZ() * var26.z > 0.3)) {
            var11.add(var1.getBlockPos().offset(var15));
            var11.add(var27.offset(var15));
         }
      }

      BlockPos var28 = null;
      Rotation var29 = null;
      float var16 = var6;
      float var17 = 0.0F;

      for (Object var19_o : (Iterable<?>)(Object)(var11)) {
         BlockPos var19 = (BlockPos)var19_o;
         boolean var20;
         if (!this.method5923(var19)
            && this.method5867(var19)
            && !this.method5777(var19)
            && (!(var20 = this.method5805(var19, var10, var3)) || var8 && var1.isFlag2())) {
            float var21 = var7;
            float var22 = 0.0F;

            for (Object var24_o : (Iterable<?>)(Object)(var5)) {
               CrystalAuraModule.Data10 var24 = (CrystalAuraModule.Data10)var24_o;
               float var25;
               if (var24.getBlockPos2() == null && var19.equals(var24.getBlockPos().up())) {
                  var25 = var1.getItem() == Items.OBSIDIAN && mc.world.isAir(var19.up()) && this.method5812(var19)
                     ? this.class35.method5452(method5959(var19), 6.0F, mc.player, var9, null, var19)
                     : 0.0F;
               } else {
                  var25 = this.class35.method5452(var24.getVec3d(), var24.getFloatValue2(), mc.player, var9, var24.getBlockPos2(), var19);
               }

               var21 = Math.max(var21, var25);
               var22 += Math.max(0.0F, var24.getFloatValue() - var25);
            }

            float var31 = var6 - var21 + var22 * 0.1F - (var20 ? 2.5F : 0.0F);
            if (!(var6 - var21 < 0.5F) && !(var31 <= var17)) {
               Rotation var32 = this.method5831(CrystalAuraModule.Type.SHIELD, var19, this.vec3d, var2);
               if (var32 != null) {
                  var17 = var31;
                  var16 = var21;
                  var28 = var19;
                  var29 = var32;
               }
            }
         }
      }

      if (var28 == null) {
         return null;
      } else {
         float var30 = mc.player.getHealth() + mc.player.getAbsorptionAmount();
         if (var8 && var16 >= var30 - 1.0F && var6 - var16 < 3.0F) {
            return null;
         } else {
            this.item = var1.getItem();
            if (this.method5925()) {
               this.combatLogger
                  .method4612(
                     var8 ? "DEF!" : "DEF",
                     "план %s %s: худший урон %.1f -> %.1f (спотов %d)",
                     method5948(var28),
                     method5950(var1.getItem()),
                     var6,
                     var16,
                     var4.size()
                  );
            }

            return new CrystalAuraModule.Data(
               CrystalAuraModule.Type.SHIELD, null, var28.toImmutable(), var29, 0.0F, var6, var17, var8, this.items18.getFirst().getPlayerEntity()
            );
         }
      }
   }

   private boolean method5803(BlockPos var1) {
      CrystalAuraModule.Data12 var2 = this.method5821(method5959(var1), 6.0F, null, null, false);
      return var2 != null && var2.isFlag();
   }

   private Vec3d method5804(BlockPos var1) {
      Vec3d var2 = Vec3d.ofBottomCenter(var1);
      Vec3d var3 = null;
      double var4 = Double.MAX_VALUE;

      for (CrystalAuraModule.Data5 var7 : this.items18) {
         Vec3d var8 = var7.getVec3d().subtract(var2);
         double var9;
         if ((var9 = var8.horizontalLengthSquared()) > 1.0E-4 && var9 < var4) {
            var4 = var9;
            var3 = new Vec3d(var8.x, 0.0, var8.z).normalize();
         }
      }

      return var3;
   }

   private boolean method5805(BlockPos var1, Vec3d var2, CrystalAuraModule.Data var3) {
      Box var4 = new Box(var1);

      for (CrystalAuraModule.Data var6 : this.items17) {
         if (var6.getType() != CrystalAuraModule.Type.SHIELD && (var6.pos().equals(var1) || method5960(var6.pos()).intersects(var4))) {
            return true;
         }
      }

      if (this.data != null && var4.raycast(var2, this.data.getEndCrystalEntity().getBoundingBox().getCenter()).isPresent()) {
         return true;
      } else if (var3 == null) {
         return false;
      } else if (!var3.pos().equals(var1) && var4.raycast(var2, Vec3d.ofCenter(var3.pos())).isPresent()) {
         return true;
      } else {
         if ((var3.getType() == CrystalAuraModule.Type.CRYSTAL || var3.getType() == CrystalAuraModule.Type.OBSIDIAN)
            && var3.isFlag()
            && var3.getPlayerEntity() != null) {
            for (CrystalAuraModule.Data5 var9 : this.items18) {
               if (var9.getPlayerEntity() == var3.getPlayerEntity()) {
                  return this.class35.method5452(method5959(var3.pos()), 6.0F, var9.getPlayerEntity(), var9.getBox2(), null, var1)
                     < var3.getFloatValue() * 0.85F;
               }
            }
         }

         return false;
      }
   }

   private boolean method5806() {
      if (this.method5896(this.intValue2) <= 100) {
         return true;
      } else {
         for (CrystalAuraModule.Data5 var2 : this.items18) {
            if (var2.getPlayerEntity().getMainHandStack().isOf(Items.END_CRYSTAL) || var2.getPlayerEntity().getOffHandStack().isOf(Items.END_CRYSTAL)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean method5807() {
      if (this.method5896(this.intValue21) <= 100) {
         return true;
      } else {
         for (CrystalAuraModule.Data5 var2 : this.items18) {
            ItemStack var3 = var2.getPlayerEntity().getMainHandStack();
            if ((var3.isOf(Items.RESPAWN_ANCHOR) || var3.isOf(Items.GLOWSTONE)) && var2.getVec3d().distanceTo(mc.player.getEntityPos()) <= 7.0) {
               return true;
            }
         }

         return false;
      }
   }

   private void method5808(List<CrystalAuraModule.Data10> var1, Box var2, BlockPos var3, boolean var4) {
      if (mc.world.getRegistryKey() != World.NETHER) {
         this.items11.values().removeIf(var0 -> var0 < mc.player.age);

         for (int var5 = -2; var5 <= 2; var5++) {
            for (int var6 = -2; var6 <= 2; var6++) {
               for (int var7 = -1; var7 <= 2; var7++) {
                  BlockPos var8;
                  BlockState var9;
                  boolean var14;
                  label80: {
                     var8 = var3.add(var5, var7, var6);
                     var9 = mc.world.getBlockState(var8);
                     if (var9.isOf(Blocks.RESPAWN_ANCHOR)) {
                        label77: {
                           var var10000 = this.items11;
                           Long var10001 = var8.asLong();
                           if (var10000 != null) {
                              if (var10000.isEmpty()) {
                                 var14 = false;
                                 break label77;
                              }
                           }

                           var14 = var10000.containsKey(var10001);
                        }

                        if (!var14) {
                           var14 = true;
                           break label80;
                        }
                     }

                     var14 = false;
                  }

                  boolean var10 = var14;
                  if ((var14 || var4 && var9.isReplaceable() && this.method5811(var8.down()))
                     && !var2.intersects(new Box(var8))
                     && (var10 || this.method5809(var8) && this.method5812(var8))) {
                     Vec3d var11 = Vec3d.ofCenter(var8);
                     float var12;
                     if (!((var12 = this.class35.method5452(var11, 5.0F, mc.player, var2, var8, null)) < 2.0F)) {
                        this.floatValue2 = Math.max(this.floatValue2, var12);
                        var1.add(new CrystalAuraModule.Data10(var8.down().toImmutable(), var11, var10 ? var8.toImmutable() : null, var12, 5.0F, var10));
                     }
                  }
               }
            }
         }
      }
   }

   private boolean method5809(BlockPos var1) {
      for (Direction var5 : Direction.values()) {
         if (!mc.world.getBlockState(var1.offset(var5)).isReplaceable()) {
            return true;
         }
      }

      return false;
   }

   private Item method5810() {
      for (Item var4 : new Item[]{Items.CRYING_OBSIDIAN, Items.ENDER_CHEST, Items.OBSIDIAN}) {
         if (this.method5915(var1 -> var1.isOf(var4))) {
            return var4;
         }
      }

      return null;
   }

   private boolean method5811(BlockPos var1) {
      var var10000 = mc.world.getOtherEntities(null, method5960(var1), var0 -> !var0.isSpectator() && !(var0 instanceof EndCrystalEntity));
      return var10000.isEmpty();
   }

   private boolean method5812(BlockPos var1) {
      Box var2 = new Box(var1);

      for (CrystalAuraModule.Data5 var4 : this.items18) {
         Box var5 = var4.getBox();
         Vec3d var6 = new Vec3d((var5.minX + var5.maxX) * 0.5, var5.minY + 1.62, (var5.minZ + var5.maxZ) * 0.5);
         if (method5962(var6, var2) <= 25.0) {
            return true;
         }
      }

      return false;
   }

   private BlockPos method5813(Vec3d var1) {
      BlockPos var2 = null;
      double var3 = 3.24;
      var var5 = this.items20.keySet().iterator();

      while (var5.hasNext()) {
         BlockPos var8 = BlockPos.fromLong((Long)var5.next());
         double var9;
         if ((var9 = Vec3d.ofCenter(var8).squaredDistanceTo(var1)) < var3) {
            var3 = var9;
            var2 = var8;
         }
      }

      return var2;
   }

   private void method5814(
      List<CrystalAuraModule.Data13> var1, BlockPos var2, CrystalAuraModule.Type var3, Vec3d var4, float var5, BlockPos var6, BlockPos var7, float var8
   ) {
      this.method5815(var1, var2, var3, var4, var5, var6, var7, var8, 0);
   }

   private void method5815(
      List<CrystalAuraModule.Data13> var1,
      BlockPos var2,
      CrystalAuraModule.Type var3,
      Vec3d var4,
      float var5,
      BlockPos var6,
      BlockPos var7,
      float var8,
      int var9
   ) {
      float var10 = 0.0F;

      for (CrystalAuraModule.Data5 var12 : this.items18) {
         float var13;
         if ((
               var13 = Math.max(
                  this.class35.method5453(var4, var5, var12.getPlayerEntity(), var12.getBox2()),
                  this.class35.method5453(var4, var5, var12.getPlayerEntity(), var12.getBox3())
               )
            )
            >= Math.min(this.method5827(var12), var12.getFloatValue())) {
            var10 = Math.max(var10, var13);
         }
      }

      if (var10 > 0.0F) {
         var1.add(
            new CrystalAuraModule.Data13(
               var2.toImmutable(), var3, var4, var5, var6 == null ? null : var6.toImmutable(), var7 == null ? null : var7.toImmutable(), var8, var10, var9
            )
         );
      } else {
         this.method5937(var3 + ": даже без укрытий урон ниже порога");
      }
   }

   private Set<BlockPos> method5816(Vec3d var1, double var2) {
      LinkedHashSet var4 = new LinkedHashSet();

      for (CrystalAuraModule.Data5 var6 : this.items18) {
         method5817(var4, var6.getBox(), var1, var2);
         method5817(var4, var6.getBox2(), var1, var2);
         method5817(var4, var6.getBox3(), var1, var2);
      }

      return var4;
   }

   private static void method5817(Set<BlockPos> var0, Box var1, Vec3d var2, double var3) {
      BlockPos var5 = BlockPos.ofFloored((var1.minX + var1.maxX) * 0.5, var1.minY + 0.001, (var1.minZ + var1.maxZ) * 0.5);

      for (int var6 = -3; var6 <= 3; var6++) {
         for (int var7 = -3; var7 <= 3; var7++) {
            for (int var8 = -3; var8 <= 2; var8++) {
               BlockPos var9 = var5.add(var6, var8, var7);
               if (method5962(var2, new Box(var9)) <= var3 * var3) {
                  var0.add(var9);
               }
            }
         }
      }
   }

   private int method5818() {
      return Math.max(1, (this.intValue15 + 1) / 2);
   }

   private int method5819() {
      return this.intValue15 + this.method5818() + 1;
   }

   private Box method5820(int var1) {
      Box var2 = mc.player.getBoundingBox();
      Vec3d var3 = mc.player.getVelocity();
      return mc.player.isGliding() ? var2.offset(var3.multiply(var1)) : this.method5906(mc.player, var2, var3, mc.player.isOnGround(), Math.min(var1, 12));
   }

   private CrystalAuraModule.Data12 method5821(Vec3d var1, float var2, BlockPos var3, BlockPos var4, boolean var5) {
      return this.method5822(var1, var2, var3, var4, var5, 0);
   }

   private CrystalAuraModule.Data12 method5822(Vec3d var1, float var2, BlockPos var3, BlockPos var4, boolean var5, int var6) {
      int var7 = (var5 ? this.method5818() : this.method5819()) + var6;
      PlayerEntity var8 = null;
      float var9 = 0.0F;
      float var10 = -Float.MAX_VALUE;
      boolean var11 = false;
      PlayerEntity var12 = null;
      float var13 = -Float.MAX_VALUE;
      this.text2 = null;
      float var14 = -1.0F;
      float var15 = 0.0F;
      PlayerEntity var16 = null;

      for (CrystalAuraModule.Data5 var18 : this.items18) {
         float var19 = this.method5827(var18);
         Box var20 = var5 ? var18.getBox() : var18.getBox2();
         float var21 = Math.min(var19, var18.getFloatValue());
         boolean var22 = this.class35.method5453(var1, var2, var18.getPlayerEntity(), var20) >= var21;
         boolean var23 = this.class35.method5453(var1, var2, var18.getPlayerEntity(), var18.getBox3()) >= var21;
         if (var22 || var23) {
            float var24;
            if ((var24 = var22 ? this.method5823(var18, var1, var2, var20, var3, var4, var7) : 0.0F) > var14) {
               var14 = var24;
               var15 = var21;
               var16 = var18.getPlayerEntity();
            }

            boolean var25 = var24 >= var18.getFloatValue();
            if (!(var24 >= var19) && !var25) {
               if (var8 == null) {
                  float var35;
                  if (var22
                     && !var5
                     && this.entry2.isEnabled()
                     && this.method5788(var18.getPlayerEntity())
                     && (var35 = this.method5824(var18, var1, var2, var20, var3, var4, var7, true)) >= var19
                     && var35 * 0.8F > var13) {
                     var13 = var35 * 0.8F;
                     var12 = var18.getPlayerEntity();
                  }

                  if (var23
                     && (
                           var35 = method5826(var18.getPlayerEntity(), var1)
                              ? 0.0F
                              : this.class35.method5452(var1, var2, var18.getPlayerEntity(), var18.getBox3(), var3, var4)
                        )
                        >= var19
                     && var35 * 0.7F > var13) {
                     var13 = var35 * 0.7F;
                     var12 = var18.getPlayerEntity();
                  }
               }
            } else {
               float var26;
               if ((var26 = var24 + (var25 ? (var18.isFlag() ? 6.0F : 15.0F) : 0.0F) + this.method5825(var18, var24, var25)) > var10) {
                  var10 = var26;
                  var9 = var24;
                  var8 = var18.getPlayerEntity();
                  var11 = var25 && !var18.isFlag();
               }
            }
         }
      }

      boolean var27;
      if (!(var27 = var8 != null) && var12 == null) {
         this.text2 = var16 == null
            ? "ни одна цель не достаёт даже по верхней оценке"
            : String.format(
               Locale.ROOT,
               "мало урона: %.1f по %s, нужно %.1f%s",
               var14,
               method5947(var16),
               var15,
               var14 == 0.0F ? " (сейчас не достаёт / щит / i-frames)" : ""
            );
         return null;
      } else {
         Box var28 = var5 ? this.box : this.box2;
         if (var28 == null) {
            var28 = mc.player.getBoundingBox();
         }

         float var29;
         if ((var29 = this.class35.method5452(var1, var2, mc.player, var28, var3, var4)) >= this.method5830()) {
            this.text2 = String.format(Locale.ROOT, "по нам %.1f — летально (у нас %.1f)", var29, this.method5830() + 0.5F);
            return null;
         } else if (!(var29 > this.maxSelfDamage.getValue3()) || var27 && var11) {
            float var30 = 0.0F;

            for (PlayerEntity var33 : this.items19) {
               float var34;
               if ((var34 = this.class35.method5452(var1, var2, var33, var33.getBoundingBox(), var3, var4)) > this.maxSelfDamage.getValue3()
                  || var34 >= method5902(var33) - 0.5F) {
                  this.text2 = String.format(Locale.ROOT, "задевает друга %s на %.1f", method5947(var33), var34);
                  return null;
               }

               var30 = Math.max(var30, var34);
            }

            float var32 = this.method5828();
            return var27
               ? new CrystalAuraModule.Data12(var8, var9, var29, var10 - var29 * var32 - var30, true)
               : new CrystalAuraModule.Data12(var12, 0.0F, var29, var13 - var29 * var32 - var30, false);
         } else {
            this.text2 = String.format(Locale.ROOT, "по нам %.1f > макс %.1f (по цели %.1f)", var29, this.maxSelfDamage.getValue3(), var9);
            return null;
         }
      }
   }

   private float method5823(CrystalAuraModule.Data5 var1, Vec3d var2, float var3, Box var4, BlockPos var5, BlockPos var6, int var7) {
      return this.method5824(var1, var2, var3, var4, var5, var6, var7, false);
   }

   private float method5824(CrystalAuraModule.Data5 var1, Vec3d var2, float var3, Box var4, BlockPos var5, BlockPos var6, int var7, boolean var8) {
      if (method5826(var1.getPlayerEntity(), var2)) {
         return 0.0F;
      } else {
         float var9 = this.class35.method5454(var2, var3, var1.getPlayerEntity(), var4, var5, var6);
         if (this.entry2.isEnabled() && !var8) {
            float var10 = var1.getIntValue() - var7 - (this.intValue15 + 1) / 2 > 10 ? var1.getFloatValue2() : 0.0F;
            CrystalAuraModule.Entry var11 = this.items14.get(var1.getPlayerEntity().getId());
            if (var11 != null && var11.method5969(mc.player.age + var7)) {
               var10 = Math.max(var10, var11.floatValue8);
            }

            if ((var9 -= var10) <= 0.0F) {
               return 0.0F;
            }
         }

         float var12 = this.class35.method5456(var1.getPlayerEntity(), var9);
         if (var1.isFlag3() && !var1.getPlayerEntity().hasStatusEffect(StatusEffects.RESISTANCE)) {
            var12 *= 0.8F;
         }

         return var12;
      }
   }

   private float method5825(CrystalAuraModule.Data5 var1, float var2, boolean var3) {
      if (!this.entry16.isEnabled() || !var1.isFlag()) {
         return 0.0F;
      } else if (var3) {
         float var5 = -Math.min(4.0F, (var2 - var1.getFloatValue()) * 0.5F);
         return var1.getIntValue2() <= 10 ? var5 + 4.0F : var5;
      } else {
         float var4;
         return (var4 = var1.getFloatValue() - var2) > 0.0F && var4 < 2.0F ? 2.5F : 0.0F;
      }
   }

   private static boolean method5826(PlayerEntity var0, Vec3d var1) {
      if (!var0.isBlocking()) {
         return false;
      } else {
         Vec3d var2 = var0.getRotationVector(0.0F, var0.getHeadYaw());
         Vec3d var3 = var1.subtract(var0.getEntityPos());
         var3 = new Vec3d(var3.x, 0.0, var3.z);
         return var3.lengthSquared() < 1.0E-6 ? false : Math.acos(MathHelper.clamp(var3.normalize().dotProduct(var2), -1.0, 1.0)) <= Math.PI / 2;
      }
   }

   private float method5827(CrystalAuraModule.Data5 var1) {
      float var2 = this.minDamage.getValue3();
      return !(var1.getFloatValue() <= this.faceplace.getValue3()) && !var1.isFlag2() ? var2 : Math.min(var2, 2.0F);
   }

   private float method5828() {
      float var1 = mc.player.getHealth() + mc.player.getAbsorptionAmount();
      return 1.2F * MathHelper.clamp(16.0F / Math.max(1.0F, var1), 1.0F, 3.0F);
   }

   private boolean method5829() {
      PlayerInventory var1 = mc.player.getInventory();

      for (int var2 = 0; var2 < var1.size(); var2++) {
         ItemStack var3 = var1.getStack(var2);
         if (var3.isOf(Items.END_CRYSTAL) || var3.isOf(Items.RESPAWN_ANCHOR) || var3.isOf(Items.COBWEB) && this.entry23.isEnabled()) {
            return true;
         }
      }

      return mc.player.getOffHandStack().isOf(Items.END_CRYSTAL);
   }

   private float method5830() {
      return !this.entry9.isEnabled() ? Float.MAX_VALUE : Math.max(0.0F, mc.player.getHealth() + mc.player.getAbsorptionAmount() - 2.0F);
   }

   private Rotation method5831(CrystalAuraModule.Type var1, BlockPos var2, Vec3d var3, Rotation var4) {
      return switch (var1) {
         case BREAK -> null;
         case CRYSTAL -> this.method5836(var2, method5961(var2), var3, var4);
         case OBSIDIAN, SHIELD, ANCHOR_PLACE -> this.method5841(var2, var3, var4);
         case DIG, ANCHOR_CHARGE, ANCHOR_EXPLODE -> this.method5836(var2, null, var3, var4);
      };
   }

   private BlockHitResult method5832(CrystalAuraModule.Data var1, Vec3d var2, Rotation var3) {
      BlockHitResult var4 = this.method5875(var2, var3, this.method5878());
      if (var4.getType() == HitResult.Type.BLOCK && !var4.isInsideBlock()) {
         return switch (var1.getType()) {
            case BREAK -> null;
            case CRYSTAL, DIG, ANCHOR_CHARGE, ANCHOR_EXPLODE -> var4.getBlockPos().equals(var1.pos()) ? var4 : null;
            case OBSIDIAN, SHIELD, ANCHOR_PLACE -> this.method5833(var4, var1.pos()) ? var4 : null;
         };
      } else {
         return null;
      }
   }

   private boolean method5833(BlockHitResult var1, BlockPos var2) {
      BlockState var3 = mc.world.getBlockState(var1.getBlockPos());
      return !var3.isReplaceable() && var1.getBlockPos().offset(var1.getSide()).equals(var2)
         ? mc.player.isSneaking() || var3.createScreenHandlerFactory(mc.world, var1.getBlockPos()) == null
         : false;
   }

   private Rotation method5834(Vec3d var1, Rotation var2, EndCrystalEntity var3) {
      Box var4 = var3.getBoundingBox();
      Rotation var5 = null;
      double var6 = Double.MAX_VALUE;

      for (double var11 : CONST_doubleValues2) {
         for (double var16 : CONST_doubleValues2) {
            for (double var21 : CONST_doubleValues2) {
               Vec3d var23 = new Vec3d(
                  MathHelper.lerp(var11, var4.minX, var4.maxX), MathHelper.lerp(var16, var4.minY, var4.maxY), MathHelper.lerp(var21, var4.minZ, var4.maxZ)
               );
               Rotation var24 = method5898(var1, var23);
               double var25;
               if (this.method5835(var1, var24, var3) != null && (var25 = var2.method3131(var24).method5327()) < var6) {
                  var6 = var25;
                  var5 = var24;
               }
            }
         }
      }

      return var5;
   }

   private Vec3d method5835(Vec3d var1, Rotation var2, EndCrystalEntity var3) {
      Box var4 = var3.getBoundingBox();
      if (var4.contains(var1)) {
         return var1;
      } else {
         var var5 = var4.raycast(var1, var1.add(var2.method3135().multiply(this.method5877())));
         if (var5.isEmpty()) {
            return null;
         } else {
            return !this.data3.isFlag5() && this.method5874(var1, (Vec3d)var5.get()) ? null : (Vec3d)var5.get();
         }
      }
   }

   private Rotation method5836(BlockPos var1, Box var2, Vec3d var3, Rotation var4) {
      double var5 = this.method5878();
      BlockState var7 = mc.world.getBlockState(var1);
      VoxelShape var8 = var7.getOutlineShape(mc.world, var1);
      List<Box> var9 = var8.isEmpty() ? List.of(new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)) : var8.getBoundingBoxes();
      ArrayList var10 = new ArrayList();

      for (Object var12_o : (Iterable<?>)(Object)(var9)) {
         Box var12 = (Box)var12_o;
         Box var13 = var12.offset(var1);

         for (Direction var17 : Direction.values()) {
            Vec3d var18 = new Vec3d(var17.getOffsetX(), var17.getOffsetY(), var17.getOffsetZ());

            for (double var22 : CONST_doubleValues) {
               for (double var27 : CONST_doubleValues) {
                  Vec3d var29 = method5963(var13, var17, var22 + this.doubleValue2, var27 + this.doubleValue3);
                  if (!(var3.subtract(var29).dotProduct(var18) <= 0.0) && !(var3.squaredDistanceTo(var29) > var5 * var5)) {
                     Rotation var30 = method5898(var3, var29);
                     BlockHitResult var31 = this.method5875(var3, var30, var5);
                     if (var31.getType() == HitResult.Type.BLOCK && !var31.isInsideBlock() && var31.getBlockPos().equals(var1) && !this.method5838(var3, var30)
                        )
                      {
                        double var32 = var4.method3131(var30).method5327()
                           + (var17 == Direction.UP ? 0.0 : 2.0)
                           + (Math.abs(var22 - 0.5) + Math.abs(var27 - 0.5)) * 4.0;
                        if (var2 != null && var2.raycast(var3, var3.add(var30.method3135().multiply(this.method5877()))).isEmpty()) {
                           var32 += 6.0;
                        }

                        var10.add(new CrystalAuraModule.Data4(var30, var32, var1, null));
                     }
                  }
               }
            }
         }
      }

      return this.method5839(var10);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private Rotation method5837(CrystalAuraModule.Data var1, EndCrystalEntity var2) {
      if (var1 != null && var1.isFlag() && var1.getType() != CrystalAuraModule.Type.BREAK && var1.getType() != CrystalAuraModule.Type.DIG) {
         this.box3 = var2.getBoundingBox();

         Rotation var3;
         try {
            var3 = this.method5831(var1.getType(), var1.pos(), this.vec3d, this.method5897());
         } catch (Throwable var6) {
            this.box3 = null;
            throw var6;
         }

         this.box3 = null;
         return var3;
      } else {
         return null;
      }
   }

   private boolean method5838(Vec3d var1, Rotation var2) {
      return this.box3 != null && this.box3.raycast(var1, var1.add(var2.method3135().multiply(this.method5877()))).isEmpty();
   }

   private Rotation method5839(List<CrystalAuraModule.Data4> var1) {
      if (var1.isEmpty()) {
         return null;
      } else {
         var1.sort(Comparator.comparingDouble(CrystalAuraModule.Data4::getDoubleValue));

         for (int var2 = 0; var2 < var1.size() && var2 < 6; var2++) {
            CrystalAuraModule.Data4 var3 = (CrystalAuraModule.Data4)var1.get(var2);
            if (this.method5840(var3.rotation(), var3.pos(), var3.getDirection())) {
               return var3.rotation();
            }
         }

         return ((CrystalAuraModule.Data4)var1.getFirst()).rotation();
      }
   }

   private boolean method5840(Rotation var1, BlockPos var2, Direction var3) {
      double var4 = this.method5878();

      for (Vec3d var9 : new Vec3d[]{this.vec3d, mc.player.getEyePos()}) {
         for (float[] var13 : CONST_floatValues) {
            Rotation var14 = new Rotation(var1.getFloatValue() + var13[0], MathHelper.clamp(var1.getFloatValue2() + var13[1], -90.0F, 90.0F));
            BlockHitResult var15 = this.method5875(var9, var14, var4);
            if (var15.getType() != HitResult.Type.BLOCK
               || var15.isInsideBlock()
               || !var15.getBlockPos().equals(var2)
               || var3 != null && var15.getSide() != var3) {
               return false;
            }
         }
      }

      return true;
   }

   private Rotation method5841(BlockPos var1, Vec3d var2, Rotation var3) {
      double var4 = this.method5878();
      Box var6 = method5961(var1);
      ArrayList var7 = new ArrayList();

      for (Direction var11 : Direction.values()) {
         BlockPos var12 = var1.offset(var11);
         BlockState var13 = mc.world.getBlockState(var12);
         if (!var13.isReplaceable() && (mc.player.isSneaking() || var13.createScreenHandlerFactory(mc.world, var12) == null)) {
            VoxelShape var14 = var13.getOutlineShape(mc.world, var12);
            if (!var14.isEmpty()) {
               Direction var15 = var11.getOpposite();

               for (Box var17 : var14.getBoundingBoxes()) {
                  Box var18 = var17.offset(var12);

                  for (double var22 : CONST_doubleValues) {
                     for (double var27 : CONST_doubleValues) {
                        Vec3d var29 = method5963(var18, var15, var22 + this.doubleValue2, var27 + this.doubleValue3);
                        if (!(var2.squaredDistanceTo(var29) > var4 * var4)) {
                           Rotation var30 = method5898(var2, var29);
                           BlockHitResult var31 = this.method5875(var2, var30, var4);
                           if (var31.getType() == HitResult.Type.BLOCK
                              && !var31.isInsideBlock()
                              && var31.getBlockPos().equals(var12)
                              && var31.getSide() == var15
                              && !this.method5838(var2, var30)) {
                              double var32 = var3.method3131(var30).method5327()
                                 + var2.distanceTo(var29)
                                 + (Math.abs(var22 - 0.5) + Math.abs(var27 - 0.5)) * 6.0;
                              if (var11 != Direction.DOWN) {
                                 var32 += 2.0;
                              }

                              if (var6.raycast(var2, var2.add(var30.method3135().multiply(this.method5877()))).isEmpty()) {
                                 var32 += 4.0;
                              }

                              var7.add(new CrystalAuraModule.Data4(var30, var32, var12.toImmutable(), var15));
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return this.method5839(var7);
   }

   private boolean method5842(EndCrystalEntity var1, int var2) {
      int var3;
      if (!this.entry8.isEnabled() || !this.method5911() || (var3 = this.method5853()) != -1 && this.method5859(var3) != null) {
         mc.interactionManager.attackEntity(mc.player, var1);
         mc.player.swingHand(Hand.MAIN_HAND);
         this.items6.put(var1.getId(), var2 + this.method5909());
         this.items7.put(var1.getId(), Math.sqrt(method5962(mc.player.getEyePos(), var1.getBoundingBox())));
         this.data8 = new CrystalAuraModule.Data8(var1.getEntityPos(), 6.0F, null, var2);
         this.method5843(var1.getEntityPos(), 6.0F, null, var2);
         this.intValue17 = var2;
         this.intValue8++;
         this.method5860();
         return true;
      } else {
         return false;
      }
   }

   private void method5843(Vec3d var1, float var2, BlockPos var3, int var4) {
      int var5 = var4 + this.method5818();

      for (CrystalAuraModule.Data5 var7 : this.items18) {
         CrystalAuraModule.Entry var8 = this.items14.get(var7.getPlayerEntity().getId());
         if (var8 != null && !method5826(var7.getPlayerEntity(), var1)) {
            var8.method5968(var5, this.class35.method5454(var1, var2, var7.getPlayerEntity(), var7.getBox(), var3, null));
         }
      }
   }

   private boolean method5844(CrystalAuraModule.Data var1, BlockHitResult var2, Rotation var3, int var4) {
      Hand var5 = this.method5858(var0 -> var0.isOf(Items.END_CRYSTAL), true);
      if (var5 == null) {
         return false;
      } else {
         this.method5856(var5, var2, Items.END_CRYSTAL, method5958(var1.pos(), 0), var4, true);
         this.items.put(var1.pos().asLong(), var4 + this.method5909());
         this.method5857(var3, var4);
         if (this.method5925()) {
            this.combatLogger.intValue2++;
            this.combatLogger
               .method4612(
                  "PLACE",
                  "кристалл на %s%s: урон %.1f по %s (hp %.1f), по нам %.1f, до места %.2f б, цель в %.2f б, рука %s",
                  method5948(var1.pos()),
                  var1.isFlag() ? "" : " (на вырост)",
                  var1.getFloatValue(),
                  method5947(var1.getPlayerEntity()),
                  var1.getPlayerEntity() == null ? 0.0F : method5902(var1.getPlayerEntity()),
                  var1.getFloatValue2(),
                  Math.sqrt(method5962(mc.player.getEyePos(), new Box(var1.pos()))),
                  var1.getPlayerEntity() == null ? -1.0 : var1.getPlayerEntity().distanceTo(mc.player),
                  var5 == Hand.OFF_HAND ? "левая" : "правая"
               );
         }

         if (var1.getFloatValue() <= 4.0F) {
            this.intValue18 = var4;
         }

         if (!var1.isFlag() && this.entry13.isEnabled() && var1.getPlayerEntity() != null && mc.player.distanceTo(var1.getPlayerEntity()) <= 3.5F) {
            this.longValue = var1.pos().asLong();
            this.playerEntity2 = var1.getPlayerEntity();
            this.intValue19 = var4;
         }

         return true;
      }
   }

   private boolean method5845(CrystalAuraModule.Data var1, BlockHitResult var2, Item var3, Rotation var4, int var5) {
      Hand var6 = this.method5858(var1x -> var1x.isOf(var3), true);
      if (var6 == null) {
         return false;
      } else {
         boolean var7 = this.method5856(var6, var2, var3, method5958(var1.pos(), 1), var5, true);
         if (var3 == Items.COBWEB) {
            this.intValue33 = var5;
         }

         this.items2.put(var1.pos().asLong(), var5 + this.method5909() * 2);
         this.items3.put(var1.pos().asLong(), var5);
         this.method5857(var4, var5);
         if (var1.getType() == CrystalAuraModule.Type.SHIELD) {
            this.intValue22 = var5;
            if (var7) {
               this.combatLogger.intValue4++;
            }
         }

         if (this.method5925()) {
            this.combatLogger
               .method4612(
                  var1.getType() == CrystalAuraModule.Type.SHIELD ? (var1.isFlag() ? "DEF!" : "DEF") : "BLOCK+",
                  "%s %s на %s%s, до места %.2f б%s",
                  var1.getType(),
                  method5950(var3),
                  method5948(var1.pos()),
                  var7 ? "" : " — клиент отклонил",
                  Math.sqrt(method5962(mc.player.getEyePos(), new Box(var1.pos()))),
                  var1.getType() == CrystalAuraModule.Type.SHIELD
                     ? String.format(Locale.ROOT, ", худший урон был %.1f", var1.getFloatValue2())
                     : String.format(Locale.ROOT, ", под урон %.1f по %s", var1.getFloatValue(), method5947(var1.getPlayerEntity()))
               );
         }

         BlockState var8 = mc.world.getBlockState(var1.pos());
         return var7 && (method5956(var8) || var8.isOf(Blocks.RESPAWN_ANCHOR)) && var1.getType() != CrystalAuraModule.Type.SHIELD;
      }
   }

   private boolean method5846(CrystalAuraModule.Data var1, BlockHitResult var2, int var3) {
      BlockState var4 = mc.world.getBlockState(var1.pos());
      if (var4.isOf(Blocks.RESPAWN_ANCHOR) && var4.get(RespawnAnchorBlock.CHARGES) < 4) {
         Hand var5 = this.method5858(var0 -> var0.isOf(Items.GLOWSTONE), true);
         if (var5 == null) {
            return false;
         } else {
            this.method5856(var5, var2, Items.GLOWSTONE, method5958(var1.pos(), 2), var3, true);
            if (this.method5925()) {
               this.combatLogger.method4612("ANCHOR", "зарядили якорь %s (было зарядов %d)", method5948(var1.pos()), var4.get(RespawnAnchorBlock.CHARGES));
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean method5847(CrystalAuraModule.Data var1, BlockHitResult var2, int var3) {
      if (!mc.player.isSneaking() && !mc.player.getOffHandStack().isOf(Items.GLOWSTONE) && mc.world.getBlockState(var1.pos()).isOf(Blocks.RESPAWN_ANCHOR)) {
         int var4 = this.intValue14 != -1 ? this.intValue14 : mc.player.getInventory().getSelectedSlot();
         Hand var5 = !mc.player.getMainHandStack().isOf(Items.GLOWSTONE)
            ? Hand.MAIN_HAND
            : (
               !mc.player.getInventory().getStack(var4).isOf(Items.GLOWSTONE)
                  ? this.method5859(var4)
                  : this.method5858(var0 -> !var0.isOf(Items.GLOWSTONE), false)
            );
         if (var5 == null) {
            return false;
         } else {
            this.method5856(var5, var2, null, 0L, 0, false);
            if (this.method5925()) {
               this.combatLogger
                  .method4612(
                     "ANCHOR",
                     "взорвали якорь %s: урон %.1f по %s, по нам %.1f, до якоря %.2f б",
                     method5948(var1.pos()),
                     var1.getFloatValue(),
                     method5947(var1.getPlayerEntity()),
                     var1.getFloatValue2(),
                     Math.sqrt(method5962(mc.player.getEyePos(), new Box(var1.pos())))
                  );
            }

            this.data8 = new CrystalAuraModule.Data8(Vec3d.ofCenter(var1.pos()), 5.0F, var1.pos().toImmutable(), var3);
            this.method5843(Vec3d.ofCenter(var1.pos()), 5.0F, var1.pos().toImmutable(), var3);
            this.items5.put(var1.pos().asLong(), var3 + 2);
            if (this.entry12.isEnabled()) {
               this.items15.add(new CrystalAuraModule.Data6(var1.pos().toImmutable(), System.currentTimeMillis(), true));
            }

            this.type2 = CrystalAuraModule.Type2.CRYSTALS;
            this.intValue = var3;
            return true;
         }
      } else {
         return false;
      }
   }

   private boolean method5848(CrystalAuraModule.Data var1, BlockHitResult var2) {
      BlockPos var3 = var1.pos();
      BlockState var4 = mc.world.getBlockState(var3);
      if (var4.isIn(BlockTags.FIRE)) {
         mc.interactionManager.attackBlock(var3, var2.getSide());
         mc.player.swingHand(Hand.MAIN_HAND);
         this.intValue8++;
         return true;
      } else if (var4.isReplaceable()) {
         return false;
      } else {
         int var6 = this.method5912(var4);
         if (!var3.equals(this.blockPos3)) {
            this.method5864();
         }

         if (var6 != -1) {
            this.method5849(var6);
         }

         if (!var3.equals(this.blockPos3)) {
            this.blockPos3 = var3.toImmutable();
            if (this.method5925()) {
               this.combatLogger
                  .method4612(
                     "DIG",
                     "копаем %s (%s) ради урона %.1f по %s, до блока %.2f б",
                     method5948(var3),
                     method5950(var4.getBlock().asItem()),
                     var1.getFloatValue(),
                     method5947(var1.getPlayerEntity()),
                     Math.sqrt(method5962(mc.player.getEyePos(), new Box(var3)))
                  );
            }
         }

         this.intValue20 = mc.player.age;
         if (!mc.interactionManager.isBreakingBlock() || this.intValue32 == (-32768 << 16 | 0)) {
            this.intValue32 = mc.player.age;
         }

         if (mc.interactionManager.updateBlockBreakingProgress(var3, var2.getSide())) {
            mc.player.swingHand(Hand.MAIN_HAND);
         }

         if (mc.interactionManager.isBreakingBlock() && mc.interactionManager.currentBreakingProgress < 1.0F) {
            float var7 = this.method5851(var3, var4, mc.player.getMainHandStack());
            int var8 = mc.player.age - this.intValue32;
            if (var7 > 0.0F && var7 * (var8 + 1) >= 1.0F) {
               Direction var9 = var2.getSide();
               float var10 = mc.interactionManager.currentBreakingProgress;
               mc.interactionManager.sendSequencedPacket(mc.world, var2x -> new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, var3, var9, var2x));
               mc.interactionManager.breakBlock(var3);
               mc.interactionManager.breakingBlock = false;
               mc.interactionManager.currentBreakingProgress = 0.0F;
               if (this.method5925()) {
                  this.combatLogger
                     .method4612(
                        "DIG",
                        "%s докопан по серверному времени: %d т (клиент насчитал бы %.0f%%)",
                        method5948(var3),
                        var8 + 1,
                        Math.min(100.0F, var10 * 100.0F)
                     );
               }

               this.intValue32 = -32768 << 16 | 0;
            }
         }

         this.floatValue = mc.interactionManager.isBreakingBlock() ? MathHelper.clamp(mc.interactionManager.currentBreakingProgress, 0.0F, 1.0F) : 0.0F;
         this.intValue8++;
         return true;
      }
   }

   private void method5849(int var1) {
      int var2 = mc.player.getInventory().getSelectedSlot();
      int var3;
      if ((var3 = this.intValue14 != -1 ? this.intValue14 : var2) != var1) {
         if (this.intValue27 == -1) {
            this.intValue27 = var3;
         }

         if (var2 != var1) {
            mc.player.getInventory().setSelectedSlot(var1);
            mc.interactionManager.syncSelectedSlot();
            this.intValue9++;
         }

         this.intValue31 = var1;
         if (this.intValue14 != -1) {
            this.intValue14 = var1;
         }

         if (this.method5925()) {
            this.combatLogger.method4612("SWAP", "кирку в руку на копание: слот %d -> %d", var3, var1);
         }
      }
   }

   private int method5850(BlockPos var1, BlockState var2) {
      if (!this.method5923(var1) && mc.world.getFluidState(var1).isEmpty() && !var2.isOf(Blocks.RESPAWN_ANCHOR)) {
         float var3;
         if ((var3 = var2.getHardness(mc.world, var1)) < 0.0F) {
            return -1;
         } else if (var3 == 0.0F) {
            return 1;
         } else {
            int var4;
            ItemStack var5 = (var4 = this.method5912(var2)) == -1 ? mc.player.getMainHandStack() : mc.player.getInventory().getStack(var4);
            float var6;
            if ((var6 = this.method5851(var1, var2, var5)) <= 0.0F) {
               return -1;
            } else {
               int var7;
               return (var7 = var6 >= 1.0F ? 1 : (int)Math.ceil(1.0F / var6)) <= this.mineTime.getValue3() * 20.0F ? var7 : -1;
            }
         }
      } else {
         return -1;
      }
   }

   private float method5851(BlockPos var1, BlockState var2, ItemStack var3) {
      float var4;
      if ((var4 = var2.getHardness(mc.world, var1)) < 0.0F) {
         return 0.0F;
      } else if (var4 == 0.0F) {
         return 1.0F;
      } else {
         float var5;
         if ((var5 = var3.getMiningSpeedMultiplier(var2)) > 1.0F) {
            this.method5852();
            int var6;
            if ((var6 = this.registryEntry == null ? 0 : EnchantmentHelper.getLevel(this.registryEntry, var3)) > 0) {
               var5 += var6 * var6 + 1;
            }
         }

         StatusEffectInstance var10 = mc.player.getStatusEffect(StatusEffects.HASTE);
         if (var10 != null) {
            var5 *= 1.0F + (var10.getAmplifier() + 1) * 0.2F;
         }

         StatusEffectInstance var7 = mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE);
         if (var7 != null) {
            var5 *= switch (var7.getAmplifier()) {
               case 0 -> 0.3F;
               case 1 -> 0.09F;
               case 2 -> 0.0027F;
               default -> 8.1E-4F;
            };
         }

         var5 *= (float)mc.player.getAttributeValue(EntityAttributes.BLOCK_BREAK_SPEED);
         if (mc.player.isSubmergedIn(FluidTags.WATER)) {
            var5 *= (float)mc.player.getAttributeValue(EntityAttributes.SUBMERGED_MINING_SPEED);
         }

         if (!mc.player.isOnGround()) {
            var5 /= 5.0F;
         }

         boolean var8 = !var2.isToolRequired() || var3.isSuitableFor(var2);
         return var5 / var4 / (var8 ? 30.0F : 100.0F);
      }
   }

   private void method5852() {
      DynamicRegistryManager var1 = mc.world.getRegistryManager();
      if (var1 != this.obj) {
         Registry<net.minecraft.enchantment.Enchantment> var2 = mc.world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
         this.registryEntry = var2.getOptional(Enchantments.EFFICIENCY).orElse(null);
         this.registryEntry2 = var2.getOptional(Enchantments.KNOCKBACK).orElse(null);
         this.registryEntry3 = var2.getOptional(Enchantments.SHARPNESS).orElse(null);
         this.obj = var1;
      }
   }

   private int method5853() {
      double var1 = this.method5854();
      int var3 = -1;
      double var4 = 0.0;

      for (int var6 = 0; var6 < 9; var6++) {
         ItemStack var7 = mc.player.getInventory().getStack(var6);
         double var8;
         if ((var7.isIn(ItemTags.SWORDS) || var7.isIn(ItemTags.AXES)) && (var8 = var1 + method5855(var7)) > var4) {
            var4 = var8;
            var3 = var6;
         }
      }

      return var3;
   }

   private double method5854() {
      EntityAttributeInstance var1 = mc.player.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
      if (var1 == null) {
         return 1.0;
      } else {
         double var2 = var1.getValue();
         if (var1.hasModifier(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID)) {
            var2 -= method5855(mc.player.getMainHandStack());
         }

         return var2;
      }
   }

   private static double method5855(ItemStack var0) {
      double[] var1 = new double[]{0.0};
      var0.applyAttributeModifiers(EquipmentSlot.MAINHAND, (var1x, var2) -> {
         if (var1x.equals(EntityAttributes.ATTACK_DAMAGE) && var2.operation() == Operation.ADD_VALUE) {
            var1[0] += var2.value();
         }
      });
      return var1[0];
   }

   private boolean method5856(Hand var1, BlockHitResult var2, Item var3, long var4, int var6, boolean var7) {
      int var8 = PlacementQueue.method2811(var1);
      ActionResult var9 = mc.interactionManager.interactBlock(mc.player, var1, var2);
      this.intValue8++;
      boolean var10;
      if ((var10 = var9.isAccepted()) && var3 != null) {
         this.placementQueue.method2812(var4, var8, var3, var6 + this.method5909() + 2);
      }

      if (var9 instanceof Success var12 && var12.swingSource() == SwingSource.CLIENT || var7) {
         mc.player.swingHand(var1);
      }

      return var10;
   }

   private void method5857(Rotation var1, int var2) {
      this.rotation3 = var1;
      this.intValue16 = var2;
      if (this.data3.isFlag4()) {
         ThreadLocalRandom var3 = ThreadLocalRandom.current();
         this.doubleValue2 = var3.nextDouble(-0.08, 0.08);
         this.doubleValue3 = var3.nextDouble(-0.08, 0.08);
      }
   }

   private Hand method5858(Predicate<ItemStack> var1, boolean var2) {
      if (var2 && !this.flag && var1.test(mc.player.getOffHandStack())) {
         return Hand.OFF_HAND;
      } else if (var1.test(mc.player.getMainHandStack())) {
         return Hand.MAIN_HAND;
      } else {
         int var3;
         return (var3 = this.method5916(var1)) == -1 ? null : this.method5859(var3);
      }
   }

   private Hand method5859(int var1) {
      int var2;
      if ((var2 = mc.player.getInventory().getSelectedSlot()) == var1) {
         return Hand.MAIN_HAND;
      } else {
         org.zenith.utility.misc.Data3.Type var3 = this.data3.getType();
         if (var3 != org.zenith.utility.misc.Data3.Type.OFF && (var3 != org.zenith.utility.misc.Data3.Type.KEEP || this.intValue9 < 1)) {
            if (var3 == org.zenith.utility.misc.Data3.Type.KEEP && this.intValue12 == -1) {
               this.intValue12 = var2;
            }

            if (var3 == org.zenith.utility.misc.Data3.Type.SILENT && this.intValue14 == -1) {
               this.intValue14 = var2;
            }

            mc.player.getInventory().setSelectedSlot(var1);
            mc.interactionManager.syncSelectedSlot();
            this.intValue9++;
            this.intValue13 = var1;
            if (this.method5925()) {
               this.combatLogger
                  .method4612("SWAP", "слот %d -> %d (%s), режим %s", var2, var1, method5950(mc.player.getInventory().getStack(var1).getItem()), var3);
            }

            return Hand.MAIN_HAND;
         } else {
            return null;
         }
      }
   }

   private void method5860() {
      if (this.intValue14 != -1 && mc.player != null) {
         int var1 = this.intValue14;
         this.intValue14 = -1;
         if (mc.player.getInventory().getSelectedSlot() != var1) {
            mc.player.getInventory().setSelectedSlot(var1);
            mc.interactionManager.syncSelectedSlot();
         }
      }
   }

   private void method5861() {
      if (this.intValue12 != -1 && mc.player != null) {
         int var1 = this.intValue12;
         this.intValue12 = -1;
         if (mc.player.getInventory().getSelectedSlot() == this.intValue13 && this.intValue13 != var1) {
            mc.player.getInventory().setSelectedSlot(var1);
            mc.interactionManager.syncSelectedSlot();
            this.intValue9++;
         }

         this.intValue13 = -1;
      }
   }

   private void method5862() {
      this.intValue10++;
      if (this.intValue12 != -1 && this.intValue10 >= 10 && this.intValue9 == 0) {
         this.method5861();
      }
   }

   private void method5863() {
      this.method5864();
      int var1 = mc.player == null ? -1 : (this.intValue14 != -1 ? this.intValue14 : mc.player.getInventory().getSelectedSlot());
      if (this.intValue27 != -1 && mc.player != null && mc.interactionManager != null && var1 == this.intValue31) {
         if (this.intValue14 != -1) {
            this.intValue14 = this.intValue27;
         } else if (mc.player.getInventory().getSelectedSlot() != this.intValue27) {
            mc.player.getInventory().setSelectedSlot(this.intValue27);
            mc.interactionManager.syncSelectedSlot();
         }
      }

      this.intValue27 = -1;
   }

   private void method5864() {
      this.intValue32 = -32768 << 16 | 0;
      if (this.blockPos3 != null && mc.interactionManager != null) {
         mc.interactionManager.cancelBlockBreaking();
      }

      this.blockPos3 = null;
      this.floatValue = 0.0F;
   }

   private boolean method5865(BlockPos var1, Entity var2) {
      if (!mc.world.isAir(var1.up())) {
         return false;
      } else {
         Box var3 = method5960(var1);
         var var4 = this.items.keySet().iterator();

         while (var4.hasNext()) {
            long var5;
            if ((var5 = (Long)var4.next()) != var1.asLong() && method5961(BlockPos.fromLong(var5)).intersects(var3)) {
               return false;
            }
         }

         return this.method5866(var3, var2);
      }
   }

   private boolean method5866(Box var1, Entity var2) {
      int var3 = mc.player.age;
      Vec3d var4 = mc.player.getEyePos();
      double var5 = this.doubleValue - 0.15;
      var var10000 = mc.world.getOtherEntities(null, var1, var6 -> {
         if (!var6.isSpectator() && var6 != var2 && !this.method5868(var6)) {
            if (!(var6 instanceof EndCrystalEntity)) {
               return true;
            }

            boolean var7;
            label32: {
               var var10000x = this.items6;
               Integer var10001 = var6.getId();
               if (var10000x != null) {
                  if (var10000x.isEmpty()) {
                     var7 = false;
                     break label32;
                  }
               }

               var7 = var10000x.containsKey(var10001);
            }

            if (!var7 || this.items6.get(var6.getId()) - this.method5909() < var3 || !(method5962(var4, var6.getBoundingBox()) <= var5 * var5)) {
               return true;
            }
         }

         return false;
      });
      if (!var10000.isEmpty()) {
         return false;
      } else {
         Box var8 = var1.expand(0.1, 0.0, 0.1);

         for (CrystalAuraModule.Data5 var10 : this.items18) {
            if (var10.getBox().intersects(var8) || var10.getBox2().intersects(var8) || this.method5903(var10.getPlayerEntity()).intersects(var1)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean method5867(BlockPos var1) {
      if (mc.world.isInBuildLimit(var1) && mc.world.getWorldBorder().contains(var1)) {
         boolean var10;
         label61: {
            var var10000 = this.items2;
            Long var10001 = var1.asLong();
            if (var10000 != null) {
               if (var10000.isEmpty()) {
                  var10 = false;
                  break label61;
               }
            }

            var10 = var10000.containsKey(var10001);
         }

         if (!var10) {
            if (mc.world.getBlockState(var1).isReplaceable() && mc.world.getFluidState(var1).isEmpty()) {
               Box var2 = new Box(var1);
               var var11 = mc.world
                  .getOtherEntities(null, var2, var1x -> var1x.intersectionChecked && !var1x.isRemoved() && !var1x.isSpectator() && !this.method5868(var1x));
               if (!var11.isEmpty()) {
                  return false;
               }

               for (CrystalAuraModule.Data5 var5 : this.items18) {
                  if (var5.getBox().intersects(var2)) {
                     return false;
                  }
               }

               for (Direction var7 : Direction.values()) {
                  if (!mc.world.getBlockState(var1.offset(var7)).isReplaceable()) {
                     return true;
                  }
               }

               return false;
            }

            return false;
         }
      }

      return false;
   }

   private boolean method5868(Entity var1) {
      for (CrystalAuraModule.Data5 var3 : this.items18) {
         if (var3.getPlayerEntity() == var1) {
            return true;
         }
      }

      return false;
   }

   private boolean method5869() {
      return AutoTotemModule.INSTANCE.isEnabled() && AutoTotemModule.INSTANCE.method704();
   }

   private boolean method5870() {
      return AutoTotemModule.INSTANCE.isEnabled() && AutoTotemModule.INSTANCE.method705();
   }

   private boolean method5871(float var1) {
      if (!(var1 <= 0.0F)
         && AutoTotemModule.INSTANCE.isEnabled()
         && !mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)
         && !AutoTotemModule.INSTANCE.method706()) {
         float var2 = mc.player.getHealth() + mc.player.getAbsorptionAmount();
         float var3 = Math.max(AutoTotemModule.INSTANCE.getHealthSetting().getValue3(), AutoTotemModule.INSTANCE.getCrystalMargin().getValue3());
         return var2 - var1 > var3 || !AutoTotemModule.INSTANCE.method703();
      } else {
         return true;
      }
   }

   private boolean method5872() {
      if (this.entry11.isEnabled() && AutoTrapModule.INSTANCE.isEnabled() && AutoTrapModule.INSTANCE.method2427()) {
         this.intValue11++;
         return this.intValue11 % 3 != 0;
      } else {
         this.intValue11 = 0;
         return false;
      }
   }

   private boolean method5873() {
      return this.entry3.isEnabled() && mc.world.getRegistryKey() != World.NETHER && !mc.player.isSneaking();
   }

   private boolean method5874(Vec3d var1, Vec3d var2) {
      BlockHitResult var3 = mc.world.raycast(new RaycastContext(var1, var2, ShapeType.OUTLINE, FluidHandling.NONE, mc.player));
      return var3.getType() == HitResult.Type.BLOCK && var1.squaredDistanceTo(var3.getPos()) + 1.0E-4 < var1.squaredDistanceTo(var2);
   }

   private BlockHitResult method5875(Vec3d var1, Rotation var2, double var3) {
      Vec3d var5 = var1.add(var2.method3135().multiply(var3));
      return mc.world.raycast(new RaycastContext(var1, var5, ShapeType.OUTLINE, FluidHandling.NONE, mc.player));
   }

   private Rotation method5876(Rotation var1) {
      float var2;
      if ((var2 = this.data3.getFloatValue3()) >= 180.0F) {
         return var1;
      } else {
         Rotation var3 = rotationManager.method7844();
         float var4 = MathHelper.clamp(MathHelper.wrapDegrees(var1.getFloatValue() - var3.getFloatValue()), -var2, var2);
         float var5 = MathHelper.clamp(var1.getFloatValue2() - var3.getFloatValue2(), -var2, var2);
         return new Rotation(var3.getFloatValue() + var4, MathHelper.clamp(var3.getFloatValue2() + var5, -90.0F, 90.0F));
      }
   }

   private double method5877() {
      double var1 = mc.player == null
         ? this.data3.getFloatValue2()
         : mc.player.getAttributeValue(EntityAttributes.ENTITY_INTERACTION_RANGE) + (this.data3.isFlag6() ? 0.0 : 3.0);
      return Math.max(1.0, Math.min(Math.min((double)this.data3.getFloatValue2(), var1), this.doubleValue) - 0.03F);
   }

   private double method5878() {
      double var1 = mc.player == null
         ? this.data3.getFloatValue()
         : mc.player.getAttributeValue(EntityAttributes.BLOCK_INTERACTION_RANGE) + (this.data3.isFlag6() ? 0.0 : 1.0);
      return Math.max(1.0, Math.min((double)this.data3.getFloatValue(), var1) - 0.03F);
   }

   private Vec3d method5879(PlayerEntity var1, Vec3d var2) {
      if (!var1.isOnGround()) {
         return null;
      } else {
         double var3;
         if ((var3 = 1.0 - method5881(var1)) <= 0.0) {
            return null;
         } else {
            double[] var5 = new double[]{var2.x, -0.0784, var2.z};
            method5880(var5, 0.4 * var3, mc.player.getX() - var1.getX(), mc.player.getZ() - var1.getZ());
            float var6 = this.method5882(mc.player.getMainHandStack()) * 0.5F;
            if (mc.player.isSprinting() && mc.player.getAttackCooldownProgress(0.5F) > 0.9F) {
               var6 += 0.5F;
            }

            if (var6 > 0.0F) {
               float var7 = this.method5897().getFloatValue() * (float) (Math.PI / 180.0);
               method5880(var5, var6 * var3, MathHelper.sin(var7), -MathHelper.cos(var7));
            }

            return new Vec3d(var5[0], var5[1], var5[2]);
         }
      }
   }

   private static void method5880(double[] var0, double var1, double var3, double var5) {
      double var7;
      if ((var7 = Math.sqrt(var3 * var3 + var5 * var5)) < 1.0E-5) {
         var3 = 0.0;
         var5 = 0.0;
      } else {
         var3 /= var7;
         var5 /= var7;
      }

      var0[0] = var0[0] / 2.0 - var3 * var1;
      var0[1] = Math.min(0.4, var0[1] / 2.0 + var1);
      var0[2] = var0[2] / 2.0 - var5 * var1;
   }

   private static double method5881(PlayerEntity var0) {
      double var1 = 0.0;

      for (EquipmentSlot var6 : equipmentSlots) {
         ItemStack var7 = var0.getEquippedStack(var6);
         if (var7.isOf(Items.NETHERITE_HELMET)
            || var7.isOf(Items.NETHERITE_CHESTPLATE)
            || var7.isOf(Items.NETHERITE_LEGGINGS)
            || var7.isOf(Items.NETHERITE_BOOTS)) {
            var1 += 0.1;
         }
      }

      return var1;
   }

   private float method5882(ItemStack var1) {
      this.method5852();
      return this.registryEntry2 == null ? 0.0F : EnchantmentHelper.getLevel(this.registryEntry2, var1);
   }

   private int method5883(PlayerEntity var1) {
      if (mc.getNetworkHandler() == null) {
         return this.intValue15;
      } else {
         PlayerListEntry var2 = mc.getNetworkHandler().getPlayerListEntry(var1.getUuid());
         return var2 == null ? this.intValue15 : Math.round(Math.max(0, var2.getLatency()) / 50.0F);
      }
   }

   private Box[] method5884(PlayerEntity var1, Box var2, Vec3d var3, int var4) {
      Box[] var5 = new Box[var4 + 1];
      var5[0] = var2;
      Box var6 = var2;
      double var7 = var3.x;
      double var9 = var3.y;
      double var11 = var3.z;
      boolean var13 = false;

      for (int var14 = 1; var14 <= var4; var14++) {
         if (!var13) {
            Vec3d var15 = Entity.adjustMovementForCollisions(var1, new Vec3d(var7, var9, var11), var6, mc.world, List.of());
            var13 = var9 < 0.0 && var15.y > var9 + 1.0E-4;
            if (Math.abs(var15.x - var7) > 1.0E-4) {
               var7 = 0.0;
            }

            if (Math.abs(var15.z - var11) > 1.0E-4) {
               var11 = 0.0;
            }

            var6 = var6.offset(var15);
            var9 = (var9 - 0.08) * 0.98;
            var7 *= 0.91;
            var11 *= 0.91;
         }

         var5[var14] = var6;
      }

      return var5;
   }

   private void method5885() {
      this.playerEntity3 = null;
      this.rotation5 = null;
      this.floatValue5 = 0.0F;
      this.endCrystalEntity = null;
      if (this.entry13.isEnabled()
         && !AuraModule.INSTANCE.isEnabled()
         && (this.data4 == null || this.data4.getType() != CrystalAuraModule.Type.SHIELD || !this.data4.isFlag())) {
         int var1;
         if ((var1 = this.method5891() ? -2 : this.method5853()) != -1) {
            this.method5889();
            if (this.playerEntity3 == null) {
               this.method5888();
            }

            if (this.playerEntity3 != null) {
               this.intValue30 = mc.player.age;
               if (var1 != -2) {
                  this.method5887(var1);
               }

               if (var1 == -2 && !(mc.player.getAttackCooldownProgress(0.5F) < 0.9F)) {
                  if (this.floatValue5 > 0.0F) {
                     EndCrystalEntity var2 = this.endCrystalEntity;
                     this.items16.removeIf(var1x -> var1x.getEndCrystalEntity() == var2);
                     this.data = (this.items16 != null && this.items16.getClass() == EmptyList.class ? true : this.items16.isEmpty())
                        ? null
                        : this.items16.getFirst();
                     if (this.method5925()) {
                        this.combatLogger
                           .method4612("COMBO", "план удара мечом по %s: кристалл на пике подлёта даст +%.1f", method5947(this.playerEntity3), this.floatValue5);
                     }
                  }
               } else {
                  this.playerEntity3 = null;
                  this.rotation5 = null;
                  this.endCrystalEntity = null;
               }
            }
         }
      }
   }

   private void method5886(boolean var1) {
      if (this.intValue28 != -1 && mc.player != null && mc.interactionManager != null && this.blockPos3 == null && this.intValue14 == -1) {
         if (var1 || this.method5896(this.intValue30) > 60) {
            if (mc.player.getInventory().getSelectedSlot() == this.intValue29 && this.intValue28 != this.intValue29) {
               mc.player.getInventory().setSelectedSlot(this.intValue28);
               mc.interactionManager.syncSelectedSlot();
               if (this.method5925()) {
                  this.combatLogger.method4612("SWAP", "меч больше не нужен — возвращаем слот %d", this.intValue28);
               }
            }

            this.intValue28 = -1;
            this.intValue29 = -1;
         }
      }
   }

   private void method5887(int var1) {
      if (var1 >= 0 && mc.player.getInventory().getSelectedSlot() != var1 && this.intValue14 == -1 && this.blockPos3 == null) {
         if (this.intValue28 == -1) {
            this.intValue28 = mc.player.getInventory().getSelectedSlot();
         }

         mc.player.getInventory().setSelectedSlot(var1);
         mc.interactionManager.syncSelectedSlot();
         this.intValue29 = var1;
         if (this.method5925()) {
            this.combatLogger
               .method4612(
                  "SWORD", "берём %s в руку (слот %d) — ударим, когда наберётся кулдаун", method5950(mc.player.getInventory().getStack(var1).getItem()), var1
               );
         }
      }
   }

   private void method5888() {
      boolean var1 = this.data != null
         || this.data4 != null && this.data4.isFlag()
         || this.data2 != null
         || (this.items != null && this.items.getClass() == EmptyMap.class ? false : !this.items.isEmpty());
      double var2 = this.method5877();
      float var4 = this.method5893(1.0F);
      int var5 = mc.player.age + this.method5818();

      for (CrystalAuraModule.Data5 var7 : this.items18) {
         PlayerEntity var8 = var7.getPlayerEntity();
         Box var9 = this.method5903(var8);
         if (!(method5962(this.vec3d, var9) > var2 * var2)) {
            CrystalAuraModule.Entry var10 = this.items14.get(var8.getId());
            boolean var11 = this.class35.method5457(var8, var4) >= var7.getFloatValue() && !var7.isFlag();
            if (!var1 || var11) {
               float var12 = var7.getIntValue() > 10 ? var7.getFloatValue2() : 0.0F;
               if (var10 != null && var10.method5969(var5)) {
                  var12 = Math.max(var12, var10.floatValue8);
               }

               if (!(var4 <= var12)) {
                  Vec3d var13 = new Vec3d(
                     MathHelper.clamp(this.vec3d.x, var9.minX + 0.05, var9.maxX - 0.05),
                     MathHelper.clamp(this.vec3d.y, var9.minY + 0.2, var9.maxY - 0.2),
                     MathHelper.clamp(this.vec3d.z, var9.minZ + 0.05, var9.maxZ - 0.05)
                  );
                  this.playerEntity3 = var8;
                  this.rotation5 = method5898(this.vec3d, var13);
                  this.floatValue5 = 0.0F;
                  return;
               }
            }
         }
      }
   }

   private void method5889() {
      double var1 = this.method5877();

      for (CrystalAuraModule.Data5 var4 : this.items18) {
         PlayerEntity var5 = var4.getPlayerEntity();
         CrystalAuraModule.Entry var6 = this.items14.get(var5.getId());
         Box var7 = this.method5903(var5);
         if (var6 != null && var5.isOnGround() && !var6.method5966(mc.player.age) && var4.getIntValue() <= 10 && !(method5962(this.vec3d, var7) > var1 * var1)) {
            Vec3d var8 = this.method5879(var5, var6.method5971());
            if (var8 != null && !(var8.y < 0.15)) {
               Box[] var9 = this.method5884(var5, var7, var8, 14);
               float var10 = this.method5892();

               for (EndCrystalEntity var12 : mc.world.getEntitiesByClass(EndCrystalEntity.class, var7.expand(6.0), var1x -> {
                  if (var1x.isAlive()) {
                     boolean var2;
                     label20: {
                        var var10000 = this.items6;
                        Integer var10001 = var1x.getId();
                        if (var10000 != null) {
                           if (var10000.isEmpty()) {
                              var2 = false;
                              break label20;
                           }
                        }

                        var2 = var10000.containsKey(var10001);
                     }

                     if (!var2) {
                        return true;
                     }
                  }

                  return false;
               })) {
                  if (!(method5962(this.vec3d, var12.getBoundingBox()) > var1 * var1)) {
                     Vec3d var13 = var12.getEntityPos();
                     float var14 = this.class35.method5452(var13, 6.0F, var5, var7, null, null);
                     float var15 = 0.0F;

                     for (int var16 = 2; var16 < var9.length; var16++) {
                        float var17 = this.class35.method5454(var13, 6.0F, var5, var9[var16], null, null) - (var16 < 10 ? var10 : 0.0F);
                        var15 = Math.max(var15, var17 <= 0.0F ? 0.0F : this.class35.method5456(var5, var17));
                     }

                     float var18 = var15 - var14;
                     if (var15 >= this.minDamage.getValue3() && var18 >= 4.0F && var18 > this.floatValue5) {
                        Vec3d var19 = new Vec3d(
                           MathHelper.clamp(this.vec3d.x, var7.minX + 0.05, var7.maxX - 0.05),
                           MathHelper.clamp(this.vec3d.y, var7.minY + 0.2, var7.maxY - 0.2),
                           MathHelper.clamp(this.vec3d.z, var7.minZ + 0.05, var7.maxZ - 0.05)
                        );
                        this.playerEntity3 = var5;
                        this.rotation5 = method5898(this.vec3d, var19);
                        this.floatValue5 = var18;
                        this.endCrystalEntity = var12;
                     }
                  }
               }
            }
         }
      }
   }

   private void method5890(Vec3d var1, Rotation var2) {
      PlayerEntity var3 = this.playerEntity3;
      if (var3 != null && this.method5919() && var3.isAlive() && !(mc.player.getAttackCooldownProgress(0.5F) < 0.9F)) {
         if ((this.items16 != null && this.items16.getClass() == EmptyList.class ? true : this.items16.isEmpty()) && this.method5891()) {
            Box var4 = var3.getBoundingBox().expand(var3.getTargetingMargin());
            double var5 = this.method5877();
            if (!var4.raycast(var1, var1.add(var2.method3135().multiply(var5))).isEmpty() && (this.data3.isFlag5() || !this.method5874(var1, var4.getCenter()))
               )
             {
               float var7 = this.method5892();
               boolean var8 = this.floatValue5 > 0.0F;
               mc.interactionManager.attackEntity(mc.player, var3);
               mc.player.swingHand(Hand.MAIN_HAND);
               this.intValue8++;
               if (this.method5925()) {
                  this.combatLogger
                     .method4612(
                        var8 ? "COMBO" : "SWORD",
                        "удар мечом по %s в %.2f б, raw %.1f, спринт %s%s",
                        method5947(var3),
                        Math.sqrt(method5962(var1, var4)),
                        var7,
                        mc.player.isSprinting() ? "да" : "нет",
                        var8 ? " — ждём подлёт под кристалл" : " — кристаллам сейчас нечего делать"
                     );
               }

               this.playerEntity3 = null;
               this.rotation5 = null;
               this.method5860();
            }
         }
      }
   }

   private boolean method5891() {
      ItemStack var1 = mc.player.getMainHandStack();
      return var1.isIn(ItemTags.SWORDS) || var1.isIn(ItemTags.AXES);
   }

   private float method5892() {
      return this.method5893(mc.player.getAttackCooldownProgress(0.5F));
   }

   private float method5893(float var1) {
      ItemStack var2 = mc.player.getMainHandStack();
      int var3;
      if (!this.method5891() && (var3 = this.method5853()) != -1) {
         var2 = mc.player.getInventory().getStack(var3);
      }

      double var9 = this.method5854() + method5855(var2);
      this.method5852();
      int var5 = this.registryEntry3 == null ? 0 : EnchantmentHelper.getLevel(this.registryEntry3, var2);
      double var6 = var9 * (0.2 + var1 * var1 * 0.8);
      if (var1 > 0.9F
         && mc.player.fallDistance > 0.0
         && !mc.player.isOnGround()
         && !mc.player.isClimbing()
         && !mc.player.isTouchingWater()
         && !mc.player.isSprinting()) {
         var6 *= 1.5;
      }

      var6 += (var5 > 0 ? 0.5 * var5 + 0.5 : 0.0) * var1;
      return (float)Math.max(0.0, var6);
   }

   private boolean method5894(CrystalAuraModule.Data12 var1, Vec3d var2) {
      if (this.entry13.isEnabled() && var1.getPlayerEntity() != null) {
         for (CrystalAuraModule.Data5 var4 : this.items18) {
            Box[] var5 = var4.getBoxs();
            if (var4.getPlayerEntity() == var1.getPlayerEntity() && var5 != null) {
               int var6;
               if ((var6 = var4.getIntValue3()) < 0 || var6 < var5.length && !(var5[var6].minY <= var5[0].minY + 0.1)) {
                  if (var1.getFloatValue() >= var4.getFloatValue()) {
                     return false;
                  }

                  float var7 = 0.0F;

                  for (int var8 = 1; var8 <= 12 && var4.getIntValue3() + var8 < var5.length; var8++) {
                     float var9 = this.method5823(var4, var2, 6.0F, var5[Math.max(0, var4.getIntValue3() + var8)], null, null, this.method5818() + var8)
                        * (1.0F - 0.02F * var8);
                     var7 = Math.max(var7, var9);
                  }

                  return var7 > var1.getFloatValue() * 1.15F + 1.5F;
               }

               return false;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean method5895(CrystalAuraModule.Data var1, int var2) {
      return this.entry18.isEnabled()
            && !(var1.getFloatValue() > 4.0F)
            && var1.getPlayerEntity() != null
            && !(var1.getFloatValue() >= method5902(var1.getPlayerEntity()))
            && !(this.floatValue2 >= this.shieldDamage.getValue3())
         ? this.method5896(this.intValue18) >= 10
         : true;
   }

   private int method5896(int var1) {
      return var1 != (-32768 << 16 | 0) && mc.player != null ? mc.player.age - var1 : 32767 << 16 | (char)-1;
   }

   private Rotation method5897() {
      Rotation var1 = rotationManager.getRotation2();
      return var1 == null ? new Rotation(mc.player.getYaw(), mc.player.getPitch()) : var1;
   }

   private static Rotation method5898(Vec3d var0, Vec3d var1) {
      return RotationUtils.method6912(var1.subtract(var0));
   }

   private static boolean method5899(Rotation var0, Rotation var1) {
      return var0 != null
         && var1 != null
         && Math.abs(MathHelper.wrapDegrees(var0.getFloatValue() - var1.getFloatValue())) < 0.001F
         && Math.abs(var0.getFloatValue2() - var1.getFloatValue2()) < 0.001F;
   }

   private List<CrystalAuraModule.Data5> method5900(Vec3d var1, int var2) {
      double var3 = this.targetRange.getValue3();
      ArrayList<PlayerEntity> var5 = new ArrayList<>();

      for (PlayerEntity var7 : mc.world.getPlayers()) {
         if (var7 != mc.player
            && method5901(var7)
            && !Zenith.getInstance().getFriends().method7626(var7)
            && !AntiBotModule.INSTANCE.method2785(var7)
            && method5962(var1, this.method5903(var7)) <= var3 * var3) {
            var5.add(var7);
         }
      }

      var5.sort(Comparator.comparingDouble((PlayerEntity var2x) -> method5962(var1, this.method5903(var2x))));
      this.items14.keySet().removeIf(var1x -> var5.stream().noneMatch((PlayerEntity var1xx) -> var1xx.getId() == var1x));
      int var27 = Math.max(1, (this.intValue15 + 1) / 2);
      int var28 = this.predictTicks.getValue3() <= 0.0F ? 0 : MathHelper.clamp(Math.round(this.predictTicks.getValue3()) + this.intValue15 + var27, 0, 12);
      List<Data5> var8 = new ArrayList<>(Math.min(var5.size(), 3));

      for (int var9 = 0; var9 < Math.min(var5.size(), 3); var9++) {
         PlayerEntity var10 = (PlayerEntity)var5.get(var9);
         CrystalAuraModule.Entry var11 = this.items14.computeIfAbsent(var10.getId(), var0 -> new CrystalAuraModule.Entry());
         Vec3d var12 = var10.getTrackedPosition().getPos();
         var11.method5970(var10, var12, var2, this.data8, this.class35, this.items23.remove(var10.getId()), this.method5818());
         if (var11.floatValue5 > 0.0F && this.method5925()) {
            this.combatLogger.floatValue2 = this.combatLogger.floatValue2 + var11.floatValue5;
            this.combatLogger.method4612("THP", "%s hp -%.1f -> %.1f", method5947(var10), var11.floatValue5, method5902(var10));
         }

         Box var13 = this.method5903(var10);
         Vec3d var14 = var11.method5971();
         boolean var15 = var10.isOnGround();
         Box var19 = null;
         Box[] var20 = null;
         int var21 = 0;
         int var22 = 0;
         if (this.entry13.isEnabled() && var11.method5966(var2)) {
            var22 = (var21 = var2 - var11.intValue5 - var11.intValue6) + this.intValue15 + 1;
            var20 = this.method5884(var10, var11.box, var11.vec3d, Math.max(var22, 0) + 12);
            int var23;
            if ((var23 = var21 - this.intValue15) >= 1 && var23 < var20.length) {
               Box var24 = var20[var23];
               Vec3d var25 = var13.getCenter();
               if (Math.abs(var13.minY - var24.minY) > 0.45 || var25.subtract(var24.getCenter()).horizontalLengthSquared() > 0.64) {
                  var11.box = null;
                  var20 = null;
                  if (this.method5925()) {
                     this.combatLogger
                        .method4612(
                           "KNOCK",
                           "%s: подлёт не подтвердился (ждали y+%.2f, видим y+%.2f) — прогноз сброшен",
                           method5947(var10),
                           var24.minY - var11.doubleValue,
                           var13.minY - var11.doubleValue
                        );
                  }
               }
            }
         }

         Box var16;
         Box var17;
         Box var18;
         if (var20 != null) {
            var16 = var20[MathHelper.clamp(var21, 0, var20.length - 1)];
            var17 = var20[MathHelper.clamp(var22, 0, var20.length - 1)];
            var18 = var17;

            for (int var29 = Math.max(var22, 0); var29 < var20.length; var29++) {
               if (var20[var29].minY > var18.minY) {
                  var18 = var20[var29];
               }
            }

            if (var21 >= 0 && var21 + 2 < var20.length && var20[var21 + 2].minY > var16.minY + 0.1) {
               var19 = var20[var21 + 2];
            }
         } else {
            var16 = var10.isGliding() ? var13.offset(var14.multiply(var27)) : this.method5906(var10, var13, var14, var15, var27);
            var17 = var10.isGliding() ? var13.offset(var14.multiply(var28)) : this.method5906(var10, var13, var14, var15, var28);
            var18 = var15 && this.method5945(var13)
               ? var16
               : (var15 ? this.method5906(var10, var16, new Vec3d(var14.x, 0.42, var14.z), false, 6) : this.method5907(var10, var16, var14));
         }

         float var30 = method5902(var10);
         boolean var31 = var10.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) || var10.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING);
         int var32 = var10.timeUntilRegen;
         int var26 = var11.intValue3 == (-32768 << 16 | 0) ? 32767 << 16 | (char)-1 : var2 - var11.intValue3;
         var8.add(
            new CrystalAuraModule.Data5(
               var10,
               var12,
               var16,
               var17,
               var18,
               var30,
               var31,
               this.method5904(var10),
               var32,
               var11.floatValue3,
               var19,
               var26,
               var11.intValue4 > var2,
               var20,
               var21
            )
         );
      }

      return var8;
   }

   public static boolean method5901(PlayerEntity var0) {
      return !var0.isRemoved() && !var0.isSpectator() && (var0.getHealth() > 0.0F || var0.deathTime == 0);
   }

   public static float method5902(PlayerEntity var0) {
      float var1;
      if ((var1 = var0.getHealth()) <= 0.0F || Float.isNaN(var1)) {
         var1 = var0.getMaxHealth();
      }

      return var1 + var0.getAbsorptionAmount();
   }

   private Box method5903(PlayerEntity var1) {
      Vec3d var2 = var1.getTrackedPosition().getPos();
      Vec3d var3 = var2.subtract(var1.getEntityPos());
      return var3.lengthSquared() > 64.0 ? var1.getBoundingBox() : var1.getBoundingBox().offset(var3);
   }

   private boolean method5904(PlayerEntity var1) {
      float var2;
      if ((var2 = this.armorBreak.getValue3() / 100.0F) <= 0.0F) {
         return false;
      } else {
         for (EquipmentSlot var6 : equipmentSlots) {
            ItemStack var7 = var1.getEquippedStack(var6);
            if (var7.isDamageable() && var7.getMaxDamage() > 0 && (float)(var7.getMaxDamage() - var7.getDamage()) / var7.getMaxDamage() <= var2) {
               return true;
            }
         }

         return false;
      }
   }

   private List<PlayerEntity> method5905(Vec3d var1) {
      double var2 = this.targetRange.getValue3() + 6.0;
      ArrayList<PlayerEntity> var4 = new ArrayList<>();

      for (PlayerEntity var6 : mc.world.getPlayers()) {
         if (var6 != mc.player
            && method5901(var6)
            && Zenith.getInstance().getFriends().method7626(var6)
            && method5962(var1, var6.getBoundingBox()) <= var2 * var2) {
            var4.add(var6);
         }
      }

      return var4;
   }

   private Box method5906(PlayerEntity var1, Box var2, Vec3d var3, boolean var4, int var5) {
      Box var6 = var2;
      double var7 = var3.x;
      double var9 = var4 && var3.y <= 0.0 ? -0.0784 : var3.y;
      double var11 = var3.z;
      if (Math.hypot(var7, var11) < 0.03) {
         var7 = 0.0;
         var11 = 0.0;
      }

      for (int var13 = 0; var13 < var5; var13++) {
         Vec3d var14 = Entity.adjustMovementForCollisions(var1, new Vec3d(var7, var9, var11), var6, mc.world, List.of());
         boolean var15 = var9 < 0.0 && var14.y > var9 + 1.0E-4;
         boolean var16 = var9 > 0.0 && var14.y < var9 - 1.0E-4;
         if (Math.abs(var14.x - var7) > 1.0E-4) {
            var7 = 0.0;
         }

         if (Math.abs(var14.z - var11) > 1.0E-4) {
            var11 = 0.0;
         }

         var6 = var6.offset(var14);
         var9 = var15 ? -0.0784 : ((var16 ? 0.0 : var9) - 0.08) * 0.98;
      }

      return var6;
   }

   private Box method5907(PlayerEntity var1, Box var2, Vec3d var3) {
      Box var4 = var2;
      double var5 = var3.x;
      double var7 = var3.y;
      double var9 = var3.z;

      for (int var11 = 0; var11 < 30; var11++) {
         Vec3d var12 = Entity.adjustMovementForCollisions(var1, new Vec3d(var5, var7, var9), var4, mc.world, List.of());
         var4 = var4.offset(var12);
         if (var7 < 0.0 && var12.y > var7 + 1.0E-4) {
            return var4;
         }

         if (Math.abs(var12.x - var5) > 1.0E-4) {
            var5 = 0.0;
         }

         if (Math.abs(var12.z - var9) > 1.0E-4) {
            var9 = 0.0;
         }

         var7 = (var7 - 0.08) * 0.98;
      }

      return var4;
   }

   private boolean method5908(BlockPos var1) {
      Integer var2 = this.items3.get(var1.asLong());
      return var2 != null && mc.player != null && var2 >= mc.player.age;
   }

   private int method5909() {
      return MathHelper.clamp(this.intValue15 + 3, 3, 20);
   }

   private int method5910() {
      if (mc.getNetworkHandler() == null) {
         return 0;
      } else {
         PlayerListEntry var1 = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
         return var1 == null ? 0 : Math.max(0, var1.getLatency());
      }
   }

   private boolean method5911() {
      return mc.player.getAttributeValue(EntityAttributes.ATTACK_DAMAGE) <= 0.0;
   }

   private int method5912(BlockState var1) {
      int var2 = -1;
      float var3 = 1.0F;

      for (int var4 = 0; var4 < 9; var4++) {
         float var5;
         if ((var5 = mc.player.getInventory().getStack(var4).getMiningSpeedMultiplier(var1)) > var3) {
            var3 = var5;
            var2 = var4;
         }
      }

      return var2;
   }

   private void method5913() {
      if (this.entry6.isEnabled() && !this.class33.pending() && this.data3.getType() != org.zenith.utility.misc.Data3.Type.OFF) {
         this.method5914(var0 -> var0.isOf(Items.END_CRYSTAL), true);
         this.method5914(var0 -> var0.isOf(Items.OBSIDIAN), this.entry4.isEnabled() || this.entry7.isEnabled());
         this.method5914(var0 -> var0.isOf(Items.RESPAWN_ANCHOR), this.method5873());
         this.method5914(var0 -> var0.isOf(Items.GLOWSTONE), this.method5873());
      }
   }

   private void method5914(Predicate<ItemStack> var1, boolean var2) {
      if (var2 && !this.class33.pending() && !this.method5915(var1) && this.class33.method5302(var1)) {
         this.class33.method5303(var1);
      }
   }

   private boolean method5915(Predicate<ItemStack> var1) {
      return !var1.test(mc.player.getOffHandStack()) && !var1.test(mc.player.getMainHandStack())
         ? this.data3.getType() != org.zenith.utility.misc.Data3.Type.OFF && this.method5916(var1) != -1
         : true;
   }

   private int method5916(Predicate<ItemStack> var1) {
      for (int var2 = 0; var2 < 9; var2++) {
         if (var1.test(mc.player.getInventory().getStack(var2))) {
            return var2;
         }
      }

      return -1;
   }

   private boolean method5917() {
      return this.method5918() == null;
   }

   private String method5918() {
      if (mc.player.isSpectator() || mc.player.isDead() || mc.player.hasVehicle()) {
         return "наблюдатель / мёртв / в транспорте";
      } else if (mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
         return "открыт контейнер";
      } else {
         if (this.entry10.isEnabled() && mc.player.isUsingItem()) {
            UseAction var1 = mc.player.getActiveItem().getUseAction();
            if (var1 == UseAction.EAT || var1 == UseAction.DRINK) {
               return "едим/пьём (пауза при использовании)";
            }
         }

         return FreeCamModule.INSTANCE.isEnabled() ? "FreeCam" : null;
      }
   }

   private boolean method5919() {
      return this.intValue8 < this.data3.getIntValue();
   }

   private boolean method5920() {
      return this.method5922(this.intValue17, this.data3.getIntValue3());
   }

   private boolean method5921() {
      return this.method5922(this.intValue16, this.data3.getIntValue2());
   }

   private boolean method5922(int var1, int var2) {
      return var2 > 0 && var1 != (-32768 << 16 | 0) && mc.player != null ? mc.player.age - var1 >= Math.max(1, Math.round(var2 / 50.0F)) : true;
   }

   private boolean method5923(BlockPos var1) {
      var var10000 = this.items5;
      Long var10001 = var1.asLong();
      if (var10000 != null) {
         if (var10000.isEmpty()) {
            return false;
         }
      }

      return var10000.containsKey(var10001);
   }

   private void method5924(int var1) {
      ArrayList var3 = new ArrayList();

      CrystalAuraModule.Data7 var2;
      while ((var2 = this.queue.poll()) != null) {
         long var4 = var2.getLongValue();
         BlockState var6 = var2.state();
         boolean var7;
         if (!(var7 = var2.getBlockState2() != null && var2.getBlockState2().getBlock() == var6.getBlock())) {
            boolean var11;
            label202: {
               var var10000 = this.items2;
               Long var10001 = var4;
               if (var10000 != null) {
                  if (var10000.isEmpty()) {
                     var11 = false;
                     break label202;
                  }
               }

               var11 = var10000.containsKey(var10001);
            }

            if (!var11
               && method5956(var6)
               && (this.items18 != null && this.items18.getClass() == EmptyList.class ? false : !this.items18.isEmpty())
               && BlockPos.fromLong(var4).isWithinDistance(mc.player.getBlockPos(), 5.0)) {
               var3.add(var4);
            }
         }

         label190:
         if (this.method5925()) {
            if (var7) {
               boolean var13;
               label186: {
                  var var12 = this.items2;
                  Long var22 = var4;
                  if (var12 != null) {
                     if (var12.isEmpty()) {
                        var13 = false;
                        break label186;
                     }
                  }

                  var13 = var12.containsKey(var22);
               }

               if (!var13) {
                  break label190;
               }
            }

            if (BlockPos.fromLong(var4).isWithinDistance(mc.player.getBlockPos(), 4.0)) {
               CombatLogger var14;
               boolean var27;
               Object[] var10003;
               label177: {
                  var14 = this.combatLogger;
                  var10003 = new Object[]{method5948(BlockPos.fromLong(var4)), var6.isAir() ? "воздух" : method5950(var6.getBlock().asItem()), null};
                  var var10006 = this.items2;
                  Long var10007 = var4;
                  if (var10006 != null) {
                     if (var10006.isEmpty()) {
                        var27 = false;
                        break label177;
                     }
                  }

                  var27 = var10006.containsKey(var10007);
               }

               var10003[2] = var27 ? " (наш)" : "";
               var14.method4612("BLOCK", "%s -> %s%s", var10003);
            }
         }

         boolean var16;
         label172: {
            var var15 = this.items2;
            Long var23 = var4;
            if (var15 != null) {
               if (var15.isEmpty()) {
                  var16 = false;
                  break label172;
               }
            }

            var16 = var15.containsKey(var23);
         }

         if (var16) {
            if (!var6.isReplaceable()) {
               this.items2.remove(var4);
               this.placementQueue.method2813(var4 * 4L + 1L);
            } else if (var6.isReplaceable()) {
               this.items2.remove(var4);
               this.placementQueue.method2814(var4 * 4L + 1L);
               this.items5.put(var4, var1 + 10 + this.intValue15);
            }
         }

         if (var6.isOf(Blocks.RESPAWN_ANCHOR) && var6.get(RespawnAnchorBlock.CHARGES) > 0) {
            this.placementQueue.method2813(var4 * 4L + 2L);
         }

         if (var6.isReplaceable()) {
            label164: {
               var var17 = this.items20;
               Long var24 = var4;
               if (var17 != null) {
                  if (var17.isEmpty()) {
                     var16 = false;
                     break label164;
                  }
               }

               var16 = var17.containsKey(var24);
            }

            if (var16 && this.method5772().contains(BlockPos.fromLong(var4))) {
               label156: {
                  var var19 = this.items2;
                  Long var25 = var4;
                  if (var19 != null) {
                     if (var19.isEmpty()) {
                        var16 = false;
                        break label156;
                     }
                  }

                  var16 = var19.containsKey(var25);
               }

               if (!var16) {
                  this.items22.put(var4, var1 + 40);
                  if (this.method5925()) {
                     Object[] var26;
                     boolean var29;
                     org.zenith.utility.config.CombatLogger var21;
                     label149: {
                        var21 = this.combatLogger;
                        var26 = new Object[]{method5948(BlockPos.fromLong(var4)), null};
                        var var28 = this.items20;
                        Long var30 = var4;
                        if (var28 != null) {
                           if (var28.isEmpty()) {
                              var29 = false;
                              break label149;
                           }
                        }

                        var29 = var28.containsKey(var30);
                     }

                     var26[1] = var29 ? " (его копали)" : "";
                     var21.method4612("ANTIMINE", "сурраунд %s выбит%s", var26);
                  }
               }
            }

            this.items20.remove(var4);
            this.items21.remove(var4);
         }
      }

      if ((!var3.isEmpty()) && var3.size() <= 3) {
         var var9 = var3.iterator();

         while (var9.hasNext()) {
            long var5;
            Vec3d var10 = Vec3d.ofCenter(BlockPos.fromLong(var5 = (Long)var9.next()));
            if (this.items18.stream().anyMatch(var1x -> var1x.getPlayerEntity().getEyePos().distanceTo(var10) <= 6.0)) {
               this.items8.put(var5, var1 + 60);
               if (this.method5925()) {
                  this.combatLogger.method4612("ENEMY", "поставил основание %s рядом с нами", method5948(BlockPos.fromLong(var5)));
               }
            }
         }
      }
   }

   private boolean method5925() {
      return !this.flag4 && this.entry20.isEnabled() && this.combatLogger.active();
   }

   public void method5926() {
      if (!this.isEnabled() && this.combatLogger.active()) {
         this.combatLogger.method4611(false, "");
      }
   }

   public void method5927(String var1) {
      if (this.entry20.isEnabled() && this.combatLogger.active()) {
         this.combatLogger.method4612("TOTEM", "%s (в руке у клиента: %s)", var1, mc.player == null ? "?" : method5950(mc.player.getOffHandStack().getItem()));
      }
   }

   private void method5928(Packet<?> var1, PlayerEntity var2) {
      if (var1 instanceof DeathMessageS2CPacket var3 && var3.playerId() == var2.getId()) {
         this.combatLogger.method4612("DEATH", "%s", var3.message().getString());
         this.combatLogger.method4616("смерть");
      } else if (var1 instanceof HealthUpdateS2CPacket var4) {
         float var6 = this.floatValue4;
         float var7 = var4.getHealth();
         this.floatValue4 = var7;
         if (var6 >= 0.0F && Math.abs(var7 - var6) > 0.01F) {
            if (var7 < var6) {
               this.combatLogger.floatValue += var6 - var7;
            }

            this.combatLogger.method4612("HP", "наше hp %.1f -> %.1f (%+.1f), absorption %.1f", var6, var7, var7 - var6, var2.getAbsorptionAmount());
         }
      } else if (var1 instanceof PlayerPositionLookS2CPacket var5) {
         Vec3d var8 = var5.change().position();
         CombatLogger var10000 = this.combatLogger;
         Object[] var10003 = new Object[]{method5949(var8), null, null, null, null, null, null};
         var var10006 = var5.relatives();
         var10003[1] = (var10006.isEmpty())
            ? ""
            : " (относительно: " + var5.relatives() + ")";
         var10003[2] = method5949(var2.getEntityPos());
         var10003[3] = method5949(var2.getVelocity());
         var10003[4] = var2.isOnGround() ? "да" : "нет";
         var10003[5] = var2.fallDistance;
         var10003[6] = this.rotation != null ? "да" : "нет";
         var10000.method4612("SETBACK", "сервер переставил нас в %s%s; были в %s, скорость %s, на земле %s, падение %.2f, держим поворот %s", var10003);
      } else if (var1 instanceof EntityDamageS2CPacket || var1 instanceof EntityAnimationS2CPacket || var1 instanceof EntityEquipmentUpdateS2CPacket) {
         if (var1 instanceof EntityDamageS2CPacket var9) {
            this.items23.put(var9.entityId(), var9.sourceType().isIn(DamageTypeTags.IS_EXPLOSION));
         }

         this.queue5.add(var1);
      }
   }

   private void method5929() {
      Packet var1;
      while ((var1 = this.queue5.poll()) != null) {
         if (this.method5925()) {
            if (var1 instanceof EntityDamageS2CPacket var2) {
               Entity var10 = mc.world.getEntityById(var2.entityId());
               Entity var12 = var2.sourceCauseId() >= 0 ? mc.world.getEntityById(var2.sourceCauseId()) : null;
               String var13 = var2.sourceType().getIdAsString();
               if (var10 == mc.player) {
                  this.combatLogger
                     .method4612(
                        "HURT",
                        "по нам: %s от %s%s",
                        var13,
                        method5947(var12),
                        var2.sourcePosition()
                           .map(var0 -> " из " + method5949(var0) + String.format(Locale.ROOT, " (%.1f б)", var0.distanceTo(mc.player.getEntityPos())))
                           .orElse("")
                     );
               } else if (var10 instanceof PlayerEntity var8 && this.method5868(var8)) {
                  this.combatLogger
                     .method4612(
                        "HIT", "по цели %s: %s от %s, hp %.1f", method5947(var8), var13, var12 == mc.player ? "нас" : method5947(var12), method5902(var8)
                     );
               }
            } else if (var1 instanceof EntityAnimationS2CPacket var3) {
               if (mc.world.getEntityById(var3.getEntityId()) instanceof PlayerEntity var11
                  && this.method5868(var11)
                  && (var3.getAnimationId() == 0 || var3.getAnimationId() == 3)) {
                  this.combatLogger
                     .method4612(
                        "ENEMY",
                        "%s взмах %s рукой, в руке %s, дист %.1f",
                        method5947(var11),
                        var3.getAnimationId() == 0 ? "правой" : "левой",
                        method5950(var11.getMainHandStack().getItem()),
                        var11.distanceTo(mc.player)
                     );
               }
            } else if (var1 instanceof EntityEquipmentUpdateS2CPacket var4
               && mc.world.getEntityById(var4.getEntityId()) instanceof PlayerEntity var6
               && this.method5868(var6)) {
               StringBuilder var7 = new StringBuilder();
               var4.getEquipmentList()
                  .forEach(
                     var1x -> var7.append(' ')
                        .append(((EquipmentSlot)var1x.getFirst()).getName())
                        .append('=')
                        .append(
                           ((ItemStack)var1x.getSecond()).isEmpty()
                              ? "пусто"
                              : method5950(((ItemStack)var1x.getSecond()).getItem()) + "x" + ((ItemStack)var1x.getSecond()).getCount()
                        )
                  );
               this.combatLogger.method4612("ENEMY", "%s сменил экипировку:%s", method5947(var6), var7);
            }
         }
      }
   }

   private void method5930() {
      if (this.method5925()) {
         StringBuilder var1 = new StringBuilder();
         var1.append(
            String.format(
               Locale.ROOT,
               "мы hp %.1f%s%s нора=%s кристаллов %d обсы %d",
               mc.player.getHealth(),
               mc.player.getAbsorptionAmount() > 0.0F ? String.format(Locale.ROOT, "+%.0f", mc.player.getAbsorptionAmount()) : "",
               mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) ? " тотем" : " БЕЗ ТОТЕМА",
               this.method5945(mc.player.getBoundingBox()) ? "да" : "нет",
               this.method5946(Items.END_CRYSTAL),
               this.method5946(Items.OBSIDIAN)
            )
         );

         for (CrystalAuraModule.Data5 var3 : this.items18) {
            var1.append(
               String.format(
                  Locale.ROOT,
                  " | %s hp %.1f%s дист %.1f нора=%s%s%s",
                  method5947(var3.getPlayerEntity()),
                  var3.getFloatValue(),
                  var3.isFlag() ? " тотем" : "",
                  var3.getVec3d().distanceTo(mc.player.getEntityPos()),
                  this.method5945(var3.getBox()) ? "да" : "нет",
                  var3.getBox4() != null ? " ПОДЛЁТ" : "",
                  var3.getIntValue2() <= 10 ? " ПОПНУЛСЯ" : ""
               )
            );
         }

         var1.append(" || break ").append(this.method5944(this.data));
         var1.append(" | план ").append(this.method5944(this.data4));
         if (this.data2 != null) {
            var1.append(" | ждём ").append(this.method5944(this.data2));
         }

         if (this.data5 != null) {
            var1.append(" | деф ").append(this.method5944(this.data5));
         }

         var1.append(
            String.format(Locale.ROOT, " | опасность %.1f | спотов %d/%d без прицела %d", this.floatValue2, this.intValue24, this.intValue23, this.intValue25)
         );
         if (this.blockPos3 != null) {
            var1.append(String.format(Locale.ROOT, " | копаем %s %.0f%%", method5948(this.blockPos3), this.floatValue * 100.0F));
         }

         this.combatLogger.method4615(var1.toString());
      }
   }

   private void method5931(int var1) {
      this.method5939();
      if (!this.method5925()) {
         this.jsonObject = null;
      } else {
         this.jsonObject = new JsonObject();
         this.jsonObject.addProperty("t", var1);
         this.jsonObject.addProperty("ms", System.currentTimeMillis());
         this.jsonArray = new JsonArray();
         this.jsonArray2 = new JsonArray();
         this.items24.clear();
         this.text3 = null;
         this.floatValue3 = -1.0F;
         JsonObject var2 = new JsonObject();
         var2.addProperty("pos", method5941(mc.player.getEntityPos()));
         var2.addProperty("vel", method5941(mc.player.getVelocity()));
         var2.addProperty("yaw", round(mc.player.getYaw()));
         var2.addProperty("pitch", round(mc.player.getPitch()));
         Rotation var3 = this.method5897();
         var2.addProperty("srvYaw", round(var3.getFloatValue()));
         var2.addProperty("srvPitch", round(var3.getFloatValue2()));
         var2.addProperty("hp", round(mc.player.getHealth()));
         var2.addProperty("abs", round(mc.player.getAbsorptionAmount()));
         var2.addProperty("ground", mc.player.isOnGround());
         var2.addProperty("sprint", mc.player.isSprinting());
         var2.addProperty("sneak", mc.player.isSneaking());
         var2.addProperty("using", mc.player.isUsingItem());
         var2.addProperty("hurt", mc.player.hurtTime);
         var2.addProperty("fall", round(mc.player.fallDistance));
         var2.addProperty("slot", mc.player.getInventory().getSelectedSlot());
         var2.addProperty("main", method5950(mc.player.getMainHandStack().getItem()));
         var2.addProperty("off", method5950(mc.player.getOffHandStack().getItem()));
         var2.addProperty("crystals", this.method5946(Items.END_CRYSTAL));
         var2.addProperty("obby", this.method5946(Items.OBSIDIAN));
         var2.addProperty("totems", this.method5946(Items.TOTEM_OF_UNDYING));
         StringBuilder var4 = new StringBuilder();

         for (int var5 = 0; var5 < 9; var5++) {
            ItemStack var6 = mc.player.getInventory().getStack(var5);
            var4.append(var5 == 0 ? "" : ",").append(var6.isEmpty() ? "-" : var6.getItem().toString().replace("minecraft:", ""));
         }

         var2.addProperty("hotbar", var4.toString());
         var2.addProperty("ping", this.intValue15);
         var2.addProperty("hole", this.method5945(mc.player.getBoundingBox()));
         var2.addProperty("preset", this.preset.get());
         this.jsonObject.add("me", var2);
      }
   }

   private void method5932() {
      if (this.jsonObject != null) {
         JsonArray var1 = new JsonArray();

         for (CrystalAuraModule.Data5 var3 : this.items18) {
            PlayerEntity var4 = var3.getPlayerEntity();
            CrystalAuraModule.Entry var5 = this.items14.get(var4.getId());
            JsonObject var6 = new JsonObject();
            var6.addProperty("id", var4.getId());
            var6.addProperty("name", var4.getName().getString());
            var6.addProperty("pos", method5941(var3.getVec3d()));
            var6.addProperty("vel", var5 == null ? "0,0,0" : method5941(var5.method5971()));
            var6.addProperty("dist", round(var3.getVec3d().distanceTo(mc.player.getEntityPos())));
            var6.addProperty("hp", round(var3.getFloatValue()));
            var6.addProperty("totem", var3.isFlag());
            var6.addProperty("hole", this.method5945(var3.getBox()));
            var6.addProperty("ground", var4.isOnGround());
            var6.addProperty("iframes", var3.getIntValue());
            var6.addProperty("lastRaw", round(var3.getFloatValue2()));
            var6.addProperty("popAge", var3.getIntValue2() == (32767 << 16 | (char)-1) ? -1 : var3.getIntValue2());
            var6.addProperty("resist", var3.isFlag3());
            var6.addProperty("weakArmor", var3.isFlag2());
            var6.addProperty("blocking", var4.isBlocking());
            var6.addProperty("main", method5950(var4.getMainHandStack().getItem()));
            var6.addProperty("off", method5950(var4.getOffHandStack().getItem()));
            var6.addProperty("arrival", method5942(var3.getBox()));
            var6.addProperty("place", method5942(var3.getBox2()));
            var6.addProperty("potential", method5942(var3.getBox3()));
            if (var3.getBox4() != null) {
               var6.addProperty("rising", method5942(var3.getBox4()));
            }

            if (var5 != null && var5.box != null) {
               var6.addProperty("knockAge", mc.player.age - var5.intValue5);
               var6.addProperty("knockVy", round(var5.vec3d.y));
            }

            var1.add(var6);
         }

         this.jsonObject.add("targets", var1);
      }
   }

   private void method5933(List<CrystalAuraModule.Data10> var1, boolean var2) {
      if (this.jsonObject != null) {
         JsonArray var3 = new JsonArray();

         for (int var4 = 0; var4 < var1.size() && var4 < 8; var4++) {
            CrystalAuraModule.Data10 var5 = (CrystalAuraModule.Data10)var1.get(var4);
            JsonObject var6 = method5940("base", method5948(var5.getBlockPos()));
            var6.addProperty("dmg", round(var5.getFloatValue()));
            if (var5.getBlockPos2() != null) {
               var6.addProperty("opensAfterMining", method5948(var5.getBlockPos2()));
            }

            var3.add(var6);
         }

         this.jsonObject.add("dangers", var3);
         this.jsonObject.addProperty("dangerUrgent", var2);
      }
   }

   private void method5934(CrystalAuraModule.Data13 var1, CrystalAuraModule.Data12 var2, float var3, String var4) {
      if (this.jsonObject != null) {
         JsonObject var5 = method5940("pos", method5948(var1.pos()), "kind", var1.getType().name());
         var5.addProperty("bound", round(var1.getFloatValue3()));
         var5.addProperty("bonus", round(var1.getFloatValue2()));
         var5.addProperty("dist", round(Math.sqrt(method5962(mc.player.getEyePos(), new Box(var1.pos())))));
         if (var1.getBlockPos2() != null) {
            var5.addProperty("air", method5948(var1.getBlockPos2()));
         }

         if (var1.getBlockPos3() != null) {
            var5.addProperty("solid", method5948(var1.getBlockPos3()));
         }

         if (var1.getIntValue() > 0) {
            var5.addProperty("mineTicks", var1.getIntValue());
         }

         if (var2 != null) {
            var5.addProperty("dmg", round(var2.getFloatValue()));
            var5.addProperty("self", round(var2.getFloatValue2()));
            var5.addProperty("value", round(var3));
            var5.addProperty("ready", var2.isFlag());
            var5.addProperty("target", method5947(var2.getPlayerEntity()));
         }

         if (var4 != null) {
            var5.addProperty("reject", var4);
            this.method5936(
               String.format(
                  Locale.ROOT,
                  "%s %s (до места %.2f б, верхняя оценка %.1f): %s",
                  var1.getType(),
                  method5948(var1.pos()),
                  var5.get("dist").getAsDouble(),
                  var1.getFloatValue3(),
                  var4
               ),
               var1.getFloatValue3()
            );
         }

         this.jsonArray2.add(var5);
      }
   }

   private void method5935(EndCrystalEntity var1, CrystalAuraModule.Data12 var2, String var3) {
      if (this.jsonObject != null) {
         JsonObject var4 = method5940("pos", method5941(var1.getEntityPos()));
         var4.addProperty("id", var1.getId());
         var4.addProperty("dist", round(Math.sqrt(method5962(mc.player.getEyePos(), var1.getBoundingBox()))));
         var4.addProperty("serverHit", this.method5835(mc.player.getEyePos(), this.method5897(), var1) != null);
         var4.addProperty("age", var1.age);
         if (var2 != null) {
            var4.addProperty("dmg", round(var2.getFloatValue()));
            var4.addProperty("self", round(var2.getFloatValue2()));
            var4.addProperty("value", round(var2.value()));
            var4.addProperty("ready", var2.isFlag());
            var4.addProperty("target", method5947(var2.getPlayerEntity()));
         }

         if (var3 != null) {
            var4.addProperty("reject", var3);
            this.method5936(
               String.format(
                  Locale.ROOT, "кристалл #%d %s (до него %.2f б): %s", var1.getId(), method5949(var1.getEntityPos()), var4.get("dist").getAsDouble(), var3
               ),
               50.0F
            );
         }

         this.jsonArray.add(var4);
      }
   }

   private void method5936(String var1, float var2) {
      if (this.jsonObject != null && var2 > this.floatValue3) {
         this.text3 = var1;
         this.floatValue3 = var2;
      }
   }

   private void method5937(String var1) {
      if (this.jsonObject != null) {
         this.items24.merge(var1, 1, Integer::sum);
      }
   }

   private void method5938() {
      if (this.jsonObject != null) {
         this.jsonObject.addProperty("break", this.method5944(this.data));
         this.jsonObject.addProperty("action", this.method5944(this.data4));
         this.jsonObject.addProperty("wait", this.method5944(this.data2));
         this.jsonObject.addProperty("defense", this.method5944(this.data5));
         this.jsonObject.addProperty("danger", round(this.floatValue2));
         this.jsonObject.addProperty("breakOptions", this.items16.size());
         this.jsonObject.addProperty("actionOptions", this.items17.size());
         this.jsonObject.addProperty("spots", this.intValue23);
         this.jsonObject.addProperty("ranked", this.intValue24);
         this.jsonObject.addProperty("aimFails", this.intValue25);
         if (this.rotation != null) {
            this.jsonObject.addProperty("aimYaw", round(this.rotation.getFloatValue()));
            this.jsonObject.addProperty("aimPitch", round(this.rotation.getFloatValue2()));
         }

         if (this.rotation4 != null) {
            this.jsonObject.addProperty("preAim", true);
         }

         if (this.blockPos3 != null) {
            this.jsonObject.addProperty("digging", method5948(this.blockPos3));
            this.jsonObject.addProperty("digProgress", round(this.floatValue));
         }

         this.jsonObject.add("crystals", this.jsonArray);
         this.jsonObject.add("spotsEval", this.jsonArray2);
         JsonObject var1 = new JsonObject();
         this.items24.forEach(var1::addProperty);
         this.jsonObject.add("filtered", var1);
         if (this.data == null && (this.data4 == null || !this.data4.isFlag()) && this.text3 != null && !this.text3.equals(this.text4)) {
            this.text4 = this.text3;
            this.combatLogger.method4612("REJECT", "%s", this.text3);
         }
      }
   }

   private void method5939() {
      if (this.jsonObject != null) {
         JsonArray var1 = new JsonArray();
         this.combatLogger.method4614().forEach(var1::add);
         this.jsonObject.add("events", var1);
         this.jsonObject.addProperty("windowActions", this.intValue8);
         this.jsonObject.addProperty("unreachable", this.items13.size());
         this.jsonObject.addProperty("unreachableMax", this.items13.values().stream().mapToInt(Integer::intValue).max().orElse(-1));
         this.jsonObject.addProperty("age", mc.player.age);
         this.jsonObject.addProperty("mustCross", this.box3 != null);
         this.jsonObject.addProperty("webTrap", this.text6);
         this.jsonObject.addProperty("commit", this.blockPos == null ? "-" : this.type + " " + method5948(this.blockPos));
         this.jsonObject.addProperty("weapon", this.method5784() == null ? "-" : this.method5784().name());
         this.combatLogger.method4613(this.jsonObject.toString());
         this.jsonObject = null;
      }
   }

   private static JsonObject method5940(String... var0) {
      JsonObject var1 = new JsonObject();

      for (byte var2 = 0; var2 + 1 < var0.length; var2 += 2) {
         var1.addProperty(var0[var2], var0[var2 + 1]);
      }

      return var1;
   }

   private static double round(double var0) {
      return Math.round(var0 * 1000.0) / 1000.0;
   }

   private static String method5941(Vec3d var0) {
      return String.format(Locale.ROOT, "%.3f,%.3f,%.3f", var0.x, var0.y, var0.z);
   }

   private static String method5942(Box var0) {
      return String.format(Locale.ROOT, "%.3f,%.3f,%.3f", (var0.minX + var0.maxX) * 0.5, var0.minY, (var0.minZ + var0.maxZ) * 0.5);
   }

   private void method5943(CrystalAuraModule.Data var1, String var2) {
      if (var2 == null) {
         if (this.intValue26 >= 3 && this.method5925()) {
            this.combatLogger.method4612("STALL", "простой %d тиков закончился, причина была: %s", this.intValue26, this.text5);
         }

         this.intValue26 = 0;
         this.text5 = null;
      } else {
         this.intValue26++;
         this.text5 = var2;
         if (this.method5925()) {
            this.combatLogger
               .method4612(
                  "MISS",
                  "%s не сделали: %s (тик простоя %d) — урон %.1f по %s, до места %.2f б, цель в %.2f б",
                  this.method5944(var1),
                  var2,
                  this.intValue26,
                  var1.getFloatValue(),
                  method5947(var1.getPlayerEntity()),
                  Math.sqrt(method5962(mc.player.getEyePos(), new Box(var1.pos()))),
                  var1.getPlayerEntity() == null ? -1.0 : var1.getPlayerEntity().distanceTo(mc.player)
               );
         }
      }
   }

   private String method5944(CrystalAuraModule.Data var1) {
      return var1 == null
         ? "-"
         : String.format(
            Locale.ROOT,
            "%s %s %.1f/%.1f%s",
            var1.getType(),
            method5948(var1.pos()),
            var1.getFloatValue(),
            var1.getFloatValue2(),
            var1.isFlag() ? "" : " (рано)"
         );
   }

   private boolean method5945(Box var1) {
      BlockPos var2 = BlockPos.ofFloored((var1.minX + var1.maxX) * 0.5, var1.minY + 0.001, (var1.minZ + var1.maxZ) * 0.5);

      for (Direction var4 : Direction.Type.HORIZONTAL) {
         if (mc.world.getBlockState(var2.offset(var4)).getBlock().getBlastResistance() < 600.0F) {
            return false;
         }
      }

      return true;
   }

   private int method5946(Item var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < mc.player.getInventory().size(); var3++) {
         ItemStack var4 = mc.player.getInventory().getStack(var3);
         if (var4.isOf(var1)) {
            var2 += var4.getCount();
         }
      }

      return var2;
   }

   private static String method5947(Entity var0) {
      return var0 == null ? "?" : var0.getName().getString() + "#" + var0.getId();
   }

   private static String method5948(BlockPos var0) {
      return var0.getX() + "," + var0.getY() + "," + var0.getZ();
   }

   private static String method5949(Vec3d var0) {
      return String.format(Locale.ROOT, "%.1f,%.1f,%.1f", var0.x, var0.y, var0.z);
   }

   private static String method5950(Item var0) {
      return var0.toString();
   }

   private void method5951(int var1) {
      Integer var2;
      while ((var2 = this.queue2.poll()) != null) {
         CrystalAuraModule.Entry var3 = this.items14.get(var2);
         if (var3 != null) {
            var3.setIntValue3(var1);
         }

         if (this.method5925()) {
            Entity var4 = mc.world.getEntityById(var2);
            if (var4 == mc.player) {
               this.combatLogger.intValue7++;
               this.combatLogger.method4612("POP", "НАШ тотем сработал, тотемов осталось %d", this.method5946(Items.TOTEM_OF_UNDYING));
            } else if (var4 instanceof PlayerEntity var5) {
               if (var3 != null) {
                  this.combatLogger.intValue8++;
               }

               this.combatLogger.method4612("POP", "тотем у %s, дист %.1f", method5947(var5), var5.distanceTo(mc.player));
            }
         }
      }

      CrystalAuraModule.Data9 var9;
      while ((var9 = this.queue3.poll()) != null) {
         if (this.entry19.isEnabled()) {
            Vec3d var10 = var9.getVec3d();
            double var13 = var9.getFloatValue() * 2.0;

            for (EndCrystalEntity var8 : mc.world.getEntitiesByClass(EndCrystalEntity.class, new Box(var10, var10).expand(var13), var1x -> {
               if (var1x.isAlive()) {
                  boolean var2x;
                  label20: {
                     var var10000 = this.items6;
                     Integer var10001 = var1x.getId();
                     if (var10000 != null) {
                        if (var10000.isEmpty()) {
                           var2x = false;
                           break label20;
                        }
                     }

                     var2x = var10000.containsKey(var10001);
                  }

                  if (!var2x) {
                     return true;
                  }
               }

               return false;
            })) {
               if (var8.getEntityPos().distanceTo(var10) <= var13 - 0.5) {
                  this.items6.put(var8.getId(), var1 + this.method5909());
               }
            }
         }

         if (this.method5925()) {
            this.combatLogger
               .method4612(
                  "BOOM",
                  "взрыв %s сила %.0f, до нас %.1f б",
                  method5949(var9.getVec3d()),
                  var9.getFloatValue(),
                  var9.getVec3d().distanceTo(mc.player.getEntityPos())
               );
         }

         if (var9.getFloatValue() < 5.5F && var9.getVec3d().distanceTo(mc.player.getEntityPos()) <= 6.0) {
            this.intValue21 = var1;
         }

         if (this.data8 == null || this.data8.getIntValue() != var1) {
            BlockPos var11 = var9.getFloatValue() < 5.5F ? BlockPos.ofFloored(var9.getVec3d()) : null;
            this.data8 = new CrystalAuraModule.Data8(var9.getVec3d(), var9.getFloatValue(), var11, var1);
         }
      }

      CrystalAuraModule.Data2 var12;
      while ((var12 = this.queue4.poll()) != null) {
         if (var12.getIntValue() >= 0 && var12.getIntValue() <= 9) {
            if (BlockPos.fromLong(var12.getLongValue()).isWithinDistance(mc.player.getBlockPos(), 4.0)) {
               this.items21.put(var12.getLongValue(), var12.getIntValue());
               if (this.items20.put(var12.getLongValue(), var1 + 40) == null && this.method5925()) {
                  this.combatLogger
                     .method4612(
                        "ENEMY",
                        "копает блок рядом с нами %s (%s)",
                        method5948(BlockPos.fromLong(var12.getLongValue())),
                        method5950(mc.world.getBlockState(BlockPos.fromLong(var12.getLongValue())).getBlock().asItem())
                     );
               }
            }
         } else {
            this.items20.remove(var12.getLongValue());
            this.items21.remove(var12.getLongValue());
         }
      }
   }

   private void method5952(int var1) {
      var var2 = this.items.entrySet().iterator();

      while (var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         if ((Integer)var3.getValue() < var1) {
            this.items5.put((Long)var3.getKey(), var1 + 4 + this.intValue15);
            var2.remove();
         }
      }

      if (this.longValue != Long.MIN_VALUE) {
         boolean var10;
         label76: {
            var var10000 = this.items;
            Long var10001 = this.longValue;
            if (var10000 != null) {
               if (var10000.isEmpty()) {
                  var10 = false;
                  break label76;
               }
            }

            var10 = var10000.containsKey(var10001);
         }

         if (!var10) {
            this.longValue = Long.MIN_VALUE;
            this.playerEntity2 = null;
         }
      }

      this.items20.values().removeIf(var1x -> var1x < var1);
      this.items21.keySet().retainAll(this.items20.keySet());
      this.items22.values().removeIf(var1x -> var1x < var1);
      this.items3.values().removeIf(var1x -> var1x < var1 - 40);
      this.items4.values().removeIf(var1x -> var1x < var1 - 60);
      var var8 = this.items2.entrySet().iterator();

      while (var8.hasNext()) {
         Map.Entry var4 = (Map.Entry)var8.next();
         if ((Integer)var4.getValue() < var1) {
            if (mc.world.getBlockState(BlockPos.fromLong((Long)var4.getKey())).isReplaceable()) {
               this.items5.put((Long)var4.getKey(), var1 + 10 + this.intValue15);
            }

            var8.remove();
         }
      }

      this.items5.values().removeIf(var1x -> var1x < var1);
      var var9 = this.items6.entrySet().iterator();

      while (var9.hasNext()) {
         Map.Entry var5 = (Map.Entry)var9.next();
         if ((Integer)var5.getValue() < var1) {
            var9.remove();
            Double var6 = this.items7.remove(var5.getKey());
            if (var6 != null) {
               Entity var7 = mc.world.getEntityById((Integer)var5.getKey());
               if (var7 instanceof EndCrystalEntity && var7.isAlive()) {
                  if (var6 > 2.3 && var6 - 0.05 < this.doubleValue) {
                     this.doubleValue = Math.max(2.3, var6 - 0.05);
                     if (this.method5925()) {
                        this.combatLogger
                           .method4612("REACH", "кристалл #%d пережил удар с %.2f б — предел удара теперь %.2f б", var5.getKey(), var6, this.doubleValue);
                     }
                  }
               } else if (var6 >= this.doubleValue - 0.2) {
                  this.doubleValue = Math.min(3.0, this.doubleValue + 0.02);
               }
            }
         }
      }

      this.items7.keySet().retainAll(this.items6.keySet());
      this.items10.removeIf(var0 -> mc.world.getEntityById(var0) == null);
      if (this.data8 != null && var1 - this.data8.getIntValue() > 40) {
         this.data8 = null;
      }
   }

   private org.zenith.utility.misc.Data3 method5953() {
      int var1;
      org.zenith.utility.misc.Data3 var2 = (var1 = Math.max(0, this.preset.getIndex())) < org.zenith.utility.misc.Data3.data3s.length
         ? org.zenith.utility.misc.Data3.data3s[var1]
         : null;
      if (var2 != null) {
         return var2;
      } else {
         org.zenith.utility.misc.Data3.Type var3 = this.swap.getIndex() == 1
            ? org.zenith.utility.misc.Data3.Type.SILENT
            : (this.swap.getIndex() == 2 ? org.zenith.utility.misc.Data3.Type.OFF : org.zenith.utility.misc.Data3.Type.KEEP);
         return new org.zenith.utility.misc.Data3(
            this.mode.getIndex() == 1,
            this.placeRange.getValue3(),
            this.breakRange.getValue3(),
            Math.max(1, Math.round(this.actions.getValue3())),
            Math.round(this.placeDelay.getValue3()),
            Math.round(this.breakDelay.getValue3()),
            this.rotationSpeed.getValue3(),
            var3,
            this.instantBreak.isEnabled(),
            this.instantBreak.isEnabled() && this.instantPlace.isEnabled(),
            this.varyRotation.isEnabled(),
            this.throughWalls.isEnabled(),
            false
         );
      }
   }

   private boolean method5954() {
      return this.preset.getIndex() == 0;
   }

   private void method5955() {
      this.method5886(true);
      this.playerEntity = null;
      this.items18 = List.of();
      this.items19 = List.of();
      this.blockPos2 = null;
      this.rotation2 = null;
      this.box = null;
      this.box2 = null;
      this.playerEntity3 = null;
      this.rotation5 = null;
   }

   private void reset() {
      this.method5955();
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.items4.clear();
      this.type2 = null;
      this.intValue = -32768 << 16 | 0;
      this.items5.clear();
      this.items6.clear();
      this.items14.clear();
      this.queue.clear();
      this.items15.clear();
      this.items16.clear();
      this.items17.clear();
      this.placementQueue.clear();
      this.class33.cancel();
      this.class35.reset();
      this.data = null;
      this.data2 = null;
      this.data4 = null;
      this.rotation = null;
      this.rotation3 = null;
      this.data8 = null;
      this.items23.clear();
      this.items7.clear();
      this.box3 = null;
      this.items13.clear();
      this.items11.clear();
      this.items10.clear();
      this.items8.clear();
      this.items9.clear();
      this.intValue2 = -32768 << 16 | 0;
      this.items12.clear();
      this.intValue27 = -1;
      this.intValue31 = -1;
      this.intValue28 = -1;
      this.intValue29 = -1;
      this.blockPos = null;
      this.type = null;
      this.blockPos3 = null;
      this.floatValue = 0.0F;
      this.intValue4 = -32768 << 16 | 0;
      this.intValue5 = -32768 << 16 | 0;
      this.intValue6 = -32768 << 16 | 0;
      this.intValue7 = -32768 << 16 | 0;
      this.intValue8 = 0;
      this.intValue9 = 0;
      this.intValue10 = 0;
      this.intValue11 = 0;
      this.intValue12 = -1;
      this.intValue13 = -1;
      this.intValue14 = -1;
      this.intValue16 = -32768 << 16 | 0;
      this.intValue17 = -32768 << 16 | 0;
      this.intValue18 = -32768 << 16 | 0;
      this.flag2 = false;
      this.flag3 = false;
      this.queue2.clear();
      this.queue3.clear();
      this.queue4.clear();
      this.items20.clear();
      this.longValue = Long.MIN_VALUE;
      this.playerEntity2 = null;
      this.intValue20 = -32768 << 16 | 0;
      this.floatValue2 = 0.0F;
      this.data5 = null;
      this.intValue22 = -32768 << 16 | 0;
      this.queue5.clear();
      this.jsonObject = null;
      this.text4 = null;
      if (mc.player != null && mc.isOnThread()) {
         this.combatLogger.method4614();
      }

      this.items21.clear();
      this.items22.clear();
      this.rotation4 = null;
      this.blockPos4 = null;
      this.type3 = null;
      this.intValue26 = 0;
      this.text5 = null;
      this.floatValue4 = -1.0F;
   }

   private static boolean method5956(BlockState var0) {
      return var0.isOf(Blocks.OBSIDIAN) || var0.isOf(Blocks.BEDROCK);
   }

   private static boolean method5957(CrystalAuraModule.Type var0) {
      return var0 == CrystalAuraModule.Type.ANCHOR_PLACE || var0 == CrystalAuraModule.Type.ANCHOR_CHARGE || var0 == CrystalAuraModule.Type.ANCHOR_EXPLODE;
   }

   private static long method5958(BlockPos var0, int var1) {
      return var0.asLong() * 4L + var1;
   }

   private static Vec3d method5959(BlockPos var0) {
      return new Vec3d(var0.getX() + 0.5, var0.getY() + 1.0, var0.getZ() + 0.5);
   }

   private static Box method5960(BlockPos var0) {
      return new Box(var0.getX(), var0.getY() + 1.0, var0.getZ(), var0.getX() + 1.0, var0.getY() + 3.0, var0.getZ() + 1.0);
   }

   private static Box method5961(BlockPos var0) {
      return new Box(var0.getX() - 0.5, var0.getY() + 1.0, var0.getZ() - 0.5, var0.getX() + 1.5, var0.getY() + 3.0, var0.getZ() + 1.5);
   }

   private static double method5962(Vec3d var0, Box var1) {
      return var0.squaredDistanceTo(
         MathHelper.clamp(var0.x, var1.minX, var1.maxX), MathHelper.clamp(var0.y, var1.minY, var1.maxY), MathHelper.clamp(var0.z, var1.minZ, var1.maxZ)
      );
   }

   private static Vec3d method5963(Box var0, Direction var1, double var2, double var4) {
      var2 = MathHelper.clamp(var2, 0.05, 0.95);
      var4 = MathHelper.clamp(var4, 0.05, 0.95);
      double var6 = MathHelper.lerp(var2, var0.minX, var0.maxX);
      double var8 = MathHelper.lerp(var4, var0.minY, var0.maxY);
      double var10 = MathHelper.lerp(var4, var0.minZ, var0.maxZ);
      double var12 = MathHelper.lerp(var2, var0.minZ, var0.maxZ);

      return switch (var1) {
         case UP -> new Vec3d(var6, var0.maxY, var10);
         case DOWN -> new Vec3d(var6, var0.minY, var10);
         case NORTH -> new Vec3d(var6, var8, var0.minZ);
         case SOUTH -> new Vec3d(var6, var8, var0.maxZ);
         case WEST -> new Vec3d(var0.minX, var8, var12);
         case EAST -> new Vec3d(var0.maxX, var8, var12);
      };
   }

   private static Box method5964(BlockPos var0, double var1) {
      double var3 = 0.5 * MathHelper.clamp(var1, 0.05, 1.0);
      Vec3d var5 = Vec3d.ofCenter(var0);
      return new Box(var5.subtract(var3, var3, var3), var5.add(var3, var3, var3));
   }

   private static final class Data {
      private final CrystalAuraModule.Type type;
      private final EndCrystalEntity endCrystalEntity;
      private final BlockPos blockPos;
      private final Rotation rotation;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final boolean flag;
      private final PlayerEntity playerEntity;

      public CrystalAuraModule.Type type() {
         return this.type;
      }

      public EndCrystalEntity endCrystalEntity() {
         return this.endCrystalEntity;
      }

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public Rotation rotation() {
         return this.rotation;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public boolean flag() {
         return this.flag;
      }

      public PlayerEntity playerEntity() {
         return this.playerEntity;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.endCrystalEntity, other.endCrystalEntity) && java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.rotation, other.rotation) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.playerEntity, other.playerEntity);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(type, endCrystalEntity, blockPos, rotation, floatValue, floatValue2, floatValue3, flag, playerEntity);
      }

      @Override
      public String toString() {
         return "Data[" + "type=" + this.type + ", " + "endCrystalEntity=" + this.endCrystalEntity + ", " + "blockPos=" + this.blockPos + ", " + "rotation=" + this.rotation + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "flag=" + this.flag + ", " + "playerEntity=" + this.playerEntity + "]";
      }

      private Data(
         CrystalAuraModule.Type var1, EndCrystalEntity var2, BlockPos var3, Rotation var4, float var5, float var6, float var7, boolean var8, PlayerEntity var9
      ) {
         this.type = var1;
         this.endCrystalEntity = var2;
         this.blockPos = var3;
         this.rotation = var4;
         this.floatValue = var5;
         this.floatValue2 = var6;
         this.floatValue3 = var7;
         this.flag = var8;
         this.playerEntity = var9;
      }

      public CrystalAuraModule.Type getType() {
         return this.type;
      }

      public EndCrystalEntity getEndCrystalEntity() {
         return this.endCrystalEntity;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public PlayerEntity getPlayerEntity() {
         return this.playerEntity;
      }
   }

   private static final class Data10 {
      private final BlockPos blockPos;
      private final Vec3d vec3d;
      private final BlockPos blockPos2;
      private final float floatValue;
      private final float floatValue2;
      private final boolean flag;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public Vec3d vec3d() {
         return this.vec3d;
      }

      public BlockPos blockPos2() {
         return this.blockPos2;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data10 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.blockPos2, other.blockPos2) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, vec3d, blockPos2, floatValue, floatValue2, flag);
      }

      @Override
      public String toString() {
         return "Data10[" + "blockPos=" + this.blockPos + ", " + "vec3d=" + this.vec3d + ", " + "blockPos2=" + this.blockPos2 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "flag=" + this.flag + "]";
      }

      private Data10(BlockPos var1, Vec3d var2, BlockPos var3, float var4, float var5, boolean var6) {
         this.blockPos = var1;
         this.vec3d = var2;
         this.blockPos2 = var3;
         this.floatValue = var4;
         this.floatValue2 = var5;
         this.flag = var6;
      }

      public BlockPos getBlockPos() {
         return this.blockPos;
      }

      public Vec3d getVec3d() {
         return this.vec3d;
      }

      public BlockPos getBlockPos2() {
         return this.blockPos2;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   private static final class Data11 {
      private final BlockPos blockPos;
      private final EndCrystalEntity endCrystalEntity;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public EndCrystalEntity endCrystalEntity() {
         return this.endCrystalEntity;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data11 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.endCrystalEntity, other.endCrystalEntity);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, endCrystalEntity);
      }

      @Override
      public String toString() {
         return "Data11[" + "blockPos=" + this.blockPos + ", " + "endCrystalEntity=" + this.endCrystalEntity + "]";
      }

      private Data11(BlockPos var1, EndCrystalEntity var2) {
         this.blockPos = var1;
         this.endCrystalEntity = var2;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public EndCrystalEntity getEndCrystalEntity() {
         return this.endCrystalEntity;
      }
   }

   private static final class Data12 {
      private final PlayerEntity playerEntity;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final boolean flag;

      public PlayerEntity playerEntity() {
         return this.playerEntity;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data12 other)) return false;
         return java.util.Objects.equals(this.playerEntity, other.playerEntity) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(playerEntity, floatValue, floatValue2, floatValue3, flag);
      }

      @Override
      public String toString() {
         return "Data12[" + "playerEntity=" + this.playerEntity + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "flag=" + this.flag + "]";
      }

      private Data12(PlayerEntity var1, float var2, float var3, float var4, boolean var5) {
         this.playerEntity = var1;
         this.floatValue = var2;
         this.floatValue2 = var3;
         this.floatValue3 = var4;
         this.flag = var5;
      }

      public PlayerEntity getPlayerEntity() {
         return this.playerEntity;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float value() {
         return this.floatValue3;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   private static final class Data13 {
      private final BlockPos blockPos;
      private final CrystalAuraModule.Type type;
      private final Vec3d vec3d;
      private final float floatValue;
      private final BlockPos blockPos2;
      private final BlockPos blockPos3;
      private final float floatValue2;
      private final float floatValue3;
      private final int intValue;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public CrystalAuraModule.Type type() {
         return this.type;
      }

      public Vec3d vec3d() {
         return this.vec3d;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public BlockPos blockPos2() {
         return this.blockPos2;
      }

      public BlockPos blockPos3() {
         return this.blockPos3;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data13 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.blockPos2, other.blockPos2) && java.util.Objects.equals(this.blockPos3, other.blockPos3) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, type, vec3d, floatValue, blockPos2, blockPos3, floatValue2, floatValue3, intValue);
      }

      @Override
      public String toString() {
         return "Data13[" + "blockPos=" + this.blockPos + ", " + "type=" + this.type + ", " + "vec3d=" + this.vec3d + ", " + "floatValue=" + this.floatValue + ", " + "blockPos2=" + this.blockPos2 + ", " + "blockPos3=" + this.blockPos3 + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "intValue=" + this.intValue + "]";
      }

      private Data13(BlockPos var1, CrystalAuraModule.Type var2, Vec3d var3, float var4, BlockPos var5, BlockPos var6, float var7, float var8, int var9) {
         this.blockPos = var1;
         this.type = var2;
         this.vec3d = var3;
         this.floatValue = var4;
         this.blockPos2 = var5;
         this.blockPos3 = var6;
         this.floatValue2 = var7;
         this.floatValue3 = var8;
         this.intValue = var9;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public CrystalAuraModule.Type getType() {
         return this.type;
      }

      public Vec3d getVec3d() {
         return this.vec3d;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public BlockPos getBlockPos2() {
         return this.blockPos2;
      }

      public BlockPos getBlockPos3() {
         return this.blockPos3;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Data14 {
      private final CrystalAuraModule.Data13 data13;
      private final CrystalAuraModule.Data12 data12;
      private final float floatValue;

      public CrystalAuraModule.Data13 data13() {
         return this.data13;
      }

      public CrystalAuraModule.Data12 data12() {
         return this.data12;
      }

      public float floatValue() {
         return this.floatValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data14 other)) return false;
         return java.util.Objects.equals(this.data13, other.data13) && java.util.Objects.equals(this.data12, other.data12) && java.util.Objects.equals(this.floatValue, other.floatValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data13, data12, floatValue);
      }

      @Override
      public String toString() {
         return "Data14[" + "data13=" + this.data13 + ", " + "data12=" + this.data12 + ", " + "floatValue=" + this.floatValue + "]";
      }

      private Data14(CrystalAuraModule.Data13 var1, CrystalAuraModule.Data12 var2, float var3) {
         this.data13 = var1;
         this.data12 = var2;
         this.floatValue = var3;
      }

      public CrystalAuraModule.Data13 getData13() {
         return this.data13;
      }

      public CrystalAuraModule.Data12 getData12() {
         return this.data12;
      }

      public float value() {
         return this.floatValue;
      }
   }

   private static final class Data2 {
      private final long longValue;
      private final int intValue;

      public long longValue() {
         return this.longValue;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, intValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "longValue=" + this.longValue + ", " + "intValue=" + this.intValue + "]";
      }

      private Data2(long var1, int var3) {
         this.longValue = var1;
         this.intValue = var3;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Data3 {
      private final List<CrystalAuraModule.Data10> items;
      private final Box box;
      private final BlockPos blockPos;
      private final Item item;
      private final boolean flag;
      private final boolean flag2;

      public List<CrystalAuraModule.Data10> items() {
         return this.items;
      }

      public Box box() {
         return this.box;
      }

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public Item item() {
         return this.item;
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
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.box, other.box) && java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.item, other.item) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(items, box, blockPos, item, flag, flag2);
      }

      @Override
      public String toString() {
         return "Data3[" + "items=" + this.items + ", " + "box=" + this.box + ", " + "blockPos=" + this.blockPos + ", " + "item=" + this.item + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + "]";
      }

      private Data3(List<CrystalAuraModule.Data10> var1, Box var2, BlockPos var3, Item var4, boolean var5, boolean var6) {
         this.items = var1;
         this.box = var2;
         this.blockPos = var3;
         this.item = var4;
         this.flag = var5;
         this.flag2 = var6;
      }

      public List<CrystalAuraModule.Data10> getItems() {
         return this.items;
      }

      public Box getBox() {
         return this.box;
      }

      public BlockPos getBlockPos() {
         return this.blockPos;
      }

      public Item getItem() {
         return this.item;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }
   }

   private static final class Data4 {
      private final Rotation rotation;
      private final double doubleValue;
      private final BlockPos blockPos;
      private final Direction direction;

      public Rotation rotation() {
         return this.rotation;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public Direction direction() {
         return this.direction;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.rotation, other.rotation) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.direction, other.direction);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(rotation, doubleValue, blockPos, direction);
      }

      @Override
      public String toString() {
         return "Data4[" + "rotation=" + this.rotation + ", " + "doubleValue=" + this.doubleValue + ", " + "blockPos=" + this.blockPos + ", " + "direction=" + this.direction + "]";
      }

      private Data4(Rotation var1, double var2, BlockPos var4, Direction var5) {
         this.rotation = var1;
         this.doubleValue = var2;
         this.blockPos = var4;
         this.direction = var5;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public Direction getDirection() {
         return this.direction;
      }
   }

   private static final class Data5 {
      private final PlayerEntity playerEntity;
      private final Vec3d vec3d;
      private final Box box;
      private final Box box2;
      private final Box box3;
      private final float floatValue;
      private final boolean flag;
      private final boolean flag2;
      private final int intValue;
      private final float floatValue2;
      private final Box box4;
      private final int intValue2;
      private final boolean flag3;
      private final Box[] boxs;
      private final int intValue3;

      public PlayerEntity playerEntity() {
         return this.playerEntity;
      }

      public Vec3d vec3d() {
         return this.vec3d;
      }

      public Box box() {
         return this.box;
      }

      public Box box2() {
         return this.box2;
      }

      public Box box3() {
         return this.box3;
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

      public int intValue() {
         return this.intValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public Box box4() {
         return this.box4;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public Box[] boxs() {
         return this.boxs;
      }

      public int intValue3() {
         return this.intValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.playerEntity, other.playerEntity) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.box, other.box) && java.util.Objects.equals(this.box2, other.box2) && java.util.Objects.equals(this.box3, other.box3) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.box4, other.box4) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.boxs, other.boxs) && java.util.Objects.equals(this.intValue3, other.intValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(playerEntity, vec3d, box, box2, box3, floatValue, flag, flag2, intValue, floatValue2, box4, intValue2, flag3, boxs, intValue3);
      }

      @Override
      public String toString() {
         return "Data5[" + "playerEntity=" + this.playerEntity + ", " + "vec3d=" + this.vec3d + ", " + "box=" + this.box + ", " + "box2=" + this.box2 + ", " + "box3=" + this.box3 + ", " + "floatValue=" + this.floatValue + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "intValue=" + this.intValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "box4=" + this.box4 + ", " + "intValue2=" + this.intValue2 + ", " + "flag3=" + this.flag3 + ", " + "boxs=" + this.boxs + ", " + "intValue3=" + this.intValue3 + "]";
      }

      private Data5(
         PlayerEntity var1,
         Vec3d var2,
         Box var3,
         Box var4,
         Box var5,
         float var6,
         boolean var7,
         boolean var8,
         int var9,
         float var10,
         Box var11,
         int var12,
         boolean var13,
         Box[] var14,
         int var15
      ) {
         this.playerEntity = var1;
         this.vec3d = var2;
         this.box = var3;
         this.box2 = var4;
         this.box3 = var5;
         this.floatValue = var6;
         this.flag = var7;
         this.flag2 = var8;
         this.intValue = var9;
         this.floatValue2 = var10;
         this.box4 = var11;
         this.intValue2 = var12;
         this.flag3 = var13;
         this.boxs = var14;
         this.intValue3 = var15;
      }

      public PlayerEntity getPlayerEntity() {
         return this.playerEntity;
      }

      public Vec3d getVec3d() {
         return this.vec3d;
      }

      public Box getBox() {
         return this.box;
      }

      public Box getBox2() {
         return this.box2;
      }

      public Box getBox3() {
         return this.box3;
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

      public int getIntValue() {
         return this.intValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public Box getBox4() {
         return this.box4;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public Box[] getBoxs() {
         return this.boxs;
      }

      public int getIntValue3() {
         return this.intValue3;
      }
   }

   private static final class Data6 {
      private final BlockPos blockPos;
      private final long longValue;
      private final boolean flag;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public long longValue() {
         return this.longValue;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data6 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, longValue, flag);
      }

      @Override
      public String toString() {
         return "Data6[" + "blockPos=" + this.blockPos + ", " + "longValue=" + this.longValue + ", " + "flag=" + this.flag + "]";
      }

      private Data6(BlockPos var1, long var2, boolean var4) {
         this.blockPos = var1;
         this.longValue = var2;
         this.flag = var4;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public long time() {
         return this.longValue;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   private static final class Data7 {
      private final long longValue;
      private final BlockState blockState;
      private final BlockState blockState2;

      public long longValue() {
         return this.longValue;
      }

      public BlockState blockState() {
         return this.blockState;
      }

      public BlockState blockState2() {
         return this.blockState2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data7 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.blockState, other.blockState) && java.util.Objects.equals(this.blockState2, other.blockState2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, blockState, blockState2);
      }

      @Override
      public String toString() {
         return "Data7[" + "longValue=" + this.longValue + ", " + "blockState=" + this.blockState + ", " + "blockState2=" + this.blockState2 + "]";
      }

      private Data7(long var1, BlockState var3, BlockState var4) {
         this.longValue = var1;
         this.blockState = var3;
         this.blockState2 = var4;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public BlockState state() {
         return this.blockState;
      }

      public BlockState getBlockState2() {
         return this.blockState2;
      }
   }

   private static final class Data8 {
      private final Vec3d vec3d;
      private final float floatValue;
      private final BlockPos blockPos;
      private final int intValue;

      public Vec3d vec3d() {
         return this.vec3d;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data8 other)) return false;
         return java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(vec3d, floatValue, blockPos, intValue);
      }

      @Override
      public String toString() {
         return "Data8[" + "vec3d=" + this.vec3d + ", " + "floatValue=" + this.floatValue + ", " + "blockPos=" + this.blockPos + ", " + "intValue=" + this.intValue + "]";
      }

      private Data8(Vec3d var1, float var2, BlockPos var3, int var4) {
         this.vec3d = var1;
         this.floatValue = var2;
         this.blockPos = var3;
         this.intValue = var4;
      }

      public Vec3d getVec3d() {
         return this.vec3d;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public BlockPos getBlockPos() {
         return this.blockPos;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Data9 {
      private final Vec3d vec3d;
      private final float floatValue;

      public Vec3d vec3d() {
         return this.vec3d;
      }

      public float floatValue() {
         return this.floatValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data9 other)) return false;
         return java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.floatValue, other.floatValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(vec3d, floatValue);
      }

      @Override
      public String toString() {
         return "Data9[" + "vec3d=" + this.vec3d + ", " + "floatValue=" + this.floatValue + "]";
      }

      private Data9(Vec3d var1, float var2) {
         this.vec3d = var1;
         this.floatValue = var2;
      }

      public Vec3d getVec3d() {
         return this.vec3d;
      }

      public float getFloatValue() {
         return this.floatValue;
      }
   }

   private static final class Entry {
      private final ArrayDeque<Vec3d> arrayDeque = new ArrayDeque<>();
      private int intValue;
      private int intValue2 = Integer.MIN_VALUE;
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      public float floatValue4;
      private int intValue3 = Integer.MIN_VALUE;
      private int intValue4 = Integer.MIN_VALUE;
      private float floatValue5;
      private float floatValue6;
      private int intValue5 = Integer.MIN_VALUE;
      private Box box;
      private Vec3d vec3d;
      private int intValue6;
      private double doubleValue;
      private int intValue7 = Integer.MIN_VALUE;
      private float floatValue7;
      private boolean flag;
      private int intValue8 = Integer.MIN_VALUE;
      private float floatValue8;
      private boolean flag2;
      private boolean flag3;

      private void method5965(int var1, Box var2, Vec3d var3, int var4) {
         this.intValue5 = var1;
         this.box = var2;
         this.doubleValue = var2.minY;
         this.vec3d = var3;
         this.intValue6 = var4;
      }

      private boolean method5966(int var1) {
         if (this.box == null) {
            return false;
         } else if (var1 - this.intValue5 > 18 + this.intValue6) {
            this.box = null;
            return false;
         } else {
            return true;
         }
      }

      private void setIntValue3(int var1) {
         this.intValue3 = var1;
      }

      private void method5967(int var1, float var2) {
         this.intValue7 = var1;
         this.floatValue7 = var2;
      }

      private void method5968(int var1, float var2) {
         if (!(var2 <= 0.0F)) {
            if (this.method5969(var1)) {
               this.floatValue8 = Math.max(this.floatValue8, var2);
            } else {
               this.intValue8 = var1;
               this.floatValue8 = var2;
               this.flag2 = true;
               this.flag3 = false;
            }
         }
      }

      private boolean method5969(int var1) {
         return this.intValue8 != Integer.MIN_VALUE && var1 - this.intValue8 >= 0 && var1 - this.intValue8 < 10;
      }

      private void method5970(PlayerEntity var1, Vec3d var2, int var3, CrystalAuraModule.Data8 var4, Class35 var5, Boolean var6, int var7) {
         this.arrayDeque.addLast(var2);

         while (this.arrayDeque.size() > 3) {
            this.arrayDeque.removeFirst();
         }

         float var8 = var1.getAbsorptionAmount();
         if (var8 >= 15.9F && this.floatValue6 < 15.9F) {
            this.intValue4 = var3 + 6000;
         }

         this.floatValue6 = var8;
         float var9 = var1.getHealth() + var1.getAbsorptionAmount();
         if (var1.timeUntilRegen > this.intValue) {
            this.intValue2 = var3;
            this.floatValue = this.floatValue2;
            this.floatValue3 = 0.0F;
            this.flag = var6 == Boolean.TRUE || var6 == null && var4 != null && var3 - var4.getIntValue() <= 20;
            int var10 = var3 - var7;
            if (this.intValue8 == Integer.MIN_VALUE || Math.abs(var10 - this.intValue8) > 2) {
               this.intValue8 = var10;
               this.floatValue8 = 0.0F;
               this.flag2 = false;
            }

            this.flag3 = true;
            if (var6 != Boolean.TRUE && this.intValue7 != Integer.MIN_VALUE && var3 - this.intValue7 <= 12) {
               this.floatValue3 = this.floatValue7;
            } else if (var6 != Boolean.FALSE && var4 != null && var3 - var4.getIntValue() <= 20) {
               this.floatValue3 = var5.method5454(var4.getVec3d(), var4.getFloatValue(), var1, var1.getBoundingBox(), var4.getBlockPos(), null);
            }

            this.floatValue4 = var4 != null && var3 - var4.getIntValue() <= 2 ? this.floatValue3 : 0.0F;
         }

         if (this.intValue2 != Integer.MIN_VALUE && var3 - this.intValue2 <= 3) {
            float var11 = this.floatValue - var9;
            if (this.flag) {
               if (var11 > 0.05F && this.intValue3 < this.intValue2 && var1.getHealth() > 0.0F) {
                  this.floatValue3 = Math.max(this.floatValue4, var5.method5458(var1, var11));
               }
            } else {
               this.floatValue3 = Math.max(this.floatValue3, var11);
            }

            if (!this.flag2) {
               this.floatValue8 = this.floatValue3;
            }
         }

         if (this.flag2 && !this.flag3 && this.intValue8 != Integer.MIN_VALUE && var3 - var7 - this.intValue8 > 3) {
            this.intValue8 = Integer.MIN_VALUE;
            this.flag2 = false;
         }

         this.intValue = var1.timeUntilRegen;
         this.floatValue5 = this.floatValue2 > 0.0F ? Math.max(0.0F, this.floatValue2 - var9) : 0.0F;
         this.floatValue2 = var9;
      }

      private Vec3d method5971() {
         if (this.arrayDeque.size() < 2) {
            return Vec3d.ZERO;
         } else {
            Vec3d var1 = this.arrayDeque.getFirst();
            Vec3d var2 = this.arrayDeque.getLast();
            return var2.subtract(var1).multiply(1.0 / (this.arrayDeque.size() - 1));
         }
      }
   }

   private static enum Type {
      BREAK,
      CRYSTAL,
      OBSIDIAN,
      DIG,
      SHIELD,
      ANCHOR_PLACE,
      ANCHOR_CHARGE,
      ANCHOR_EXPLODE;
   }

   private static enum Type2 {
      CRYSTALS,
      ANCHOR;
   }
}
