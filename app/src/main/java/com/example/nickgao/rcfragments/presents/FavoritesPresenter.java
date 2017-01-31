package com.example.nickgao.rcfragments.presents;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.nickgao.contacts.PersonalFavorites;
import com.example.nickgao.contacts.adapters.FavoriteEntity;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactsProvider;
import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.database.RCMDataStore;
import com.example.nickgao.database.RCMProvider;
import com.example.nickgao.database.UriHelper;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick.gao on 1/31/17.
 */

public class FavoritesPresenter {

    protected static final String TAG = "FavoritesPresenter";

    protected Context mContext;
    protected IFavoritesAdapter mFavoritesAdapter;
    private QueryTask mQueryTask;

    protected IFavoritesView mFavoritesListView;

    public FavoritesPresenter(Context context, IFavoritesView favoritesView, IFavoritesAdapter favoritesAdapter) {
        mContext = context;
        setFavoritesView(favoritesView, favoritesAdapter);
//        mContext.getApplicationContext().bindService(new Intent(mContext, LocalSyncService.class), mSyncSvcConn, Activity.BIND_AUTO_CREATE);
//        if (PermissionControl.getInstance().hasReadPresenceStatusPermission(mContext)) {
//            mPresenceRefresher = new FavoritePresencesRefresher(mContext);
//            mPresenceDataStore = PresenceDataStore.getInstance();
//            mPresenceChangeListener = new PresenceDataStore.PresenceChangeListener() {
//                public void onChanged() {
//                    if (mFavoritesAdapter != null) {
//                        mFavoritesAdapter.updateAdapter();
//                    }
//                }
//            };
//            mPresenceDataStore.addOnChangeListener(mPresenceChangeListener);
//        }
    }

    public void setFavoritesView(IFavoritesView favoritesView, IFavoritesAdapter favoritesAdapter) {
        mFavoritesListView = favoritesView;
        mFavoritesAdapter = favoritesAdapter;
        mFavoritesListView.setAdapter(favoritesAdapter);
    }

    private boolean isReady() {
        return mFavoritesAdapter != null && !mFavoritesAdapter.isPause();
    }

    private void setPause(boolean isPause) {
        if (mFavoritesAdapter != null) {
            mFavoritesAdapter.setPause(isPause);
        }
    }

    private class ContactChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isReady()) {
                startQuery(false);
            }
        }
    }

    public void resume(boolean isShowLoading) {
        setPause(false);


        startQuery(isShowLoading);
    }

    public void pause() {
        setPause(true);
        mFavoritesListView.deselectContact();
    }


    public void startQuery(boolean showLoading) {
        if (!isReady()) {
            return;
        }

        if (mQueryTask == null) {
            mQueryTask = new QueryTask(showLoading);
        } else {
            mQueryTask.cancel(true);
            mQueryTask = new QueryTask(showLoading);
        }
        mQueryTask.execute("");
    }

    public void start() {
//        try {
//            mContactChangeReceiver = new ContactChangeReceiver();
//            mContext.registerReceiver(mContactChangeReceiver, new IntentFilter(RCMConstants.ACTION_UI_CONTACT_CHANGED));
//
//            mFavoriteSyncChangeReceiver = new FavoriteSyncChangeReceiver();
//            mContext.registerReceiver(mFavoriteSyncChangeReceiver, new IntentFilter(RCMConstants.ACTION_CLOUD_FAVORITE_CHANGED));
//
//            mContactPhotoChangeReceiver = new ContactPhotoChangeReceiver();
//            mContext.registerReceiver(mContactPhotoChangeReceiver, new IntentFilter(RCMConstants.ACTION_PHOTO_CHANGED));
//        } catch (Throwable th) {
//              MktLog.e(TAG, th);
//        }
    }



    protected List<FavoriteEntity> loadFavoriteData(String... params) {
        List<FavoriteEntity> list = new ArrayList<>();

        try {
            String selection = " ( " + RCMDataStore.CloudFavoritesTable.SYNC_STATUS + " <> " + RCMDataStore.CloudFavoriteSyncStatus.UNKNOWN.ordinal()
                    + " AND "
                    + RCMDataStore.CloudFavoritesTable.SYNC_STATUS + " <> " + RCMDataStore.CloudFavoriteSyncStatus.Deleted.ordinal()
                    + " ) ";

            if (DeviceUtils.isContainOrAboveAndroidOS6_0() && mContext.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                selection = selection + " AND " + RCMDataStore.CloudFavoritesTable.CONTACT_TYPE + " <> " + PersonalFavorites.matchToFavoriteDBContactType(Contact.ContactType.DEVICE);
            }

            Cursor cursor = mContext.getContentResolver().query(UriHelper.getUri(RCMProvider.CLOUD_FAVORITES, CurrentUserSettings.getSettings().getCurrentMailboxId()),
                    PersonalFavorites.PROJECTION,
                    selection,
                    null,
                    RCMDataStore.CloudFavoritesTable.RCM_SORT);
            while (cursor != null && cursor.moveToNext()) {
                FavoriteEntity entity = new FavoriteEntity();
                entity.id = cursor.getLong(PersonalFavorites._ID_INDX);
                entity.contactId = cursor.getLong(PersonalFavorites.CONTACT_ID_INDX);
                entity.contactType = PersonalFavorites.matchToContactType(cursor.getInt(PersonalFavorites.CONTACT_TYPE_INDX));
                entity.contact = ContactsProvider.getInstance().getContact(entity.contactType, entity.contactId, true);
                if (entity.contact != null) {
                    list.add(entity);
                }
            }
        } catch (Exception e) {
            MktLog.e(TAG, "Fail to load favorites", e);
        }

        return list;
    }


    private class QueryTask extends AsyncTask<String, String, List<FavoriteEntity>> {
        private boolean mShowLoading;

        private QueryTask(boolean show) {
            mShowLoading = show;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mFavoritesListView.setLoading(mShowLoading);
        }

        @Override
        protected List<FavoriteEntity> doInBackground(String... params) {
            return loadFavoriteData(params);
        }

        @Override
        protected void onPostExecute(List<FavoriteEntity> favoriteEntities) {
            super.onPostExecute(favoriteEntities);
            mFavoritesListView.setLoading(false);
            //AB-10551 [Once]App crash after tap "Add Contact" in Favorites screen, add "mAdapter == null"
            if (mFavoritesAdapter == null) {
                return;
            }

            if (mFavoritesListView.isUIReady()) {

                if (favoriteEntities.size() == 0) {
                    mFavoritesListView.showEmpty();
                } else {
                    mFavoritesListView.hideEmpty();
                }

                mFavoritesAdapter.refresh(favoriteEntities);
            }
        }
    }
}
