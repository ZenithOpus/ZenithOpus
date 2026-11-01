package org.zenith.utility.misc;

import com.google.common.reflect.TypeToken;
import java.util.HashSet;
import java.util.Set;
import org.zenith.utility.api.JsonListStorage;

public class StaffName extends JsonListStorage<String> {
   public StaffName() {
      super("staffName.json", "", (new TypeToken<Set<String>>() {}).getType(), HashSet::new);
   }

   public boolean method1668(String var1) {
      return this.getItems().contains(var1);
   }
}
