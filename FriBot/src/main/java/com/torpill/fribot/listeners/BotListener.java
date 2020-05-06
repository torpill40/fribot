package com.torpill.fribot.listeners;

import org.javacord.api.listener.GloballyAttachableListener;

import com.torpill.fribot.bot.DiscordBot;

/**
 * 
 * Cette classe représente un écouteur attachable au bot.
 * 
 * @author torpill40
 * 
 * @see org.javacord.api.listener.GloballyAttachableListener
 *
 */

public abstract class BotListener implements GloballyAttachableListener {

	protected final DiscordBot bot;

	/**
	 * 
	 * Constructeur de la classe <code>BotListener</code>.
	 * 
	 * @param bot
	 *            : bot Discord communiquant avec l'écouteur.
	 * 
	 * @see com.torpill.fribot.bot.DiscordBot
	 */
	public BotListener(DiscordBot bot) {

		this.bot = bot;
	}
}
