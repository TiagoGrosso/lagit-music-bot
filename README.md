
![Docker Pulls](https://img.shields.io/docker/pulls/tiagopgrosso/lagit-music-bot?label=Docker%20Pulls)
![GitHub Release](https://img.shields.io/github/v/release/tiagogrosso/lagit-music-bot?label=Version)
![License](https://img.shields.io/badge/License-MIT-white)

A Self-Hostable Discord Music Bot built with [Discord-JDA](https://jda.wiki/introduction/jda/)
and [Quarkus](https://quarkus.io/)

# Running the bot

Check the [Running the bot with Docker](docs/Running.md) docs for instructions and examples of how to run the bot.

Also check the [Installing the bot on a server](./docs/Bot.md#installing-the-bot-on-a-server) to have the bot join your
server.

# Using the Bot

Simply use the `/play` command to add songs to the queue. The bot will join your voice channel and start playing
whatever you asked it to play!

Check below for the list of all available commands:

## Commands

Currently supported commands are:

- `/play [query]`
    - Plays the song passed in the `query` parameter:
        - Supports autocomplete, just type something in and wait for the suggestions
        - You can also paste a link from any of the supported sources (e.g a link for a YouTube video or a Twitch Stream)
    - If `query` is not provided, it will attempt to resume playing a paused song
- `/stop`
    - Stops the current song and leaves the channel
- `/skip [n]`
    - Skips the next `n` songs in the queue
    - If `n` is not provided, skips the current song
- `/queue`
    - Displays all the queued songs
- `/pause`
    - Pauses the current song

## Supported Sources

|     Source     |            Enabled by Default?             |     Search      |
|:--------------:|:------------------------------------------:|:---------------:|
|    Youtube     |                    Yes                     |     Videos      |
|    Spotify     | No (check Spotify section below to enable) | Tracks & Albums |
|   Soundcloud   |                    Yes                     |       N/A       |
|    Bandcamp    |                    Yes                     |       N/A       |
|     Vimeo      |                    Yes                     |       N/A       |
| Twitch Streams |                    Yes                     |       N/A       |
|   HTTP URLs    |                    Yes                     |       N/A       |

### Spotify

To enable the Spotify source, you need to provide a Spotify Client Id and Spotify Client Secret when running the bot.
Check the [Spotify for Developers documentation](https://developer.spotify.com/documentation/web-api/concepts/apps) to
find out how to get them.

You can use `/play` with links to Spotify tracks, albums and playlist, e.
`/play https://open.spotify.com/album/4SD2UxRO9OgeSCQK0PN7cC?si=r1hnaOZ1Tzi3GvbhpalGdw`. For albums and playlists, all
the tracks in them will be added to the queue individually.

> [!IMPORTANT]
>
> The Bot doesn't play from Spotify directly, it finds a mirror for each track. In the vast majority of cases this is
> not an issue, but it's possible that a track that exists on Spotify is not found when trying to get a mirror for it.
