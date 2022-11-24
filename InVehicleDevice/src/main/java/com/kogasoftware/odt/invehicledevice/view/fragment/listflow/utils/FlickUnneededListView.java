package com.kogasoftware.odt.invehicledevice.view.fragment.listflow.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.kogasoftware.odt.invehicledevice.R;

/**
 * フリックする必要のないListView 右側に大きくスクロールボタンがある
 */
public class FlickUnneededListView extends FrameLayout
        implements
        OnScrollListener {
    @SuppressWarnings("unused")
    private static final String TAG = FlickUnneededListView.class
            .getSimpleName();
    protected static final Integer DISABLED_TEXT_COLOR = Color.GRAY;
    protected static final Integer ENABLED_TEXT_COLOR = Color.BLACK;
    protected ListView listView;
    protected final Button scrollUpButton;
    protected final Button scrollDownButton;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        boolean scrollUpButtonEnabled = false;
        if (firstVisibleItem > 0) {
            scrollUpButtonEnabled = true;
        } else {
            View firstVisibleView = listView.getChildAt(0);
            if (firstVisibleView != null && firstVisibleView.getTop() < 0) {
                scrollUpButtonEnabled = true;
            }
        }
        int lastVisibleItem = listView.getLastVisiblePosition();
        boolean scrollDownButtonEnabled = false;
        if (lastVisibleItem < listView.getCount() - 1) {
            scrollDownButtonEnabled = true;
        } else {
            View lastVisibleView = listView
                    .getChildAt(listView.getChildCount() - 1);
            if (lastVisibleView != null
                    && lastVisibleView.getBottom() > listView.getHeight()) {
                scrollDownButtonEnabled = true;
            }
        }

        if (scrollUpButtonEnabled) {
            scrollUpButton.setTextColor(ENABLED_TEXT_COLOR);
        } else {
            scrollUpButton
                    .setBackgroundResource(android.R.drawable.btn_default);
            scrollUpButton.setTextColor(DISABLED_TEXT_COLOR);
        }

        if (scrollDownButtonEnabled) {
            scrollDownButton.setTextColor(ENABLED_TEXT_COLOR);
        } else {
            scrollDownButton
                    .setBackgroundResource(android.R.drawable.btn_default);
            scrollDownButton.setTextColor(DISABLED_TEXT_COLOR);
        }

        scrollUpButton.setEnabled(scrollUpButtonEnabled);
        scrollDownButton.setEnabled(scrollDownButtonEnabled);
    }

    public void replaceListView(ListView newListView) {
        final ListView oldListView = listView;
        listView = newListView;
        listView.setOnScrollListener(this);
        final ViewGroup container = findViewById(R.id.flick_unneeded_list_view_inner_list_view_container);
        container.addView(newListView, 0);
        if (oldListView.getCount() == 0) {
            container.removeView(oldListView);
            listView.scrollTo(0, 0);
            return;
        }
        Animation hideAnimation = AnimationUtils.loadAnimation(getContext(),
                R.anim.hide_list_view);
        Animation showAnimation = AnimationUtils.loadAnimation(getContext(),
                R.anim.show_list_view);
        oldListView.setAnimation(hideAnimation);
        newListView.setAnimation(showAnimation);
        hideAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                container.removeView(oldListView);
                listView.scrollTo(0, 0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
        showAnimation.start();
        hideAnimation.start();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public FlickUnneededListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater layoutInflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addView(layoutInflater.inflate(R.layout.flick_unneeded_list_view, null),
                new FlickUnneededListView.LayoutParams(
                        FlickUnneededListView.LayoutParams.MATCH_PARENT,
                        FlickUnneededListView.LayoutParams.MATCH_PARENT));
        listView = findViewById(R.id.flick_unneeded_list_view_inner_list_view);
        scrollUpButton = findViewById(R.id.flick_unneeded_list_view_scroll_up_button);
        scrollDownButton = findViewById(R.id.flick_unneeded_list_view_scroll_down_button);
        if (isInEditMode()) {
            return;
        }
        listView.setOnScrollListener(this);
        scrollUpButton.setOnClickListener(view -> scrollUp());
        scrollDownButton.setOnClickListener(view -> scrollDown());
    }

    enum Adjust {
        NONE, UPPER, LOWER,
    }

    Adjust adjust = Adjust.UPPER;

    public void scrollUp() {
        int position = listView.getFirstVisiblePosition();
        if (position == listView.getLastVisiblePosition()) {
            position -= 1;
            if (position < 0) {
                return;
            }
        }
        if (Build.VERSION.SDK_INT >= 16 /* TODO きれいな実装にする */
                && adjust == Adjust.UPPER && position > 0) {
            adjust = Adjust.NONE;
            listView.smoothScrollToPosition(position - 1);
        } else {
            adjust = Adjust.UPPER;
            listView.smoothScrollToPosition(position);
        }
    }

    public void scrollDown() {
        int position = listView.getLastVisiblePosition();
        if (position == listView.getFirstVisiblePosition()) {
            position += 1;
            if (position >= listView.getCount()) {
                return;
            }
        }
        if (Build.VERSION.SDK_INT >= 16 /* TODO きれいな実装にする */
                && adjust == Adjust.LOWER && position + 1 < listView.getCount()) {
            adjust = Adjust.NONE;
            listView.smoothScrollToPosition(position + 1);
        } else {
            adjust = Adjust.LOWER;
            listView.smoothScrollToPosition(position);
        }
    }

    public ListView getListView() {
        return listView;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

}
