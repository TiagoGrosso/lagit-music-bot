package org.tiagop.lagit.listener.command.autocomplete;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.command.PlayCommand;
import org.tiagop.lagit.service.search.SearchResult;
import org.tiagop.lagit.service.search.SearchService;
import org.tiagop.lagit.util.Validation;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Dependent
public class PlayQueryAutocompleteListener extends AbstractAutocompleteListener<PlayCommand> {

    private static final Set<String> SEARCH_PREFIXES = Arrays.stream(SearchResult.Source.values())
            .map(SearchResult.Source::getSearchPrefix)
            .collect(Collectors.toSet());

    private final SearchService searchService;

    protected PlayQueryAutocompleteListener(
            @NotNull final PlayCommand command,
            final SearchService searchService
    ) {
        super(command, PlayCommand.QUERY_OPTION);
        this.searchService = searchService;
    }

    @Override
    protected void handleAutocomplete(@NotNull final CommandAutoCompleteInteractionEvent event) {
        final var query = event.getFocusedOption().getValue();
        if (StringUtils.isBlank(query)) {
            return;
        }
        if (Validation.isValidUrl(query)) {
            return;
        }
        if (SEARCH_PREFIXES.stream().anyMatch(query::startsWith)) {
            return;
        }

        final var searchResults = searchService.search(query);
        if (searchResults.isEmpty()) {
            return;
        }
        final var choices = searchResults.stream()
                .map(r -> new Command.Choice(r.choiceName(), r.id()))
                .toList();
        event.replyChoices(choices).queue();
    }
}
