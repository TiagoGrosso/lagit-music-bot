# Lagit Music Bot

A Self-Hostable Discord Music Bot built with [Discord-JDA](https://jda.wiki/introduction/jda/)
and [Quarkus](https://quarkus.io/).

![Docker Pulls](https://img.shields.io/docker/pulls/tiagopgrosso/lagit-music-bot?label=Docker%20Pulls)
![GitHub Release](https://img.shields.io/github/v/release/tiagogrosso/lagit-music-bot?label=Version)
![License](https://img.shields.io/badge/License-MIT-white)

## Using the Bot

Simply use the `/play` command to add songs to the queue. The bot will join your voice channel and start playing
whatever you asked it to play!

Check below for the list of all available commands:

### Commands

Currently supported commands are:

- `/play [query]`
    - Plays the song passed in the `query` parameter:
        - Supports autocomplete, just type something in and wait for the suggestions
        - You can also simply paste a link from any of the supported sources
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

### Supported Sources

|     Source     |            Enabled by Default?             |     Search      |
|:--------------:|:------------------------------------------:|:---------------:|
|    Youtube     |                    Yes                     |     Videos      |
|    Spotify     | No (check Spotify section below to enable) | Tracks & Albums |
|   Soundcloud   |                    Yes                     |       N/A       |
|    Bandcamp    |                    Yes                     |       N/A       |
|     Vimeo      |                    Yes                     |       N/A       |
| Twitch Streams |                    Yes                     |       N/A       |
|   HTTP URLs    |                    Yes                     |       N/A       |

#### Spotify

To enable the Spotify source, you need to provide a Spotify Client Id and Spotify Client Secret when running the bot.
Check the [Spotify for Developers documentation](https://developer.spotify.com/documentation/web-api/concepts/apps) to
find out how to get them.

## Running the bot

Check the [Running the bot with Docker](docs/Running.md) docs for instructions and examples of how to run the bot.

Also check the [Installing the bot on a server](./docs/Bot.md#installing-the-bot-on-a-server) to have the bot join your
server.