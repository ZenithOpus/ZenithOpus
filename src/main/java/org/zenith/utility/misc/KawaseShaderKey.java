package org.zenith.utility.misc;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.zenith.MinecraftAccess;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.LightUtil;
import org.zenith.utility.render.ShaderDefinition;

public class KawaseShaderKey extends ShaderDefinition implements MinecraftAccess {
   private static final List<KawaseShaderKey> items = new ArrayList<>();

   public KawaseShaderKey(Identifier var1, VertexFormat var2) {
      super(method5164(var1), var2, LightUtil.method6717(var2));
      items.add(this);
   }

   private static Identifier method5164(Identifier var0) {
      return var0.getPath().startsWith("core/") ? var0 : var0.withPrefixedPath("core/");
   }

   public KawaseShaderKey method5165() {
      ImmediateRenderer.method8012(this);
      return this;
   }

   public void method4659() {
   }

   @Override
   public ShaderUniform method5166(String var1) {
      return super.method5166(var1);
   }

   @Internal
   public static void method5167() {
      for (KawaseShaderKey var1 : items) {
         var1.method5566();
         var1.method4659();
      }
   }
}
