package com.example.nickgao.contacts.adapters;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.example.nickgao.R;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.contacts.adapters.contactsprovider.DeviceContact;
import com.example.nickgao.utils.RoundedCornersTransformation;

/**
 * Created by nick.gao on 1/31/17.
 */

public class FavoriteItemPresenter {


    private Context mContext;
    private boolean isPresenceEnabled;
    private boolean isStorageAccessible;

    public FavoriteItemPresenter(Context context) {
        mContext = context;
//        isPresenceEnabled = PermissionControl.getInstance().hasReadPresenceStatusPermission(context);
        isStorageAccessible = true;
    }


    public void setPhotoView(final ImageView photoView, Contact contact) {
        switch (contact.getType()) {
            case DEVICE: {
                DeviceContact deviceContact = (DeviceContact) contact;
                Uri uri = contact.getContactUri();
                uri = Uri.withAppendedPath(uri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                if (deviceContact.getPhotoFileId() == 0) {
                    Glide.with(mContext).load(R.drawable.ic_contact_list_picture).into(photoView);

                } else {
                    Glide.with(mContext)
                            .load(uri)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .transform(new RoundedCornersTransformation(mContext, 360, 0))
                            .placeholder(R.drawable.ic_contact_list_picture)
                            .signature(new StringSignature(String.valueOf(deviceContact.getPhotoFileId())))
                            .into(photoView);
                }
            }
            break;
//            case CLOUD_COMPANY: {
//                CompanyContact companyContact = (CompanyContact) contact;
//                String profilePath;
//
//                if (ProfileImageOperator.isNeedDownloadExtensionPic(companyContact.getEtag(), String.valueOf(companyContact.getId()), RestRequestDownloadExtensionProfileImage.PicSize.SMALL, companyContact.getProfileImage().getUri(), isStorageAccessible)) {
//                    ProfileImageInfo info = new ProfileImageInfo(companyContact.getProfileImage(), RestRequestDownloadExtensionProfileImage.PicSize.SMALL, String.valueOf(companyContact.getId()));
//                    ProfileImageSyncService.addToImageDownLoadQueen(mContext, info);
//                }
//
//                if (companyContact.getId() == CurrentUserSettings.getSettings().getCurrentMailboxId()) {
//                    profilePath = ProfileImageOperator.getAvatarPath();
//                } else {
//                    profilePath = ProfileImageOperator.getProfilePath(companyContact.getEtag(), companyContact.getId(), isStorageAccessible);
//                }
//                if (profilePath == null) {
//                    Glide.with(mContext)
//                            .load(R.drawable.ic_contact_list_picture)
//                            .asBitmap()
//                            .diskCacheStrategy(DiskCacheStrategy.NONE)
//                            .into(photoView);
//                } else {
//
//                    Glide.with(mContext).load(profilePath).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).
//                            placeholder(R.drawable.ic_contact_list_picture).centerCrop().into(new BitmapImageViewTarget(photoView) {
//                        @Override
//                        protected void setResource(Bitmap resource) {
//                            RoundedBitmapDrawable circularBitmapDrawable =
//                                    RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
//                            circularBitmapDrawable.setCircular(true);
//                            photoView.setImageDrawable(circularBitmapDrawable);
//                        }
//                    });
//
//                }
//            }
//            break;
            case CLOUD_PERSONAL: {
                Glide.with(mContext)
                        .load(R.drawable.ic_contact_list_picture)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.ic_contact_list_picture)
                        .into(photoView);
            }
            break;
        }
    }

}
