package blake.com.morningalarm.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import blake.com.morningalarm.MainActivity;
import blake.com.morningalarm.R;

/**
 * Created by Raiders on 6/8/16.
 * Sends a notification to the phone at the designated time
 */
public class AlarmService extends IntentService {

    private NotificationManager alarmNotificationManager;

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        if (MainActivity.ronQuote != null) {
            sendNotification(intent.getStringExtra(MainActivity.QUOTE_KEY));
        }
        else {
            sendNotification(getString(R.string.default_alarm_notification));
        }
    }

    private void sendNotification(String msg) {
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        android.support.v4.app.NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle(getString(R.string.alarm)).setSmallIcon(R.drawable.ron_icon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);


        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
    }
}
