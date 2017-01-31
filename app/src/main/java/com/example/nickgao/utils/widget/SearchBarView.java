package com.example.nickgao.utils.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.nickgao.R;

/**
 * Created by nick.gao on 1/31/17.
 */

public class SearchBarView extends RelativeLayout implements TextWatcher,View.OnClickListener,
                                    View.OnLongClickListener{

    private static final String TAG = "SearchBarView";

    private EditText searchInput;
    private ImageView closeBtn;
    private Context context;

    private SearchHandler mSearchHandler;

    public SearchBarView(Context context) {
        super(context);
        init(context);
    }


    private void init(Context context) {
        inflate(context, R.layout.search_bar, this);
        searchInput = (EditText) findViewById(R.id.search_bar_et);
        searchInput.addTextChangedListener(this);
       // searchInput.setOnFocusChangeListener(this);
        searchInput.setOnClickListener(this);
        closeBtn = (ImageView) findViewById(R.id.search_bar_close_iv);
        closeBtn.setOnClickListener(this);
        closeBtn.setOnLongClickListener(this);

        this.context = context;
    }

    public void setSearchHandler(SearchHandler searchHandler){
        if (searchHandler == null){
            throw new IllegalArgumentException("search handler");
        }

        mSearchHandler = searchHandler;
    }


    public SearchBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mSearchHandler.search(s.toString());

        if (searchInput.length() > 0) {
            closeBtn.setVisibility(View.VISIBLE);
            closeBtn.setClickable(searchInput.length() != 0);
        } else {
            closeBtn.setVisibility(View.GONE);
        }
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(searchInput.getText());
    }

    public boolean isInputFiledFocused() {
        return searchInput.isFocused();
    }

    public CharSequence getSearchInputText() {
        return searchInput.getText();
    }


    public interface SearchHandler {
        void search(String filter);
    }


    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.search_bar_close_iv:
                searchInput.getEditableText().clear();
                searchInput.setPressed(false);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_bar_close_iv:
                searchInput.getEditableText().clear();
                searchInput.requestFocus();
                return;

            case R.id.search_bar_et:
                setFocusable();
                return;
        }
    }

    public void setFocusable() {
        inputRequestFocus();
        showInput();
    }

    public void inputRequestFocus() {
        searchInput.setFocusable(true);
        searchInput.setFocusableInTouchMode(true);
        searchInput.requestFocus();
    }

    private void showInput() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void clearEditText() {
        searchInput.getEditableText().clear();
    }

    public void clearFocus() {
        searchInput.clearFocus();
    }



}
