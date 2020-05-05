package com.torpill.fribot;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.torpill.fribot.bot.DiscordBotBuilder;
import com.torpill.fribot.commands.HelloCommand;
import com.torpill.fribot.threads.CommandThread;
import com.torpill.fribot.threads.HelpThread;

public class App {

	public static final Logger LOGGER = LogManager.getLogger("FriBot");

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
