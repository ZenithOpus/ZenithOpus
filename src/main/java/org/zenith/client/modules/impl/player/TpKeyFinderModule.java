package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.zenith.ChatLogger;
import org.zenith.MinecraftAccess;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.SpawnTracker;

@ModuleInfo(
   name = "TpKeyFinder",
   category = ModuleCategory.PLAYER,
   description = "module.tpkeyfinder.description"
)
public final class TpKeyFinderModule extends Module {
   public static final TpKeyFinderModule INSTANCE = new TpKeyFinderModule();
   private static final double CONST_doubleValue = -266.0;
   private static final double CONST_doubleValue2 = 134.0;
   private static final double CONST_doubleValue3 = 80.0;
   private static final double CONST_doubleValue4 = 36.0;
   private static final int CONST_intValue = 512;
   private static final int CONST_intValue2 = 4096;
   private static final int CONST_intValue3 = 60;
   private static final int CONST_intValue4 = 2;
   private static final int CONST_intValue5 = 40;
   private static final int CONST_intValue6 = 3;
   private static final int CONST_intValue7 = 40;
   private static final int CONST_intValue8 = 3;
   private static final int CONST_intValue9 = 64;
   private static final int CONST_intValue10 = 256;
   private static final int CONST_intValue11 = 256;
   private static final int CONST_intValue12 = 10;
   private static final long CONST_longValue = 1000L;
   private static final int[] CONST_intValues = new int[]{1, 0, -1, 0};
   private static final int[] CONST_intValues2 = new int[]{0, 1, 0, -1};
   private static final Pattern pattern = Pattern.compile("лечен\\S{0,8}[^0-9]{0,40}(\\d+)\\s*сек");
   private final BooleanSetting autoHeal = new BooleanSetting("module.tpKeyFinder.autoHeal", "module.tpKeyFinder.autoHeal.desc", true);
   private final SliderSetting healHp = new SliderSetting(
      "module.tpKeyFinder.healHp", 10.0F, 0.0F, 20.0F, 1.0F, "module.tpKeyFinder.healHp.desc", "", this.autoHeal::isEnabled
   );
   private final SliderSetting healFood = new SliderSetting(
      "module.tpKeyFinder.healFood", 8.0F, 0.0F, 20.0F, 1.0F, "module.tpKeyFinder.healFood.desc", "", this.autoHeal::isEnabled
   );
   private final List<TpKeyFinderModule.Entry> items = new ArrayList<>();
   private final Set<BlockPos> items2 = new HashSet<>();
   private final Set<UUID> items3 = new HashSet<>();
   private final LinkedHashSet<BlockPos> linkedHashSet = new LinkedHashSet<>();
   private final LinkedHashSet<UUID> linkedHashSet2 = new LinkedHashSet<>();
   private final LinkedHashMap<Object, Integer> linkedHashMap = new LinkedHashMap<>();
   private ClientWorld clientWorld;
   private ClientPlayerEntity player;
   private int ticks;
   private TpKeyFinderModule.Entry entry;
   private Vec3d vec3d;
   private boolean flag;
   private boolean flag2;
   private int intValue;
   private int intValue2;
   private GenericContainerScreenHandler genericContainerScreenHandler;
   private int intValue3;
   private ItemStack itemStack = ItemStack.EMPTY;
   private int intValue4 = -1;
   private int intValue5;
   private boolean flag3;
   private int intValue6;
   private int intValue7;
   private int intValue8 = -1;
   private int intValue9;
   private int intValue10;
   private boolean flag4;
   private Iterator<Map.Entry<BlockPos, BlockEntity>> iterator;
   private Iterator<Entity> iterator2;
   private boolean flag5;
   private double doubleValue;
   private double doubleValue2;
   private int intValue11;
   private int intValue12 = 1;
   private int intValue13 = 1;
   private int intValue14;
   private volatile long longValue;

   private TpKeyFinderModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.isEnabled()) {
         this.resetAll();
         if (mc.player != null && !mc.player.getAbilities().flying) {
            ChatLogger.method2021("TpKeyFinder: включи /fly — без полёта модуль ждёт");
         }
      }
   }

   @Override
   public void onDisable() {
      this.resetAll();
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.clientWorld != mc.world || this.player != mc.player) {
         this.resetAll();
         this.clientWorld = mc.world;
         this.player = mc.player;
      }

      if (mc.interactionManager != null && !mc.isPaused() && mc.player.isAlive()) {
         this.ticks++;
         if (!mc.player.getAbilities().flying) {
            this.method4332();
         } else {
            this.method4338();
            this.method4324();
            if (this.genericContainerScreenHandler != null && !this.method4341()) {
               this.method4331(false, false);
            }

            if (!this.method4342()) {
               this.method4332();
            } else if (this.method4328() && mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
               mc.player.setPitch(90.0F);
               mc.player.lastPitch = 90.0F;
               this.method4335();
               if (!SpawnTracker.isActive()) {
                  if (this.flag) {
                     this.method4323();
                  } else {
                     if (this.entry == null) {
                        this.entry = this.method4334();
                     }

                     if (this.entry != null) {
                        this.method4327();
                     } else if (this.flag4) {
                        this.method4330();
                     }
                  }
               }
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         if (this.autoHeal.isEnabled() && var1.getPacket3() instanceof GameMessageS2CPacket var4) {
            int var6 = method4339(var4.content().getString());
            if (var6 > 0) {
               this.longValue = Math.max(this.longValue, System.currentTimeMillis() + var6 * 1000L + 500L);
            }
         }
      } else if (mc.isOnThread() && !this.flag3) {
         if (this.genericContainerScreenHandler == null
            || !(var1.getPacket3() instanceof ClickSlotC2SPacket var2 && var2.syncId() != this.genericContainerScreenHandler.syncId)
               && !(var1.getPacket3() instanceof CloseHandledScreenC2SPacket)) {
            if (this.flag && (var1.getPacket3() instanceof PlayerInteractBlockC2SPacket || var1.getPacket3() instanceof PlayerInteractEntityC2SPacket)) {
               this.method4331(true, false);
            }
         } else {
            this.method4331(false, false);
         }
      }
   }

   private void method4323() {
      if (this.entry != null && this.entry.method4346() != null) {
         if (this.method4341()) {
            this.method4325();
         } else if (!this.flag2) {
            if (mc.player.isSneaking() || ++this.intValue <= 2) {
               return;
            }

            if (this.entry.open()) {
               this.flag2 = true;
               this.intValue2++;
               this.intValue = 0;
            } else if (this.intValue >= 42) {
               this.method4331(false, false);
            }
         } else if (++this.intValue >= 40) {
            if (this.intValue2 < 3) {
               this.flag2 = false;
               this.intValue = 0;
            } else {
               this.method4331(false, false);
            }
         }
      } else {
         this.method4331(true, false);
      }
   }

   private void method4324() {
      if (this.flag2 && this.genericContainerScreenHandler == null && this.entry != null) {
         if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var1 && this.entry.method4348() && this.entry.method4349(var1)) {
            this.genericContainerScreenHandler = var1;
            this.intValue4 = -1;
            this.intValue = 0;
         }
      }
   }

   private void method4325() {
      if (this.intValue4 == -1) {
         if (this.intValue3 > 0) {
            this.intValue3--;
         } else {
            int var9 = this.genericContainerScreenHandler.getInventory().size();
            boolean var2 = false;

            for (int var3 = 0; var3 < var9; var3++) {
               Slot var4 = this.genericContainerScreenHandler.getSlot(var3);
               ItemStack var5 = var4.getStack();
               if (method4340(var5) != null) {
                  var2 = true;
                  if (var4.canTakeItems(mc.player) && this.method4326(var5, var9)) {
                     this.itemStack = var5.copy();
                     this.intValue5 = var5.getCount();
                     this.intValue4 = var3;
                     this.intValue = 0;
                     this.flag3 = true;

                     try {
                        mc.interactionManager.clickSlot(this.genericContainerScreenHandler.syncId, var3, 0, SlotActionType.QUICK_MOVE, mc.player);
                     } finally {
                        this.flag3 = false;
                     }

                     return;
                  }
               }
            }

            if (var2) {
               ChatLogger.method2021("TpKeyFinder: нет места в инвентаре — сундук оставлен открытым");
            }

            this.method4331(true, !var2);
         }
      } else {
         ItemStack var1 = this.genericContainerScreenHandler.getSlot(this.intValue4).getStack();
         if (var1.isEmpty() || !ItemStack.areItemsAndComponentsEqual(var1, this.itemStack) || var1.getCount() < this.intValue5) {
            ChatLogger.method2019("Успешно подобрано: " + method4340(this.itemStack));
            this.intValue4 = -1;
            this.intValue3 = 3;
         } else if (++this.intValue >= 40) {
            this.method4331(true, false);
         }
      }
   }

   private boolean method4326(ItemStack var1, int var2) {
      for (int var3 = var2; var3 < this.genericContainerScreenHandler.slots.size(); var3++) {
         Slot var4 = this.genericContainerScreenHandler.getSlot(var3);
         if (var4.inventory == mc.player.getInventory() && var4.canInsert(var1)) {
            ItemStack var5 = var4.getStack();
            if (var5.isEmpty()
               || ItemStack.areItemsAndComponentsEqual(var5, var1) && var5.getCount() < Math.min(var5.getMaxCount(), var4.getMaxItemCount(var5))) {
               return true;
            }
         }
      }

      return false;
   }

   private void method4327() {
      Vec3d var1 = this.entry.method4347();
      if (var1 == null) {
         this.method4331(false, false);
      } else if (SpawnTracker.method6210(var1) && SpawnTracker.method6211(this, var1)) {
         this.vec3d = var1;
         this.flag = true;
         this.flag2 = false;
         this.intValue = this.intValue2 = 0;
      } else if (Math.hypot(var1.x - mc.player.getX(), var1.z - mc.player.getZ()) > 80.0) {
         this.method4329(var1.x, var1.z);
      } else {
         this.method4331(false, false);
      }
   }

   private boolean method4328() {
      if (this.vec3d == null) {
         return true;
      } else if (SpawnTracker.isActive()) {
         if (SpawnTracker.method6207(this)) {
            return true;
         } else {
            this.method4333("маршрут перехватил другой модуль");
            return false;
         }
      } else if (mc.player.squaredDistanceTo(this.vec3d) > 1.0) {
         this.method4333("телепорт сорвался");
         return false;
      } else {
         this.vec3d = null;
         return true;
      }
   }

   private boolean method4329(double var1, double var3) {
      if (!SpawnTracker.method6213(this, var1, var3, true)) {
         return false;
      } else {
         this.vec3d = new Vec3d(var1, -66.0, var3);
         return true;
      }
   }

   private void method4330() {
      if (!this.flag5) {
         this.doubleValue = mc.player.getX();
         this.doubleValue2 = mc.player.getZ();
         this.flag5 = true;
      }

      if (Math.hypot(mc.player.getX() - this.doubleValue, mc.player.getZ() - this.doubleValue2) > 0.5) {
         this.method4329(this.doubleValue, this.doubleValue2);
      } else {
         double var1 = this.doubleValue + CONST_intValues[this.intValue11] * 80.0;
         double var3 = this.doubleValue2 + CONST_intValues2[this.intValue11] * 80.0;
         if (this.method4329(var1, var3)) {
            this.doubleValue = var1;
            this.doubleValue2 = var3;
            if (--this.intValue13 <= 0) {
               this.intValue11 = this.intValue11 + 1 & 3;
               if (++this.intValue14 == 2) {
                  this.intValue14 = 0;
                  this.intValue12++;
               }

               this.intValue13 = this.intValue12;
            }
         }
      }
   }

   private void method4331(boolean var1, boolean var2) {
      if (var2 && this.method4341() && this.genericContainerScreenHandler.getCursorStack().isEmpty()) {
         this.flag3 = true;

         try {
            mc.player.closeHandledScreen();
         } finally {
            this.flag3 = false;
         }
      }

      if (this.entry != null) {
         Object var3 = this.entry.key();
         if (var1) {
            this.linkedHashMap.remove(var3);
         } else {
            this.linkedHashMap.put(var3, this.ticks + 60);
            if (this.linkedHashMap.size() > 4096) {
               this.linkedHashMap.remove(this.linkedHashMap.keySet().iterator().next());
            }
         }

         if (this.entry.blockPos != null) {
            if (var1) {
               method4344(this.linkedHashSet, this.entry.blockPos);
            }

            this.items2.remove(this.entry.blockPos);
         } else {
            if (var1) {
               method4344(this.linkedHashSet2, this.entry.uUID);
            }

            this.items3.remove(this.entry.uUID);
         }
      }

      SpawnTracker.method6209(this);
      this.vec3d = null;
      this.entry = null;
      this.genericContainerScreenHandler = null;
      this.flag = this.flag2 = false;
      this.intValue = this.intValue3 = this.intValue2 = 0;
      this.intValue4 = -1;
      this.itemStack = ItemStack.EMPTY;
   }

   private void method4332() {
      if (this.vec3d != null) {
         SpawnTracker.method6209(this);
         this.vec3d = null;
         this.flag = this.flag2 = false;
         this.intValue = this.intValue2 = 0;
      }
   }

   private void method4333(String var1) {
      ChatLogger.method2021("TpKeyFinder: " + var1);
      this.setToggled(false);
   }

   private TpKeyFinderModule.Entry method4334() {
      TpKeyFinderModule.Entry var1 = null;
      double var2 = Double.MAX_VALUE;

      for (int var4 = this.items.size() - 1; var4 >= 0; var4--) {
         TpKeyFinderModule.Entry var5 = this.items.get(var4);
         if (this.linkedHashMap.getOrDefault(var5.key(), 0) <= this.ticks) {
            Vec3d var6 = var5.method4346();
            if (var6 != null && method4343(var6.y)) {
               double var7 = mc.player.squaredDistanceTo(var6);
               if (var7 < var2) {
                  var2 = var7;
                  var1 = var5;
               }
            } else {
               this.items.remove(var4);
               var5.method4345();
            }
         }
      }

      if (var1 != null) {
         this.items.remove(var1);
      }

      return var1;
   }

   private void method4335() {
      int var1 = MathHelper.clamp(mc.options.getClampedViewDistance(), 2, 32);
      int var2 = MathHelper.floor(mc.player.getX()) >> 4;
      int var3 = MathHelper.floor(mc.player.getZ()) >> 4;
      if (var2 != this.intValue6 || var3 != this.intValue7 || var1 != this.intValue8) {
         this.intValue6 = var2;
         this.intValue7 = var3;
         this.intValue8 = var1;
         this.intValue9 = this.intValue10 = 0;
         this.flag4 = false;
         this.iterator = null;
         this.iterator2 = null;
         double var4 = (var1 + 6) * 16;
         this.items.removeIf(var2x -> {
            Vec3d var3x = var2x.method4346();
            if (var3x != null && Math.abs(var3x.x - mc.player.getX()) <= var4 && Math.abs(var3x.z - mc.player.getZ()) <= var4) {
               return false;
            } else {
               var2x.method4345();
               return true;
            }
         });
      }

      if (this.intValue10 <= 0 || --this.intValue10 <= 0) {
         int var13 = var1 * 2 + 1;
         int var5 = 0;
         int var6 = 0;

         while (var6 < 256 && var5 < 64 && this.intValue9 < var13 * var13) {
            int var7 = var2 - var1 + this.intValue9 % var13;
            int var8 = var3 - var1 + this.intValue9 / var13;
            WorldChunk var9 = mc.world.getChunkManager().getChunk(var7, var8, ChunkStatus.FULL, false);
            var5++;
            if (var9 == null) {
               this.iterator = null;
               this.intValue9++;
            } else {
               if (this.iterator == null) {
                  this.iterator = new ArrayList<>(var9.getBlockEntities().entrySet()).iterator();
               }

               while (this.iterator.hasNext() && var6 < 256) {
                  Map.Entry var10 = this.iterator.next();
                  var6++;
                  BlockPos var11 = (BlockPos)var10.getKey();
                  if (var10.getValue() instanceof ChestBlockEntity
                     && method4343(var11.getY())
                     && this.items.size() < 512
                     && !this.linkedHashSet.contains(var11)
                     && !this.items2.contains(var11)
                     && method4337(var11)) {
                     BlockPos var12 = var11.toImmutable();
                     this.items2.add(var12);
                     this.items.add(new TpKeyFinderModule.Entry(var12, null));
                  }
               }

               if (this.iterator.hasNext()) {
                  break;
               }

               this.iterator = null;
               this.intValue9++;
            }
         }

         this.method4336();
         if (this.intValue9 >= var13 * var13) {
            this.flag4 = true;
            this.intValue9 = 0;
            this.intValue10 = 10;
         }
      }
   }

   private void method4336() {
      if (this.iterator2 == null) {
         ArrayList var1 = new ArrayList();
         mc.world.getEntities().forEach(var1::add);
         this.iterator2 = var1.iterator();
      }

      for (int var4 = 0; var4 < 256 && this.iterator2.hasNext(); var4++) {
         if (this.iterator2.next() instanceof ChestMinecartEntity var2
            && var2.isAlive()
            && !var2.isRemoved()
            && mc.world.getEntityById(var2.getId()) == var2
            && method4343(var2.getY())
            && this.items.size() < 512
            && !this.linkedHashSet2.contains(var2.getUuid())
            && this.items3.add(var2.getUuid())) {
            this.items.add(new TpKeyFinderModule.Entry(null, var2));
         }
      }

      if (!this.iterator2.hasNext()) {
         this.iterator2 = null;
      }
   }

   private static boolean method4337(BlockPos var0) {
      Mutable var1 = new Mutable();

      for (int var2 = -3; var2 <= 3; var2++) {
         for (int var3 = -3; var3 <= 3; var3++) {
            WorldChunk var4 = mc.world.getChunkManager().getChunk(var0.getX() + var2 >> 4, var0.getZ() + var3 >> 4, ChunkStatus.FULL, false);
            if (var4 != null) {
               for (int var5 = -1; var5 <= 1; var5++) {
                  var1.set(var0.getX() + var2, var0.getY() + var5, var0.getZ() + var3);
                  if (var4.getBlockEntities().get(var1) instanceof MobSpawnerBlockEntity) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private void method4338() {
      if (this.autoHeal.isEnabled() && System.currentTimeMillis() >= this.longValue && mc.getNetworkHandler() != null) {
         float var1 = MathHelper.clamp(this.healHp.getValue3(), 0.0F, 20.0F);
         float var2 = MathHelper.clamp(this.healFood.getValue3(), 0.0F, 20.0F);
         if (var1 > 0.0F && mc.player.getHealth() < var1 || var2 > 0.0F && mc.player.getHungerManager().getFoodLevel() < var2) {
            mc.getNetworkHandler().sendChatCommand("heal");
            this.longValue = System.currentTimeMillis() + 1000L;
         }
      }
   }

   private static int method4339(String var0) {
      String var1 = var0.replaceAll("§.", "").trim().toLowerCase(Locale.ROOT);
      if (var1.contains("лечен") && (var1.contains("ошибка") || var1.contains("следующ") || var1.contains("секунд"))) {
         Matcher var2 = pattern.matcher(var1);
         if (!var2.find()) {
            return 60;
         } else {
            try {
               return Math.clamp(Long.parseLong(var2.group(1)), 1, 86400);
            } catch (NumberFormatException var4) {
               return 86400;
            }
         }
      } else {
         return 0;
      }
   }

   private static String method4340(ItemStack var0) {
      if (var0.isEmpty()) {
         return null;
      } else if (var0.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
         return "зачарованное золотое яблоко";
      } else if (var0.isOf(Items.GOLDEN_APPLE)) {
         return "золотое яблоко";
      } else {
         String var1 = Formatting.strip(var0.getName().getString());
         if (var1 != null && var1.toLowerCase(Locale.ROOT).contains("ключ-карта")) {
            return "ключ-карта";
         } else {
            ItemEnchantmentsComponent var2 = var0.get(DataComponentTypes.STORED_ENCHANTMENTS);
            return var2 != null && var2.getEnchantments().stream().anyMatch(var0x -> var0x.matchesKey(Enchantments.MENDING)) ? "книга на починку" : null;
         }
      }
   }

   private boolean method4341() {
      return this.genericContainerScreenHandler != null && mc.player != null && mc.player.currentScreenHandler == this.genericContainerScreenHandler;
   }

   private boolean method4342() {
      if (mc.player.currentScreenHandler != mc.player.playerScreenHandler && !this.method4341()) {
         return false;
      } else {
         return mc.currentScreen != null && !(mc.currentScreen instanceof GameMenuScreen)
            ? this.method4341() && mc.currentScreen instanceof HandledScreen var1 && var1.getScreenHandler() == this.genericContainerScreenHandler
            : true;
      }
   }

   private static boolean method4343(double var0) {
      return var0 >= -266.0 && var0 <= 134.0;
   }

   private static <T> void method4344(LinkedHashSet<T> var0, T var1) {
      var0.add(var1);
      if (var0.size() > 4096) {
         var0.removeFirst();
      }
   }

   private void resetAll() {
      SpawnTracker.method6209(this);
      this.method4331(false, false);
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.linkedHashSet.clear();
      this.linkedHashSet2.clear();
      this.linkedHashMap.clear();
      this.ticks = 0;
      this.clientWorld = null;
      this.player = null;
      this.vec3d = null;
      this.flag5 = false;
      this.doubleValue = this.doubleValue2 = 0.0;
      this.intValue11 = this.intValue14 = 0;
      this.intValue12 = this.intValue13 = 1;
      this.intValue8 = -1;
      this.flag4 = false;
      this.iterator = null;
      this.iterator2 = null;
   }

   private final class Entry {
      private final BlockPos blockPos;
      private final ChestMinecartEntity chestMinecartEntity;
      private final UUID uUID;
      private int intValue;

      private Entry(BlockPos var2, ChestMinecartEntity var3) {
         this.blockPos = var2;
         this.chestMinecartEntity = var3;
         this.uUID = var3 == null ? null : var3.getUuid();
      }

      private Object key() {
         return this.blockPos != null ? this.blockPos : this.uUID;
      }

      private void method4345() {
         if (this.blockPos != null) {
            TpKeyFinderModule.this.items2.remove(this.blockPos);
         } else {
            TpKeyFinderModule.this.items3.remove(this.uUID);
         }
      }

      private Vec3d method4346() {
         if (this.blockPos != null) {
            return this.method4351(this.blockPos) != null ? Vec3d.ofBottomCenter(this.blockPos).add(0.0, 1.0, 0.0) : null;
         } else {
            return this.chestMinecartEntity.isAlive()
                  && !this.chestMinecartEntity.isRemoved()
                  && MinecraftAccess.mc.world.getEntityById(this.chestMinecartEntity.getId()) == this.chestMinecartEntity
               ? this.chestMinecartEntity.getEntityPos()
               : null;
         }
      }

      private Vec3d method4347() {
         Vec3d var1 = this.method4346();
         return var1 == null ? null : SpawnTracker.method6215(var1);
      }

      private boolean method4348() {
         Vec3d var1 = this.method4346();
         return var1 != null && MinecraftAccess.mc.player.squaredDistanceTo(var1) <= 36.0;
      }

      private boolean method4349(GenericContainerScreenHandler var1) {
         return this.intValue > 0 && this.method4346() != null && var1.getInventory().size() == this.intValue;
      }

      private boolean open() {
         if (this.method4348()
            && TpKeyFinderModule.this.method4342()
            && MinecraftAccess.mc.player.currentScreenHandler == MinecraftAccess.mc.player.playerScreenHandler) {
            TpKeyFinderModule.this.flag3 = true;

            try {
               if (this.blockPos != null) {
                  Inventory var1 = this.method4350();
                  if (var1 == null || !MinecraftAccess.mc.player.canInteractWithBlockAt(this.blockPos, 0.0)) {
                     return false;
                  }

                  BlockHitResult var2 = MinecraftAccess.mc
                     .world
                     .raycast(
                        new RaycastContext(
                           MinecraftAccess.mc.player.getEyePos(),
                           Vec3d.ofCenter(this.blockPos),
                           ShapeType.OUTLINE,
                           FluidHandling.NONE,
                           MinecraftAccess.mc.player
                        )
                     );
                  if (var2.getType() != Type.BLOCK || !var2.getBlockPos().equals(this.blockPos)) {
                     return false;
                  }

                  this.intValue = var1.size();
                  MinecraftAccess.mc.interactionManager.interactBlock(MinecraftAccess.mc.player, Hand.MAIN_HAND, var2);
               } else {
                  if (!MinecraftAccess.mc.player.canInteractWithEntity(this.chestMinecartEntity, 0.0)) {
                     return false;
                  }

                  this.intValue = this.chestMinecartEntity.size();
                  MinecraftAccess.mc.interactionManager.interactEntity(MinecraftAccess.mc.player, this.chestMinecartEntity, Hand.MAIN_HAND);
               }

               return true;
            } finally {
               TpKeyFinderModule.this.flag3 = false;
            }
         } else {
            return false;
         }
      }

      private Inventory method4350() {
         if (this.method4351(this.blockPos) == null) {
            return null;
         } else {
            BlockState var1 = MinecraftAccess.mc.world.getBlockState(this.blockPos);
            if (!(var1.getBlock() instanceof ChestBlock var2)) {
               return null;
            } else {
               if (var1.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
                  BlockPos var5 = this.blockPos.offset(ChestBlock.getFacing(var1));
                  if (this.method4351(var5) == null) {
                     return null;
                  }

                  BlockState var4 = MinecraftAccess.mc.world.getBlockState(var5);
                  if (var4.getBlock() != var2
                     || var4.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE
                     || var4.get(ChestBlock.CHEST_TYPE) == var1.get(ChestBlock.CHEST_TYPE)
                     || var4.get(ChestBlock.FACING) != var1.get(ChestBlock.FACING)
                     || !var5.offset(ChestBlock.getFacing(var4)).equals(this.blockPos)) {
                     return null;
                  }
               }

               return ChestBlock.getInventory(var2, var1, MinecraftAccess.mc.world, this.blockPos, false);
            }
         }
      }

      private ChestBlockEntity method4351(BlockPos var1) {
         WorldChunk var2 = MinecraftAccess.mc.world.getChunkManager().getChunk(var1.getX() >> 4, var1.getZ() >> 4, ChunkStatus.FULL, false);
         if (var2 == null) {
            return null;
         } else {
            BlockState var3 = var2.getBlockState(var1);
            return var3.getBlock() instanceof ChestBlock
                  && var2.getBlockEntities().get(var1) instanceof ChestBlockEntity var4
                  && !var4.isRemoved()
                  && var4.getWorld() == MinecraftAccess.mc.world
                  && var4.supports(var3)
               ? var4
               : null;
         }
      }
   }
}
