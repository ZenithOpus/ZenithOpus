package org.zenith.utility.misc;

import java.util.List;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALUtil;

public final class AudioDevices {
   private static final String text = "OpenAL Soft on ";

   private AudioDevices() {
   }

   public static List<String> method3219() {
      try {
         if (!ALC10.alcIsExtensionPresent(0L, "ALC_EXT_CAPTURE")) {
            return List.of();
         } else {
            var var0 = ALUtil.getStringList(0L, 784);
            return var0 == null ? List.of() : List.copyOf(var0);
         }
      } catch (RuntimeException var1) {
         return List.of();
      }
   }

   public static List<String> method3220() {
      try {
         List var0;
         if (ALC10.alcIsExtensionPresent(0L, "ALC_ENUMERATE_ALL_EXT")) {
            var0 = ALUtil.getStringList(0L, 4115);
         } else if (ALC10.alcIsExtensionPresent(0L, "ALC_ENUMERATION_EXT")) {
            var0 = ALUtil.getStringList(0L, 4101);
         } else {
            var0 = null;
         }

         return var0 == null ? List.of() : List.copyOf(var0);
      } catch (RuntimeException var1) {
         return List.of();
      }
   }

   public static String method3221() {
      try {
         String var0 = ALC10.alcGetString(0L, 785);
         return var0 == null ? "" : var0;
      } catch (RuntimeException var1) {
         return "";
      }
   }

   public static String method3222() {
      try {
         String var0 = ALC10.alcIsExtensionPresent(0L, "ALC_ENUMERATE_ALL_EXT") ? ALC10.alcGetString(0L, 4114) : ALC10.alcGetString(0L, 4100);
         return var0 == null ? "" : var0;
      } catch (RuntimeException var1) {
         return "";
      }
   }

   public static String display(String var0) {
      return var0.startsWith("OpenAL Soft on ") ? var0.substring("OpenAL Soft on ".length()) : var0;
   }
}
