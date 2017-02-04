package com.example.nickgao.rcfragments.presents;

import com.example.nickgao.contacts.adapters.FavInContactsListAdapter;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.contacts.adapters.IFavoritesAdapter;

/**
 * Created by nick.gao on 1/31/17.
 */

public interface IFavoritesView extends FavInContactsListAdapter.FavoritesViewDelegate {
    void setLoading(boolean value);

    void showEmpty();

    void hideEmpty();

    boolean isUIReady();

    void selectedContactChanged(Contact contact);

    boolean deselectContact();

    void photoChanged(long contactId, String eTag);

    void setAdapter(IFavoritesAdapter adapter);
}
