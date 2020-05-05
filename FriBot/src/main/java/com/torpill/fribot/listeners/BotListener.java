package com.torpill.fribot.listeners;

import org.javacord.api.listener.GloballyAttachableListener;

import com.torpill.fribot.bot.DiscordBot;

public abstract class BotListener implements GloballyAttachableListener {

	protected final DiscordBot bot;
	
	public BotListener(DiscordBot bot) {
		
		this.bot = bot;
	}
}
