package com.example.nickgao.contacts.adapters;

/**
 * Created by nick.gao on 2/4/17.
 */
import java.util.List;

/**
 * Created by jerry.cai on 11/4/16.
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
