package com.torpill.fribot.commands.fun;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;

/**
 * 
 * Cette classe représente une commande basique disant bonjour à l'utilisateur.
 * 
 * @author torpill40
 * 
 * @see com.torpill.fribot.commands.Command
 *
 */

public class HelloCommand extends Command {

	/**
	 * 
	 * Constructeur de la classe <code>HelloCommand</code>.
	 * 
	 */
	public HelloCommand() {

		super("hello", Command.ArgumentType.NONE, Command.Category.FUN);
	}

	@Override
	public String getHelp() {

		return "Dit \"Hello\" à l'auteur du message.";
	}

	@Override
	public boolean deleteCommandUsage() {

		return true;
	}

	@Override
	public int execute(final DiscordBot bot, final String[] args, final User user, final TextChannel channel, final Server server) {

		channel.sendMessage("Hello " + user.getMentionTag());

		return 0;
	}
}
