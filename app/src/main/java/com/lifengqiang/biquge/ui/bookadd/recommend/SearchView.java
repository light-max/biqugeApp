package com.lifengqiang.biquge.ui.bookadd.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lifengqiang.biquge.R;

public class SearchView extends LinearLayout {
    private OnSearchListener onSearchListener;
    private ImageView clearButton;
    private ImageView searchIcon;
    private EditText searchValue;
    private TextView searchButton;

    public SearchView(Context context) {
        super(context, null);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_search_edittext, this);
        clearButton = findViewById(R.id.clear);
        searchIcon = findViewById(R.id.icon);
        searchValue = findViewById(R.id.value);
        searchButton = findViewById(R.id.button);
        clearButton.setOnClickListener(v -> {
            searchValue.setText("");
            checkValue("");
        });
        searchValue.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                checkValue(v.getText().toString());
                return true;
            }
            return false;
        });
        searchButton.setOnClickListener(v -> {
            checkValue(searchValue.getText().toString());
        });
    }

    public void setText(String text) {
        searchValue.setText(text);
    }

    public void callOnSearch(){
        searchButton.callOnClick();
    }

    private void checkValue(String text) {
        searchValue.clearFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(((AppCompatActivity) getContext()).getWindow().getDecorView().getWindowToken(), 0);
        if (onSearchListener != null && !text.trim().isEmpty()) {
            onSearchListener.onSearch(text);
        }
    }

    public void clearFocus() {
        searchValue.clearFocus();
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    public interface OnSearchListener {
        void onSearch(String text);
    }
}
