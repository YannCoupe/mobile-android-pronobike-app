package fr.ycoupe.pronobike.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;

/**
 * Utility class gathering android view functions.
 */
public class ViewUtils {

    public static float convertDpToPx(Context context, float dp) {
        Resources res = context.getResources();

        return dp * (res.getDisplayMetrics().densityDpi / 160f);
    }

    /**
     * Get the view location on the screen.
     *
     * @param v                    View to get the position
     * @param relativeToParentView Flag to set if position is relative to the parent view (to show
     *                             PopupWindow) or parent window relative (to show Dialog)
     * @return Y position of the view
     */
    public static int[] getViewPosition(final View v, final boolean relativeToParentView) {
        int x = 0;
        int y = 0;

        final View rootView = v.getRootView();
        if (rootView != null) {
            final int[] viewLocation = new int[2];
            v.getLocationOnScreen(viewLocation);
            x = viewLocation[0];
            y = viewLocation[1];

            final Rect windowRect = new Rect();
            v.getWindowVisibleDisplayFrame(windowRect);

            final int[] rootLocation = new int[2];
            rootView.getLocationOnScreen(rootLocation);

            if (relativeToParentView && rootLocation[1] != windowRect.top) {
                x -= rootLocation[0];
                y -= rootLocation[1];
            } else {
                x -= windowRect.left;
                y -= windowRect.top;
            }
        }

        return new int[]{x, y};
    }

    /**
     * Override the overscroll color to force it the the given color.
     *
     * @param res        Resources object.
     * @param colorResId The color to force.
     */
    public static void setOverscrollColor(final Resources res, final int colorResId) {
        try {
            final Drawable glow = res.getDrawable(res.getIdentifier("overscroll_glow", "drawable", "android"));
            if (glow != null) {
                glow.setColorFilter(res.getColor(colorResId), PorterDuff.Mode.SRC_IN);
            }

            final Drawable edge = res.getDrawable(res.getIdentifier("overscroll_edge", "drawable", "android"));
            if (edge != null) {
                edge.setColorFilter(res.getColor(colorResId), PorterDuff.Mode.SRC_IN);
            }
        } catch (final Exception e) {
            // Nothing to do
        }
    }

    /**
     * Expand a view with an animation
     *
     * @param v        The view to expand
     * @param duration The duration of the animation
     */
    public static void expandViewWithAnimation(final View v, final int duration) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        final Animation a = new Animation() {
            @Override
            protected void applyTransformation(final float interpolatedTime, final Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(duration);
        v.startAnimation(a);
    }

    /**
     * Collapse a view with an animation
     *
     * @param v        The view to expand
     * @param duration The duration of the animation
     */
    public static void collapseViewWithAnimation(final View v, final int duration) {
        final int initialHeight = v.getMeasuredHeight();

        final Animation a = new Animation() {
            @Override
            protected void applyTransformation(final float interpolatedTime, final Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(duration);
        v.startAnimation(a);
    }

    public static void closeKeyboard(final Context c, final IBinder windowToken) {
        final InputMethodManager inputMethodManager = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
    }
}
