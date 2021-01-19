package tom.eyre.mp2021.utility;

import android.content.Context;
import android.content.res.Resources;
import android.icu.util.Measure;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by thomaseyre on 04/01/2018.
 */

public class ScreenUtils {

    public static int getScreenHeight(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int height = metrics.heightPixels;

        return height;
    }

    public static float getMeasuredHeight(ViewGroup view, float titleHeight, float adHeight){
        float measuredHeight = 0;

        for (int i = 0; i < view.getChildCount(); i++) {
            if(view.getVisibility() == View.VISIBLE) {
                measuredHeight += view.getChildAt(i).getHeight();// + convertDpToPixel(10, view.getContext());
            }
        }

        return measuredHeight + titleHeight + adHeight;
    }

    public static float getMeasuredHeight(ViewGroup view){
        float measuredHeight = 0;

        for (int i = 0; i < view.getChildCount(); i++) {
                measuredHeight += view.getChildAt(i).getHeight();
        }

        return measuredHeight;
    }

    public static float getParentMeasuredHeight(ViewGroup view){
        return ((View) view.getParent()).getMeasuredHeight();
    }

    public static int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;

        return width;
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
}
