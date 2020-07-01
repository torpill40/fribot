package com.torpill.fribot.api.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import com.torpill.fribot.App;
import com.torpill.fribot.api.APIException;
import com.torpill.fribot.util.JSON;

/**
 *
 * API Open Weather Map <a href=
 * "https://home.openweathermap.org/">https://home.openweathermap.org/</a>.
 *
 * @author torpill40
 *
 */

public class WeatherAPI {

	private String appID;
	private final String baseURL = "http://api.openweathermap.org/data/2.5";

	/**
	 *
	 * Charger la clé d'API.
	 *
	 * @param appID
	 *            : clé d'API
	 */
	public void setAppID(final String appID) {

		this.appID = appID;
	}

	private JSONObject getForecastJSON(final String city) {

		if (this.appID == null) {

			App.LOGGER.error(APIException.NO_KEY);
			throw new APIException(APIException.NO_KEY);
		}

		try {

			final URL url = new URL(this.baseURL + "/forecast?q=" + city + "&units=metric&lang=fr&appid=" + this.appID);
			final HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			final InputStream response = con.getInputStream();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(response));

			final StringBuilder builder = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {

				builder.append(line + "\n");
			}

			reader.close();
			con.disconnect();

			return new JSONObject(builder.toString());

		} catch (final MalformedURLException e) {

			App.LOGGER.error(APIException.BAD_URL);
			throw new APIException(APIException.BAD_URL);

		} catch (final IOException e) {

			App.LOGGER.error(APIException.NO_CONNECTION);
			return null;
		}
	}

	/**
	 *
	 * Récupérer les prévisions sur 5 jours d'une ville.
	 *
	 * @param cityName
	 *            : nom de la ville dont on veut récupérer les prévisions.
	 * @return prévisons météo de la ville
	 *
	 * @see com.torpill.fribot.api.weather.WeatherCity
	 * @see com.torpill.fribot.api.weather.Weather
	 */
	public WeatherCity getForecast(final String cityName) {

		final JSONObject forecast = this.getForecastJSON(cityName);
		if (forecast == null) return null;

		final JSONObject city = JSON.getJSONObject(forecast, "city");
		if (city != null) {

			final WeatherCity weatherCity = new WeatherCity(JSON.getString(city, "name"), JSON.getString(city, "country"));

			final JSONArray list = JSON.getJSONArray(forecast, "list");
			if (list != null) {

				for (int i = 0; i < list.length(); i++) {

					final JSONObject listElem = JSON.getJSONObject(list, i);
					if (listElem != null) {

						final JSONArray weatherData = JSON.getJSONArray(listElem, "weather");
						if (weatherData != null) {

							final JSONObject weatherElem = JSON.getJSONObject(weatherData, 0);
							if (weatherElem != null) {

								final Weather weather = weatherCity.addWeather(JSON.getString(listElem, "dt_txt"), JSON.getString(weatherElem, "description"));

								final JSONObject main = JSON.getJSONObject(listElem, "main");
								if (main != null) {

									// @formatter:off

									weather.setTemp(JSON.getDouble(main, "temp"))
										.setFeelsLike(JSON.getDouble(main, "feels_like"))
										.setTempMin(JSON.getDouble(main, "temp_min"))
										.setTempMax(JSON.getDouble(main, "temp_max"))
										.setPressure(JSON.getDouble(main, "pressure"))
										.setSeaLevel(JSON.getDouble(main, "sea_level"))
										.setGroundLevel(JSON.getDouble(main, "grnd_level"))
										.setHumidity(JSON.getDouble(main, "humidity"));

									// @formatter:on
								}

								final JSONObject clouds = JSON.getJSONObject(listElem, "clouds");
								if (clouds != null) {

									weather.setClouds(JSON.getDouble(clouds, "all"));
								}

								final JSONObject wind = JSON.getJSONObject(listElem, "wind");
								if (wind != null) {

									// @formatter:off

									weather.setWindSpeed(JSON.getDouble(wind, "speed"))
										.setWindDir(JSON.getDouble(wind, "deg"));

									// @formatter:on
								}

								final JSONObject rain = JSON.getJSONObject(listElem, "rain");
								if (rain != null) {

									weather.setRain(JSON.getDouble(rain, "3h"));
								}

								final JSONObject snow = JSON.getJSONObject(listElem, "snow");
								if (snow != null) {

									weather.setSnow(JSON.getDouble(snow, "3h"));
								}
							}
						}
					}
				}

				return weatherCity;
			}
		}

		return null;
	}
}
