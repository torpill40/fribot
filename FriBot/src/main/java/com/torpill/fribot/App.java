package com.torpill.fribot;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.torpill.fribot.bot.DiscordBotBuilder;
import com.torpill.fribot.commands.fun.DogCommand;
import com.torpill.fribot.commands.fun.HelloCommand;
import com.torpill.fribot.commands.tutorial.TutorialTorpill40Command;
import com.torpill.fribot.listeners.NitroListener;

/**
 *
 * Un bot Discord collaboratif pour le serveur Fripouz Team. Pour plus
 * d'informations, allez voir le
 * <a href="https://github.com/torpill40/fribot">Dépot Github</a>.
 *
 * @author torpill40
 * @version RELEASE-1.0
 *
 */

public class App {

	public static final String APP_NAME = "FriBot";
	public static final String APP_ID = "fribot";
	public static final Logger LOGGER = LogManager.getLogger(APP_NAME);
	public static final String VERSION = "RELEASE-1.0";
	public static final String GITHUB = "https://github.com/torpill40/fribot";

	/**
	 *
	 * Point d'entrée du programme, démarrage du bot.
	 *
	 * @param args
	 *            <br />
	 *            <b>--token &lt;TOKEN&gt;</b> : token du bot. <br />
	 *            <b>--prefix &lt;PREFIX&gt;</b> : préfix du bot. <br />
	 *            <b>--color &lt;R&gt; &lt;G&gt; &lt;B&gt;</b> : couleurs des embeds
	 *            par défaut. <br />
	 *            <b>--role &lt;ROLE_ID&gt;</b> : rôle utilisateur par défaut.
	 *            <br />
	 *            <b>--devrole &lt;ROLE_ID&gt;</b> : rôle développeur par défaut.
	 *            <br />
	 */

	public static void main(final String[] args) {

		final DiscordBotBuilder botBuilder = new DiscordBotBuilder();

		for (int i = 0; i < args.length; i++) {

			switch (args[i]) {
			case "--token":
				botBuilder.setToken(args[i + 1]);
				break;
			case "--prefix":
				botBuilder.setPrefix(args[i + 1]);
				break;
			case "--color":
				botBuilder.setColor(new Color(Integer.parseInt(args[i + 1]), Integer.parseInt(args[i + 2]), Integer.parseInt(args[i + 3])));
				break;
			case "--role":
				botBuilder.setRole(args[i + 1]);
				break;
			case "--devrole":
				botBuilder.setDevRole(args[i + 1]);
				break;
			}
		}

		try {

			// @formatter:off

			botBuilder.addCommand(HelloCommand.class)
				.addCommand(TutorialTorpill40Command.class)
				.addCommand(DogCommand.class)
				.addListener(NitroListener.class)
				.build();

			// @formatter:on

			LOGGER.info("FriBot est prêt !");

		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {

			e.printStackTrace();
		}
	}
}
