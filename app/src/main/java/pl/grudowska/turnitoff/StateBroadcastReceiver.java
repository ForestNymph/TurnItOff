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
        String SSID_current_wifi;

        // Get connectivity intent
        if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {

            // Collecting data about wifi
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            NetworkInfo.State state = networkInfo.getState();

            // Collecting data about type of connection
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

            // If general network has state CONNECTED and type of network connection is wifi
            if (state == NetworkInfo.State.CONNECTED && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {

                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                SSID_current_wifi = wifiManager.getConnectionInfo().getSSID().replace("\"", "");

                Log.i(LOG, "WIFI CONNECTED name: " + SSID_current_wifi);

                SharedPreferences settings = context.getSharedPreferences(ConfigDialog.CURRENT_WIFI_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(ConfigDialog.CURRENT_WIFI_NAME, SSID_current_wifi);
                editor.apply();
                // If wifi option is enabled and state is DISCONNECTED
            } else if (manager.isWifiEnabled() && state == NetworkInfo.State.DISCONNECTED) {

                // Get current wifi name
                SharedPreferences wifi_curr = context.getSharedPreferences(ConfigDialog.CURRENT_WIFI_NAME, 0);
                SSID_current_wifi = wifi_curr.getString(ConfigDialog.CURRENT_WIFI_NAME, "none");
                Log.i(LOG, "WIFI current name: " + SSID_current_wifi);

                // Get saved wifi name
                SharedPreferences wifi_saved = context.getSharedPreferences(ConfigDialog.SAVED_WIFI_NAME, 0);
                String SSID_saved_wifi = wifi_saved.getString(ConfigDialog.SAVED_WIFI_NAME, "none");
                Log.i(LOG, "WIFI saved name: " + SSID_saved_wifi);

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
