package com.example.nickgao.rcfragments;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ListFragment;

import com.example.nickgao.R;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.titlebar.DropDownItem;
import com.example.nickgao.titlebar.DropDownMenuClicked;
import com.example.nickgao.titlebar.RCBaseTitleBar;
import com.example.nickgao.titlebar.RCTitleBarWithDropDownFilter;
import com.example.nickgao.utils.RCMConstants;

import java.util.ArrayList;

/**
 * Created by nick.gao on 1/28/17.
 */

public abstract class ContactsTabFragment extends ListFragment {

    private static final String TAG = "ContactsTabFragment";
    public static final int ARG_MODE_ALL = 0;
    public static final int ARG_MODE_CLOUD = 1;

    protected RCTitleBarWithDropDownFilter mRCTitleBarWithDropDownFilter;
    protected RCBaseTitleBar mRCTitleBar;
    public Activity mActivity;


    private int mCloudContactMode = ARG_MODE_ALL;

    protected boolean isCloudContactMode() {
        return mCloudContactMode == ARG_MODE_CLOUD;
    }

    private void setArgContactLoadingMode(int mode) {
        mCloudContactMode = mode;
    }

    protected ArrayList<DropDownItem> getTopMenuListData() {
        ArrayList<DropDownItem> mTopMenuList = new ArrayList<>();
        mTopMenuList.add(new DropDownItem(RingCentralApp.getContextRC().getResources().getString(
                R.string.filter_name_all), RCTitleBarWithDropDownFilter.CONTACTS_ALL_TAB, 0));
        mTopMenuList.add(new DropDownItem(RingCentralApp.getContextRC().getResources().getString(
                R.string.filter_name_company), RCTitleBarWithDropDownFilter.CONTACTS_COMPANY_TAB, 0));
        mTopMenuList.add(new DropDownItem(RingCentralApp.getContextRC().getResources().getString(
                R.string.filter_name_personal), RCTitleBarWithDropDownFilter.CONTACTS_DEVICE_TAB, 0));

        if (!isContactSelector()) {
            mTopMenuList.add(new DropDownItem(RingCentralApp.getContextRC().getResources().getString(
                    R.string.filter_name_favorite_contacts), RCTitleBarWithDropDownFilter.CONTACTS_FAVORITE_TAB, 0));
        }
        return mTopMenuList;
    }

    protected Tabs getCurrentTab() {
        int index = mRCTitleBarWithDropDownFilter.getCurrentIndex();
        MktLog.d(TAG, "selected tab: " + index);
        switch (index) {
            case RCTitleBarWithDropDownFilter.CONTACTS_ALL_TAB:
                return Tabs.ALL;
            case RCTitleBarWithDropDownFilter.CONTACTS_COMPANY_TAB:
                return Tabs.COMPANY;
            case RCTitleBarWithDropDownFilter.CONTACTS_DEVICE_TAB:
                return Tabs.DEVICE;
            case RCTitleBarWithDropDownFilter.CONTACTS_FAVORITE_TAB:
                return Tabs.FAVORITE;
        }
        throw new IllegalStateException("invalid title bar index: " + index);
    }

    protected void initDropDownFilter(String action) {
        MktLog.d(TAG, "contacts started with action: " + action);
        if (RCMConstants.ACTION_LIST_ALL_CONTACTS.equals(action)) {
            initContactsFilterWithState(RCTitleBarWithDropDownFilter.CONTACTS_ALL_TAB);
            storeCurrentTab(Tabs.ALL);
        } else if (RCMConstants.ACTION_LIST_DEVICE_CONTACTS.equals(action)) {
            initContactsFilterWithState(RCTitleBarWithDropDownFilter.CONTACTS_DEVICE_TAB);
            storeCurrentTab(Tabs.DEVICE);
        } else if (RCMConstants.ACTION_LIST_EXTENSIONS.equals(action)) {
            initContactsFilterWithState(RCTitleBarWithDropDownFilter.CONTACTS_COMPANY_TAB);
            storeCurrentTab(Tabs.COMPANY);
        } else if (RCMConstants.ACTION_LIST_CLOUD_CONTACTS.equals(action)) {
            initContactsFilterWithState(RCTitleBarWithDropDownFilter.CONTACTS_DEVICE_TAB);
            storeCurrentTab(Tabs.DEVICE);
            setArgContactLoadingMode(ARG_MODE_CLOUD);
        } else if (RCMConstants.ACTION_LIST_FAVORITES.equals(action)) {
            initContactsFilterWithState(RCTitleBarWithDropDownFilter.CONTACTS_FAVORITE_TAB);
            storeCurrentTab(Tabs.FAVORITE);
        } else {
            initDefaultFilter();
        }
    }

    private void initDropDownFilterView() {
        mRCTitleBarWithDropDownFilter = (RCTitleBarWithDropDownFilter) mRCTitleBar;

        mRCTitleBarWithDropDownFilter.setDropDownItemList(getTopMenuListData());

        initDropDownFilter(mActivity.getIntent().getAction());

        mRCTitleBarWithDropDownFilter.setOnDropDownMenuClick(new DropDownMenuClicked() {
            @Override
            public void onDropDownMenuClicked(int index) {
                mRCTitleBarWithDropDownFilter.initContactsFilterWithState(index);
                onDropDownMenuSelected(index);
            }
        });
    }

    protected void init() {
        //initDropDownFilterView();
    }

    protected void initContactsFilterWithState(int state) {
        if (mRCTitleBarWithDropDownFilter != null) {
            mRCTitleBarWithDropDownFilter.initContactsFilterWithState(state);
        }
    }

    protected boolean isContactSelector() {
        return false;
    }

    protected void onDropDownMenuSelected(int index) {
//        switch (getCurrentTab()) {
//            case ALL:
//                logForTapAll();
//                break;
//            case COMPANY:
//                logForTapCompany();
//                break;
//            case DEVICE:
//                logForTapDevice();
//                break;
//            case FAVORITE:
//                logForTapFavorite();
//                break;
//        }
    }

    protected abstract void initDefaultFilter();

    private void storeCurrentTab(Tabs tab) {
//        if (!isContactSelector()) {
//            CurrentUserSettings.getSettings().setContactsCurrentTab(tab);
//        }
    }

    public void argumentsChanged(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                initDropDownFilter(action);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mRCTitleBarWithDropDownFilter != null) {
            mRCTitleBarWithDropDownFilter.hideDropDownDialog();
        }

        System.gc();
    }

    public enum Tabs {
        ALL {
            @Override
            public String toString() {
                return "ALL";
            }
        },
        COMPANY {
            @Override
            public String toString() {
                return "COMPANY";
            }
        },
        DEVICE {
            @Override
            public String toString() {
                return "DEVICE";
            }
        },
        FAVORITE {
            @Override
            public String toString() {
                return "FAVORITE";
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }
}
