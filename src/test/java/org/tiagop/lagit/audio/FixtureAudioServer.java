package org.tiagop.lagit.audio;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;

public final class FixtureAudioServer implements AutoCloseable {

    private static final String FIXTURE_RESOURCE = "/audio/sine-440hz-5s.mp3";

    private final HttpServer server;

    public FixtureAudioServer() {
        final var fixtureBytes = readFixture();
        try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        server.createContext("/fixture.mp3", exchange -> {
            exchange.getResponseHeaders().add("Content-Type", "audio/mpeg");
            exchange.sendResponseHeaders(200, fixtureBytes.length);
            try (exchange) {
                exchange.getResponseBody().write(fixtureBytes);
            }
        });
        server.start();
    }

    public String url() {
        return "http://127.0.0.1:%d/fixture.mp3".formatted(server.getAddress().getPort());
    }

    @Override
    public void close() {
        server.stop(0);
    }

    private static byte[] readFixture() {
        try (final var fixture = FixtureAudioServer.class.getResourceAsStream(FIXTURE_RESOURCE)) {
            if (fixture == null) {
                throw new IllegalStateException("Missing test audio fixture %s".formatted(FIXTURE_RESOURCE));
            }
            return fixture.readAllBytes();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
