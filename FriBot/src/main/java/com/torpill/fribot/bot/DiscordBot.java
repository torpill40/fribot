package com.torpill.fribot.bot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

/**
 * 
 * Cette classe représente un bot Discord. Contient l'api en membre privé.
 * 
 * @author torpill40
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

			category = new ArrayList<Command>();
			this.categories.put(command.getCategory(), category);
		}

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
	public Role getUserRole(Server server) {

		Optional<Role> role = server.getRoleById(this.role);
		return (role.isPresent() ? role.get() : null);
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
	public Role getDevRole(Server server) {

		Optional<Role> devrole = server.getRoleById(this.devrole);
		return (devrole.isPresent() ? devrole.get() : null);
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

			e.printStackTrace();
		}

		return null;
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

		if (command == null) {

			return -1;
		}

		if (command.deleteCommandUsage()) message.delete();

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
	public EmbedBuilder defaultEmbedBuilder(String title, String description, User user) {

		EmbedBuilder embed = new EmbedBuilder();
		if (title != null) embed.setTitle(title);
		if (description != null) embed.setDescription(description);
		embed.setColor(this.color);
		embed.setThumbnail(this.api.getYourself().getAvatar());
		if (user != null) embed.setFooter("En réponse à " + user.getDiscriminatedName(), user.getAvatar());
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
	public String getHelpFor(String commandName) {

		final Command command = this.commands.get(commandName);
		return (command == null ? null : command.getHelp());
	}

	/**
	 * 
	 * Renvoyer le type d'argument d'une commande.
	 * 
	 * @param commandName
	 *            : nom de la commande.
	 * @return type d'argument.
	 * 
	 * @see com.torpill.fribot.commands.Command
	 * @see com.torpill.fribot.commands.Command.ArgumentType
	 */
	public String getTypeFor(String commandName) {

		final Command command = this.commands.get(commandName);
		return (command == null ? null : command.getType().NAME);
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
	public String getCategoryFor(String commandName) {

		final Command command = this.commands.get(commandName);
		return (command == null ? null : command.getCategory().NAME);
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
	public List<Command> commandsIn(Command.Category category) {

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
	public boolean is(User user) {

		return this.api.getYourself() == user;
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
	public boolean isAdmin(User user, Server server) {

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
	public boolean isOwner(User user) {

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
	public List<User> allUsersFrom(Server server) {

		List<User> users = new ArrayList<>();
		for (User user : server.getMembers()) {

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
	public List<Role> allRolesFrom(Server server) {

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
	public List<Role> roles(Server server, String... roles) {

		List<Role> list = new ArrayList<>();
		for (String roleId : roles) {

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
	 *            : IDs des rôles que l'on veut récupérer.
	 * @return liste des utilisateurs
	 * 
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 */
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
}
