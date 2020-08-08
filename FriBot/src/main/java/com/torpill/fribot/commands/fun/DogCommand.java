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
import com.vdurmont.emoji.EmojiParser;

/**
 *
 * Déguisement de chien gratuit !
 *
 * @author torpill40
 *
 * @see com.torpill.fribot.commands.Command
 *
 */

public class DogCommand extends Command {

	/**
	 *
	 * Constructeur de la classe <code>DogCommand</code>.
	 *
	 */
	public DogCommand() {

		super("dog", Command.ArgumentType.RAW, Command.Category.FUN);
	}

	@Override
	public String getHelp() {

		return "Déguisement de chien gratuit !\nSans argument pour cibler l'auteur du message.\nAvec un argument en mention pour cibler un autre membre.";
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
		final BufferedImage dog = ImageLoader.loadImage("dog.png");
		final int maskX = 119, maskY = 8, maskWidth = 104, maskHeight = 104;
		final BufferedImage res = ImageProcessor.applyMask(dog, avatar, maskX, maskY, maskWidth, maskHeight);

		// @formatter:off

		new MessageBuilder()
			.append(EmojiParser.parseToUnicode(":dog:"))
			.addAttachment(res, Long.toHexString(user.getId()) + "-" + Long.toHexString(user0.getId()) + ".png")
			.send(channel);

		// @formatter:on

		return 0;
	}
}
