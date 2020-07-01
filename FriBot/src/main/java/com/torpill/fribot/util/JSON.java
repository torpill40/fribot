package com.torpill.fribot.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Cette classe permet d'intéragir avec les objets JSON tout en évitant les
 * JSONException
 *
 * @author torpill40
 *
 * @see org.json.JSONObject
 * @see org.json.JSONArray
 * @see org.json.JSONException
 *
 */

public class JSON {

	public static JSONObject getJSONObject(final JSONObject source, final String key) {

		try {

			return source.getJSONObject(key);

		} catch (final JSONException e) {

		}

		return null;
	}

	public static JSONArray getJSONArray(final JSONObject source, final String key) {

		try {

			return source.getJSONArray(key);

		} catch (final JSONException e) {

		}

		return null;
	}

	public static String getString(final JSONObject source, final String key) {

		try {

			return source.getString(key);

		} catch (final JSONException e) {

		}

		return "";
	}

	public static double getDouble(final JSONObject source, final String key) {

		try {

			return source.getDouble(key);

		} catch (final JSONException e) {

		}

		return 0;
	}

	public static int getInt(final JSONObject source, final String key) {

		try {

			return source.getInt(key);

		} catch (final JSONException e) {

		}

		return 0;
	}

	public static boolean getBoolean(final JSONObject source, final String key) {

		try {

			return source.getBoolean(key);

		} catch (final JSONException e) {

		}

		return false;
	}

	public static JSONObject getJSONObject(final JSONArray source, final int index) {

		try {

			return source.getJSONObject(index);

		} catch (final JSONException e) {

		}

		return null;
	}

	public static JSONArray getJSONArray(final JSONArray source, final int index) {

		try {

			return source.getJSONArray(index);

		} catch (final JSONException e) {

		}

		return null;
	}

	public static String getString(final JSONArray source, final int index) {

		try {

			return source.getString(index);

		} catch (final JSONException e) {

		}

		return "";
	}

	public static double getDouble(final JSONArray source, final int index) {

		try {

			return source.getDouble(index);

		} catch (final JSONException e) {

		}

		return 0;
	}

	public static int getInt(final JSONArray source, final int index) {

		try {

			return source.getInt(index);

		} catch (final JSONException e) {

		}

		return 0;
	}

	public static boolean getBoolean(final JSONArray source, final int index) {

		try {

			return source.getBoolean(index);

		} catch (final JSONException e) {

		}

		return false;
	}
}
