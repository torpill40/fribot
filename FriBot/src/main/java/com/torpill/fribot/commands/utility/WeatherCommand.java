package com.torpill.fribot.commands.utility;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.App;
import com.torpill.fribot.api.weather.Weather;
import com.torpill.fribot.api.weather.WeatherCity;
import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;

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
				cityName = args[i + 1];
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

		final WeatherCity weatherCity = App.WEATHER.getForecast(cityName);
		if (weatherCity == null) {

			channel.sendMessage(user.getMentionTag() + " veuillez cibler une ville valide.");
			return 2;
		}

		final Weather weather = weatherCity.getWeather(0);
		final EmbedBuilder embed = bot.defaultEmbedBuilder("Météo :", "Prévisons pour " + weatherCity.getCityName() + " (" + weatherCity.getCityCountry() + ") (" + weather.getDate() + ")", user);
		if (temp) embed.addField("Température :", weather.getTemp() + "°C", true);
		if (humidity) embed.addField("Humidité :", weather.getHumidity() + "%", true);
		if (pressure) embed.addField("Pression atmosphérique :", weather.getPressure() + "hPa", true);
		if (clouds) embed.addField("Nuages :", weather.getClouds() + "%", true);
		if (wind) embed.addField("Vent :", weather.getWindSpeed() + "m/s, " + weather.getWindDir() + "°", true);
		if (rain) embed.addField("Pluie :", weather.getRain() + "mm", true);
		if (snow) embed.addField("Neige :", weather.getSnow() + "mm", true);
		channel.sendMessage(embed);

		return 0;
	}
}
