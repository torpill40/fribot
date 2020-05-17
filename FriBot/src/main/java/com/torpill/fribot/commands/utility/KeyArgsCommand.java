package com.torpill.fribot.commands.utility;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;

/**
 * 
 * Cette classe représente une commande privée de test des arguments clé /
 * valeur.
 * 
 * @author torpill40
 * 
 * @see com.torpill.fribot.commands.Command
 *
 */

public class KeyArgsCommand extends Command {

	/**
	 * 
	 * Constructeur de la classe <code>KeyArgsCommand</code>.
	 * 
	 */
	public KeyArgsCommand() {

		super("__key", Command.ArgumentType.KEY, Command.Category.UTILITY);
	}

	@Override
	public String getHelp() {

		return "Commande de test des arguments clé / valeur.";
	}

	@Override
	public boolean deleteCommandUsage() {

		return false;
	}

	@Override
	public int execute(DiscordBot bot, String[] args, User user, TextChannel channel, Server server) {

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < args.length; i += 2) {

			builder.append(args[i] + " :\n");
			for (String arg : args[i + 1].split(" ")) {

				builder.append("- " + arg + "\n");
			}
		}

		channel.sendMessage(builder.toString());

		return 0;
	}
}
