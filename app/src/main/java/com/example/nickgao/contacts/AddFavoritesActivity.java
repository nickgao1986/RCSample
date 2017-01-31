package com.example.nickgao.contacts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.example.nickgao.R;

/**
 * Created by nick.gao on 1/31/17.
 */

public class AddFavoritesActivity extends FragmentActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favorites);
    }

    private Fragment getFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.add_favorites_fragment);
    }
}
