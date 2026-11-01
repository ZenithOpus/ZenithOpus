package org.zenith.utility.misc;

import lombok.Generated;
import org.zenith.ZenithColor;

public class ThemeStyle {
   private final String text;
   private final String text2;
   private final ThemeStyle.Data data;
   private ZenithColor zenithColor;
   private ZenithColor zenithColor2;
   private ZenithColor zenithColor3;
   private ZenithColor zenithColor4;
   private ZenithColor zenithColor5;
   private ZenithColor zenithColor6;
   private ZenithColor zenithColor7;
   private ZenithColor zenithColor8;
   private ZenithColor zenithColor9;
   private ZenithColor zenithColor10;
   private ZenithColor zenithColor11;
   private ZenithColor zenithColor12;
   private ZenithColor zenithColor13;
   private ZenithColor zenithColor14;
   private ZenithColor zenithColor15;
   private ZenithColor zenithColor16;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4 = false;
   public static final ThemeStyle INSTANCE = ThemeStyle.Entry.method2838()
      .method2839(new ZenithColor(181, 162, 255))
      .method2840(new ZenithColor(255, 203, 162))
      .method2841(ZenithColor.INSTANCE3)
      .method2842(new ZenithColor(181, 162, 255))
      .method2843(new ZenithColor(255, 203, 162))
      .method2844(new ZenithColor(88, 87, 93))
      .method2845(new ZenithColor(128, 127, 133))
      .method2846(new ZenithColor(32, 31, 37))
      .method2847(new ZenithColor(68, 67, 73))
      .method2848(new ZenithColor(48, 47, 53))
      .method2849(new ZenithColor(38, 37, 43))
      .method2850(new ZenithColor(28, 27, 33))
      .method2851(new ZenithColor(35, 34, 40))
      .method2852(new ZenithColor(25, 24, 30))
      .method2853(new ZenithColor(255, 255, 255))
      .method2854(new ZenithColor(23, 22, 28))
      .method2855(false)
      .method2856(false)
      .method2857(false)
      .method2858("Dark", "8");
   public static final ThemeStyle INSTANCE2 = ThemeStyle.Entry.method2838()
      .method2839(new ZenithColor(123, 93, 234))
      .method2840(new ZenithColor(255, 192, 121))
      .method2841(ZenithColor.INSTANCE3)
      .method2842(new ZenithColor(123, 93, 234))
      .method2843(new ZenithColor(255, 192, 121))
      .method2844(new ZenithColor(138, 137, 143))
      .method2845(new ZenithColor(148, 147, 153))
      .method2846(new ZenithColor(236, 236, 236))
      .method2847(new ZenithColor(178, 177, 183))
      .method2848(new ZenithColor(188, 187, 193))
      .method2849(new ZenithColor(229, 229, 229))
      .method2850(new ZenithColor(246, 246, 246))
      .method2851(new ZenithColor(229, 229, 229))
      .method2852(new ZenithColor(251, 251, 251))
      .method2853(new ZenithColor(23, 22, 28))
      .method2854(new ZenithColor(255, 255, 255))
      .method2855(false)
      .method2856(false)
      .method2857(false)
      .method2858("Light", "T");
   public static final ThemeStyle INSTANCE3 = ThemeStyle.Entry.method2838()
      .method2839(new ZenithColor(181, 162, 255))
      .method2840(new ZenithColor(255, 203, 162))
      .method2841(ZenithColor.INSTANCE3)
      .method2842(new ZenithColor(181, 162, 255))
      .method2843(new ZenithColor(255, 203, 162))
      .method2844(new ZenithColor(88, 87, 93))
      .method2845(new ZenithColor(128, 127, 133))
      .method2846(new ZenithColor(32, 31, 37))
      .method2847(new ZenithColor(68, 67, 73))
      .method2848(new ZenithColor(48, 47, 53))
      .method2849(new ZenithColor(38, 37, 43))
      .method2850(new ZenithColor(28, 27, 33))
      .method2851(new ZenithColor(35, 34, 40))
      .method2852(new ZenithColor(25, 24, 30))
      .method2853(new ZenithColor(255, 255, 255))
      .method2854(new ZenithColor(23, 22, 28))
      .method2855(false)
      .method2856(false)
      .method2857(false)
      .method2858("Custom", "F");

   public ThemeStyle(String var1, String var2, ThemeStyle.Entry var3) {
      this.text = var1;
      this.text2 = var2;
      this.zenithColor = var3.zenithColor;
      this.zenithColor2 = var3.zenithColor2;
      this.zenithColor3 = var3.zenithColor3 != null ? var3.zenithColor3 : ZenithColor.INSTANCE3;
      this.zenithColor4 = var3.zenithColor4 != null ? var3.zenithColor4 : this.zenithColor2;
      this.zenithColor5 = var3.zenithColor5 != null ? var3.zenithColor5 : this.zenithColor2;
      this.zenithColor6 = var3.zenithColor6;
      this.zenithColor7 = var3.zenithColor7;
      this.zenithColor8 = var3.zenithColor8;
      this.zenithColor9 = var3.zenithColor9;
      this.zenithColor10 = var3.zenithColor10;
      this.zenithColor11 = var3.zenithColor11;
      this.zenithColor12 = var3.zenithColor12;
      this.zenithColor13 = var3.zenithColor13;
      this.zenithColor14 = var3.zenithColor14;
      this.zenithColor15 = var3.zenithColor15;
      this.zenithColor16 = var3.zenithColor16;
      this.flag = var3.flag;
      this.flag2 = var3.flag2;
      this.flag3 = var3.flag3;
      this.data = new ThemeStyle.Data(
         var3.zenithColor,
         var3.zenithColor2,
         var3.zenithColor3,
         var3.zenithColor6,
         var3.zenithColor7,
         var3.zenithColor8,
         var3.zenithColor9,
         var3.zenithColor10,
         var3.zenithColor11,
         var3.zenithColor12,
         var3.zenithColor13,
         var3.zenithColor14,
         var3.zenithColor15,
         var3.zenithColor16,
         var3.flag,
         var3.flag2,
         var3.flag3
      );
   }

   public void reset() {
      this.zenithColor = this.data.zenithColor;
      this.zenithColor2 = this.data.zenithColor2;
      this.zenithColor3 = this.data.zenithColor3;
      this.zenithColor6 = this.data.zenithColor4;
      this.zenithColor7 = this.data.zenithColor5;
      this.zenithColor8 = this.data.zenithColor6;
      this.zenithColor9 = this.data.zenithColor7;
      this.zenithColor10 = this.data.zenithColor8;
      this.zenithColor11 = this.data.zenithColor9;
      this.zenithColor12 = this.data.zenithColor10;
      this.zenithColor13 = this.data.zenithColor11;
      this.zenithColor14 = this.data.zenithColor12;
      this.zenithColor15 = this.data.zenithColor13;
      this.zenithColor16 = this.data.zenithColor14;
      this.flag = this.data.flag;
      this.flag2 = this.data.flag2;
      this.flag3 = this.data.flag3;
      this.flag4 = true;
   }

   public ThemeStyle method2837(ThemeStyle var1, float var2) {
      return ThemeStyle.Entry.method2838()
         .method2839(this.zenithColor.method7295(var1.getZenithColor(), var2))
         .method2840(this.zenithColor2.method7295(var1.getZenithColor2(), var2))
         .method2841(this.zenithColor3.method7295(var1.getZenithColor3(), var2))
         .method2844(this.zenithColor6.method7295(var1.getZenithColor6(), var2))
         .method2845(this.zenithColor7.method7295(var1.getZenithColor7(), var2))
         .method2846(this.zenithColor8.method7295(var1.getZenithColor8(), var2))
         .method2847(this.zenithColor9.method7295(var1.getZenithColor9(), var2))
         .method2848(this.zenithColor10.method7295(var1.getZenithColor10(), var2))
         .method2849(this.zenithColor11.method7295(var1.getZenithColor11(), var2))
         .method2850(this.zenithColor12.method7295(var1.getZenithColor12(), var2))
         .method2851(this.zenithColor13.method7295(var1.getZenithColor13(), var2))
         .method2852(this.zenithColor14.method7295(var1.getZenithColor14(), var2))
         .method2853(this.zenithColor15.method7295(var1.getZenithColor15(), var2))
         .method2854(this.zenithColor16.method7295(var1.getZenithColor16(), var2))
         .method2855(this.flag)
         .method2856(this.flag2)
         .method2857(this.flag3)
         .method2858(var1.text, var1.text2);
   }

   @Generated
   public String getName() {
      return this.text;
   }

   @Generated
   public String getIcon() {
      return this.text2;
   }

   @Generated
   public ThemeStyle.Data getData() {
      return this.data;
   }

   @Generated
   public ZenithColor getZenithColor() {
      return this.zenithColor;
   }

   @Generated
   public ZenithColor getZenithColor2() {
      return this.zenithColor2;
   }

   @Generated
   public ZenithColor getZenithColor3() {
      return this.zenithColor3;
   }

   @Generated
   public ZenithColor getZenithColor4() {
      return this.zenithColor4;
   }

   @Generated
   public ZenithColor getZenithColor5() {
      return this.zenithColor5;
   }

   @Generated
   public ZenithColor getZenithColor6() {
      return this.zenithColor6;
   }

   @Generated
   public ZenithColor getZenithColor7() {
      return this.zenithColor7;
   }

   @Generated
   public ZenithColor getZenithColor8() {
      return this.zenithColor8;
   }

   @Generated
   public ZenithColor getZenithColor9() {
      return this.zenithColor9;
   }

   @Generated
   public ZenithColor getZenithColor10() {
      return this.zenithColor10;
   }

   @Generated
   public ZenithColor getZenithColor11() {
      return this.zenithColor11;
   }

   @Generated
   public ZenithColor getZenithColor12() {
      return this.zenithColor12;
   }

   @Generated
   public ZenithColor getZenithColor13() {
      return this.zenithColor13;
   }

   @Generated
   public ZenithColor getZenithColor14() {
      return this.zenithColor14;
   }

   @Generated
   public ZenithColor getZenithColor15() {
      return this.zenithColor15;
   }

   @Generated
   public ZenithColor getZenithColor16() {
      return this.zenithColor16;
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public boolean isFlag2() {
      return this.flag2;
   }

   @Generated
   public boolean isFlag3() {
      return this.flag3;
   }

   @Generated
   public boolean isFlag4() {
      return this.flag4;
   }

   @Generated
   public void setZenithColor(ZenithColor var1) {
      this.zenithColor = var1;
   }

   @Generated
   public void setZenithColor2(ZenithColor var1) {
      this.zenithColor2 = var1;
   }

   @Generated
   public void setZenithColor3(ZenithColor var1) {
      this.zenithColor3 = var1;
   }

   @Generated
   public void setZenithColor4(ZenithColor var1) {
      this.zenithColor4 = var1;
   }

   @Generated
   public void setZenithColor5(ZenithColor var1) {
      this.zenithColor5 = var1;
   }

   @Generated
   public void setZenithColor6(ZenithColor var1) {
      this.zenithColor6 = var1;
   }

   @Generated
   public void setZenithColor7(ZenithColor var1) {
      this.zenithColor7 = var1;
   }

   @Generated
   public void setZenithColor8(ZenithColor var1) {
      this.zenithColor8 = var1;
   }

   @Generated
   public void setZenithColor9(ZenithColor var1) {
      this.zenithColor9 = var1;
   }

   @Generated
   public void setZenithColor10(ZenithColor var1) {
      this.zenithColor10 = var1;
   }

   @Generated
   public void setZenithColor11(ZenithColor var1) {
      this.zenithColor11 = var1;
   }

   @Generated
   public void setZenithColor12(ZenithColor var1) {
      this.zenithColor12 = var1;
   }

   @Generated
   public void setZenithColor13(ZenithColor var1) {
      this.zenithColor13 = var1;
   }

   @Generated
   public void setZenithColor14(ZenithColor var1) {
      this.zenithColor14 = var1;
   }

   @Generated
   public void setZenithColor15(ZenithColor var1) {
      this.zenithColor15 = var1;
   }

   @Generated
   public void setZenithColor16(ZenithColor var1) {
      this.zenithColor16 = var1;
   }

   @Generated
   public void setFlag(boolean var1) {
      this.flag = var1;
   }

   @Generated
   public void setFlag2(boolean var1) {
      this.flag2 = var1;
   }

   @Generated
   public void setFlag3(boolean var1) {
      this.flag3 = var1;
   }

   @Generated
   public void setFlag4(boolean var1) {
      this.flag4 = var1;
   }

   private static final class Data {
      private final ZenithColor zenithColor;
      private final ZenithColor zenithColor2;
      private final ZenithColor zenithColor3;
      private final ZenithColor zenithColor4;
      private final ZenithColor zenithColor5;
      private final ZenithColor zenithColor6;
      private final ZenithColor zenithColor7;
      private final ZenithColor zenithColor8;
      private final ZenithColor zenithColor9;
      private final ZenithColor zenithColor10;
      private final ZenithColor zenithColor11;
      private final ZenithColor zenithColor12;
      private final ZenithColor zenithColor13;
      private final ZenithColor zenithColor14;
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;

      public ZenithColor zenithColor() {
         return this.zenithColor;
      }

      public ZenithColor zenithColor2() {
         return this.zenithColor2;
      }

      public ZenithColor zenithColor3() {
         return this.zenithColor3;
      }

      public ZenithColor zenithColor4() {
         return this.zenithColor4;
      }

      public ZenithColor zenithColor5() {
         return this.zenithColor5;
      }

      public ZenithColor zenithColor6() {
         return this.zenithColor6;
      }

      public ZenithColor zenithColor7() {
         return this.zenithColor7;
      }

      public ZenithColor zenithColor8() {
         return this.zenithColor8;
      }

      public ZenithColor zenithColor9() {
         return this.zenithColor9;
      }

      public ZenithColor zenithColor10() {
         return this.zenithColor10;
      }

      public ZenithColor zenithColor11() {
         return this.zenithColor11;
      }

      public ZenithColor zenithColor12() {
         return this.zenithColor12;
      }

      public ZenithColor zenithColor13() {
         return this.zenithColor13;
      }

      public ZenithColor zenithColor14() {
         return this.zenithColor14;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.zenithColor, other.zenithColor) && java.util.Objects.equals(this.zenithColor2, other.zenithColor2) && java.util.Objects.equals(this.zenithColor3, other.zenithColor3) && java.util.Objects.equals(this.zenithColor4, other.zenithColor4) && java.util.Objects.equals(this.zenithColor5, other.zenithColor5) && java.util.Objects.equals(this.zenithColor6, other.zenithColor6) && java.util.Objects.equals(this.zenithColor7, other.zenithColor7) && java.util.Objects.equals(this.zenithColor8, other.zenithColor8) && java.util.Objects.equals(this.zenithColor9, other.zenithColor9) && java.util.Objects.equals(this.zenithColor10, other.zenithColor10) && java.util.Objects.equals(this.zenithColor11, other.zenithColor11) && java.util.Objects.equals(this.zenithColor12, other.zenithColor12) && java.util.Objects.equals(this.zenithColor13, other.zenithColor13) && java.util.Objects.equals(this.zenithColor14, other.zenithColor14) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.flag3, other.flag3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(zenithColor, zenithColor2, zenithColor3, zenithColor4, zenithColor5, zenithColor6, zenithColor7, zenithColor8, zenithColor9, zenithColor10, zenithColor11, zenithColor12, zenithColor13, zenithColor14, flag, flag2, flag3);
      }

      @Override
      public String toString() {
         return "Data[" + "zenithColor=" + this.zenithColor + ", " + "zenithColor2=" + this.zenithColor2 + ", " + "zenithColor3=" + this.zenithColor3 + ", " + "zenithColor4=" + this.zenithColor4 + ", " + "zenithColor5=" + this.zenithColor5 + ", " + "zenithColor6=" + this.zenithColor6 + ", " + "zenithColor7=" + this.zenithColor7 + ", " + "zenithColor8=" + this.zenithColor8 + ", " + "zenithColor9=" + this.zenithColor9 + ", " + "zenithColor10=" + this.zenithColor10 + ", " + "zenithColor11=" + this.zenithColor11 + ", " + "zenithColor12=" + this.zenithColor12 + ", " + "zenithColor13=" + this.zenithColor13 + ", " + "zenithColor14=" + this.zenithColor14 + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "flag3=" + this.flag3 + "]";
      }

      private Data(
         ZenithColor var1,
         ZenithColor var2,
         ZenithColor var3,
         ZenithColor var4,
         ZenithColor var5,
         ZenithColor var6,
         ZenithColor var7,
         ZenithColor var8,
         ZenithColor var9,
         ZenithColor var10,
         ZenithColor var11,
         ZenithColor var12,
         ZenithColor var13,
         ZenithColor var14,
         boolean var15,
         boolean var16,
         boolean var17
      ) {
         this.zenithColor = var1;
         this.zenithColor2 = var2;
         this.zenithColor3 = var3;
         this.zenithColor4 = var4;
         this.zenithColor5 = var5;
         this.zenithColor6 = var6;
         this.zenithColor7 = var7;
         this.zenithColor8 = var8;
         this.zenithColor9 = var9;
         this.zenithColor10 = var10;
         this.zenithColor11 = var11;
         this.zenithColor12 = var12;
         this.zenithColor13 = var13;
         this.zenithColor14 = var14;
         this.flag = var15;
         this.flag2 = var16;
         this.flag3 = var17;
      }

      public ZenithColor getZenithColor() {
         return this.zenithColor;
      }

      public ZenithColor getZenithColor2() {
         return this.zenithColor2;
      }

      public ZenithColor getZenithColor3() {
         return this.zenithColor3;
      }

      public ZenithColor getZenithColor4() {
         return this.zenithColor4;
      }

      public ZenithColor getZenithColor5() {
         return this.zenithColor5;
      }

      public ZenithColor getZenithColor6() {
         return this.zenithColor6;
      }

      public ZenithColor getZenithColor7() {
         return this.zenithColor7;
      }

      public ZenithColor getZenithColor8() {
         return this.zenithColor8;
      }

      public ZenithColor getZenithColor9() {
         return this.zenithColor9;
      }

      public ZenithColor getZenithColor10() {
         return this.zenithColor10;
      }

      public ZenithColor getZenithColor11() {
         return this.zenithColor11;
      }

      public ZenithColor getZenithColor12() {
         return this.zenithColor12;
      }

      public ZenithColor getZenithColor13() {
         return this.zenithColor13;
      }

      public ZenithColor getZenithColor14() {
         return this.zenithColor14;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }
   }

   public static class Entry {
      private ZenithColor zenithColor;
      private ZenithColor zenithColor2;
      private ZenithColor zenithColor3;
      private ZenithColor zenithColor4;
      private ZenithColor zenithColor5;
      private ZenithColor zenithColor6;
      private ZenithColor zenithColor7;
      private ZenithColor zenithColor8;
      private ZenithColor zenithColor9;
      private ZenithColor zenithColor10;
      private ZenithColor zenithColor11;
      private ZenithColor zenithColor12;
      private ZenithColor zenithColor13;
      private ZenithColor zenithColor14;
      private ZenithColor zenithColor15;
      private ZenithColor zenithColor16;
      private boolean flag = false;
      private boolean flag2 = false;
      private boolean flag3 = false;

      public static ThemeStyle.Entry method2838() {
         return new ThemeStyle.Entry();
      }

      public ThemeStyle.Entry method2839(ZenithColor var1) {
         this.zenithColor = var1;
         return this;
      }

      public ThemeStyle.Entry method2840(ZenithColor var1) {
         this.zenithColor2 = var1;
         return this;
      }

      public ThemeStyle.Entry method2841(ZenithColor var1) {
         this.zenithColor3 = var1;
         return this;
      }

      public ThemeStyle.Entry method2842(ZenithColor var1) {
         this.zenithColor4 = var1;
         return this;
      }

      public ThemeStyle.Entry method2843(ZenithColor var1) {
         this.zenithColor5 = var1;
         return this;
      }

      public ThemeStyle.Entry method2844(ZenithColor var1) {
         this.zenithColor6 = var1;
         return this;
      }

      public ThemeStyle.Entry method2845(ZenithColor var1) {
         this.zenithColor7 = var1;
         return this;
      }

      public ThemeStyle.Entry method2846(ZenithColor var1) {
         this.zenithColor8 = var1;
         return this;
      }

      public ThemeStyle.Entry method2847(ZenithColor var1) {
         this.zenithColor9 = var1;
         return this;
      }

      public ThemeStyle.Entry method2848(ZenithColor var1) {
         this.zenithColor10 = var1;
         return this;
      }

      public ThemeStyle.Entry method2849(ZenithColor var1) {
         this.zenithColor11 = var1;
         return this;
      }

      public ThemeStyle.Entry method2850(ZenithColor var1) {
         this.zenithColor12 = var1;
         return this;
      }

      public ThemeStyle.Entry method2851(ZenithColor var1) {
         this.zenithColor13 = var1;
         return this;
      }

      public ThemeStyle.Entry method2852(ZenithColor var1) {
         this.zenithColor14 = var1;
         return this;
      }

      public ThemeStyle.Entry method2853(ZenithColor var1) {
         this.zenithColor15 = var1;
         return this;
      }

      public ThemeStyle.Entry method2854(ZenithColor var1) {
         this.zenithColor16 = var1;
         return this;
      }

      public ThemeStyle.Entry method2855(boolean var1) {
         this.flag = var1;
         return this;
      }

      public ThemeStyle.Entry method2856(boolean var1) {
         this.flag2 = var1;
         return this;
      }

      public ThemeStyle.Entry method2857(boolean var1) {
         this.flag3 = var1;
         return this;
      }

      public ThemeStyle method2858(String var1, String var2) {
         return new ThemeStyle(var1, var2, this);
      }
   }
}
