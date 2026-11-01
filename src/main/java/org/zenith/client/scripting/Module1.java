package org.zenith.client.scripting;

import com.darkmagician6.eventapi.EventManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.mozilla.javascript.Function;
import org.zenith.ModuleCategory;
import org.zenith.base.Module;
import org.zenith.client.events.ModuleToggleEvent;
import org.zenith.client.settings.Setting;

public final class Module1 extends Module {
   private final Script script;
   private final List<Setting> items = new ArrayList<>();
   private Function function;
   private Function function2;
   private boolean flag;
   public boolean flag2;
   public boolean flag3;

   public Module1(Script var1, String var2, String var3, ModuleCategory var4) {
      super(method7805(var1, var2), var2, var3, var4);
      this.script = var1;
   }

   public static Module1 method7802(Script var0, String var1, String var2, ModuleCategory var3) {
      return new Module1(var0, var1, var2, var3);
   }

   public static Module1 method7803(Script var0, String var1) {
      Module1 var2 = new Module1(var0, "__" + var1, "", ModuleCategory.MISC);
      var2.flag3 = true;
      return var2;
   }

   public void setFlag2(boolean var1) {
      this.flag2 = var1;
   }

   public void setFunction(Function var1) {
      this.function = var1;
   }

   public void setFunction2(Function var1) {
      this.function2 = var1;
   }

   public void method7804(Setting var1) {
      this.items.add(var1);
   }

   public static String method7805(Script var0, String var1) {
      return "script:" + var0.getId() + ":" + var1.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9а-яё_-]", "_");
   }

   public Script getScript() {
      return this.script;
   }

   public void release() {
      this.flag = true;
   }

   @Override
   public String getDescription() {
      String var1 = super.getDescription();
      return var1.isBlank() ? "Скрипт " + this.script.method1403() : var1;
   }

   @Override
   public List<Setting> getSettings() {
      return Collections.unmodifiableList(this.items);
   }

   @Override
   public boolean isEnabled() {
      return super.isEnabled() && this.script.isRunning();
   }

   @Override
   public boolean isHidden() {
      return this.flag3;
   }

   @Override
   public void onEnable() {
      if (!this.flag3) {
         EventManager.call(new ModuleToggleEvent(this, true));
      }

      if (!this.flag && this.function != null) {
         this.script.getStorage().method1321(this.script, this.function);
      }
   }

   @Override
   public void onDisable() {
      if (!this.flag3) {
         EventManager.call(new ModuleToggleEvent(this, false));
      }

      if (!this.flag && this.function2 != null) {
         this.script.getStorage().method1321(this.script, this.function2);
      }
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1;
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this);
   }

   @Override
   public String toString() {
      return this.getId();
   }
}
