package com.torpill.fribot.threads;

import java.util.List;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.App;
import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.util.ListBuilder;

/**
 *
 * Cette class repésente un thread de commande.
 *
 * @author torpill40
 *
 */

public class CommandThread extends BotThread {

	/**
	 *
	 * Constructeur de la classe <code>CommandThread</code>.
	 *
	 * @param bot
	 *            : bot Discord relié au thread.
	 *
	 * @see com.torpill.fribot.bot.DiscordBot
	 */
	public CommandThread(final DiscordBot bot) {

		super(bot, "Command");
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

		} else {

			App.LOGGER.info(user.getDiscriminatedName() + " n'a pas pu utilisé la commande '" + commandName + "' : " + response);
			switch (response) {

			case -1:
				message.delete();
				channel.sendMessage(user.getMentionTag() + ", la commande `" + commandName + "` n'existe pas : faites `" + this.bot.getPrefix() + "help` pour avoir la liste des commandes.");
				break;

			case -2:
				message.delete();
				channel.sendMessage(user.getMentionTag() + ", vous n'avez pas les permissions pour exécuter cette commande : faites `" + this.bot.getPrefix() + "help " + commandName + "` pour plus d'informations.");
				break;

			case 1:
				channel.sendMessage(user.getMentionTag() + ", vous n'avez pas entré un nombre correct d'arguments : faites `" + this.bot.getPrefix() + "help " + commandName + "` pour plus d'informations.");
				break;
			}
		}

		super.run();
	}
}
