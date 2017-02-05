package com.example.nickgao.favorite;

import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;

import com.example.nickgao.R;
import com.example.nickgao.contacts.PersonalFavorites;
import com.example.nickgao.contacts.adapters.FavoriteEntity;
import com.example.nickgao.contacts.adapters.FavoritesAdapter;
import com.example.nickgao.contacts.adapters.contactsprovider.CompanyContact;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.database.RCMDataStore;
import com.example.nickgao.database.RCMProvider;
import com.example.nickgao.database.UriHelper;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcfragments.presents.FavoritesPresenter;
import com.example.nickgao.rcfragments.presents.IFavoritesView;
import com.example.nickgao.titlebar.RCMainTitleBar;
import com.example.nickgao.titlebar.RCTitleBarWithDropDownFilter;
import com.example.nickgao.utils.widget.RCMDragSortController;
import com.example.nickgao.utils.widget.RCMListView;
import com.example.nickgao.utils.widget.RelativeLayoutEx;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick.gao on 2/4/17.
 */

public class FavoritesFragment extends BaseFavoritesFragment implements RCMainTitleBar.HeaderClickListener,
        RCMListView.RemoveListener, FavoritesAdapter.FavoritesClickListener  {

    private static final String TAG = "[RC]FavoritesFragment";

    private DragSortListView mDragableListView;
    private RCMListView mRCMListView;
    public int mDragStartMode = DragSortController.ON_DOWN;
    public boolean mRemoveEnabled = false;
    public int mRemoveMode = RCMDragSortController.MISS;
    public boolean mSortEnabled = true;
    public boolean mDragEnabled = true;


    private Button mDeleteButton;
    private View mEditControls;

    public RCMDragSortController buildController(DragSortListView dslv) {
        // defaults are
        // dragStartMode = onDown
        // removeMode = flingRight
        RCMDragSortController controller = new RCMDragSortController(dslv);
        controller.setDragHandleId(R.id.dragButton);
        controller.setClickRemoveId(R.id.deleteButton);
        controller.setRemoveEnabled(mRemoveEnabled);
        controller.setSortEnabled(mSortEnabled);
        controller.setDragInitMode(mDragStartMode);
        controller.setRemoveMode(mRemoveMode);
        controller.setBackgroundColor(Color.WHITE);
        return controller;
    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                FavoritesAdapter adapter = (FavoritesAdapter) mFavoritesPresenter.getAdapter();
                adapter.drop(from, to);
            }
        }
    };

    private FavoritesListView mFavoritesListView;
    private FavoritesPresenter mFavoritesPresenter;
    /**
     * Flag which indicate first start
     */
    protected boolean mJustCreated;

    private class InterceptTouchEventListener implements RelativeLayoutEx.InterceptTouchEventListener {
        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return false;
        }
    }

    InterceptTouchEventListener mInterceptTouchEventListener = new InterceptTouchEventListener();



    private class FavoritesListView implements IFavoritesView {
        @Override
        public void setLoading(boolean value) {
            FavoritesFragment.this.setLoading(value);
        }

        @Override
        public void showEmpty() {
            mEmptyIndicateView.setVisibility(View.VISIBLE);
            setEmptyText();
            changeLeftButtonStatus(true, false);
        }

        @Override
        public void hideEmpty() {
            mEmptyIndicateView.setVisibility(View.GONE);
            changeLeftButtonStatus(true, true);
        }

        @Override
        public boolean isUIReady() {
            return true;
        }

        @Override
        public void selectedContactChanged(Contact contact) {
        }

        @Override
        public boolean deselectContact() {
            return false;
        }

        @Override
        public void photoChanged(long contactId, String eTag) {
            int firstVisiblePosition = mRCMListView.getFirstVisiblePosition();
            int lastVisiblePosition = mRCMListView.getLastVisiblePosition();
            for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                FavoriteEntity entity = getFavoritesAdapter().getItem(i);
                if (entity.contactType == Contact.ContactType.CLOUD_COMPANY) {
                    CompanyContact companyContact = (CompanyContact) entity.contact;
                    if (contactId == companyContact.getId()) {
                        FavoritesAdapter.ContactListItemCache cache = (FavoritesAdapter.ContactListItemCache) mRCMListView.getChildAt(i - firstVisiblePosition).getTag();
                     //   ProfileImageOperator.loadImage(cache.photoView, eTag, contactId, RestRequestDownloadExtensionProfileImage.PicSize.SMALL);
                        break;
                    }
                }
            }
        }

        @Override
        public void setAdapter(com.example.nickgao.contacts.adapters.IFavoritesAdapter adapter) {
            if (adapter instanceof ListAdapter) {
                setListAdapter((ListAdapter) adapter);
            }
        }

        @Override
        public void itemClick(View v, int position, Contact contact) {

        }

        @Override
        public void requestAddingFavorite() {

        }
    }

    void changeLeftButtonStatus(boolean menuVisiable) {
        changeLeftButtonStatus(menuVisiable, (getFavoritesAdapter() != null) ? (getFavoritesAdapter().getFavoritesCount() > 0) : false);
    }

    void changeLeftButtonStatus(boolean menuVisiable, boolean hasFavorites) {
        if (menuVisiable) {
            if (hasFavorites) {
                setRightMenu(false);
            } else {
                setRightMenu(true);
            }
        } else {
            mRCTitleBar.setRightText(R.string.cancel);
            mRCTitleBar.setRightFirstImageResVisiblility(View.GONE);
            mRCTitleBar.setRightBtnEnabled(true);
            mRCTitleBar.setRightVisibility(View.VISIBLE);

            mRCTitleBar.setRightImageVisibility(View.GONE);
            mRCTitleBar.requestLayout();
        }
    }

    private void setRightMenu(boolean isSingleBtn) {
        mRCTitleBar.setRightBtnEnabled(false);
        mRCTitleBar.setRightVisibility(View.GONE);
        mRCTitleBar.setRightImageBtnEnabled(true);
        mRCTitleBar.setRightImageVisibility(View.VISIBLE);
        if (isSingleBtn) {
            mRCTitleBar.setRightFirstImageResVisiblility(View.GONE);
            mRCTitleBar.setRightImageVisibility(View.VISIBLE);
        } else {
            mRCTitleBar.setRightFirstImageResVisiblility(View.VISIBLE);
            mRCTitleBar.setRightImageVisibility(View.VISIBLE);
        }
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mChangeMainTabReceiver = new ChangeMainTabReceiver();
//        IntentFilter intentFilter = new IntentFilter(RCMConstants.ACTION_CURRENT_TAB_CHANGED);
//        mActivity.registerReceiver(mChangeMainTabReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorites_list_content_fragment, container, false);
    }


    private class ChangeMainTabReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            if (RCMConstants.ACTION_CURRENT_TAB_CHANGED.equals(intent.getAction())) {
//                String tabTag = intent.getStringExtra(RingCentralMain.TAB_TAG);
//                if (!RingCentralMain.MainActivities.Favorites.toString().equals(tabTag)) {
//                    mFavoritesPresenter.stopPresenceRefresher();
//                }
//                if (mFavoritesPresenter.getAdapter().isEditMode()) {
//                    cancelEditMode();
//                    //AB-9812 Favorite is not successfully deleted but not shown when tap Favorites item in favorites edit mode
//                    startQuery(false);
//                }
//            }
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mJustCreated = true;

        mRCTitleBar = (RCMainTitleBar) view.findViewById(R.id.favorites_top_tilte);
        mRCTitleBar.setButtonsClickCallback(this);
        mRCTitleBar.setRightBtnEnabled(false);
        mRCTitleBar.setRightVisibility(View.GONE);
        mRCTitleBar.setRightImageBtnEnabled(true);
        mRCTitleBar.setRightImageVisibility(View.VISIBLE);

        initCommon(view);

        mDragableListView = (DragSortListView) view.findViewById(R.id.dragSortListView);    // (DragSortListView)getListView();
        DragSortController dragSortController = buildController(mDragableListView);
        mDragableListView.setFloatViewManager(dragSortController);
        mDragableListView.setOnTouchListener(dragSortController);
        mDragableListView.setDragEnabled(mDragEnabled);
        mDragableListView.setDropListener(onDrop);

        mRCMListView = (RCMListView) getListView();
        mRCMListView.setRemoveable(true);


        mFavoritesListView = new FavoritesListView();
        mFavoritesPresenter = new FavoritesPresenter(getContext(), mFavoritesListView, new FavoritesAdapter(mActivity, this));

        mRCMListView.setRemoveListener(this);

        mDeleteButton = (Button) view.findViewById(R.id.delete_checked_documents);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Long> items = getFavoritesAdapter().getSelectedItems();
                applyDeleteChanges(items);
            }
        });

        view.findViewById(R.id.done_checked_documents).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyOrderChanges();
            }
        });

        mEditControls = view.findViewById(R.id.delete_controls_layout);

        RelativeLayoutEx viewGroup = (RelativeLayoutEx) view.findViewById(R.id.favoritesMainView);
        viewGroup.setInterceptTouchEvent(mInterceptTouchEventListener);
        if (!mRCMListView.getAdapter().isEmpty() && mEmptyIndicateView != null) {
            mEmptyIndicateView.setVisibility(View.GONE);
        }
        mRCTitleBarWithDropDownFilter.setRightFirstImageResVisiblility(View.VISIBLE);
        mRCTitleBarWithDropDownFilter.setRightImageVisibility(View.VISIBLE);
        setEmptyText();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (isHidden()) {
            return;
        }

        if (mJustCreated) {
            getListView().clearTextFilter();
        }

        mRCMListView.cancelRemoveMode(false);
        cancelEditMode();

        mFavoritesPresenter.resume(mJustCreated);

        if (!mRCMListView.isFocused()) {
            mRCMListView.requestFocus();
        }


        mJustCreated = false;
    }


    private void applyOrderChanges() {
        FavoritesAdapter adapter = getFavoritesAdapter();
        ArrayList<Long> remainedArrayIds = new ArrayList<>();
        if (adapter != null && adapter.getCount() != 0) {
            for (int i = 0; i < adapter.getCount(); i++) {
                FavoriteEntity entity = adapter.getItem(i);
                if (entity != null) {
                    remainedArrayIds.add(entity.id);
                }
            }
        }
        updateData(remainedArrayIds);
        cancelEditMode();
        PersonalFavorites.onOrderChanges();
    }

    private void updateData(final List<Long> contactList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri favoriteUri = UriHelper.getUri(RCMProvider.CLOUD_FAVORITES,
                        CurrentUserSettings.getSettings().getCurrentMailboxId());
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                for (int j = 0; j < contactList.size(); j++) {
                    ops.add(ContentProviderOperation.newUpdate(ContentUris.withAppendedId(favoriteUri, contactList.get(j)))
                            .withValue(RCMDataStore.CloudFavoritesTable.RCM_SORT, j)
                            .build());
                }

                try {
                    mActivity.getContentResolver().applyBatch(RCMProvider.AUTHORITY, ops);
                    startQuery(false);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }




    private void applyDeleteChanges(List<Long> items) {
        FavoritesAdapter adapter = getFavoritesAdapter();
        final ArrayList<Long> removedArrayIds = new ArrayList<>();
        if (adapter != null && adapter.getCount() != 0) {
            for (int i = 0; i < adapter.getCount(); i++) {
                FavoriteEntity entity = adapter.getItem(i);
                if (entity != null && items.contains(entity.id)) {
                    removedArrayIds.add(entity.contactId);
                }
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PersonalFavorites.markedAsDeletedInFavorites(removedArrayIds);
                    startQuery(false);
                } catch (Throwable th) {
                    MktLog.d(TAG, th.toString());
                }
            }
        }).start();

        cancelEditMode();
    }



    private void cancelEditMode() {
        FavoritesAdapter adapter = getFavoritesAdapter();
        if (adapter == null) {
            return;
        }

        if (!adapter.isEditMode()) {
            return;
        }
        adapter.setEditMode(false);
        adapter.setEditable(false);
        changeLeftButtonStatus(true);
        mRCTitleBarWithDropDownFilter.setRightFirstImageResVisiblility(View.VISIBLE);
        mRCTitleBarWithDropDownFilter.showProfile();
        mEditControls.setVisibility(View.GONE);
        mDragableListView.setVisibility(View.GONE);
        mRCMListView.setVisibility(View.VISIBLE);
    }



    @Override
    public void remove(final int pos) {
        FavoritesAdapter adapter = getFavoritesAdapter();
        if (adapter == null) {
            return;
        }

        FavoriteEntity entity = adapter.getItem(pos);
        if (entity != null) {
            long contactId = entity.contactId;
            Contact.ContactType contactType = entity.contactType;
            PersonalFavorites.markedAsDeletedInFavorites(contactId, contactType);

            startQuery(false);
        }
    }


    protected void updateCurrentTab() {
        switch (CurrentUserSettings.getSettings().getContactsCurrentTab()) {
            case ALL:
                mRCTitleBarWithDropDownFilter.initContactsFilterWithState(RCTitleBarWithDropDownFilter.CONTACTS_ALL_TAB);
                break;
            case COMPANY:
                mRCTitleBarWithDropDownFilter.initContactsFilterWithState(RCTitleBarWithDropDownFilter.CONTACTS_COMPANY_TAB);
                break;
            case DEVICE:
                mRCTitleBarWithDropDownFilter.initContactsFilterWithState(RCTitleBarWithDropDownFilter.CONTACTS_DEVICE_TAB);
                break;
            case FAVORITE:
                mRCTitleBarWithDropDownFilter.initContactsFilterWithState(RCTitleBarWithDropDownFilter.CONTACTS_FAVORITE_TAB);
                break;
        }
    }


    @Override
    public void setListAdapter(ListAdapter adapter) {
        mDragableListView.setAdapter(adapter);
        mRCMListView.setAdapter(adapter);
        super.setListAdapter(adapter);
    }


    @Override
    public void sendText(Contact contact) {

    }

    @Override
    public void call(Contact contact) {

    }

    @Override
    public void onRightButtonClicked() {

    }

    @Override
    public void updateDeleteButton(int itemsForDelete) {

    }

    @Override
    public void onRightFirstButtonClicked() {
        onEditFavorites();
    }

    @Override
    public void sendFax(Contact contact) {

    }

    @Override
    public void onLeftButtonClicked() {

    }

    @Override
    protected void queryFavorites(boolean showLoading) {

    }

    @Override
    protected FavoritesAdapter getFavoritesAdapter() {
        return (FavoritesAdapter) mFavoritesPresenter.getAdapter();
    }

    private void onEditFavorites() {
        FavoritesAdapter adapter = getFavoritesAdapter();
        if (adapter == null) {
            return;
        }

        adapter.setEditMode(true);
        adapter.setEditable(true);
        changeLeftButtonStatus(false);

        mRCMListView.setVisibility(View.GONE);
        mDragableListView.setVisibility(View.VISIBLE);
        mEditControls.setVisibility(View.VISIBLE);
        mRCTitleBarWithDropDownFilter.setRightFirstImageResVisiblility(View.GONE);
        mRCTitleBarWithDropDownFilter.hideProfile();
    }


}
