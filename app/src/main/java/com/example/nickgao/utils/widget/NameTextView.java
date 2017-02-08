package com.example.nickgao.utils.widget;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.nickgao.R;

import java.util.ArrayList;
import java.util.List;

public class NameTextView extends TextView {

    private static final String TAG = "[RC]NameTextView";

    private List<String> mNameList = null;

    public NameTextView(Context context) {
        super(context);
    }

    public NameTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NameTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w != oldw) {
            adjustText(w);
        }
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        adjustText(getWidth());
    }

    public void setNameList(List<String> names) {
        if (mNameList == names) {
            return;
        }

        mNameList = names;

        if (this.getWidth() > 0) {
            adjustText(getWidth());
        }
    }

    private void adjustText(int width) {
        if (mNameList == null || mNameList.size() == 0) {
            //AB-12636 App crashed after login app
            CharSequence text = getText();
            if (text != null) {
                setText(text);
            }
            return;
        }

        width = width - (getPaddingLeft() + getPaddingRight());
        setText(adjustText(width, getPaint(), mNameList));
    }

    private String adjustText(int fullwidth, Paint paint, List<String> names) {
        if (fullwidth <= 0) {
            return "";
        }

        ArrayList<Integer> sizeList = new ArrayList<Integer>();
        StringBuilder sb = new StringBuilder();

        int curLength = 0;
        String displayName = "";
        boolean canShowAll = true;
        for (int i = 0; i < names.size(); i++) {
            displayName = names.get(i);
            if (TextUtils.isEmpty(displayName)) {
                continue;
            }
            if (i > 0) {
                displayName = ", " + displayName;
            }
            sb.append(displayName);

            int contactWidth = (int) paint.measureText(displayName);
            sizeList.add(contactWidth);

            curLength += contactWidth;
            if (curLength > fullwidth) {
                canShowAll = false;
                break;
            }
        }

        if (canShowAll) {
            return sb.toString();
        }

        String strAndMoreSample = getContext().getString(R.string.expandable_and_more_sample);
        String strEllipsis = getContext().getString(R.string.expandable_ellipsis);
        String strAndMoreEllipsis = getContext().getString(R.string.expandable_and_more_ellipsis);
        String strEllipsisAndMore = getContext().getString(R.string.expandable_ellipsis_and_more);

        float morePartWidth = paint.measureText(strAndMoreSample);
        float ellipsisWidth = paint.measureText(strEllipsis);

        curLength = 0;
        sb.delete(0, sb.length());

        for (int i = 0; i < sizeList.size(); i++) {
            displayName = names.get(i);
            curLength += sizeList.get(i);

            if (curLength + morePartWidth <= fullwidth) {
                if (i == 0) {
                    sb.append(displayName);
                } else {
                    sb.append(", " + displayName);
                }
                continue;
            }

            int remainingCount = names.size() - i;

            if (i == 0) {
                if (names.size() == 1) {
                    String subName = getDisplaySubString(names.get(i), fullwidth - ellipsisWidth);
                    sb.append(subName).append(strEllipsis);
                } else if (names.size() > 1) {
                    String subName = getDisplaySubString(names.get(i), fullwidth - morePartWidth);
                    sb.append(subName).append(String.format(strEllipsisAndMore, --remainingCount));
                }
            } else {
                sb.append(String.format(strAndMoreEllipsis, remainingCount));
            }
            break;
        }

        return sb.toString();
    }

    private String getDisplaySubString(String strName, float remainingWidth) {
        String subString = "";

        for (int charIndex = 1; charIndex < strName.length(); charIndex++) {
            subString = strName.substring(0, charIndex);
            float width = this.getPaint().measureText(subString);

            if (width >= remainingWidth) {
                subString = strName.substring(0, charIndex - 1);
                break;
            }
        }

        return subString;
    }
}
