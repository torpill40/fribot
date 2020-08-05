package com.torpill.fribot.commands.utility;

import java.util.concurrent.ExecutionException;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;
import com.torpill.fribot.threads.HelpThread;

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

		super("help", Command.ArgumentType.RAW, Command.Category.UTILITY);
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
	public int execute(final DiscordBot bot, final String[] args, final User user, final TextChannel channel, final Server server) {

		switch (args.length) {

		case 0:
			try {

				final TextChannel dm = user.openPrivateChannel().get();
				if (bot.startThread(HelpThread.class, user, dm) == 0) {

					channel.sendMessage("Utilitaire d'aide envoyé en MP " + user.getMentionTag());
				}

			} catch (InterruptedException | ExecutionException e) {

				e.printStackTrace();
			}
			break;

		case 1:
			final String commandName = args[0];
			final String help = bot.getHelpFor(commandName);
			final String example = bot.getExampleFor(commandName, user);
			final String type = bot.getTypeFor(commandName);
			final String category = bot.getCategoryFor(commandName);

			if (help == null) {

				channel.sendMessage(user.getMentionTag() + ", la commande `" + commandName + "` n'existe pas : fais `" + bot.getPrefix() + "help` pour avoir la liste des commandes.");
				return 2;
			}

			final EmbedBuilder embed = bot.defaultEmbedBuilder("Aide :", commandName + " :", user);
			embed.addField("Description :", help);
			if (example != null) embed.addField("Exemple :", example);
			embed.addField("Catégorie :", category);
			embed.addField("Type d'arguments :", type);
			if (!bot.isOwner(user)) {

				if (bot.isPrivate(commandName)) embed.addField("Commande privée :", "Seul le propriétaire du bot peut utiliser cette commande.");
				else if (bot.onBlacklist(user, commandName, server) == 1) embed.addField("Liste noire :", "Tu es sur la liste noire, tu ne peux pas utiliser cette commande.");
				else if (bot.onWhitelist(user, commandName, server) == 0) embed.addField("Liste blanche :", "Tu n'es pas sur la liste blanche, tu ne peux pas utiliser cette commande.");
				else if (!bot.isAdmin(user, server)) {

					if (bot.onRoleBlacklist(user, commandName, server) == 1) embed.addField("Liste noire :", "Tu as un rôle sur la liste noire, tu ne peux pas utiliser cette commande.");
					else if (bot.onRoleWhitelist(user, commandName, server) == 0) embed.addField("Liste blanche :", "Tu n'as aucun rôle sur la liste blanche, tu ne peux pas utiliser cette commande.");
					else if (bot.canUse(user, commandName, server) == 0) embed.addField("Permissions :", "Tu n'as pas les permissions requises pour utiliser cette commande.");
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
