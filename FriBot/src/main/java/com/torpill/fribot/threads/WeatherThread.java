package com.torpill.fribot.threads;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

import com.torpill.fribot.App;
import com.torpill.fribot.api.weather.Weather;
import com.torpill.fribot.api.weather.WeatherCity;
import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.util.ListBuilder;
import com.vdurmont.emoji.EmojiParser;

/**
 *
 * Cette classe représente un thread permettant la gestion de l'utilitaire de
 * prévisions météorologiques.
 *
 * @author torpill40
 *
 * @see com.torpill.fribot.threads.BotThread
 *
 */

public class WeatherThread extends BotThread {

	private int page, numberOfPage;

	/**
	 *
	 * Constructeur de la classe <code>WeatherThread</code>.
	 *
	 * @param bot
	 *            : bot Discord relié au thread.
	 *
	 * @see com.torpill.fribot.bot.DiscordBot
	 */
	public WeatherThread(final DiscordBot bot) {

		super(bot, "weather");
	}

	@Override
	protected List<? extends Class<?>> args() {

		return ListBuilder.listOf(User.class, TextChannel.class, WeatherCity.class, boolean[].class);
	}

	@Override
	public void run() {

		App.LOGGER.debug("'" + Thread.currentThread().getName() + "' démarré avec succès.");

		final User user = (User) this.args[0];
		final TextChannel channel = (TextChannel) this.args[1];
		final WeatherCity weatherCity = (WeatherCity) this.args[2];
		final boolean[] queries = (boolean[]) this.args[3];

		App.LOGGER.debug("'" + Thread.currentThread().getName() + "' paramètres récupérés avec succès.");

		this.page = 1;
		this.numberOfPage = weatherCity.getWeathers().size();

		final boolean temp = queries[0];
		final boolean humidity = queries[1];
		final boolean pressure = queries[2];
		final boolean clouds = queries[3];
		final boolean wind = queries[4];
		final boolean rain = queries[5];
		final boolean snow = queries[6];

		final Weather weather = weatherCity.getWeather(this.page - 1);
		final String description = weather.getDescription().replaceFirst(".", (weather.getDescription().charAt(0) + "").toUpperCase());
		final EmbedBuilder embed = this.bot.defaultEmbedBuilder("Météo :", "Prévisons pour " + weatherCity.getCityName() + " (" + weatherCity.getCityCountry() + ") (" + weather.getDate() + ")", null);
		embed.setFooter("Page : " + this.page + " / " + this.numberOfPage, user.getAvatar());
		embed.addField("Description :", description);
		if (temp) embed.addField("Température :", weather.getTemp() + "°C", true);
		if (humidity) embed.addField("Humidité :", weather.getHumidity() + "%", true);
		if (pressure) embed.addField("Pression atmosphérique :", weather.getPressure() + "hPa", true);
		if (clouds) embed.addField("Nuages :", weather.getClouds() + "%", true);
		if (wind) embed.addField("Vent :", weather.getWindSpeed() + "m/s, " + weather.getWindDir() + "°", true);
		if (rain) embed.addField("Pluie :", weather.getRain() + "mm", true);
		if (snow) embed.addField("Neige :", weather.getSnow() + "mm", true);

		try {

			final Message message = channel.sendMessage(embed).get();
			message.addReaction(EmojiParser.parseToUnicode(":arrow_left:"));
			message.addReaction(EmojiParser.parseToUnicode(":arrow_right:"));
			final ReactionAddListener listener = event -> {

				final User user1 = event.getUser();
				final Optional<Reaction> optReaction = event.getReaction();
				if (!optReaction.isPresent()) return;
				final Reaction reaction = optReaction.get();
				final Emoji emoji = reaction.getEmoji();
				if (!WeatherThread.this.bot.is(user1)) {

					App.LOGGER.debug("Reaction '" + EmojiParser.parseToAliases(emoji.getMentionTag()) + "' added !");

					reaction.removeUser(user1);

					switch (EmojiParser.parseToAliases(emoji.getMentionTag())) {
					case ":arrow_right:":
						WeatherThread.this.next();
						break;

					case ":arrow_left:":
						WeatherThread.this.prev();
						break;

					default:
						return;
					}

					final Weather weather1 = weatherCity.getWeather(this.page - 1);
					final String description1 = weather1.getDescription().replaceFirst(".", (weather1.getDescription().charAt(0) + "").toUpperCase());
					final EmbedBuilder embed1 = this.bot.defaultEmbedBuilder("Météo :", "Prévisons pour " + weatherCity.getCityName() + " (" + weatherCity.getCityCountry() + ") (" + weather1.getDate() + ")", null);
					embed1.setFooter("Page : " + this.page + " / " + this.numberOfPage, user.getAvatar());
					embed1.addField("Description :", description1);
					if (temp) embed1.addField("Température :", weather1.getTemp() + "°C", true);
					if (humidity) embed1.addField("Humidité :", weather1.getHumidity() + "%", true);
					if (pressure) embed1.addField("Pression atmosphérique :", weather1.getPressure() + "hPa", true);
					if (clouds) embed1.addField("Nuages :", weather1.getClouds() + "%", true);
					if (wind) embed1.addField("Vent :", weather1.getWindSpeed() + "m/s, " + weather1.getWindDir() + "°", true);
					if (rain) embed1.addField("Pluie :", weather1.getRain() + "mm", true);
					if (snow) embed1.addField("Neige :", weather1.getSnow() + "mm", true);
					event.editMessage(embed1);
				}
			};

			message.addReactionAddListener(listener);

			final int time = 90; // En secondes
			Thread.sleep(time * 1000);

			message.removeListener(ReactionAddListener.class, listener);

		} catch (InterruptedException | ExecutionException e) {

			e.printStackTrace();
		}

		super.run();
	}

	/**
	 *
	 * Passer à la page suivante.
	 *
	 */
	private void next() {

		this.page++;
		if (this.page > this.numberOfPage) {

			this.page = 1;
		}
	}

	/**
	 *
	 * Passer à la page précédente.
	 *
	 */
	private void prev() {

		this.page--;
		if (this.page < 1) {

			this.page = this.numberOfPage;
		}
	}
}
