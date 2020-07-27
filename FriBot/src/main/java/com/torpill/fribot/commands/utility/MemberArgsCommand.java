package com.torpill.fribot.commands.utility;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;

/**
 *
 * Cette classe représente une commande privée de test des arguments membres.
 *
 * @author torpill40
 *
 * @see com.torpill.fribot.commands.Command
 *
 */

public class MemberArgsCommand extends Command {

	/**
	 *
	 * Constructeur de la classe <code>MemberArgsCommand</code>.
	 *
	 */
	public MemberArgsCommand() {

		super("__member", Command.ArgumentType.RAW, Command.Category.UTILITY);
	}

	@Override
	public String getHelp() {

		return "Commande de test des arguments membres.";
	}

	@Override
	public boolean deleteCommandUsage() {

		return false;
	}

	@Override
	public int execute(final DiscordBot bot, final String[] args, final User user, final TextChannel channel, final Server server) {

		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < args.length; i++) {

			final User user0 = bot.getUser(server, channel, user, args[i]);
			if (user0 != null) builder.append(i + " : \n- " + user0.getDiscriminatedName() + "\n");
		}
		channel.sendMessage(builder.toString());

		return 0;
	}
}
