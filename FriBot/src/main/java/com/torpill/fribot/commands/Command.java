package com.torpill.fribot.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;

/**
 *
 * Cette classe représente une commande.
 *
 * @author torpill40
 *
 */

public abstract class Command {

	private final String name;
	private final ArgumentType argumentType;
	private final Category category;

	/**
	 *
	 * Constructeur de la classe <code>Command</code>.
	 *
	 * @param name
	 *            : nom de la commande.
	 * @param argumentType
	 *            : type d'argument de la commande.
	 * @param category
	 *            : catégorie de la commande.
	 *
	 * @see com.torpill.fribot.commands.Command.ArgumentType
	 * @see com.torpill.fribot.commands.Command.Category
	 */
	protected Command(final String name, final ArgumentType argumentType, final Category category) {

		this.name = name;
		this.argumentType = argumentType;
		this.category = category;
	}

	/**
	 *
	 * Récupérer le message d'aide de la commande.
	 *
	 * @return message daide
	 */
	public abstract String getHelp();

	/**
	 *
	 * Savoir si le message de la commande doit être supprimé lors de l'exécution.
	 *
	 * @return booléen
	 */
	public abstract boolean deleteCommandUsage();

	/**
	 *
	 * Récupérer les permissions requises à l'utilisation de la commande.
	 *
	 * @return liste de permissions.
	 *
	 * @see org.javacord.api.entity.permission.PermissionType
	 */
	public List<PermissionType> permissionNeeded() {

		return null;
	}

	/**
	 *
	 * Récupérer les rôles sur la liste blanche de la commande.
	 *
	 * @param bot
	 *            : bot Discord récupérant les informations.
	 * @param server
	 *            : serveur sur lequel on cherche les informations.
	 * @return liste de rôles
	 *
	 * @see org.javacord.api.entity.permission.Role
	 * @see org.javacord.api.entity.server.Server
	 * @see com.torpill.fribot.bot.DiscordBot
	 */
	public List<Role> whiteListedRoles(final DiscordBot bot, final Server server) {

		return null;
	}

	/**
	 *
	 * Récupérer les rôles sur la liste noire de la commande.
	 *
	 * @param bot
	 *            : bot Discord récupérant les informations.
	 * @param server
	 *            : serveur sur lequel on cherche les informations.
	 * @return liste de rôles
	 *
	 * @see org.javacord.api.entity.permission.Role
	 * @see org.javacord.api.entity.server.Server
	 * @see com.torpill.fribot.bot.DiscordBot
	 */
	public List<Role> blackListedRoles(final DiscordBot bot, final Server server) {

		return null;
	}

	/**
	 *
	 * Récupérer les utilisateurs sur la liste blanche de la commande.
	 *
	 * @param bot
	 *            : bot Discord récupérant les informations.
	 * @param server
	 *            : serveur sur lequel on cherche les informations.
	 * @return liste d'utilisateurs
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 * @see com.torpill.fribot.bot.DiscordBot
	 */
	public List<User> whiteListedUsers(final DiscordBot bot, final Server server) {

		return null;
	}

	/**
	 *
	 * Récupérer les utilisateurs sur la liste noire de la commande.
	 *
	 * @param bot
	 *            : bot Discord récupérant les informations.
	 * @param server
	 *            : serveur sur lequel on cherche les informations.
	 * @return liste d'utilisateurs
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.server.Server
	 * @see com.torpill.fribot.bot.DiscordBot
	 */
	public List<User> blackListedUsers(final DiscordBot bot, final Server server) {

		return null;
	}

	/**
	 *
	 * Exécuter la commande.
	 *
	 * @param bot
	 *            : bot Discord a demandant l'exécution de la commande.
	 * @param args
	 *            : arguments passés lors de l'appel de la commande.
	 * @param user
	 *            : utilisateur utilisant la commande.
	 * @param channel
	 *            : salon dans lequel est exécutée la commande.
	 * @param server
	 *            : serveur dans lequel est exécutée la commande.
	 * @return code d'erreur
	 *
	 * @see org.javacord.api.entity.user.User
	 * @see org.javacord.api.entity.channel.TextChannel
	 * @see org.javacord.api.entity.server.Server
	 * @see com.torpill.fribot.bot.DiscordBot
	 */
	public abstract int execute(final DiscordBot bot, final String args[], final User user, final TextChannel channel, final Server server);

	/**
	 *
	 * Récupérer l'exemple d'utilisation de la commande.
	 *
	 * @param prefix
	 *            : prefix à appliquer à l'exemple
	 * @param user
	 *            : utilisateur sur lequel on applique l'exemple
	 * @return exemple d'utilisation
	 *
	 * @see org.javacord.api.entity.user.User
	 */
	public String getExample(final String prefix, final User user) {

		return prefix + this.getName();
	}

	/**
	 *
	 * Convertir les arguments au format demandé par la commande.
	 *
	 * @param args
	 *            : arguments en entrée.
	 * @return arguments convertis
	 */
	public String[] parseArguments(final String[] args) {

		return this.argumentType.parseArguments(args);
	}

	/**
	 *
	 * Récupérer le nom de la commande.
	 *
	 * @return nom de la commande
	 */
	public String getName() {

		return this.name;
	}

	/**
	 *
	 * Récupérer le type d'argument de la commande.
	 *
	 * @return type d'argument
	 *
	 * @see com.torpill.fribot.commands.Command.ArgumentType
	 */
	public ArgumentType getType() {

		return this.argumentType;
	}

	/**
	 *
	 * Récupérer la catégorie de la commande.
	 *
	 * @return catégorie
	 *
	 * @see com.torpill.fribot.commands.Command.Category
	 */
	public Category getCategory() {

		return this.category;
	}

	/**
	 *
	 * Savoir si la commande est privée.
	 *
	 * @return booléen
	 */
	public boolean isPrivate() {

		return this.name.startsWith("__");
	}

	/**
	 *
	 * Cette classe énumératrice représente les différents types d'argument.
	 *
	 * @author torpill40
	 *
	 */

	public enum ArgumentType {

		NONE("none", "Aucun argument, si un argument est présent, il ne sera pas pris en compte.", "arg1 arg2 arg3 -> aucun argument gardé."), RAW("raw", "Arguments classiques, séparés par des espaces.", "arg1 arg2 arg3 -> ARG1 = arg1, ARG2 = arg2, ARG3 = arg3."), QUOTE("quote", "Arguments guillemets, les arguments sont écrits entre \"\", tous les arguments qui ne sont pas entre guillemets sont séparés par des espaces. Pour afficher un guillemet, il faut l'écrire \\\\\".", "arg1 \"arg2 \\\\\"arg3\\\\\"\" -> ARG1 = arg1, ARG2 = arg2 \"arg3\"."), KEY("key", "Arguments clé / valeur, séparés en groupe. Un nom de groupe commence par --. Si un argument est placé avant le premier groupe, celui-ci est compté dans le groupe par défaut.", "arg1 arg2 --group1 arg3 --group2 arg4 arg5 -> DEFAULT = arg1 arg2, group1 = arg3, group2 = arg4 arg5.");

		public final String NAME;
		public final String DESCRIPTION;
		public final String EXAMPLE;

		/**
		 *
		 * Constructeur de la classe énumératrice <code>ArgumentType</code>.
		 *
		 * @param name
		 *            : nom du type d'argument.
		 * @param description
		 *            : description de l'utilisation du type d'argument.
		 * @param example
		 *            : exemple d'utilisation du type d'argument.
		 */
		private ArgumentType(final String name, final String description, final String example) {

			this.NAME = name;
			this.DESCRIPTION = description;
			this.EXAMPLE = example;
		}

		/**
		 *
		 * Convertir les arguments au format du type d'argument.
		 *
		 * @param args
		 *            : arguments en entrée.
		 * @return arguments convertis
		 */
		public String[] parseArguments(final String[] args) {

			switch (this) {

			case KEY:
				return this.keyParse(args);

			case NONE:
				return this.noneParse(args);

			case QUOTE:
				return this.quoteParse(args);

			case RAW:
				return this.rawParse(args);
			}

			return args;
		}

		/**
		 *
		 * Convertir les arguments au format nul.
		 *
		 * @param args
		 *            : arguments en entrée.
		 * @return arguments convertis
		 */
		private String[] noneParse(final String args[]) {

			final String[] none = {
					args[0]
			};

			return none;
		}

		/**
		 *
		 * Convertir les arguments au format classique.
		 *
		 * @param args
		 *            : arguments en entrée.
		 * @return arguments convertis
		 */
		private String[] rawParse(final String args[]) {

			final String[] raw = new String[args.length - 1];
			for (int i = 0; i < raw.length; i++) {

				raw[i] = args[i + 1];
			}

			return raw;
		}

		/**
		 *
		 * Convertir les arguments au format guillemet.
		 *
		 * @param args
		 *            : arguments en entrée.
		 * @return arguments convertis
		 */
		private String[] quoteParse(final String args[]) {

			final StringBuilder builder = new StringBuilder();
			for (int i = 1; i < args.length; i++) {

				builder.append(args[i] + (i == args.length - 1 ? "" : " "));
			}
			final String quoted = builder.toString();
			final List<StringBuilder> builders = new ArrayList<>();
			final char[] chrs = quoted.toCharArray();
			for (int j = 0, i = 0; j < chrs.length; j++) {

				final char chr = chrs[j];
				final char prev = j == 0 ? ' ' : chrs[j - 1];
				final char beforePrev = j <= 1 ? ' ' : chrs[j - 2];
				final char next = j == chrs.length - 1 ? ' ' : chrs[j + 1];
				final boolean in = i % 2 != 0;

				if (prev != '\\' && chr == '\"') {

					if (i++ % 2 == 0) {

						builders.add(0, new StringBuilder());
					}

				} else {

					if (!in && (beforePrev != '\\' && prev == '\"' || chr == ' ') && next != ' ' && next != '\"') {

						builders.add(0, new StringBuilder());
					}

					if (!(chr == '\\' || !in && chr == ' ')) {

						builders.get(0).append(chr);
					}
				}
			}

			final String[] quote = new String[builders.size()];
			for (int i = 0; i < quote.length; i++) {

				quote[i] = builders.get(builders.size() - 1 - i).toString();
			}

			return quote;
		}

		/**
		 *
		 * Convertir les arguments au format clé / valeur.
		 *
		 * @param args
		 *            : arguments en entrée.
		 * @return arguments convertis
		 */
		private String[] keyParse(final String args[]) {

			final Map<String, StringBuilder> builders = new HashMap<>();
			String currentKey = "%";
			builders.put(currentKey, new StringBuilder());
			for (final String arg : this.rawParse(args)) {

				if (arg.startsWith("--")) {

					builders.put(currentKey = arg.substring(2, arg.length()), new StringBuilder());

				} else {

					builders.get(currentKey).append(arg + " ");
				}
			}

			final String[] key = new String[builders.size() * 2];
			int i = 0;
			for (final String builderKey : builders.keySet()) {

				key[i++] = builderKey;
				key[i++] = builders.get(builderKey).toString();
			}

			return key;
		}
	}

	/**
	 *
	 * Cette classe énumératrice représente les différentes catégories de commande.
	 *
	 * @author torpill40
	 *
	 */

	public enum Category {

		UTILITY("utilitaire", "Commandes utiles aux membres du serveur"), GAME("jeu", "Commandes permettant de jouer à un jeu"), MODERATION("modération", "Commandes permettant de modérer un serveur"), FUN("fun", "Commandes qui permettent de s'amuser un peu"), TUTORIAL("tutoriel", "Commandes issues du tutoriel");

		public final String NAME;
		public final String DESCRIPTION;

		/**
		 *
		 * Constructeur de la classe énumératrice <code>Category</code>.
		 *
		 * @param name
		 *            : nom de la catégorie.
		 * @param description
		 *            : description de la catégorie.
		 */
		private Category(final String name, final String description) {

			this.NAME = name;
			this.DESCRIPTION = description;
		}
	}
}
