package com.kids.launcher.viewutil;

import android.view.View;

import com.kids.launcher.interfaces.ItemHistory;
import com.kids.launcher.model.Item;

public interface DesktopCallback extends ItemHistory {
    boolean addItemToPoint(Item item, int x, int y);

    boolean addItemToPage(Item item, int page);

    boolean addItemToCell(Item item, int x, int y);

    void removeItem(View view, boolean animate);
}
