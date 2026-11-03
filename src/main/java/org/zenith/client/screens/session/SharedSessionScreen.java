package org.zenith.client.screens.session;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;
import org.zenith.MatrixUtils;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.override.main.MainMenuBlurRenderer;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;
import org.zenith.utility.session.SharedSession;

/** Design coordinates follow the supplied session dialogs and scale together on small screens. */
public final class SharedSessionScreen extends Screen {
    private static final ZenithColor WHITE = new ZenithColor("#e3e3e8");
    private static final ZenithColor MUTED = new ZenithColor("#85858c");
    private static final ZenithColor ACCENT = new ZenithColor("#6866ff");
    private static final ZenithColor BACKDROP = new ZenithColor(16, 17, 21, 217);
    private final Screen parent;
    private final SharedSession session = SharedSession.getInstance();
    private final MainMenuBlurRenderer backgroundBlur = new MainMenuBlurRenderer();
    private float scale;
    private float left;
    private float top;
    private boolean invitation;
    private String notice = "";
    private long copiedUntil;
    private String endpointState = "";

    public SharedSessionScreen(Screen parent) {
        super(Text.literal("Общая сессия · ZenithOpus"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        invitation = session.isActive();
        endpointState = session.address() + session.isGettingPublicAddress() + session.hasTunnelFailed();
        float bodyHeight = invitation ? 640 : 392;
        scale = Math.min(1, Math.min((width - 32f) / 500f, (height - 24f) / bodyHeight));
        left = (width - 460 * scale) / 2;
        top = (height - bodyHeight * scale) / 2;
        if (!invitation) {
            button(0, 150, 226, 26, "Через интернет", session.internetEnabled() ? 1 : 0, () -> {
                session.setInternetEnabled(true); clearAndInit();
            });
            button(234, 150, 226, 26, "В одной сети", session.internetEnabled() ? 0 : 1, () -> {
                session.setInternetEnabled(false); clearAndInit();
            });
            button(0, 250, 460, 23, "Всегда заходить через общую сессию", -3, () -> {
                session.setAlwaysOpen(!session.alwaysOpen());
                clearAndInit();
            });
            button(0, 290, 460, 36, "Создать и войти                                      ▶", 2, () -> {
                if (session.start()) clearAndInit();
                else notice = session.error();
            });
            button(0, 332, 460, 24, "Как это работает", -1, () -> notice = session.internetEnabled()
                ? "Встроенный туннель e4mc. Основной ПК остаётся включённым."
                : "Два ПК в одной сети. Основной ПК остаётся включённым.");
        } else {
            boolean hasAddress = !session.address().isEmpty();
            button(0, 126, 460, 32, hasAddress ? session.address() + "     Копировать"
                : session.hasTunnelFailed() ? "Адрес пока недоступен" : "Получаем публичный адрес…", 0,
                () -> { if (!session.address().isEmpty()) copy(session.address()); }).active = hasAddress;
            button(40, 340, 420, 38, session.takeCommand() + "    ⧉", 1, () -> copy(session.takeCommand()));
            button(0, 466, 226, 26, "Показывать", session.showMessages() ? 1 : 0, () -> {
                session.setShowMessages(true); clearAndInit();
            });
            button(234, 466, 226, 26, "Скрыть", session.showMessages() ? 0 : 1, () -> {
                session.setShowMessages(false); clearAndInit();
            });
            button(0, 502, 460, 28, "Закрыть сессию", -2, () -> { session.stop(); clearAndInit(); });
            button(0, 540, 460, 36, session.hasTunnelFailed() ? "Повторить подключение" : hasAddress ? "Скопировать адрес                                     ⧉" : "Подключаемся…", 2, () -> {
                if (session.hasTunnelFailed()) { session.retryPublicTunnel(); clearAndInit(); }
                else if (!session.address().isEmpty()) copy(session.address());
            }).active = hasAddress || session.hasTunnelFailed();
            button(0, 584, 460, 24, session.hasTunnelFailed() ? "Использовать локальную сеть" : session.isControlled() ? "Вернуть управление на этот ПК" : "Закрыть", -1, () -> {
                if (session.hasTunnelFailed()) { session.useLocalNetwork(); clearAndInit(); }
                else if (session.isControlled()) { session.reclaim(); clearAndInit(); } else close();
            });
        }
    }

    private void copy(String value) {
        client.keyboard.setClipboard(value);
        copiedUntil = System.currentTimeMillis() + 1800;
    }

    @Override
    public void tick() {
        if (session.isActive() != invitation || !(session.address() + session.isGettingPublicAddress() + session.hasTunnelFailed()).equals(endpointState)) clearAndInit();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MenuDrawContext draw = MenuDrawContext.method8080(context, mouseX, mouseY, delta);
        backgroundBlur.capture(draw, 16.0F);
        backgroundBlur.render(MatrixUtils.method664(draw.getMatrices()), 0, 0, width, height,
            ProxyDotRadiusData.INSTANCE, ZenithColor.INSTANCE);
        draw.drawRect(0, 0, width, height, BACKDROP);
        draw.getMatrices().pushMatrix();
        draw.getMatrices().translate(left, top);
        draw.getMatrices().scale(scale, scale);
        round(draw, 181, 0, 98, 24, new ZenithColor("#282847"));
        center(draw, invitation ? "♧  Пригласить" : "♧  Общая сессия", 7, 11, ACCENT);
        if (!invitation) {
            center(draw, "Как подключиться к сессии", 44, 20, WHITE);
            center(draw, "Сервер видит IP этого ПК. Второй ПК подключается", 76, 13, MUTED);
            center(draw, session.internetEnabled() ? "к тебе через встроенный интернет-туннель." : "к тебе напрямую по локальной сети.", 94, 13, MUTED);
            text(draw, "Способ подключения", 0, 126, 13, WHITE);
            round(draw, 0, 191, 28, 28, new ZenithColor("#242540"));
            text(draw, "↔", 8, 198, 16, ACCENT);
            text(draw, session.internetEnabled() ? "Из любой сети, без дополнительных программ" : "Подходит для двух ПК в одной сети", 40, 193, 14, WHITE);
            text(draw, "Окно можно закрыть: сессия останется в фоне.", 40, 214, 13, MUTED);
            text(draw, "Выключишь основной ПК — сессия закончится.", 40, 231, 13, MUTED);
        } else {
            center(draw, "Адрес твоей сессии", 44, 20, WHITE);
            center(draw, session.isInternetSession() ? "Отправь его второму ПК — подключаться можно из другой сети." : "Отправь его второму ПК в той же сети.", 76, 13, MUTED);
            center(draw, session.hasTunnelFailed() ? "Нет связи с ретранслятором. Нажми «Повторить подключение»." : "Зайти можно из обычного Minecraft 1.21.11.", 94, session.hasTunnelFailed() ? 12 : 13, MUTED);
            step(draw, "1", "1. Открой Сетевую игру", "На втором ПК в обычном Minecraft нажми", "«Добавить сервер» или «Прямое подключение».", 176);
            step(draw, "↗", "2. Вставь адрес и зайди", "Название любое. В списке будет ZenithOpus.", "", 238);
            step(draw, "♧", "3. Возьми управление", "Сначала второй ПК смотрит твоими глазами. Введи", "в его чат команду ниже. Не показывай её чужим.", 282);
            step(draw, "i", "Открыть это окно снова", "Esc → Общая сессия или .session в чате.",
                session.isControlled() ? "Управление сейчас на втором ПК." : "Основной ПК можно оставить работать в фоне.", 394);
            text(draw, "Сообщения сессии в чате", 0, 442, 13, WHITE);
        }
        if (!notice.isEmpty() || System.currentTimeMillis() < copiedUntil) {
            String message = System.currentTimeMillis() < copiedUntil ? "Скопировано" : notice;
            center(draw, message, invitation ? 616 : 368, 11, MUTED);
        }
        draw.getMatrices().popMatrix();
        super.render(context, mouseX, mouseY, delta);
    }

    private void step(MenuDrawContext draw, String icon, String title, String line1, String line2, float y) {
        round(draw, 0, y, 28, 28, new ZenithColor("#232842"));
        text(draw, icon, 9, y + 6, 15, ACCENT);
        text(draw, title, 40, y + 1, 14, WHITE);
        text(draw, line1, 40, y + 21, 13, MUTED);
        if (!line2.isEmpty()) text(draw, line2, 40, y + 38, 13, MUTED);
    }

    private ClickableWidget button(float x, float y, float w, float h, String label, int style, Runnable action) {
        return addDrawableChild(new ClickableWidget(Math.round(left + x * scale), Math.round(top + y * scale),
            Math.round(w * scale), Math.round(h * scale), Text.literal(label)) {
            @Override public void onClick(Click click, boolean doubled) { action.run(); }
            @Override public boolean keyPressed(KeyInput key) {
                if (isFocused() && (key.key() == 257 || key.key() == 32 || key.key() == 335)) { action.run(); return true; }
                return false;
            }
            @Override protected void appendClickableNarrations(NarrationMessageBuilder builder) { appendDefaultNarrations(builder); }
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                MenuDrawContext draw = MenuDrawContext.method8080(context, mouseX, mouseY, delta);
                boolean hover = isHovered() || isFocused();
                if (style >= 0) {
                    ZenithColor color = style == 2 ? ACCENT : style == 1 ? new ZenithColor("#252541") : new ZenithColor("#1c1d23");
                    draw.drawRoundedRect(getX(), getY(), getWidth(), getHeight(), ProxyDotRadiusData.method8069(7 * scale),
                        hover ? color.method7293(255) : color);
                    if (hover) draw.drawRoundedRect(getX(), getY(), getWidth(), getHeight(), ProxyDotRadiusData.method8069(7 * scale), new ZenithColor(255, 255, 255, 12));
                }
                Font font = Fonts.NEW_MEDIUM.getFont(13 * scale);
                String clipped = font.clip(label, getWidth() - 16 * scale);
                if (style == -3) {
                    draw.drawRoundedRect(getX(), getY() + 4 * scale, 14 * scale, 14 * scale, ProxyDotRadiusData.method8069(4 * scale),
                        session.alwaysOpen() ? ACCENT : new ZenithColor("#25252a"));
                    if (session.alwaysOpen()) draw.drawText(font, "✓", getX() + 2 * scale, getY() + 3 * scale, WHITE);
                    draw.drawText(font, label, getX() + 22 * scale, getY() + (getHeight() - font.height()) / 2, MUTED);
                    return;
                }
                ZenithColor color = style == -2 ? new ZenithColor("#f05b57") : style == 1 ? ACCENT : style == 2 ? WHITE : MUTED;
                draw.drawText(font, clipped, getX() + (getWidth() - font.width(clipped)) / 2,
                    getY() + (getHeight() - font.height()) / 2, color);
            }
        });
    }

    private static void round(MenuDrawContext draw, float x, float y, float w, float h, ZenithColor color) {
        draw.drawRoundedRect(x, y, w, h, ProxyDotRadiusData.method8069(Math.min(h / 2, 8)), color);
    }

    private static void text(MenuDrawContext draw, String value, float x, float y, float size, ZenithColor color) {
        draw.drawText(Fonts.NEW_REGULAR.getFont(size), value, x, y, color);
    }

    private static void center(MenuDrawContext draw, String value, float y, float size, ZenithColor color) {
        Font font = Fonts.NEW_REGULAR.getFont(size);
        draw.drawText(font, value, (460 - font.width(value)) / 2, y, color);
    }

    @Override public void close() { client.setScreen(parent); }
    @Override public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Vanilla queues its blur and darkening until after Zenith's immediate-mode GUI is drawn.
    }
    @Override public void removed() { backgroundBlur.close(); super.removed(); }
    @Override public boolean shouldPause() { return false; }
}
