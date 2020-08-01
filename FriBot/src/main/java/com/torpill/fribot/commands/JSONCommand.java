package com.torpill.fribot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.json.JSONArray;
import org.json.JSONObject;

import com.torpill.fribot.App;
import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.util.JSON;
import com.torpill.fribot.util.StringProcessor;

/**
 *
 * Cette classe représente une commande JSON.
 *
 * @author torpill40
 *
 * @see com.torpill.fribot.commands.Command
 *
 */

public class JSONCommand extends Command {

	private final String help;
	private final String example;
	private final boolean deleteCommandUsage;
	private final List<String> permissionNeeded;
	private final List<String> whiteListedUsers;
	private final List<String> blackListedUsers;
	private final List<String> whiteListedRoles;
	private final List<String> blackListedRoles;
	private final List<JSONObject> messages;
	private final double interval;

	/**
	 *
	 * Constructeur de la classe <code>Command</code>.
	 *
	 * @param command
	 *            : commande au format JSON.
	 *
	 * @see org.json.JSONObject
	 */
	public JSONCommand(final JSONObject command) throws IllegalArgumentException {

		super(JSONCommand.stringFromJSON(command, "name"), JSONCommand.argTypeFromJSON(command), JSONCommand.categoryFromJSON(command));

		this.help = JSONCommand.stringFromJSON(command, "help");
		this.example = JSONCommand.stringFromJSON(command, "example", null);
		this.deleteCommandUsage = JSONCommand.booleanFromJSON(command, "deleteUsage", true);
		this.permissionNeeded = JSONCommand.listFromJSON(command, "permissionsNeeded", null);
		this.whiteListedUsers = JSONCommand.listFromJSON(command, "usersWhiteList", null);
		this.blackListedUsers = JSONCommand.listFromJSON(command, "usersBlackList", null);
		this.whiteListedRoles = JSONCommand.listFromJSON(command, "rolesWhiteList", null);
		this.blackListedRoles = JSONCommand.listFromJSON(command, "rolesBlackList", null);
		this.messages = JSONCommand.objectListFromJSON(command, "send");
		this.interval = JSONCommand.doubleFromJSON(command, "interval", 0.0);
	}

	private static String stringFromJSON(final JSONObject source, final String field) {

		if (!source.has(field) || source.isNull(field)) throw new NullPointerException();
		return JSON.getString(source, field);
	}

	private static String stringFromJSON(final JSONObject source, final String field, final String defaultVal) {

		try {

			return JSONCommand.stringFromJSON(source, field);

		} catch (final NullPointerException e) {

			return defaultVal;
		}
	}

	private static boolean booleanFromJSON(final JSONObject source, final String field) {

		if (!source.has(field) || source.isNull(field)) throw new NullPointerException();
		return JSON.getBoolean(source, field);
	}

	private static boolean booleanFromJSON(final JSONObject source, final String field, final boolean defaultVal) {

		try {

			return JSONCommand.booleanFromJSON(source, field);

		} catch (final NullPointerException e) {

			return defaultVal;
		}
	}

	private static double doubleFromJSON(final JSONObject source, final String field) {

		if (!source.has(field) || source.isNull(field)) throw new NullPointerException();
		return JSON.getDouble(source, field);
	}

	private static double doubleFromJSON(final JSONObject source, final String field, final double defaultVal) {

		try {

			return JSONCommand.doubleFromJSON(source, field);

		} catch (final NullPointerException e) {

			return defaultVal;
		}
	}

	private static Command.ArgumentType argTypeFromJSON(final JSONObject source) throws IllegalArgumentException {

		return Command.ArgumentType.valueOf(JSONCommand.stringFromJSON(source, "args").toUpperCase());
	}

	private static Command.Category categoryFromJSON(final JSONObject source) throws IllegalArgumentException {

		return Command.Category.valueOf(JSONCommand.stringFromJSON(source, "category").toUpperCase());
	}

	private static List<String> listFromJSON(final JSONObject source, final String field) {

		if (!source.has(field) || source.isNull(field)) throw new NullPointerException();
		final JSONArray array = JSON.getJSONArray(source, field);
		final List<String> list = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {

			list.add(JSON.getString(array, i));
		}
		return list;
	}

	private static List<String> listFromJSON(final JSONObject source, final String field, final List<String> defaultVal) {

		try {

			return JSONCommand.listFromJSON(source, field);

		} catch (final NullPointerException e) {

			return defaultVal;
		}
	}

	private static List<JSONObject> objectListFromJSON(final JSONObject source, final String field) {

		if (!source.has(field) || source.isNull(field)) throw new NullPointerException();
		final JSONArray array = JSON.getJSONArray(source, field);
		final List<JSONObject> list = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {

			list.add(JSON.getJSONObject(array, i));
		}
		return list;
	}

	private String format(final String text, final String prefix, final DiscordBot bot, final String[] args, final User user) {

		String res = text;
		String newRes = res;
		final Pattern brackets = Pattern.compile("(\\$\\[[^\\n\\r\\$]+\\]\\$)");
		final Pattern braces = Pattern.compile("(\\$\\{[^\\n\\r\\$]+\\}\\$)");
		final Pattern parentheses = Pattern.compile("(\\$\\([^\\n\\r\\$]+\\)\\$)");
		try {

			final Matcher bracketsMatcher = brackets.matcher(res);
			while (bracketsMatcher.find()) {

				final int offset = newRes.length() - res.length();
				final String start = newRes.substring(0, bracketsMatcher.start() + offset);
				final String par = newRes.substring(bracketsMatcher.start() + offset, bracketsMatcher.end() + offset);
				final String end = newRes.substring(bracketsMatcher.end() + offset);

				newRes = start + this.replaceParameter(par, prefix, bot, args, user) + end;
			}
			res = newRes;
			final Matcher bracesMatcher = braces.matcher(res);
			while (bracesMatcher.find()) {

				final int offset = newRes.length() - res.length();
				final String start = newRes.substring(0, bracesMatcher.start() + offset);
				final String par = newRes.substring(bracesMatcher.start() + offset, bracesMatcher.end() + offset);
				final String end = newRes.substring(bracesMatcher.end() + offset);

				newRes = start + this.replaceParameter(par, prefix, bot, args, user) + end;
			}
			res = newRes;
			final Matcher parenthesesMatcher = parentheses.matcher(res);
			while (parenthesesMatcher.find()) {

				final int offset = newRes.length() - res.length();
				final String start = newRes.substring(0, parenthesesMatcher.start() + offset);
				final String par = newRes.substring(parenthesesMatcher.start() + offset, parenthesesMatcher.end() + offset);
				final String end = newRes.substring(parenthesesMatcher.end() + offset);

				newRes = start + this.replaceParameter(par, prefix, bot, args, user) + end;
			}
			return newRes;

		} catch (final ScriptException | NullPointerException | NumberFormatException | AssertionError e) {

			return "```Une erreur est survenue : " + e.getMessage() + "```";
		}
	}

	private String replaceParameter(final String par, final String prefix, final DiscordBot bot, final String[] args, final User user) throws ScriptException {

		final String res = par.substring(2, par.length() - 2);
		final ScriptEngineManager manager = new ScriptEngineManager();
		final ScriptEngine engine = manager.getEngineByName("js");
		if (res.contains(":")) {

			final String defaultVal = res.contains("|") ? res.split("\\|")[1] : null;
			final String[] parts = res.split("\\|")[0].split(":");
			switch (parts[0]) {

			case "arg":
				if (this.getType() == Command.ArgumentType.RAW || this.getType() == Command.ArgumentType.QUOTE) {

					try {

						final String index = parts[1].replace("*", args.length - 1 + "");
						final int resultIndex = (int) engine.eval(index);
						if (parts.length == 2) return args[resultIndex];
						else {

							final String end = parts[2].replace("*", args.length - 1 + "");
							final int resultEnd = (int) engine.eval(end);
							return StringProcessor.join(args, resultIndex, resultEnd);
						}

					} catch (final IndexOutOfBoundsException e) {

						if (defaultVal == null) throw new NullPointerException();
						return defaultVal;
					}

				} else if (this.getType() == Command.ArgumentType.KEY) {

					final String key = parts[1];
					for (int i = 0; i < args.length; i += 2) {

						if (args[i].equals(key) && !args[i + 1].isEmpty()) return args[i + 1];
					}

					if (defaultVal == null) throw new NullPointerException();
					return defaultVal;

				} else throw new NullPointerException();

			case "eval":
				try {

					final Object calc = engine.eval(parts[1]);
					if (calc instanceof Integer) return (double) (int) calc + "";
					else if (calc instanceof Double) return (double) calc + "";
					else throw new NumberFormatException();

				} catch (final IndexOutOfBoundsException e) {

					if (defaultVal == null) throw new NullPointerException();
					return defaultVal;
				}
			}
		}
		switch (res) {

		case "bot.prefix":
			return prefix;

		case "cmd.name":
			return this.getName();

		case "user.mention":
			return user.getMentionTag();

		default:
			return par;
		}
	}

	@Override
	public String getHelp() {

		return this.help;
	}

	@Override
	public String getExample(final String prefix, final User user) {

		return this.example != null ? this.format(this.example, prefix, null, null, user) : super.getExample(prefix, user);
	}

	@Override
	public boolean deleteCommandUsage() {

		return this.deleteCommandUsage;
	}

	@Override
	public List<PermissionType> permissionNeeded() {

		final List<PermissionType> list = new ArrayList<>();
		if (this.permissionNeeded == null) return null;
		for (final String permission : this.permissionNeeded) {

			list.add(PermissionType.valueOf(permission));
		}
		return list;
	}

	@Override
	public List<User> whiteListedUsers(final DiscordBot bot, final Server server) {

		return bot.usersList(this.whiteListedUsers);
	}

	@Override
	public List<User> blackListedUsers(final DiscordBot bot, final Server server) {

		return bot.usersList(this.blackListedUsers);
	}

	@Override
	public List<Role> whiteListedRoles(final DiscordBot bot, final Server server) {

		return bot.rolesList(server, this.whiteListedRoles);
	}

	@Override
	public List<Role> blackListedRoles(final DiscordBot bot, final Server server) {

		return bot.rolesList(server, this.blackListedRoles);
	}

	@Override
	public int execute(final DiscordBot bot, final String[] args, final User user, final TextChannel channel, final Server server) {

		final double delay = this.interval < 0.0 ? 0.0 : this.interval;

		try {

			for (final JSONObject message : this.messages) {

				final String type = JSONCommand.stringFromJSON(message, "type", "classic");
				switch (type) {

				case "classic":
					final List<String> lines = JSONCommand.listFromJSON(message, "lines", null);
					if (lines == null) break;
					final MessageBuilder builder = new MessageBuilder();
					for (final String line : lines) {

						builder.append(this.format(line, bot.getPrefix(), bot, args, user));
					}
					builder.send(channel);
					break;
				}

				Thread.sleep((long) (delay * 1000L));
			}

		} catch (final InterruptedException e) {

			App.LOGGER.error("Une erreur est survenue :", e);
		}

		return 0;
	}
}
