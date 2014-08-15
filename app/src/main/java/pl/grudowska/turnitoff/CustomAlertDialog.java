package pl.grudowska.turnitoff;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAlertDialog extends Activity {

    private final String LOG = getClass().getSimpleName();
    private Vibrator vibrator;
    private Ringtone alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.activity_alert_dialog);
        setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);

        SharedPreferences settings = getSharedPreferences(ConfigDialog.NOTIFICATION, 0);

        String notification = settings.getString(ConfigDialog.NOTIFICATION, "Set Notification");

        TextView notificationText = (TextView) findViewById(R.id.text_notification);

        Log.i(LOG, "Notification: " + notification);

        notificationText.setText(notification);

        //final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Start immediately
        // Vibrate for 200 milliseconds
        // Sleep for 200 milliseconds
        long[] pattern = {0, 400, 100, 400, 100, 400, 100, 400, 100, 400, 100, 400, 100, 400, 100};

        // The "0" means to repeat the pattern starting at the beginning
        // The "-1" means to repeat only once
        vibrator.vibrate(pattern, -1);

        Uri system_sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        alarm = RingtoneManager.getRingtone(getApplicationContext(), system_sound);
        alarm.play();

        Button turnoffYES = (Button) findViewById(R.id.turnoff_yes);
        turnoffYES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(),
                        "Good :)", Toast.LENGTH_SHORT)
                        .show();
                alarm.stop();
                finish();
            }
        });

        Button turnoffNO = (Button) findViewById(R.id.turnoff_no);
        turnoffNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(),
                        "Go back", Toast.LENGTH_SHORT)
                        .show();
                alarm.stop();
                finish();
            }
        });
    }

    @Override
    public void finish() {
        vibrator.cancel();
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alert_dialog, menu);
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
