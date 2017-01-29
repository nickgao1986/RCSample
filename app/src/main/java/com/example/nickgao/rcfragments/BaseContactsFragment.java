package com.example.nickgao.rcfragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nickgao.R;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactListItem;
import com.example.nickgao.logging.MktLog;

/**
 * Created by nick.gao on 1/28/17.
 */

public abstract class BaseContactsFragment extends ContactsTabFragment {

    private static final String TAG = "[RC] BaseContactsFragment";
    protected TextView mEmptyIndicateView;
    protected ProgressBar mLoadingBar;

    private class ContactObserver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            reloadTheData();
        }
    }

    private ContactObserver mContactObserver = new ContactObserver();

    protected static final int SHOW_PROGRESS_DELAY = 300;

    protected boolean mNeedReload;


    protected final void initCommon(View rootView) {
        super.init();
        mEmptyIndicateView = (TextView) rootView.findViewById(R.id.emptyListText);
        mLoadingBar = (ProgressBar) rootView.findViewById(R.id.loading);
        mNeedReload = true;
    }


    protected abstract void reloadTheData();


    protected Contact getDisplayContact(int position) {
        ContactListItem ret = (ContactListItem) getListView().getItemAtPosition(position);
        MktLog.d(TAG, "getDisplayContact position: " + position + "  " + ret);
        return ret != null ? ret.getDisplayContact() : null;
    }

    protected void tabChangedManually() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isHidden()) {
            return;
        }
        if (mNeedReload) {
            getListView().clearTextFilter();
            reloadTheData();
        }

        mNeedReload = false;
    }

    @Override
    protected void initDefaultFilter() {

    }


    @Override
    protected void onDropDownMenuSelected(int index) {
        super.onDropDownMenuSelected(index);

        boolean needReload = true;
//        if (getCurrentTab() == Tabs.FAVORITE) {
//            logForTapFavorite();
//            mRCMainInterface.switchToFavorites(new Intent(RCMConstants.ACTION_LIST_FAVORITES));
//            needReload = false;
//        }

        tabChangedManually();
        getListView().setSelection(0);
        if (needReload) {
            reloadTheData();
        }
    }


}
