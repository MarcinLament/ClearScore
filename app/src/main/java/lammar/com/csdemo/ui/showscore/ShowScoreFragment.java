package lammar.com.csdemo.ui.showscore;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lammar.com.csdemo.R;
import lammar.com.csdemo.ui.controls.CreditScoreControl;
import lammar.com.csdemo.util.DialogUtils;

/**
 * Created by marcinlament on 01/02/2017.
 */

public class ShowScoreFragment extends Fragment implements ShowScoreContract.View {

    @BindView(R.id.get_score_btn) Button getScoreBtn;
    @BindView(R.id.start_again_btn) Button startAgainBtn;
    @BindView(R.id.intro_message_view) TextView introMessageView;
    @BindView(R.id.progress_bar) ProgressBar progressBarView;
    @BindView(R.id.credit_score_control) CreditScoreControl creditScoreControl;

    private ShowScoreContract.Presenter presenter;

    public ShowScoreFragment() {
        // Requires empty public constructor
    }

    public static ShowScoreFragment newInstance() {
        return new ShowScoreFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_score, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.initialize();
    }

    @Override
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    @OnClick(R.id.get_score_btn)
    public void getScore(View view) {
        presenter.getScore();
    }

    @OnClick(R.id.start_again_btn)
    public void startAgain(View view) {
        presenter.startAgain();
    }

    @Override
    public void showLoading() {
        progressBarView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBarView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showScore(int minScore, int maxScore, int score) {
        creditScoreControl.show(minScore, maxScore, score);
    }

    @Override
    public void hideScore() {
        creditScoreControl.hide();
    }

    @Override
    public void showNoNetworkError() {
        DialogUtils.showAlertDialog(getActivity(),
                R.string.no_connection_error_title,
                R.string.no_connection_error_message,
                R.string.no_connection_error_button);
    }

    @Override
    public void showNoScoreError() {
        DialogUtils.showAlertDialog(getActivity(),
                R.string.no_score_error_title,
                R.string.no_score_error_message,
                R.string.no_score_error_button);
    }

    @Override
    public void showIntroMessage() {
        introMessageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideIntroMessage() {
        introMessageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setActionButtonReadyState() {
        startAgainBtn.setVisibility(View.INVISIBLE);
        getScoreBtn.setVisibility(View.VISIBLE);
        getScoreBtn.setEnabled(true);
    }

    @Override
    public void setActionButtonLoadingState() {
        startAgainBtn.setVisibility(View.INVISIBLE);
        getScoreBtn.setVisibility(View.VISIBLE);
        getScoreBtn.setEnabled(false);
    }

    @Override
    public void setActionButtonCompleteState() {
        getScoreBtn.setVisibility(View.INVISIBLE);
        startAgainBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPresenter(ShowScoreContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
