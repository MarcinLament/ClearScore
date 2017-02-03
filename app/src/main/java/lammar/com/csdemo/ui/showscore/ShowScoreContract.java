package lammar.com.csdemo.ui.showscore;

import lammar.com.csdemo.ui.BasePresenter;
import lammar.com.csdemo.ui.BaseView;

/**
 * Created by marcinlament on 01/02/2017.
 */

public interface ShowScoreContract {

    interface View extends BaseView<Presenter> {

        void showLoading();
        void hideLoading();

        void showScore(int minScore, int maxScore, int score);
        void hideScore();
        void showNoNetworkError();
        void showNoScoreError();

        void showIntroMessage();
        void hideIntroMessage();

        void setActionButtonReadyState();
        void setActionButtonLoadingState();
        void setActionButtonCompleteState();
    }

    interface Presenter extends BasePresenter {

        void getScore();
        void startAgain();
    }
}
