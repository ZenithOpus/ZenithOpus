package org.zenith.utility.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

public class ShaderProgram {
   private final int intValue;
   private final Map<String, Integer> items = new HashMap<>();
   private final String text;
   private final String text2;
   private static int current;
   private static final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);

   public ShaderProgram(String var1, String var2) {
      this(var1, var2, var2);
   }

   public ShaderProgram(String var1, String var2, String var3) {
      this("/assets/gl/shaders/" + var1 + "/" + var3 + ".vsh", "/assets/gl/shaders/" + var1 + "/" + var2 + ".fsh", true);
   }

   public static ShaderProgram method5512(String var0, String var1) {
      return new ShaderProgram(var0, var1, true);
   }

   private ShaderProgram(String var1, String var2, boolean var3) {
      this.text = var1;
      this.text2 = var2;
      int var4 = this.method5513(this.text, 35633);
      int var5 = this.method5513(this.text2, 35632);
      this.intValue = GL20.glCreateProgram();
      GL20.glAttachShader(this.intValue, var4);
      GL20.glAttachShader(this.intValue, var5);
      GL20.glBindAttribLocation(this.intValue, 0, "Position");
      GL20.glLinkProgram(this.intValue);
      if (GL20.glGetProgrami(this.intValue, 35714) == 0) {
         throw new RuntimeException("Failed to link shader program: " + GL20.glGetProgramInfoLog(this.intValue));
      } else {
         GL20.glDeleteShader(var4);
         GL20.glDeleteShader(var5);
      }
   }

   private int method5513(String var1, int var2) {
      try {
         String var3 = this.method5514(var1);
         int var4 = GL20.glCreateShader(var2);
         GL20.glShaderSource(var4, var3);
         GL20.glCompileShader(var4);
         if (GL20.glGetShaderi(var4, 35713) == 0) {
            throw new RuntimeException("Failed to compile shader: " + var1 + "\n" + GL20.glGetShaderInfoLog(var4));
         } else {
            return var4;
         }
      } catch (IOException var5) {
         throw new RuntimeException("Failed to load shader: " + var1, var5);
      }
   }

   private String method5514(String var1) throws IOException {
      return this.method5515(var1, 0);
   }

   private String method5515(String var1, int var2) throws IOException {
      if (var2 > 8) {
         throw new IOException("Shader import loop: " + var1);
      } else {
         InputStream var3 = ShaderProgram.class.getResourceAsStream(var1);
         if (var3 == null) {
            throw new IOException("Shader file not found: " + var1);
         } else {
            StringBuilder var4 = new StringBuilder();
            BufferedReader var5 = new BufferedReader(new InputStreamReader(var3));

            try {
               String var6;
               while ((var6 = var5.readLine()) != null) {
                  String var7 = method5516(var6);
                  var4.append(var7 == null ? var6 : this.method5515(var7, var2 + 1)).append("\n");
               }

               var5.close();
            } catch (Throwable var9) {
               try {
                  var5.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            return var4.toString();
         }
      }
   }

   private static String method5516(String var0) {
      String var1 = var0.trim();
      if (!var1.startsWith("#moj_import")) {
         return null;
      } else {
         int var2 = var1.indexOf(60);
         int var3 = var1.indexOf(62);
         if (var2 >= 0 && var3 >= var2) {
            String var4 = var1.substring(var2 + 1, var3);
            int var5 = var4.indexOf(58);
            String var6 = var5 < 0 ? "minecraft" : var4.substring(0, var5);
            String var7 = var5 < 0 ? var4 : var4.substring(var5 + 1);
            return "/assets/" + var6 + "/shaders/include/" + var7;
         } else {
            return null;
         }
      }
   }

   public static int getCurrent() {
      return current;
   }

   public void method5517() {
      current = this.intValue;
      GL20.glUseProgram(this.intValue);
      FramebufferUtils.method5653();
   }

   public void unbind() {
      current = 0;
      GL20.glUseProgram(0);
      FramebufferUtils.method5653();
   }

   public void method5518(String var1, int var2) {
      int var3 = this.method5529(var1);
      if (var3 != -1) {
         GL20.glUniform1i(var3, var2);
      }
   }

   public void method5519(String var1, float var2) {
      int var3 = this.method5529(var1);
      if (var3 != -1) {
         GL20.glUniform1f(var3, var2);
      }
   }

   public void method5520(String var1, FloatBuffer var2) {
      int var3 = this.method5529(var1);
      if (var3 != -1) {
         GL20.glUniform1fv(var3, var2);
      }
   }

   public void method5521(String var1, float var2, float var3) {
      int var4 = this.method5529(var1);
      if (var4 != -1) {
         GL20.glUniform2f(var4, var2, var3);
      }
   }

   public void method5522(String var1, Vector2f var2) {
      this.method5521(var1, var2.x, var2.y);
   }

   public void method5523(String var1, float var2, float var3, float var4, float var5) {
      int var6 = this.method5529(var1);
      if (var6 != -1) {
         GL20.glUniform4f(var6, var2, var3, var4, var5);
      }
   }

   public void method5524(String var1, float var2, float var3, float var4) {
      int var5 = this.method5529(var1);
      if (var5 != -1) {
         GL20.glUniform3f(var5, var2, var3, var4);
      }
   }

   public void method5525(String var1, Vector3f var2) {
      this.method5524(var1, var2.x, var2.y, var2.z);
   }

   public void method5526(String var1, boolean var2) {
      this.method5518(var1, var2 ? 1 : 0);
   }

   public void method5527(String var1, Matrix4f var2) {
      int var3 = this.method5529(var1);
      if (var3 != -1) {
         var2.get(floatBuffer);
         GL20.glUniformMatrix4fv(var3, false, floatBuffer);
      }
   }

   public void method5528() {
      GL20.glDeleteProgram(this.intValue);
   }

   private int method5529(String var1) {
      return this.items.computeIfAbsent(var1, var1x -> GL20.glGetUniformLocation(this.intValue, var1x));
   }
}
