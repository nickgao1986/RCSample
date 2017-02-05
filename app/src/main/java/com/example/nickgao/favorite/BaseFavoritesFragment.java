package com.example.nickgao.favorite;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.nickgao.R;
import com.example.nickgao.contacts.PersonalFavorites;
import com.example.nickgao.contacts.adapters.FavoritesAdapter;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactsProvider;
import com.example.nickgao.contacts.adapters.contactsprovider.DeviceContact;
import com.example.nickgao.contacts.adapters.contactsprovider.Projections;
import com.example.nickgao.logging.EngLog;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.rcfragments.ContactsTabFragment;
import com.example.nickgao.titlebar.RCTitleBarWithDropDownFilter;
import com.example.nickgao.utils.EmailSelectionDialog;
import com.example.nickgao.utils.EmailSender;

import java.util.List;

/**
 * Created by nick.gao on 2/3/17.
 */

public abstract class BaseFavoritesFragment extends ContactsTabFragment {

    private static final String TAG = "[RC] BaseFavoritesFragment";
    protected TextView mEmptyIndicateView;
    protected View mLoadingBar;

    private View mFavoriteListContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (LogSettings.ENGINEERING) {
            EngLog.i(TAG, "Bind to local sync service");
        }
    }

    /**
     * Called on Restart
     */
//    @Override
//    public void onFragmentRestart() {
//        super.onFragmentRestart();
//        startQuery(true);
//    }

    @Override
    protected void onDropDownMenuSelected(int index) {
        super.onDropDownMenuSelected(index);

        switch (getCurrentTab()) {
            case ALL:
             //   mRCMainInterface.switchToContacts(new Intent(RCMConstants.ACTION_LIST_ALL_CONTACTS));
                break;
            case COMPANY:
            //   mRCMainInterface.switchToContacts(new Intent(RCMConstants.ACTION_LIST_EXTENSIONS));
                break;
            case DEVICE:
            //    mRCMainInterface.switchToContacts(new Intent(RCMConstants.ACTION_LIST_DEVICE_CONTACTS));
                break;
        }
    }

    @Override
    protected void initDefaultFilter() {
        initContactsFilterWithState(RCTitleBarWithDropDownFilter.CONTACTS_FAVORITE_TAB);
    }

    protected final void initCommon(View rootView) {
        super.init();
        mEmptyIndicateView = (TextView) rootView.findViewById(R.id.emptyListText);
        mLoadingBar = rootView.findViewById(R.id.loading);
        mFavoriteListContainer = rootView.findViewById(R.id.favorites_list_container);
        /**
         * Set the proper empty string
         */
        setEmptyText();

        registerForContextMenu(getListView());
    }

    /**
     * Process querying data in multiple modes
     */
    protected void startQuery(boolean showLoading) {
        queryFavorites(showLoading);
    }


    protected void setEmptyText() {
        mEmptyIndicateView.setText(getText(R.string.noContacts));
        mEmptyIndicateView.setGravity(Gravity.CENTER);
    }

    /**
     * Set Default text if Contacts are empty
     */
    protected void setLoading(final boolean enable) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isDetached()) {
                    return;
                }
                if (enable) {
                    mEmptyIndicateView.setVisibility(View.GONE);
                    mFavoriteListContainer.setVisibility(View.GONE);
                    mLoadingBar.setVisibility(View.VISIBLE);
                } else {
                    mLoadingBar.setVisibility(View.GONE);
                    mFavoriteListContainer.setVisibility(View.VISIBLE);
                    mEmptyIndicateView.setVisibility(View.GONE);
                }
            }
        });

    }




    /**
     * Query under personal mode
     */
    protected abstract void queryFavorites(boolean showLoading);


    /**
     * Get Favorite Adapter instance
     */
    protected abstract FavoritesAdapter getFavoritesAdapter();


    /**
     * Switch to RingOut page and insert Extension Number. If we have not one number -> call Selection
     */
    protected boolean sendEmail(Cursor cursor, long personalContactId) {
        Contact.ContactType contactType = PersonalFavorites.matchToContactType(cursor.getInt(PersonalFavorites.CONTACT_TYPE_INDX));
        if (Contact.ContactType.CLOUD_COMPANY == contactType) {
            return sendExtensionEmail(cursor);
        } else {
            return sendPersonalEmail(personalContactId);
        }
    }

    private boolean sendExtensionEmail(Cursor cursor) {
        String email = cursor.getString(Projections.Extensions.EXT_EMAIL_COLUMN_INDEX);
        EmailSender emailSender = new EmailSender(mActivity);
        String[] to = {email};
        emailSender.sendEmail(to, "", "", null);
        return true;
    }

    private boolean sendPersonalEmail(long contactId) {
        if (contactId == -1) {
            return false;
        }

        DeviceContact deviceContact = (DeviceContact) ContactsProvider.getInstance().getContact(Contact.ContactType.DEVICE, contactId, true);
        if (deviceContact != null) {
            List<Contact.TypeValue> emails = deviceContact.getEmailAddressList();
            if (emails.size() == 0) {
               // signalError("No email address for this record");
                return false;
            } else if (emails.size() == 1) {
                EmailSender emailSender = new EmailSender(mActivity);
                String[] to = {emails.get(0).getValue()};
                emailSender.sendEmail(to, "", "", null);
            } else {
                EmailSelectionDialog emailDialog = new EmailSelectionDialog(mActivity, Contact.ContactType.DEVICE, emails);
                emailDialog.show();
            }
            return true;
        }

        return false;
    }


}
