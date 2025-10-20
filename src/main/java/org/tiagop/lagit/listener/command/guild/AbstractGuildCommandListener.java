package org.tiagop.lagit.listener.command.guild;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.command.AbstractCommand;
import org.tiagop.lagit.listener.command.AbstractCommandListener;

@Getter
public abstract class AbstractGuildCommandListener<D, C extends AbstractCommand<D>> extends AbstractCommandListener<D, C> {

    protected AbstractGuildCommandListener(final C command) {
        super(command);
    }

    @Override
    protected final void handleCommand(@NotNull final SlashCommandInteractionEvent event, @NotNull final D data) {
        final var guild = event.getGuild();
        if (guild == null) {
            throw new IllegalStateException("Command can only be used in a guild");
        }
        handleCommand(event, data, guild);
    }

    protected abstract void handleCommand(
            @NotNull final SlashCommandInteractionEvent event,
            @NotNull final D data,
            @NotNull final Guild guild
    );
}
