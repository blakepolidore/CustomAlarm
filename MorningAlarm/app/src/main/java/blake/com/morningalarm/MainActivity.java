package blake.com.morningalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private ToggleButton toggleButton;
    private static MainActivity activityInstance;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    public static MainActivity instance() {
        return activityInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        onToggledClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityInstance = this;
    }

    private void setViews() {
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
    }

    public void onToggledClick() {
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleButton.isChecked()) {
                    toggleButton.setTextOn("Alarm On");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                    calendar.set(Calendar.MINUTE, timePicker.getMinute());
                    Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                    AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);
//                    alarmManager.setAlarmClock(info, pendingIntent);
                }
                else {
                    toggleButton.setTextOff("Alarm Off");
                    alarmManager.cancel(pendingIntent);
                }
            }
        });
    }
}
