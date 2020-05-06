package com.torpill.fribot.commands;

import java.util.List;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;

/**
 * 
 * Cette classe représente une commande permettant d'afficher la liste des
 * commandes disponibles (hormis les commandes privées) et d'obtenir de l'aide
 * sur chacunes d'elles.
 * 
 * @author torpill40
 * 
 * @see com.torpill.fribot.commands.Command
 *
 */

public class HelpCommand extends Command {

	/**
	 * 
	 * Constructeur de la classe <code>HelpCommand</code>.
	 * 
	 */
	public HelpCommand() {

		super("help", Command.ArgumentType.RAW);
	}

	@Override
	public String getHelp() {

		return "Affiche la liste des commandes disponibles.\nAffiche l'aide pour une commande passée en paramètre.";
	}

	@Override
	public boolean deleteCommandUsage() {

		return true;
	}

	@Override
	public List<PermissionType> permissionNeeded() {

		return null;
	}

	@Override
	public List<Role> whiteListedRoles(DiscordBot bot, Server server) {

		return null;
	}

	@Override
	public List<Role> blackListedRoles(DiscordBot bot, Server server) {

		return null;
	}

	@Override
	public List<User> whiteListedUsers(DiscordBot bot, Server server) {

		return null;
	}

	@Override
	public List<User> blackListedUsers(DiscordBot bot, Server server) {

		return null;
	}

	@Override
	public int execute(DiscordBot bot, String[] args, User user, TextChannel channel, Server server) {

		switch (args.length) {
		case 0:
			bot.displayCommandList(channel, user);
			break;

		case 1:
			final String commandName = args[0];
			final String help = bot.getHelpFor(commandName);
			final String type = bot.getTypeFor(commandName);

			if (help == null) {

				return 2;
			}

			final EmbedBuilder embed = bot.defaultEmbedBuilder("Aide :", commandName + " :", user);
			embed.addField("Description :", help);
			embed.addField("Type d'arguments :", type);
			if (!bot.isOwner(user)) {

				if (bot.onBlacklist(user, commandName, server) == 1) embed.addField("Liste noire :", "Vous êtes sur la liste noire, vous ne pouvez pas utiliser cette commande.");
				else if (bot.onWhitelist(user, commandName, server) == 0) embed.addField("Liste blanche :", "Vous n'êtes pas sur la liste blanche, vous ne pouvez pas pas utiliser cette commande.");
				else if (!bot.isAdmin(user, server)) {

					if (bot.onRoleBlacklist(user, commandName, server) == 1) embed.addField("Liste noire :", "Vous avez un rôle sur la liste noire, vous ne pouvez pas utiliser cette commande.");
					else if (bot.onRoleWhitelist(user, commandName, server) == 0) embed.addField("Liste blanche :", "Vous n'avez aucun rôle sur la liste blanche, vous ne pouvez pas utiliser cette commande.");
					else if (bot.canUse(user, commandName, server) == 0) embed.addField("Permissions :", "Vous n'avez pas les permissions requises pour utiliser cette commande.");
				}
			}
			channel.sendMessage(embed);
			break;

		default:
			return 1;
		}

		return 0;
	}
}
