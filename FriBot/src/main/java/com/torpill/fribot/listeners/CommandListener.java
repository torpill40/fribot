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

/**
 *
 * Cete classe représente un écouteur dédié aux commandes.
 *
 * @author torpill40
 *
 * @see org.javacord.api.listener.message.MessageCreateListener
 * @see com.torpill.fribot.listeners.BotListener
 *
 */

public class CommandListener extends BotListener implements MessageCreateListener {

	/**
	 *
	 * Constructeur de la classe <code>CommandListener</code>.
	 *
	 * @param bot
	 *            : bot Discord communiquant avec l'écouteur.
	 */
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

		if (!user.isBot()) {

			if (content.startsWith(this.bot.getPrefix())) {

				App.LOGGER.debug("Commande envoyée par " + user.getDiscriminatedName() + ": " + message);

				final String command = content.substring(this.bot.getPrefix().length(), content.length()).replace("\n", " ");
				final String[] args = command.split(" ");
				final String commandName = args[0];

				this.bot.startThread(CommandThread.class, user, channel, message, server, commandName, args);

			} else if (content.startsWith(this.bot.bot().getMentionTag()) || content.startsWith(this.bot.bot().getNicknameMentionTag()) && !App.TEST) {

				App.LOGGER.debug("Mention envoyée par " + user.getDiscriminatedName() + ": " + message);

				channel.sendMessage(user.getMentionTag() + ", mon préfix est `" + this.bot.getPrefix() + "`. Fais `" + this.bot.getPrefix() + "help` pour la liste des commandes par catégories.");
			}
		}
	}
}