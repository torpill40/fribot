package com.torpill.fribot.commands.tutorial;

import java.util.List;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;

/**
 * 
 * Cette classe représente la commande du tutoriel débutant créée par torpill40.
 * 
 * @author torpill40
 * 
 * @see com.torpill.fribot.commands.Command
 *
 */

public class TutorialTorpill40Command extends Command {

	/**
	 * 
	 * Constructeur de la classe <code>TutorialTorpill40Command</code>.
	 * 
	 */
	public TutorialTorpill40Command() {

		super("tuto-torpill40", Command.ArgumentType.NONE, Command.Category.TUTORIAL);
	}

	@Override
	public String getHelp() {

		return "Commande du tutoriel débutant créée par torpill40.\nCette commande ne peut être utilisée qu'uniquement par torpill40.";
	}

	@Override
	public boolean deleteCommandUsage() {

		return true;
	}

	@Override
	public List<User> whiteListedUsers(DiscordBot bot, Server server) {

		return bot.users("392722513202774016");
	}

	@Override
	public int execute(DiscordBot bot, String[] args, User user, TextChannel channel, Server server) {

		Role role = bot.getDevRole(server);

		if (role != null) {

			channel.sendMessage("Félicitations " + user.getMentionTag() + " ! Tu as terminé le tutoriel du débutant ! Tu reçois donc ton rôle " + role.getMentionTag() + " !");
			server.addRoleToUser(user, role);
		}

		return 0;
	}
}
