package com.torpill.fribot.threads;

import java.util.List;

import com.torpill.fribot.App;
import com.torpill.fribot.bot.DiscordBot;

/**
 * 
 * Cette classe représente un thread relié a un bot Discord.
 * 
 * @author torpill40
 *
 */

public abstract class BotThread implements Runnable, Cloneable {

	protected final DiscordBot bot;
	private final String name;
	protected Object[] args = null;

	/**
	 * 
	 * Constructeur de la classe <code>BotThread</code>.
	 * 
	 * @param bot
	 *            : bot Discord relié au thread.
	 * @param name
	 *            : nom du thread.
	 * 
	 * @see com.torpill.fribot.bot.DiscordBot
	 */
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

	/**
	 * 
	 * Récupérer la liste des classes des arguments nécéssaires au fonctionnement du
	 * bot.
	 * 
	 * @return liste de classes
	 */
	protected abstract List<? extends Class<?>> args();

	/**
	 * 
	 * Vérifier si les arguments passés en paramètres sont valides pour le démarrage
	 * du thread.
	 * 
	 * @param args
	 *            : arguments à tester.
	 * @return code d'erreur
	 */
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

	/**
	 * 
	 * Attribuer les arguments au thread avant son exécution.
	 * 
	 * @param args
	 *            : arguments à attribuer.
	 * @return code d'erreur
	 */
	public int setArgs(Object... args) {

		final int response = this.checkArgs(args);

		if (response == 0) {

			App.LOGGER.debug("'" + this.getName() + "' paramètre enregistré avec succès.");
			this.args = args;
		}

		return response;
	}

	/**
	 * 
	 * Récupérer le nom du thread.
	 * 
	 * @return nom du thread
	 */
	public String getName() {

		return this.name;
	}
}
