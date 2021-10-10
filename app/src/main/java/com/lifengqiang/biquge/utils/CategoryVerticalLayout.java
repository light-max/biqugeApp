package com.lifengqiang.biquge.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.data.BookClassification;

import java.util.ArrayList;
import java.util.List;

public class CategoryVerticalLayout extends ScrollView {
    private List<BookClassification> classifications;
    private final RadioGroup rootView;
    private final int itemWidth;
    private final int itemHeight;
    private final float textSize;
    private final int dp4;
    private List<RadioButton> buttons;

    private OnSelectedListener onSelectedListener;

    public CategoryVerticalLayout(Context context) {
        this(context, null);
    }

    public CategoryVerticalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        itemWidth = DisplayUtils.dp2px(context, 88);
        itemHeight = DisplayUtils.dp2px(context, 48);
        textSize = 16;
        dp4 = DisplayUtils.dp2px(context, 4);
        rootView = new RadioGroup(context);
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setLayoutParams(new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.MATCH_PARENT
        ));
        rootView.setPadding(0, itemHeight / 2, 0, 0);
        addView(rootView);
    }

    public void setBookClassifications(List<BookClassification> classifications) {
        this.classifications = classifications;
        updateUi();
    }

    @SuppressLint({"UseCompatLoadingForColorStateLists", "ResourceType"})
    public void updateUi() {
        rootView.removeAllViews();
        buttons = new ArrayList<>();
        for (int i = 0; i < classifications.size(); i++) {
            BookClassification c = classifications.get(i);
            RadioButton button = new RadioButton(getContext());
            button.setLayoutParams(new LinearLayout.LayoutParams(itemWidth, itemHeight));
            button.setButtonDrawable(null);
            button.setGravity(Gravity.CENTER);
//            button.setTypeface(Typeface.DEFAULT_BOLD);
            button.setTextSize(textSize);
            button.setText(c.name);
            button.setTextColor(getResources().getColorStateList(R.drawable.color_category_item));
            rootView.addView(button);
            View line = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = dp4;
            params.rightMargin = dp4;
            line.setLayoutParams(params);
            line.setBackgroundResource(android.R.color.darker_gray);
            rootView.addView(line);
            int finalI = i;
            button.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked && onSelectedListener != null) {
                    onSelectedListener.onSelected(c, finalI);
                }
            });
            buttons.add(button);
        }
    }

    public void setCurrentSelectItem(int position) {
        if (buttons != null && buttons.size() != 0) {
            buttons.get(position).setChecked(true);
        }
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    public interface OnSelectedListener {
        void onSelected(BookClassification c, int position);
    }
}
