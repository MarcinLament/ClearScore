package lammar.com.csdemo.ui.showscore;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import lammar.com.csdemo.R;
import lammar.com.csdemo.data.source.ClearScoreService;
import lammar.com.csdemo.helper.NetworkConnectivityHelper;
import lammar.com.csdemo.util.ActivityUtils;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_score);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        ShowScoreFragment showScoreFragment =  (ShowScoreFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (showScoreFragment == null) {
            showScoreFragment = ShowScoreFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), showScoreFragment, R.id.content_frame);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ClearScoreService.SERVICE_ENDPOINT)
                .build();

        ClearScoreService clearScoreService = retrofit.create(ClearScoreService.class);

        NetworkConnectivityHelper networkConnectivity = new NetworkConnectivityHelper(this);
        ShowScorePresenter presenter = new ShowScorePresenter(networkConnectivity, clearScoreService, showScoreFragment);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
