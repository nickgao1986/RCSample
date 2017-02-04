package com.example.nickgao.contacts.adapters;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nickgao.R;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by nick.gao on 1/31/17.
 */

public class FavInContactsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IFavoritesAdapter {

    public interface FavoritesViewDelegate {
        void itemClick(View v, int position, Contact contact);

        void requestAddingFavorite();

        void selectedContactChanged(Contact contact);
    }



    protected boolean mEditable = false;
    protected boolean mIsPause = false;

    protected FavoritesViewDelegate mFavoritesViewDelegate;
    protected Context mContext;
    protected List<FavoriteEntity> mFavorites = new ArrayList<>();

    protected FavoriteItemPresenter mFavoriteItemPresenter;
    protected Contact mSelectedContact = null;

    public FavInContactsListAdapter(Context context, FavoritesViewDelegate delegate) {
        mContext = context;
        mFavoritesViewDelegate = delegate;

        mFavoriteItemPresenter = new FavoriteItemPresenter(context);
    }

    public void setSelectedContact(Contact contact) {
        this.mSelectedContact = contact;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_in_contact_list_item, parent, false);
        FavoriteViewHolder favoriteViewHolder = new FavoriteViewHolder(view);
        return favoriteViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FavoriteViewHolder cache = (FavoriteViewHolder) holder;
        boolean isLastItem = (position == getItemCount() - 1);
        cache.setAddNewFavorite(isLastItem);
        if (isLastItem) {
            cache.addNewFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFavoritesViewDelegate.requestAddingFavorite();
                }
            });
            return;
        }

        bindViewHolderWithoutLastItem(holder, position);
    }

    @Override
    public int getItemCount() {
        return mFavorites == null ? 0 : mFavorites.size();
    }

    @Override
    public void refresh(List<FavoriteEntity> list) {
        this.mFavorites.clear();
        this.mFavorites.addAll(list);
        addEmptyFavoriteEntity(list);
        mFavoriteItemPresenter.refresh();

        verifySelectedContact(list);

        notifyDataSetChanged();
    }

    protected void addEmptyFavoriteEntity(List<FavoriteEntity> list) {
        if (!list.isEmpty()) {
            //append action add
            this.mFavorites.add(new FavoriteEntity());
        }
    }

    private void verifySelectedContact(List<FavoriteEntity> favoriteEntities) {
        Contact selectedContact = getSelectedContact();
        if (selectedContact != null) {
            Contact item;
            boolean isVerified = false;
            for (int i = 0; i < favoriteEntities.size(); i++) {
                item = favoriteEntities.get(i).contact;
                if (item != null && item.getId() == selectedContact.getId()) {
                    setSelectedContact(item);
                    mFavoritesViewDelegate.selectedContactChanged(item);
                    isVerified = true;
                    break;
                }
            }
            if (!isVerified) {
                mFavoritesViewDelegate.selectedContactChanged(null);
            }
        }
    }


    @Override
    public FavoriteEntity getFavoriteEntity(int position) {
        return (position < mFavorites.size()) ? mFavorites.get(position) : null;

    }

    @Override
    public int getFavoritesCount() {
        return getItemCount();
    }

    @Override
    public void updateAdapter() {
        this.notifyDataSetChanged();
    }

    @Override
    public void setPause(boolean isPause) {
        this.mIsPause = isPause;
    }

    @Override
    public boolean isPause() {
        return this.mIsPause;
    }

    @Override
    public boolean isEditMode() {
        return false;
    }

    public void setEditMode(boolean isEditMode) {
    }

    private String trimName(String name) {
        return (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(name.trim())) ? name.trim() : null;
    }

    private String getName(Contact contact) {
        String result = "";
        do {
            result = trimName(contact.getFirstName());
            if (result != null) {
                break;
            }

            result = trimName(contact.getLastName());
            if (result != null) {
                break;
            }

            result = trimName(contact.getNickName());
            if (result != null) {
                break;
            }

            result = trimName(contact.getCompany());
            if (result != null) {
                break;
            }

            result = trimName(contact.getMiddleName());
            if (result != null) {
                break;
            }

            result = trimName(contact.getJobTitle());
            if (result != null) {
                break;
            }

            result = contact.getDisplayName();
        } while (false);

        return result;
    }

    public Contact getSelectedContact() {
        return mSelectedContact;
    }


    private void setImageViewSize(ImageView targetView, @DimenRes int res) {
        int size = mContext.getResources().getDimensionPixelSize(res);
        ViewGroup.LayoutParams lp = targetView.getLayoutParams();
        lp.height = lp.width = size;
    }

    public void bindViewHolderWithoutLastItem(RecyclerView.ViewHolder holder, final int position) {
        final FavoriteViewHolder cache = (FavoriteViewHolder) holder;

        cache.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPause()) {
                    Contact contact = mFavorites.get(position).contact;
                    if (contact != null) {
                        mFavoritesViewDelegate.itemClick(view, position, contact);
                    }
                }
            }
        });

        FavoriteEntity entity = getFavoriteEntity(position);
        long contactId = entity.contactId;
        Contact contact = entity.contact;
        Contact.ContactType contactType = entity.contactType;
        String name = getName(contact);

        cache.nameView.setText(name);

//        boolean isPresenceEnable = mFavoriteItemPresenter.isPresenceEnabled(contactType);
//        if (isPresenceEnable) {
//            PresenceInfo presenceStatus = PresenceDataStore.getInstance().getPresenceInfo(contactId);
//            mFavoriteItemPresenter.setPresenceView(cache.presenceView, presenceStatus);
//        } else {
//            cache.presenceView.setVisibility(View.GONE);
//        }

        cache.presenceOverlay.setVisibility(View.GONE);

        Contact selectedContact = getSelectedContact();
        if (selectedContact == null) {
            cache.nameView.setTextColor(mContext.getResources().getColor(R.color.text_color_main));
            cache.photoView.setImageAlpha(255);
            setImageViewSize(cache.photoView, R.dimen.fav_in_contacts_item_photo_size);

//            if (isPresenceEnable) {
//                cache.presenceView.setVisibility(View.VISIBLE);
//                setImageViewSize(cache.presenceView, R.dimen.fav_in_contacts_item_presence_size);
//            }

        } else if (selectedContact.getId() == contactId) {
            cache.nameView.setTextColor(mContext.getResources().getColor(R.color.add_fav_text_color));
            cache.photoView.setImageAlpha(255);
            setImageViewSize(cache.photoView, R.dimen.fav_in_contacts_item_photo_size_zoom_out);

//            if (isPresenceEnable) {
//                cache.presenceView.setVisibility(View.VISIBLE);
//                setImageViewSize(cache.presenceView, R.dimen.fav_in_contacts_item_presence_size_zoom_out);
//            }

        } else {
            cache.nameView.setTextColor(mContext.getResources().getColor(R.color.text_secondary));
            cache.photoView.setImageAlpha(128);

//            if (isPresenceEnable) {
//                cache.presenceView.setVisibility(View.VISIBLE);
//                cache.presenceOverlay.setVisibility(View.VISIBLE);
//                setImageViewSize(cache.presenceView, R.dimen.fav_in_contacts_item_presence_size);
//            }
        }

        mFavoriteItemPresenter.setPhotoView(cache.photoView, contact);
    }




    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        /**
         * Person name view.
         */
        public TextView nameView;

        /**
         * Person photo cache holder.
         */
        public ImageView photoView;

        public ImageView presenceView;

        public ImageView presenceOverlay;

        public View addNewFav;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.name);
            photoView = (ImageView) itemView.findViewById(R.id.photo);
            presenceView = (ImageView) itemView.findViewById(R.id.presence_imageview);
            presenceOverlay = (ImageView) itemView.findViewById(R.id.presence_overlay);
            addNewFav = itemView.findViewById(R.id.btnAddNewFav);
        }

        public void setAddNewFavorite(boolean show) {
            if (show) {
                photoView.setVisibility(View.GONE);
                presenceView.setVisibility(View.GONE);
                presenceOverlay.setVisibility(View.GONE);
                nameView.setVisibility(View.GONE);
                addNewFav.setVisibility(View.VISIBLE);
            } else {
                photoView.setVisibility(View.VISIBLE);
                presenceView.setVisibility(View.VISIBLE);
                presenceOverlay.setVisibility(View.VISIBLE);
                nameView.setVisibility(View.VISIBLE);
                addNewFav.setVisibility(View.GONE);
            }
        }
    }

}
