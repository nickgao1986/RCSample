package com.example.nickgao.contacts.adapters.contactsprovider;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.nickgao.R;
import com.example.nickgao.rcfragments.BaseContactsFragment;

import java.util.List;

/**
 * Created by Sergey Onofreychuck on 10.09.15.
 */
public class CombinedContactsAdapter extends BaseAdapter implements SectionIndexer {

    private List<ContactListItem> mItems;

    private Context mContext;

    private int[] mSectionToPosition;
    private int[] mPositionToSection;
    private String[] mSections;
    private BaseContactsFragment.Tabs mCurrentTab;

    public CombinedContactsAdapter(Context context, List<ContactListItem> items){
        if (items == null){
            throw new IllegalArgumentException("items");
        }
        if (context == null) {
            throw new IllegalArgumentException("context");
        }

        mContext = context;
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactListItem item = mItems.get(position);

        if (convertView == null) {
            convertView = prepareConvertView(item);
        }

        ((ContactsViewHolder)convertView.getTag()).update(item);


        return convertView;
    }

    private View prepareConvertView(ContactListItem item){
        View convertView;
        ContactsViewHolder viewHolder;
        if (item.getType() == ContactListItem.ItemType.CONTACT){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.contacts_list_item, null);
            viewHolder = new ContactItemViewHolder(
                    (TextView)convertView.findViewById(R.id.name),
                    (ImageView)convertView.findViewById(R.id.photo),
                    convertView.findViewById(R.id.name_type));
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.contacts_list_divider, null);
            viewHolder = new DividerViewHolder((TextView)convertView.findViewById(R.id.txtSectionHeader));
        }

        convertView.setTag(viewHolder);
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return ContactListItem.ItemType.getLength();
    }

    @Override
    public int getItemViewType(int position) {
        return ((ContactListItem) getItem(position)).getType().getPosition();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateSections(int[] sectionToPosition, int[] positionToSection, String[] sections){
        if (sectionToPosition == null) {
            throw new IllegalArgumentException("sectionToPosition");
        }
        if (positionToSection == null) {
            throw new IllegalArgumentException("positions");
        }
        mSectionToPosition = sectionToPosition;
        mSections = sections;
        mPositionToSection = positionToSection;
    }


    @Override
    public Object[] getSections() {
        return mSections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        if (mSectionToPosition.length == 0) {
            return 0;
        }
        if (sectionIndex > mSectionToPosition.length - 1) {
            return mSectionToPosition[mSectionToPosition.length - 1];
        }
        return mSectionToPosition[sectionIndex];
    }

    @Override
    public int getSectionForPosition(int position) {
        if (mSections.length == 0) {
            return 0;
        }
        return  mPositionToSection[position];
    }


    private interface ContactsViewHolder{
        void update(ContactListItem item);
    }

    private class ContactItemViewHolder implements ContactsViewHolder {
        private TextView mDescription;
        private ImageView mAvatar;
        private View mNameTypeWrapper;
        private TextView mType;

        public ContactItemViewHolder(TextView description,ImageView avatar, View nameTypeWrapper){
            mDescription = description;
            mAvatar = avatar;
            mNameTypeWrapper = nameTypeWrapper;
            mType = (TextView)mNameTypeWrapper.findViewById(R.id.type);
        }

        @Override
        public void update(ContactListItem item) {
            mDescription.setText(item.getContactDisplayName());
            if (item.isContactBeforeDivider()) {
                mNameTypeWrapper.setBackgroundDrawable(null);
            } else {
                mNameTypeWrapper.setBackgroundResource(R.drawable.bg_list_item_divider);
            }

         //   mAvatar.setVisibility(mCurrentTab == BaseContactsFragment.Tabs.COMPANY ? View.GONE : View.VISIBLE);
            final Contact displayContact = item.getDisplayContact();
            if (displayContact.getType() == Contact.ContactType.DEVICE) {
                DeviceContact contact = (DeviceContact) displayContact;
                Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contact.getContactId());
                uri = Uri.withAppendedPath(uri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                if(contact.getPhotoFileId() == 0) {
                    Glide.with(mContext).load(R.drawable.ic_contact_list_picture).into(mAvatar);

                }else {
//                    Glide.with(mContext)
//                            .load(uri)
//                            .asBitmap()
//                            .diskCacheStrategy(DiskCacheStrategy.NONE)
//                            .transform(new RoundedCornersTransformation(mContext, 180, 0))
//                            .placeholder(R.drawable.ic_contact_list_picture)
//                            .signature(new StringSignature(String.valueOf(contact.getPhotoFileId())))
//                            .into(mAvatar);
                }
                mType.setText("");
            } else if(displayContact.getType() == Contact.ContactType.CLOUD_COMPANY) {

                CompanyContact companyContact = (CompanyContact)displayContact;
                String profilePath = null;
//                if(companyContact.getId() == CurrentUserSettings.getSettings().getCurrentMailboxId()) {
//                    profilePath = ProfileImageOperator.getAvatarPath();
//                }else{
//                    profilePath = ProfileImageOperator.getProfilePath(companyContact.getEtag(), companyContact.getId(), FileUtils.isStorageAccessible());
//                }
                if(profilePath == null) {
                    Glide.with(mContext)
                            .load(R.drawable.ic_contact_list_picture)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(mAvatar);
                }else {

                    Glide.with(mContext).load(profilePath).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).
                            placeholder(R.drawable.ic_contact_list_picture).centerCrop().into(new BitmapImageViewTarget(mAvatar) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            mAvatar.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                }

                mType.setText(companyContact.getPin());

            } else {
                Glide.with(mContext)
                        .load(R.drawable.ic_contact_list_picture)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(mAvatar);
                mType.setText("");
            }
        }
    }

    private class DividerViewHolder implements ContactsViewHolder {
        private TextView mName;

        public DividerViewHolder(TextView name) {
            mName = name;
        }

        @Override
        public void update(ContactListItem item) {
            mName.setText(item.getDividerName());
        }
    }
}
