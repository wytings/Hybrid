package com.wytings.hybrid.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wytings.hybrid.App;
import com.wytings.hybrid.R;

/**
 * Created by rex.wei on 2016/10/27.
 */
public class ViewReplaceHelper {
    private final static int REPLACE_ID = R.id.container_replace; //this is id for activity or fragment
    private View replaceView;
    private ViewGroup containerParent;
    private View.OnClickListener clickListener;

    public void setOnClickListener(View.OnClickListener listener) {
        this.clickListener = listener;
    }

    /**
     * @param replaceSource you can pass activity, fragment or view to tell where is the container for ReplaceView
     * @return detect whether replace has been performed
     */


    public boolean replace(Object replaceSource) {
        View originalContent = null;
        if (containerParent == null) {
            if (replaceSource instanceof Activity) {
                originalContent = ((Activity) replaceSource).findViewById(REPLACE_ID);
            } else if (replaceSource instanceof Fragment) {
                View rootView = ((Fragment) replaceSource).getView();
                if (rootView != null) {
                    originalContent = rootView.findViewById(REPLACE_ID);
                }
            } else if (replaceSource instanceof ViewGroup) {
                originalContent = (View) replaceSource;
            } else {
            }

            if (originalContent != null && originalContent.getParent() != null) {
                containerParent = (ViewGroup) originalContent.getParent();
                containerParent.setTag(REPLACE_ID, originalContent);
            } else {
                Log.d("wytings", "cannot handle replacement lack of a view container");
                return false;
            }
        }
        if (containerParent == null) {
            return false;
        }
        originalContent = (View) containerParent.getTag(REPLACE_ID);
        if (originalContent.getParent() == null) {
            return false;
        }

        containerParent.removeView(originalContent);
        containerParent.addView(getReplaceView());
        return true;
    }

    public void restore() {
        if (containerParent == null) {
            Log.d("wytings", "you don't view container to restore");
            return;
        }

        View originalContent = (View) containerParent.getTag(REPLACE_ID);
        if (originalContent.getParent() == containerParent) {
            return;
        }

        containerParent.removeView(getReplaceView());
        containerParent.addView(originalContent);
    }

    private View getReplaceView() {
        if (replaceView == null) {
            Context context = App.getContext();
            Resources resources = context.getResources();
            int matchParent = LinearLayout.LayoutParams.MATCH_PARENT;
            int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
            int top = resources.getDimensionPixelSize(R.dimen.fail_load_data_top_margin);
            int padding = resources.getDimensionPixelSize(R.dimen.super_toast_padding);
            float textSize = resources.getDimensionPixelSize(R.dimen.common_font_size_1080p_42px);

            LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(wrapContent, wrapContent);
            textLayout.setMargins(0, top, 0, 0);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            linearLayout.setClickable(true);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(matchParent, matchParent));

            TextView textView = new TextView(context);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(resources.getColor(R.color.text_market_cn_bid_ask));
            textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.fail_load_data, 0, 0);
            textView.setCompoundDrawablePadding(padding);
            textView.setLayoutParams(textLayout);
            textView.setText(R.string.retry_after_fail);

            linearLayout.setOnClickListener(clickListener);
            linearLayout.addView(textView);
            replaceView = linearLayout;
        }
        return replaceView;
    }


}
