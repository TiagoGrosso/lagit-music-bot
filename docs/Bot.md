# Getting a Token

Follow these steps to get a Discord Token

- Create a new application in the [Discord Developer Portal](https://discord.com/developers/applications)
- Go to "Bot" and click "Add Bot"
- Give it a name and any additional info you want
- Generate a token under the "Token" section

# Installing the bot on a server

Follow these steps to install the Bot on a server (also known as Guild):

- Go to your application in the [Discord Developer Portal](https://discord.com/developers/applications)
- Go to "Installation"
- Make sure you have "Guild Install" checked
- "Install Link" should be set to "Discord Provided Link"
- Under "Default Install Settings" -> "Guild Install":
    - "Scopes" must include "applications.commands" and "bot"
    - "Permissions" must include "Connect", "Send Messages", "Speak", "View Channels"
- Copy the link from the "Install Link" section and paste it in your browser
- Follow the steps to install the Bot in a server of your choosing. Note: the Bot supports multiple simultaneous
  servers
