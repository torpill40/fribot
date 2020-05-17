package com.torpill.fribot.commands.utility;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;

/**
 * 
 * Cette classe représente une commande privée de test des arguments nuls.
 * 
 * @author torpill40
 * 
 * @see com.torpill.fribot.commands.Command
 *
 */

public class NoneArgsCommand extends Command {

	/**
	 * 
	 * Constructeur de la classe <code>NoneArgsCommand</code>.
	 * 
	 */
	public NoneArgsCommand() {

		super("__none", Command.ArgumentType.NONE, Command.Category.UTILITY);
	}

	@Override
	public String getHelp() {

		return "Commande de test des arguments nuls.";
	}

	@Override
	public boolean deleteCommandUsage() {

		return false;
	}

	@Override
	public int execute(DiscordBot bot, String[] args, User user, TextChannel channel, Server server) {

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < args.length; i++) {

			builder.append(i + " : \n- " + args[i] + "\n");
		}

		channel.sendMessage(builder.toString());

		return 0;
	}
}
