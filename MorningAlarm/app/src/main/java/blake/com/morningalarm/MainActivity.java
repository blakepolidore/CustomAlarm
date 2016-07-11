package blake.com.morningalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.Calendar;

import blake.com.morningalarm.interfaces.PhotoInterface;
import blake.com.morningalarm.interfaces.QuotesInterface;
import blake.com.morningalarm.interfaces.RonSwansonInterface;
import blake.com.morningalarm.models.pictures.PhotoRoot;
import blake.com.morningalarm.models.quotes.Root;
import blake.com.morningalarm.services.RingtoneService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private ToggleButton toggleButton;
    private TextView textView;
    private ImageView imageView;
    private static MainActivity activityInstance;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private QuotesInterface quotesInterface;
    private PhotoInterface photoInterface;
    private RonSwansonInterface ronSwansonInterface;
    private Calendar calendar;

    //region sharedpref
    private SharedPreferences sharedPreferences;
    private int picCounter = 0;
    private String COUNTER_KEY = "counter key";
    private boolean isToggled = false;
    private String TOGGLE_KEY = "toggle key";
    //endregion sharedpref

    public static String quoteOfTheDay;
    public static String authorQuoteOfTheDay;
    public static String ronQuote;
    public static String imageURL;
    public static String QUOTE_KEY = "quote key";
    public static MainActivity instance() {
        return activityInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        onToggledClick();
        //setQuotesRetrofit();
        setPicturesRetrofit();
        setRonSwansonRetrofit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityInstance = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(COUNTER_KEY, picCounter);
        editor.putBoolean(TOGGLE_KEY, isToggled);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        picCounter = sharedPreferences.getInt(COUNTER_KEY, 0);
        if (picCounter >98) {
            picCounter = 1;
        }
        isToggled = sharedPreferences.getBoolean(TOGGLE_KEY, isToggled);
        toggleButton.setChecked(isToggled);
    }

    private void setViews() {
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void onToggledClick() {
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleButton.isChecked()) {
                    if (calendar != null) {
                        calendar.clear();
                    }
                    isToggled = true;
                    Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                    calendar.set(Calendar.MINUTE, timePicker.getMinute());
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
                else {
                    if (calendar != null) {
                        calendar.clear();
                    }
                    isToggled = false;
                    alarmManager.cancel(pendingIntent);
                    if (pendingIntent != null) {
                        pendingIntent.cancel();
                    }
                    Intent ringtoneIntent = new Intent(MainActivity.this, RingtoneService.class);
                    stopService(ringtoneIntent);
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
                Log.d("onFailure", "failed");
            }
        });
    }

    private void setPicturesRetrofit() {
        Retrofit retrofitQuotes = new Retrofit.Builder()
                .baseUrl("https://pixabay.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        photoInterface = retrofitQuotes.create(PhotoInterface.class);

        Call<PhotoRoot> call = photoInterface.getPicture(Keys.photoKey, "nature", 100);
        call.enqueue(new Callback<PhotoRoot>() {
            @Override
            public void onResponse(Call<PhotoRoot> call, Response<PhotoRoot> response) {
                Log.d("pic onResponse", response.body().getHits()[picCounter].getWebformatURL());
                imageURL = response.body().getHits()[picCounter].getWebformatURL();
                picCounter++;
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

    public void setImageView(String url) {
        Picasso.with(getApplicationContext()).load(url).resize(900, 600).placeholder(R.drawable.ron).into(imageView);
    }

    private void setRonSwansonRetrofit() {
        Retrofit retrofitQuotes = new Retrofit.Builder()
                .baseUrl("http://ron-swanson-quotes.herokuapp.com/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ronSwansonInterface = retrofitQuotes.create(RonSwansonInterface.class);

        Call<String[]> call = ronSwansonInterface.getRonQuote();
        call.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {
                ronQuote = response.body()[0];
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                Log.d("Swanson", "fail");
            }
        });
    }

}
