package blake.com.morningalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;

import blake.com.morningalarm.interfaces.QuotesInterface;
import blake.com.morningalarm.models.Root;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private ToggleButton toggleButton;
    private static MainActivity activityInstance;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private QuotesInterface quotesInterface;

    public static MainActivity instance() {
        return activityInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        onToggledClick();
        setRetrofit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityInstance = this;
    }

    private void setViews() {
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
    }

    public void onToggledClick() {
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleButton.isChecked()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                    calendar.set(Calendar.MINUTE, timePicker.getMinute());
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
                else {
                    Log.d("Main", "toggle unchecked");
                    alarmManager.cancel(pendingIntent);
                }
            }
        });
    }

    private void setRetrofit() {
        Retrofit retrofitQuotes = new Retrofit.Builder()
                .baseUrl("http://quotes.rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        quotesInterface = retrofitQuotes.create(QuotesInterface.class);

        Log.d("setRetrofit", "in");

        Call<Root> call = quotesInterface.getQuotes();
        call.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                Log.d("onResponse", response.body().getContents().getQuotes()[0].getQuote());
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                Log.d("onFailure", "fucked up");
            }
        });
    }
}
