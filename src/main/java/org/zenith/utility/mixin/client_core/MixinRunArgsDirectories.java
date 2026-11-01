package org.zenith.utility.mixin.client_core;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.RunArgs.Directories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({Directories.class})
public abstract class MixinRunArgsDirectories {
   @Unique
   private static final String ZENITH$GAME_ASSET_INDEX = "29";
   @Unique
   private static final Logger ZENITH$LOGGER = LoggerFactory.getLogger("Zenith/Assets");
   @Shadow
   @Final
   public File assetDir;

   @ModifyArg(
      method = {"getAssetDir()Ljava/nio/file/Path;"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/resource/ResourceIndex;buildFileSystem(Ljava/nio/file/Path;Ljava/lang/String;)Ljava/nio/file/Path;"
      ),
      index = 1
   )
   private String zenith$gameAssetIndex(String var1) {
      if (!"29".equals(var1) && this.assetDir != null) {
         Path var2 = this.assetDir.toPath().resolve("indexes").resolve("29.json");
         if (!Files.isRegularFile(var2)) {
            ZENITH$LOGGER.warn("Индекс ассетов {} не от этой версии, а {} рядом нет — остаёмся на {}", new Object[]{var1, var2, var1});
            return var1;
         } else {
            ZENITH$LOGGER.info("Индекс ассетов {} от другой версии игры, берём свой {}", var1, "29");
            return "29";
         }
      } else {
         return var1;
      }
   }
}
