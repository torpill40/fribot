package com.torpill.fribot;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.torpill.fribot.bot.DiscordBotBuilder;
import com.torpill.fribot.commands.HelloCommand;

/**
 * 
 * Un bot Discord collaboratif pour le serveur Fripouz Team. Pour plus
 * d'informations, allez voir le
 * <a href="https://github.com/torpill40/fribot">Dépot Github</a>.
 * 
 * @author torpill40
 * @version BETA-1.0.1
 *
 */

public class App {

	public static final Logger LOGGER = LogManager.getLogger("FriBot");
	public static final String VERSION = "BETA-1.0.1";
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
	 */

	public static void main(String[] args) {

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
			}
		}

		try {

			// @formatter:off
			
			botBuilder.addCommand(HelloCommand.class)
				.build();
			
			// @formatter:on

			LOGGER.info("FriBot est prêt !");

		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {

			e.printStackTrace();
		}
	}
}
