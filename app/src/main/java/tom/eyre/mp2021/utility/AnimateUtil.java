package tom.eyre.mp2021.utility;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.CornerFamily;

import static tom.eyre.mp2021.utility.ScreenUtils.convertDpToPixel;

/**
 * Created by thomaseyre on 27/03/2018.
 */

public class AnimateUtil {

    public void squareOffSearchCorners(final MaterialCardView cv, Context context) {
        ValueAnimator anim = ValueAnimator.ofFloat(convertDpToPixel(30f, context), convertDpToPixel(0f, context));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                cv.setShapeAppearanceModel(
                        cv.getShapeAppearanceModel()
                                .toBuilder()
                                .setBottomRightCorner(CornerFamily.ROUNDED, val)
                                .setBottomLeftCorner(CornerFamily.ROUNDED, val)
                                .build());
            }
        });
        anim.setDuration(250);
        anim.start();
    }

    public void roundOffSearchCorners(final MaterialCardView cv, Context context,int delay) {
        ValueAnimator anim = ValueAnimator.ofFloat(convertDpToPixel(0f, context), convertDpToPixel(30f, context));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                cv.setShapeAppearanceModel(
                        cv.getShapeAppearanceModel()
                                .toBuilder()
                                .setBottomRightCorner(CornerFamily.ROUNDED, val)
                                .setBottomLeftCorner(CornerFamily.ROUNDED, val)
                                .build());
            }
        });
        anim.setStartDelay(delay);
        anim.setDuration(50);
        anim.start();
    }

    public void expandListView(final ListView lv, Context context, int height) {
        ValueAnimator anim = ValueAnimator.ofInt((int) convertDpToPixel(0f, context), height);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) lv
                        .getLayoutParams();

                lp.height = val;

                lv.setLayoutParams(lp);
            }
        });
        anim.setStartDelay(250);
        anim.setDuration(500);
        anim.start();
    }

    public void officeCostsBreakdown(final RelativeLayout rl, Context context, int currentHeight, int newHeight) {
        ValueAnimator anim = ValueAnimator.ofInt(currentHeight, newHeight);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams lp = rl.getLayoutParams();

                lp.height = val;

                rl.setLayoutParams(lp);
            }
        });
        anim.setDuration(500);
        anim.start();
    }

    public void expand(final CardView cv, Context context) {
        ValueAnimator anim = ValueAnimator.ofInt((int) convertDpToPixel(32f, context), (int) convertDpToPixel(80f, context));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams lp = cv.getLayoutParams();

                lp.width = val;

                cv.setLayoutParams(lp);
            }
        });
        anim.setDuration(500);
        anim.start();
    }

    public void slowShow(final View v, int animTime, int delay) {
        ValueAnimator anim = ValueAnimator.ofFloat(0f,1f);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float val = (float) valueAnimator.getAnimatedValue();
                v.setAlpha(val);
            }
        });
        anim.setDuration(animTime);
        anim.setStartDelay(delay);
        anim.start();
    }

    public void slowHide(final View v, int animTime, int delay) {
        ValueAnimator anim = ValueAnimator.ofFloat(1f,0f);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float val = (float) valueAnimator.getAnimatedValue();
                v.setAlpha(val);
            }
        });
        anim.setDuration(animTime);
        anim.setStartDelay(delay);
        anim.start();
    }

    public void shrinkListView(final ListView lv, Context context, int height) {
        ValueAnimator anim = ValueAnimator.ofInt(height, (int) convertDpToPixel(0f, context));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) lv
                        .getLayoutParams();

                lp.height = val;

                lv.setLayoutParams(lp);
            }
        });
        anim.setDuration(500);
        anim.start();
    }
}
