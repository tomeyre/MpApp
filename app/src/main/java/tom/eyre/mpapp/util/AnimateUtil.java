package tom.eyre.mpapp.util;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.CornerFamily;

import static tom.eyre.mpapp.util.ScreenUtils.convertDpToPixel;

/**
 * Created by thomaseyre on 27/03/2018.
 */

public class AnimateUtil {

    public void squareOffSearchCorners(final MaterialCardView cv, Context context) {
        final Context innerContext = context;
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

    public void roundOffSearchCorners(final MaterialCardView cv, Context context) {
        final Context innerContext = context;
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
        anim.setStartDelay(500);
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
