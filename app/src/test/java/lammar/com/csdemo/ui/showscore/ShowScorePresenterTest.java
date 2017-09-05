package lammar.com.csdemo.ui.showscore;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import lammar.com.csdemo.data.CreditScore;
import lammar.com.csdemo.data.source.ClearScoreService;
import lammar.com.csdemo.helper.NetworkConnectivityHelper;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShowScorePresenterTest {

    @Mock
    private ShowScoreContract.View showScoreView;

    @Mock
    private Context mockContext;

    private ShowScorePresenter showScorePresenter;

    @Mock
    private NetworkConnectivityHelper networkConnectivityHelper;

    @Mock
    private ClearScoreService clearScoreService;

    @Before
    public void setupShowScorePresenter(){

        MockitoAnnotations.initMocks(this);
        showScorePresenter = new ShowScorePresenter(networkConnectivityHelper, clearScoreService, showScoreView);

        // Override RxJava scheduler
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.immediate());

        // Override RxAndroid schedulers
        final RxAndroidPlugins rxAndroidPlugins = RxAndroidPlugins.getInstance();
        rxAndroidPlugins.registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void getScoreWhenNoConnection_ShowsNoConnectionError(){

        //Given that there is no network connection
        when(networkConnectivityHelper.isNetworkAvailable()).thenReturn(false);

        //When get score is requested
        showScorePresenter.getScore();

        //Then no network error is shown
        verify(showScoreView).showNoNetworkError();
    }

    @Test
    public void getScoreWhenReturnedObjectIsNull_ShowNoScoreError(){
        //Given that there is a network connection
        when(networkConnectivityHelper.isNetworkAvailable()).thenReturn(true);

        //And null object is returned
        when(clearScoreService.getScore()).thenReturn(Observable.just(null));

        //When get score is requested
        showScorePresenter.getScore();

        //Then no score error is shown
        verify(showScoreView).showNoScoreError();
    }

    @Test
    public void getScoreWhenReturnedObjectHasInvalidMaxScoreValue_ShowNoScoreError(){
        //Given that there is a network connection
        when(networkConnectivityHelper.isNetworkAvailable()).thenReturn(true);

        //And object with invalid maxProgress value (equal to 0) is returned
        when(clearScoreService.getScore()).thenReturn(Observable.just(new CreditScore()));

        //When get score is requested
//        showScorePresenter.getScore();

        //Then no score error is shown
        verify(showScoreView).showNoScoreError();
    }

    @Test
    public void getScore_ShowScoreView(){
        //Given that there is a network connection
        when(networkConnectivityHelper.isNetworkAvailable()).thenReturn(true);

        //And object with valid maxProgress value (greater than 0) is returned
        CreditScore creditScore = new CreditScore();
        creditScore.creditReportInfo = new CreditScore.CreditReportInfo();
        creditScore.creditReportInfo.maxScoreValue = 100;
        when(clearScoreService.getScore()).thenReturn(Observable.just(creditScore));

        //When get score is requested
        showScorePresenter.getScore();

        //Then no score error is shown
        verify(showScoreView).showScore(0,100,0);
    }
}