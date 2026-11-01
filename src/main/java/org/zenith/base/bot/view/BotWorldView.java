package org.zenith.base.bot.view;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RawProjectionMatrix;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.command.RenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.utility.mixin.accessors.MinecraftClientAccessor;
import org.zenith.utility.render.BlitUtils;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.MeshBuffer;
import org.zenith.utility.render.OutlineVertexProvider;

public final class BotWorldView implements BotWorld.RenderListener, MinecraftAccess {
   private final RawProjectionMatrix zenithProjection = new RawProjectionMatrix("zenith bot view");
   private final RawProjectionMatrix zenithHandProjection = new RawProjectionMatrix("zenith bot hand");
   private static final int RENDER_DISTANCE_CHUNKS = 5;
   private static final int VERTICAL_SECTION_RADIUS = 5;
   private static final int MAX_BUILDS_PER_FRAME = 8;
   private static final int MAX_PENDING_BUILDS = 32;
   private static final double ENTITY_RENDER_DISTANCE_SQ = 9216.0;
   private static final double BLOCK_ENTITY_RENDER_DISTANCE_SQ = 2304.0;
   private static final float FOV_DEGREES = 100.0F;
   private static final float HAND_FOV_DEGREES = 70.0F;
   private static final OutlineVertexConsumerProvider NO_OUTLINE = new OutlineVertexConsumerProvider();
   private static final Comparator<BotWorldView.Section> BY_SORT_KEY = (var0, var1) -> Double.compare(var0.sortKey, var1.sortKey);
   private final BotClient client;
   private final Map<Long, BotWorldView.Section> sections = new HashMap<>();
   private final Set<Long> dirty = ConcurrentHashMap.newKeySet();
   private final Queue<BotSectionMesher.MeshResult> uploads = new ConcurrentLinkedQueue<>();
   private final AtomicInteger pendingBuilds = new AtomicInteger();
   private final AtomicBoolean entitySnapshotScheduled = new AtomicBoolean();
   private volatile BotWorldView.EntitySnapshot entitySnapshot = BotWorldView.EntitySnapshot.EMPTY;
   private volatile boolean closed;
   private final OrderedRenderCommandQueueImpl commandQueue = new OrderedRenderCommandQueueImpl();
   private RenderDispatcher commandDispatcher;
   private boolean sectionsValid;
   private int lastCenterX;
   private int lastCenterY;
   private int lastCenterZ;
   private int lastMinSection;
   private int lastMaxSection;
   private final List<BotWorldView.Section> layerSections = new ArrayList<>();
   private final List<MeshBuffer> layerMeshes = new ArrayList<>();
   private final List<Matrix4f> layerMatrices = new ArrayList<>();
   private final Camera camera = new Camera();
   private final BotLightmap lightmap = new BotLightmap();
   private final BotPlayerRenderer playerRenderer = new BotPlayerRenderer();
   private final BotHeldItemRenderer heldItem = new BotHeldItemRenderer();
   private final Matrix4f projection = new Matrix4f();
   private final Matrix4f handProjection = new Matrix4f();
   private final Matrix4f positionMatrix = new Matrix4f();
   private final Quaternionf rotationConjugate = new Quaternionf();
   private SimpleFramebuffer fbo;
   private BotWorld boundWorld;
   private boolean loggedRenderError;
   private volatile String lastError;
   private int lastEyeHeightAge = Integer.MIN_VALUE;

   public BotWorldView(BotClient var1) {
      this.client = var1;
   }

   public GpuTextureView getColorAttachment() {
      return BlitUtils.method6450(this.fbo);
   }

   public boolean renderToFbo(int var1, int var2) {
      BotWorld var3 = this.client.getWorld();
      BotPlayer var4 = this.client.getPlayer();
      if (var3 != null && var4 != null && this.client.isJoined() && var1 > 0 && var2 > 0) {
         this.bindWorld(var3);
         this.drainUploads();
         this.refreshSections(var3, var4);
         this.scheduleBuilds(var3, var4);
         this.scheduleEntitySnapshot(var3);
         this.ensureFbo(var1, var2);
         if (this.fbo == null) {
            return false;
         } else {
            Matrix4fStack var5 = RenderSystem.getModelViewStack();
            RenderSystem.backupProjectionMatrix();
            var5.pushMatrix();
            var5.identity();
            int var6 = ImmediateRenderer.method8007();
            int var7 = mc.getWindow().getFramebufferWidth();
            int var8 = mc.getWindow().getFramebufferHeight();
            Framebuffer var9 = mc.getFramebuffer();
            synchronized (this.client.getRenderStateLock()) {
               float var11 = this.client.getTickDelta();
               this.updateCamera(var3, var4, var11);
               this.lightmap.update(var3);

               try {
                  ((MinecraftClientAccessor)(Object)mc).zenith$setFramebuffer(this.fbo);
                  float var12 = (float)var1 / var2;
                  float var13 = 128.0F;
                  this.projection.identity().perspective((float) (Math.PI * 5.0 / 9.0), var12, 0.05F, var13);
                  this.positionMatrix.rotation(this.camera.getRotation().conjugate(this.rotationConjugate));
                  RenderSystem.setProjectionMatrix(this.zenithProjection.set(this.projection), ProjectionType.PERSPECTIVE);
                  float[] var14 = skyColor(var3, var4);
                  BlitUtils.method6456(this.fbo, ColorHelper.fromFloats(1.0F, var14[0], var14[1], var14[2]));
                  Vec3d var15 = this.camera.getCameraPos();
                  this.drawTerrainLayer(BlockRenderLayer.SOLID, var15, false);
                  this.drawTerrainLayer(BlockRenderLayer.CUTOUT, var15, false);
                  this.renderEntities(var3, var4, var15, var11);
                  this.renderBlockEntities(var3, var15, var11);
                  this.drawTerrainLayer(BlockRenderLayer.TRANSLUCENT, var15, true);
                  this.drawTerrainLayer(BlockRenderLayer.TRIPWIRE, var15, true);
                  this.renderHand(var3, var4, var11, var12, var13);
               } catch (Throwable var21) {
                  this.lastError = var21.toString();
                  if (!this.loggedRenderError) {
                     this.loggedRenderError = true;
                     System.err.println("[BotWorldView] render failed:");
                     var21.printStackTrace();
                  }
               } finally {
                  this.endCommandFrame();
                  ((MinecraftClientAccessor)(Object)mc).zenith$setFramebuffer(var9);
                  RenderSystem.restoreProjectionMatrix();
                  var5.popMatrix();
                  this.restoreDispatchers();
                  ImmediateRenderer.method8008(var6);
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   private void updateCamera(BotWorld var1, BotPlayer var2, float var3) {
      boolean var4 = this.camera.getFocusedEntity() != var2;
      if (!var4 && var2.age != this.lastEyeHeightAge) {
         this.camera.updateEyeHeight();
         this.heldItem.tick(var2);
      }

      this.lastEyeHeightAge = var2.age;
      this.camera.update(var1, var2, false, false, var3);
      if (var4) {
         for (int var5 = 0; var5 < 24; var5++) {
            this.camera.updateEyeHeight();
         }

         this.camera.update(var1, var2, false, false, var3);
         this.heldItem.snap(var2);
      }
   }

   public String getDebugStatus() {
      int var1 = 0;

      for (BotWorldView.Section var3 : this.sections.values()) {
         if (!var3.buffers.isEmpty()) {
            var1++;
         }
      }

      StringBuilder var4 = new StringBuilder("mesh ")
         .append(var1)
         .append('/')
         .append(this.sections.size())
         .append(" | dirty ")
         .append(this.dirty.size())
         .append(" | pending ")
         .append(this.pendingBuilds.get());
      String var5 = this.lastError;
      if (var5 != null) {
         var4.append(" | err: ").append(var5);
      }

      return var4.toString();
   }

   public void close() {
      this.closed = true;

      for (BotWorldView.Section var2 : this.sections.values()) {
         var2.closeBuffers();
      }

      this.sections.clear();
      this.sectionsValid = false;
      this.dirty.clear();
      this.drainUploadsDiscarding();
      if (this.fbo != null) {
         this.fbo.delete();
         this.fbo = null;
      }

      this.lightmap.close();
      if (this.commandDispatcher != null) {
         this.commandQueue.clear();
         this.commandDispatcher.endLayeredCustoms();
         this.commandDispatcher.close();
         this.commandDispatcher = null;
      }

      if (this.boundWorld != null) {
         this.boundWorld.removeRenderListener(this);
         this.boundWorld = null;
      }
   }

   @Override
   public void onBlockChanged(BlockPos var1) {
      int var2 = ChunkSectionPos.getSectionCoord(var1.getX() - 1);
      int var3 = ChunkSectionPos.getSectionCoord(var1.getX() + 1);
      int var4 = ChunkSectionPos.getSectionCoord(var1.getY() - 1);
      int var5 = ChunkSectionPos.getSectionCoord(var1.getY() + 1);
      int var6 = ChunkSectionPos.getSectionCoord(var1.getZ() - 1);
      int var7 = ChunkSectionPos.getSectionCoord(var1.getZ() + 1);

      for (int var8 = var2; var8 <= var3; var8++) {
         for (int var9 = var4; var9 <= var5; var9++) {
            for (int var10 = var6; var10 <= var7; var10++) {
               this.dirty.add(ChunkSectionPos.asLong(var8, var9, var10));
            }
         }
      }
   }

   @Override
   public void onChunkChanged(int var1, int var2) {
      BotWorld var3 = this.client.getWorld();
      if (var3 != null) {
         int var4 = ChunkSectionPos.getSectionCoord(var3.getBottomY());
         int var5 = ChunkSectionPos.getSectionCoord(var3.getTopYInclusive());

         for (int var6 = var4; var6 <= var5; var6++) {
            this.dirty.add(ChunkSectionPos.asLong(var1, var6, var2));
            this.dirty.add(ChunkSectionPos.asLong(var1 - 1, var6, var2));
            this.dirty.add(ChunkSectionPos.asLong(var1 + 1, var6, var2));
            this.dirty.add(ChunkSectionPos.asLong(var1, var6, var2 - 1));
            this.dirty.add(ChunkSectionPos.asLong(var1, var6, var2 + 1));
         }
      }
   }

   @Override
   public void onSectionChanged(int var1, int var2, int var3) {
      this.dirty.add(ChunkSectionPos.asLong(var1, var2, var3));
      this.dirty.add(ChunkSectionPos.asLong(var1 - 1, var2, var3));
      this.dirty.add(ChunkSectionPos.asLong(var1 + 1, var2, var3));
      this.dirty.add(ChunkSectionPos.asLong(var1, var2 - 1, var3));
      this.dirty.add(ChunkSectionPos.asLong(var1, var2 + 1, var3));
      this.dirty.add(ChunkSectionPos.asLong(var1, var2, var3 - 1));
      this.dirty.add(ChunkSectionPos.asLong(var1, var2, var3 + 1));
   }

   private void bindWorld(BotWorld var1) {
      if (this.boundWorld != var1) {
         if (this.boundWorld != null) {
            this.boundWorld.removeRenderListener(this);
         }

         for (BotWorldView.Section var3 : this.sections.values()) {
            var3.closeBuffers();
         }

         this.sections.clear();
         this.sectionsValid = false;
         this.dirty.clear();
         this.drainUploadsDiscarding();
         this.closed = false;
         this.boundWorld = var1;
         var1.addRenderListener(this);
      }
   }

   private void drainUploads() {
      BotSectionMesher.MeshResult var1;
      while ((var1 = this.uploads.poll()) != null) {
         BotWorldView.Section var2 = this.sections.get(var1.sectionPos());
         if (var2 == null) {
            discardResult(var1);
         } else {
            var2.building = false;
            var2.closeBuffers();
            var2.blockEntities = var1.blockEntities();

            for (BotSectionMesher.LayerMesh var4 : var1.layers()) {
               MeshBuffer var5 = new MeshBuffer("zenith bot section");
               var5.method6583(var4.buffer());
               var4.allocator().close();
               var2.buffers.put(var4.layer(), var5);
            }
         }
      }
   }

   private void drainUploadsDiscarding() {
      BotSectionMesher.MeshResult var1;
      while ((var1 = this.uploads.poll()) != null) {
         discardResult(var1);
      }
   }

   private static void discardResult(BotSectionMesher.MeshResult var0) {
      for (BotSectionMesher.LayerMesh var2 : var0.layers()) {
         var2.buffer().close();
         var2.allocator().close();
      }
   }

   private void refreshSections(BotWorld var1, BotPlayer var2) {
      int var3 = ChunkSectionPos.getSectionCoord(MathHelper.floor(var2.getX()));
      int var4 = ChunkSectionPos.getSectionCoord(MathHelper.floor(var2.getEyeY()));
      int var5 = ChunkSectionPos.getSectionCoord(MathHelper.floor(var2.getZ()));
      int var6 = ChunkSectionPos.getSectionCoord(var1.getBottomY());
      int var7 = ChunkSectionPos.getSectionCoord(var1.getTopYInclusive());
      if (!this.sectionsValid
         || var3 != this.lastCenterX
         || var4 != this.lastCenterY
         || var5 != this.lastCenterZ
         || var6 != this.lastMinSection
         || var7 != this.lastMaxSection) {
         this.sectionsValid = true;
         this.lastCenterX = var3;
         this.lastCenterY = var4;
         this.lastCenterZ = var5;
         this.lastMinSection = var6;
         this.lastMaxSection = var7;
         this.sections
            .entrySet()
            .removeIf(
               var3x -> {
                  long var4x = var3x.getKey();
                  boolean var6x = Math.abs(ChunkSectionPos.unpackX(var4x) - var3) > 5
                     || Math.abs(ChunkSectionPos.unpackZ(var4x) - var5) > 5
                     || Math.abs(ChunkSectionPos.unpackY(var4x) - var4) > 5;
                  if (var6x) {
                     var3x.getValue().closeBuffers();
                  }

                  return var6x;
               }
            );

         for (int var8 = var3 - 5; var8 <= var3 + 5; var8++) {
            for (int var9 = var5 - 5; var9 <= var5 + 5; var9++) {
               int var10 = Math.max(var4 - 5, var6);
               int var11 = Math.min(var4 + 5, var7);

               for (int var12 = var10; var12 <= var11; var12++) {
                  long var13 = ChunkSectionPos.asLong(var8, var12, var9);
                  if (!this.sections.containsKey(var13)) {
                     this.sections.put(var13, new BotWorldView.Section(var13));
                     this.dirty.add(var13);
                  }
               }
            }
         }
      }
   }

   private void scheduleBuilds(BotWorld var1, BotPlayer var2) {
      if (!this.dirty.isEmpty() && this.pendingBuilds.get() < 32) {
         ArrayList<BotWorldView.Section> var3 = new ArrayList();

         for (Long var5 : this.dirty) {
            BotWorldView.Section var6 = this.sections.get(var5);
            if (var6 == null) {
               this.dirty.remove(var5);
            } else if (!var6.building) {
               var3.add(var6);
            }
         }

         if (!var3.isEmpty()) {
            double var15 = var2.getX();
            double var16 = var2.getY();
            double var8 = var2.getZ();

            for (Object var11_o : (Iterable<?>)(Object)(var3)) {
               BotWorldView.Section var11 = (BotWorldView.Section)var11_o;
               var11.sortKey = var11.squaredDistanceTo(var15, var16, var8);
            }

            var3.sort(BY_SORT_KEY);
            int var17 = 0;

            for (Object var12_o : (Iterable<?>)(Object)(var3)) {
               BotWorldView.Section var12 = (BotWorldView.Section)var12_o;
               if (var17 >= 8 || this.pendingBuilds.get() >= 32) {
                  break;
               }

               long var13 = var12.pos;
               if (this.dirty.remove(var13)) {
                  var12.building = true;
                  this.pendingBuilds.incrementAndGet();
                  var17++;
                  this.client.execute(() -> {
                     try {
                        if (this.closed) {
                           return;
                        }

                        this.uploads.add(BotSectionMesher.build(var1, var13));
                     } catch (Throwable var8x) {
                        this.lastError = "mesh: " + var8x;
                        this.uploads.add(new BotSectionMesher.MeshResult(var13, List.of(), List.of(), true));
                        this.dirty.add(var13);
                     } finally {
                        this.pendingBuilds.decrementAndGet();
                     }

                     if (this.closed) {
                        this.drainUploadsDiscarding();
                     }
                  });
               }
            }
         }
      }
   }

   private void scheduleEntitySnapshot(BotWorld var1) {
      BotWorldView.EntitySnapshot var2 = this.entitySnapshot;
      if (var2.world() != var1 || var2.version() != var1.getEntitySetVersion()) {
         if (this.entitySnapshotScheduled.compareAndSet(false, true)) {
            this.client.execute(() -> {
               try {
                  int var2x = var1.getEntitySetVersion();
                  ArrayList var3 = new ArrayList();

                  for (Entity var5 : var1.getEntities()) {
                     var3.add(var5);
                  }

                  this.entitySnapshot = new BotWorldView.EntitySnapshot(var1, var2x, var3);
               } catch (Throwable var9) {
               } finally {
                  this.entitySnapshotScheduled.set(false);
               }
            });
         }
      }
   }

   private RenderDispatcher commandDispatcher(Immediate var1) {
      if (this.commandDispatcher == null) {
         this.commandDispatcher = new RenderDispatcher(
            this.commandQueue, mc.getBlockRenderManager(), var1, mc.getAtlasManager(), NO_OUTLINE, var1, mc.textRenderer
         );
      }

      return this.commandDispatcher;
   }

   private void endCommandFrame() {
      if (this.commandDispatcher != null) {
         this.commandQueue.clear();
         this.commandDispatcher.endLayeredCustoms();
         this.commandQueue.onNextFrame();
      }
   }

   private void ensureFbo(int var1, int var2) {
      if (this.fbo == null || this.fbo.textureWidth != var1 || this.fbo.textureHeight != var2) {
         if (this.fbo != null) {
            this.fbo.delete();
         }

         this.fbo = BlitUtils.method6448("zenith bot view", var1, var2, true);
      }
   }

   private void drawTerrainLayer(BlockRenderLayer var1, Vec3d var2, boolean var3) {
      double var4 = var2.x;
      double var6 = var2.y;
      double var8 = var2.z;
      List<BotWorldView.Section> var10 = this.layerSections;
      var10.clear();

      for (BotWorldView.Section var12 : this.sections.values()) {
         if (var12.buffers.containsKey(var1)) {
            var12.sortKey = var12.squaredDistanceTo(var4, var6, var8);
            var10.add(var12);
         }
      }

      if (!var10.isEmpty()) {
         var10.sort(BY_SORT_KEY);
         if (var3) {
            Collections.reverse(var10);
         }

         RenderState.method1887(2, this.lightmap.getGlTextureView());
         RenderPipeline var17 = var1.getPipeline();
         var var18 = this.layerMeshes;
         var18.clear();
         int var13 = 0;

         for (Object var15_o : (Iterable<?>)(Object)(var10)) {
            BotWorldView.Section var15 = (BotWorldView.Section)var15_o;
            MeshBuffer var16 = var15.buffers.get(var1);
            if (var16 != null) {
               if (var13 == this.layerMatrices.size()) {
                  this.layerMatrices.add(new Matrix4f());
               }

               this.layerMatrices
                  .get(var13)
                  .set(this.positionMatrix)
                  .translate((float)(var15.originX() - var4), (float)(var15.originY() - var6), (float)(var15.originZ() - var8));
               var18.add(var16);
               var13++;
            }
         }

         MeshBuffer.method6588(var18, this.layerMatrices.subList(0, var13), var17, this.fbo);
         var18.clear();
         var10.clear();
         RenderState.method1893(2);
      }
   }

   private void renderEntities(BotWorld var1, BotPlayer var2, Vec3d var3, float var4) {
      EntityRenderManager var5 = mc.getEntityRenderDispatcher();
      var5.configure(this.camera, null);
      Immediate var6 = mc.getBufferBuilders().getEntityVertexConsumers();
      BotPlayHandler var7 = this.client.getPlayHandler();
      boolean var8 = mc.player == null;
      RenderDispatcher var9 = this.commandDispatcher(var6);
      CameraRenderState var10 = OutlineVertexProvider.method1541(var3);

      for (Entity var12 : this.entitySnapshot.entities()) {
         if (var12 != var2 && !var12.isRemoved() && var12.getEntityWorld() == var1 && !(var12.squaredDistanceTo(var3) > 9216.0)) {
            try {
               MatrixStack var13 = new MatrixStack();
               var13.multiplyPositionMatrix(this.positionMatrix);
               if (var12 instanceof PlayerEntity var14) {
                  int var23 = WorldRenderer.getLightmapCoordinates(var1, var14.getBlockPos());
                  this.playerRenderer.render(var14, var7, var3.x, var3.y, var3.z, var4, var13, var6, var23, true);
               } else if (!var8 || !(var12 instanceof LivingEntity)) {
                  double var15 = MathHelper.lerp((double)var4, var12.lastRenderX, var12.getX());
                  double var17 = MathHelper.lerp((double)var4, var12.lastRenderY, var12.getY());
                  double var19 = MathHelper.lerp((double)var4, var12.lastRenderZ, var12.getZ());
                  EntityRenderState var21 = var5.getAndUpdateRenderState(var12, var4);
                  var21.outlineColor = 0;
                  var5.render(var21, var10, var15 - var3.x, var17 - var3.y, var19 - var3.z, var13, this.commandQueue);
                  var9.render();
               }
            } catch (Throwable var22) {
               this.commandQueue.clear();
               this.lastError = "entity: " + var22;
               if (!this.loggedRenderError) {
                  this.loggedRenderError = true;
                  System.err.println("[BotWorldView] entity render failed for " + var12.getType() + ":");
                  var22.printStackTrace();
               }
            }
         }
      }

      var6.draw();
   }

   private void renderBlockEntities(BotWorld var1, Vec3d var2, float var3) {
      BlockEntityRenderManager var4 = mc.getBlockEntityRenderDispatcher();
      var4.configure(this.camera);
      Immediate var5 = mc.getBufferBuilders().getEntityVertexConsumers();
      RenderDispatcher var6 = this.commandDispatcher(var5);
      CameraRenderState var7 = OutlineVertexProvider.method1541(var2);

      for (BotWorldView.Section var9 : this.sections.values()) {
         for (BlockEntity var11 : var9.blockEntities) {
            if (!var11.isRemoved() && var11.getWorld() == var1) {
               BlockPos var12 = var11.getPos();
               if (!(var12.getSquaredDistance(var2) > 2304.0)) {
                  try {
                     MatrixStack var13 = new MatrixStack();
                     var13.multiplyPositionMatrix(this.positionMatrix);
                     var13.push();
                     var13.translate(var12.getX() - var2.x, var12.getY() - var2.y, var12.getZ() - var2.z);
                     BlockEntityRenderState var14 = var4.getRenderState(var11, var3, null);
                     if (var14 != null) {
                        var4.render(var14, var13, this.commandQueue, var7);
                        var6.render();
                     }

                     var13.pop();
                  } catch (Throwable var15) {
                     this.commandQueue.clear();
                  }
               }
            }
         }
      }

      var5.draw();
   }

   private void renderHand(BotWorld var1, BotPlayer var2, float var3, float var4, float var5) {
      if (this.fbo.getDepthAttachment() != null) {
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.fbo.getDepthAttachment(), 1.0);
      }

      this.handProjection.identity().perspective(1.2217305F, var4, 0.05F, var5);
      RenderSystem.setProjectionMatrix(this.zenithHandProjection.set(this.handProjection), ProjectionType.PERSPECTIVE);
      RenderState.method1887(2, this.lightmap.getGlTextureView());
      Immediate var6 = mc.getBufferBuilders().getEntityVertexConsumers();
      MatrixStack var7 = new MatrixStack();
      int var8 = WorldRenderer.getLightmapCoordinates(var1, BlockPos.ofFloored(var2.getEyePos()));
      this.heldItem.render(var2, this.client.getPlayHandler(), var3, var7, this.commandQueue, var8);
      this.commandDispatcher(var6).render();
      var6.draw();
   }

   private void restoreDispatchers() {
      if (mc.world != null) {
         Camera var1 = mc.gameRenderer.getCamera();
         mc.getEntityRenderDispatcher().configure(var1, mc.targetedEntity);
         mc.getBlockEntityRenderDispatcher().configure(var1);
      }
   }

   private static float[] skyColor(BotWorld var0, BotPlayer var1) {
      float var2 = BotLightmap.getSkyBrightness(var0);

      int var3;
      try {
         var3 = var0.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.SKY_COLOR_VISUAL);
      } catch (Exception var5) {
         var3 = 7907327;
      }

      return new float[]{ColorHelper.getRed(var3) / 255.0F * var2, ColorHelper.getGreen(var3) / 255.0F * var2, ColorHelper.getBlue(var3) / 255.0F * var2};
   }

   private static final class EntitySnapshot {
      private final BotWorld world;
      private final int version;
      private final List<Entity> entities;

      public BotWorld world() {
         return this.world;
      }

      public int version() {
         return this.version;
      }

      public List<Entity> entities() {
         return this.entities;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof EntitySnapshot other)) return false;
         return java.util.Objects.equals(this.world, other.world) && java.util.Objects.equals(this.version, other.version) && java.util.Objects.equals(this.entities, other.entities);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(world, version, entities);
      }

      @Override
      public String toString() {
         return "EntitySnapshot[" + "world=" + this.world + ", " + "version=" + this.version + ", " + "entities=" + this.entities + "]";
      }
      public static final BotWorldView.EntitySnapshot EMPTY = new BotWorldView.EntitySnapshot(null, 0, List.of());

      private EntitySnapshot(BotWorld var1, int var2, List<Entity> var3) {
         this.world = var1;
         this.version = var2;
         this.entities = var3;
      }
   }

   private static final class Section {
      public final long pos;
      public final Map<BlockRenderLayer, MeshBuffer> buffers = new HashMap<>();
      public List<BlockEntity> blockEntities = List.of();
      public boolean building;
      public double sortKey;

      public Section(long var1) {
         this.pos = var1;
      }

      public int originX() {
         return ChunkSectionPos.unpackX(this.pos) << 4;
      }

      public int originY() {
         return ChunkSectionPos.unpackY(this.pos) << 4;
      }

      public int originZ() {
         return ChunkSectionPos.unpackZ(this.pos) << 4;
      }

      public double squaredDistanceTo(double var1, double var3, double var5) {
         double var7 = this.originX() + 8.0 - var1;
         double var9 = this.originY() + 8.0 - var3;
         double var11 = this.originZ() + 8.0 - var5;
         return var7 * var7 + var9 * var9 + var11 * var11;
      }

      public void closeBuffers() {
         for (MeshBuffer var2 : this.buffers.values()) {
            var2.close();
         }

         this.buffers.clear();
         this.blockEntities = List.of();
      }
   }
}
