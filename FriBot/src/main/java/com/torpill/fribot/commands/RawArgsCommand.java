package com.torpill.fribot.commands;

import java.util.List;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;

public class RawArgsCommand extends Command {

	public RawArgsCommand() {
		
		super("__raw", Command.ArgumentType.RAW);
	}
	
	@Override
	public String getHelp() {
	
		return "Commande de test des arguments classiques.";
	}
	
	@Override
	public boolean deleteCommandUsage() {
	
		return false;
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

		return bot.allUsersFrom(server);
	}

	@Override
	public int execute(DiscordBot bot, String[] args, User user, TextChannel channel, Server server) {
		
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < args.length; i ++) {
			
			builder.append(i + " : \n- " + args[i] + "\n");
		}
		
		channel.sendMessage(builder.toString());
		
		return 0;
	}
}

