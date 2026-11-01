package org.zenith.utility.discord.utils;

import com.sun.jna.Structure;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DiscordRichPresence extends Structure {
   public String largeImageKey;
   public String largeImageText;
   public String smallImageText;
   public String partyPrivacy;
   public long startTimestamp;
   public int instance;
   public String partyId;
   public int partySize;
   public long endTimestamp;
   public String details;
   public String joinSecret;
   public String spectateSecret;
   public String smallImageKey;
   public String matchSecret;
   public String state;
   public int partyMax;
   public String button_url_1;
   public String button_label_1;
   public String button_url_2;
   public String button_label_2;

   public DiscordRichPresence() {
      this.setStringEncoding("UTF-8");
   }

   public List<String> getFieldOrder() {
      return Arrays.asList(
         "state",
         "details",
         "startTimestamp",
         "endTimestamp",
         "largeImageKey",
         "largeImageText",
         "smallImageKey",
         "smallImageText",
         "partyId",
         "partySize",
         "partyMax",
         "partyPrivacy",
         "matchSecret",
         "joinSecret",
         "spectateSecret",
         "button_label_1",
         "button_url_1",
         "button_label_2",
         "button_url_2",
         "instance"
      );
   }

   public static class Builder {
      private final DiscordRichPresence essencePresence = new DiscordRichPresence();

      public DiscordRichPresence.Builder setSmallImage(String var1) {
         return this.setSmallImage(var1, "");
      }

      public DiscordRichPresence.Builder setState(String var1) {
         if (var1 != null && !var1.isEmpty()) {
            this.essencePresence.state = var1.substring(0, Math.min(var1.length(), 128));
         }

         return this;
      }

      public DiscordRichPresence.Builder setDetails(String var1) {
         if (var1 != null && !var1.isEmpty()) {
            this.essencePresence.details = var1.substring(0, Math.min(var1.length(), 128));
         }

         return this;
      }

      public DiscordRichPresence.Builder setLargeImage(String var1, String var2) {
         this.essencePresence.largeImageKey = var1;
         this.essencePresence.largeImageText = var2;
         return this;
      }

      public DiscordRichPresence.Builder setInstance(boolean var1) {
         if ((this.essencePresence.button_label_1 == null || !this.essencePresence.button_label_1.isEmpty())
            && (this.essencePresence.button_label_2 == null || !this.essencePresence.button_label_2.isEmpty())) {
            this.essencePresence.instance = var1 ? 1 : 0;
         }

         return this;
      }

      public DiscordRichPresence.Builder setButtons(RPCButton var1) {
         return this.setButtons(Collections.singletonList(var1));
      }

      public DiscordRichPresence.Builder setSmallImage(String var1, String var2) {
         this.essencePresence.smallImageKey = var1;
         this.essencePresence.smallImageText = var2;
         return this;
      }

      public DiscordRichPresence.Builder setButtons(List<RPCButton> var1) {
         if (var1 != null && !var1.isEmpty()) {
            int var2 = Math.min(var1.size(), 2);
            this.essencePresence.button_label_1 = ((RPCButton)var1.get(0)).getLabel();
            this.essencePresence.button_url_1 = ((RPCButton)var1.get(0)).getUrl();
            if (var2 == 2) {
               this.essencePresence.button_label_2 = ((RPCButton)var1.get(1)).getLabel();
               this.essencePresence.button_url_2 = ((RPCButton)var1.get(1)).getUrl();
            }
         }

         return this;
      }

      public DiscordRichPresence.Builder setStartTimestamp(OffsetDateTime var1) {
         this.essencePresence.startTimestamp = var1.toEpochSecond();
         return this;
      }

      public DiscordRichPresence.Builder setSecrets(String var1, String var2, String var3) {
         if ((this.essencePresence.button_label_1 == null || !this.essencePresence.button_label_1.isEmpty())
            && (this.essencePresence.button_label_2 == null || !this.essencePresence.button_label_2.isEmpty())) {
            this.essencePresence.matchSecret = var1;
            this.essencePresence.joinSecret = var2;
            this.essencePresence.spectateSecret = var3;
         }

         return this;
      }

      public DiscordRichPresence.Builder setButtons(RPCButton var1, RPCButton var2) {
         this.setButtons(Arrays.asList(var1, var2));
         return this;
      }

      public DiscordRichPresence.Builder setStartTimestamp(long var1) {
         this.essencePresence.startTimestamp = var1;
         return this;
      }

      public DiscordRichPresence.Builder setSecrets(String var1, String var2) {
         if ((this.essencePresence.button_label_1 == null || !this.essencePresence.button_label_1.isEmpty())
            && (this.essencePresence.button_label_2 == null || !this.essencePresence.button_label_2.isEmpty())) {
            this.essencePresence.joinSecret = var1;
            this.essencePresence.spectateSecret = var2;
         }

         return this;
      }

      public DiscordRichPresence.Builder setEndTimestamp(long var1) {
         this.essencePresence.endTimestamp = var1;
         return this;
      }

      public DiscordRichPresence.Builder setEndTimestamp(OffsetDateTime var1) {
         this.essencePresence.endTimestamp = var1.toEpochSecond();
         return this;
      }

      public DiscordRichPresence.Builder setLargeImage(String var1) {
         return this.setLargeImage(var1, "");
      }

      public DiscordRichPresence build() {
         return this.essencePresence;
      }
   }
}
