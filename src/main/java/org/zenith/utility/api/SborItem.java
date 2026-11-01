package org.zenith.utility.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;

public class SborItem {
   private String text;
   private String text2;
   private int count;
   private int intValue;
   private long longValue;
   private int intValue2;
   private String text3;
   private String text4;

   public SborItem(String var1, String var2, int var3, int var4, long var5, int var7, String var8) {
      this(var1, var2, var3, var4, var5, var7, var8, null);
   }

   public SborItem(String var1, String var2, int var3, int var4, long var5, int var7, String var8, String var9) {
      this.text = var1;
      this.text2 = var2;
      this.count = var3;
      this.intValue = var4;
      this.longValue = var5;
      this.intValue2 = var7;
      this.text3 = var8;
      this.text4 = var9;
   }

   public SborItem() {
   }

   public static SborItem method1747(ItemStack var0, String var1, int var2, long var3, int var5) {
      String var6 = Registries.ITEM.getId(var0.getItem()).toString();
      String var7 = method1748(var0);
      return new SborItem(var6, var1, var0.getCount(), var2, var3, var5, var7);
   }

   public static String method1748(ItemStack var0) {
      try {
         MinecraftClient var1 = MinecraftClient.getInstance();
         ClientWorld var2 = var1.world;
         if (var2 == null) {
            return null;
         }

         RegistryOps var3 = var2.getRegistryManager().getOps(JsonOps.INSTANCE);
         DataResult var4 = ItemStack.CODEC.encodeStart(var3, var0);
         if (var4.result().isPresent()) {
            return ((JsonElement)var4.result().get()).toString();
         }
      } catch (Exception var5) {
      }

      return null;
   }

   public ItemStack method1749() {
      if (this.text3 != null && !this.text3.isEmpty()) {
         try {
            MinecraftClient var1 = MinecraftClient.getInstance();
            ClientWorld var2 = var1.world;
            if (var2 == null) {
               throw new IllegalStateException("World is not loaded");
            }

            JsonElement var3 = JsonParser.parseString(this.text3);
            RegistryOps var4 = var2.getRegistryManager().getOps(JsonOps.INSTANCE);
            DataResult var5 = ItemStack.CODEC.decode(var4, var3);
            if (var5.result().isPresent()) {
               return (ItemStack)((Pair)var5.result().get()).getFirst();
            }
         } catch (Exception var6) {
         }
      }

      Item var7 = Registries.ITEM.get(Identifier.tryParse(this.text));
      return new ItemStack(var7, this.count);
   }

   public JsonObject save() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("itemId", this.text);
      if (this.text2 != null) {
         var1.addProperty("searchName", this.text2);
      }

      var1.addProperty("count", this.count);
      var1.addProperty("slotId", this.intValue);
      var1.addProperty("maxSumBuy", this.longValue);
      var1.addProperty("countBuy", this.intValue2);
      if (this.text3 != null) {
         var1.addProperty("componentsJson", this.text3);
      }

      if (this.text4 != null) {
         var1.addProperty("extraSettingsJson", this.text4);
      }

      return var1;
   }

   public void load(JsonObject var1) {
      if (var1.has("itemId")) {
         this.text = var1.get("itemId").getAsString();
      }

      if (var1.has("searchName")) {
         this.text2 = var1.get("searchName").getAsString();
      }

      if (var1.has("count")) {
         this.count = var1.get("count").getAsInt();
      }

      if (var1.has("slotId")) {
         this.intValue = var1.get("slotId").getAsInt();
      }

      if (var1.has("maxSumBuy")) {
         this.longValue = var1.get("maxSumBuy").getAsLong();
      }

      if (var1.has("countBuy")) {
         this.intValue2 = var1.get("countBuy").getAsInt();
      }

      if (var1.has("componentsJson")) {
         this.text3 = var1.get("componentsJson").getAsString();
      }

      if (var1.has("extraSettingsJson")) {
         this.text4 = var1.get("extraSettingsJson").getAsString();
      }
   }

   @Generated
   public String getText() {
      return this.text;
   }

   @Generated
   public String getText2() {
      return this.text2;
   }

   @Generated
   public int getCount() {
      return this.count;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public long getLongValue() {
      return this.longValue;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue2;
   }

   @Generated
   public String getText3() {
      return this.text3;
   }

   @Generated
   public String getText4() {
      return this.text4;
   }

   @Generated
   public void setText(String var1) {
      this.text = var1;
   }

   @Generated
   public void setText2(String var1) {
      this.text2 = var1;
   }

   @Generated
   public void setCount(int var1) {
      this.count = var1;
   }

   @Generated
   public void setIntValue(int var1) {
      this.intValue = var1;
   }

   @Generated
   public void setLongValue(long var1) {
      this.longValue = var1;
   }

   @Generated
   public void setIntValue2(int var1) {
      this.intValue2 = var1;
   }

   @Generated
   public void setText3(String var1) {
      this.text3 = var1;
   }

   @Generated
   public void setText4(String var1) {
      this.text4 = var1;
   }
}
