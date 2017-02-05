package com.example.nickgao.utils.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.nickgao.R;

/**
 * Created by nick.gao on 2/4/17.
 */

public class RCMListView extends ListView implements View.OnTouchListener {

    private static final String TAG = "RCMListView";

    private int mTouchSlop;
    private int mMinmumVelocity;
    private AdapterWrapper mAdapterWrapper;
    private RemoveListener mRemoveListener = null;
    private RemoveViewClickListener mRemoveViewClickListener = null;
    private DelWidgetListener mDelWidgetListener = null;
    private boolean mRemoveable = false;
    private View.OnTouchListener mOutSideToucherListener = null;
    private int mCurrentRemoveStatusItem = -1;

    private int mDownX = 0;
    private int mDownY = 0;
    private boolean mDraging = false;
    private int mDeleteResource = -1;

    private Animation mHideAnimation, mShowAnimation;
    private boolean mNeedConfirm = false;
    private DelConfirmListener mDelConfirmListener = null;
    private boolean mRemoveAnimation = false;
    private boolean mShowDel = false;
    private boolean mComfirmFocused = true;

    /**
     * handler delete event.
     */
    public interface RemoveListener {
        void remove(int removeItem);
    }

    public interface DelWidgetListener {
        void onDelWidgetStateChanged(final View v, int state);
    }

    public interface DelConfirmListener {
        void onDelConfirm(final View item, int pos);
    }

    public RCMListView(Context context) {
        super(context);
        init();
    }

    public RCMListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        final ViewConfiguration configuration = ViewConfiguration.get(this.getContext());
        mMinmumVelocity = configuration.getScaledMinimumFlingVelocity();
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        mHideAnimation = AnimationUtils.loadAnimation(this.getContext(), R.anim.scalegone);
        mShowAnimation = AnimationUtils.loadAnimation(this.getContext(), R.anim.scale);
        mOutSideToucherListener = null;
        this.setOnTouchListener(this);
        mDeleteResource = R.id.deleteButton;
        mComfirmFocused = true;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mAdapterWrapper = new AdapterWrapper(adapter);
        mCurrentRemoveStatusItem = -1;
        mShowDel = false;
        super.setAdapter(mAdapterWrapper);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //AB-10272 Call Log list item long-click response becomes the same as normal-click after switching between Call Log and other screen, e.g. Messages.
        if (mComfirmFocused && !this.isFocused()) {
            this.requestFocus();
        }

        if (ev == null) {
            return false;
        }
        final int action = ev.getAction();
        /*
        if(action == MotionEvent.ACTION_DOWN && mRemoveable && mCurrentRemoveStatusItem != -1){
			cancelRemoveMode(true);
		}*/
        if (action == MotionEvent.ACTION_DOWN) {
            if (mRemoveable && mCurrentRemoveStatusItem != -1 && judgeRemoveEvent(ev) == false) {
                int pos = mCurrentRemoveStatusItem + this.getHeaderViewsCount() - this.getFirstVisiblePosition();
                final View child = RCMListView.this.getChildAt(pos);
                if (child != null) {
                    final View delete =  child.findViewById(mDeleteResource);
                    if (delete != null && delete.getVisibility() != View.GONE) {
                        hideAnimation(delete, child);
                    }
                }
                return true;
            } else {
                if (mShowDel && judgeRemoveEvent(ev) == false) {
                    return true;
                }
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mRemoveable) {
            int action = ev.getAction();
            int posX = (int) ev.getX();
            int posY = (int) ev.getY();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = posX;
                    mDownY = posY;
                    if (mCurrentRemoveStatusItem != -1) {
                        mCurrentRemoveStatusItem = -1;
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE: {
                    if (mShowDel) {
                        return true;
                    }

                    if (mDraging) {
                        return true;
                    }

                    int pos = RCMListView.this.pointToPosition(posX, posY);
                    int pos2 = pos - RCMListView.this.getFirstVisiblePosition();
                    final View child = RCMListView.this.getChildAt(pos2);
                    if (child != null) {
                        final View delete =  child.findViewById(mDeleteResource);
                        if (delete != null && mCurrentRemoveStatusItem == -1) {
                            if (mDraging == false) {
                                if (mDownX - posX > mTouchSlop && Math.abs(mDownY - posY) < mTouchSlop) {
                                    mDraging = true;
                                }
                            }
                            if (mDraging) {
                                showAnimation(delete, child);
                                mCurrentRemoveStatusItem = pos - RCMListView.this.getHeaderViewsCount();
                            }
                        }
                    }
                }
                break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mDraging = false;
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mOutSideToucherListener != null) {
            return mOutSideToucherListener.onTouch(v, event);
        }
        return false;
    }

    private void showAnimation(final View v1, final View v2) {
        mShowDel = true;
        mShowAnimation.reset();
        if (mDelWidgetListener != null) {
            mDelWidgetListener.onDelWidgetStateChanged(v2, 1);
        }
        v1.setPressed(false);
        v1.setVisibility(View.VISIBLE);
        v1.startAnimation(mShowAnimation);
    }

    private void hideAnimation(final View v1, final View v2) {
        mHideAnimation.reset();
        v1.startAnimation(mHideAnimation);
        mHideAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mShowDel = false;
                v1.setVisibility(View.GONE);
                if (mDelWidgetListener != null) {
                    mDelWidgetListener.onDelWidgetStateChanged(v2, 0);
                }
            }
        });
    }

    protected void removeListItem(final View rowView, final int positon) {
        if (rowView == null) {
            Log.e(TAG, "rowView is nul");
            return;
        }

        if (mRemoveAnimation) {
            final Animation animation = AnimationUtils.loadAnimation(rowView.getContext(), R.anim.remove_item);
            animation.setAnimationListener(new Animation.AnimationListener() {

                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    if (mRemoveListener != null) {
                        mRemoveListener.remove(positon);
                    }
                }
            });
            rowView.startAnimation(animation);
        } else {
            if (mRemoveListener != null) {
                mRemoveListener.remove(positon);
            }
        }
    }

    /**
     * judge if the motion-event point is in the delete button's region
     *
     * @param ev
     * @return
     */
    private boolean judgeRemoveEvent(MotionEvent ev) {
        int posX = (int) ev.getX();
        int posY = (int) ev.getY();
        int pos = RCMListView.this.pointToPosition(posX, posY);
        int pos2 = pos - RCMListView.this.getFirstVisiblePosition();
        View child = RCMListView.this.getChildAt(pos2);
        if (child != null) {
            View delete = child.findViewById(mDeleteResource);
            if (delete != null && delete.getVisibility() == View.VISIBLE) {
                Rect rect = new Rect();
                delete.getGlobalVisibleRect(rect);
                if (rect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * if we want to /do not want to make sure the view is focused when user touches the widget.
     *
     * @param need
     */
    public void setComfirmFocused(boolean need) {
        mComfirmFocused = need;
    }

    /**
     * cancel remove mode and update mTargetlist-view
     */
    public void cancelRemoveMode(boolean smooth) {

        if (mCurrentRemoveStatusItem == -1) {
            mShowDel = false;
            return;
        }

        if (mRemoveable) {
            int pos = mCurrentRemoveStatusItem + this.getHeaderViewsCount() - this.getFirstVisiblePosition();
            final View child = RCMListView.this.getChildAt(pos);
            if (child != null) {
                final View delete = child.findViewById(mDeleteResource);
                if (delete != null && delete.getVisibility() != View.GONE) {
                    if (smooth) {
                        hideAnimation(delete, child);
                    } else {
                        mShowDel = false;
                        delete.setVisibility(View.GONE);
                    }
                }
            } else {
                mShowDel = false;
            }
        } else {
            mShowDel = false;
        }
        mCurrentRemoveStatusItem = -1;
    }

    public boolean isRemoveMode() {
        return mCurrentRemoveStatusItem != -1;
    }

    public void setDelConfirmListener(DelConfirmListener l) {
        mNeedConfirm = true;
        mDelConfirmListener = l;
    }

    public void setDelWidgetListener(DelWidgetListener l) {
        mDelWidgetListener = l;
    }

    /**
     * set item remove listener for user defined.
     *
     * @param l
     */
    public void setRemoveListener(RemoveListener l) {
        mRemoveListener = l;
    }

    /**
     * set if list-view can remove item.
     *
     * @param removeable
     */
    public void setRemoveable(boolean removeable) {
        mRemoveable = removeable;
        if (mRemoveable) {
            mRemoveViewClickListener = new RemoveViewClickListener();
        }
    }

    /**
     * support to delete ani
     *
     * @param ani
     */
    public void setRemoveAnimation(boolean ani) {
        mRemoveAnimation = ani;
    }

    /**
     * support user defined touch listener for list-view.
     *
     * @param listner
     */
    public void setOutSideTouchListener(View.OnTouchListener listner) {
        mOutSideToucherListener = listner;
    }

    /**
     * inner remove view click listener.
     */
    private class RemoveViewClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Integer item = null;
            if (v.getTag() instanceof Integer) {
                item = (Integer) v.getTag();
            }

            if (item == null) {
                return;
            }

            if (mNeedConfirm && mDelConfirmListener != null) {
                int pos = mCurrentRemoveStatusItem + RCMListView.this.getHeaderViewsCount() - RCMListView.this.getFirstVisiblePosition();
                final View listItem = RCMListView.this.getChildAt(pos);
                mDelConfirmListener.onDelConfirm(listItem, item);
            } else {
                afterDeleteConfirm(item);
            }
        }
    }

    public void afterDeleteConfirm(int item) {
        if (mCurrentRemoveStatusItem == -1) {
            return;
        }

        int pos = mCurrentRemoveStatusItem + RCMListView.this.getHeaderViewsCount() - RCMListView.this.getFirstVisiblePosition();
        final View listItem = RCMListView.this.getChildAt(pos);
        removeListItem(listItem, item);
        mCurrentRemoveStatusItem = -1;
        mShowDel = false;
    }


    /**
     * AdapterWrapper is an adapter for RCMListView,it just like package for user defined adapter.
     *
     * @author jerry.cai
     */
    private class AdapterWrapper extends BaseAdapter {
        private ListAdapter mAdapter;

        public AdapterWrapper(ListAdapter adapter) {
            super();
            mAdapter = adapter;
            if (mAdapter != null) {
                mAdapter.registerDataSetObserver(new DataSetObserver() {
                    public void onChanged() {
                        notifyDataSetChanged();
                    }

                    public void onInvalidated() {
                        notifyDataSetInvalidated();
                    }
                });
            }
        }

        @Override
        public long getItemId(int position) {
            if (mAdapter != null) {
                return mAdapter.getItemId(position);
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mAdapter != null) {
                return mAdapter.getItem(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            if (mAdapter != null) {
                return mAdapter.getCount();
            }

            return 0;
        }

        @Override
        public boolean areAllItemsEnabled() {
            if (mAdapter != null) {
                return mAdapter.areAllItemsEnabled();
            }

            return super.areAllItemsEnabled();
        }

        @Override
        public boolean isEnabled(int position) {
            if (mAdapter != null) {
                return mAdapter.isEnabled(position);
            }

            return super.isEnabled(position);
        }

        @Override
        public int getItemViewType(int position) {
            if (mAdapter != null) {
                return mAdapter.getItemViewType(position);
            }

            return super.getItemViewType(position);
        }

        @Override
        public int getViewTypeCount() {
            if (mAdapter != null) {
                return mAdapter.getViewTypeCount();
            }

            return super.getViewTypeCount();
        }

        @Override
        public boolean hasStableIds() {
            if (mAdapter != null) {
                return mAdapter.hasStableIds();
            }

            return super.hasStableIds();
        }

        @Override
        public boolean isEmpty() {
            if (mAdapter != null) {
                return mAdapter.isEmpty();
            }

            return super.isEmpty();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (mAdapter == null) {
                return null;
            }

            View view = null;

            if (convertView != null) {
                view = mAdapter.getView(position, convertView, RCMListView.this);
            } else {
                view = mAdapter.getView(position, null, RCMListView.this);
            }


            //item can be removed?
            if (mRemoveable) {
                final View delView = view.findViewById(mDeleteResource);
                if (delView != null) {
                    delView.setOnClickListener(mRemoveViewClickListener);
                    delView.setTag(position);

                    if (mCurrentRemoveStatusItem != position) {
                        final Animation anim = delView.getAnimation();
                        if (anim == null && delView.getVisibility() != View.GONE) {
                            delView.setVisibility(View.GONE);
                        } else if (anim != null && anim.hasEnded() && delView.getVisibility() != View.GONE) {
                            delView.setVisibility(View.GONE);
                        }
                    } else {
                        delView.setVisibility(View.VISIBLE);
                    }

                } else {
                    Log.e(TAG, "you forget to add delete button to item view");
                }

            } else {
                final View delView = view.findViewById(mDeleteResource);
                if (delView != null) {
                    delView.setVisibility(View.GONE);
                }
            }

            return view;
        }
    }
}


