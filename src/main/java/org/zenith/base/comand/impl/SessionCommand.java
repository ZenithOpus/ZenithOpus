package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.utility.session.SharedSession;

public final class SessionCommand extends CommandAbstract {
    public SessionCommand() { super("session"); }

    @Override
    public void execute(LiteralArgumentBuilder<CommandSource> command) {
        command.executes(context -> {
            mc.execute(() -> SharedSession.getInstance().open(null));
            return 1;
        });
        command.then(literal("stop").executes(context -> { SharedSession.getInstance().stop(); return 1; }));
        command.then(literal("release").executes(context -> { SharedSession.getInstance().reclaim(); return 1; }));
    }
}
