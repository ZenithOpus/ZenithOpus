package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import kotlin.collections.EmptyList;
import kotlin.collections.EmptySet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.AesGcmCrypto;
import org.zenith.utility.misc.Localization;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(
   name = "BaseFinder",
   description = "module.basefinder.description",
   category = ModuleCategory.MISC
)
public final class BaseFinderModule extends Module {
   private static final int CONST_intValue = 8192;
   private static final int CONST_intValue2 = 4096;
   private static final int CONST_intValue3 = 3072;
   private static final int CONST_intValue4 = 1536;
   public static final BaseFinderModule INSTANCE = new BaseFinderModule();
   private JsonObject jsonObject;
   private final SliderSetting range = new SliderSetting("module.baseFinder.range", 30.0F, 1.0F, 128.0F, 2.0F, "module.baseFinder.range.desc", "b");
   private final MultiSelectSetting mods = new MultiSelectSetting(
      "module.baseFinder.mods",
      "module.baseFinder.mods.desc",
      MultiSelectSetting.Entry.method5231("module.baseFinder.mods.cave", false),
      MultiSelectSetting.Entry.method5232("module.baseFinder.mods.bypass"),
      MultiSelectSetting.Entry.method5232("module.baseFinder.mods.click"),
      MultiSelectSetting.Entry.method5232("module.baseFinder.mods.packetAnalysis")
   );
   private final SliderSetting min = new SliderSetting(
      "module.baseFinder.min", 3.0F, 0.0F, 30.0F, 1.0F, "module.baseFinder.min.desc", "x", () -> this.mods.method5227(0), null
   );
   private final SliderSetting max = new SliderSetting(
      "module.baseFinder.max", 50.0F, 5.0F, 100.0F, 5.0F, "module.baseFinder.max.desc", "x", () -> this.mods.method5227(0), null
   );
   private final SliderSetting minLength = new SliderSetting(
      "module.baseFinder.minLength", 2.0F, 0.0F, 30.0F, 1.0F, "module.baseFinder.minLength.desc", "b", () -> this.mods.method5227(0), null
   );
   private final SliderSetting minWidth = new SliderSetting(
      "module.baseFinder.minWidth", 2.0F, 0.0F, 30.0F, 1.0F, "module.baseFinder.minWidth.desc", "b", () -> this.mods.method5227(0), null
   );
   private final StopWatch stopWatch = new StopWatch();
   private final Set<BlockPos> items = ConcurrentHashMap.newKeySet();
   private final Set<BlockPos> items2 = ConcurrentHashMap.newKeySet();
   private final CopyOnWriteArrayList<BaseFinderModule.Entry4> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
   private final List<BlockPos> items3 = new ArrayList<>();
   private volatile int intValue = 1;
   private volatile int intValue2 = 0;
   private volatile long longValue = 0L;
   private volatile boolean flag = false;
   private BaseFinderModule.Entry entry;
   private BaseFinderModule.Entry2 entry2;

   private BaseFinderModule() {
   }

   @Override
   public void onEnable() {
      if (mc.player != null && mc.world != null) {
         this.stopWatch.setLongValue(0L);
         this.copyOnWriteArrayList.clear();
         this.items.clear();
         this.items2.clear();
         this.items3.clear();
         this.entry = null;
         this.entry2 = null;
         this.flag = false;
         this.intValue2 = 0;
         this.intValue = 1;
         this.method4076();
         this.method4064();
         super.onEnable();
      } else {
         this.setEnabled(false);
      }
   }

   @Override
   public boolean isPremium() {
      return true;
   }

   @Override
   public void onDisable() {
      this.entry = null;
      this.entry2 = null;
      this.flag = false;
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.jsonObject = null;
      super.onDisable();
   }

   @EventTarget
   public void method4060(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         if (this.stopWatch.method7546(1000L)) {
            this.method4064();
            this.stopWatch.reset();
         }

         if (this.mods.method5225(1).isEnabled()) {
            this.method4065();
         } else {
            this.entry = null;
            this.items.clear();
            this.items3.clear();
         }

         if (this.mods.method5227(0)) {
            this.method4066();
         } else {
            this.method4067();
            this.items2.clear();
         }

         if (this.mods.method5227(2)) {
            for (int var2 = 0; var2 < 5 && (this.items3 != null && this.items3.getClass() == EmptyList.class ? false : !this.items3.isEmpty()); var2++) {
               BlockPos var3 = this.items3.getFirst();
               UseItemUtils.method6514(new BlockHitResult(var3.toCenterPos(), Direction.UP, var3, false), Hand.OFF_HAND);
               this.items3.removeFirst();
            }
         }
      }
   }

   @EventTarget
   public void method4061(DrawEvent var1) {
      if (this.flag && this.mods.method5227(0)) {
         double var2 = this.intValue2 * 100.0 / Math.max(this.intValue, 1);
         long var6;
         long var12;
         long var14 = (
               var12 = Math.max((long)((var6 = System.currentTimeMillis() - this.longValue) * ((double)this.intValue / Math.max(this.intValue2, 1))) - var6, 0L)
                  / 1000L
            )
            / 60L;
         var12 %= 60L;
         String var16 = String.format("Поиск: %.1f%% (%d/%d) | Осталось: %02d:%02d", var2, this.intValue2, this.intValue, var14, var12);
         var1.getCustomDrawContext().drawText(Fonts.MEDIUM.getFont(20.0F), var16, 0.0F, 0.0F, ZenithColor.INSTANCE);
      }
   }

   @EventTarget
   public void method4062(WorldRenderEvent var1) {
      if (this.mods.method5227(1)) {
         this.items.forEach(var1x -> this.method4075(var1x, -1));
      }

      if (this.mods.method5227(0)) {
         this.items2.forEach(var1x -> this.method4075(var1x, Color.GREEN.getRGB()));
      }

      this.copyOnWriteArrayList.forEach(var1x -> {
         Block var2 = var1x.block;
         if (var2 == Blocks.DIAMOND_ORE || var2 == Blocks.DEEPSLATE_DIAMOND_ORE) {
            this.method4075(var1x.blockPos, Color.cyan.getRGB());
         } else if (var2 == Blocks.GOLD_ORE || var2 == Blocks.DEEPSLATE_GOLD_ORE || var2 == Blocks.NETHER_GOLD_ORE) {
            this.method4075(var1x.blockPos, -10496);
         } else if (var2 == Blocks.EMERALD_ORE || var2 == Blocks.DEEPSLATE_EMERALD_ORE) {
            this.method4075(var1x.blockPos, -256 << 16 | (char)-179);
         } else if (var2 == Blocks.IRON_ORE || var2 == Blocks.DEEPSLATE_IRON_ORE) {
            this.method4075(var1x.blockPos, -43 << 16 | (char)-10795);
         } else if (var2 == Blocks.REDSTONE_ORE || var2 == Blocks.DEEPSLATE_REDSTONE_ORE) {
            this.method4075(var1x.blockPos, -1 << 16 | 0);
         } else if (var2 == Blocks.ANCIENT_DEBRIS) {
            this.method4075(var1x.blockPos, -1);
         } else if (var2 == Blocks.CHEST) {
            this.method4075(var1x.blockPos, Color.ORANGE.getRGB());
         } else {
            MapColor var3 = var2.getDefaultMapColor();
            if (var3 != null) {
               this.method4075(var1x.blockPos, new Color(var3.color).getRGB());
            }
         }
      });
   }

   @EventTarget
   public void method4063(PacketEvent var1) {
      if (this.mods.method5227(3)) {
      }
   }

   private void method4064() {
      if (mc.player != null && mc.world != null) {
         BaseFinderModule.Entry5 var1 = this.method4068();
         if (this.mods.method5225(1).isEnabled() && this.entry == null) {
            this.entry = new BaseFinderModule.Entry(var1);
         }

         if (this.mods.method5227(0) && this.entry2 == null) {
            this.entry2 = new BaseFinderModule.Entry2(var1);
            this.flag = true;
            this.longValue = System.currentTimeMillis();
            this.intValue2 = 0;
            this.intValue = Math.max(1, var1.method4081());
         }
      }
   }

   private void method4065() {
      if (this.entry != null && mc.world != null) {
         int var1 = 0;
         int var2 = this.mods.method5227(0) ? 4096 : 8192;
         Mutable var3 = new Mutable();
         BaseFinderModule.Entry var4 = this.entry;

         for (boolean var5 = Zenith.getInstance().getServerDetector().method1642(); var1 < var2 && !var4.finished; var1++) {
            var3.set(var4.intValue, var4.intValue2, var4.intValue3);
            if (mc.world.isChunkLoaded(var4.intValue >> 4, var4.intValue3 >> 4)) {
               BlockState var6 = mc.world.getBlockState(var3);
               if (this.method4070(var6, var3, var5)) {
                  var4.items.add(var3.toImmutable());
               }
            }

            var4.advance();
         }

         if (var4.finished) {
            var var7 = this.method4069(var4.items);
            this.items.clear();
            this.items.addAll(var7);
            this.items3.clear();
            this.items3.addAll(var7);
            this.entry = null;
         }
      }
   }

   private void method4066() {
      if (this.entry2 != null && mc.world != null) {
         int var1 = 0;
         int var2 = this.mods.method5225(1).isEnabled() ? 1536 : 3072;
         BaseFinderModule.Entry2 var3 = this.entry2;

         for (Mutable var4 = new Mutable(); var1 < var2 && !var3.finished; var1++) {
            var4.set(var3.intValue, var3.intValue2, var3.intValue3);
            this.intValue2++;
            if (mc.world.isChunkLoaded(var4) && !var3.bitSet.get(var3.entry5.method4082(var3.intValue, var3.intValue2, var3.intValue3)) && mc.world.isAir(var4)
               )
             {
               BaseFinderModule.Entry3 var5 = this.method4072(var4.toImmutable(), var3.bitSet, mc.world, var3.entry5);
               if (this.method4071(var5)) {
                  var3.items.addAll(var5.items);
                  if (var5.flag) {
                     var3.items.addAll(var5.items2);
                  }
               }
            }

            var3.advance();
         }

         if (var3.finished) {
            this.items2.clear();
            this.items2.addAll(var3.items);
            this.method4067();
         }
      }
   }

   private void method4067() {
      this.entry2 = null;
      this.flag = false;
   }

   private BaseFinderModule.Entry5 method4068() {
      int var1 = (int)this.range.getValue3();
      return new BaseFinderModule.Entry5(
         (int)Math.floor(mc.player.getX() - var1),
         (int)Math.ceil(mc.player.getX() + var1),
         mc.world.getBottomY() + 1,
         mc.world.getTopYInclusive(),
         (int)Math.floor(mc.player.getZ() - var1),
         (int)Math.ceil(mc.player.getZ() + var1)
      );
   }

   private Set<BlockPos> method4069(Set<BlockPos> var1) {
      return !Zenith.getInstance().getServerDetector().method1642()
         ? var1
         : method4077(var1, 20).stream().flatMap(Collection::stream).collect(Collectors.toSet());
   }

   private boolean method4070(BlockState var1, BlockPos var2, boolean var3) {
      if (!var3) {
         Block var4 = var1.getBlock();
         return var4 != Blocks.NETHERRACK && var4 != Blocks.STONE ? false : mc.world.getLightLevel(LightType.BLOCK, var2) != 0;
      } else {
         return var1.getBlock() != Blocks.LAVA && mc.world.getLightLevel(LightType.BLOCK, var2) > 5;
      }
   }

   private boolean method4071(BaseFinderModule.Entry3 var1) {
      if (var1.size < this.min.getValue3() || var1.size > this.max.getValue3()) {
         return false;
      } else if (var1.height() < this.minLength.getValue3()) {
         return false;
      } else {
         return var1.width() < this.minWidth.getValue3() ? false : var1.flag || var1.flag2;
      }
   }

   private BaseFinderModule.Entry3 method4072(BlockPos var1, BitSet var2, World var3, BaseFinderModule.Entry5 var4) {
      ArrayDeque var5 = new ArrayDeque();
      BaseFinderModule.Entry3 var6 = new BaseFinderModule.Entry3();
      var5.add(var1);
      var2.set(var4.method4083(var1));

      while (!var5.isEmpty()) {
         BlockPos var7 = (BlockPos)var5.poll();
         var6.items.add(var7);
         var6.size++;
         int var8 = var7.getX();
         int var9 = var7.getY();
         int var10 = var7.getZ();
         var6.intValue = Math.min(var6.intValue, var8);
         var6.intValue4 = Math.max(var6.intValue4, var8);
         var6.intValue2 = Math.min(var6.intValue2, var9);
         var6.intValue5 = Math.max(var6.intValue5, var9);
         var6.intValue3 = Math.min(var6.intValue3, var10);
         var6.intValue6 = Math.max(var6.intValue6, var10);
         if (var6.size > this.max.getValue3()) {
            break;
         }

         for (Direction var14 : Direction.values()) {
            BlockPos var15 = var7.offset(var14);
            if (!var4.method4080(var15)) {
               var6.flag = true;
               var6.items2.add(var7);
            } else if (!var3.isChunkLoaded(var15)) {
               var6.flag = true;
               var6.items2.add(var7);
            } else {
               int var16 = var4.method4083(var15);
               if (!var2.get(var16)) {
                  if (var3.isAir(var15)) {
                     var2.set(var16);
                     var5.add(var15);
                  } else if (this.method4074(var3, var15)) {
                     var6.flag = true;
                     var6.items2.add(var15);
                  }
               }
            }
         }
      }

      var6.flag2 = this.method4073(var6.items, var3);
      return var6;
   }

   private boolean method4073(List<BlockPos> var1, World var2) {
      var var3 = new HashSet<>(var1);

      for (BlockPos var5 : var1) {
         for (Direction var9 : Direction.values()) {
            BlockPos var10 = var5.offset(var9);
            if (!var2.isChunkLoaded(var10)) {
               return false;
            }

            if (!var3.contains(var10) && var2.isAir(var10)) {
               return false;
            }
         }
      }

      return true;
   }

   private boolean method4074(World var1, BlockPos var2) {
      return var1.isChunkLoaded(var2) && var1.getBlockState(var2).isReplaceable();
   }

   private void method4075(BlockPos var1, int var2) {
      GlowUtil.method2230(new Box(var1), var2, 1.0F);
   }

   private void method4076() {
      try (InputStream var1 = Localization.class.getResourceAsStream("/assets/zenith/fonts/msdf/hold.json")) {
         if (var1 == null) {
            this.jsonObject = null;
            return;
         }

         byte[] var2 = Base64.getDecoder().decode(var1.readAllBytes());
         byte[] var3 = AesGcmCrypto.method5342(var2, "1.21.4");

         try (InputStreamReader var4 = new InputStreamReader(new ByteArrayInputStream(var3), StandardCharsets.UTF_8)) {
            this.jsonObject = (JsonObject)new Gson().fromJson(var4, JsonObject.class);
         }
      } catch (Exception var11) {
         this.jsonObject = null;
         var11.printStackTrace();
      }
   }

   public static List<List<BlockPos>> method4077(Collection<BlockPos> var0, int var1) {
      ArrayList<List<BlockPos>> var2 = new ArrayList<>();
      var var3 = new HashSet<>(var0);

      while (!var3.isEmpty()) {
         BlockPos var4 = (BlockPos)var3.iterator().next();
         ArrayList var5 = new ArrayList();
         ArrayDeque var6 = new ArrayDeque();
         var6.add(var4);
         var3.remove(var4);

         while (!var6.isEmpty()) {
            BlockPos var7 = (BlockPos)var6.poll();
            var5.add(var7);

            for (BlockPos var9 : method4078(var7)) {
               if (var3.contains(var9)) {
                  var3.remove(var9);
                  var6.add(var9);
               }
            }
         }

         if (var5.size() <= var1) {
            var2.add(var5);
         }
      }

      return var2;
   }

   private static List<BlockPos> method4078(BlockPos var0) {
      ArrayList var1 = new ArrayList(6);

      for (Direction var5 : Direction.values()) {
         var1.add(var0.offset(var5));
      }

      return var1;
   }

   public static CopyOnWriteArrayList<BaseFinderModule.Entry4> method4079(InputStream var0, BlockPos var1, double var2) {
      CopyOnWriteArrayList var4 = new CopyOnWriteArrayList();
      double var5 = var2 * var2;

      String var8;
      try (BufferedReader var7 = new BufferedReader(new InputStreamReader(var0))) {
         while ((var8 = var7.readLine()) != null) {
            String[] var9 = var8.split(" ");
            if (var9.length == 4) {
               try {
                  int var10 = Integer.parseInt(var9[0]);
                  int var11 = Integer.parseInt(var9[1]);
                  int var12 = Integer.parseInt(var9[2]);
                  double var13 = var10 - var1.getX();
                  double var15 = var12 - var1.getZ();
                  if (!(var13 * var13 + var15 * var15 > var5)) {
                     String var17 = new String(Base64.getDecoder().decode(var9[3]), StandardCharsets.UTF_8);
                     if (var17.startsWith("block.minecraft.")) {
                        var17 = var17.substring("block.minecraft.".length());
                     }

                     Identifier var18 = Identifier.of("minecraft", var17);
                     Block var19 = Registries.BLOCK.get(var18);
                     if (var19 != Blocks.AIR) {
                        var4.add(new BaseFinderModule.Entry4(new BlockPos(var10, var11, var12), var19));
                     }
                  }
               } catch (Exception var21) {
                  var21.printStackTrace();
               }
            }
         }
      } catch (Exception var23) {
         var23.printStackTrace();
      }

      return var4;
   }

   private static class Entry {
      public final BaseFinderModule.Entry5 entry5;
      public final Set<BlockPos> items = new HashSet<>();
      public int intValue;
      public int intValue2;
      public int intValue3;
      public boolean finished;

      private Entry(BaseFinderModule.Entry5 var1) {
         this.entry5 = var1;
         this.intValue = var1.intValue;
         this.intValue2 = var1.intValue3;
         this.intValue3 = var1.intValue5;
      }

      public void advance() {
         if (!this.finished) {
            if (++this.intValue3 > this.entry5.intValue6) {
               this.intValue3 = this.entry5.intValue5;
               if (++this.intValue2 > this.entry5.intValue4) {
                  this.intValue2 = this.entry5.intValue3;
                  if (++this.intValue > this.entry5.intValue2) {
                     this.finished = true;
                  }
               }
            }
         }
      }
   }

   private static final class Entry2 extends BaseFinderModule.Entry {
      private final BitSet bitSet;

      private Entry2(BaseFinderModule.Entry5 var1) {
         super(var1);
         this.bitSet = new BitSet(Math.max(1, var1.method4081()));
      }
   }

   private static class Entry3 {
      public final List<BlockPos> items = new ArrayList<>();
      public final List<BlockPos> items2 = new ArrayList<>();
      public boolean flag = false;
      public boolean flag2 = true;
      public int size = 0;
      public int intValue = Integer.MAX_VALUE;
      public int intValue2 = Integer.MAX_VALUE;
      public int intValue3 = Integer.MAX_VALUE;
      public int intValue4 = Integer.MIN_VALUE;
      public int intValue5 = Integer.MIN_VALUE;
      public int intValue6 = Integer.MIN_VALUE;

      public int width() {
         return Math.max(this.intValue4 - this.intValue + 1, this.intValue6 - this.intValue3 + 1);
      }

      public int height() {
         return this.intValue5 - this.intValue2 + 1;
      }
   }

   public static class Entry4 {
      public final BlockPos blockPos;
      public final Block block;

      public Entry4(BlockPos var1, Block var2) {
         this.blockPos = var1;
         this.block = var2;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return !(var1 instanceof BaseFinderModule.Entry4 var2) ? false : this.blockPos.equals(var2.blockPos) && this.block.equals(var2.block);
         }
      }

      @Override
      public int hashCode() {
         return this.blockPos.hashCode() * 31 + this.block.hashCode();
      }
   }

   private static final class Entry5 {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;
      private final int intValue5;
      private final int intValue6;

      private Entry5(int var1, int var2, int var3, int var4, int var5, int var6) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.intValue3 = var3;
         this.intValue4 = var4;
         this.intValue5 = var5;
         this.intValue6 = var6;
      }

      private boolean method4080(BlockPos var1) {
         return var1.getX() >= this.intValue
            && var1.getX() <= this.intValue2
            && var1.getY() >= this.intValue3
            && var1.getY() <= this.intValue4
            && var1.getZ() >= this.intValue5
            && var1.getZ() <= this.intValue6;
      }

      private int method4081() {
         return (this.intValue2 - this.intValue + 1) * (this.intValue4 - this.intValue3 + 1) * (this.intValue6 - this.intValue5 + 1);
      }

      private int method4082(int var1, int var2, int var3) {
         return ((var1 - this.intValue) * (this.intValue4 - this.intValue3 + 1) + (var2 - this.intValue3)) * (this.intValue6 - this.intValue5 + 1)
            + (var3 - this.intValue5);
      }

      private int method4083(BlockPos var1) {
         return this.method4082(var1.getX(), var1.getY(), var1.getZ());
      }
   }
}
