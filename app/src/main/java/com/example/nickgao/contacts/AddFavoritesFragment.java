package com.example.nickgao.contacts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nickgao.R;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.rcfragments.ContactsFragment;
import com.example.nickgao.utils.widget.SearchBarView;

/**
 * Created by nick.gao on 1/31/17.
 */

public class AddFavoritesFragment extends ContactsFragment {

    private static final String TAG = "[RC]AddFavoritesFragment";
    private Toast mToast;

    public static Fragment newInstance() {
        return new AddFavoritesFragment();
    }

    public AddFavoritesFragment() {
    }

    @Override
    protected boolean isContactSelector() {
        return true;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mToast = Toast.makeText(mActivity, R.string.toast_no_phone_number_add_to_favorite, Toast.LENGTH_LONG);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRCTitleBarWithDropDownFilter.setLeftImageVisible();
        mRCTitleBarWithDropDownFilter.setRightVisibility(View.INVISIBLE);
        mRCTitleBarWithDropDownFilter.setRightImageVisibility(View.GONE);
        mRCTitleBarWithDropDownFilter.setEnabled(true);
        mRCTitleBarWithDropDownFilter.setButtonsClickCallback(this);
        mShouldDismissSavedState = true;
    }

    protected void initializeListHeader() {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        mSearchBar = (SearchBarView) inflater.inflate(R.layout.search_bar_view, null);
        mSearchBar.setSearchHandler(new SearchBarView.SearchHandler() {
            @Override
            public void search(String filter) {
                reloadTheData();
            }
        });
        getListView().addHeaderView(mSearchBar);
    }

    @Override
    protected void updateCurrentTab() {
        Tabs currentTab = getCurrentTab();
        mRCTitleBarWithDropDownFilter.initAddFavoritesFilterWithState(currentTab.ordinal());
    }


    protected void checkToHeadBarStatus() {
        switch (getCurrentTab()) {
            case ALL:
            case DEVICE:
            case COMPANY:
                mRCTitleBar.setRightFirstImageResVisiblility(View.GONE);
                mRCTitleBar.setRightImageVisibility(View.GONE);
                break;
        }
    }

    private void finish() {
        mToast.cancel();
        mActivity.finish();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Contact contact = getDisplayContact(position);
        if (contact != null) {
            boolean result=ContactOperator.getContactsOperator(contact, mActivity).toggleFavoriteFromAddFavorite(
                    null,
                    null
            );
            if(result) {
                getActivity().finish();
            }
        }
        //TODO may be use other flurry types
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    }

    @Override
    public void onLeftButtonClicked() {
        finish();
    }
}
