package org.tiagop.lagit;

import io.quarkus.arc.All;
import io.quarkus.logging.Log;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.command.AbstractCommand;
import org.tiagop.lagit.listener.AbstractListener;
import org.tiagop.lagit.listener.command.AbstractCommandListener;

@Startup
@ApplicationScoped
public class Bot {

    private final JDA jda;

    public Bot(
        @All @NotNull final List<AbstractListener<?>> listeners,
        @ConfigProperty(name = "discord.token") @NotNull final String token
    ) {
        Log.info("Starting Lagit Music Bot");
        jda = JDABuilder.createDefault(token)
            .addEventListeners((Object[]) listeners.toArray(AbstractListener[]::new))
            .enableIntents(listeners.stream().flatMap(l -> l.getIntents().stream()).collect(Collectors.toSet()))
            .build();

        jda.updateCommands().addCommands(
            listeners.stream()
                .filter(l -> l instanceof AbstractCommandListener<?, ?>)
                .map(l -> (AbstractCommandListener<?, ?>) l)
                .map(AbstractCommandListener::getCommand)
                .map(AbstractCommand::toCommandData)
                .collect(Collectors.toSet())
        ).queue();
    }

    public void shutdown(@Observes final ShutdownEvent event) {
        jda.shutdown();
    }

    @Produces
    public JDA getJda() {
        return jda;
    }
}
