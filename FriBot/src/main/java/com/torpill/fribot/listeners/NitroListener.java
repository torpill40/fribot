package com.torpill.fribot.listeners;

import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.role.UserRoleAddEvent;
import org.javacord.api.listener.server.role.UserRoleAddListener;

import com.torpill.fribot.bot.DiscordBot;

public class NitroListener extends BotListener implements UserRoleAddListener {

	final private String NITRO = "619256360327970821";

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

	private boolean isNitro(final String id) {

		return this.NITRO.equals(id);
	}
}
