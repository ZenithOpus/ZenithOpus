package org.zenith.client.screens.session;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.zenith.ZenithColor;
import org.zenith.base.font.Fonts;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class SessionMenuButton extends ClickableWidget {
    private final Runnable action;

    public SessionMenuButton(int x, int y, String label, Runnable action) {
        super(x, y, 128, 20, Text.literal(label));
        this.action = action;
    }

    @Override protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        var draw = MenuDrawContext.method8080(context, mouseX, mouseY, delta);
        draw.drawRoundedRect(getX(), getY(), getWidth(), getHeight(), ProxyDotRadiusData.method8069(10),
            new ZenithColor(isHovered() || isFocused() ? "#37365b" : "#24243b"));
        var font = Fonts.NEW_MEDIUM.getFont(9);
        String label = getMessage().getString();
        draw.drawText(font, label, getX() + (getWidth() - font.width(label)) / 2,
            getY() + (getHeight() - font.height()) / 2, new ZenithColor("#9391ff"));
    }

    @Override public void onClick(Click click, boolean doubled) { action.run(); }
    @Override public boolean keyPressed(KeyInput key) {
        if (isFocused() && (key.key() == 257 || key.key() == 32 || key.key() == 335)) { action.run(); return true; }
        return false;
    }
    @Override protected void appendClickableNarrations(NarrationMessageBuilder builder) { appendDefaultNarrations(builder); }
}
