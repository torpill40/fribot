package com.torpill.fribot.commands;

import java.util.List;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;

public class TypeCommand extends Command {

	public TypeCommand() {

		super("type", Command.ArgumentType.NONE);
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

		final EmbedBuilder embed = bot.defaultEmbedBuilder("Type :", "Les différents types d'arguments :", user);
		for (Command.ArgumentType type : Command.ArgumentType.values()) {

			embed.addField(type.NAME + " :", type.DESCRIPTION + "\n" + bot.getPrefix() + "<cmd> " + type.EXAMPLE);
		}
		channel.sendMessage(embed);

		return 0;
	}

}
