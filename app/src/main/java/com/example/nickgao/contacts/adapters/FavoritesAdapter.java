package com.example.nickgao.contacts.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nickgao.R;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick.gao on 2/4/17.
 */

public class FavoritesAdapter extends BaseAdapter implements IFavoritesAdapter{

    private static final String TAG = "[RC] FavoritesAdapter";

    public interface FavoritesClickListener {
        void sendText(Contact contact);

        void call(Contact contact);

        void updateDeleteButton(int itemsForDelete);

        void sendFax(Contact contact);
    }


    private boolean mEditable = false;

    private ArrayList<Long> mItemsForDelete = new ArrayList<Long>();

    private FavoritesClickListener mListener;
    private Activity mActivity;
    private List<FavoriteEntity> mFavorites;
    private boolean isFaxTier = false;
    private boolean hasSendTextPermission = true;
    private boolean hasCallPermission = true;

    private FavoriteItemPresenter mFavoriteItemPresenter;

    private boolean mIsInEditMode = false;
    private boolean mIsPause = false;


    public FavoritesAdapter(Activity context, FavoritesClickListener listener) {
        mListener = listener;
        mActivity = context;
        mFavorites = new ArrayList<>();


        mFavoriteItemPresenter = new FavoriteItemPresenter(context);
    }

    @Override
    public void refresh(List<FavoriteEntity> list) {
        if (isEditMode()) {
            return;
        }

        this.mFavorites.clear();
        this.mFavorites.addAll(list);
        mFavoriteItemPresenter.refresh();
        notifyDataSetChanged();
    }

    @Override
    public FavoriteEntity getFavoriteEntity(int position) {
        return getItem(position);
    }

    @Override
    public int getFavoritesCount() {
        return getCount();
    }

    @Override
    public void updateAdapter() {
        if (!isEditMode() && !isPause()) {
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mFavorites == null ? 0 : mFavorites.size();
    }

    @Override
    public FavoriteEntity getItem(int position) {
        return mFavorites.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ContactListItemCache cache;
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.favorites_fragment_list_item_photo, parent, false);
            cache = new ContactListItemCache();
            cache.nameView = (TextView) convertView.findViewById(R.id.name);
            cache.moodView = (TextView) convertView.findViewById(R.id.mood);
            cache.photoView = (ImageView) convertView.findViewById(R.id.photo);
            cache.call = (ImageView) convertView.findViewById(R.id.callButton);
            cache.sms = (ImageView) convertView.findViewById(R.id.smsButton);
            cache.drag = (ImageView) convertView.findViewById(R.id.dragButton);
            cache.delete = (ImageView) convertView.findViewById(R.id.deleteIcon);
            cache.delBtn = convertView.findViewById(R.id.deleteButton);
            cache.presenceView = (ImageView) convertView.findViewById(R.id.presence_imageview);
            cache.spaceView = convertView.findViewById(R.id.place_without_divider);
            cache.clickableControlsContainer = convertView.findViewById(R.id.clickable_controls_container);
            cache.fax = (ImageView) convertView.findViewById(R.id.faxButton);
        } else {
            cache = (ContactListItemCache) convertView.getTag();
        }


        FavoriteEntity entity = getItem(position);
        long key = entity.id;
        final long contactId = entity.contactId;
        Contact.ContactType contactType = entity.contactType;
        final Contact contact = entity.contact;
        final String name = contact.getDisplayName();
        cache.nameView.setText(name);

        cache.call.setVisibility(View.GONE);
        cache.sms.setVisibility(View.GONE);
        cache.fax.setVisibility(View.GONE);

        cache.call.setImageResource(R.drawable.list_item_call_button_selector);
        cache.presenceView.setVisibility(View.GONE);
        cache.moodView.setVisibility(View.GONE);

        mFavoriteItemPresenter.setPhotoView(cache.photoView, contact);

        if (mEditable) {
            if (cache.delete.getVisibility() != View.VISIBLE) {
                cache.delete.setVisibility(View.VISIBLE);
            }
            if (cache.call.getVisibility() != View.GONE) {
                cache.call.setVisibility(View.GONE);
            }
            if (cache.sms.getVisibility() != View.GONE) {
                cache.sms.setVisibility(View.GONE);
            }

            if (cache.fax.getVisibility() != View.GONE) {
                cache.fax.setVisibility(View.GONE);
            }

            if (cache.delBtn.getVisibility() != View.GONE) {
                cache.delBtn.setVisibility(View.GONE);
            }

            if (mItemsForDelete.contains(key)) {
                cache.delete.setImageResource(R.drawable.icon_checkbox_checked);
            } else {
                cache.delete.setImageResource(R.drawable.icon_checkbox_unchecked);
            }
            setDelBtnListener(cache.clickableControlsContainer, key);

        } else {
            if (cache.drag.getVisibility() != View.GONE) {
                cache.drag.setVisibility(View.GONE);
            }

            if (cache.delete.getVisibility() != View.GONE) {
                cache.delete.setVisibility(View.GONE);
            }

            if (cache.delBtn.getVisibility() != View.GONE) {
                cache.delBtn.setVisibility(View.GONE);
            }
        }

        convertView.setTag(cache);
        return convertView;
    }

    @Override
    public void setPause(boolean isPause) {
        mIsPause = isPause;
    }

    @Override
    public boolean isPause() {
        return mIsPause;
    }

    @Override
    public boolean isEditMode() {
        return mIsInEditMode;
    }

    public void setEditMode(boolean isEditMode) {
        this.mIsInEditMode = isEditMode;
    }

    private void setDelBtnListener(final View clickableControlsContainer, final long key) {
        clickableControlsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemsForDelete.contains(key)) {
                    mItemsForDelete.remove(key);
                } else {
                    mItemsForDelete.add(key);
                }

                updateDeleteButton();
                notifyDataSetChanged();
            }
        });
    }

    public void setEditable(boolean value) {
        mEditable = value;
        mItemsForDelete.clear();
        updateDeleteButton();
        this.notifyDataSetChanged();
    }

    public boolean getEditable() {
        return this.mEditable;
    }

    private void updateDeleteButton() {
        if (mListener != null) {
            mListener.updateDeleteButton(mItemsForDelete.size());
        }
    }

    public ArrayList<Long> getSelectedItems() {
        ArrayList<Long> items = new ArrayList<Long>();
        for (Long key : mItemsForDelete) {
            items.add(key);
        }

        for (Long item : items) {
            mItemsForDelete.remove(item);
        }

        updateDeleteButton();
        return items;
    }

    private boolean onDelete(long key) {
        int position = getPosition(key);
        if (position != -1) {
            mFavorites.remove(position);
            return true;
        }
        return false;
    }

    private int getPosition(long key) {
        int position = -1;
        if (mFavorites == null) {
            return -1;
        }
        for (int i = 0; i < mFavorites.size(); i++) {
            if (key == mFavorites.get(i).id) {
                position = i;
                break;
            }
        }
        return position;
    }

    /**
     * Item view cache holder.
     */
    public static final class ContactListItemCache {

        /**
         * Person name view.
         */
        public TextView nameView;

        public TextView moodView;

        /**
         * Person photo cache holder.
         */
        public ImageView photoView;

        public ImageView call;

        public ImageView sms;

        public ImageView delete;

        public ImageView drag;

        public View delBtn;

        public ImageView presenceView;

        public View spaceView;

        public View clickableControlsContainer;

        public ImageView fax;
    }

    public void drop(int from, int to) {
        if (from != to) {
            FavoriteEntity fromEntity = mFavorites.get(from);
            mFavorites.remove(from);
            mFavorites.add(to, fromEntity);
            this.notifyDataSetChanged();
        }
    }

    public void remove(int position) {
        if (mFavorites != null && mFavorites.size() > position) {
            mFavorites.remove(position);
            notifyDataSetChanged();
        }
    }

}
