package com.example.nickgao.rcfragments.presents;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import com.example.nickgao.R;
import com.example.nickgao.contacts.AddFavoritesActivity;
import com.example.nickgao.contacts.PersonalFavorites;
import com.example.nickgao.contacts.adapters.FavInContactsListAdapter;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.utils.RCMConstants;
import com.example.nickgao.utils.RcAlertDialog;

/**
 * Created by nick.gao on 1/31/17.
 */

public class FavoritesHorizontalListView implements IFavoritesView {

    private Fragment mFragment;

    private RecyclerView mFavRecyclerView;
    protected View mEmptyView;
    private boolean mIsEmpty = true;


    public FavoritesHorizontalListView(Fragment fragment, RecyclerView recyclerView, View emptyView) {
        mFragment = fragment;

        mFavRecyclerView = recyclerView;
        mFavRecyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(mFragment.getResources().getDimensionPixelOffset(R.dimen.fav_in_contacts_item_padding)));
        CustomLayoutManager layoutManager = new CustomLayoutManager(mFragment.getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFavRecyclerView.setLayoutManager(layoutManager);

        mEmptyView = emptyView;

        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAddingFavorite();
            }
        });
    }

    @Override
    public void setLoading(boolean value) {

    }

    @Override
    public void showEmpty() {
        mIsEmpty = true;
        mFavRecyclerView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmpty() {
        mIsEmpty = false;
        mEmptyView.setVisibility(View.GONE);
        mFavRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean isUIReady() {
        return !mFragment.getActivity().isFinishing() && !mFragment.isDetached();
    }

    @Override
    public void itemClick(View v, int position, final Contact contact) {
        MktLog.d("Favorites", "itemClick()");
    }



    private Context getContext() {
        return mFragment.getContext();
    }


    @Override
    public void requestAddingFavorite() {
        if (PersonalFavorites.isCloudFavoritesAchieveServerLimitation(mFragment.getContext())) {
            RcAlertDialog.showOkAlertDialog(mFragment.getContext(), R.string.favorite_over_server_limitation_title, R.string.favorite_over_server_limitation_content);
            return;
        }

        Intent intent = new Intent(mFragment.getContext(), AddFavoritesActivity.class);
        intent.setAction(RCMConstants.ACTION_LIST_ALL_CONTACTS);
        mFragment.startActivity(intent);
    }

    @Override
    public void setAdapter(IFavoritesAdapter adapter) {
        if (adapter instanceof RecyclerView.Adapter) {
            mFavRecyclerView.setAdapter((RecyclerView.Adapter) adapter);
        }
    }

    private void selectContact(Contact contact) {
        if (mFavRecyclerView != null) {
            RecyclerView.Adapter adapter = mFavRecyclerView.getAdapter();
            if (adapter != null && adapter instanceof FavInContactsListAdapter) {
                FavInContactsListAdapter favAdapter = (FavInContactsListAdapter) adapter;
                favAdapter.setSelectedContact(contact);
            }
        }
    }

    public void setVisibility(int visibility) {
        if (visibility == View.GONE) {
            mFavRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
        } else {
            if (mIsEmpty) {
                showEmpty();
            } else {
                hideEmpty();
            }
        }
    }

    private class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int mHorizontalSpace;

        public HorizontalSpaceItemDecoration(int horizontalSpace) {
            this.mHorizontalSpace = horizontalSpace;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.right = mHorizontalSpace;
        }
    }

    private class CustomLayoutManager extends LinearLayoutManager {
        private static final float MILLISECONDS_PER_INCH = 50f;
        private Context mContext;

        public CustomLayoutManager(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {
            LinearSmoothScroller smoothScroller = new LinearSmoothScroller(mContext) {
                @Override
                public PointF computeScrollVectorForPosition(int targetPosition) {
                    return CustomLayoutManager.this.computeScrollVectorForPosition(targetPosition);
                }

                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                }
            };

            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }
    }

    @Override
    public void photoChanged(long contactId, String eTag) {

    }

    @Override
    public boolean deselectContact() {
        return false;
    }

    @Override
    public void selectedContactChanged(Contact contact) {

    }
}
