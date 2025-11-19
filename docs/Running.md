# Running the bot with Docker

This bot is distributed as a docker image in
the [tiagopgrosso/lagit-music-bot repo](https://hub.docker.com/repository/docker/tiagopgrosso/lagit-music-bot/general).
**As such you are required to have [Docker](https://www.docker.com/get-started/) installed** [¹](#docker-alternatives)

To run it, you'll need to get a Discord Token. Check the [Getting a Token](./docs/Bot.md#getting-a-token) docs for
information on how to get one.
The simplest way to run the bot is with `docker run`:

```shell
docker run -e DISCORD_TOKEN=<your discord token> 
  -e SPOTIFY_CLIENT_ID=<your spotify client id> # If you don't want to use spotify, remove this line
  -e SPOTIFY_CLIENT_SECRET=<your spotify client secret> # If you don't want to use spotify, remove this line
  tiagopgrosso/lagit-music-bot
```

However, you can also use a docker compose file. Here's an example:

```yml
services:
  bot:
    image: tiagopgrosso/lagit-music-bot # optionally add ":X.Y.Z" for a specific version
    restart: unless-stopped
    environment:
      - DISCORD_TOKEN=<your discord token>
      - SPOTIFY_CLIENT_ID=<your spotify client id> # If you don't want to use spotify, remove this line
      - SPOTIFY_CLIENT_SECRET=<your spotify client secret> # If you don't want to use spotify, remove this line
```

Or, as you might already know, you can have the environment variables stored in a `.env` file:

```dotenv
DISCORD_TOKEN=<your discord token> 
SPOTIFY_CLIENT_ID=<your spotify client id> # If you don't want to use spotify, remove this line
SPOTIFY_CLIENT_SECRET=<your spotify client secret> # If you don't want to use spotify, remove this line
```

And run with:

```shell
docker run --env-file .env tiagopgrosso/lagit-music-bot
```

Or reference it in the `docker-compose` file:

```yml
services:
  bot:
    image: tiagopgrosso/lagit-music-bot # optionally add ":X.Y.Z" for a specific version
    restart: unless-stopped
    environment:
      - DISCORD_TOKEN=${DISCORD_TOKEN}
      - SPOTIFY_CLIENT_ID=${SPOTIFY_CLIENT_ID} # If you don't want to use spotify, remove this line
      - SPOTIFY_CLIENT_SECRET=${SPOTIFY_CLIENT_SECRET} # If you don't want to use spotify, remove this line
```

# Advanced

## Docker Alternatives

You don't actually have to use Docker to run the bot. You can use any alternative such
as [colima](https://github.com/abiosoft/colima) or [podman](https://podman.io/).

If you are using any of these, it's likely you won't need any help from me to adapt the `docker run` command or the
`docker-compose` file. However, feel free to reach out via GitHub Issues.

## I don't want to run the bot in a container

You're free to install a Java JDK, clone the repo and run `./mvnw quarkus:build` and run the jar yourself. However, I'll
not be
providing any jar distribution myself, nor any detailed instructions for the time being. I might offer some support if
you really really really don't want to use containers.