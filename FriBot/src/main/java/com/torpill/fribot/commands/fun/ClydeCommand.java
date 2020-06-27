package com.torpill.fribot.commands.fun;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;
import com.torpill.fribot.util.FontLoader;
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

public class ClydeCommand extends Command {

	/**
	 *
	 * Constructeur de la classe <code>ClydeCommand</code>.
	 *
	 */
	public ClydeCommand() {

		super("clyde", Command.ArgumentType.RAW, Command.Category.FUN);
	}

	@Override
	public String getHelp() {

		return "Devenir un bot le temps d'une image.\nSans argument pour cibler l'auteur du message.\nAvec un argument en mention pour cibler un autre membre.";
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
		if (args.length > 0) user0 = bot.getUserFromMention(args[0]);
		if (user0 == null) user0 = user;

		final BufferedImage avatar = ImageProcessor.multiply(ImageProcessor.noise(bot.getAvatar(user0), 0.35F, 0F, 1F), 0F, 0.18F, 0.36F);
		final BufferedImage clyde = ImageLoader.loadImage("clyde.png");
		final int maskX = 95, maskY = 102, maskWidth = 322, maskHeight = 322;
		final Font font = FontLoader.loadFont("8-bit-madness-regular.ttf");
		final BufferedImage res = ImageProcessor.write(ImageProcessor.applyMask(clyde, avatar, maskX, maskY, maskWidth, maskHeight), "clyde_clone_" + Math.abs(user0.getName().hashCode()) + "_a" + user0.getIdAsString(), 5, 510, font, new Color(53, 231, 83));

		// @formatter:off

		new MessageBuilder()
			.addAttachment(res, user0.getName() + ".png")
			.send(channel);

		// @formatter:on

		return 0;
	}
}
