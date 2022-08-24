package com.kids.launcher.interfaces;

import android.view.View;

import com.kids.launcher.model.Item;

public interface ItemHistory {
    void setLastItem(Item item, View view);

    void revertLastItem();

    void consumeLastItem();
}
