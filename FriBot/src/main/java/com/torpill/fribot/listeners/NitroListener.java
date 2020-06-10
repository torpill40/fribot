package com.torpill.fribot.listeners;

import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.role.UserRoleAddEvent;
import org.javacord.api.listener.server.role.UserRoleAddListener;

import com.torpill.fribot.bot.DiscordBot;

/**
 *
 * Cete classe représente un écouteur dédié au nitro.
 *
 * @author torpill40
 *
 * @see org.javacord.api.listener.server.role.UserRoleAddListener
 * @see com.torpill.fribot.listeners.BotListener
 *
 */

public class NitroListener extends BotListener implements UserRoleAddListener {

	final private String NITRO = "619256360327970821";

	/**
	 *
	 * Constructeur de la classe <code>NitroListener</code>.
	 *
	 * @param bot
	 *            : bot Discord communiquant avec l'écouteur.
	 */
	public NitroListener(final DiscordBot bot) {

		super(bot);
	}

	@Override
	public void onUserRoleAdd(final UserRoleAddEvent event) {

		final String roleId = event.getRole().getIdAsString();

		if (this.isNitro(roleId)) {

			final User user = event.getUser();
			final Server server = event.getServer();

			this.bot.addRoleToUser(user, server, "643095823525085194");
		}
	}

	/**
	 *
	 * Savoir si l'ID d'un rôle correspond à celui du rôle nitro.
	 *
	 * @param id
	 *            : ID du rôle à tester
	 * @return booléen
	 */
	private boolean isNitro(final String id) {

		return this.NITRO.equals(id);
	}
}
