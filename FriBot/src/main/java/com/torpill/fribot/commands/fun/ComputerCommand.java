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

/**
 *
 * Devenir un bot le temps d'une image.
 *
 * @author torpill40
 *
 * @see com.torpill.fribot.commands.Command
 *
 */

public class ComputerCommand extends Command {

	/**
	 *
	 * Constructeur de la classe <code>ComputerCommand</code>.
	 *
	 */
	public ComputerCommand() {

		super("computer", Command.ArgumentType.RAW, Command.Category.FUN);
	}

	@Override
	public String getHelp() {

		return "Prendre place dans un vieil ordinateur.\nSans argument pour cibler l'auteur du message.\nAvec un argument en mention pour cibler un autre membre.";
	}

	@Override
	public String getExample(final String prefix, final User user) {

		return prefix + this.getName() + "\n" + prefix + this.getName() + " " + user.getMentionTag();
	}

	@Override
	public boolean deleteCommandUsage() {

		return true;
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

		final BufferedImage gray = ImageProcessor.multiply(ImageProcessor.noise(ImageProcessor.grayScale(bot.getAvatar(user0)), 0.15F, 0F, 1F), 0.75F, 0.75F, 0.75F);
		final BufferedImage redMask = ImageProcessor.createGaussianBlur(ImageProcessor.redMask(gray), 5, 2.5F);
		final BufferedImage greenMask = ImageProcessor.greenMask(gray);
		final BufferedImage blueMask = ImageProcessor.blueMask(gray);
		final BufferedImage avatar = ImageProcessor.applyRGBMasks(redMask, -2, 0, greenMask, 2, -1, blueMask, 2, -1);
		final BufferedImage computer = ImageLoader.loadImage("computer.png");
		final int maskX = 182, maskY = 85, maskWidth = 198, maskHeight = 198;
		final BufferedImage res = ImageProcessor.applyMask(computer, avatar, maskX, maskY, maskWidth, maskHeight);

		// @formatter:off

		new MessageBuilder()
			.addAttachment(res, user0.getName() + ".png")
			.send(channel);

		// @formatter:on

		return 0;
	}
}
