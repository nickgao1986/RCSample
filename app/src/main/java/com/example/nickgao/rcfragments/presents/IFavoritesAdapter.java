package com.example.nickgao.rcfragments.presents;

import com.example.nickgao.contacts.adapters.FavoriteEntity;

import java.util.List;

/**
 * Created by nick.gao on 1/31/17.
 */
public interface IFavoritesAdapter {

    void refresh(List<FavoriteEntity> list);

    FavoriteEntity getFavoriteEntity(int position);

    int getFavoritesCount();

    void updateAdapter();

    void setPause(boolean isPause);

    boolean isPause();

    void setEditMode(boolean isEditMode);

    boolean isEditMode();

}
