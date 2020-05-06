package com.torpill.fribot.threads;

import java.util.List;

import com.torpill.fribot.App;
import com.torpill.fribot.bot.DiscordBot;

public abstract class BotThread implements Runnable, Cloneable {

	protected final DiscordBot bot;
	private final String name;
	protected Object[] args = null;

	protected BotThread(final DiscordBot bot, final String name) {

		this.bot = bot;
		this.name = name;
	}

	@Override
	public void run() {

		this.args = null;
	}
	
	@Override
	public BotThread clone() throws CloneNotSupportedException {
	
		return (BotThread) super.clone();
	}

	protected abstract List<? extends Class<?>> args();

	private int checkArgs(Object... args) {

		final List<?> needed = this.args();
		if (args.length != needed.size()) {

			App.LOGGER.debug("'" + this.getName() + "' " + args.length + " arguments au lieu de " + needed.size());
			return 1;
		}

		for (int i = 0; i < args.length; i++) {

			boolean isSame = args[i].getClass() == needed.get(i);
			boolean isAssignable = ((Class<?>) needed.get(i)).isAssignableFrom(args[i].getClass());
			if (!isSame && !isAssignable) {
				
				App.LOGGER.debug("'" + this.getName() + "' '" + args[i].getClass() + "' ne correspond pas au paramètre attendu '" + needed.get(i) + "'");
				return 2;
			}
		}
		
		return 0;
	}

	public int setArgs(Object... args) {

		final int response = this.checkArgs(args);

		if (response == 0) {

			App.LOGGER.debug("'" + this.getName() + "' paramètre enregistré avec succès.");
			this.args = args;
		}

		return response;
	}

	public String getName() {

		return this.name;
	}
}
