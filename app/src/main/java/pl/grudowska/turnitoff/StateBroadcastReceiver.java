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
        final String WIFI_CONNECTION = "OtherConnectionStates";

        Log.i(LOG, "Intent action: " + action);

        // Get connectivity intent
        if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {

            // Collecting data about type of connection
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

            // Collecting data about hardware wifi state connection [enable/disable]
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            if (manager.isWifiEnabled()) {
                // If network connection exist, if type of network connection is wifi
                if (null != activeNetwork && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {

                    // Collecting information about current wifi state
                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    NetworkInfo.State state = networkInfo.getState();

                    if (state == NetworkInfo.State.CONNECTED) {

                        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        String SSID_current_wifi = wifiManager.getConnectionInfo().getSSID().replace("\"", "");

                        Log.i(LOG, "WIFI CONNECTED name: " + SSID_current_wifi);

                        // Save current wifi name
                        SharedPreferencesManager.saveDataString(context, ConfigDialog.CURRENT_WIFI_NAME,
                                SSID_current_wifi);
                        // Save information about connection. [is wifi connected]
                        SharedPreferencesManager.saveDataBoolean(context, WIFI_CONNECTION, true);
                    }
                } else {
                    // If connection status changed for wifi not for other types of connections [mobile/bluetooth etc.]
                    if (SharedPreferencesManager.loadDataBoolean(context, WIFI_CONNECTION, true)) {
                        // Collecting information about current wifi state
                        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                        NetworkInfo.State state = networkInfo.getState();

                        if (state == NetworkInfo.State.DISCONNECTED) {
                            // Get current wifi name
                            String SSID_current_wifi = SharedPreferencesManager.loadDataString(context,
                                    ConfigDialog.CURRENT_WIFI_NAME, "none");

                            Log.i(LOG, "WIFI current name: " + SSID_current_wifi);

                            // Get saved wifi name
                            SharedPreferences wifi_saved = context.getSharedPreferences(ConfigDialog.SAVED_WIFI_NAME, 0);
                            String SSID_saved_wifi = SharedPreferencesManager.loadDataString(context,
                                    ConfigDialog.SAVED_WIFI_NAME, "none");

                            Log.i(LOG, "WIFI saved name: " + SSID_saved_wifi);

                            // If disconnected with wifi network defined by the user
                            if (SSID_current_wifi.equals(SSID_saved_wifi)) {

                                // Flag for not checking other states connection when wifi disconnected
                                // We only intrested in changing a wifi state
                                SharedPreferencesManager.saveDataBoolean(context, WIFI_CONNECTION, false);

                                Log.i(LOG, "WIFI DISCONNECTED name: " + SSID_current_wifi);

                                Intent intentAlert = new Intent(context, CustomAlertDialog.class);
                                intentAlert.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intentAlert);
                            }
                        }
                    }
                }
            }
        }
    }
}
