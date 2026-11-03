package org.zenith.utility.session;

import java.awt.*;
import java.awt.image.BufferedImage;

/** Provides a way back to the original window even when no guest is connected. */
final class SessionTray {
    private TrayIcon icon;

    boolean show(SharedSession session) {
        if (icon != null) return true;
        try {
            if (!SystemTray.isSupported()) return false;
            BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(new Color(104, 102, 255));
            graphics.fillRoundRect(0, 0, 32, 32, 10, 10);
            graphics.setColor(Color.WHITE);
            graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
            graphics.drawString("Z", 8, 25);
            graphics.dispose();
            PopupMenu menu = new PopupMenu();
            MenuItem restore = new MenuItem("Открыть ZenithOpus");
            restore.addActionListener(event -> session.client().execute(session::restoreWindow));
            menu.add(restore);
            MenuItem release = new MenuItem("Вернуть управление на этот ПК");
            release.addActionListener(event -> session.client().execute(() -> { session.reclaim(); session.restoreWindow(); }));
            menu.add(release);
            menu.addSeparator();
            MenuItem quit = new MenuItem("Завершить Minecraft и сессию");
            quit.addActionListener(event -> session.client().execute(() -> { session.stop(); session.client().scheduleStop(); }));
            menu.add(quit);
            TrayIcon created = new TrayIcon(image, "ZenithOpus · Общая сессия", menu);
            created.setImageAutoSize(true);
            created.addActionListener(event -> session.client().execute(session::restoreWindow));
            SystemTray.getSystemTray().add(created);
            icon = created;
            return true;
        } catch (RuntimeException | AWTException e) {
            SharedSession.LOGGER.debug("System tray unavailable; keeping the window in the taskbar", e);
            return false;
        }
    }

    void remove() {
        if (icon != null) {
            SystemTray.getSystemTray().remove(icon);
            icon = null;
        }
    }
}
