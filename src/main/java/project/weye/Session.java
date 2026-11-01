package project.weye;

/**
 * Stub for the removed licence/session runtime (the original "weye" protection layer is not part of the sources).
 * Returns developer defaults so the client starts without any online licence check.
 */
public final class Session {
   private Session() {
   }

   public static void loadWEye() {
   }

   public static String getRole() {
      return System.getProperty("DevRole", "Opus/Private");
   }

   public static String getUsername() {
      return System.getProperty("DevName", "developer");
   }

   public static String getUid() {
      return "0";
   }

   public static String getExpirationDate() {
      return "2099-12-31";
   }

   public static String getToken() {
      return "";
   }
}
