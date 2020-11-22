package com.torpill.fribot.bot;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
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

/**
 *
 * Cette classe représente un bot Discord. Contient l'api en membre privé.
 *
 * @author torpill40
 *
 * @see org.javacord.api.DiscordApi
 *
 */

public class DiscordBot {

	private final String prefix;
	private final Map<String, Command> commands;
	private final Map<Command.Category, List<Command>> categories;
	private final Map<Class<? extends BotThread>, BotThread> threads;
	private final Color color;
	private final String role, devrole;
	private DiscordApi api;

	/**
	 *
	 * Constructeur de la classe <code>DiscordBot</code>.
	 *
	 * @param prefix
	 *            : prefix du bot.
	 * @param color
	 *            : couleur par défaut des embeds.
	 * @param role
	 *            : rôle utilisateur par défaut.
	 * @param devrole
	 *            : rôle développeur par défaut.
	 */
	public DiscordBot(final String prefix, final Color color, final String role, final String devrole) {

		this.prefix = prefix;
		this.color = color;
		this.role = role;
		this.devrole = devrole;
		this.commands = new HashMap<>();
		this.categories = new HashMap<>();
		this.threads = new HashMap<>();
	}

	/**
	 *
	 * Ajouter une commande au bot.
	 *
	 * @param command
	 *            : commande à rajouter.
	 */
	public void addCommand(final Command command) {

		this.commands.put(command.getName(), command);
		List<Command> category = this.categories.get(command.getCategory());
		if (category == null) {

			category = new ArrayList<>();
			this.categories.put(command.getCategory(), category);
		}

		App.LOGGER.debug("Commande {} (\u001B[4m\u001B[91m{}\u001B[96m.class\u001B[0m) ajoutée dans {}.", command.getName(), command.getClass().getSimpleName(), command.getCategory());

		category.add(command);
	}

	/**
	 *
	 * Rajouter un thread au bot.
	 *
	 * @param thread
	 *            : thread à rajouter.
	 *
	 * @see com.torpill.fribot.threads.BotThread
	 */
	public void addThread(final BotThread thread) {

		this.threads.put(thread.getClass(), thread);
	}

	/**
	 *
	 * Relier l'API Javacord avec le bot.
	 *
	 * @param api
	 *            : API Javacord.
	 * @return this
	 *
	 * @see org.javacord.api.DiscordApi
	 */
	public DiscordBot api(final DiscordApi api) {

		this.api = api;
		return this;
	}

	/**
	 *
	 * Récupérer le préfix du bot.
	 *
	 * @return préfix
	 */
	public String getPrefix() {

		return this.prefix;
	}

	/**
	 *
	 * Récupérer l'ID du rôle utilisateur.
	 *
	 * @return ID du rôle
	 */
	public String getUserRoleID() {

		return this.role;
	}

	/**
	 *
	 * Récupérer l'ID du rôle développeur.
	 *
	 * @return ID du rôle
	 */
	public String getDevRoleID() {

		return this.devrole;
	}

	/**
	 *
	 * Récupérer le rôle utilisateur.
	 *
	 * @return rôle utilisateur
	 *
	 * @see org.javacord.api.entity.permission.Role
	 * @see org.javacord.api.entity.server.Server
	 */
	public Role getUserRole(final Server server) {

		final Optional<Role> role = server.getRoleById(this.role);
		return role.isPresent() ? role.get() : null;
	}

	/**
	 *
	 * Récupérer le rôle développeur.
	 *
	 * @return rôle développeur
	 *
	 * @see org.javacord.api.entity.permission.Role
	 * @see org.javacord.api.entity.server.Server
	 */
	public Role getDevRole(final Server server) {

		final Optional<Role> devrole = server.getRoleById(this.devrole);
		return devrole.isPresent() ? devrole.get() : null;
	}

	/**
	 *
	 * Récupérer l'utilisateur du propriétaire du bot.
	 *
	 * @return propriétaire
	 *
	 * @see org.javacord.api.entity.user.User
	 */
	public User owner() {

		try {

			return this.api.getOwner().get();

		} catch (InterruptedException | ExecutionException e) {

			App.LOGGER.error("ERREUR: ", e);
		}

		return null;
	}

	/**
	 *
	 * Récupérer l'utilisateur du bot.
	 *
	 * @return bot
	 *
	 * @see org.javacord.api.entity.user.User
	 */
	public User bot() {

		return this.api.getYourself();
	}

	/**
	 *
	 * Exécuter une commande.
	 *
	 * @param user
	 *            : utilisateur de la commande.
	 * @param channel
	 *            : salon dans lequel est exécutée la commande.
	 * @param message
	 *            : message contenant la commande.
	 * @param server
	 *            : serveur dans lequel est exécutée la commande.
	 * @param commandName
	 *            : nom de la commande exécutée.
	 * @param args
	 *            : arguments passés à la commande.
	 * @return code d'erreur
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.channel.TextChannel
	 * @see org.javacord.api.entity.message.Message
	 * @see org.javacord.api.entity.server.Server
	 * @see com.torpill.fribot.commands.Command
	 */
	public int execute(final User user, final TextChannel channel, final Message message, final Server server, final String commandName, final String... args) {

		final Command command = this.commands.get(commandName);

		if (command == null) return -1;
		if (command.deleteCommandUsage()) {

			message.delete();
		}
		if (!this.isOwner(user)) {

			if (command.isPrivate()) return -2;

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

	/**
	 *
	 * Démarrer un thread connu du bot.
	 *
	 * @param thread
	 *            : class du thread à démarrer.
	 * @param args
	 *            : arguments passés au thread.
	 * @return code d'erreur
	 *
	 * @see com.torpill.fribot.threads.BotThread
	 */
	public int startThread(final Class<? extends BotThread> thread, final Object... args) {

		BotThread target = null;
		try {

			target = this.threads.get(thread).clone();

		} catch (final CloneNotSupportedException e) {

			e.printStackTrace();
		}

		if (target == null) return -1;

		App.LOGGER.debug("BotThread '" + target.getName() + "' trouvé, préparation de l'éxecution.");

		final int response = target.setArgs(args);
		if (response == 0) {

			final Thread thr = new Thread(target);
			thr.setName(target.getName());
			App.LOGGER.debug("Démarrage de '" + thr.getName() + "'.");
			thr.start();
		}

		return response;
	}

	/**
	 *
	 * Générer l'embed par défaut du bot.
	 *
	 * @param title
	 *            : titre de l'embed.
	 * @param description
	 *            : description de l'embed.
	 * @param user
	 *            : utilisateur demandant la création de l'embed.
	 * @return embed par défaut
	 *
	 * @see org.javacord.api.entity.message.embed.EmbedBuilder
	 */
	public EmbedBuilder defaultEmbedBuilder(final String title, final String description, final User user) {

		final EmbedBuilder embed = new EmbedBuilder();
		if (title != null) {

			embed.setTitle(title);
		}
		if (description != null) {

			embed.setDescription(description);
		}
		embed.setColor(this.color);
		embed.setThumbnail(this.api.getYourself().getAvatar());
		if (user != null) {

			embed.setFooter("En réponse à " + user.getDiscriminatedName(), user.getAvatar());
		}
		return embed;
	}

	/**
	 *
	 * Renvoyer le message d'aide d'une commande.
	 *
	 * @param commandName
	 *            : nom de la commande.
	 * @return message d'aide
	 *
	 * @see com.torpill.fribot.commands.Command
	 */
	public String getHelpFor(final String commandName) {

		final Command command = this.commands.get(commandName);
		return command == null ? null : command.getHelp();
	}

	/**
	 *
	 * Renvoyer l'exemple d'utilisation d'une commande.
	 *
	 * @param commandName
	 *            : nom de la commande.
	 * @param user
	 *            : utilisateur sur lequel on applique l'exemple
	 * @return exemple d'utilisation
	 *
	 * @see com.torpill.fribot.commands.Command
	 * @see org.javacord.api.entity.user.User
	 */
	public String getExampleFor(final String commandName, final User user) {

		final Command command = this.commands.get(commandName);
		return command == null ? null : command.getExample(this.prefix, user);
	}

	/**
	 *
	 * Renvoyer le type d'argument d'une commande.
	 *
	 * @param commandName
	 *            : nom de la commande.
	 * @return type d'argument
	 *
	 * @see com.torpill.fribot.commands.Command
	 * @see com.torpill.fribot.commands.Command.ArgumentType
	 */
	public String getTypeFor(final String commandName) {

		final Command command = this.commands.get(commandName);
		return command == null ? null : command.getType().NAME;
	}

	/**
	 *
	 * Renvoyer le nom de la catégorie d'une commande.
	 *
	 * @param commandName
	 *            : nom de la commande.
	 * @return type d'argument.
	 *
	 * @see com.torpill.fribot.commands.Command
	 * @see com.torpill.fribot.commands.Command.Category
	 */
	public String getCategoryFor(final String commandName) {

		final Command command = this.commands.get(commandName);
		return command == null ? null : command.getCategory().NAME;
	}

	/**
	 *
	 * Récupérer les commandes dans une catégorie particulière.
	 *
	 * @param category
	 *            : la catégorie dont l'on veut récupérer les commandes.
	 * @return liste de commandes
	 *
	 * @see com.torpill.fribot.commands.Command
	 * @see com.torpill.fribot.commands.Command.Category
	 */
	public List<Command> commandsIn(final Command.Category category) {

		return this.categories.get(category);
	}

	/**
	 *
	 * Savoir si l'utilisateur passé en argument est le bot.
	 *
	 * @param user
	 *            : utilisateur à comparer.
	 * @return booléen
	 *
	 * @see org.javacord.entity.user.User
	 */
	public boolean is(final User user) {

		return this.api.getYourself().getId() == user.getId();
	}

	/**
	 *
	 * Savoir si l'utilisateur passé en argument est un administrateur du serveur
	 * passé en argument.
	 *
	 * @param user
	 *            : utilisateur à tester.
	 * @param server
	 *            : serveur sur lequel on teste l'utilisateur.
	 * @return booléen
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 * @see org.javacord.api.entity.permission.PermissionType
	 */
	public boolean isAdmin(final User user, final Server server) {

		final Collection<PermissionType> userPermissions = server.getAllowedPermissions(user);
		if (userPermissions.contains(PermissionType.ADMINISTRATOR)) {

			App.LOGGER.debug(user.getDiscriminatedName() + " est un administrateur.");
			return true;
		}

		return false;
	}

	/**
	 *
	 * Savoir si l'utilisateur passé en argument est le propriétaire du bot.
	 *
	 * @param user
	 *            : utilisateur à comparer.
	 * @return booléen
	 *
	 * @see org.javacord.api.entity.user.User
	 */
	public boolean isOwner(final User user) {

		if (user.getId() == this.owner().getId()) {

			App.LOGGER.debug(user.getDiscriminatedName() + " est le propriétaire du bot.");
			return true;
		}

		return false;
	}

	/**
	 *
	 * Vérifier si un utilisateur à les permissions pour exécuter une commande.
	 *
	 * @param user
	 *            : utilisateur voulant exécuter la commande.
	 * @param commandName
	 *            : commande à exécuter.
	 * @param server
	 *            : serveur sur lequel l'utilisateur veut exécuter la commande.
	 * @return code d'exécution
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 * @see org.javacord.api.entity.permission.PermissionType
	 * @see com.torpill.fribot.commands.Command
	 */
	public int canUse(final User user, final String commandName, final Server server) {

		final Command command = this.commands.get(commandName);
		final List<PermissionType> needed = command.permissionNeeded();
		final Collection<PermissionType> userPermissions = server.getAllowedPermissions(user);
		if (needed != null) {

			for (final PermissionType permission : needed) {

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

	/**
	 *
	 * Savoir si un utilisateur est sur la liste noire d'une commande.
	 *
	 * @param user
	 *            : utilisateur voulant exécuter la commande.
	 * @param commandName
	 *            : commande à exécuter.
	 * @param server
	 *            : serveur sur lequel l'utilisateur veut exécuter la commande.
	 * @return code d'exécution
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 * @see com.torpill.fribot.commands.Command
	 */
	public int onBlacklist(final User user, final String commandName, final Server server) {

		final Command command = this.commands.get(commandName);
		final List<User> blackListedUsers = command.blackListedUsers(this, server);
		if (blackListedUsers != null) {

			for (final User blackListedUser : blackListedUsers) {

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

	/**
	 *
	 * Savoir si un utilisateur est sur la liste blanche d'une commande.
	 *
	 * @param user
	 *            : utilisateur voulant exécuter la commande.
	 * @param commandName
	 *            : commande à exécuter.
	 * @param server
	 *            : serveur sur lequel l'utilisateur veut exécuter la commande.
	 * @return code d'exécution
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 * @see com.torpill.fribot.commands.Command
	 */
	public int onWhitelist(final User user, final String commandName, final Server server) {

		final Command command = this.commands.get(commandName);
		final List<User> whiteListedUsers = command.whiteListedUsers(this, server);
		if (whiteListedUsers != null) {

			for (final User whiteListedUser : whiteListedUsers) {

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

	/**
	 *
	 * Savoir si un utilisateur possède un role sur la liste noire d'une commande.
	 *
	 * @param user
	 *            : utilisateur voulant exécuter la commande.
	 * @param commandName
	 *            : commande à exécuter.
	 * @param server
	 *            : serveur sur lequel l'utilisateur veut exécuter la commande.
	 * @return code d'exécution
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 * @see org.javacord.api.entity.permission.Role
	 * @see com.torpill.fribot.commands.Command
	 */
	public int onRoleBlacklist(final User user, final String commandName, final Server server) {

		final Command command = this.commands.get(commandName);
		final List<Role> userRoles = server.getRoles(user);
		final List<Role> blackListedRoles = command.blackListedRoles(this, server);
		if (blackListedRoles != null) {

			for (final Role blackListedRole : blackListedRoles) {

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

	/**
	 *
	 * Savoir si un utilisateur possède un role sur la liste blanche d'une commande.
	 *
	 * @param user
	 *            : utilisateur voulant exécuter la commande.
	 * @param commandName
	 *            : commande à exécuter.
	 * @param server
	 *            : serveur sur lequel l'utilisateur veut exécuter la commande.
	 * @return code d'exécution
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 * @see org.javacord.api.entity.permission.Role
	 * @see com.torpill.fribot.commands.Command
	 */
	public int onRoleWhitelist(final User user, final String commandName, final Server server) {

		final Command command = this.commands.get(commandName);
		final List<Role> userRoles = server.getRoles(user);
		final List<Role> whiteListedRoles = command.whiteListedRoles(this, server);
		if (whiteListedRoles != null) {

			for (final Role whiteListedRole : whiteListedRoles) {

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

	/**
	 *
	 * Savoir si une commande est privée.
	 *
	 * @param commandName
	 *            : nom de la commande.
	 * @return booléen
	 *
	 * @see com.torpill.fribot.commands.Command
	 */
	public boolean isPrivate(final String commandName) {

		final Command command = this.commands.get(commandName);
		return command.isPrivate();
	}

	/**
	 *
	 * Récupérer tous les utilisateurs membres du serveur passé en argument.
	 *
	 * @param server
	 *            : serveur dont on veut récupérer les utilisateurs.
	 * @return liste des utilisateurs membres
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 */
	public List<User> allUsersFrom(final Server server) {

		final List<User> users = new ArrayList<>();
		for (final User user : server.getMembers()) {

			users.add(user);
		}
		return users;
	}

	/**
	 *
	 * Récupérer tous les rôles du serveur passé en argument.
	 *
	 * @param server
	 *            : serveur dont on veut récupérer les rôles.
	 * @return liste des rôles
	 *
	 * @see org.javacord.api.entity.permission.Role
	 * @see org.javacord.api.entity.server.Server
	 */
	public List<Role> allRolesFrom(final Server server) {

		return server.getRoles();
	}

	/**
	 *
	 * Récupérer les rôles, dont les IDs ont été passés en arguments, du serveur
	 * passé en argument.
	 *
	 * @param server
	 *            : serveur dont on veut récupérer les rôles.
	 * @param roles
	 *            : IDs des rôles que l'on veut récupérer.
	 * @return liste des rôles
	 *
	 * @see org.javacord.api.entity.permission.Role
	 * @see org.javacord.api.entity.server.Server
	 */
	public List<Role> roles(final Server server, final String... roles) {

		final List<Role> list = new ArrayList<>();
		for (String roleId : roles) {

			if (roleId.equals("user-role")) roleId = this.getUserRoleID();
			if (roleId.equals("dev-role")) roleId = this.getDevRoleID();
			server.getRoleById(roleId).ifPresent(role -> {

				list.add(role);
			});
		}
		return list;
	}

	/**
	 *
	 * Récupérer les rôles, dont les IDs ont été passés en arguments, du serveur
	 * passé en argument.
	 *
	 * @param server
	 *            : serveur dont on veut récupérer les rôles.
	 * @param roles
	 *            : IDs des rôles que l'on veut récupérer.
	 * @return liste des rôles
	 *
	 * @see org.javacord.api.entity.permission.Role
	 * @see org.javacord.api.entity.server.Server
	 */
	public List<Role> rolesList(final Server server, final List<String> roles) {

		if (roles == null) return null;
		final List<Role> list = new ArrayList<>();
		for (String roleId : roles) {

			if (roleId.equals("user-role")) roleId = this.getUserRoleID();
			if (roleId.equals("dev-role")) roleId = this.getDevRoleID();
			server.getRoleById(roleId).ifPresent(role -> {

				list.add(role);
			});
		}
		return list;
	}

	/**
	 *
	 * Récupérer les utilisateurs dont les IDs ont été passés en arguments.
	 *
	 * @param users
	 *            : IDs des utilisateurs que l'on veut récupérer.
	 * @return liste des utilisateurs
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 */
	public List<User> users(final String... users) {

		final List<User> list = new ArrayList<>();
		for (final String userId : users) {

			try {

				list.add(this.api.getUserById(userId).get());

			} catch (InterruptedException | ExecutionException e) {

				App.LOGGER.error("ERREUR: ", e);
			}
		}
		return list;
	}

	/**
	 *
	 * Récupérer les utilisateurs dont les IDs ont été passés en arguments.
	 *
	 * @param users
	 *            : IDs des utilisateurs que l'on veut récupérer.
	 * @return liste des utilisateurs
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 */
	public List<User> usersList(final List<String> users) {

		if (users == null) return null;
		final List<User> list = new ArrayList<>();
		for (final String userId : users) {

			try {

				list.add(this.api.getUserById(userId).get());

			} catch (InterruptedException | ExecutionException e) {

				App.LOGGER.error("ERREUR: ", e);
			}
		}
		return list;
	}

	/**
	 *
	 * Récupérer la date de création du bot.
	 *
	 * @return date de création
	 */
	public String getCreationDate() {

		return this.api.getYourself().getCreationTimestamp().toString();
	}

	/**
	 *
	 * Récupérer le nom du bot.
	 *
	 * @return nom du bot
	 */
	public String getName() {

		return this.api.getYourself().getDiscriminatedName();
	}

	/**
	 *
	 * Récupérer la couleur par défaut du bot.
	 *
	 * @return couleur par défaut.
	 */
	public Color getColor() {

		return this.color;
	}

	/**
	 *
	 * Ajouter un rôle à un membre d'un serveur.
	 *
	 * @param user
	 *            : membre du serveur
	 * @param server
	 *            : serveur sur lequel on ajoute le rôle
	 * @param id
	 *            : ID du rôle à ajouter
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.permission.Role
	 * @see org.javacord.api.entity.server.Server
	 */
	public void addRoleToUser(final User user, final Server server, final String id) {

		final Optional<Role> optRole = server.getRoleById(id);
		if (optRole.isPresent()) {

			final Role role = optRole.get();
			server.addRoleToUser(user, role);
		}
	}

	/**
	 *
	 * Récupérer l'avatar d'un utilisateur.
	 *
	 * @param user
	 *            : utilisateur dont on veut récupérer l'avatar
	 * @return avatar de l'utilisateur
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see java.awt.image.BufferedImage
	 */
	public BufferedImage getAvatar(final User user) {

		try {

			return user.getAvatar().asBufferedImage().get();

		} catch (InterruptedException | ExecutionException e) {

			App.LOGGER.error("ERREUR: ", e);
		}

		return new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 *
	 * Récupérer le salon textuel d'un serveur selon une mention.
	 *
	 * @param mention
	 *            : mention du salon
	 * @param server
	 *            : serveur sur lequel on cherche le salon
	 * @return salon textuel correspondant
	 *
	 * @see org.javacord.api.entity.channel.TextChannel
	 * @see org.javacord.api.entity.server.Server
	 */
	public TextChannel getTextChannelFromMention(final String mention, final Server server) {

		final Pattern pattern = Pattern.compile("^<#[0-9]{1,}>$");
		final Matcher matcher = pattern.matcher(mention);
		TextChannel channel = null;
		while (matcher.find()) {

			if (channel != null) return null;

			final Optional<ServerTextChannel> optChannel = server.getTextChannelById(mention.split("<#")[1].split(">")[0]);
			if (optChannel.isPresent()) {

				channel = optChannel.get();
			}
		}

		return channel;
	}

	/**
	 *
	 * Récupérer le rôle d'un serveur selon une mention.
	 *
	 * @param mention
	 *            : mention du rôle
	 * @param server
	 *            : serveur sur lequel on cherche le rôle
	 * @return rôle correspondant
	 *
	 * @see org.javacord.api.entity.permission.Role
	 * @see org.javacord.api.entity.server.Server
	 */
	public Role getRoleFromMention(final String mention, final Server server) {

		final Pattern pattern = Pattern.compile("^<@&[0-9]{1,}>$");
		final Matcher matcher = pattern.matcher(mention);
		Role role = null;
		while (matcher.find()) {

			if (role != null) return null;

			final Optional<Role> optRole = server.getRoleById(mention.split("<@&")[1].split(">")[0]);
			if (optRole.isPresent()) {

				role = optRole.get();
			}
		}

		return role;
	}

	/**
	 *
	 * Récupérer un utilisateur depuis une de ses caractéristiques.
	 *
	 * @param server
	 *            : serveur sur lequel on cherche l'utilisateur
	 * @param channel
	 *            : salon dans lequel on envoie les messages d'erreur
	 * @param user
	 *            : utilisateur voulant récupérer l'utilisateur correspondant au
	 *            memebre
	 * @param member
	 *            : caractéristique de l'utilisateur
	 * @return utilisteur correspondant
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 * @see org.javacord.api.entity.channel.TextChannel
	 */
	public User getUser(final Server server, final TextChannel channel, final User user, final String member) {

		if (member.matches("^<@!?[0-9]{1,}>$")) {

			final User user0 = this.getUserFromMention(member);
			if (user0 == null) channel.sendMessage(this.defaultEmbedBuilder("Membre inconnu :", "La mention passée en paramètre n'existe pas.", user).addField("Mention :", member));
			return user0;

		} else if (member.matches("^[0-9]{1,}$")) {

			final User user0 = this.getUserFromID(member);
			if (user0 == null) channel.sendMessage(this.defaultEmbedBuilder("Membre inconnu :", "L'ID passé en paramètre n'existe pas.", user).addField("ID :", member));
			return user0;

		} else if (member.matches("^.{1,}#[0-9]{4}$")) {

			final User user0 = this.getUserFromDiscriminatedName(server, member);
			if (user0 == null) channel.sendMessage(this.defaultEmbedBuilder("Membre inconnu :", "Le pseudo passé en paramètre n'existe pas.", user).addField("Pseudo :", member));
			return user0;

		} else {

			final List<User> displayUsers = this.getUsersFromDisplayName(server, member);
			if (displayUsers.size() == 1) return displayUsers.get(0);
			final boolean flag = displayUsers.size() > 1;
			final List<User> nameUsers = this.getUserFromName(server, member);
			if (nameUsers.size() == 1) return nameUsers.get(0);
			else if (nameUsers.size() == 0) {

				if (flag) {

					final EmbedBuilder embed = this.defaultEmbedBuilder("Plusieurs membres possibles :", "Le pseudo passé en paramètre correspond à plusieurs membres du serveur. Réessaies avec un membre ci-dessous.", user);
					for (final User user0 : displayUsers) embed.addInlineField(user0.getDisplayName(server) + " :", user0.getDiscriminatedName());
					channel.sendMessage(embed);

				} else channel.sendMessage(this.defaultEmbedBuilder("Membre inconnu :", "Le pseudo passé en paramètre n'existe pas.", user).addField("Pseudo :", member));

			} else {

				final EmbedBuilder embed = this.defaultEmbedBuilder("Plusieurs membres possibles :", "Le pseudo passé en paramètre correspond à plusieurs membres du serveur. Réessaies avec un membre ci-dessous.", user);
				for (final User user0 : nameUsers) embed.addInlineField(user0.getDisplayName(server) + " :", user0.getDiscriminatedName());
				channel.sendMessage(embed);
			}
			return null;
		}
	}

	/**
	 *
	 * Récupérer un utilisateur depuis une mention.
	 *
	 * @param mention
	 *            : mention de l'utilisateur
	 * @return utilisateur correspondant
	 *
	 * @see org.javacord.api.entity.user.User
	 */
	public User getUserFromMention(final String mention) {

		try {

			return this.api.getUserById(mention.split("<@!?")[1].split(">")[0]).get();

		} catch (final IndexOutOfBoundsException | InterruptedException | ExecutionException e) {

			App.LOGGER.error("ERREUR: ", e);
			return null;
		}
	}

	/**
	 *
	 * Récupérer un utilisateur depuis un ID.
	 *
	 * @param id
	 *            : ID de l'utilisateur
	 * @return utilisateur correspondant
	 *
	 * @see org.javacord.api.entity.user.User
	 */
	public User getUserFromID(final String id) {

		try {

			return this.api.getUserById(id).get();

		} catch (InterruptedException | ExecutionException e) {

			App.LOGGER.error("ERREUR: ", e);
			return null;
		}
	}

	/**
	 *
	 * Récupérer un utilisateur depuis un nom.
	 *
	 * @param server
	 *            : serveur sur lequel on cherche l'utilisateur
	 * @param name
	 *            : nom de l'utilisateur
	 * @return utilisateur correspondant
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 */
	public List<User> getUserFromName(final Server server, final String name) {

		final Collection<User> colUsers = server.getMembersByNameIgnoreCase(name);
		final List<User> users = new ArrayList<>(colUsers);

		return users;
	}

	/**
	 *
	 * Récupérer un utilisateur depuis un nom avec son descriminant.
	 *
	 * @param server
	 *            : serveur sur lequel on cherche l'utilisateur
	 * @param name
	 *            : nom descriminé de l'utilisateur
	 * @return utilisateur correspondant
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 */
	public User getUserFromDiscriminatedName(final Server server, final String discriminatedName) {

		final Optional<User> optUser = server.getMemberByDiscriminatedNameIgnoreCase(discriminatedName);

		if (optUser.isPresent()) return optUser.get();

		return null;
	}

	/**
	 *
	 * Récupérer un utilisateur depuis un nom visible.
	 *
	 * @param server
	 *            : serveur sur lequel on cherche l'utilisateur
	 * @param name
	 *            : nom visible de l'utilisateur
	 * @return utilisateur correspondant
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 */
	public List<User> getUsersFromDisplayName(final Server server, final String name) {

		final Collection<User> colUsers = server.getMembersByDisplayNameIgnoreCase(name);
		final List<User> users = new ArrayList<>(colUsers);

		return users;
	}

	/**
	 *
	 * Envoyer un message dans un salon textuel.
	 *
	 * @param serverId
	 *            : ID du serveur
	 * @param channelId
	 *            : ID du salon textuel
	 * @param text
	 *            : Texte du message
	 */
	public void send(final String serverId, final String channelId, final String text) {

		this.api.getServerById(serverId).ifPresent(server -> {

			server.getTextChannelById(channelId).ifPresent(channel -> {

				channel.sendMessage(text);
			});
		});
	}
}
