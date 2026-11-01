package org.zenith.utility.misc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.Generated;

public class Account {
   private Account.Type type = Account.Type.OFFLINE;
   private String name = "";
   private String text = "";
   private String text2 = "";
   private String text3 = "";
   private long longValue = System.currentTimeMillis();

   public static Account method5470(String var0) {
      Account var1 = new Account();
      var1.type = Account.Type.OFFLINE;
      var1.name = var0;
      var1.text = offlineUuid(var0).toString();
      return var1;
   }

   public static Account method5471(String var0, UUID var1, String var2, String var3) {
      Account var4 = new Account();
      var4.type = Account.Type.MICROSOFT;
      var4.name = var0;
      var4.text = var1.toString();
      var4.text2 = var2;
      var4.text3 = var3;
      return var4;
   }

   public static UUID offlineUuid(String var0) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + var0).getBytes(StandardCharsets.UTF_8));
   }

   public UUID method5472() {
      try {
         return UUID.fromString(this.text);
      } catch (Exception var2) {
         return offlineUuid(this.name);
      }
   }

   public boolean method5473() {
      return this.type == Account.Type.MICROSOFT;
   }

   @Generated
   public Account.Type getType() {
      return this.type;
   }

   @Generated
   public String getName() {
      return this.name;
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
   public String getText3() {
      return this.text3;
   }

   @Generated
   public long getLongValue() {
      return this.longValue;
   }

   @Generated
   public void setType(Account.Type var1) {
      this.type = var1;
   }

   @Generated
   public void setName(String var1) {
      this.name = var1;
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
   public void setText3(String var1) {
      this.text3 = var1;
   }

   @Generated
   public void setLongValue(long var1) {
      this.longValue = var1;
   }

   public static enum Type {
      OFFLINE,
      MICROSOFT;
   }
}
