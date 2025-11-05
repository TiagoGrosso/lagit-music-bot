package org.tiagop.lagit.listener;

import io.quarkus.logging.Log;
import java.util.Collections;
import java.util.Set;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;

public abstract class AbstractListener<E extends GenericEvent> implements EventListener {
    private final Class<E> eventClass;

    protected AbstractListener(final Class<E> eventClass) {
        this.eventClass = eventClass;
    }

    public Set<GatewayIntent> getIntents() {
        return Collections.emptySet();
    }

    @Override
    public final void onEvent(final GenericEvent event) {
        if (!eventClass.isInstance(event)) {
            Log.debugf("Ignoring event of type %s", event.getClass().getSimpleName());
            return;
        }
        processEvent(eventClass.cast(event));
    }

    protected abstract void processEvent(final E event);
}
