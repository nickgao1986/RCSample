package com.example.nickgao.contacts.adapters.contactsprovider;

import com.example.nickgao.logging.MktLog;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick.gao on 1/29/17.
 */

public class DisplayContactsProviderUtils {

    private static final String TAG = "DisplayContactsProviderUtils";

    public static ContactsAdapterDataContainer getContactsAdapterDataWithSections(List<Contact> contacts){
        MktLog.d(TAG, "getItems with sections: " + contacts.size() + " contacts");
        long time = System.currentTimeMillis();
        List<ContactListItem> contactViewitems = new ArrayList<>();
        List<String> sections = new ArrayList<>();
        List<Integer> sectionToPosition = new ArrayList<>(128);
        List<Integer> positionToSection = new ArrayList<>(128);
        if (contacts == null || contacts.size() == 0){
            return ContactsAdapterDataContainer.getEmpty();
        }

        Contact currentContact;
        Contact nextContact;
        int currentSectionInd = 0;

        char currentSection = getSection(contacts.get(0).getDisplayName());
        contactViewitems.add(ContactListItem.createDivider(String.valueOf(currentSection)));
        sectionToPosition.add(0);
        positionToSection.add(0);
        sections.add(String.valueOf(currentSection));

        for (int i = 0; i < contacts.size() - 1; i++){
            currentContact = contacts.get(i);
            nextContact = contacts.get(i+1);
            char nextSection = getSection(nextContact.getDisplayName());

            if (currentSection != nextSection){
                contactViewitems.add(ContactListItem.createContactItem(currentContact, true));
                positionToSection.add(currentSectionInd);
                currentSection = nextSection;
                contactViewitems.add(ContactListItem.createDivider(String.valueOf(currentSection)));
                currentSectionInd ++;
                positionToSection.add(currentSectionInd);
                sectionToPosition.add(contactViewitems.size()-1);
                sections.add(String.valueOf(currentSection));
            } else {
                contactViewitems.add(ContactListItem.createContactItem(currentContact, false));
                positionToSection.add(currentSectionInd);
            }
        }

        contactViewitems.add(ContactListItem.createContactItem(contacts.get(contacts.size() - 1), false));
        positionToSection.add(currentSectionInd);

        MktLog.d(TAG, "getItems: " + contactViewitems.size() + " list Items, spent=" + (System.currentTimeMillis() - time));
        ArrayUtils.toPrimitive(positionToSection.toArray(new Integer[positionToSection.size()]));

        return new ContactsAdapterDataContainer(
                contactViewitems,
                ArrayUtils.toPrimitive(sectionToPosition.toArray(new Integer[sectionToPosition.size()])),
                ArrayUtils.toPrimitive(positionToSection.toArray(new Integer[positionToSection.size()])),
                sections.toArray(new String[0]));
    }

    public static ContactsAdapterDataContainer getContactsAdapterData(List<Contact> contacts){
        MktLog.d(TAG, "getItems: " + contacts.size() + " contacts");

        List<ContactListItem> contactViewitems = new ArrayList<>();
        for (Contact contact : contacts) {
            contactViewitems.add(ContactListItem.createContactItem(contact, false));
        }

        return new ContactsAdapterDataContainer(
                contactViewitems,
                new int[0],
                new int[0],
                new String[0]);
    }

    private static char getSection(String displayName) {
        if(displayName == null || displayName.isEmpty()) {
            return '#';
        }
        char section = Character.toUpperCase(displayName.charAt(0));
        if (!isLatinLetter(section)) {
            return "#".charAt(0);
        }
        return section;
    }

    private static boolean isLatinLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    public static class ContactsAdapterDataContainer {
        private final List<ContactListItem> mContacts;
        private final int[] mSectionToPosition;
        private final int[] mPositionToSection;
        private final String[] mSections;

        public static ContactsAdapterDataContainer getEmpty(){
            return new ContactsAdapterDataContainer(
                    new ArrayList<ContactListItem>(),
                    new int[0],
                    new int[0],
                    new String[0]);
        }

        public ContactsAdapterDataContainer(
                List<ContactListItem> contacts,
                int[] sectionToPosition,
                int[] positionToSection,
                String[] sections) {

            if (contacts == null || sectionToPosition == null || positionToSection == null || sections == null) {
                throw new IllegalArgumentException("null argument");
            }

            mContacts = contacts;
            mSectionToPosition = sectionToPosition;
            mPositionToSection = positionToSection;
            mSections = sections;
        }

        public List<ContactListItem> getContacts(){
            return mContacts;
        }

        public int[] getSectionToPosition(){
            return mSectionToPosition;
        }

        public int[] getPositionToSection() {
            return mPositionToSection;
        }

        public String[] getSections() {
            return mSections;
        }

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append("ContactsAdapterDataContainer ")
                    .append(" sections: ")
                    .append(mSections.length)
                    .append(" sectionToPosition: ")
                    .append(mSectionToPosition.length)
                    .append(" positionToSection: ")
                    .append(mPositionToSection.length)
                    .append(" contacts: ")
                    .append(mContacts.size());
            return sb.toString();
        }
    }

}
