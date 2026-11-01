package org.zenith.utility.config;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import kotlin.collections.EmptyMap;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.base.figura.avatar.AvatarLayer;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.client.events.TickEvent;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.ShareCodec;
import org.zenith.utility.network.Cloud;

public class FiguraManager implements MinecraftAccess {
   private Path path = null;
   private Path path2 = null;
   private Path path3 = null;
   private Path path4 = null;
   private static final long CONST_longValue = 1000L;
   private final Map<FiguraManager.Data2, FiguraManager.Data> items = new HashMap<>();
   private boolean flag;
   private final Map<UUID, FiguraManager.Data3> items2 = new HashMap<>();
   private final MultiSelectSetting render = new MultiSelectSetting("cosmetics.figura.render");

   public List<Setting> getSettings() {
      return Arrays.stream(this.getClass().getDeclaredFields()).map(var1 -> {
         try {
            var1.setAccessible(true);
            return var1.get(this);
         } catch (Exception var3) {
            return null;
         }
      }).filter(var0 -> var0 instanceof Setting).map(var0 -> (Setting)var0).collect(Collectors.toList());
   }

   public boolean method7664() {
      return !this.render.method5222("cosmetics.figura.render.self").isEnabled() && !this.render.method5222("cosmetics.figura.render.friends").isEnabled();
   }

   public boolean method7665() {
      return this.render.method5222("cosmetics.figura.render.self").isEnabled();
   }

   public boolean method7666() {
      return this.render.method5222("cosmetics.figura.render.friends").isEnabled();
   }

   public FiguraManager() {
      new MultiSelectSetting.Entry(this.render, "cosmetics.figura.render.self", true);
      new MultiSelectSetting.Entry(this.render, "cosmetics.figura.render.friends", true);
      EventManager.register(this);
   }

   @EventTarget
   public void onTick(TickEvent var1) {
      if (mc.player != null) {
         this.method7674();
         this.method7667();
         AvatarManager.tick();
      } else {
         this.path2 = null;
         this.path4 = null;
         this.items.clear();
         if (this.items2 != null && this.items2.getClass() == EmptyMap.class ? false : !this.items2.isEmpty()) {
            this.method7676(null);
         }
      }
   }

   private void method7667() {
      UUID var1 = mc.player.getUuid();
      this.path2 = this.method7668(var1, AvatarLayer.BODY, this.path, this.path2);
      this.path4 = this.method7668(var1, AvatarLayer.WEAPON, this.path3, this.path4);
   }

   private Path method7668(UUID var1, AvatarLayer var2, Path var3, Path var4) {
      boolean var5;
      boolean var6 = (var5 = var2 == AvatarLayer.WEAPON) ? AvatarUtil.method7250(var1) : AvatarUtil.method7249(var1);
      if (this.method7665() && var3 != null && this.method7680(var3)) {
         if (var6 && method7681(var3, var4)) {
            return var4;
         } else if (!var6 && !this.method7670(var1, var2, var3)) {
            return var3;
         } else {
            if (var5) {
               AvatarUtil.method7246(var1, var3);
            } else {
               AvatarUtil.method7244(var1, var3);
            }

            return var3;
         }
      } else {
         if (var6) {
            method7669(var1, var2);
         }

         return null;
      }
   }

   private static void method7669(UUID var0, AvatarLayer var1) {
      if (var1 == AvatarLayer.WEAPON) {
         AvatarUtil.method7253(var0);
      } else {
         AvatarUtil.method7252(var0);
      }
   }

   private boolean method7670(UUID var1, AvatarLayer var2, Object var3) {
      long var4 = System.currentTimeMillis();
      FiguraManager.Data2 var6 = new FiguraManager.Data2(var1, var2);
      FiguraManager.Data var7 = this.items.get(var6);
      if (var7 != null && var7.getObj().equals(var3) && var4 - var7.getLongValue() < 1000L) {
         return false;
      } else {
         this.items.put(var6, new FiguraManager.Data(var3, var4));
         return true;
      }
   }

   private void method7671(UUID var1) {
      if (var1 != null && (this.items != null && this.items.getClass() == EmptyMap.class ? false : !this.items.isEmpty())) {
         this.items.remove(new FiguraManager.Data2(var1, AvatarLayer.BODY));
         this.items.remove(new FiguraManager.Data2(var1, AvatarLayer.WEAPON));
      }
   }

   public void method7672(Path var1) {
      if (!method7681(this.path, var1)) {
         this.path = var1;
         this.path2 = null;
         if (mc.player != null) {
            AvatarUtil.method7255(mc.player.getUuid());
            this.items.remove(new FiguraManager.Data2(mc.player.getUuid(), AvatarLayer.BODY));
            if (var1 == null) {
               AvatarUtil.method7252(mc.player.getUuid());
            }
         }
      }
   }

   public void method7673(Path var1) {
      if (!method7681(this.path3, var1)) {
         this.path3 = var1;
         this.path4 = null;
         if (mc.player != null) {
            AvatarUtil.method7256(mc.player.getUuid());
            this.items.remove(new FiguraManager.Data2(mc.player.getUuid(), AvatarLayer.WEAPON));
            if (var1 == null) {
               AvatarUtil.method7253(mc.player.getUuid());
            }
         }
      }
   }

   private void method7674() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 != null && var1.world != null && var1.player != null) {
         if (!this.method7666()) {
            if (!this.flag) {
               this.flag = true;
               this.method7676(null);
            }
         } else {
            if (this.flag) {
               this.flag = false;
               this.items.clear();
            }

            Zenith var2 = Zenith.getInstance();
            Cloud var3 = var2 == null ? null : var2.getCloud();
            String var4 = var3 != null && var3.method7862() ? var3.method7911() : "";
            HashSet var5 = new HashSet();
            HashSet var6 = new HashSet();
            if (!var4.isEmpty()) {
               for (PartyPlayerInfo var8 : var3.state().method8038()) {
                  var6.add(var8.uid());
                  if (!var3.prefs().method3316(var8.username()) && var4.equals(var8.server())) {
                     ShareCodec.Data2 var9 = ShareCodec.method6724(var8.uid(), var8.getItems());
                     if (!var9.body().isEmpty() || !var9.getText2().isEmpty()) {
                        UUID var10 = method7677(var1, var8.getText2());
                        if (var10 != null) {
                           var5.add(var10);
                           FiguraManager.Data3 var11 = this.items2.getOrDefault(var10, new FiguraManager.Data3("", ""));
                           this.method7675(var10, AvatarLayer.BODY, var9.body(), var11.body());
                           this.method7675(var10, AvatarLayer.WEAPON, var9.getText2(), var11.getText2());
                           this.items2.put(var10, new FiguraManager.Data3(var9.body(), var9.getText2()));
                        }
                     }
                  }
               }
            }

            ShareCodec.method6725(var6);
            this.method7676(var5);
         }
      }
   }

   private void method7675(UUID var1, AvatarLayer var2, String var3, String var4) {
      boolean var5;
      boolean var6 = (var5 = var2 == AvatarLayer.WEAPON) ? AvatarUtil.method7250(var1) : AvatarUtil.method7249(var1);
      if (var3.isEmpty()) {
         if (!var4.isEmpty() && var6) {
            method7669(var1, var2);
         }
      } else if (!var6 || !var3.equals(var4)) {
         if (this.method7670(var1, var2, var3)) {
            if (var5) {
               AvatarUtil.method7245(var1, var3);
            } else {
               AvatarUtil.method7243(var1, var3);
            }
         }
      }
   }

   private void method7676(Set<UUID> var1) {
      var var2 = this.items2.entrySet().iterator();

      while (var2.hasNext()) {
         UUID var3 = (UUID)((Entry)var2.next()).getKey();
         if (var1 == null || !var1.contains(var3)) {
            AvatarUtil.method7251(var3);
            this.method7671(var3);
            var2.remove();
         }
      }
   }

   private static UUID method7677(MinecraftClient var0, String var1) {
      if (var1 != null && !var1.isBlank()) {
         for (AbstractClientPlayerEntity var3 : var0.world.getPlayers()) {
            if (var3 != var0.player && var1.equalsIgnoreCase(var3.getGameProfile().name())) {
               return var3.getUuid();
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public String method7678() {
      return this.method7680(this.path) ? AvatarUtil.method7257(this.path) : "";
   }

   public String method7679() {
      return this.method7680(this.path3) ? AvatarUtil.method7257(this.path3) : "";
   }

   public boolean method7680(Path var1) {
      return var1 != null && !AvatarUtil.method7257(var1).isBlank();
   }

   private static boolean method7681(Path var0, Path var1) {
      return var0 != null && var1 != null ? var0.toAbsolutePath().normalize().equals(var1.toAbsolutePath().normalize()) : var0 == var1;
   }

   @Generated
   public Path getPath() {
      return this.path;
   }

   @Generated
   public Path getPath2() {
      return this.path2;
   }

   @Generated
   public Path getPath3() {
      return this.path3;
   }

   @Generated
   public Path getPath4() {
      return this.path4;
   }

   @Generated
   public MultiSelectSetting getRender() {
      return this.render;
   }

   private static final class Data {
      private final Object obj;
      private final long longValue;

      public Object obj() {
         return this.obj;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.obj, other.obj) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(obj, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "obj=" + this.obj + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(Object var1, long var2) {
         this.obj = var1;
         this.longValue = var2;
      }

      public Object getObj() {
         return this.obj;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Data2 {
      private final UUID uUID;
      private final AvatarLayer avatarLayer;

      public UUID uUID() {
         return this.uUID;
      }

      public AvatarLayer avatarLayer() {
         return this.avatarLayer;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.uUID, other.uUID) && java.util.Objects.equals(this.avatarLayer, other.avatarLayer);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(uUID, avatarLayer);
      }

      @Override
      public String toString() {
         return "Data2[" + "uUID=" + this.uUID + ", " + "avatarLayer=" + this.avatarLayer + "]";
      }

      private Data2(UUID var1, AvatarLayer var2) {
         this.uUID = var1;
         this.avatarLayer = var2;
      }

      public UUID getUUID() {
         return this.uUID;
      }

      public AvatarLayer getAvatarLayer() {
         return this.avatarLayer;
      }
   }

   private static final class Data3 {
      private final String text;
      private final String text2;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2);
      }

      @Override
      public String toString() {
         return "Data3[" + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      private Data3(String var1, String var2) {
         this.text = var1;
         this.text2 = var2;
      }

      public String body() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }
   }
}
