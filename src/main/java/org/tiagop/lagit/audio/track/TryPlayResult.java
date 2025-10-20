package org.tiagop.lagit.audio.track;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TryPlayResult {
    STARTED_PLAYING,
    ALREADY_PLAYING,
    RESUMED_PLAYING,
    EMPTY_QUEUE
}
