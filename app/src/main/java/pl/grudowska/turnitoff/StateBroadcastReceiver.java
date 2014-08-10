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
        final String SSID_wifi;

        // Get saved network name
        SharedPreferences settings = context.getSharedPreferences(ConfigDialog.SAVED_WIFI_NAME, 0);
        SSID_wifi = settings.getString(ConfigDialog.SAVED_WIFI_NAME, "Default");
        Log.i("WIFI name", SSID_wifi);

        // Get connectivity intent
        if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {

            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            // check is hardware wifi is on
            if (manager.isWifiEnabled()) {

                Log.i(LOG, "1. hardware WIFI enabled");

                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.State state = networkInfo.getState();

                if (state == NetworkInfo.State.CONNECTED) {
                    WifiStateHistory.recordConnectedSsid(manager.getConnectionInfo().getSSID().replace("\"", ""));
                    Log.i(LOG, "2A. WIFI CONNECTED: " + manager.getConnectionInfo().getSSID().replace("\"", ""));
                }

                if (state == NetworkInfo.State.DISCONNECTED) {

                    Log.i(LOG, "2B. WIFI DISCONNECTED: " + manager.getConnectionInfo().getSSID().replace("\"", ""));

                    // If disconnected with network defined by the user
                    if (WifiStateHistory.getLastConnectedSsid() != null && WifiStateHistory.getLastConnectedSsid().equals(SSID_wifi)) {

                        Log.i(LOG, "3. WIFI HISTORY: " + WifiStateHistory.getLastConnectedSsid());

                        Intent intentAlert = new Intent(context, CustomAlertDialog.class);
                        intentAlert.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intentAlert);

                        WifiStateHistory.recordConnectedSsid("none");
                    }
                }
            }
        }
    }
}
