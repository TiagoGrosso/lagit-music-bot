package org.tiagop.lagit.audio.sources;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;

public interface SourceManagerProvider {
    AudioSourceManager getSourceManager(AudioPlayerManager playerManager);
}
