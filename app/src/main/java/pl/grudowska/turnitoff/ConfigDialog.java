package pl.grudowska.turnitoff;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class ConfigDialog extends Activity {

    public static final String NOTIFICATION = "NotificationText";
    public static final String SAVED_WIFI_NAME = "SavedWifiName";
    public static final String CURRENT_WIFI_NAME = "CurrentWifiName";
    private static final String STATE_BROADCAST = "StateOfBroadcastReceiver";
    private final String LOG = getClass().getSimpleName();
    private ComponentName receiver;
    private boolean mSaveStateBroadcast;

    private static String getSSID(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (networkInfo.isConnected()) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            return wifiManager.getConnectionInfo().getSSID().replace("\"", "");
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*        try {
            File filename = new File(Environment.getExternalStorageDirectory()+"/logfile.log");
            Log.i(LOG, filename.getAbsolutePath().toString());
            filename.createNewFile();
            String cmd = "logcat -d -f "+filename.getAbsolutePath();
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

        // Setting app state
        SharedPreferences settings = getSharedPreferences(STATE_BROADCAST, 0);
        // If no any preferences or preferences set as false return false
        mSaveStateBroadcast = settings.getBoolean(STATE_BROADCAST, false);
        String state;
        if (mSaveStateBroadcast) {
            Log.i(LOG, "onCreate() Get existing preferences");
            state = "ON";
        } else {
            Log.i(LOG, "onCreate() Create new preferences");
            state = "OFF";
        }

        // Setting icon
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.activity_config_dialog);
        setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);

        // Setting current wifi name
        final TextView currentWifiName = (TextView) findViewById(R.id.current_wifi_ssid);
        if (getSSID(getApplicationContext()) == null) {
            currentWifiName.setText("not connected");
        } else {
            currentWifiName.setText(getSSID(getApplicationContext()));
            // Value need to check if this wifi was set last time
            Log.i(LOG, getSSID(getApplicationContext()));
            saveData(CURRENT_WIFI_NAME, getSSID(getApplicationContext()));
        }

        final TextView status = (TextView) findViewById(R.id.status);
        status.setText(state);

        // Setting saved wifi name
        final TextView savedWifiName = (TextView) findViewById(R.id.saved_wifi_ssid);
        savedWifiName.setText(loadData(SAVED_WIFI_NAME));

        // Setting notification text
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.switcher);
        TextView notificationText = (TextView) switcher.findViewById(R.id.notification_text_view);
        EditText notificationEdit = (EditText) switcher.findViewById(R.id.hidden_edit_view);

        String notification = loadData(NOTIFICATION);
        notificationText.setText(notification);
        notificationEdit.setText(notification);

        Button enable = (Button) findViewById(R.id.enable);
        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mSaveStateBroadcast) {

                    Log.i(LOG, "onClick() New preferences");

                    final TextView status = (TextView) findViewById(R.id.status);
                    status.setText("ON");

                    enableBroadcastReceiver();
                } else {
                    Log.i(LOG, "onClick() Existing preferences");
                }
            }
        });

        Button disable = (Button) findViewById(R.id.disable);
        disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final TextView status = (TextView) findViewById(R.id.status);
                status.setText("OFF");

                disableBroadcastReceiver();
            }
        });

        Button close = (Button) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button editNotification = (Button) findViewById(R.id.edit_wifi);
        editNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNotification();
            }
        });

        Button setWifi = (Button) findViewById(R.id.set_wifi);
        setWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData(SAVED_WIFI_NAME, getSSID(getApplicationContext()));

                final TextView savedWifiName = (TextView) findViewById(R.id.saved_wifi_ssid);
                savedWifiName.setText(loadData(SAVED_WIFI_NAME));
            }
        });
    }

    private void setNotification() {
        // New notification set by the user
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.switcher);
        switcher.showNext();

        TextView notificationText = (TextView) switcher.findViewById(R.id.notification_text_view);
        EditText notificationEdit = (EditText) switcher.findViewById(R.id.hidden_edit_view);

        String newNotification = notificationEdit.getText().toString();

        if (newNotification.isEmpty()) {
            notificationText.setText(loadData(NOTIFICATION));
        } else {
            notificationText.setText(newNotification);
        }
        saveData(NOTIFICATION, notificationEdit.getText().toString());
    }

    private void saveData(String preference, String data) {
        SharedPreferences settings = getSharedPreferences(preference, 0);
        SharedPreferences.Editor editor = settings.edit();

        Log.i(LOG, "saveData() " + data);

        editor.putString(preference, data);
        editor.apply();
    }

    private String loadData(String preference) {
        SharedPreferences settings = getSharedPreferences(preference, 0);
        String notification = settings.getString(preference, "Set data");

        Log.i(LOG, "loadData() " + notification);

        return notification;
    }

    protected void onStop() {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(STATE_BROADCAST, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(STATE_BROADCAST, mSaveStateBroadcast);
        // Apply the edits!
        editor.apply();

        super.onStop();
        Log.i(LOG, "onStop()");
    }

    private void enableBroadcastReceiver() {
        receiver = new ComponentName(this, pl.grudowska.turnitoff.StateBroadcastReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        mSaveStateBroadcast = true;
    }

    private void disableBroadcastReceiver() {
        receiver = new ComponentName(this, pl.grudowska.turnitoff.StateBroadcastReceiver.class);
        PackageManager pm = this.getPackageManager();
        // unregister receiver
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        mSaveStateBroadcast = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.config_dialog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
