package com.torpill.fribot.threads;

import java.util.List;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.App;
import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.util.ListBuilder;

public class CommandThread extends BotThread {

	public CommandThread(DiscordBot bot) {

		super(bot, "command");
	}

	@Override
	protected List<? extends Class<?>> args() {

		return ListBuilder.listOf(User.class, TextChannel.class, Message.class, Server.class, String.class, String[].class);
	}

	@Override
	public void run() {

		App.LOGGER.debug("'" + Thread.currentThread().getName() + "' démarré avec succès.");

		final User user = (User) this.args[0];
		final TextChannel channel = (TextChannel) this.args[1];
		final Message message = (Message) this.args[2];
		final Server server = (Server) this.args[3];
		final String commandName = (String) this.args[4];
		final String[] args = (String[]) this.args[5];

		App.LOGGER.debug("'" + Thread.currentThread().getName() + "' paramètres récupérés avec succès.");

		final int response = this.bot.execute(user, channel, message, server, commandName, args);
		if (response == 0) {

			App.LOGGER.info(user.getDiscriminatedName() + " a utilisé la commande '" + commandName + "' : " + response);

		} else if (response > 0) {

			App.LOGGER.warn(user.getDiscriminatedName() + " n'a pas pu utilisé la commande '" + commandName + "' : " + response);

		} else {

			App.LOGGER.error(user.getDiscriminatedName() + " n'a pas pu utilisé la commande '" + commandName + "' : " + response);
		}

		super.run();
	}
}
