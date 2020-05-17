package com.torpill.fribot.commands.utility;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;

/**
 * 
 * Cette classe représente une commande affichant les différents types
 * d'argument, ainsi que leur fonctionnement et un exemple d'utilisation.
 * 
 * @author torpill40
 * 
 * @see com.torpill.fribot.commands.Command
 *
 */

public class TypeCommand extends Command {

	/**
	 * 
	 * Constructeur de la classe <code>TypeCommand</code>.
	 * 
	 */
	public TypeCommand() {

		super("type", Command.ArgumentType.NONE, Command.Category.UTILITY);
	}

	@Override
	public String getHelp() {

		return "Affiche les différents types d'arguments et leur utilisation.";
	}

	@Override
	public boolean deleteCommandUsage() {

		return true;
	}

	@Override
	public int execute(DiscordBot bot, String[] args, User user, TextChannel channel, Server server) {

		final EmbedBuilder embed = bot.defaultEmbedBuilder("Type :", "Les différents types d'arguments :", user);
		for (Command.ArgumentType type : Command.ArgumentType.values()) {

			embed.addField(type.NAME + " :", type.DESCRIPTION + "\n" + bot.getPrefix() + "<cmd> " + type.EXAMPLE);
		}
		channel.sendMessage(embed);

		return 0;
	}

}
