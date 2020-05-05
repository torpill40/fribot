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

public abstract class Command {

	private final String name;
	private final ArgumentType argumentType;

	protected Command(final String name, final ArgumentType argumentType) {

		this.name = name;
		this.argumentType = argumentType;
	}
	
	public abstract String getHelp();
	
	public abstract boolean deleteCommandUsage();

	public abstract List<PermissionType> permissionNeeded();

	public abstract List<Role> whiteListedRoles(final DiscordBot bot, final Server server);

	public abstract List<Role> blackListedRoles(final DiscordBot bot, final Server server);

	public abstract List<User> whiteListedUsers(final DiscordBot bot, final Server server);

	public abstract List<User> blackListedUsers(final DiscordBot bot, final Server server);

	public abstract int execute(final DiscordBot bot, final String args[], final User user, final TextChannel channel,
			final Server server);

	public String[] parseArguments(final String[] args) {

		return this.argumentType.parseArguments(args);
	}

	public String getName() {

		return this.name;
	}
	
	public ArgumentType getType() {

		return this.argumentType;
	}

	public enum ArgumentType {

		NONE("none", "Aucun argument, si un argument est présent, il ne sera pas pris en compte.", "arg1 arg2 arg3 -> aucun argument gardé."),
		RAW("raw", "Arguments classiques, séparés par des espaces.", "arg1 arg2 arg3 -> ARG1 = arg1, ARG2 = arg2, ARG3 = arg3."),
		QUOTE("quote", "Arguments guillemets, les arguments sont écrits entre \"\", tous les arguments qui ne sont pas entre guillemets sont séparés par des espaces. Pour afficher un guillemet, il faut l'écrire \\\\\".", "arg1 \"arg2 \\\\\"arg3\\\\\"\" -> ARG1 = arg1, ARG2 = arg2 \"arg3\"."),
		KEY("key", "Arguments clé / valeur, séparés en groupe. Un nom de groupe commence par --. Si un argument est placé avant le premier groupe, celui-ci est compté dans le groupe par défaut.", "arg1 arg2 --group1 arg3 --group2 arg4 arg5 -> DEFAULT = arg1 arg2, group1 = arg3, group2 = arg4 arg5.");

		public final String NAME;
		public final String DESCRIPTION;
		public final String EXAMPLE;
		
		private ArgumentType(final String name, final String description, final String example) {

			this.NAME = name;
			this.DESCRIPTION = description;
			this.EXAMPLE = example;
		}

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

		private String[] noneParse(final String args[]) {

			String[] none = { args[0] };

			return none;
		}

		private String[] rawParse(final String args[]) {

			String[] raw = new String[args.length - 1];
			for (int i = 0; i < raw.length; i++) {

				raw[i] = args[i + 1];
			}

			return raw;
		}

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
				final char prev = (j == 0 ? ' ' : chrs[j - 1]);
				final char beforePrev = (j <= 1 ? ' ' : chrs[j - 2]);
				final char next = (j == chrs.length - 1 ? ' ' : chrs[j + 1]);
				final boolean in = i % 2 != 0;

				if (prev != '\\' && chr == '\"') {

					if (i++ % 2 == 0) {

						builders.add(0, new StringBuilder());
					}

				} else {

					if (!in && ((beforePrev != '\\' && prev == '\"') || chr == ' ') && next != ' ' && next != '\"') {

						builders.add(0, new StringBuilder());
					}

					if (!(chr == '\\' || (!in && chr == ' '))) {

						builders.get(0).append(chr);
					}
				}
			}

			String[] quote = new String[builders.size()];
			for (int i = 0; i < quote.length; i++) {

				quote[i] = builders.get(builders.size() - 1 - i).toString();
			}

			return quote;
		}

		private String[] keyParse(final String args[]) {

			final Map<String, StringBuilder> builders = new HashMap<>();
			String currentKey = "%";
			builders.put(currentKey, new StringBuilder());
			for (String arg : this.rawParse(args)) {

				if (arg.startsWith("--")) {

					builders.put(currentKey = arg.substring(2, arg.length()), new StringBuilder());

				} else {

					builders.get(currentKey).append(arg + " ");
				}
			}

			final String[] key = new String[builders.size() * 2];
			int i = 0;
			for (String builderKey : builders.keySet()) {

				key[i++] = builderKey;
				key[i++] = builders.get(builderKey).toString();
			}

			return key;
		}
	}
}
