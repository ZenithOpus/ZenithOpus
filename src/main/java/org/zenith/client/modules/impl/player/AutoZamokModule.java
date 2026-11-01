package org.zenith.client.modules.impl.player;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.pathing.goals.GoalNear;
import baritone.api.pathing.goals.GoalXZ;
import com.darkmagician6.eventapi.EventTarget;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.chunk.WorldChunk;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.ChatReceiveEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.SettingsBackup;
import org.zenith.utility.misc.Util17;

@ModuleInfo(
   name = "AutoZamok",
   category = ModuleCategory.PLAYER,
   description = "module.autozamok.description"
)
public class AutoZamokModule extends Module {
   public static final AutoZamokModule INSTANCE = new AutoZamokModule();
   private static final double[][] CONST_doubleValues = new double[][]{
      {-12.5, 64.0, 0.4},
      {-5.5, 64.0, -5.0},
      {-0.5, 64.0, -12.5},
      {6.5, 64.0, -5.5},
      {6.3, 64.0, 6.7},
      {6.5, 83.0, 6.4},
      {15.6, 79.0, 15.6},
      {6.5, 83.0, -5.5},
      {15.6, 79.0, -14.7},
      {-5.8, 83.0, 6.6},
      {-14.5, 79.0, 15.6},
      {13.2, 64.0, 0.5},
      {-5.6, 64.0, 6.7},
      {-14.3, 79.0, -14.5},
      {-5.5, 83.0, -5.4},
      {-19.5, 54.0, 6.5},
      {-12.0, 55.0, 34.0},
      {13.0, 57.0, 26.5},
      {-2.6, 57.0, 8.7},
      {-15.3, 64.0, -15.4},
      {16.4, 64.0, -15.3}
   };
   private static final BlockPos blockPos = new BlockPos(10, 64, 10);
   private static final String[] texts = new String[]{"инвиз", "невид", "invis"};
   private static final String[] texts2 = new String[]{"кирк", "кирки", "pick"};
   private static final String[] texts3 = new String[]{"склад", "store", "sklad"};
   private static final long CONST_longValue = 70L;
   private static final long CONST_longValue2 = 4000L;
   private static final long CONST_longValue3 = 8000L;
   private static final long CONST_longValue4 = 6000L;
   private static final long CONST_longValue5 = 100L;
   private static final long CONST_longValue6 = 200L;
   private static final long CONST_longValue7 = 500L;
   private static final long CONST_longValue8 = 12000L;
   private static final long CONST_longValue9 = 15000L;
   private static final long CONST_longValue10 = 60000L;
   private static final long CONST_longValue11 = 15000L;
   private static final double CONST_doubleValue = 144.0;
   private static final long CONST_longValue12 = 300000L;
   private static final long CONST_longValue13 = 20000L;
   private static final int CONST_intValue = 3;
   private static final double CONST_doubleValue2 = 2.1;
   private static final long CONST_longValue14 = 20000L;
   private static final int CONST_intValue2 = 10;
   private static final int CONST_intValue3 = 2;
   private static final int CONST_intValue4 = 200;
   private static final int CONST_intValue5 = 17;
   private static final long CONST_longValue15 = 20000L;
   private static final double CONST_doubleValue3 = 30.0;
   private static final long CONST_longValue16 = 50000L;
   private static final long CONST_longValue17 = 12000L;
   private static final long CONST_longValue18 = 120L;
   private static final long CONST_longValue19 = 180L;
   private static final float CONST_floatValue = 1.35F;
   private static final double CONST_doubleValue4 = 35.0;
   private static final long CONST_longValue20 = 3500L;
   private static final long CONST_longValue21 = 20000L;
   private final SliderSetting boxesPerTrip = new SliderSetting(
      "module.autoZamok.boxesPerTrip", 1.0F, 1.0F, 9.0F, 1.0F, "module.autoZamok.boxesPerTrip.desc", "шт"
   );
   private AutoZamokModule.Type type = AutoZamokModule.Type.TP_TO_SKLAD;
   private long longValue;
   private long longValue2;
   private BlockPos blockPos2;
   private BlockPos blockPos3;
   private Vec3d vec3d;
   private int intValue;
   private long longValue3;
   private long longValue4;
   private int intValue2;
   private int intValue3;
   private int intValue4;
   private int intValue5;
   private int intValue6;
   private int intValue7;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private Vec3d vec3d2;
   private long longValue5;
   private long longValue6;
   private float floatValue;
   private boolean flag4;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private BlockPos blockPos4;
   private long longValue7;
   private boolean flag5;
   private float floatValue2;
   private long longValue8;
   private BlockPos blockPos5;
   private Vec3d vec3d3;
   private long longValue9;
   private long longValue10;
   private final Map<BlockPos, Long> items = new HashMap<>();
   private final Map<BlockPos, Long> items2 = new HashMap<>();
   private int intValue8;
   private int intValue9;
   private boolean flag6;
   private volatile BlockPos blockPos6;
   private volatile long longValue11;
   private BlockPos blockPos7;
   private int intValue10;
   private int intValue11;
   private int intValue12;
   private BlockPos blockPos8;
   private long longValue12;
   private long longValue13;
   private BlockPos blockPos9;
   private long longValue14;
   private long longValue15;
   private long longValue16;
   private volatile long longValue17;
   private volatile int intValue13;
   private volatile long longValue18;
   private volatile boolean flag7;
   private boolean flag8;
   private boolean flag9;
   private long longValue19;

   @Override
   public void onEnable() {
      super.onEnable();
      this.method4206(AutoZamokModule.Type.TP_TO_SKLAD);
      this.longValue4 = 0L;
      this.longValue17 = 0L;
      this.floatValue = 0.0F;
      this.flag4 = false;
      this.flag8 = false;
      this.flag9 = false;
      this.intValue10 = 0;
      this.vec3d3 = null;
      this.blockPos6 = null;
      this.blockPos7 = null;
      this.items2.clear();
      this.method4199();
      Util17.method3334(true);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      Util17.method3334(false);
      this.method4204();
      this.method4200();
      this.method4210();
      this.method4132();
      this.method4209();
      if (mc.options != null) {
         mc.options.jumpKey.setPressed(false);
         if (this.flag4) {
            mc.options.sprintKey.setPressed(false);
            this.flag4 = false;
         }
      }

      if (mc.getNetworkHandler() != null) {
         this.method4159();
      }

      this.flag5 = false;
      this.method4165();
      this.method4140();
      this.blockPos2 = null;
      this.blockPos3 = null;
      this.vec3d = null;
      this.blockPos4 = null;
      this.items.clear();
      this.items2.clear();
      this.blockPos6 = null;
      this.blockPos7 = null;
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null && mc.getNetworkHandler() != null && mc.interactionManager != null) {
         long var2 = System.currentTimeMillis();
         if (this.type != AutoZamokModule.Type.DRINK_INVIS && !this.flag8 && !this.flag9) {
            this.method4209();
         }

         if (this.flag5 && this.type != AutoZamokModule.Type.BREAK_SHULKER) {
            this.method4159();
         }

         if (this.flag3 && this.type != AutoZamokModule.Type.COLLECT && this.type != AutoZamokModule.Type.GO_EXIT) {
            this.method4165();
         }

         if (this.method4207()) {
            this.method4210();
            this.method4132();
            if (var2 - this.longValue10 >= 1000L) {
               this.longValue10 = var2;
               mc.player.requestRespawn();
               if (mc.currentScreen instanceof DeathScreen) {
                  mc.setScreen(null);
               }
            }

            if (this.type != AutoZamokModule.Type.TP_TO_SKLAD && this.type != AutoZamokModule.Type.WAIT_SKLAD) {
               this.log("Смерть — возвращаемся за снаряжением");
               this.method4204();
               this.method4206(AutoZamokModule.Type.TP_TO_SKLAD);
            }
         } else {
            float var4 = mc.player.getHealth();
            if (this.floatValue > 0.0F && var4 < this.floatValue - 0.01F && this.method4149()) {
               PlayerEntity var5 = this.method4153(12.0);
               if (var5 != null) {
                  this.floatValue = var4;
                  this.method4150(var2, var5);
                  return;
               }
            }

            this.floatValue = var4;
            if (!this.method4131(var2) && !this.method4135(var2)) {
               this.method4142();
               if (this.method4139(var2)) {
                  this.longValue9 = var2;
               } else {
                  this.method4143(var2);
                  switch (this.type) {
                     case TP_TO_SKLAD:
                        if (this.method4197(var2) && this.method4194(var2, "home sklad")) {
                           this.log("/home sklad");
                           this.method4206(AutoZamokModule.Type.WAIT_SKLAD);
                        }
                        break;
                     case WAIT_SKLAD:
                        this.method4195(var2, AutoZamokModule.Type.REFILL_INVIS, AutoZamokModule.Type.TP_TO_SKLAD, true);
                        break;
                     case REFILL_INVIS:
                        this.method4129(var2, texts, AutoZamokModule.Type.REFILL_PICK, true);
                        break;
                     case REFILL_PICK:
                        this.method4129(var2, texts2, AutoZamokModule.Type.DRINK_INVIS, false);
                        break;
                     case DRINK_INVIS:
                        this.method4130(var2);
                        break;
                     case TP_TO_ZAMOK:
                        this.closeScreen();
                        if (this.method4197(var2) && this.method4194(var2, "home zamok")) {
                           this.log("/home zamok");
                           this.method4206(AutoZamokModule.Type.WAIT_ZAMOK);
                        }
                        break;
                     case WAIT_ZAMOK:
                        this.method4195(var2, AutoZamokModule.Type.GO_ENTRY, AutoZamokModule.Type.TP_TO_ZAMOK, false);
                        break;
                     case GO_ENTRY:
                        this.method4136(var2);
                        break;
                     case GO_SHULKER:
                        this.method4138(var2);
                        break;
                     case BREAK_SHULKER:
                        this.method4156(var2);
                        break;
                     case COLLECT:
                        this.method4164(var2);
                        break;
                     case GO_EXIT:
                        this.method4155(var2);
                        break;
                     case FLEE:
                        this.method4152(var2);
                        break;
                     case TP_TO_DEPOSIT:
                        mc.interactionManager.cancelBlockBreaking();
                        if (this.method4197(var2) && this.method4194(var2, "home sklad")) {
                           this.log("/home sklad (сдача)");
                           this.method4206(AutoZamokModule.Type.WAIT_DEPOSIT);
                        }
                        break;
                     case WAIT_DEPOSIT:
                        this.method4195(var2, AutoZamokModule.Type.DEPOSIT, AutoZamokModule.Type.TP_TO_DEPOSIT, true);
                        break;
                     case DEPOSIT:
                        this.method4171(var2);
                  }

                  if (this.method4191()) {
                     this.method4192(var2);
                  }
               }
            } else {
               this.longValue9 = var2;
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100() && var1.getPacket3() instanceof ExplosionS2CPacket var2) {
         BlockPos var4 = BlockPos.ofFloored(var2.center());
         if (method4128(var4)) {
            this.blockPos6 = var4;
            this.longValue11 = System.currentTimeMillis();
            this.longValue14 = 0L;
         }
      }
   }

   private static boolean method4128(BlockPos var0) {
      for (double[] var4 : CONST_doubleValues) {
         if (var0.getSquaredDistance(var4[0], var4[1], var4[2]) <= 144.0) {
            return true;
         }
      }

      return false;
   }

   @EventTarget
   public void onChatReceive(ChatReceiveEvent var1) {
      if (var1.getText() != null && this.method4196()) {
         String var2 = var1.getText().getString().toLowerCase(Locale.ROOT);
         long var3 = System.currentTimeMillis();
         if (!var2.contains("времени до следующей телепортаци") && (!var2.contains("ошибк") || !var2.contains("телепортаци"))) {
            if (!var2.contains("не двигайтесь") && !var2.contains("начнется через") && !var2.contains("начнётся через")) {
               if (var2.contains("телепортирование") || var2.contains("вы телепортированы")) {
                  this.intValue13 = 1;
                  this.longValue18 = var3;
               }
            } else {
               this.flag7 = true;
            }
         } else {
            long var5 = method4198(var2);
            this.longValue17 = var3 + (var5 > 0L ? var5 * 1000L : 1500L) + 700L;
            this.intValue13 = -1;
            this.longValue18 = var3;
         }
      }
   }

   private void method4129(long var1, String[] var3, AutoZamokModule.Type var4, boolean var5) {
      if (var5 ? !this.method4187() : !this.method4183()) {
         if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var7)) {
            if (this.blockPos2 == null) {
               this.blockPos2 = this.method4177(var3, var1, false);
               if (this.blockPos2 == null) {
                  if (var1 - this.longValue > 6000L) {
                     this.log("Не найден сундук с табличкой " + var3[0]);
                     this.method4206(var4);
                  }

                  return;
               }
            }

            if (var1 - this.longValue2 >= 70L) {
               this.method4181(this.blockPos2);
               this.longValue2 = var1;
            }
         } else if (var1 - this.longValue2 >= 70L) {
            Slot var8 = this.method4182(var7, var5 ? this::method4185 : this::method4184);
            if (var8 != null) {
               mc.interactionManager.clickSlot(var7.syncId, var8.id, 0, SlotActionType.QUICK_MOVE, mc.player);
               this.longValue2 = var1;
               this.log("Взял предмет из сундука " + var3[0]);
               if (var5 && !this.method4187() && !this.method4168()) {
                  return;
               }
            } else if (!var5 || this.method4169(this::method4185) == 0) {
               var8 = this.method4182(var7, var0 -> true);
               if (var8 != null) {
                  mc.interactionManager.clickSlot(var7.syncId, var8.id, 0, SlotActionType.QUICK_MOVE, mc.player);
                  this.longValue2 = var1;
                  this.log("Взял предмет из сундука " + var3[0]);
               }
            }

            this.closeScreen();
            this.method4206(var4);
         }
      } else {
         this.closeScreen();
         this.method4206(var4);
      }
   }

   private void method4130(long var1) {
      if (mc.player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
         this.method4209();
         this.method4206(AutoZamokModule.Type.TP_TO_ZAMOK);
      } else {
         int var3 = this.method4188(this::method4185);
         if (var3 == -1 || var1 - this.longValue > 5000L) {
            this.method4209();
            this.method4206(AutoZamokModule.Type.TP_TO_ZAMOK);
         } else if (var3 > 8) {
            this.method4190(var3, var1);
         } else {
            this.method4211(var3);
            this.method4208();
         }
      }
   }

   private boolean method4131(long var1) {
      if (!this.method4134() || this.flag8) {
         this.method4132();
         return false;
      } else if (!this.flag9 && !this.method4133()) {
         return false;
      } else if (!this.flag9 || (this.method4133() || mc.player.isUsingItem()) && var1 - this.longValue19 <= 8000L) {
         int var3 = this.method4188(this::method4185);
         if (var3 == -1) {
            this.method4132();
            return false;
         } else {
            if (!this.flag9) {
               this.flag9 = true;
               this.longValue19 = var1;
               this.log("Обновляю невидимость");
            }

            this.method4204();
            if (var3 > 8) {
               this.method4190(var3, var1);
               return true;
            } else {
               this.method4211(var3);
               this.method4208();
               return true;
            }
         }
      } else {
         this.method4132();
         return false;
      }
   }

   private void method4132() {
      if (this.flag9) {
         this.method4209();
         this.flag9 = false;
      }
   }

   private boolean method4133() {
      StatusEffectInstance var1 = mc.player.getStatusEffect(StatusEffects.INVISIBILITY);
      return var1 == null ? true : !var1.isInfinite() && var1.getDuration() <= 200;
   }

   private boolean method4134() {
      return this.type == AutoZamokModule.Type.GO_ENTRY
         || this.type == AutoZamokModule.Type.GO_SHULKER
         || this.type == AutoZamokModule.Type.BREAK_SHULKER
         || this.type == AutoZamokModule.Type.COLLECT
         || this.type == AutoZamokModule.Type.GO_EXIT;
   }

   private boolean method4135(long var1) {
      if (!this.method4134()) {
         this.method4210();
         return false;
      } else {
         boolean var3 = mc.player.getHungerManager().getFoodLevel() <= 17;
         if (!this.flag8 && !var3) {
            return false;
         } else if (this.flag8 && !var3 && !mc.player.isUsingItem()) {
            this.method4210();
            return false;
         } else {
            int var4 = this.method4188(this::method4186);
            if (var4 == -1) {
               this.method4210();
               return false;
            } else {
               this.flag8 = true;
               this.method4204();
               if (var4 > 8) {
                  this.method4190(var4, var1);
                  return true;
               } else {
                  this.method4211(var4);
                  this.method4208();
                  return true;
               }
            }
         }
      }
   }

   private void method4136(long var1) {
      if (!(mc.player.getEntityPos().squaredDistanceTo(Vec3d.ofCenter(blockPos)) <= 9.0)
         && (var1 - this.longValue <= 2000L || this.method4173(var1) == null)
         && var1 - this.longValue <= 20000L) {
         if (var1 - this.longValue > 1000L) {
            this.method4201(blockPos, var1);
         }
      } else {
         this.method4137(var1);
      }
   }

   private void method4137(long var1) {
      this.intValue = 0;
      this.longValue3 = var1;
      this.blockPos4 = null;
      this.flag = false;
      this.flag2 = false;
      this.method4206(AutoZamokModule.Type.GO_SHULKER);
   }

   private void method4138(long var1) {
      BlockPos var3 = this.method4173(var1);
      if (var3 != null) {
         this.flag = false;
         if (!var3.equals(this.blockPos4)) {
            this.blockPos4 = var3;
            this.longValue7 = var1;
         }

         if (this.method4176(var3) <= 4.41) {
            this.method4204();
            this.blockPos3 = var3;
            this.longValue13 = 0L;
            this.flag5 = false;
            this.floatValue2 = 0.0F;
            this.longValue8 = 0L;
            this.method4160();
            this.intValue11 = this.method4169(null);
            this.intValue12 = this.method4169(this::method4170);
            this.type = AutoZamokModule.Type.BREAK_SHULKER;
            this.longValue = var1;
         } else if (var1 - this.longValue7 > 15000L) {
            this.log("Бокс недоступен, пропускаю");
            this.items.put(var3, var1);
            this.blockPos4 = null;
            this.longValue14 = 0L;
            this.method4204();
         } else {
            this.method4202(var3, var1);
         }
      } else {
         this.blockPos4 = null;
         BlockPos var4 = this.blockPos6;
         if (var4 != null) {
            if (var1 - this.longValue11 <= 15000L && !(mc.player.getEntityPos().squaredDistanceTo(Vec3d.ofCenter(var4)) <= 9.0)) {
               if (!var4.equals(this.blockPos7)) {
                  this.blockPos7 = var4;
                  this.log("Взрыв у точки спавна — иду туда");
               }

               this.longValue3 = var1;
               this.method4202(var4, var1);
               return;
            }

            this.blockPos6 = null;
         }

         if (this.flag2) {
            int var5 = this.method4148();
            if (var5 == -1) {
               this.longValue9 = var1;
               if (var1 - this.longValue3 > 50000L) {
                  this.longValue3 = var1;
                  this.flag = false;
                  this.method4145(var1);
                  if (this.type == AutoZamokModule.Type.GO_SHULKER) {
                     this.intValue = 0;
                     this.flag2 = false;
                     this.log("Иду на новый обход");
                  }

                  return;
               }

               if (!this.flag) {
                  this.method4146();
                  this.log("Боксов нет — жду спавн в укрытии");
               }

               double var11 = mc.player.getX() - (this.intValue6 + 0.5);
               double var8 = mc.player.getZ() - (this.intValue7 + 0.5);
               if (var11 * var11 + var8 * var8 <= 9.0) {
                  this.method4204();
               } else {
                  this.method4203(this.intValue6, this.intValue7, var1);
               }

               return;
            }

            if (var5 != this.intValue) {
               this.intValue = var5;
               this.longValue3 = var1;
            }
         }

         this.flag = false;
         double[] var10 = CONST_doubleValues[this.intValue];
         BlockPos var6 = BlockPos.ofFloored(var10[0], var10[1], var10[2]);
         if (!(mc.player.getEntityPos().squaredDistanceTo(Vec3d.ofCenter(var6)) <= 9.0) && var1 - this.longValue3 <= 12000L) {
            this.method4201(var6, var1);
         } else {
            this.method4144(var1);
         }
      }
   }

   private boolean method4139(long var1) {
      boolean var3 = this.type == AutoZamokModule.Type.GO_ENTRY
         || this.type == AutoZamokModule.Type.GO_SHULKER
         || this.type == AutoZamokModule.Type.GO_EXIT
         || this.type == AutoZamokModule.Type.FLEE;
      if (!var3) {
         this.method4140();
         return false;
      } else {
         boolean var4 = this.method4141();
         if (this.vec3d2 == null) {
            if (!var4 || this.blockPos8 == null) {
               return false;
            }

            this.vec3d2 = new Vec3d(this.blockPos8.getX() + 0.5, mc.player.getY(), this.blockPos8.getZ() + 0.5);
            this.longValue5 = var1;
            this.method4204();
         } else if (!var4 && var1 - this.longValue6 > 700L) {
            this.method4140();
            return false;
         }

         if (var4) {
            this.longValue6 = var1;
         }

         if (var1 - this.longValue5 <= 20000L) {
            Util17.method3336(this.vec3d2);
            mc.options.jumpKey.setPressed(true);
            return true;
         } else {
            this.log("Застрял в паутине — повторяю заход");
            this.method4140();
            this.method4204();
            this.method4206(
               this.type != AutoZamokModule.Type.GO_EXIT && this.type != AutoZamokModule.Type.FLEE
                  ? AutoZamokModule.Type.TP_TO_ZAMOK
                  : AutoZamokModule.Type.TP_TO_DEPOSIT
            );
            return false;
         }
      }
   }

   private void method4140() {
      if (this.vec3d2 != null) {
         this.vec3d2 = null;
         Util17.method3337();
         mc.options.jumpKey.setPressed(false);
      }
   }

   private boolean method4141() {
      Box var1 = mc.player.getBoundingBox().expand(0.05);

      for (BlockPos var3 : BlockPos.iterate(
         MathHelper.floor(var1.minX),
         MathHelper.floor(var1.minY),
         MathHelper.floor(var1.minZ),
         MathHelper.floor(var1.maxX),
         MathHelper.floor(var1.maxY),
         MathHelper.floor(var1.maxZ)
      )) {
         if (mc.world.getBlockState(var3).getBlock() == Blocks.COBWEB) {
            return true;
         }
      }

      return false;
   }

   private void method4142() {
      boolean var1 = mc.player.isTouchingWater() && this.method4205();
      if (var1) {
         mc.options.sprintKey.setPressed(true);
         this.flag4 = true;
      } else if (this.flag4) {
         mc.options.sprintKey.setPressed(false);
         this.flag4 = false;
      }
   }

   private void method4143(long var1) {
      boolean var3 = this.type == AutoZamokModule.Type.GO_ENTRY || this.type == AutoZamokModule.Type.GO_SHULKER || this.type == AutoZamokModule.Type.GO_EXIT;
      Vec3d var4 = mc.player.getEntityPos();
      if (var3 && this.vec3d3 != null && !(var4.squaredDistanceTo(this.vec3d3) > 0.25)) {
         if (var1 - this.longValue9 > 20000L) {
            this.log("Застрял — повторяю заход на замок");
            if (this.blockPos4 != null) {
               this.items.put(this.blockPos4, var1);
               this.blockPos4 = null;
            }

            this.vec3d3 = null;
            this.longValue9 = var1;
            this.method4204();
            this.method4206(this.type == AutoZamokModule.Type.GO_EXIT ? AutoZamokModule.Type.TP_TO_DEPOSIT : AutoZamokModule.Type.TP_TO_ZAMOK);
         }
      } else {
         this.vec3d3 = var4;
         this.longValue9 = var1;
      }
   }

   private void method4144(long var1) {
      this.method4204();
      this.longValue3 = var1;
      if (++this.intValue >= CONST_doubleValues.length) {
         this.intValue = 0;
         this.flag2 = true;
         this.method4145(var1);
      }
   }

   private void method4145(long var1) {
      if (this.intValue10 <= 0 && !this.method4167()) {
         this.items.clear();
      } else {
         this.log("Обход завершён, боксов выбито: " + this.intValue10);
         this.method4154(var1);
      }
   }

   private void method4146() {
      ThreadLocalRandom var1 = ThreadLocalRandom.current();
      double var2 = -1.0;

      for (int var4 = 0; var4 < 8; var4++) {
         double var5 = var1.nextDouble(Math.PI * 2);
         double var7 = blockPos.getX() + 0.5 + Math.cos(var5) * 35.0;
         double var9 = blockPos.getZ() + 0.5 + Math.sin(var5) * 35.0;
         double var11 = Double.MAX_VALUE;

         for (double[] var16 : CONST_doubleValues) {
            double var17 = var7 - var16[0];
            double var19 = var9 - var16[2];
            var11 = Math.min(var11, var17 * var17 + var19 * var19);
         }

         if (var11 > var2) {
            var2 = var11;
            this.intValue6 = MathHelper.floor(var7);
            this.intValue7 = MathHelper.floor(var9);
         }
      }

      this.flag = true;
   }

   private boolean method4147(int var1) {
      double[] var2 = CONST_doubleValues[var1];
      return mc.world.isChunkLoaded(MathHelper.floor(var2[0]) >> 4, MathHelper.floor(var2[2]) >> 4);
   }

   private int method4148() {
      for (int var1 = 0; var1 < CONST_doubleValues.length; var1++) {
         int var2 = (this.intValue + var1) % CONST_doubleValues.length;
         if (!this.method4147(var2)) {
            return var2;
         }
      }

      return -1;
   }

   private boolean method4149() {
      return switch (this.type) {
         case TP_TO_SKLAD, WAIT_SKLAD, TP_TO_ZAMOK, WAIT_ZAMOK, GO_ENTRY, GO_SHULKER, BREAK_SHULKER, COLLECT, GO_EXIT, FLEE, TP_TO_DEPOSIT, WAIT_DEPOSIT -> true;
         default -> false;
      };
   }

   private void method4150(long var1, PlayerEntity var3) {
      this.method4210();
      this.method4132();
      this.method4159();
      if (mc.options != null) {
         mc.options.jumpKey.setPressed(false);
      }

      this.blockPos6 = null;
      this.method4151(var3);
      if (this.type != AutoZamokModule.Type.FLEE) {
         this.log("Получил урон от игрока — убегаю");
         this.method4206(AutoZamokModule.Type.FLEE);
      } else {
         this.longValue = var1;
      }
   }

   private void method4151(PlayerEntity var1) {
      Vec3d var2 = mc.player.getEntityPos();
      Vec3d var3 = new Vec3d(var2.x - var1.getX(), 0.0, var2.z - var1.getZ());
      if (var3.lengthSquared() < 0.01) {
         var3 = new Vec3d(1.0, 0.0, 0.0);
      }

      var3 = var3.normalize();
      this.intValue4 = MathHelper.floor(var2.x + var3.x * 25.0);
      this.intValue5 = MathHelper.floor(var2.z + var3.z * 25.0);
   }

   private void method4152(long var1) {
      PlayerEntity var3 = this.method4153(20.0);
      if (var3 != null && var1 - this.longValue <= 12000L) {
         double var4 = mc.player.getX() - this.intValue4;
         double var6 = mc.player.getZ() - this.intValue5;
         if (var4 * var4 + var6 * var6 <= 9.0) {
            this.method4151(var3);
         }

         this.method4203(this.intValue4, this.intValue5, var1);
      } else {
         this.method4204();
         this.method4206(AutoZamokModule.Type.TP_TO_DEPOSIT);
      }
   }

   private PlayerEntity method4153(double var1) {
      PlayerEntity var3 = null;
      double var4 = var1 * var1;

      for (PlayerEntity var7 : mc.world.getPlayers()) {
         if (var7 != mc.player && !var7.isSpectator()) {
            double var8 = var7.squaredDistanceTo(mc.player);
            if (var8 < var4) {
               var4 = var8;
               var3 = var7;
            }
         }
      }

      return var3;
   }

   private void method4154(long var1) {
      Vec3d var3 = mc.player.getEntityPos();
      Vec3d var4 = Vec3d.ofCenter(blockPos);
      Vec3d var5 = new Vec3d(var3.x - var4.x, 0.0, var3.z - var4.z);
      if (var5.lengthSquared() < 1.0) {
         var5 = new Vec3d(1.0, 0.0, 0.0);
      }

      var5 = var5.normalize();
      this.intValue2 = MathHelper.floor(var4.x + var5.x * 30.0);
      this.intValue3 = MathHelper.floor(var4.z + var5.z * 30.0);
      this.log("Ухожу из замка перед телепортом");
      this.method4206(AutoZamokModule.Type.GO_EXIT);
   }

   private void method4155(long var1) {
      double var3 = mc.player.getX() - this.intValue2;
      double var5 = mc.player.getZ() - this.intValue3;
      if (!(var3 * var3 + var5 * var5 <= 16.0) && var1 - this.longValue <= 20000L) {
         this.method4203(this.intValue2, this.intValue3, var1);
         if (!this.method4205()) {
            this.flag3 = true;
            Util17.method3336(new Vec3d(this.intValue2 + 0.5, mc.player.getY(), this.intValue3 + 0.5));
         } else if (this.flag3) {
            this.method4165();
         }
      } else {
         this.method4204();
         this.method4165();
         this.method4206(AutoZamokModule.Type.TP_TO_DEPOSIT);
      }
   }

   private void method4156(long var1) {
      BlockPos var3 = this.blockPos3;
      if (var3 == null) {
         this.method4159();
         this.method4206(AutoZamokModule.Type.COLLECT);
      } else {
         boolean var4 = var1 - this.longValue >= 20000L;
         if (this.method4175(var3)) {
            this.longValue13 = 0L;
            double var5 = 2.6;
            if (this.method4176(var3) > var5 * var5) {
               this.method4159();
               this.method4206(AutoZamokModule.Type.GO_SHULKER);
               return;
            }

            if (!this.method4189(var1)) {
               return;
            }

            this.method4162(this.method4161(var3));
            if (!this.flag5) {
               if (var1 >= this.longValue8) {
                  this.method4157(var3);
               }
            } else {
               this.floatValue2 = this.floatValue2 + mc.world.getBlockState(var3).calcBlockBreakingDelta(mc.player, mc.world, var3);
               mc.player.swingHand(Hand.MAIN_HAND);
               mc.world.setBlockBreakingInfo(mc.player.getId(), var3, MathHelper.clamp((int)(this.floatValue2 * 10.0F), 0, 9));
               if (this.floatValue2 >= 1.35F) {
                  this.method4158(var1);
               }
            }
         } else {
            this.flag5 = false;
            if (this.longValue13 == 0L) {
               this.longValue13 = var1;
            }

            if (var1 - this.longValue13 >= 100L) {
               this.vec3d = Vec3d.ofCenter(var3);
               this.method4206(AutoZamokModule.Type.COLLECT);
               return;
            }
         }

         if (var4) {
            this.method4159();
            if (this.method4175(var3)) {
               this.log("Бокс не поддаётся — пропускаю");
               this.items.put(var3, var1);
               this.blockPos4 = null;
               this.longValue14 = 0L;
               this.method4206(AutoZamokModule.Type.GO_SHULKER);
            } else {
               this.vec3d = Vec3d.ofCenter(var3);
               this.method4206(AutoZamokModule.Type.COLLECT);
            }
         }
      }
   }

   private void method4157(BlockPos var1) {
      Direction var2 = this.method4163(var1);
      mc.interactionManager.sendSequencedPacket(mc.world, var2x -> new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var1, var2, var2x));
      this.flag5 = true;
      this.blockPos5 = var1;
      this.floatValue2 = 0.0F;
      mc.player.swingHand(Hand.MAIN_HAND);
   }

   private void method4158(long var1) {
      BlockPos var3 = this.blockPos5;
      Direction var4 = this.method4163(var3);
      mc.interactionManager.sendSequencedPacket(mc.world, var2 -> new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, var3, var4, var2));
      mc.world.setBlockBreakingInfo(mc.player.getId(), var3, -1);
      this.flag5 = false;
      this.longValue8 = var1 + 120L + ThreadLocalRandom.current().nextLong(180L);
      this.method4160();
   }

   private void method4159() {
      if (this.flag5) {
         mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, this.blockPos5, Direction.DOWN));
         mc.world.setBlockBreakingInfo(mc.player.getId(), this.blockPos5, -1);
         this.flag5 = false;
      }
   }

   private void method4160() {
      ThreadLocalRandom var1 = ThreadLocalRandom.current();
      this.doubleValue = var1.nextDouble(-0.18, 0.18);
      this.doubleValue2 = var1.nextDouble(-0.18, 0.18);
      this.doubleValue3 = var1.nextDouble(-0.18, 0.18);
   }

   private Vec3d method4161(BlockPos var1) {
      return new Vec3d(var1.getX() + 0.5 + this.doubleValue, var1.getY() + 0.5 + this.doubleValue2, var1.getZ() + 0.5 + this.doubleValue3);
   }

   private void method4162(Vec3d var1) {
      Util17.method3329(var1);
   }

   private Direction method4163(BlockPos var1) {
      Vec3d var2 = mc.player.getEyePos().subtract(Vec3d.ofCenter(var1));
      double var3 = Math.abs(var2.x);
      double var5 = Math.abs(var2.y);
      double var7 = Math.abs(var2.z);
      if (var3 >= var5 && var3 >= var7) {
         return var2.x >= 0.0 ? Direction.EAST : Direction.WEST;
      } else if (var7 >= var3 && var7 >= var5) {
         return var2.z >= 0.0 ? Direction.SOUTH : Direction.NORTH;
      } else {
         return var2.y >= 0.0 ? Direction.UP : Direction.DOWN;
      }
   }

   private void method4164(long var1) {
      this.method4204();
      boolean var3 = this.method4169(this::method4170) > this.intValue12 || this.method4169(null) > this.intValue11;
      if (var3) {
         this.intValue10++;
         this.log("Бокс выбит (" + this.intValue10 + "/" + (int)this.boxesPerTrip.getValue3() + ")");
         this.method4166(var1, true);
      } else if (var1 - this.longValue >= 3500L) {
         this.log("Бокс достался не нам, продолжаю обход");
         this.method4166(var1, false);
      } else if (this.vec3d != null) {
         double var4 = this.vec3d.x - mc.player.getX();
         double var6 = this.vec3d.z - mc.player.getZ();
         if (var4 * var4 + var6 * var6 > 0.64) {
            this.flag3 = true;
            Util17.method3336(this.vec3d);
         } else {
            this.method4165();
         }
      }
   }

   private void method4165() {
      if (this.flag3) {
         this.flag3 = false;
         Util17.method3337();
      }
   }

   private void method4166(long var1, boolean var3) {
      this.method4165();
      if ((!var3 || this.intValue10 < (int)this.boxesPerTrip.getValue3()) && !this.method4168() && !this.method4167()) {
         this.longValue3 = var1;
         this.blockPos4 = null;
         this.method4206(AutoZamokModule.Type.GO_SHULKER);
      } else {
         this.method4154(var1);
      }
   }

   private boolean method4167() {
      return this.method4188(this::method4184) == -1 ? true : this.method4133() && this.method4188(this::method4185) == -1;
   }

   private boolean method4168() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (mc.player.getInventory().getStack(var1).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private int method4169(Predicate<ItemStack> var1) {
      int var2 = 0;
      int var3 = mc.player.getInventory().size();

      for (int var4 = 0; var4 < var3; var4++) {
         ItemStack var5 = mc.player.getInventory().getStack(var4);
         if (!var5.isEmpty() && (var1 == null || var1.test(var5))) {
            var2 += var5.getCount();
         }
      }

      return var2;
   }

   private boolean method4170(ItemStack var1) {
      return var1 != null && !var1.isEmpty() && Registries.ITEM.getId(var1.getItem()).getPath().endsWith("shulker_box");
   }

   private void method4171(long var1) {
      if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var4) {
         if (var1 - this.longValue2 >= 70L) {
            int var5 = this.method4169(null);
            if (this.flag6) {
               this.flag6 = false;
               if (var5 == this.intValue9) {
                  if (++this.intValue8 >= 2) {
                     this.log("Сундук забит — ищу другой");
                     this.method4180(this.blockPos2, var1);
                     this.closeScreen();
                     this.method4206(AutoZamokModule.Type.DEPOSIT);
                     return;
                  }
               } else {
                  this.intValue8 = 0;
               }
            }

            int var6 = var4.getInventory().size();

            for (Slot var8 : var4.slots) {
               if (var8.id >= var6) {
                  ItemStack var9 = var8.getStack();
                  if (!var9.isEmpty() && !this.method4184(var9) && !this.method4185(var9) && !this.method4186(var9)) {
                     mc.interactionManager.clickSlot(var4.syncId, var8.id, 0, SlotActionType.QUICK_MOVE, mc.player);
                     this.longValue2 = var1;
                     this.intValue9 = var5;
                     this.flag6 = true;
                     return;
                  }
               }
            }

            this.log("Ресурсы сложены");
            this.closeScreen();
            this.method4172();
         }
      } else {
         if (this.blockPos2 == null) {
            this.blockPos2 = this.method4177(texts3, var1, true);
            if (this.blockPos2 == null) {
               if (var1 - this.longValue > 6000L) {
                  this.log(this.items2.isEmpty() ? "Не найден сундук 'склад'" : "Все сундуки 'склад' забиты");
                  this.method4172();
               }

               return;
            }
         }

         if (var1 - this.longValue2 >= 70L) {
            this.method4181(this.blockPos2);
            this.longValue2 = var1;
         }
      }
   }

   private void method4172() {
      this.closeScreen();
      this.intValue10 = 0;
      this.items.clear();
      this.method4206(AutoZamokModule.Type.REFILL_INVIS);
   }

   private BlockPos method4173(long var1) {
      if (var1 - this.longValue14 >= 200L) {
         this.longValue14 = var1;
         this.items.values().removeIf(var2 -> var1 - var2 > 60000L);
         this.blockPos9 = this.method4174();
      }

      return this.blockPos9;
   }

   private BlockPos method4174() {
      byte var1 = 3;
      double var2 = var1 * 1.7321 + 1.0;
      Vec3d var4 = mc.player.getEyePos();
      Mutable var5 = new Mutable();
      BlockPos var6 = null;
      double var7 = Double.MAX_VALUE;

      for (double[] var12 : CONST_doubleValues) {
         double var13 = Math.sqrt(var4.squaredDistanceTo(var12[0], var12[1], var12[2])) - var2;
         if (!(var13 > 0.0) || !(var13 * var13 >= var7)) {
            int var15 = MathHelper.floor(var12[0]);
            int var16 = MathHelper.floor(var12[1]);
            int var17 = MathHelper.floor(var12[2]);

            for (int var18 = -var1; var18 <= var1; var18++) {
               for (int var19 = -var1; var19 <= var1; var19++) {
                  for (int var20 = -var1; var20 <= var1; var20++) {
                     var5.set(var15 + var18, var16 + var19, var17 + var20);
                     if (mc.world.getBlockState(var5).getBlock() instanceof ShulkerBoxBlock && !this.items.containsKey(var5)) {
                        double var21 = var4.squaredDistanceTo(var5.getX() + 0.5, var5.getY() + 0.5, var5.getZ() + 0.5);
                        if (var21 < var7) {
                           var7 = var21;
                           var6 = var5.toImmutable();
                        }
                     }
                  }
               }
            }
         }
      }

      return var6;
   }

   private boolean method4175(BlockPos var1) {
      return mc.world.getBlockState(var1).getBlock() instanceof ShulkerBoxBlock;
   }

   private double method4176(BlockPos var1) {
      Vec3d var2 = mc.player.getEyePos();
      double var3 = Math.max(Math.max(var1.getX() - var2.x, 0.0), var2.x - (var1.getX() + 1));
      double var5 = Math.max(Math.max(var1.getY() - var2.y, 0.0), var2.y - (var1.getY() + 1));
      double var7 = Math.max(Math.max(var1.getZ() - var2.z, 0.0), var2.z - (var1.getZ() + 1));
      return var3 * var3 + var5 * var5 + var7 * var7;
   }

   private BlockPos method4177(String[] var1, long var2, boolean var4) {
      if (var2 - this.longValue15 < 500L) {
         return null;
      } else {
         this.longValue15 = var2;
         this.items2.values().removeIf(var2x -> var2 - var2x > 300000L);
         byte var5 = 10;
         double var6 = var5 * var5;
         BlockPos var8 = mc.player.getBlockPos();
         BlockPos var9 = null;
         double var10 = Double.MAX_VALUE;
         int var12 = Integer.MAX_VALUE;
         int var13 = (var5 >> 4) + 1;
         int var14 = mc.player.getChunkPos().x;
         int var15 = mc.player.getChunkPos().z;

         for (int var16 = var14 - var13; var16 <= var14 + var13; var16++) {
            for (int var17 = var15 - var13; var17 <= var15 + var13; var17++) {
               if (mc.world.isChunkLoaded(var16, var17)) {
                  WorldChunk var18 = mc.world.getChunk(var16, var17);
                  if (var18 != null) {
                     for (BlockEntity var20 : var18.getBlockEntities().values()) {
                        if (var20 instanceof SignBlockEntity var21 && !(var8.getSquaredDistance(var21.getPos()) > var6)) {
                           int var22 = this.method4178(var21, var1);
                           if (var22 >= 0) {
                              if (!var4) {
                                 var22 = 0;
                              }

                              BlockPos var23 = this.method4179(var21.getPos());
                              if (var23 != null && !this.items2.containsKey(var23)) {
                                 double var24 = var8.getSquaredDistance(var23);
                                 if (var22 < var12 || var22 == var12 && var24 < var10) {
                                    var12 = var22;
                                    var10 = var24;
                                    var9 = var23;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return var9;
      }
   }

   private int method4178(SignBlockEntity var1, String[] var2) {
      StringBuilder var3 = new StringBuilder();

      for (int var4 = 0; var4 < 4; var4++) {
         var3.append(var1.getFrontText().getMessage(var4, false).getString()).append(' ');
         var3.append(var1.getBackText().getMessage(var4, false).getString()).append(' ');
      }

      String var13 = var3.toString().toLowerCase(Locale.ROOT);

      for (String var8 : var2) {
         int var9 = var13.indexOf(var8);
         if (var9 != -1) {
            int var10 = var9 + var8.length();

            while (var10 < var13.length() && !Character.isDigit(var13.charAt(var10)) && !Character.isLetter(var13.charAt(var10))) {
               var10++;
            }

            int var11 = 0;

            boolean var12;
            for (var12 = false; var10 < var13.length() && Character.isDigit(var13.charAt(var10)); var10++) {
               var11 = var11 * 10 + (var13.charAt(var10) - '0');
               var12 = true;
            }

            return var12 ? var11 : 1;
         }
      }

      return -1;
   }

   private BlockPos method4179(BlockPos var1) {
      BlockState var2 = mc.world.getBlockState(var1);
      if (var2.getBlock() instanceof WallSignBlock) {
         BlockPos var3 = var1.offset(var2.get(WallSignBlock.FACING).getOpposite());
         if (mc.world.getBlockState(var3).getBlock() instanceof ChestBlock) {
            return var3;
         }
      } else if (var2.getBlock() instanceof SignBlock) {
         BlockPos var9 = var1.down();
         if (mc.world.getBlockState(var9).getBlock() instanceof ChestBlock) {
            return var9;
         }
      }

      if (var2.getBlock() instanceof ChestBlock) {
         return var1.toImmutable();
      } else {
         for (Direction var6 : Direction.values()) {
            BlockPos var7 = var1.offset(var6);
            if (mc.world.getBlockState(var7).getBlock() instanceof ChestBlock) {
               return var7;
            }

            BlockPos var8 = var7.down();
            if (mc.world.getBlockState(var8).getBlock() instanceof ChestBlock) {
               return var8;
            }
         }

         return null;
      }
   }

   private void method4180(BlockPos var1, long var2) {
      this.items2.put(var1, var2);
      BlockState var4 = mc.world.getBlockState(var1);
      if (var4.getBlock() instanceof ChestBlock && var4.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
         this.items2.put(var1.offset(ChestBlock.getFacing(var4)), var2);
      }
   }

   private void method4181(BlockPos var1) {
      Vec3d var2 = Vec3d.ofCenter(var1);
      this.method4212(var2);
      mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(var2, Direction.UP, var1, false));
      mc.player.swingHand(Hand.MAIN_HAND);
   }

   private Slot method4182(GenericContainerScreenHandler var1, Predicate<ItemStack> var2) {
      int var3 = var1.getInventory().size();

      for (Slot var5 : var1.slots) {
         if (var5.id >= var3) {
            break;
         }

         if (var5.hasStack() && var2.test(var5.getStack())) {
            return var5;
         }
      }

      return null;
   }

   private boolean method4183() {
      return this.method4188(this::method4184) != -1;
   }

   private boolean method4184(ItemStack var1) {
      return var1 != null && !var1.isEmpty() && Registries.ITEM.getId(var1.getItem()).getPath().contains("pickaxe");
   }

   private boolean method4185(ItemStack var1) {
      if (var1 != null && !var1.isEmpty() && var1.getItem() instanceof PotionItem) {
         PotionContentsComponent var2 = var1.get(DataComponentTypes.POTION_CONTENTS);
         if (var2 == null) {
            return false;
         } else {
            for (StatusEffectInstance var4 : var2.getEffects()) {
               if (var4.getEffectType().equals(StatusEffects.INVISIBILITY)) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   private boolean method4186(ItemStack var1) {
      return var1 != null
         && !var1.isEmpty()
         && !var1.isOf(Items.CHORUS_FRUIT)
         && !var1.isOf(Items.SPIDER_EYE)
         && !var1.isOf(Items.POISONOUS_POTATO)
         && !var1.isOf(Items.PUFFERFISH)
         && !var1.isOf(Items.ROTTEN_FLESH)
         && var1.getUseAction() == UseAction.EAT;
   }

   private boolean method4187() {
      int var1 = 2;
      if (!mc.player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
         var1++;
      }

      return this.method4169(this::method4185) >= var1;
   }

   private int method4188(Predicate<ItemStack> var1) {
      byte var2 = 36;

      for (int var3 = 0; var3 < 9 && var3 < var2; var3++) {
         if (var1.test(mc.player.getInventory().getStack(var3))) {
            return var3;
         }
      }

      for (int var4 = 9; var4 < var2; var4++) {
         if (var1.test(mc.player.getInventory().getStack(var4))) {
            return var4;
         }
      }

      return -1;
   }

   private boolean method4189(long var1) {
      int var3 = this.method4188(this::method4184);
      if (var3 == -1) {
         return false;
      } else if (var3 <= 8) {
         this.method4211(var3);
         return true;
      } else {
         this.method4190(var3, var1);
         return false;
      }
   }

   private void method4190(int var1, long var2) {
      if (var2 - this.longValue2 >= 70L) {
         mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, var1, 8, SlotActionType.SWAP, mc.player);
         this.longValue2 = var2;
      }
   }

   private boolean method4191() {
      if (!this.flag8 && !this.flag9) {
         return switch (this.type) {
            case TP_TO_SKLAD, WAIT_SKLAD, TP_TO_ZAMOK, WAIT_ZAMOK, GO_ENTRY, GO_SHULKER, COLLECT, GO_EXIT, FLEE, TP_TO_DEPOSIT, WAIT_DEPOSIT -> true;
            default -> false;
         };
      } else {
         return false;
      }
   }

   private void method4192(long var1) {
      if (!mc.player.getInventory().getStack(mc.player.getInventory().getSelectedSlot()).isEmpty()) {
         for (int var3 = 0; var3 < 9; var3++) {
            if (mc.player.getInventory().getStack(var3).isEmpty()) {
               this.method4211(var3);
               return;
            }
         }

         if (var1 - this.longValue2 >= 70L && this.method4193()) {
            int var6 = -1;

            for (int var4 = 0; var4 < 9; var4++) {
               ItemStack var5 = mc.player.getInventory().getStack(var4);
               if (!this.method4184(var5) && !this.method4185(var5) && !this.method4186(var5)) {
                  var6 = var4;
                  break;
               }
            }

            if (var6 == -1) {
               for (int var7 = 0; var7 < 9; var7++) {
                  if (!this.method4184(mc.player.getInventory().getStack(var7))) {
                     var6 = var7;
                     break;
                  }
               }
            }

            if (var6 != -1) {
               mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, 36 + var6, 0, SlotActionType.QUICK_MOVE, mc.player);
               this.longValue2 = var1;
            }
         }
      }
   }

   private boolean method4193() {
      for (int var1 = 9; var1 < 36; var1++) {
         if (mc.player.getInventory().getStack(var1).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private boolean method4194(long var1, String var3) {
      if (var1 >= this.longValue17 && var1 - this.longValue4 >= 4000L) {
         this.intValue13 = 0;
         this.longValue18 = 0L;
         this.flag7 = false;
         this.longValue16 = var1;
         this.longValue4 = var1;
         mc.getNetworkHandler().sendChatCommand(var3);
         return true;
      } else {
         return false;
      }
   }

   private void method4195(long var1, AutoZamokModule.Type var3, AutoZamokModule.Type var4, boolean var5) {
      if (this.intValue13 == 1) {
         if (var5) {
            mc.options.jumpKey.setPressed(var1 - this.longValue18 < 300L);
         }

         if (var1 - this.longValue18 >= 1000L) {
            this.method4206(var3);
         }
      } else if (this.intValue13 == -1) {
         if (var1 >= this.longValue17 && var1 - this.longValue4 >= 4000L) {
            this.method4206(var4);
         }
      } else {
         if (var1 - this.longValue16 > (this.flag7 ? 12000L : 8000L)) {
            this.method4206(var4);
         }
      }
   }

   private boolean method4196() {
      return this.type == AutoZamokModule.Type.WAIT_SKLAD || this.type == AutoZamokModule.Type.WAIT_ZAMOK || this.type == AutoZamokModule.Type.WAIT_DEPOSIT;
   }

   private boolean method4197(long var1) {
      this.method4204();
      Vec3d var3 = mc.player.getVelocity();
      return Math.abs(var3.x) < 0.05 && Math.abs(var3.z) < 0.05 || var1 - this.longValue > 2000L;
   }

   private static long method4198(String var0) {
      long var1 = 0L;
      boolean var3 = false;

      for (int var4 = 0; var4 < var0.length(); var4++) {
         char var5 = var0.charAt(var4);
         if (var5 >= '0' && var5 <= '9') {
            var1 = var1 * 10L + (var5 - '0');
            var3 = true;
         } else if (var3) {
            break;
         }
      }

      return var3 ? var1 : 0L;
   }

   private void method4199() {
      SettingsBackup.apply();
   }

   private void method4200() {
      SettingsBackup.restore();
   }

   private void method4201(BlockPos var1, long var2) {
      if (!var1.equals(this.blockPos8) || !this.method4205() && var2 - this.longValue12 >= 6000L) {
         this.method4199();
         BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalNear(var1, 2));
         this.blockPos8 = var1;
         this.longValue12 = var2;
      }
   }

   private void method4202(BlockPos var1, long var2) {
      if (!var1.equals(this.blockPos8) || !this.method4205() && var2 - this.longValue12 >= 6000L) {
         this.method4199();
         BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalGetToBlock(var1));
         this.blockPos8 = var1;
         this.longValue12 = var2;
      }
   }

   private void method4203(int var1, int var2, long var3) {
      BlockPos var5 = new BlockPos(var1, 0, var2);
      if (!var5.equals(this.blockPos8) || !this.method4205() && var3 - this.longValue12 >= 6000L) {
         this.method4199();
         BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalXZ(var1, var2));
         this.blockPos8 = var5;
         this.longValue12 = var3;
      }
   }

   private void method4204() {
      if (this.blockPos8 != null || this.method4205()) {
         BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().onLostControl();
         this.blockPos8 = null;
      }
   }

   private boolean method4205() {
      return BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
   }

   private void method4206(AutoZamokModule.Type var1) {
      this.type = var1;
      this.longValue = System.currentTimeMillis();
      this.blockPos2 = null;
      this.blockPos3 = null;
      this.longValue14 = 0L;
      this.longValue15 = 0L;
      this.intValue8 = 0;
      this.flag6 = false;
   }

   private boolean method4207() {
      return mc.currentScreen instanceof DeathScreen || !mc.player.isAlive();
   }

   private void method4208() {
      mc.options.sneakKey.setPressed(true);
      mc.options.useKey.setPressed(true);
      if (!mc.player.isUsingItem() || mc.player.getActiveHand() != Hand.MAIN_HAND) {
         mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
      }
   }

   private void method4209() {
      if (mc.options != null) {
         mc.options.useKey.setPressed(false);
         mc.options.sneakKey.setPressed(false);
      }
   }

   private void method4210() {
      if (this.flag8) {
         this.method4209();
         this.flag8 = false;
      }
   }

   private void closeScreen() {
      if (mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
         mc.player.closeHandledScreen();
      }
   }

   private void method4211(int var1) {
      if (var1 >= 0 && var1 <= 8 && mc.player.getInventory().getSelectedSlot() != var1) {
         mc.player.getInventory().setSelectedSlot(var1);
         mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var1));
      }
   }

   private void method4212(Vec3d var1) {
      Util17.method3329(var1);
   }

   private void log(String var1) {
      ChatLogger.method2019("AutoZamok: " + var1);
   }

   private static enum Type {
      TP_TO_SKLAD,
      WAIT_SKLAD,
      REFILL_INVIS,
      REFILL_PICK,
      DRINK_INVIS,
      TP_TO_ZAMOK,
      WAIT_ZAMOK,
      GO_ENTRY,
      GO_SHULKER,
      BREAK_SHULKER,
      COLLECT,
      GO_EXIT,
      FLEE,
      TP_TO_DEPOSIT,
      WAIT_DEPOSIT,
      DEPOSIT;
   }
}
