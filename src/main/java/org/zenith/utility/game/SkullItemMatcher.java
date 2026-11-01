package org.zenith.utility.game;

import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.ComponentChanges.Builder;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

public class SkullItemMatcher extends ItemMatcher {
   private final String text;
   private final String text2;

   public SkullItemMatcher(String var1, ItemMatcher.Type var2, String var3) {
      this(var3, var1, var1, var2);
   }

   public SkullItemMatcher(String var1, String var2, String var3, ItemMatcher.Type var4) {
      super(Items.PLAYER_HEAD.getDefaultStack(), var2, var3, var4);
      this.text = var1;
      this.text2 = this.method2783(var1);
      Builder var5 = ComponentChanges.builder();
      var var6 = Optional.of("");
      var var7 = Optional.of(UUID.randomUUID());
      ImmutableMultimap var8 = ImmutableMultimap.of("textures", new Property("textures", var1));
      PropertyMap var9 = new PropertyMap(var8);
      ProfileComponent var10 = ProfileComponent.ofStatic(new GameProfile(var7.orElse(UUID.randomUUID()), var6.orElse(""), var9));
      var5.add(DataComponentTypes.PROFILE, var10);
      ItemStackArgument var11 = new ItemStackArgument(this.itemStack.getRegistryEntry(), var5.build());

      try {
         this.itemStack = var11.createStack(1, false);
      } catch (CommandSyntaxException var13) {
      }
   }

   private String method2783(String var1) {
      try {
         String var2 = new String(Base64.getDecoder().decode(var1));
         int var3 = var2.indexOf("\"url\"");
         if (var3 != -1) {
            int var4 = var2.indexOf("\"", var3 + 5);
            if (var4 != -1) {
               int var5 = var2.indexOf("\"", var4 + 1);
               if (var5 != -1) {
                  return var2.substring(var4 + 1, var5);
               }
            }
         }
      } catch (Exception var6) {
      }

      return null;
   }

   @Override
   public boolean method2672(ItemStack var1) {
      if (!super.method2672(var1)) {
         return false;
      } else {
         ProfileComponent var2 = var1.get(DataComponentTypes.PROFILE);
         if (var2 != null) {
            PropertyMap var3 = var2.getGameProfile().properties();
            if (var3 != null && var3.containsKey("textures")) {
               for (Property var5 : var3.get("textures")) {
                  if (this.text2 != null) {
                     String var6 = this.method2783(var5.value());
                     if (var6 != null && var6.equals(this.text2)) {
                        return true;
                     }
                  }

                  if (var5.value().contains(this.text)) {
                     return true;
                  }
               }
            }
         }

         NbtComponent var7 = var1.get(DataComponentTypes.CUSTOM_DATA);
         NbtCompound var8 = var7 != null ? var7.copyNbt() : null;
         if (var8 != null && var8.contains("SkullOwner")) {
            String var9 = var8.get("SkullOwner").toString();
            if (this.text2 != null && var9.contains(this.text2)) {
               return true;
            }

            if (var9.contains(this.text)) {
               return true;
            }
         }

         return false;
      }
   }

   public String getText() {
      return this.text;
   }
}
