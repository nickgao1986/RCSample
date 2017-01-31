package com.example.nickgao.rcfragments.presents;

import android.content.Context;
import android.view.View;

import com.example.nickgao.contacts.adapters.FavInContactsListAdapter;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;

/**
 * Created by nick.gao on 1/31/17.
 */

public class FavoritesInContactsPresenter extends FavoritesPresenter {

    public FavoritesInContactsPresenter(Context context, IFavoritesView favoritesListView) {
        super(context, favoritesListView, new FavInContactsListAdapter(context, new FavoritesViewProxy(favoritesListView)));
    }

    private static class FavoritesViewProxy implements FavInContactsListAdapter.FavoritesViewDelegate {
        private IFavoritesView mFavoritesListView;
        public FavoritesViewProxy(IFavoritesView favoritesView) {
            mFavoritesListView = favoritesView;
        }
        @Override
        public void itemClick(View v, int position, Contact contact) {
            mFavoritesListView.itemClick(v, position, contact);
        }

        @Override
        public void requestAddingFavorite() {
            mFavoritesListView.requestAddingFavorite();
        }

        @Override
        public void selectedContactChanged(Contact contact) {
            mFavoritesListView.selectedContactChanged(contact);
        }
    }

}
