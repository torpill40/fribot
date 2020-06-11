package com.torpill.fribot.api.weather;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Cette classe représente les prévisions météorologiques d'une villes.
 *
 * @author torpill40
 *
 * @see com.torpill.fribot.api.weather.Weather
 *
 */

public class WeatherCity {

	private final String cityName;
	private final String cityCountry;

	private final List<Weather> weathers;

	/**
	 *
	 * Constructeur de la classe <code>WeatherCity</code>.
	 *
	 */
	public WeatherCity(final String cityName, final String cityCountry) {

		this.cityName = cityName;
		this.cityCountry = cityCountry;

		this.weathers = new ArrayList<>();
	}

	/**
	 *
	 * Ajouter une prévision à la liste.
	 *
	 * @param date
	 *            : date de la prévision
	 * @param description
	 *            : descriptif de la prévision
	 * @return la prévision créée
	 */
	public Weather addWeather(final String date, final String description) {

		final Weather weather = new Weather(date, description);
		this.weathers.add(weather);
		return weather;
	}

	public String getCityName() {

		return this.cityName;
	}

	public String getCityCountry() {

		return this.cityCountry;
	}

	/**
	 *
	 * Récupérer toutes les prévisions.
	 *
	 * @return prévisions
	 */
	public List<Weather> getWeathers() {

		return this.weathers;
	}

	/**
	 *
	 * Récupérer une prévision précise.<br>
	 * Les prévisions sont stockées par ordre croissant.
	 *
	 * @param index
	 *            : numéro de la prévision
	 * @return prévision
	 */
	public Weather getWeather(final int index) {

		if (index < 0 || index >= this.weathers.size()) return null;

		return this.weathers.get(index);
	}
}
