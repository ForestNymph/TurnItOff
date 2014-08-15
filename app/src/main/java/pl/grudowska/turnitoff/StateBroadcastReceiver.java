package pl.grudowska.turnitoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by sylwia on 7/23/14.
 */
public class StateBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        final String LOG = getClass().getSimpleName();
        final String SSID_saved_wifi;
        String SSID_current_wifi = "none";

        // Get saved network name
        SharedPreferences settings_save = context.getSharedPreferences(ConfigDialog.SAVED_WIFI_NAME, 0);
        SSID_saved_wifi = settings_save.getString(ConfigDialog.SAVED_WIFI_NAME, "none");

        Log.i(LOG, "WIFI saved name: " + SSID_saved_wifi);

        // Get connectivity intent
        if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {

            // Check is WIFI is enabled on device
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
            if (null != activeNetwork) {

                // If network available, if type wifi, if connected
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {

                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    NetworkInfo.State state = networkInfo.getState();

                    if (state == NetworkInfo.State.CONNECTED) {

                        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        SSID_current_wifi = wifiManager.getConnectionInfo().getSSID().replace("\"", "");

                        Log.i(LOG, "WIFI current wifi: " + SSID_current_wifi);

                        SharedPreferences settings = context.getSharedPreferences(ConfigDialog.CURRENT_WIFI_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(ConfigDialog.CURRENT_WIFI_NAME, SSID_current_wifi);
                        editor.apply();
                    }
                    // If wifi network disconnected
                } else {

                    SharedPreferences current_wifi = context.getSharedPreferences(ConfigDialog.CURRENT_WIFI_NAME, 0);
                    SSID_current_wifi = current_wifi.getString(ConfigDialog.CURRENT_WIFI_NAME, "none");

                    // If disconnected with wifi network defined by the user
                    if (SSID_current_wifi.equals(SSID_saved_wifi)) {

                        Log.i(LOG, "WIFI DISCONNECTED: " + SSID_current_wifi);

                        Intent intentAlert = new Intent(context, CustomAlertDialog.class);
                        intentAlert.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intentAlert);
                    }
                }
            }
        }
    }
}
