package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.zenith.Zenith;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;
import org.zenith.utility.misc.ScriptHeaderParser;

public final class Server {
   public static final List<String> items = List.of(
      "get_api_docs",
      "list_scripts",
      "read_file",
      "write_file",
      "create_script",
      "delete_file",
      "reload_script",
      "set_script_enabled",
      "check_syntax",
      "get_logs",
      "run_js",
      "game_state"
   );
   private static final Logger logger = LogUtils.getLogger();
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   private static final int CONST_intValue = 25610;
   private static final int CONST_intValue2 = 1048576;
   private static final String text = "2025-06-18";
   private static final Server INSTANCE = new Server();
   private final Path path;
   private boolean enabled;
   private int port = 25610;
   private String text2 = "";
   private ServerSocket serverSocket;
   private ExecutorService executorService;
   private Thread thread;
   private final AtomicInteger atomicInteger = new AtomicInteger();

   private Server() {
      this.path = Zenith.file.toPath().resolve("scripts").resolve(".mcp-server.json");
      this.method220();
   }

   public static Server getInstance() {
      return INSTANCE;
   }

   public boolean isRunning() {
      return this.serverSocket != null && !this.serverSocket.isClosed();
   }

   public int port() {
      return this.port;
   }

   public int method215() {
      return this.atomicInteger.get();
   }

   public String url() {
      return "http://127.0.0.1:" + this.port + "/mcp/" + this.text2;
   }

   public String method216() {
      return "claude mcp add --transport http zenith " + this.url();
   }

   public void method217() {
      if (this.enabled) {
         this.start();
      }
   }

   public synchronized void setEnabled(boolean var1) {
      this.enabled = var1;
      if (var1) {
         this.start();
      } else {
         this.stop();
      }

      this.method221();
   }

   public synchronized void method218() {
      this.text2 = method219();
      this.method221();
      this.method222();
   }

   private static String method219() {
      byte[] var0 = new byte[24];
      new SecureRandom().nextBytes(var0);
      return HexFormat.of().formatHex(var0);
   }

   private void method220() {
      try {
         if (Files.isRegularFile(this.path)) {
            JsonObject var1 = JsonParser.parseString(Files.readString(this.path, StandardCharsets.UTF_8)).getAsJsonObject();
            this.enabled = var1.has("enabled") && var1.get("enabled").getAsBoolean();
            this.port = var1.has("port") ? var1.get("port").getAsInt() : 25610;
            this.text2 = var1.has("token") ? var1.get("token").getAsString() : "";
         }
      } catch (Exception var2) {
         logger.warn("MCP: не удалось прочитать настройки", var2);
      }

      if (this.text2.length() < 32) {
         this.text2 = method219();
      }
   }

   private void method221() {
      try {
         JsonObject var1 = new JsonObject();
         var1.addProperty("enabled", this.enabled);
         var1.addProperty("port", this.port);
         var1.addProperty("token", this.text2);
         Files.createDirectories(this.path.getParent());
         Files.writeString(this.path, gson.toJson(var1), StandardCharsets.UTF_8);
      } catch (IOException var2) {
         logger.warn("MCP: не удалось сохранить настройки", var2);
      }
   }

   private synchronized void start() {
      if (!this.isRunning()) {
         InetAddress var1 = InetAddress.getLoopbackAddress();

         for (int var2 = 0; var2 < 10; var2++) {
            try {
               this.serverSocket = new ServerSocket(this.port + var2, 16, var1);
               this.port += var2;
               break;
            } catch (IOException var4) {
               this.serverSocket = null;
            }
         }

         if (this.serverSocket == null) {
            logger.warn("MCP: нет свободного порта рядом с {}", this.port);
         } else {
            this.executorService = Executors.newFixedThreadPool(2, var0 -> {
               Thread var1x = new Thread(var0, "Zenith-MCP");
               var1x.setDaemon(true);
               return var1x;
            });
            ServerSocket var5 = this.serverSocket;
            this.thread = new Thread(() -> {
               while (!var5.isClosed()) {
                  try {
                     Socket var2x = var5.accept();
                     var2x.setSoTimeout(15000);
                     this.executorService.submit(() -> this.method223(var2x));
                  } catch (IOException var3) {
                     if (!var5.isClosed()) {
                        logger.warn("MCP: ошибка приёма", var3);
                     }
                  }
               }
            }, "Zenith-MCP-accept");
            this.thread.setDaemon(true);
            this.thread.start();
            this.method221();
            this.method222();
            logger.info("MCP: слушаю {}", "http://127.0.0.1:" + this.port + "/mcp/<token>");
         }
      }
   }

   public synchronized void stop() {
      try {
         if (this.serverSocket != null) {
            this.serverSocket.close();
         }
      } catch (IOException var2) {
      }

      this.serverSocket = null;
      if (this.executorService != null) {
         this.executorService.shutdownNow();
      }

      this.executorService = null;
   }

   private void method222() {
      Path var1 = this.path.getParent();

      try {
         JsonObject var2 = new JsonObject();
         var2.addProperty("type", "http");
         var2.addProperty("url", this.url());
         JsonObject var3 = new JsonObject();
         var3.add("zenith", var2);
         JsonObject var4 = new JsonObject();
         var4.add("mcpServers", var3);
         Files.writeString(var1.resolve(".mcp.json"), gson.toJson(var4), StandardCharsets.UTF_8);
         String var5 = "# Zenith scripts — гайд для ИИ-агента\n\nВ этой папке лежат JS-скрипты клиента Zenith. Пока игра запущена и MCP включён\n(меню → Scripts → MCP), клиент отвечает по адресу из `.mcp.json`. Пользуйся инструментами,\nа не правь файлы вслепую:\n\n1. `get_api_docs` — прочитай API: оно своё, не браузерное и не Node.\n2. `list_scripts` — что есть, что включено, что упало.\n3. `read_file` / `write_file` — запись сразу перезагружает скрипт и возвращает ошибку, если она есть.\n4. `check_syntax` — проверить код без записи.\n5. `get_logs` — вывод print/console и ошибки; `run_js` — выполнить код в живом окружении скрипта.\n\nСкрипт — папка `<имя>/main.js`. Язык — ES6 (Rhino): нет class/async/spread.\nJava недоступна вовсе, только API клиента. Лимит — 250 мс на вызов.\nОбязательно прочитай раздел «Частые ошибки» в API: у сущностей методы (`player.x()`), у предметов и блоков — поля\n(`item.block`, `block.air`); `player.place` ставит блок только к опоре; долгие действия — по шагу за тик в `events.update`.\nМодуль из `Module(...)` включается в меню клиента в своей категории, переключатель скрипта только запускает скрипт.\n";
         Files.writeString(var1.resolve("CLAUDE.md"), var5, StandardCharsets.UTF_8);
         Files.writeString(var1.resolve("AGENTS.md"), var5, StandardCharsets.UTF_8);
      } catch (IOException var6) {
         logger.warn("MCP: не удалось записать файлы для агентов", var6);
      }
   }

   private void method223(Socket var1) {
      try {
         Socket var2 = var1;

         label166: {
            label167: {
               label168: {
                  label185: {
                     label169: {
                        label170: {
                           try {
                              InputStream var3 = var1.getInputStream();
                              String var4 = method224(var3);
                              if (var4 == null) {
                                 break label185;
                              }

                              String[] var5 = var4.split("\r\n");
                              String[] var6 = var5[0].split(" ");
                              if (var6.length < 2) {
                                 method225(var1, 400, null);
                                 break label168;
                              }

                              LinkedHashMap var7 = new LinkedHashMap();

                              for (int var8 = 1; var8 < var5.length; var8++) {
                                 int var9 = var5[var8].indexOf(58);
                                 if (var9 > 0) {
                                    var7.put(var5[var8].substring(0, var9).trim().toLowerCase(Locale.ROOT), var5[var8].substring(var9 + 1).trim());
                                 }
                              }

                              String var19 = ((String)var7.getOrDefault("host", ""));
                              if (!var7.containsKey("origin")
                                 && !var7.containsKey("sec-fetch-site")
                                 && (var19.equals("127.0.0.1:" + this.port) || var19.equals("localhost:" + this.port))) {
                                 if (!MessageDigest.isEqual(("/mcp/" + this.text2).getBytes(StandardCharsets.UTF_8), var6[1].getBytes(StandardCharsets.UTF_8))) {
                                    method225(var1, 404, null);
                                    break label167;
                                 }

                                 if (!"POST".equals(var6[0])) {
                                    method225(var1, 405, null);
                                    break label166;
                                 }

                                 int var20 = Integer.parseInt(((String)var7.getOrDefault("content-length", "0")));
                                 if (var20 > 0 && var20 <= 1048576) {
                                    byte[] var10 = var3.readNBytes(var20);
                                    this.atomicInteger.incrementAndGet();
                                    JsonElement var11 = JsonParser.parseString(new String(var10, StandardCharsets.UTF_8));
                                    if (var11.isJsonArray()) {
                                       JsonArray var12 = new JsonArray();

                                       for (JsonElement var14 : var11.getAsJsonArray()) {
                                          JsonObject var15 = this.method226(var14.getAsJsonObject());
                                          if (var15 != null) {
                                             var12.add(var15);
                                          }
                                       }

                                       method225(var1, var12.isEmpty() ? 202 : 200, var12.isEmpty() ? null : var12);
                                    } else {
                                       JsonObject var21 = this.method226(var11.getAsJsonObject());
                                       method225(var1, var21 == null ? 202 : 200, var21);
                                    }
                                    break label169;
                                 }

                                 method225(var1, 413, null);
                                 break label170;
                              }

                              method225(var1, 403, null);
                           } catch (Throwable var17) {
                              if (var1 != null) {
                                 try {
                                    var2.close();
                                 } catch (Throwable var16) {
                                    var17.addSuppressed(var16);
                                 }
                              }

                              throw var17;
                           }

                           if (var1 != null) {
                              var1.close();
                           }

                           return;
                        }

                        if (var1 != null) {
                           var1.close();
                        }

                        return;
                     }

                     if (var1 != null) {
                        var1.close();
                     }

                     return;
                  }

                  if (var1 != null) {
                     var1.close();
                  }

                  return;
               }

               if (var1 != null) {
                  var1.close();
               }

               return;
            }

            if (var1 != null) {
               var1.close();
            }

            return;
         }

         if (var1 != null) {
            var1.close();
         }
      } catch (Exception var18) {
         logger.warn("MCP: ошибка запроса", var18);
      }
   }

   private static String method224(InputStream var0) throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      int var2 = 0;

      while (var1.size() < 16384) {
         int var3 = var0.read();
         if (var3 < 0) {
            return null;
         }

         var1.write(var3);

         var2 = switch (var2) {
            case 0 -> var3 == 13 ? 1 : 0;
            case 1 -> var3 == 10 ? 2 : 0;
            case 2 -> var3 == 13 ? 3 : 0;
            case 3 -> var3 == 10 ? 4 : 0;
            default -> 0;
         };
         if (var2 == 4) {
            return var1.toString(StandardCharsets.ISO_8859_1).trim();
         }
      }

      return null;
   }

   private static void method225(Socket var0, int var1, JsonElement var2) throws IOException {
      byte[] var3 = var2 == null ? new byte[0] : gson.toJson(var2).getBytes(StandardCharsets.UTF_8);

      String var4 = switch (var1) {
         case 200 -> "OK";
         case 202 -> "Accepted";
         case 400 -> "Bad Request";
         case 403 -> "Forbidden";
         case 404 -> "Not Found";
         case 405 -> "Method Not Allowed";
         case 413 -> "Payload Too Large";
         default -> "Error";
      };
      String var5 = "HTTP/1.1 " + var1 + " " + var4 + "\r\nContent-Type: application/json\r\nContent-Length: " + var3.length + "\r\nConnection: close\r\n\r\n";
      OutputStream var6 = var0.getOutputStream();
      var6.write(var5.getBytes(StandardCharsets.ISO_8859_1));
      var6.write(var3);
      var6.flush();
   }

   private JsonObject method226(JsonObject var1) {
      JsonElement var2 = var1.get("id");
      String var3 = var1.has("method") ? var1.get("method").getAsString() : "";
      if (var2 != null && !var2.isJsonNull()) {
         JsonObject var4 = new JsonObject();
         var4.addProperty("jsonrpc", "2.0");
         var4.add("id", var2);

         try {
            JsonObject var5 = var1.has("params") && var1.get("params").isJsonObject() ? var1.getAsJsonObject("params") : new JsonObject();
            switch (var3) {
               case "initialize":
                  JsonObject var14 = new JsonObject();
                  var14.addProperty("protocolVersion", var5.has("protocolVersion") ? var5.get("protocolVersion").getAsString() : "2025-06-18");
                  JsonObject var15 = new JsonObject();
                  var15.add("tools", new JsonObject());
                  var14.add("capabilities", var15);
                  JsonObject var10 = new JsonObject();
                  var10.addProperty("name", "zenith-scripts");
                  var10.addProperty("version", String.valueOf(3));
                  var14.add("serverInfo", var10);
                  var14.addProperty("instructions", "Сначала get_api_docs, затем list_scripts. write_file сразу перезагружает скрипт и возвращает ошибку.");
                  var4.add("result", var14);
                  break;
               case "ping":
                  var4.add("result", new JsonObject());
                  break;
               case "tools/list":
                  JsonObject var13 = new JsonObject();
                  var13.add("tools", McpToolSchema.method5499());
                  var4.add("result", var13);
                  break;
               case "tools/call":
                  String var12 = var5.get("name").getAsString();
                  JsonObject var9 = var5.has("arguments") && var5.get("arguments").isJsonObject() ? var5.getAsJsonObject("arguments") : new JsonObject();
                  var4.add("result", this.method227(var12, var9));
                  break;
               default:
                  JsonObject var8 = new JsonObject();
                  var8.addProperty("code", 32935);
                  var8.addProperty("message", "метод не найден: " + var3);
                  var4.add("error", var8);
            }
         } catch (Exception var11) {
            JsonObject var6 = new JsonObject();
            var6.addProperty("code", 32933);
            var6.addProperty("message", String.valueOf(var11.getMessage()));
            var4.add("error", var6);
         }

         return var4;
      } else {
         return null;
      }
   }

   private JsonObject method227(String var1, JsonObject var2) {
      JsonObject var3 = new JsonObject();
      JsonArray var4 = new JsonArray();
      boolean var6 = false;

      String var5;
      try {
         var5 = method228(() -> McpToolSchema.method5502(var1, var2));
      } catch (Exception var8) {
         var5 = String.valueOf(var8.getMessage() == null ? var8 : var8.getMessage());
         var6 = true;
      }

      JsonObject var7 = new JsonObject();
      var7.addProperty("type", "text");
      var7.addProperty("text", var5);
      var4.add(var7);
      var3.add("content", var4);
      var3.addProperty("isError", var6);
      return var3;
   }

   private static String method228(Server.Handler var0) throws Exception {
      MinecraftClient var1 = MinecraftClient.getInstance();
      CompletableFuture var2 = new CompletableFuture();
      var1.execute(() -> {
         try {
            var2.complete(var0.run());
         } catch (Throwable var3) {
            var2.completeExceptionally(var3);
         }
      });

      try {
         return (String)var2.get(5L, TimeUnit.SECONDS);
      } catch (ExecutionException var6) {
         Throwable var4 = var6.getCause();
         throw (Exception)(var4 instanceof Exception var5 ? var5 : new RuntimeException(var4));
      }
   }

   public static String method229() {
      return String.join(", ", ScriptHeaderParser.items);
   }

   public static Script method230(Storage var0, JsonObject var1) {
      String var2 = var1.has("script") ? var1.get("script").getAsString() : "";
      Script var3 = var0.method1294(var2);
      if (var3 == null) {
         throw new IllegalArgumentException("скрипт не найден: " + var2);
      } else {
         return var3;
      }
   }

   @FunctionalInterface
   private interface Handler {
      String run() throws Exception;
   }
}
