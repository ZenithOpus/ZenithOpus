package org.zenith.utility.misc;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.input.Input;
import net.minecraft.util.PlayerInput;

public final class ViaCompat {
   private static final boolean CONST_flag = FabricLoader.getInstance().isModLoaded("viafabricplus");

   private ViaCompat() {
   }

   public static float method4846(Input var0) {
      return var0.getMovementInput().y;
   }

   public static float method4847(Input var0) {
      return var0.getMovementInput().x;
   }

   public static void method4848(Input var0, float var1) {
      method4850(var0, var1, method4847(var0));
   }

   public static void method4849(Input var0, float var1) {
      method4850(var0, method4846(var0), var1);
   }

   public static void method4850(Input var0, float var1, float var2) {
      PlayerInput var3 = var0.playerInput;
      var0.playerInput = new PlayerInput(var1 > 0.0F, var1 < 0.0F, var2 > 0.0F, var2 < 0.0F, var3.jump(), var3.sneak(), var3.sprint());
      ((ViaCompat.Handler)var0).zenith$setMovementVector(new net.minecraft.util.math.Vec2f(var2, var1));
   }

   public static net.minecraft.util.math.Vec2f method4851(net.minecraft.util.math.Vec2f var0) {
      return method4852() ? var0 : var0.normalize();
   }

   private static boolean method4852() {
      if (!CONST_flag) {
         return false;
      } else {
         try {
            return ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4);
         } catch (Throwable var1) {
            return false;
         }
      }
   }

   public interface Handler {
      void zenith$setMovementVector(net.minecraft.util.math.Vec2f var1);
   }
}
