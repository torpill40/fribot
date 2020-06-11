package com.torpill.fribot.api.weather;

/**
 *
 * Cette classe représente une prévision météorologique.
 *
 * @author torpill40
 *
 */

public class Weather {

	private final String date;
	private final String description;

	private double temp, feelsLike, tempMin, tempMax;
	private double pressure;
	private double seaLevel, groundLevel;
	private double humidity;

	private double clouds;
	private double windSpeed, windDir;
	private double rain;
	private double snow;

	/**
	 *
	 * Constructeur de la classe <code>Weather</code>.
	 *
	 * @param date
	 *            : date de la prévision
	 * @param description
	 *            : descriptif de la prévision
	 */
	public Weather(final String date, final String description) {

		this.date = date;
		this.description = description;
	}

	public double getTemp() {

		return this.temp;
	}

	public Weather setTemp(final double temp) {

		this.temp = temp;
		return this;
	}

	public double getFeelsLike() {

		return this.feelsLike;
	}

	public Weather setFeelsLike(final double feelsLike) {

		this.feelsLike = feelsLike;
		return this;
	}

	public double getTempMin() {

		return this.tempMin;
	}

	public Weather setTempMin(final double tempMin) {

		this.tempMin = tempMin;
		return this;
	}

	public double getTempMax() {

		return this.tempMax;
	}

	public Weather setTempMax(final double tempMax) {

		this.tempMax = tempMax;
		return this;
	}

	public double getPressure() {

		return this.pressure;
	}

	public Weather setPressure(final double pressure) {

		this.pressure = pressure;
		return this;
	}

	public double getSeaLevel() {

		return this.seaLevel;
	}

	public Weather setSeaLevel(final double seaLevel) {

		this.seaLevel = seaLevel;
		return this;
	}

	public double getGroundLevel() {

		return this.groundLevel;
	}

	public Weather setGroundLevel(final double groundLevel) {

		this.groundLevel = groundLevel;
		return this;
	}

	public double getHumidity() {

		return this.humidity;
	}

	public Weather setHumidity(final double humidity) {

		this.humidity = humidity;
		return this;
	}

	public double getClouds() {

		return this.clouds;
	}

	public Weather setClouds(final double clouds) {

		this.clouds = clouds;
		return this;
	}

	public double getWindSpeed() {

		return this.windSpeed;
	}

	public Weather setWindSpeed(final double windSpeed) {

		this.windSpeed = windSpeed;
		return this;
	}

	public double getWindDir() {

		return this.windDir;
	}

	public Weather setWindDir(final double windDir) {

		this.windDir = windDir;
		return this;
	}

	public double getRain() {

		return this.rain;
	}

	public Weather setRain(final double rain) {

		this.rain = rain;
		return this;
	}

	public double getSnow() {

		return this.snow;
	}

	public Weather setSnow(final double snow) {

		this.snow = snow;
		return this;
	}

	public String getDate() {

		return this.date;
	}

	public String getDescription() {

		return this.description;
	}
}
