package blake.com.morningalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;

import blake.com.morningalarm.interfaces.PhotoInterface;
import blake.com.morningalarm.interfaces.QuotesInterface;
import blake.com.morningalarm.models.pictures.PhotoRoot;
import blake.com.morningalarm.models.quotes.Root;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private ToggleButton toggleButton;
    private TextView textView;
    private static MainActivity activityInstance;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private QuotesInterface quotesInterface;
    private PhotoInterface photoInterface;

    public static String quoteOfTheDay;
    public static String authorQuoteOfTheDay;
    public static String QUOTE_KEY = "quote key";
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
        setQuotesRetrofit();
        setPicturesRetrofit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityInstance = this;
    }

    private void setViews() {
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        textView = (TextView) findViewById(R.id.textView);
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

    private void setQuotesRetrofit() {
        Retrofit retrofitQuotes = new Retrofit.Builder()
                .baseUrl("http://quotes.rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        quotesInterface = retrofitQuotes.create(QuotesInterface.class);

        Call<Root> call = quotesInterface.getQuotes();
        call.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                quoteOfTheDay = response.body().getContents().getQuotes()[0].getQuote();
                authorQuoteOfTheDay = response.body().getContents().getQuotes()[0].getAuthor();
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                Log.d("onFailure", "fucked up");
            }
        });
    }

    private void setPicturesRetrofit() {
        Retrofit retrofitQuotes = new Retrofit.Builder()
                .baseUrl("https://pixabay.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        photoInterface = retrofitQuotes.create(PhotoInterface.class);

        Call<PhotoRoot> call = photoInterface.getPicture("2730929-e3fc386f99be9f891fc81141d", "nature");
        call.enqueue(new Callback<PhotoRoot>() {
            @Override
            public void onResponse(Call<PhotoRoot> call, Response<PhotoRoot> response) {
                Log.d("onResponse Photos", response.body().getHits()[0].getPageURL());
            }

            @Override
            public void onFailure(Call<PhotoRoot> call, Throwable t) {
                Log.d("onFailure Photos", "failed");
            }
        });
    }

    public void setQuoteText(String quote) {
        textView.setText(quote);
    }
}
