package com.torpill.fribot.commands.game;

import java.util.Random;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;
import com.torpill.fribot.threads.RightPriceThread;

/**
 *
 * Faire une partie de Juste Prix.
 *
 * @author torpill40
 *
 * @see com.torpill.fribot.command.Command
 *
 */

public class RightPriceCommand extends Command {

	/**
	 *
	 * Constructeur de la classe <code>RightPriceCommand</code>.
	 *
	 */
	public RightPriceCommand() {

		super("right-price", Command.ArgumentType.RAW, Command.Category.GAME);
	}

	@Override
	public String getHelp() {

		return "Faire une partie de Juste Prix.\nLe but du jeu est de trouver un nombre entre 2 bornes connues et en moins de 15 essais.\nLes différents niveaux de jeu :\n - easy\n - normal\n - hard\n - impossible\nPar défaut, la difficulté est en normal.";
	}

	@Override
	public String getExample(final String prefix, final User user) {

		return prefix + this.getName() + "\n" + prefix + this.getName() + " hard";
	}

	@Override
	public boolean deleteCommandUsage() {

		return true;
	}

	@Override
	public int execute(final DiscordBot bot, final String[] args, final User user, final TextChannel channel, final Server server) {

		if (args.length > 1) return 1;
		final String level = args.length > 0 ? args[0] : "normal";
		final Random rand = new Random();
		final int min = rand.nextInt(40000) + 10000;
		int max = min;
		switch (level) {

		case "impossible":
			max += 90000;

		case "hard":
			max += 9000;

		case "normal":
			max += 900;

		case "easy":
			max += 100;
			break;

		default:
			channel.sendMessage(user.getMentionTag() + ", `" + level + "` n'est pas un niveau de difficulté existant.");
			return 2;
		}

		bot.startThread(RightPriceThread.class, user, channel, rand.nextInt(max - min + 1) + min, min, max);
		return 0;
	}
}
