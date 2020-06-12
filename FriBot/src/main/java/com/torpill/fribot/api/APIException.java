package com.torpill.fribot.api;

/**
 *
 * Exception liée aux API.
 *
 * @author torpill40
 *
 * @see java.lang.RuntimeException
 *
 */

public class APIException extends RuntimeException {

	private static final long serialVersionUID = 8117623275362063075L;

	public static final String NO_KEY = "Pas de clé d'API renseignée.";
	public static final String BAD_URL = "URL inconnu.";
	public static final String NO_CONNECTION = "Erreur dans l'établissement de la connexion.";

	/**
	 *
	 * Constructeur de la classe <code>APIException</code>.
	 *
	 * @param message
	 *            : message d'erreur
	 */
	public APIException(final String message) {

		super(message);
	}
}
