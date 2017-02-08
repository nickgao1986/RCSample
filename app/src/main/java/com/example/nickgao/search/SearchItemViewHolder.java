package com.example.nickgao.search;

import android.view.View;
import android.widget.AdapterView;

import com.example.nickgao.utils.SuperSearching;


public abstract interface SearchItemViewHolder
{
  public abstract void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong);

  public abstract void update(SuperSearching.ItemSearchResult paramItemSearchResult, View paramView);
}