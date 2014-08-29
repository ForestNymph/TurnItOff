package pl.grudowska.turnitoff;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by sylwia on 29.08.14.
 */
public class SharedPreferencesManager {

    public static void saveDataString(Context context, String preference, String value) {
        SharedPreferences settings = context.getSharedPreferences(preference, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(preference, value);
        editor.apply();

        Log.i("SharedPreferencesManager ", "saveDataString() " + "PREFERENCE: " + preference + ": " + value);
    }

    public static void saveDataBoolean(Context context, String preference, Boolean value) {
        SharedPreferences settings = context.getSharedPreferences(preference, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(preference, value);
        editor.apply();

        Log.i("SharedPreferencesManager ", "saveDataBoolean() " + "PREFERENCE: " + preference + ": " + value);
    }

    public static String loadDataString(Context context, String preference, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(preference, 0);
        String data = settings.getString(preference, defaultValue);

        Log.i("SharedPreferencesManager ", "loadDataString() " + "PREFERENCE: " + preference + ": " + data);

        return data;
    }

    public static Boolean loadDataBoolean(Context context, String preference, Boolean defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(preference, 0);
        Boolean data = settings.getBoolean(preference, defaultValue);

        Log.i("SharedPreferencesManager ", "loadDataBoolean() " + "PREFERENCE: " + preference + ": " + data);

        return data;
    }
}
