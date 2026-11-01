package org.zenith.utility.misc;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.util.Identifier;

public final class EmoteInfo {
   private final String text;
   private final UUID uUID;
   private final String text2;
   private final String text3;
   private final Identifier identifier;
   private final KeyframeAnimation keyframeAnimation;

   public String text() {
      return this.text;
   }

   public UUID uUID() {
      return this.uUID;
   }

   public String text2() {
      return this.text2;
   }

   public String text3() {
      return this.text3;
   }

   public Identifier identifier() {
      return this.identifier;
   }

   public KeyframeAnimation keyframeAnimation() {
      return this.keyframeAnimation;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof EmoteInfo other)) return false;
      return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.uUID, other.uUID) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.identifier, other.identifier) && java.util.Objects.equals(this.keyframeAnimation, other.keyframeAnimation);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(text, uUID, text2, text3, identifier, keyframeAnimation);
   }

   @Override
   public String toString() {
      return "EmoteInfo[" + "text=" + this.text + ", " + "uUID=" + this.uUID + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "identifier=" + this.identifier + ", " + "keyframeAnimation=" + this.keyframeAnimation + "]";
   }

   public EmoteInfo(String var1, UUID var2, String var3, String var4, Identifier var5, KeyframeAnimation var6) {
      Objects.requireNonNull(var1, "id");
      Objects.requireNonNull(var2, "animationUuid");
      Objects.requireNonNull(var3, "displayName");
      Objects.requireNonNull(var4, "author");
      Objects.requireNonNull(var5, "icon");
      Objects.requireNonNull(var6, "animation");
      this.text = var1;
      this.uUID = var2;
      this.text2 = var3;
      this.text3 = var4;
      this.identifier = var5;
      this.keyframeAnimation = var6;
   }

   public String getText() {
      return this.text;
   }

   public UUID getUUID() {
      return this.uUID;
   }

   public String getText2() {
      return this.text2;
   }

   public String author() {
      return this.text3;
   }

   public Identifier getIdentifier() {
      return this.identifier;
   }

   public KeyframeAnimation getKeyframeAnimation() {
      return this.keyframeAnimation;
   }
}
