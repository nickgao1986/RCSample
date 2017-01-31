package com.example.nickgao.rcfragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.nickgao.R;
import com.example.nickgao.contacts.ContactOperator;
import com.example.nickgao.contacts.adapters.contactsprovider.CombinedContactsAdapter;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactEditActivity;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactListItem;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactsProvider;
import com.example.nickgao.contacts.adapters.contactsprovider.DisplayContactsProviderUtils;
import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.eventdetail.CommonEventDetailActivity;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcfragments.presents.FavoritesHorizontalListView;
import com.example.nickgao.rcfragments.presents.FavoritesInContactsPresenter;
import com.example.nickgao.rcfragments.presents.FavoritesPresenter;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.titlebar.DropDownItem;
import com.example.nickgao.titlebar.RCMainTitleBar;
import com.example.nickgao.titlebar.RCTitleBarWithDropDownFilter;
import com.example.nickgao.titlebar.RightDropDownManager;
import com.example.nickgao.utils.RCMConstants;
import com.example.nickgao.utils.widget.RightDropDownDialog;
import com.example.nickgao.utils.widget.SearchBarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nick.gao on 1/28/17.
 */

public class ContactsFragment extends BaseContactsFragment implements RCMainTitleBar.HeaderClickListener {

    private static final String TAG = "[RC]ContactsFragment";

    protected BaseAdapter mAdapter;


    protected boolean isCreateNewTextMessage;


    protected boolean mShouldDismissSavedState;

    private InputMethodManager mInputMethodManager;

    private List<ContactListItem> mContactsData;
    private AsynchContactsLoader mCurrentLoader;

    private View noContactIndication;
    private View noContactPermissionContainer;
    private View turnOnContactPermissionButton;
    private boolean shouldShowPermissionRational;
    protected int mSelectorType;
    private static int mLastListViewPosition = 0;
    private Map<String, Integer> mUdateImageContactMap = new HashMap<>();

//    protected FavoritesPresenter mFavoritesPresenter;
//    protected FavoritesHorizontalListView mFavoritesHorizontalListView;

    private View mSearchBarDivider;

    private RightDropDownManager mDropdownMenu;
    private final int MENU_LIST_NEW_CONTACT = 0;
    private final int MENU_LIST_NEW_FAVORITE = 1;


    private SearchBarClearReceiver mSearchBarClearReceiver;
    protected SearchBarView mSearchBar;  // search controls
    protected FavoritesPresenter mFavoritesPresenter;
    protected FavoritesHorizontalListView mFavoritesHorizontalListView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mCloudContactMode = getArguments().getInt(ARG_CONTACT_LOADING_MODE, ARG_MODE_CLOUD);
        isCreateNewTextMessage = false;

//        if (mActivity != null && mActivity.getIntent() != null) {
//            mSelectorType = mActivity.getIntent().getIntExtra(RCMConstants.EXTRA_CONTACT_SELECTOR_TYPE, ContactSelectorFragment.CONTACT_SELECTOR_TYPE_DEFAULT);
//        } else {
//            mSelectorType = ContactSelectorFragment.CONTACT_SELECTOR_TYPE_DEFAULT;
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_list_content_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRCTitleBar = (RCTitleBarWithDropDownFilter) view.findViewById(R.id.contacts_top_title);

        initCommon(view);

        mRCTitleBar.setRightImageRes(R.drawable.ic_action_plus);
        mRCTitleBar.setRightFirstImageRes(R.drawable.ic_action_filter);

        mRCTitleBar.setButtonsClickCallback(this);

        mContactsData = new ArrayList<>();
        mAdapter = new CombinedContactsAdapter(mActivity, mContactsData);

        initializeListHeader();

        setListAdapter(mAdapter);

        getListView().setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return hideSoftInputFromWindow();
            }
        });

        //AB-19034
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
                    Log.e(TAG, "scroll state: SCROLL_STATE_TOUCH_SCROLL");
                    View currentFocus = getActivity().getCurrentFocus();
                    if (currentFocus != null) {
                        currentFocus.clearFocus();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //no implementation
            }
        });


        mInputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        noContactIndication = view.findViewById(R.id.no_contact_indication);
        noContactPermissionContainer = view.findViewById(R.id.no_contact_permission_indication);
        turnOnContactPermissionButton = view.findViewById(R.id.turn_on_contacts_permission);
        turnOnContactPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                shouldShowPermissionRational = ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_CONTACTS);
//                mActivity.requestContactPermission();
            }
        });

        mShouldDismissSavedState = mActivity.getIntent() != null && (mActivity.getIntent().hasExtra(RCMConstants.EXTRA_CALL_CALLER_ID) || mActivity.getIntent().hasExtra(RCMConstants.EXTRA_CONTACT_SELECTOR_TYPE));
        if (!mShouldDismissSavedState) {
            updateCurrentTab();
        }
    }

    private boolean hideSoftInputFromWindow() {

        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && mActivity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
        return false;
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onResume();
        } else {
            if (mDropdownMenu != null) {
                mDropdownMenu.dismissDialog();
            }
            onPause();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        MktLog.d(TAG, "onPause()...");

        //saveListViewPosition();

    }


    protected void saveState() {
        MktLog.i(TAG, "saveState()");

//        if (!mShouldDismissSavedState) {
//            CurrentUserSettings.getSettings().setContactsCurrentTab(getCurrentTab());
//        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }


    protected DisplayContactsProviderUtils.ContactsAdapterDataContainer loadContact(boolean showDevice, boolean showCompany, boolean showPersonal, String mFilter, String... params) {
        List<Contact> contacts = ContactsProvider.getInstance().loadContacts(showDevice, showCompany, showPersonal, true, true, mFilter);
        return TextUtils.isEmpty(mFilter)
                ? DisplayContactsProviderUtils.getContactsAdapterDataWithSections(contacts)
                : DisplayContactsProviderUtils.getContactsAdapterData(contacts);
    }


    private class AsynchContactsLoader extends AsyncTask<String, Void, DisplayContactsProviderUtils.ContactsAdapterDataContainer> {

        private final boolean mShowDevice;
        private final boolean mShowCompany;
        private final boolean mShowPersonal;
        private final String mFilter;

        public AsynchContactsLoader(boolean showDevice, boolean showCompany, boolean showPersonal, String filter) {
            mShowDevice = showDevice;
            mShowCompany = showCompany;
            mShowPersonal = showPersonal;
            MktLog.i(TAG, "====mShowDevice=" + mShowDevice + "mShowCompany=" + mShowCompany + "mShowPersonal=" + mShowPersonal);
            mFilter = filter;
        }

        @Override
        protected void onPreExecute() {
            updateLoading();
        }

        @Override
        protected void onPostExecute(DisplayContactsProviderUtils.ContactsAdapterDataContainer adapterData) {
            MktLog.d(TAG, "loaded: " + adapterData.toString());

            //if (!isVisible()) {
            //    MktLog.d(TAG, "the fragment is already detached");
            //    return; //according to loading process can be invoked when the fragment is already in detached state
            //}
            //isVisible = false is not really detached state, change it to isDetached condition.
            if (isDetached() || (mActivity != null && mActivity.isFinishing()) || getView() == null || getListView() == null) {
                MktLog.d(TAG, "the fragment is already detached or the main activity is finish or listview is not initialize");
                return; //according to loading process can be invoked when the fragment is already in detached state
            }

            mLoadingBar.setVisibility(View.GONE);

            if (adapterData.getContacts().size() == 0) {
                mEmptyIndicateView.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(mFilter)) {
                    mEmptyIndicateView.setText(getText(R.string.noContacts));
                } else {
                    mEmptyIndicateView.setText(getText(R.string.messages_no_results_found));
                }
                getListView().setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            } else {
                mEmptyIndicateView.setVisibility(View.GONE);
                getListView().setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
            }

            mContactsData.clear();
            MktLog.d(TAG, "Loaded contacts; " + adapterData);
            mContactsData.addAll(adapterData.getContacts());
//            processPermissionControls(TextUtils.isEmpty(mFilter) && adapterData.getContacts().size() == 0);
            ((CombinedContactsAdapter) mAdapter).updateSections(
                    adapterData.getSectionToPosition(),
                    adapterData.getPositionToSection(),
                    adapterData.getSections());
//            ((CombinedContactsAdapter) mAdapter).updateViewMode(getCurrentTab());
            mAdapter.notifyDataSetChanged();
//            mActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    restoreListViewPosition();
//                }
//            });
        }

        @Override
        protected DisplayContactsProviderUtils.ContactsAdapterDataContainer doInBackground(String... params) {
            MktLog.d(TAG, "loading : " + (mShowDevice ? "device " : "") + (mShowCompany ? "company " : "") + (mShowPersonal ? "personal" : ""));
            if (params.length > 0) {
                throw new IllegalStateException("parameters should be empty");
            }

            return loadContact(mShowDevice, mShowCompany, mShowPersonal, mFilter, params);
        }

        private void updateLoading() {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isDetached()) {
                        MktLog.d(TAG, "the fragment is already detached");
                        return; //according to loading process can be invoked when the fragment is already in detached state
                    }
                    if (getStatus() != Status.FINISHED) {
                        mEmptyIndicateView.setVisibility(View.GONE);
                        mLoadingBar.setVisibility(View.VISIBLE);
                    }
                }
            }, SHOW_PROGRESS_DELAY);
        }
    }

    @Override
    protected void reloadTheData() {
        reloadContacts(null);
    }

    private void reloadContacts(String filter) {
        if (mCurrentLoader != null) {
            mCurrentLoader.cancel(false);
        }
        Tabs currentTab = getCurrentTab();
        if (currentTab == Tabs.FAVORITE) {
//            mRCMainInterface.switchToFavorites(new Intent(RCMConstants.ACTION_LIST_FAVORITES));
        } else {
            mCurrentLoader = new AsynchContactsLoader(
                    !isCloudContactMode() && ((currentTab == Tabs.DEVICE || currentTab == Tabs.ALL) && getDeviceContactsPermission()),
                    !isCloudContactMode() && (currentTab == Tabs.COMPANY || currentTab == Tabs.ALL),
                    isCloudContactMode() || (currentTab == Tabs.DEVICE || currentTab == Tabs.ALL),
                    filter);
            mCurrentLoader.execute();
        }
    }

    protected boolean getDeviceContactsPermission() {
        return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (mActivity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED));
    }

    @Override
    public void onRightButtonClicked() {
        Tabs tab = getCurrentTab();
        switch (tab) {
            case ALL:
                showDropMenu();
                return;
            case DEVICE:
                tapAddToNewContact();
                return;
        }

        throw new IllegalStateException("invalid tab: " + tab);
    }

    private void showDropMenu() {
        ArrayList<DropDownItem> mTopMenuList = new ArrayList<>();
        mTopMenuList.add(new DropDownItem(RingCentralApp.getContextRC().getResources().getString(
                R.string.menu_list_new_contact), MENU_LIST_NEW_CONTACT, 0));
        mTopMenuList.add(new DropDownItem(RingCentralApp.getContextRC().getResources().getString(
                R.string.menu_list_new_favorite), MENU_LIST_NEW_FAVORITE, 0));

        mDropdownMenu.setDropDownItemList(mTopMenuList);
        mDropdownMenu.showDialog(true, null);

    }


    @Override
    public void onRightFirstButtonClicked() {

    }

    @Override
    public void onLeftButtonClicked() {

    }

    protected void initializeListHeader() {

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.contacts_search_bar_view, null);
        mSearchBar = (SearchBarView) view.findViewById(R.id.contacts_list_search_bar);
        mSearchBar.setSearchHandler(new SearchBarView.SearchHandler() {
            @Override
            public void search(String filter) {
                if (mSearchBarDivider != null) {
                    mSearchBarDivider.setVisibility(TextUtils.isEmpty(filter) ? View.INVISIBLE : View.VISIBLE);
                }
                resetListViewPosition();
                reloadContacts(filter);
            }
        });

        initFavoritesHorizontalListView(view);

        initFavoritePresenter();
        mSearchBarDivider = view.findViewById(R.id.search_bar_divider);

        if (getListView() != null) {
            getListView().addHeaderView(view, null, false);
        }



        mDropdownMenu = new RightDropDownManager(mActivity);
        mDropdownMenu.setDropDownClickListener(new RightDropDownDialog.OnDropdownClickListener() {

            @Override
            public void onHide() {

            }

            @Override
            public void onClickItem(int index) {
                switch (index) {
                    case MENU_LIST_NEW_CONTACT:
                        tapAddToNewContact();
                        break;
                    case MENU_LIST_NEW_FAVORITE:
                       // tapAddToCreateNewFavorite(FlurryTypes.ALL);
                        break;
                }
            }
        });

    }

    protected void initFavoritesHorizontalListView(LinearLayout view) {
        mFavoritesHorizontalListView = new FavoritesHorizontalListView(this, (RecyclerView) view.findViewById(R.id.fav_list_view), view.findViewById(R.id.fav_list_empty_view));
    }

    protected void initFavoritePresenter() {
        mFavoritesPresenter = new FavoritesInContactsPresenter(this.getContext(), mFavoritesHorizontalListView);
    }


    protected void resetListViewPosition() {
//        if (mActivity instanceof RingCentralMain) {
//            mLastListViewPosition = 0;
//            getListView().setSelection(mLastListViewPosition);
//        } else {
//            getListView().setSelection(0);
//        }
    }

    protected void saveListViewPosition() {
//        if (mActivity instanceof RingCentralMain) {
//            mLastListViewPosition = getListView().getFirstVisiblePosition();
//        }
    }



    protected void tapAddToNewContact() {
        startActivity(new Intent(this.getContext(), ContactEditActivity.class));
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 0) {
            return;
        }
        Contact contact = getDisplayContact(position);
        if (contact == null) {
            return;
        }
        ContactOperator.getContactsOperator(contact, mActivity)
                .viewDetails(
                        CommonEventDetailActivity.VIEW_PERSONAL_CONTACT,
                        null,
                        RCMConstants.COMPANY_FROM_CONTACT_LIST);



    }

    private class SearchBarClearReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

}