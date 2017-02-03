package lammar.com.csdemo.ui.controls;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import lammar.com.csdemo.R;


public class CreditScoreControl extends FrameLayout {

    @BindView(R.id.score_value_view) TextView scoreValueView;
    @BindView(R.id.score_max_value_view) TextView scoreMaxValueView;
    @BindView(R.id.score_progress_bar) ProgressBar scoreProgressBar;

    public CreditScoreControl(Context context) {
        super(context);
        init(context);
    }

    public CreditScoreControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CreditScoreControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        inflate(context, R.layout.view_credit_score, this);
        ButterKnife.bind(this);
    }

    public void show(int minValue, int maxValue, int value){

        setVisibility(View.VISIBLE);
        scoreProgressBar.setMax(maxValue);
        scoreMaxValueView.setText(getResources().getString(R.string.score_view_line2, maxValue));

        int percentage = Math.round((float)value / maxValue * 100);

        scoreValueView.setTextColor(getProgressValueTextColor(percentage));

        Drawable progressBarDrawable = getProgressBarGradient(percentage);
        scoreProgressBar.setProgressDrawable(progressBarDrawable);

        //start main frame animations
        clearAnimation();

        ObjectAnimator translationZFrameAnimator = getTranslationZFrameAnimator();
        translationZFrameAnimator.start();

        ObjectAnimator translationYFrameAnimator = getTranslationYFrameAnimator();
        translationYFrameAnimator.start();

        //start main frame background animation
        ValueAnimator colorAnimation = getMainFrameColorAnimation();
        colorAnimation.start();

        //start progress bar animations
        scoreProgressBar.clearAnimation();

        ObjectAnimator progressBarAnimator = getProgressBarAnimator(minValue, value);
        progressBarAnimator.start();

        ObjectAnimator progressBarAlphaAnimator = getProgressBarAlphaAnimator();
        progressBarAlphaAnimator.start();

        //start score value animation
        ValueAnimator progressBarValueAnimator = getProgressBarValueAnimator(minValue, value);
        progressBarValueAnimator.start();
    }

    public void hide(){
        setVisibility(View.GONE);

        scoreProgressBar.setAlpha(0);
        scoreProgressBar.setProgress(0);

        ViewCompat.setTranslationZ(this, 0);
        scoreValueView.setText("");

        DrawableCompat.setTint(this.getBackground().mutate(), ContextCompat.getColor(getContext(), R.color.colorWindow));
    }

    private Drawable getProgressBarGradient(int percentage){
        if(percentage <= 25){
            return ContextCompat.getDrawable(getContext(), R.drawable.oval_progress_bar_red);
        }else if(percentage <= 50){
            return ContextCompat.getDrawable(getContext(), R.drawable.oval_progress_bar_orange);
        }else if(percentage <= 75){
            return ContextCompat.getDrawable(getContext(), R.drawable.oval_progress_bar_green);
        }else{
            return ContextCompat.getDrawable(getContext(), R.drawable.oval_progress_bar_blue);
        }
    }

    private int getProgressValueTextColor(int percentage){
        if(percentage <= 25){
            return ContextCompat.getColor(getContext(), R.color.colorProgressRed1);
        }else if(percentage <= 50){
            return ContextCompat.getColor(getContext(), R.color.colorProgressOrange1);
        }else if(percentage <= 75){
            return ContextCompat.getColor(getContext(), R.color.colorProgressGreen1);
        }else{
            return ContextCompat.getColor(getContext(), R.color.colorProgressBlue1);
        }
    }

    @NonNull
    private ValueAnimator getMainFrameColorAnimation() {
        int colorFrom = ContextCompat.getColor(getContext(), R.color.colorWindow);
        int colorTo = ContextCompat.getColor(getContext(), R.color.colorScoreArea);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(600);
        colorAnimation.addUpdateListener(animator -> {
            DrawableCompat.setTint(this.getBackground().mutate(), (int) animator.getAnimatedValue());
        });
        return colorAnimation;
    }

    @NonNull
    private ValueAnimator getProgressBarValueAnimator(int minValue, int value) {
        ValueAnimator progressBarValueAnimator = new ValueAnimator();
        progressBarValueAnimator.setObjectValues(minValue, value);
        progressBarValueAnimator.addUpdateListener(animation -> scoreValueView.setText(String.valueOf(animation.getAnimatedValue())));
        progressBarValueAnimator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        progressBarValueAnimator.setDuration(1000);
        return progressBarValueAnimator;
    }

    @NonNull
    private ObjectAnimator getProgressBarAlphaAnimator() {
        ObjectAnimator progressBarAlphaAnimator = ObjectAnimator.ofFloat (scoreProgressBar, "alpha", 0, 1);
        progressBarAlphaAnimator.setDuration(300);
        progressBarAlphaAnimator.setStartDelay(150);
        progressBarAlphaAnimator.setInterpolator (new DecelerateInterpolator());
        return progressBarAlphaAnimator;
    }

    @NonNull
    private ObjectAnimator getProgressBarAnimator(int minValue, int value) {
        ObjectAnimator progressBarAnimator = ObjectAnimator.ofInt (scoreProgressBar, "progress", minValue, value);
        progressBarAnimator.setDuration(1000);
        progressBarAnimator.setStartDelay(150);
        progressBarAnimator.setInterpolator (new DecelerateInterpolator());
        return progressBarAnimator;
    }

    @NonNull
    private ObjectAnimator getTranslationYFrameAnimator() {
        ObjectAnimator translationYFrameAnimator = ObjectAnimator.ofFloat (this, "translationY", 0,
                getResources().getDimension(R.dimen.credit_score_translation_y));
        translationYFrameAnimator.setDuration(700);
        translationYFrameAnimator.setInterpolator (new DecelerateInterpolator());
        return translationYFrameAnimator;
    }

    private ObjectAnimator getTranslationZFrameAnimator() {
        ObjectAnimator translationZFrameAnimator = ObjectAnimator.ofFloat (this, "translationZ", 0,
                getResources().getDimension(R.dimen.credit_score_translation_z));
        translationZFrameAnimator.setDuration(700);
        translationZFrameAnimator.setInterpolator (new DecelerateInterpolator());
        return translationZFrameAnimator;
    }
}
