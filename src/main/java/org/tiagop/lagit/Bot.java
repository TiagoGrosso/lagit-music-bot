package org.tiagop.lagit;

import io.quarkus.arc.All;
import io.quarkus.logging.Log;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.Nullable;
import org.tiagop.lagit.command.AbstractCommand;
import org.tiagop.lagit.listener.AbstractListener;
import org.tiagop.lagit.listener.command.AbstractCommandListener;

@Startup
@ApplicationScoped
public class Bot {

    private final List<AbstractListener<?>> listeners;
    private final String token;
    private final int shardsTotal;
    private final List<Integer> shardIds;
    @Nullable
    private ShardManager shardManager;

    // CDI constructor: no exceptions possible
    public Bot(
        @All final List<AbstractListener<?>> listeners,
        @ConfigProperty(name = "discord.token") final String token,
        @ConfigProperty(name = "discord.shards.total") final int shardsTotal,
        @ConfigProperty(name = "discord.shards.ids") final List<Integer> shardIds
    ) {
        this.listeners = listeners;
        this.token = token;
        this.shardsTotal = shardsTotal;
        this.shardIds = shardIds;
    }

    @PostConstruct
    public void init() {
        Log.info("Starting Lagit Music Bot");
        shardManager = DefaultShardManagerBuilder.createDefault(token)
            .setShardsTotal(shardsTotal)
            .setShards(shardIds)
            .addEventListeners((Object[]) listeners.toArray(AbstractListener[]::new))
            .enableIntents(listeners.stream().flatMap(l -> l.getIntents().stream()).collect(Collectors.toSet()))
            .build();

        shardManager.getShards()
            .getFirst()
            .updateCommands()
            .addCommands(listeners.stream()
                .filter(l -> l instanceof AbstractCommandListener<?, ?>)
                .map(l -> (AbstractCommandListener<?, ?>) l)
                .map(AbstractCommandListener::getCommand)
                .map(AbstractCommand::toCommandData)
                .peek(c -> Log.info("Found command: %s".formatted(c.getName())))
                .collect(Collectors.toSet()))
            .queue();
    }

    public void shutdown(@Observes final ShutdownEvent event) {
        if (shardManager != null) {
            shardManager.shutdown();
        }
    }
}
