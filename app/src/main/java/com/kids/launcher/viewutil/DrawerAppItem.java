package com.kids.launcher.viewutil;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.kids.launcher.R;
import com.kids.launcher.manager.Setup;
import com.kids.launcher.model.App;
import com.kids.launcher.model.Item;
import com.kids.launcher.util.DragAction;
import com.kids.launcher.widget.AppDrawerGrid;
import com.kids.launcher.widget.AppItemView;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class DrawerAppItem extends AbstractItem<DrawerAppItem, DrawerAppItem.ViewHolder> {
    private App _app;

    public DrawerAppItem(App app) {
        // TODO merge IconLabelItem and DrawerAppItem into one class
        // they both do the same thing
        // ideally remove all the custom code for AppItemView in favor of the
        // nicer code in IconLabelItem
        _app = app;
    }

    @Override
    public int getType() {
        return R.id.id_adapter_drawer_app_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_app;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bindView(DrawerAppItem.ViewHolder holder, List payloads) {
        Item item = Item.newAppItem(_app);
        holder.builder
                .setAppItem(item)
                .withOnLongClick(item, DragAction.Action.DRAWER, null);
        super.bindView(holder, payloads);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        AppItemView appItemView;
        AppItemView.Builder builder;

        public ViewHolder(View itemView) {
            super(itemView);
            appItemView = (AppItemView) itemView;
            appItemView.setTargetedWidth(AppDrawerGrid._itemWidth);
            appItemView.setTargetedHeightPadding(AppDrawerGrid._itemHeightPadding);

            builder = new AppItemView.Builder(appItemView)
                    .setIconSize(Setup.appSettings().getIconSize())
                    .setLabelVisibility(Setup.appSettings().getDrawerShowLabel())
                    .setTextColor(Setup.appSettings().getDrawerLabelColor());
        }
    }
}
