package pl.grudowska.turnitoff;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by sylwia on 7/28/14.
 */
public class WifiStateHistory {

    private static String lastConnectedSsid = null;
    private static Date lastBroadcast = null;
    private static String lastBroadcastSsid = null;

    public static void updateState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (networkInfo.isConnected()) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            lastConnectedSsid = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
        }
    }

    public static boolean notBroadcastInMinutes(String ssid, int minutes) {
        if (ssid != null && ssid.equals(lastBroadcastSsid)) {
            long timeSince = (new Date()).getTime() - lastBroadcast.getTime();

            if (timeSince < TimeUnit.MINUTES.toMillis(minutes)) {
                return false;
            }
        }

        return true;
    }

    public static void recordBroadcastNow(String ssid) {
        lastBroadcast = new Date();
        lastBroadcastSsid = ssid;
    }

    public static void recordConnectedSsid(String ssid) {
        lastConnectedSsid = ssid;
    }

    public static String getLastConnectedSsid() {
        return lastConnectedSsid;
    }
}
