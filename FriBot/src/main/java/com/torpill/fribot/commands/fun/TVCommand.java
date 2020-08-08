package com.torpill.fribot.commands.fun;

import java.awt.image.BufferedImage;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;
import com.torpill.fribot.util.ImageLoader;
import com.torpill.fribot.util.ImageProcessor;
import com.torpill.fribot.util.math.Matrix4f;
import com.torpill.fribot.util.math.Vector3f;

/**
 *
 * Passes à la télé sans avoir à payer !
 *
 * @author torpill40
 *
 * @see com.torpill.fribot.commands.Command
 *
 */

public class TVCommand extends Command {

	/**
	 *
	 * Constructeur de la classe <code>TVCommand</code>.
	 *
	 */
	public TVCommand() {

		super("tv", Command.ArgumentType.RAW, Command.Category.FUN);
	}

	@Override
	public String getHelp() {

		return "Passes à la télé sans avoir à payer !\nSans argument pour cibler l'auteur du message.\nAvec un argument en mention pour cibler un autre membre.";
	}

	@Override
	public String getExample(final String prefix, final User user) {

		return prefix + this.getName() + "\n" + Command.defaultExampleForOneMemberArgument(this, prefix, user);
	}

	@Override
	public boolean deleteCommandUsage() {

		return true;
	}

	@Override
	public int execute(final DiscordBot bot, final String[] args, final User user, final TextChannel channel, final Server server) {

		User user0 = null;
		if (args.length > 0) user0 = bot.getUser(server, channel, user, args[0]);
		if (user0 == null) user0 = user;

		final BufferedImage avatar = bot.getAvatar(user0);
		final Vector3f translate = new Vector3f(0, -0.74F, 0.15F);
		final Vector3f rotate = new Vector3f(0, -3, 0.5F);
		final Vector3f scale = new Vector3f(1, 0.75F, 1);
		final Matrix4f transform = Matrix4f.transform(translate, rotate, scale);
		final BufferedImage projection = ImageProcessor.projectImage(avatar, transform, 1);
		final BufferedImage tv = ImageLoader.loadImage("tv.png");
		final int maskX = 99, maskY = 29, maskWidth = 260, maskHeight = 240;
		final BufferedImage res = ImageProcessor.applyMask(tv, projection, maskX, maskY, maskWidth, maskHeight);

		// @formatter:off

		new MessageBuilder()
			.addAttachment(res, Long.toHexString(user.getId()) + "-" + Long.toHexString(user0.getId()) + ".png")
			.send(channel);

		// @formatter:on

		return 0;
	}
}
