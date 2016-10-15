package blake.com.morningalarm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import blake.com.morningalarm.services.AlarmService;
import blake.com.morningalarm.services.RingtoneService;

/**
 * Created by Raiders on 6/8/16.
 * Broadcast receiver when activated starts the ringtone service and the
 * alarm service. It also sets the image and text views on the main activity
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity inst = MainActivity.instance();

        Intent ringtoneService = new Intent(context, RingtoneService.class);
        context.startService(ringtoneService);

        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());
//        if (MainActivity.quoteOfTheDay != null) {
//            intent.putExtra(MainActivity.QUOTE_KEY, MainActivity.quoteOfTheDay + " (" + MainActivity.authorQuoteOfTheDay + ")");
//            inst.setQuoteText(MainActivity.quoteOfTheDay + " (" + MainActivity.authorQuoteOfTheDay + ")");
//        }
        if (MainActivity.ronQuote != null) {
            intent.putExtra(MainActivity.QUOTE_KEY, MainActivity.ronQuote);
            inst.setQuoteText(MainActivity.ronQuote);
            inst.setImageView(MainActivity.imageURL);
        }
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
