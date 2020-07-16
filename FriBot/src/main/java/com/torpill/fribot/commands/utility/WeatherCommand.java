package com.torpill.fribot.commands.utility;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.App;
import com.torpill.fribot.api.APIException;
import com.torpill.fribot.api.weather.WeatherCity;
import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;
import com.torpill.fribot.threads.WeatherThread;

/**
 *
 * Recherchez le beau temps dans le monde entier !
 *
 * @author torpill40
 *
 * @see com.torpill.fribot.command.Command
 *
 */

public class WeatherCommand extends Command {

	/**
	 *
	 * Constructeur de la classe <code>WeatherCommand</code>.
	 *
	 */
	public WeatherCommand() {

		super("weather", Command.ArgumentType.KEY, Command.Category.UTILITY);
	}

	@Override
	public String getHelp() {

		return "Recherchez le beau temps dans le monde entier !\nPassez la ville que vous souhaitez en argument (toutes les villes ne sont pas disponibles).\nVous pouvez spécifier les prévisions avec l'argument clé 'query' suivi d'un ou plusieurs élements, séparés par des espaces de cette liste :\n - temp\n - humidity\n - pressure\n - clouds\n - wind\n - rain\n - snow\n - all";
	}

	@Override
	public String getExample(final String prefix, final User user) {

		return prefix + this.getName() + " Paris\n" + prefix + this.getName() + " Berlin --query pressure wind\n" + prefix + this.getName() + " --query all --city New York";
	}

	@Override
	public boolean deleteCommandUsage() {

		return true;
	}

	@Override
	public int execute(final DiscordBot bot, final String[] args, final User user, final TextChannel channel, final Server server) {

		String cityName = null;
		boolean temp = true, humidity = true, pressure = false, clouds = false, wind = false, rain = true, snow = false;

		for (int i = 0; i < args.length; i += 2) {

			switch (args[i]) {

			case "%":
			case "city":
				if (!args[i + 1].equals("")) {

					cityName = args[i + 1];
				}
				break;

			case "query":

				temp = false;
				humidity = false;
				rain = false;
				for (final String arg : args[i + 1].split(" ")) {

					switch (arg) {

					case "temp":
						temp = true;
						break;

					case "humidity":
						humidity = true;
						break;

					case "pressure":
						pressure = true;
						break;

					case "clouds":
						clouds = true;
						break;

					case "wind":
						wind = true;
						break;

					case "rain":
						rain = true;
						break;

					case "snow":
						snow = true;
						break;

					case "all":
						temp = true;
						humidity = true;
						pressure = true;
						clouds = true;
						wind = true;
						rain = true;
						snow = true;
						break;
					}
				}
				break;
			}
		}

		if (cityName == null) {

			channel.sendMessage(user.getMentionTag() + ", veuillez renseigner une ville : `" + bot.getPrefix() + this.getName() + " Paris`.");
			return 2;
		}

		try {

			final WeatherCity weatherCity = App.WEATHER.getForecast(cityName);

			if (weatherCity == null) {

				channel.sendMessage(user.getMentionTag() + ", `" + cityName.trim() + "` n'est pas une ville disponible.");
				return 2;
			}

			bot.startThread(WeatherThread.class, user, channel, weatherCity, new boolean[] {
					temp, humidity, pressure, clouds, wind, rain, snow
			});

		} catch (final APIException e) {

			channel.sendMessage("```Une erreur est survenue : " + e.getMessage() + "```");
		}

		return 0;
	}
}
