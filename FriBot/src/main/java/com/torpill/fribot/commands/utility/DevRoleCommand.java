package com.torpill.fribot.commands.utility;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;

/**
 * 
 * Cette classe représente une commande privée de test du rôle développeur.
 * 
 * @author torpill40
 * 
 * @see com.torpill.fribot.commands.Command
 *
 */

public class DevRoleCommand extends Command {

	/**
	 * 
	 * Constructeur de la classe <code>DevRoleCommand</code>.
	 * 
	 */
	public DevRoleCommand() {

		super("__devrole", Command.ArgumentType.NONE, Command.Category.UTILITY);
	}

	@Override
	public String getHelp() {

		return "Commande de test du rôle développeur.";
	}

	@Override
	public boolean deleteCommandUsage() {

		return false;
	}

	@Override
	public int execute(DiscordBot bot, String[] args, User user, TextChannel channel, Server server) {

		Role devrole = bot.getDevRole(server);
		if (devrole != null) {

			server.addRoleToUser(user, devrole);
		}

		return 0;
	}

}
