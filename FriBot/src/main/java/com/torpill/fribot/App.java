package com.torpill.fribot;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.torpill.fribot.api.weather.WeatherAPI;
import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.bot.DiscordBotBuilder;
import com.torpill.fribot.commands.fun.ByeBroCommand;
import com.torpill.fribot.commands.fun.ClydeCommand;
import com.torpill.fribot.commands.fun.ComputerCommand;
import com.torpill.fribot.commands.fun.DogCommand;
import com.torpill.fribot.commands.fun.ProphecyCommand;
import com.torpill.fribot.commands.fun.TVCommand;
import com.torpill.fribot.commands.game.RightPriceCommand;
import com.torpill.fribot.commands.tutorial.TutorialTorpill40Command;
import com.torpill.fribot.commands.utility.WeatherCommand;
import com.torpill.fribot.listeners.NitroListener;
import com.torpill.fribot.threads.RightPriceThread;
import com.torpill.fribot.threads.WeatherThread;
import com.torpill.fribot.util.FileUtils;
import com.torpill.fribot.util.JSON;
import com.torpill.fribot.util.TempFileManager;

/**
 *
 * Un bot Discord collaboratif pour le serveur Fripouz Team. Pour plus
 * d'informations, allez voir le
 * <a href="https://github.com/torpill40/fribot">Dépot Github</a>.
 *
 * @author torpill40
 * @version RELEASE-1.1
 *
 */

public class App {

	private static String CONFIG_DIR = "config/";
	private static String CONFIG_PATH = App.CONFIG_DIR + "config.json";
	private static JSONObject CONFIG;

	public static String SRC = "./src/";
	public static String FFMPEG = "/usr/bin";
	public static boolean TEST = false;

	public static final String APP_NAME = "FriBot";
	public static final String APP_ID = "fribot";
	public static final Logger LOGGER = LogManager.getLogger(APP_NAME);
	public static final String VERSION = "RELEASE-1.3";
	public static final String GITHUB = "https://github.com/torpill40/fribot";
	public static final WeatherAPI WEATHER = new WeatherAPI();
	public static final TempFileManager TEMP = new TempFileManager();

	private static DiscordBot BOT;

	static {

		App.CONFIG = App.readConfig();
		if (App.CONFIG == null) {

			App.LOGGER.warn("Le fichier de configuration est vide, FriBot ne peut pas démarrer.");
			App.createConfig();
			System.exit(0);
		}
	}

	private static void createConfig() {

		try {

			new File(CONFIG_DIR).mkdirs();
			new File(CONFIG_PATH).createNewFile();

		} catch (final IOException e) {

			App.LOGGER.fatal("Impossible de créer le fichier de configuration : ", e);
			System.exit(-1);
		}
	}

	private static JSONObject readConfig() {

		return FileUtils.readJSONFile(CONFIG_PATH);
	}

	/**
	 *
	 * Point d'entrée du programme, démarrage du bot.
	 *
	 */

	public static void main(final String[] args) {

		final DiscordBotBuilder botBuilder = new DiscordBotBuilder();
		App.CONFIG.keySet().forEach(key -> {

			switch (key) {

			case "token":
				botBuilder.setToken(JSON.getString(App.CONFIG, key));
				break;

			case "prefix":
				botBuilder.setPrefix(JSON.getString(App.CONFIG, key));
				break;

			case "color":
				botBuilder.setColor(new Color(JSON.getInt(App.CONFIG, key)));
				break;

			case "role":
				botBuilder.setRole(JSON.getString(App.CONFIG, key));
				break;

			case "devrole":
				botBuilder.setDevRole(JSON.getString(App.CONFIG, key));
				break;

			case "src":
				App.SRC = JSON.getString(App.CONFIG, key);
				break;

			case "ffmpeg":
				App.FFMPEG = JSON.getString(App.CONFIG, key);
				break;

			case "weather":
				App.WEATHER.setAppID(JSON.getString(App.CONFIG, key));
				break;

			case "test":
				App.TEST = JSON.getBoolean(App.CONFIG, key);
				break;
			}
		});

		try {

			// @formatter:off

			App.BOT = botBuilder.addCommand(TutorialTorpill40Command.class)
				.addCommand(DogCommand.class)
				.addCommand(ClydeCommand.class)
				.addCommand(ComputerCommand.class)
				.addCommand(TVCommand.class)
				.addCommand(ProphecyCommand.class)
				.addCommand(ByeBroCommand.class)
				.addCommand(WeatherCommand.class)
				.addCommand(RightPriceCommand.class)
				.addListener(NitroListener.class)
				.addThread(WeatherThread.class)
				.addThread(RightPriceThread.class)
				.build();

			// @formatter:on

			App.LOGGER.info("FriBot est prêt !");

		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {

			App.LOGGER.fatal("Une erreur est survenue : ", e);
		}
	}

	/**
	 *
	 * Retourne le bot de l'application.
	 *
	 * @return DiscordBot
	 *
	 * @see com.torpill.fribot.bot.DiscordBot
	 */
	public static DiscordBot getBot() {

		return App.BOT;
	}
}
