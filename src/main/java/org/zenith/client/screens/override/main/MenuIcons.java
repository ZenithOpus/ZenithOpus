package org.zenith.client.screens.override.main;

import org.zenith.ModuleCategory;
import org.zenith.base.font.SvgIcon;

public final class MenuIcons {
   public static final MenuIcon ZENITH = MenuIcon.legacy('5');
   public static final MenuIcon HOME = MenuIcon.svg(SvgIcon.HOME);
   public static final MenuIcon SINGLEPLAYER = MenuIcon.svg(SvgIcon.SINGLEPLAYER);
   public static final MenuIcon MULTIPLAYER = MenuIcon.svg(SvgIcon.MULTIPLAYER);
   public static final MenuIcon ALT_MANAGER = MenuIcon.svg(SvgIcon.ACCOUNTS);
   public static final MenuIcon PROXY = MenuIcon.svg(SvgIcon.PROXY);
   public static final MenuIcon SETTINGS = MenuIcon.svg(SvgIcon.SETTINGS);
   public static final MenuIcon EXIT = MenuIcon.svg(SvgIcon.EXIT);
   public static final MenuIcon ACCEPT = MenuIcon.svg(SvgIcon.CHECK);
   public static final MenuIcon NEXT = MenuIcon.svg(SvgIcon.ACTION);
   public static final MenuIcon ADD = MenuIcon.svg(SvgIcon.PLUS);
   public static final MenuIcon PLUS = ADD;
   public static final MenuIcon MINUS = MenuIcon.svg(SvgIcon.MINUS);
   public static final MenuIcon REMOVE = MenuIcon.svg(SvgIcon.REMOVE);
   public static final MenuIcon CLEAR = MenuIcon.svg(SvgIcon.CLEAR);
   public static final MenuIcon RANDOM = MenuIcon.svg(SvgIcon.RANDOM);
   public static final MenuIcon SPARKLE = MenuIcon.svg(SvgIcon.SPARKLE);
   public static final MenuIcon CONFIGURE = MenuIcon.svg(SvgIcon.SLIDER);
   public static final MenuIcon PLAY = MenuIcon.svg(SvgIcon.PLAY);
   public static final MenuIcon REFRESH = MenuIcon.svg(SvgIcon.REFRESH);
   public static final MenuIcon CHEVRON_UP = MenuIcon.svg(SvgIcon.CHEVRON_UP);
   public static final MenuIcon CHEVRON_DOWN = MenuIcon.svg(SvgIcon.CHEVRON_DOWN);
   public static final MenuIcon CHEVRON_RIGHT = MenuIcon.svg(SvgIcon.CHEVRON_RIGHT);
   public static final MenuIcon CHEVRON_LEFT = MenuIcon.svg(SvgIcon.CHEVRON_LEFT);
   public static final MenuIcon SEARCH = MenuIcon.svg(SvgIcon.SEARCH);
   public static final MenuIcon GRID = MenuIcon.svg(SvgIcon.ITEMS);
   public static final MenuIcon WARNING = MenuIcon.svg(SvgIcon.WARNING);
   public static final MenuIcon LAUNCH = MenuIcon.svg(SvgIcon.LAUNCH);
   public static final MenuIcon USER = MenuIcon.svg(SvgIcon.PROFILE);
   public static final MenuIcon SERVER = MenuIcon.svg(SvgIcon.SERVER);
   public static final MenuIcon ADDRESS = MenuIcon.svg(SvgIcon.ADDRESS);
   public static final MenuIcon WORLD = MenuIcon.svg(SvgIcon.WORLD);
   public static final MenuIcon SUBSCRIPTION = MenuIcon.svg(SvgIcon.SUBSCRIPTION);
   public static final MenuIcon UID = MenuIcon.svg(SvgIcon.UID);
   public static final MenuIcon PLAYTIME = MenuIcon.svg(SvgIcon.PLAYTIME);
   public static final MenuIcon ONLINE = MULTIPLAYER;
   public static final MenuIcon ROLE = MenuIcon.svg(SvgIcon.ROLE);
   public static final MenuIcon VERSION = MenuIcon.svg(SvgIcon.VERSION);
   public static final MenuIcon CATEGORY_COMBAT = MenuIcon.svg(SvgIcon.COMBAT);
   public static final MenuIcon CATEGORY_MOVEMENT = MenuIcon.svg(SvgIcon.MOVEMENT);
   public static final MenuIcon CATEGORY_PLAYER = MenuIcon.svg(SvgIcon.PLAYER);
   public static final MenuIcon CATEGORY_RENDER = MenuIcon.svg(SvgIcon.VISUALS);
   public static final MenuIcon CATEGORY_MISC = MenuIcon.svg(SvgIcon.MISC);
   public static final MenuIcon FRIENDS = MenuIcon.svg(SvgIcon.FRIENDS);
   public static final MenuIcon CONFIGS = MenuIcon.svg(SvgIcon.CONFIGS);
   public static final MenuIcon INTERFACE = MenuIcon.svg(SvgIcon.INTERFACE);
   public static final MenuIcon COSMETICS = MenuIcon.svg(SvgIcon.COSMETICS);
   public static final MenuIcon VIDEO = MenuIcon.svg(SvgIcon.VIDEO);
   public static final MenuIcon CONTROLS = MenuIcon.svg(SvgIcon.CONTROLS);
   public static final MenuIcon MOUSE = MenuIcon.svg(SvgIcon.MOUSE);
   public static final MenuIcon SOUND = MenuIcon.svg(SvgIcon.SOUND);
   public static final MenuIcon CHAT = MenuIcon.svg(SvgIcon.CHAT);
   public static final MenuIcon ACCESSIBILITY = MenuIcon.svg(SvgIcon.ACCESSIBILITY);
   public static final MenuIcon TEXT = MenuIcon.svg(SvgIcon.TEXT);

   private MenuIcons() {
   }

   public static MenuIcon forCategory(ModuleCategory var0) {
      return switch (var0) {
         case COMBAT -> CATEGORY_COMBAT;
         case MOVEMENT -> CATEGORY_MOVEMENT;
         case MISC -> CATEGORY_MISC;
         case RENDER -> CATEGORY_RENDER;
         case PLAYER -> CATEGORY_PLAYER;
         default -> INTERFACE;
      };
   }
}
