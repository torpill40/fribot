package com.torpill.fribot.bot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.App;
import com.torpill.fribot.commands.Command;
import com.torpill.fribot.threads.BotThread;
import com.torpill.fribot.threads.HelpThread;

public class DiscordBot {

	private final String prefix;
	private final Map<String, Command> commands;
	private final Map<Class<? extends BotThread>, BotThread> threads;
	private final Color color;
	private final String role;
	private DiscordApi api;

	public DiscordBot(final String prefix, final Color color, final String role) {

		this.prefix = prefix;
		this.color = color;
		this.role = role;
		this.commands = new HashMap<>();
		this.threads = new HashMap<>();
	}

	public void addCommand(final Command command) {

		this.commands.put(command.getName(), command);
	}

	public void addThread(final BotThread thread) {

		this.threads.put(thread.getClass(), thread);
	}

	public DiscordBot api(final DiscordApi api) {

		this.api = api;
		return this;
	}

	public String getPrefix() {

		return this.prefix;
	}

	public String getUserRole() {

		return this.role;
	}

	public User owner() {

		try {

			return this.api.getOwner().get();

		} catch (InterruptedException | ExecutionException e) {

			e.printStackTrace();
		}

		return null;
	}

	public int execute(final User user, final TextChannel channel, final Message message, final Server server, final String commandName, final String... args) {

		final Command command = this.commands.get(commandName);

		if (command == null) {

			return -1;
		}

		if (command.deleteCommandUsage()) message.delete();

		if (!this.isOwner(user)) {

			if (this.onBlacklist(user, commandName, server) == 1) return -2;
			else if (this.onWhitelist(user, commandName, server) == 0) return -2;
			else if (!this.isAdmin(user, server)) {

				if (this.onRoleBlacklist(user, commandName, server) == 1) return -2;
				else if (this.onRoleWhitelist(user, commandName, server) == 0) return -2;
				else if (this.canUse(user, commandName, server) == 0) return -2;
			}
		}

		return command.execute(this, command.parseArguments(args), user, channel, server);
	}

	public int startThread(Class<? extends BotThread> thread, Object... args) {

		BotThread target = null;

		try {

			target = this.threads.get(thread).clone();

		} catch (CloneNotSupportedException e) {

			e.printStackTrace();
		}

		if (target == null) {

			return -1;
		}

		App.LOGGER.debug("BotThread '" + target.getName() + "' trouvé, préparation de l'éxecution.");

		final int response = target.setArgs(args);
		if (response == 0) {

			Thread thr = new Thread(target);
			thr.setName(target.getName());
			App.LOGGER.debug("Démarrage de '" + thr.getName() + "'.");
			thr.start();
		}

		return response;
	}

	public EmbedBuilder defaultEmbedBuilder(String title, String description, User user) {

		EmbedBuilder embed = new EmbedBuilder();
		if (title != null) embed.setTitle(title);
		if (description != null) embed.setDescription(description);
		embed.setColor(this.color);
		embed.setThumbnail(this.api.getYourself().getAvatar());
		if (user != null) embed.setFooter("En réponse à " + user.getDiscriminatedName(), user.getAvatar());
		return embed;
	}

	public String getHelpFor(String commandName) {

		final Command command = this.commands.get(commandName);
		return (command == null ? null : command.getHelp());
	}
	
	public String getTypeFor(String commandName) {

		final Command command = this.commands.get(commandName);
		return (command == null ? null : command.getType().NAME);
	}

	public void displayCommandList(TextChannel channel, User user) {

		this.startThread(HelpThread.class, user, channel, this.commands);
	}

	public boolean is(User user) {

		return this.api.getYourself() == user;
	}

	public boolean isAdmin(User user, Server server) {

		final Collection<PermissionType> userPermissions = server.getAllowedPermissions(user);
		if (userPermissions.contains(PermissionType.ADMINISTRATOR)) {

			App.LOGGER.debug(user.getDiscriminatedName() + " est un administrateur.");
			return true;
		}

		return false;
	}

	public boolean isOwner(User user) {

		if (user.getId() == this.owner().getId()) {

			App.LOGGER.debug(user.getDiscriminatedName() + " est le propriétaire du bot.");
			return true;
		}

		return false;
	}

	public int canUse(User user, String commandName, Server server) {

		final Command command = this.commands.get(commandName);
		final List<PermissionType> needed = command.permissionNeeded();
		final Collection<PermissionType> userPermissions = server.getAllowedPermissions(user);
		if (needed != null) {

			for (PermissionType permission : needed) {

				if (!userPermissions.contains(permission)) {

					App.LOGGER.debug(user.getDiscriminatedName() + " n'a pas toute les permissions requises.");
					return 0;
				}
			}

			App.LOGGER.debug(user.getDiscriminatedName() + " a pas toute les permissions requises.");
			return 1;

		} else {

			App.LOGGER.debug("Pas de permission requise.");
			return -1;
		}
	}

	public int onBlacklist(User user, String commandName, Server server) {

		final Command command = this.commands.get(commandName);
		final List<User> blackListedUsers = command.blackListedUsers(this, server);
		if (blackListedUsers != null) {

			for (User blackListedUser : blackListedUsers) {

				if (blackListedUser != null && user.getId() == blackListedUser.getId()) {

					App.LOGGER.debug(user.getDiscriminatedName() + " est sur la liste noire.");
					return 1;
				}
			}

			App.LOGGER.debug(user.getDiscriminatedName() + " n'est sur la liste noire.");
			return 0;

		} else {

			App.LOGGER.debug("Pas de liste noire.");
			return -1;
		}
	}

	public int onWhitelist(User user, String commandName, Server server) {

		final Command command = this.commands.get(commandName);
		final List<User> whiteListedUsers = command.whiteListedUsers(this, server);
		if (whiteListedUsers != null) {

			for (User whiteListedUser : whiteListedUsers) {

				if (whiteListedUser != null && user.getId() == whiteListedUser.getId()) {

					App.LOGGER.debug(user.getDiscriminatedName() + " est sur la liste blanche.");
					return 1;
				}
			}

			App.LOGGER.debug(user.getDiscriminatedName() + " n'est pas sur la liste blanche.");
			return 0;

		} else {

			App.LOGGER.debug("Pas de liste blanche.");
			return -1;
		}
	}

	public int onRoleBlacklist(User user, String commandName, Server server) {

		final Command command = this.commands.get(commandName);
		final List<Role> userRoles = server.getRoles(user);
		final List<Role> blackListedRoles = command.blackListedRoles(this, server);
		if (blackListedRoles != null) {

			for (Role blackListedRole : blackListedRoles) {

				if (blackListedRole != null && userRoles.contains(blackListedRole)) {

					App.LOGGER.debug(user.getDiscriminatedName() + " a un role sur la liste noire.");
					return 1;
				}
			}

			App.LOGGER.debug(user.getDiscriminatedName() + " n'a pas de role sur la liste noire.");
			return 0;

		} else {

			App.LOGGER.debug("Pas de liste noire pour les roles.");
			return -1;
		}
	}

	public int onRoleWhitelist(User user, String commandName, Server server) {

		final Command command = this.commands.get(commandName);
		final List<Role> userRoles = server.getRoles(user);
		final List<Role> whiteListedRoles = command.whiteListedRoles(this, server);
		if (whiteListedRoles != null) {

			for (Role whiteListedRole : whiteListedRoles) {

				if (whiteListedRole != null && userRoles.contains(whiteListedRole)) {

					App.LOGGER.debug(user.getDiscriminatedName() + " a un role sur la liste blanche.");
					return 1;
				}
			}

			App.LOGGER.debug(user.getDiscriminatedName() + " n'a pas de role sur la liste blanche.");
			return 0;

		} else {

			App.LOGGER.debug("Pas de liste blanche pour les roles.");
			return -1;
		}
	}

	public List<User> allUsersFrom(Server server) {

		List<User> users = new ArrayList<>();
		for (User user : server.getMembers()) {

			users.add(user);
		}
		return users;
	}

	public List<Role> allRolesFrom(Server server) {

		return server.getRoles();
	}

	public List<Role> roles(Server server, String... roles) {

		List<Role> list = new ArrayList<>();
		for (String roleId : roles) {

			server.getRoleById(roleId).ifPresent(role -> {

				list.add(role);
			});
		}
		return list;
	}

	public List<User> users(String... users) {

		List<User> list = new ArrayList<>();
		for (String userId : users) {

			try {

				list.add(this.api.getUserById(userId).get());

			} catch (InterruptedException | ExecutionException e) {

				e.printStackTrace();
			}
		}
		return list;
	}

	public String getCreationDate() {

		return this.api.getYourself().getCreationTimestamp().toString();
	}

	public String getName() {

		return this.api.getYourself().getDiscriminatedName();
	}
	
	public Color getColor() {
		
		return this.color;
	}
}
