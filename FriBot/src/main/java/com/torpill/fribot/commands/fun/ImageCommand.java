package com.torpill.fribot.commands.fun;

import java.awt.image.BufferedImage;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;

/**
 *
 * Cette commande permet de tester les récupération d'images.
 *
 * @author torpill40
 *
 * @see com.torpill.fribot.commands.Command
 *
 */

public class ImageCommand extends Command {

	/**
	 *
	 * Constructeur de la classe <code>ImageCommand</code>.
	 *
	 */
	public ImageCommand() {

		super("__image", Command.ArgumentType.RAW, Command.Category.FUN);
	}

	@Override
	public String getHelp() {

		return "Cette commande permet de tester les récupérations d'images.";
	}

	@Override
	public boolean deleteCommandUsage() {

		return false;
	}

	@Override
	public int execute(final DiscordBot bot, final String[] args, final User user, final TextChannel channel, final Server server) {

		User user0 = null;
		if (args.length > 0) {

			user0 = bot.getUserFromMention(args[0]);
		}

		if (user0 == null) {

			user0 = user;
		}

		final BufferedImage avatar = bot.getAvatar(user0);

		// @formatter:off

		new MessageBuilder()
			.append("Test d'image :")
			.addAttachment(avatar, user.getName() + ".png")
			.send(channel);

		// @formatter:on

		return 0;
	}
}
