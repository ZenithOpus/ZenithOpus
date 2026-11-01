package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexFormatElement.Type;
import com.mojang.blaze3d.vertex.VertexFormatElement.Usage;

public final class VertexFormats {
   public static final VertexFormatElement vertexFormatElement = VertexFormatElement.register(8, 0, Type.FLOAT, Usage.GENERIC, 4);
   public static final VertexFormatElement vertexFormatElement2 = VertexFormatElement.register(9, 0, Type.FLOAT, Usage.GENERIC, 2);
   public static final VertexFormat vertexFormat = VertexFormat.builder()
      .add("Position", VertexFormatElement.POSITION)
      .add("Color", VertexFormatElement.COLOR)
      .add("UV0", VertexFormatElement.UV0)
      .add("Radii", vertexFormatElement)
      .add("Params", vertexFormatElement2)
      .build();

   private VertexFormats() {
   }
}
