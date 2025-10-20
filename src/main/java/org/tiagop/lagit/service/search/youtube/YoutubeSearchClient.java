package org.tiagop.lagit.service.search.youtube;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "https://music.youtube.com/")
public interface YoutubeSearchClient {

    @POST
    @Path("youtubei/v1/search")
    @Consumes(MediaType.APPLICATION_JSON)
    JsonNode getSuggestions(MusicSuggestionsRequestBody body);

    record MusicSuggestionsRequestBody(Context context, String query) {
        public MusicSuggestionsRequestBody(String query) {
            this(new Context(new Context.Client("WEB_REMIX", "1.20220328.01.00")), query);
        }

        record Context(Client client) {
            record Client(
                    String clientName,
                    String clientVersion
            ) { }
        }
    }
}