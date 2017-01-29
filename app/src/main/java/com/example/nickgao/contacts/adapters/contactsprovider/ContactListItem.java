package com.example.nickgao.contacts.adapters.contactsprovider;

/**
 * Created by nick.gao on 1/28/17.
 */

public class ContactListItem {

    private String mDividerName;

    private Contact mContact;

    private boolean mContactBeforeDivider;

    private ItemType mType;

    public static ContactListItem createDivider(String name){
        ContactListItem item = new ContactListItem(ItemType.DIVIDER);
        item.mDividerName = name;
        return item;
    }

    public static ContactListItem createContactItem(Contact contact, boolean contactBeforeDivider) {
        ContactListItem item = new ContactListItem(ItemType.CONTACT);
        item.mContact = contact;
        item.mContactBeforeDivider = contactBeforeDivider;
        return item;
    }

    private ContactListItem(ItemType type){
        mType = type;
    }

    public ItemType getType(){
        return mType;
    }

    public String getDividerName(){
        return mDividerName;
    }

    public String getContactDisplayName(){
        if (mContact != null){
            return mContact.getDisplayName();
        }
        return null;
    }

    /*
    public String getContactNote() {
        if (mContact != null) {
            return mContact.getNote();
        }
        return null;
    }*/

    public boolean isContactBeforeDivider(){
        return mContactBeforeDivider;
    }

    public Contact getDisplayContact() {
        return mContact;
    }

    public enum ItemType {
        CONTACT (0),
        DIVIDER (1);

        private static final int size = ItemType.values().length;

        private int mPosition;

        ItemType(int position){
            mPosition = position;
        }

        public int getPosition(){
            return mPosition;
        }

        public static int getLength(){
            return size;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("type:").append(mType);
        switch (mType){
            case CONTACT:
                sb.append("   ").append(getContactDisplayName());
                break;
            case DIVIDER:
                sb.append("    ").append(getDividerName());
                break;
        }
        return sb.toString();
    }

}
