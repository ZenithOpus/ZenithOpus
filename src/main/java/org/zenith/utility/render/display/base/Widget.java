package org.zenith.utility.render.display.base;

import lombok.Generated;
import net.minecraft.client.input.CharInput;
import org.zenith.MinecraftAccess;
import org.zenith.utility.misc.Enum3;

public abstract class Widget implements MinecraftAccess {
   public float floatValue;
   public float floatValue2;
   public float width;
   public float height;

   public Widget(float var1, float var2, float var3, float var4) {
      this.floatValue = var1;
      this.floatValue2 = var2;
      this.width = var3;
      this.height = var4;
   }

   public Widget() {
      this(0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void method8081(MenuDrawContext var1) {
      this.method8084(var1);
      this.method8082(var1);
   }

   public abstract void method8082(MenuDrawContext var1);

   public void method8083() {
   }

   public void method8084(MenuDrawContext var1) {
   }

   public void onMouseClicked(double var1, double var3, Enum3 var5) {
   }

   public void onMouseReleased(double var1, double var3, Enum3 var5) {
   }

   public void method8085(int var1, int var2, int var3) {
   }

   public boolean charTyped(CharInput var1) {
      return this.charTyped((char)var1.codepoint(), var1.modifiers());
   }

   public boolean charTyped(char var1, int var2) {
      return false;
   }

   public void method8086(double var1, double var3, double var5, double var7) {
   }

   public void method8087(float var1, float var2) {
      this.floatValue = var1;
      this.floatValue2 = var2;
   }

   public void method8088(float var1, float var2, float var3, float var4) {
      this.floatValue = var1;
      this.floatValue2 = var2;
      this.width = var3;
      this.height = var4;
   }

   public boolean method8089(float var1, float var2) {
      return Vec2Utils.method8067(this.floatValue, this.floatValue2, this.width, this.height, var1, var2);
   }

   public boolean method8090(double var1, double var3) {
      return Vec2Utils.method8067(this.floatValue, this.floatValue2, this.width, this.height, var1, var3);
   }

   public boolean method8091(MenuDrawContext var1) {
      return this.method8089(var1.getMouseX(), var1.getMouseY());
   }

   @Generated
   public float getX() {
      return this.floatValue;
   }

   @Generated
   public float getY() {
      return this.floatValue2;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public void setX(float var1) {
      this.floatValue = var1;
   }

   @Generated
   public void setY(float var1) {
      this.floatValue2 = var1;
   }

   @Generated
   public void setWidth(float var1) {
      this.width = var1;
   }

   @Generated
   public void setHeight(float var1) {
      this.height = var1;
   }
}
