package org.tiagop.lagit.listener.command.guild;

import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tiagop.lagit.audio.track.TestAudioTrack;
import org.tiagop.lagit.audio.track.TrackManager;
import org.tiagop.lagit.audio.track.TrackRequest;
import org.tiagop.lagit.command.PlayingCommand;
import org.tiagop.lagit.guild.GuildService;
import org.tiagop.lagit.guild.channel.PlayInfoManager;
import org.tiagop.lagit.guild.channel.embeds.NowPlayingEmbed;

class PlayingCommandListenerTest {

    private final SlashCommandInteractionEvent event = mock();
    private final ReplyCallbackAction action = mock();
    private final Guild guild = mock();
    private final PlayInfoManager playInfoManager = mock();
    private final GuildService guildService = mock();
    private final PlayingCommand.Data data = new PlayingCommand.Data();
    private final TrackManager trackManager = mock();
    private final PlayingCommandListener listener = new PlayingCommandListener();

    @BeforeEach
    void setUp() {
        listener.guildService = guildService;
        given(guildService.getTrackManager(guild)).willReturn(trackManager);
        given(event.reply(any(String.class))).willReturn(action);
        given(guildService.getPlayInfoManager(guild)).willReturn(playInfoManager);
    }

    @Test
    void replies_on_no_playing_track() {
        // given
        given(trackManager.getCurrentTrack()).willReturn(null);

        // when
        listener.handleCommand(event, data, guild);

        // then
        final var ignored = then(event).should().reply("Nothing is playing");
        then(action).should().queue();
        then(event).shouldHaveNoMoreInteractions();
    }

    @Test
    void replies_on_track_playing() {
        // given
        final var track = new TrackRequest(new TestAudioTrack(), randomUUID().toString());
        given(trackManager.getCurrentTrack()).willReturn(track);
        given(trackManager.getQueue()).willReturn(List.of(track, track));

        // when
        listener.handleCommand(event, data, guild);

        // then
        final var ignored = then(event).should().reply("Displaying the current track");
        then(action).should().queue();
        then(event).shouldHaveNoMoreInteractions();
        then(playInfoManager).should().updatePlayInfo(new NowPlayingEmbed(track, 2), true);
    }
}