package pl.grudowska.turnitoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        final String SSID_last_wifi;

        // Get saved network name
        SharedPreferences settings_save = context.getSharedPreferences(ConfigDialog.SAVED_WIFI_NAME, 0);
        SSID_saved_wifi = settings_save.getString(ConfigDialog.SAVED_WIFI_NAME, "Default");

        // Get current network name
        SharedPreferences settings_curr = context.getSharedPreferences(ConfigDialog.CURRENT_WIFI_NAME, 0);
        SSID_last_wifi = settings_curr.getString(ConfigDialog.CURRENT_WIFI_NAME, "Default");

        Log.i("WIFI saved name", SSID_saved_wifi);
        Log.i("WIFI last name", SSID_last_wifi);

        // Get connectivity intent
        if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {

            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            // check is hardware wifi is on
            if (manager.isWifiEnabled()) {

                Log.i(LOG, "Hardware WIFI enabled");

                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.State state = networkInfo.getState();

                if (state == NetworkInfo.State.CONNECTED) {
                    // TODO add changing state to last connected wifi
                }

                if (state == NetworkInfo.State.DISCONNECTED) {

                    // If disconnected with network defined by the user
                    if (SSID_last_wifi.equals(SSID_saved_wifi)) {

                        Log.i(LOG, "WIFI LAST DISCONNECTED: " + SSID_last_wifi);

                        Intent intentAlert = new Intent(context, CustomAlertDialog.class);
                        intentAlert.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intentAlert);
                    }
                }
            }
        }
    }
}
