package com.torpill.fribot.listeners;

import java.util.Optional;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import com.torpill.fribot.App;
import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.threads.CommandThread;

public class CommandListener extends BotListener implements MessageCreateListener {

	public CommandListener(final DiscordBot bot) {

		super(bot);
	}

	@Override
	public void onMessageCreate(final MessageCreateEvent event) {

		final Optional<User> optUser = event.getMessageAuthor().asUser();
		if (!optUser.isPresent()) return;
		final User user = optUser.get();
		final TextChannel channel = event.getChannel();
		final Optional<Server> optServer = event.getServer();
		if (!optServer.isPresent()) return;
		final Server server = optServer.get();
		final Message message = event.getMessage();
		final String content = message.getContent();

		if (!user.isBot() && content.startsWith(this.bot.getPrefix())) {

			App.LOGGER.debug("Commande envoyée par " + user.getDiscriminatedName() + ": " + message);

			final String command = content.substring(this.bot.getPrefix().length(), content.length());
			final String[] args = command.split(" ");
			final String commandName = args[0];

			this.bot.startThread(CommandThread.class, user, channel, message, server, commandName, args);
		}
	}
}