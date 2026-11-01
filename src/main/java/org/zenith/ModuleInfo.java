package org.zenith;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {
   String name();

   ModuleCategory category();

   String description();

   boolean method894() default false;

   String method895() default "";

   boolean method896() default false;
}
