package org.zenith.base.bot.world;

import java.util.Objects;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.utility.misc.ViaCompat;

public class BotPlayer extends PlayerEntity {
   public final BotPlayHandler networkHandler;
   public double lastXClient;
   public double lastBaseY;
   public double lastZClient;
   public float lastYawClient;
   public float lastPitchClient;
   public boolean lastOnGround;
   private boolean lastHorizontalCollision;
   private boolean inSneakingPose;
   public boolean lastSneaking;
   public boolean lastSprinting;
   private int ticksSinceLastPositionPacketSent;
   private boolean healthInitialized;
   public Input input = new Input();
   public PlayerInput lastPlayerInput = PlayerInput.DEFAULT;
   private int clientPermissionLevel = 0;
   private boolean usingItem;
   @Nullable
   private Hand activeHand;
   private boolean riding;
   private boolean falling;

   public BotPlayer(BotWorld var1, BotPlayHandler var2, boolean var3, boolean var4) {
      super(var1, var2.getProfile());
      this.networkHandler = var2;
      this.lastSneaking = var3;
      this.lastSprinting = var4;
   }

   public BotWorld getBotWorld() {
      return (BotWorld)this.getEntityWorld();
   }

   @Override
   public void heal(float amount) {
   }

   @Override
   public void dismountVehicle() {
      super.dismountVehicle();
      this.riding = false;
   }

   @Override
   public float getPitch(float tickProgress) {
      return this.getPitch();
   }

   @Override
   public float getYaw(float tickProgress) {
      return this.hasVehicle() ? super.getYaw(tickProgress) : this.getYaw();
   }

   @Override
   public void tick() {
      if (this.networkHandler.isPlayerLoaded()) {
         super.tick();
         this.sendSneakingPacket();
         if (!this.lastPlayerInput.equals(this.input.playerInput)) {
            this.networkHandler.sendPacket(new PlayerInputC2SPacket(this.input.playerInput));
            this.lastPlayerInput = this.input.playerInput;
         }

         if (this.hasVehicle()) {
            this.networkHandler.sendPacket(new LookAndOnGround(this.getYaw(), this.getPitch(), this.isOnGround(), this.horizontalCollision));
            Entity var1 = this.getRootVehicle();
            if (var1 != this && var1.isLogicalSideForUpdatingMovement()) {
               this.networkHandler.sendPacket(VehicleMoveC2SPacket.fromVehicle(var1));
               this.sendSprintingPacket();
            }
         } else {
            this.sendMovementPackets();
         }
      }
   }

   private void sendMovementPackets() {
      this.sendSprintingPacket();
      double var1 = this.getX() - this.lastXClient;
      double var3 = this.getY() - this.lastBaseY;
      double var5 = this.getZ() - this.lastZClient;
      double var7 = this.getYaw() - this.lastYawClient;
      double var9 = this.getPitch() - this.lastPitchClient;
      this.ticksSinceLastPositionPacketSent++;
      boolean var11 = MathHelper.squaredMagnitude(var1, var3, var5) > MathHelper.square(2.0E-4) || this.ticksSinceLastPositionPacketSent >= 20;
      boolean var12 = var7 != 0.0 || var9 != 0.0;
      if (var11 && var12) {
         this.networkHandler
            .sendPacket(new Full(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch(), this.isOnGround(), this.horizontalCollision));
      } else if (var11) {
         this.networkHandler.sendPacket(new PositionAndOnGround(this.getX(), this.getY(), this.getZ(), this.isOnGround(), this.horizontalCollision));
      } else if (var12) {
         this.networkHandler.sendPacket(new LookAndOnGround(this.getYaw(), this.getPitch(), this.isOnGround(), this.horizontalCollision));
      } else if (this.lastOnGround != this.isOnGround() || this.lastHorizontalCollision != this.horizontalCollision) {
         this.networkHandler.sendPacket(new OnGroundOnly(this.isOnGround(), this.horizontalCollision));
      }

      if (var11) {
         this.lastXClient = this.getX();
         this.lastBaseY = this.getY();
         this.lastZClient = this.getZ();
         this.ticksSinceLastPositionPacketSent = 0;
      }

      if (var12) {
         this.lastYawClient = this.getYaw();
         this.lastPitchClient = this.getPitch();
      }

      this.lastOnGround = this.isOnGround();
      this.lastHorizontalCollision = this.horizontalCollision;
   }

   public final void sendSneakingPacket() {
      boolean var1 = this.isSneaking();
      if (var1 != this.lastSneaking) {
         this.lastSneaking = var1;
      }
   }

   public final void sendSprintingPacket() {
      boolean var1 = this.isSprinting();
      if (var1 != this.lastSprinting) {
         Mode var2 = var1 ? Mode.START_SPRINTING : Mode.STOP_SPRINTING;
         this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, var2));
         this.lastSprinting = var1;
      }
   }

   public boolean dropSelectedItem(boolean var1) {
      Action var2 = var1 ? Action.DROP_ALL_ITEMS : Action.DROP_ITEM;
      ItemStack var3 = this.getInventory().dropSelectedItem(var1);
      this.networkHandler.sendPacket(new PlayerActionC2SPacket(var2, BlockPos.ORIGIN, Direction.DOWN));
      return !var3.isEmpty();
   }

   @Override
   public void swingHand(Hand hand) {
      super.swingHand(hand);
      this.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
   }

   public void requestRespawn() {
      this.networkHandler.sendPacket(new ClientStatusC2SPacket(net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
   }

   @Override
   public void closeHandledScreen() {
      this.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(this.currentScreenHandler.syncId));
      this.closeScreen();
   }

   public void closeScreen() {
      super.closeHandledScreen();
   }

   public void updateHealth(float var1) {
      if (this.healthInitialized) {
         float var2 = this.getHealth() - var1;
         if (var2 <= 0.0F) {
            this.setHealth(var1);
            if (var2 < 0.0F) {
               this.timeUntilRegen = 10;
            }
         } else {
            this.lastDamageTaken = var2;
            this.timeUntilRegen = 20;
            this.setHealth(var1);
            this.maxHurtTime = 10;
            this.hurtTime = this.maxHurtTime;
         }
      } else {
         this.setHealth(var1);
         this.healthInitialized = true;
      }
   }

   @Override
   public void sendAbilitiesUpdate() {
      this.networkHandler.sendPacket(new UpdatePlayerAbilitiesC2SPacket(this.getAbilities()));
   }

   @Override
   public boolean isMainPlayer() {
      return true;
   }

   @Override
   public boolean isHoldingOntoLadder() {
      return !this.getAbilities().flying && super.isHoldingOntoLadder();
   }

   @Override
   public boolean shouldSpawnSprintingParticles() {
      return false;
   }

   public int getPermissionLevel() {
      return this.clientPermissionLevel;
   }

   public void setClientPermissionLevel(int var1) {
      this.clientPermissionLevel = var1;
   }

   @Override
   public void sendMessage(Text message, boolean overlay) {
      this.networkHandler.getClient().onChat(message);
   }

   private void pushOutOfBlocks(double var1, double var3) {
      BlockPos var5 = BlockPos.ofFloored(var1, this.getY(), var3);
      if (this.wouldCollideAt(var5)) {
         double var6 = var1 - var5.getX();
         double var8 = var3 - var5.getZ();
         Direction var10 = null;
         double var11 = Double.MAX_VALUE;
         Direction[] var13 = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};

         for (Direction var17 : var13) {
            double var18 = var17.getAxis().choose(var6, 0.0, var8);
            double var20 = var17.getDirection() == AxisDirection.POSITIVE ? 1.0 - var18 : var18;
            if (var20 < var11 && !this.wouldCollideAt(var5.offset(var17))) {
               var11 = var20;
               var10 = var17;
            }
         }

         if (var10 != null) {
            Vec3d var22 = this.getVelocity();
            if (var10.getAxis() == Axis.X) {
               this.setVelocity(0.1 * var10.getOffsetX(), var22.y, var22.z);
            } else {
               this.setVelocity(var22.x, var22.y, 0.1 * var10.getOffsetZ());
            }
         }
      }
   }

   private boolean wouldCollideAt(BlockPos var1) {
      Box var2 = this.getBoundingBox();
      Box var3 = new Box(var1.getX(), var2.minY, var1.getZ(), var1.getX() + 1.0, var2.maxY, var1.getZ() + 1.0).contract(1.0E-7);
      return this.getEntityWorld().canCollide(this, var3);
   }

   public void setExperience(float var1, int var2, int var3) {
      this.experienceProgress = var1;
      this.totalExperience = var2;
      this.experienceLevel = var3;
   }

   @Override
   public void handleStatus(byte status) {
      if (status >= 24 && status <= 28) {
         this.setClientPermissionLevel(status - 24);
      } else {
         super.handleStatus(status);
      }
   }

   @Override
   public void playSound(SoundEvent sound, float volume, float pitch) {
   }

   public void playSoundToPlayer(SoundEvent var1, SoundCategory var2, float var3, float var4) {
   }

   @Override
   public boolean canMoveVoluntarily() {
      return true;
   }

   @Override
   public void setCurrentHand(Hand hand) {
      ItemStack var2 = this.getStackInHand(hand);
      if (!var2.isEmpty() && !this.isUsingItem()) {
         super.setCurrentHand(hand);
         this.usingItem = true;
         this.activeHand = hand;
      }
   }

   @Override
   public boolean isUsingItem() {
      return this.usingItem;
   }

   @Override
   public void clearActiveItem() {
      super.clearActiveItem();
      this.usingItem = false;
   }

   @Override
   public Hand getActiveHand() {
      return Objects.requireNonNullElse(this.activeHand, Hand.MAIN_HAND);
   }

   @Override
   public void onTrackedDataSet(TrackedData<?> data) {
      super.onTrackedDataSet(data);
      if (LIVING_FLAGS.equals(data)) {
         boolean var2 = (this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
         Hand var3 = (this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
         if (var2 && !this.usingItem) {
            this.setCurrentHand(var3);
         } else if (!var2 && this.usingItem) {
            this.clearActiveItem();
         }
      }
   }

   @Nullable
   public JumpingMount getJumpingMount() {
      return this.getControllingVehicle() instanceof JumpingMount var1 && var1.canJump() ? var1 : null;
   }

   @Override
   public boolean isSneaking() {
      return this.input.playerInput.sneak();
   }

   @Override
   public boolean isInSneakingPose() {
      return this.inSneakingPose;
   }

   public boolean shouldSlowDown() {
      return this.isInSneakingPose() || this.isCrawling();
   }

   @Override
   public void tickNewAi() {
      super.tickNewAi();
      this.sidewaysSpeed = ViaCompat.method4847(this.input);
      this.forwardSpeed = ViaCompat.method4846(this.input);
      this.jumping = this.input.playerInput.jump();
   }

   public void init() {
      this.setPose(EntityPose.STANDING);
      if (this.getEntityWorld() != null) {
         for (double var1 = this.getY(); var1 > this.getEntityWorld().getBottomY() && var1 <= this.getEntityWorld().getTopYInclusive(); var1++) {
            this.setPosition(this.getX(), var1, this.getZ());
            if (this.getEntityWorld().isSpaceEmpty(this)) {
               break;
            }
         }

         this.setVelocity(Vec3d.ZERO);
         this.setPitch(0.0F);
      }

      this.setHealth(this.getMaxHealth());
      this.deathTime = 0;
   }

   @Override
   public void tickMovement() {
      this.tickPortalCooldown();
      boolean var1 = this.input.playerInput.jump();
      boolean var2 = this.input.playerInput.sneak();
      boolean var3 = this.isWalking();
      PlayerAbilities var4 = this.getAbilities();
      this.inSneakingPose = !var4.flying
         && !this.isSwimming()
         && !this.hasVehicle()
         && this.canChangeIntoPose(EntityPose.CROUCHING)
         && (this.isSneaking() || !this.isSleeping() && !this.canChangeIntoPose(EntityPose.STANDING));
      this.input.tick();
      if (this.shouldStopSprinting()) {
         this.setSprinting(false);
      }

      if (this.isUsingItem() && !this.hasVehicle()) {
         ViaCompat.method4850(this.input, ViaCompat.method4846(this.input) * 0.2F, ViaCompat.method4847(this.input) * 0.2F);
      }

      if (this.shouldSlowDown()) {
         float var5 = (float)this.getAttributeValue(EntityAttributes.SNEAKING_SPEED);
         ViaCompat.method4850(this.input, ViaCompat.method4846(this.input) * var5, ViaCompat.method4847(this.input) * var5);
      }

      if (!this.noClip) {
         this.pushOutOfBlocks(this.getX() - this.getWidth() * 0.35, this.getZ() + this.getWidth() * 0.35);
         this.pushOutOfBlocks(this.getX() - this.getWidth() * 0.35, this.getZ() - this.getWidth() * 0.35);
         this.pushOutOfBlocks(this.getX() + this.getWidth() * 0.35, this.getZ() - this.getWidth() * 0.35);
         this.pushOutOfBlocks(this.getX() + this.getWidth() * 0.35, this.getZ() + this.getWidth() * 0.35);
      }

      boolean var10 = this.canStartSprinting();
      boolean var6 = this.hasVehicle() ? this.getVehicle().isOnGround() : this.isOnGround();
      boolean var7 = !var2 && !var3;
      if ((var6 || this.isSubmergedInWater()) && var7 && var10 && this.input.playerInput.sprint()) {
         this.setSprinting(true);
      }

      if ((!this.isTouchingWater() || this.isSubmergedInWater()) && var10 && this.input.playerInput.sprint()) {
         this.setSprinting(true);
      }

      if (this.isSprinting()) {
         boolean var8 = !this.input.hasForwardMovement() || !this.canSprint();
         boolean var9 = var8 || this.horizontalCollision && !this.collidedSoftly || this.isTouchingWater() && !this.isSubmergedInWater();
         if (this.isSwimming()) {
            if (!this.isOnGround() && !this.input.playerInput.sneak() && var8 || !this.isTouchingWater()) {
               this.setSprinting(false);
            }
         } else if (var9) {
            this.setSprinting(false);
         }
      }

      boolean var11 = false;
      if (var4.allowFlying) {
         if (this.networkHandler.getInteractionManager().isFlyingLocked()) {
            if (!var4.flying) {
               var4.flying = true;
               var11 = true;
               this.sendAbilitiesUpdate();
            }
         } else if (!var1 && this.input.playerInput.jump()) {
            if (this.abilityResyncCountdown == 0) {
               this.abilityResyncCountdown = 7;
            } else if (!this.isSwimming()) {
               var4.flying = !var4.flying;
               if (var4.flying && this.isOnGround()) {
                  this.jump();
               }

               var11 = true;
               this.sendAbilitiesUpdate();
               this.abilityResyncCountdown = 0;
            }
         }
      }

      if (this.input.playerInput.jump() && !var11 && !var1 && !this.isClimbing() && this.checkGliding()) {
         this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, Mode.START_FALL_FLYING));
      }

      this.falling = this.isGliding();
      if (this.isTouchingWater() && this.input.playerInput.sneak() && this.shouldSwimInFluids()) {
         this.knockDownwards();
      }

      if (var4.flying) {
         int var12 = 0;
         if (this.input.playerInput.sneak()) {
            var12--;
         }

         if (this.input.playerInput.jump()) {
            var12++;
         }

         if (var12 != 0) {
            this.setVelocity(this.getVelocity().add(0.0, var12 * var4.getFlySpeed() * 3.0F, 0.0));
         }
      }

      super.tickMovement();
      if (this.isOnGround() && var4.flying && !this.networkHandler.getInteractionManager().isFlyingLocked()) {
         var4.flying = false;
         this.sendAbilitiesUpdate();
      }
   }

   private boolean shouldStopSprinting() {
      return this.isGliding()
         || this.isBlind()
         || this.shouldSlowDown()
         || this.hasVehicle() && !this.isRidingCamel()
         || this.isUsingItem() && !this.hasVehicle() && !this.isSubmergedInWater();
   }

   private boolean isRidingCamel() {
      return this.getVehicle() != null && this.getVehicle().getType() == EntityType.CAMEL;
   }

   public final boolean isBlind() {
      return this.hasStatusEffect(StatusEffects.BLINDNESS);
   }

   @Override
   public void updatePostDeath() {
      this.deathTime++;
      if (this.deathTime == 20) {
         this.remove(RemovalReason.KILLED);
      }
   }

   @Override
   public void tickRiding() {
      super.tickRiding();
      this.riding = false;
      if (this.getControllingVehicle() instanceof AbstractBoatEntity var1) {
         var1.setInputs(this.input.playerInput.left(), this.input.playerInput.right(), this.input.playerInput.forward(), this.input.playerInput.backward());
         this.riding = this.riding
            | (this.input.playerInput.left() || this.input.playerInput.right() || this.input.playerInput.forward() || this.input.playerInput.backward());
      }
   }

   public boolean isRiding() {
      return this.riding;
   }

   @Override
   public boolean hasCollidedSoftly(Vec3d adjustedMovement) {
      float var2 = this.getYaw() * (float) (Math.PI / 180.0);
      double var3 = MathHelper.sin(var2);
      double var5 = MathHelper.cos(var2);
      double var7 = this.sidewaysSpeed * var5 - this.forwardSpeed * var3;
      double var9 = this.forwardSpeed * var5 + this.sidewaysSpeed * var3;
      double var11 = MathHelper.square(var7) + MathHelper.square(var9);
      double var13 = MathHelper.square(adjustedMovement.x) + MathHelper.square(adjustedMovement.z);
      if (!(var11 < 1.0E-5F) && !(var13 < 1.0E-5F)) {
         double var15 = var7 * adjustedMovement.x + var9 * adjustedMovement.z;
         double var17 = Math.acos(var15 / Math.sqrt(var11 * var13));
         return var17 < 0.13962634F;
      } else {
         return false;
      }
   }

   private boolean canStartSprinting() {
      return !this.isSprinting()
         && this.isWalking()
         && this.canSprint()
         && !this.isUsingItem()
         && !this.isBlind()
         && (!this.hasVehicle() || this.canVehicleSprint(this.getVehicle()))
         && !this.isGliding()
         && (!this.shouldSlowDown() || this.isSubmergedInWater());
   }

   private boolean canVehicleSprint(Entity var1) {
      return var1.canSprintAsVehicle() && var1.isLogicalSideForUpdatingMovement();
   }

   public final boolean isWalking() {
      return this.isSubmergedInWater() ? this.input.hasForwardMovement() : ViaCompat.method4846(this.input) >= 0.8;
   }

   public final boolean canSprint() {
      return this.hasVehicle() || this.getHungerManager().getFoodLevel() > 6.0F || this.getAbilities().allowFlying;
   }

   public void onGameModeChanged(GameMode var1) {
      if (var1 == GameMode.SPECTATOR) {
         this.setVelocity(this.getVelocity().withAxis(Axis.Y, 0.0));
      }
   }

   @Override
   public boolean isSubmergedInWater() {
      return this.isSubmergedInWater;
   }

   @Override
   public boolean updateWaterSubmersionState() {
      super.updateWaterSubmersionState();
      return this.isSubmergedInWater;
   }

   @Override
   public float getBodyYaw() {
      return this.getYaw();
   }

   @Override
   public boolean isSpectator() {
      BotInteractionManager var1 = this.networkHandler.getInteractionManagerOrNull();
      return var1 != null && var1.getCurrentGameMode() == GameMode.SPECTATOR;
   }

   @Override
   public boolean isCreative() {
      BotInteractionManager var1 = this.networkHandler.getInteractionManagerOrNull();
      return var1 != null && var1.getCurrentGameMode() == GameMode.CREATIVE;
   }

   public boolean isSubmergedInWaterFlag() {
      return this.isSubmergedIn(FluidTags.WATER);
   }

   @Override
   public GameMode getGameMode() {
      PlayerListEntry var1 = this.networkHandler.getPlayerListEntry(this.getUuid());
      return var1 != null ? var1.getGameMode() : GameMode.SURVIVAL;
   }
}
