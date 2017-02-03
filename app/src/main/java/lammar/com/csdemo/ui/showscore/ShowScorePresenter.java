package lammar.com.csdemo.ui.showscore;

import android.support.annotation.NonNull;

import lammar.com.csdemo.data.CreditScore;
import lammar.com.csdemo.data.source.ClearScoreService;
import lammar.com.csdemo.helper.NetworkConnectivityHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by marcinlament on 01/02/2017.
 */

public class ShowScorePresenter implements ShowScoreContract.Presenter {

    private NetworkConnectivityHelper networkConnectivity;
    private ClearScoreService clearScoreService;
    private ShowScoreContract.View view;

    public ShowScorePresenter(NetworkConnectivityHelper networkConnectivity, ClearScoreService clearScoreService, @NonNull ShowScoreContract.View view){
        this.networkConnectivity = networkConnectivity;
        this.clearScoreService = clearScoreService;
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void initialize(){
        view.hideLoading();
        view.hideScore();
        view.showIntroMessage();
        view.setActionButtonReadyState();
    }

    @Override
    public void destroy() {
        view = null;
    }

    @Override
    public void getScore() {

        if(!networkConnectivity.isNetworkAvailable()){
            view.showNoNetworkError();
            return;
        }

        view.hideIntroMessage();
        view.showLoading();
        view.setActionButtonLoadingState();

        loadScore();
    }

    @Override
    public void startAgain() {
        view.hideScore();
        view.setActionButtonReadyState();
        view.showIntroMessage();
    }

    private void loadScore(){

        Observable<CreditScore> creditScoreObservable = clearScoreService.getScore();

        creditScoreObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CreditScore>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        if(view != null) {
                            view.hideLoading();
                            view.setActionButtonReadyState();
                            view.showNoScoreError();
                        }
                    }

                    @Override
                    public void onNext(CreditScore creditScore) {
                        if(view != null) {
                            view.hideLoading();

                            if (creditScore != null
                                    && creditScore.creditReportInfo != null
                                    && creditScore.creditReportInfo.maxScoreValue > 0) {

                                view.setActionButtonCompleteState();
                                view.showScore(creditScore.creditReportInfo.minScoreValue,
                                        creditScore.creditReportInfo.maxScoreValue,
                                        creditScore.creditReportInfo.score);
                            } else {
                                view.setActionButtonReadyState();
                                view.showNoScoreError();
                            }
                        }
                    }
                });
    }
}
