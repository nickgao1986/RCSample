package com.example.nickgao.utils.widget;

import android.view.MotionEvent;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

/**
 * Created by nick.gao on 2/4/17.
 */

public class RCMDragSortController extends DragSortController {

    public RCMDragSortController(DragSortListView dslv) {
        super(dslv, 0, ON_DOWN, FLING_REMOVE);
    }

    public RCMDragSortController(DragSortListView dslv, int dragHandleId, int dragInitMode, int removeMode) {
        super(dslv, dragHandleId, dragInitMode, removeMode, 0);
    }

    public RCMDragSortController(DragSortListView dslv, int dragHandleId, int dragInitMode,
                                 int removeMode, int clickRemoveId) {
        super(dslv, dragHandleId, dragInitMode, removeMode, clickRemoveId, 0);
    }

    public RCMDragSortController(DragSortListView dslv, int dragHandleId, int dragInitMode,
                                 int removeMode, int clickRemoveId, int flingHandleId) {
        super(dslv, dragHandleId, dragInitMode, removeMode, clickRemoveId, flingHandleId);
    }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        try {
            return super.onScroll(e1, e2, distanceX, distanceY);
        } catch (Exception e) {
            return false;
        }
    }

}
