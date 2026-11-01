package org.zenith.base.filemanager.impl.way;

import com.google.gson.annotations.SerializedName;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.ClickEvent.CopyToClipboard;
import net.minecraft.text.ClickEvent.SuggestCommand;
import net.minecraft.text.HoverEvent.ShowText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class Way {
   @SerializedName("name")
   private final String name;
   @SerializedName("pos")
   private final BlockPos pos;
   @SerializedName("server")
   private final String server;

   public Way(String var1, BlockPos var2, String var3) {
      this.name = var1;
      this.pos = var2;
      this.server = var3;
   }

   public String name() {
      return this.name;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public String server() {
      return this.server;
   }

   public Text toText() {
      String var1 = this.pos.getX() + " " + this.pos.getY() + " " + this.pos.getZ();
      String var2 = this.name.replace("\"", "\\\"");
      MutableText var3 = Text.literal("? " + this.name);
      var3.setStyle(
         Style.EMPTY
            .withBold(true)
            .withColor(TextColor.fromRgb(16765286))
            .withHoverEvent(new ShowText(Text.literal("Место: " + this.name + "\nСервер: " + this.server)))
      );
      MutableText var4 = Text.literal("  ⟨" + this.pos.getX() + ", " + this.pos.getY() + ", " + this.pos.getZ() + "⟩");
      var4.setStyle(
         Style.EMPTY
            .withColor(TextColor.fromRgb(1149618))
            .withHoverEvent(new ShowText(Text.literal("Нажми, чтобы скопировать координаты")))
            .withClickEvent(new CopyToClipboard(var1))
      );
      MutableText var5 = Text.literal("  @" + this.server);
      var5.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
      MutableText var6 = Text.literal("  [Удалить]");
      var6.setStyle(
         Style.EMPTY
            .withBold(true)
            .withColor(TextColor.fromRgb(15681391))
            .withHoverEvent(new ShowText(Text.literal("Подставить команду удаления точки " + this.name)))
            .withClickEvent(new SuggestCommand(".way remove " + var2))
      );
      return Text.empty().append(var3).append(var4).append(var5).append(var6);
   }
}
