package com.torpill.fribot.commands.fun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;
import com.torpill.fribot.util.StringProcessor;

/**
 *
 * Ecrire une prophétie avec des runes.
 *
 * @author torpill40
 *
 * @see com.torpill.fribot.commands.Command
 *
 */
public class ProphecyCommand extends Command {

	private final Map<String, String> futhark = new HashMap<>();

	/**
	 *
	 * Constructeur de la classe <code>ProphecyCommand</code>.
	 *
	 */
	public ProphecyCommand() {

		super("prophecy", Command.ArgumentType.RAW, Command.Category.FUN);

		this.futhark.put("th", "\u16A6");
		this.futhark.put("ng", "\u16DC");
		this.futhark.put("a", "\u16AB");
		this.futhark.put("b", "\u16D2");
		this.futhark.put("c", "\u16B2");
		this.futhark.put("d", "\u16DE");
		this.futhark.put("e", "\u16D6");
		this.futhark.put("f", "\u16A0");
		this.futhark.put("g", "\u16B7");
		this.futhark.put("h", "\u16BA");
		this.futhark.put("i", "\u16C1");
		this.futhark.put("j", "\u16C3");
		this.futhark.put("k", "\u16B2");
		this.futhark.put("l", "\u16DA");
		this.futhark.put("m", "\u16D7");
		this.futhark.put("n", "\u16BE");
		this.futhark.put("o", "\u16DF");
		this.futhark.put("p", "\u16C8");
		this.futhark.put("q", "\u16B2");
		this.futhark.put("r", "\u16B1");
		this.futhark.put("s", "\u16CA");
		this.futhark.put("t", "\u16CF");
		this.futhark.put("u", "\u16A2");
		this.futhark.put("v", "\u16B9");
		this.futhark.put("w", "\u16B9");
		this.futhark.put("x", "\u16B2\u16CA");
		this.futhark.put("y", "\u16C7");
		this.futhark.put("z", "\u16C9");
	}

	@Override
	public String getHelp() {

		return "Ecrire une prophétie avec des runes.";
	}

	@Override
	public String getExample(final String prefix, final User user) {

		return prefix + this.getName() + " L'enfant se lèvera et mangera son bol de céréales";
	}

	@Override
	public boolean deleteCommandUsage() {

		return true;
	}

	@Override
	public List<Role> whiteListedRoles(final DiscordBot bot, final Server server) {

		return bot.roles(server, "user-role");
	}

	@Override
	public int execute(final DiscordBot bot, final String[] args, final User user, final TextChannel channel, final Server server) {

		if (args.length == 0) return 1;

		final StringBuilder builder = new StringBuilder();
		for (final String arg : args) builder.append(StringProcessor.removeAccent(arg.toLowerCase()) + " ");

		String msg = builder.toString().trim();
		for (final String key : this.futhark.keySet()) msg = msg.replace(key, this.futhark.get(key));
		for (int i = 0; i < msg.length(); i++) {

			final char chr = msg.charAt(i);
			if (chr < 0x16A0 || chr > 0x16F0) msg = msg.replaceFirst(chr + "", " ");
		}

		//@formatter:off

		new MessageBuilder()
			.append("Vous trouvez une pierre. Vous pouvez y lire ceci :")
			.append(msg, MessageDecoration.CODE_LONG)
			.append("Cela ressemble fortement à une prophétie.")
			.send(channel);

		//@formatter:on

		return 0;
	}
}
