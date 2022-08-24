package com.kids.launcher.viewutil;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kids.launcher.R;
import com.mikepenz.fastadapter.IClickable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;

public abstract class AbstractPopupIconLabelItem<Item extends IItem & IClickable> extends AbstractItem<Item, AbstractPopupIconLabelItem.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconView;
        TextView labelView;

        ViewHolder(View itemView) {
            super(itemView);

            labelView = itemView.findViewById(R.id.item_popup_label);
            iconView = itemView.findViewById(R.id.item_popup_icon);
        }
    }

}
