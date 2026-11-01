package org.zenith.utility.misc;

import org.joml.Matrix4f;
import org.zenith.utility.render.Jebriksheet;
import org.zenith.utility.render.display.base.HudQueuedContext;

public final class HudQueuedDraw implements Jebriksheet.Handler2 {
   public HudQueuedContext hudQueuedContext;
   public HudQueuedDraw.Type type;
   public final Matrix4f matrix4f = new Matrix4f();
   public float floatValue;
   public float floatValue2;
   public float floatValue3;
   public float floatValue4;
   public float floatValue5;
   public float floatValue6;
   public float floatValue7;
   public int intValue;
   public int intValue2;
   public int intValue3;
   public int intValue4;
   public int intValue5;
   public int intValue6;
   public int intValue7;
   public int intValue8;
   public int intValue9;
   public boolean flag;
   public Object obj;
   public Object obj2;
   public Object obj3;
   public int intValue10 = -1;

   @Override
   public void render() {
      this.hudQueuedContext.replayCommand(this);
   }

   public void method2487() {
      this.hudQueuedContext = null;
      this.obj = null;
      this.obj2 = null;
      this.obj3 = null;
      this.intValue10 = -1;
   }

   public static enum Type {
      TEXT_FONT_STRING_RGBA,
      TEXT_FONT_STRING_RGBA_FADEOUT,
      TEXT_FONT_STRING_INT_FADEOUT,
      TEXT_FONT_STRING_GRADIENT,
      TEXT_FONT_TEXT_INT,
      TEXT_FONT_TEXT,
      TEXT_SHADOW_STRING,
      TEXT_STRING,
      TEXT_SHADOW_ORDERED,
      TEXT_ORDERED,
      TEXT_SHADOW_TEXT,
      TEXT_TEXT,
      WRAPPED_TEXT,
      STACK_OVERLAY,
      STACK_OVERLAY_TEXT,
      SPRITE,
      ROUNDED_RECT_GRADIENT,
      SQUIRCLE,
      ROUNDED_BORDER_GRADIENT,
      ROUNDED_BORDER_RGBA,
      ROUNDED_CORNER_ONLY,
      TEXTURE_RGBA,
      ARC_BORDER,
      ROUNDED_TEXTURE,
      ROUNDED_TEXTURE_RGBA,
      PLAYER_HEAD,
      DRAW,
      FILL_GRADIENT,
      FILL_GRADIENT_Z,
      FILL_GRADIENT_LAYER,
      SPRITE_STRETCHED,
      SPRITE_STRETCHED_COLOR,
      GUI_TEXTURE,
      GUI_TEXTURE_COLOR,
      GUI_TEXTURE_REGION,
      TEXTURE_UV,
      TEXTURE_UV_COLOR,
      TEXTURE_REGION,
      TEXTURE_REGION_COLOR,
      ITEM,
      ITEM_BAR,
      COOLDOWN_PROGRESS,
      ITEM_SEED,
      ITEM_SEED_Z,
      ITEM_WITHOUT_ENTITY,
      ITEM_WITHOUT_ENTITY_SEED,
      ITEM_ENTITY,
      SCISSOR_ENABLE,
      SCISSOR_DISABLE;
   }
}
