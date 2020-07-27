package com.torpill.fribot.commands.fun;

import java.awt.image.BufferedImage;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;
import com.torpill.fribot.util.ImageProcessor;
import com.torpill.fribot.util.math.Matrix4f;
import com.torpill.fribot.util.math.Vector3f;

/**
 *
 * Cette commande permet de tester les filtres d'images.
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

		return "Cette commande permet de tester les filtres d'images.";
	}

	@Override
	public boolean deleteCommandUsage() {

		return false;
	}

	@Override
	public int execute(final DiscordBot bot, final String[] args, final User user, final TextChannel channel, final Server server) {

		User user0 = null;
		if (args.length > 0) user0 = bot.getUser(server, channel, user, args[0]);
		if (user0 == null) user0 = user;

		final BufferedImage avatar = bot.getAvatar(user0);
		final Vector3f translate = new Vector3f(0, 0, 1);
		final Vector3f rotate = new Vector3f(0, 20, 0);
		final Vector3f scale = new Vector3f(1, 1, 1);
		final Matrix4f transform = Matrix4f.transform(translate, rotate, scale);
		final BufferedImage res = ImageProcessor.projectImage(avatar, transform, 1);

		// @formatter:off

		new MessageBuilder()
			.append("Avant :")
			.addAttachment(avatar, user.getName() + ".png")
			.send(channel).join();
		new MessageBuilder()
			.append("Après :")
			.addAttachment(res, user.getName() + ".png")
			.send(channel);

		// @formatter:on

		return 0;
	}
}
