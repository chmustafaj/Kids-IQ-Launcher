package com.kids.launcher.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.view.View;

import com.kids.launcher.activity.HomeActivity;
import com.kids.launcher.manager.Setup;
import com.kids.launcher.model.Item;
import com.kids.launcher.viewutil.DesktopCallback;
import com.kids.launcher.widget.AppItemView;

public final class DragHandler {
    public static Bitmap _cachedDragBitmap;

    public static void startDrag(View view, Item item, DragAction.Action action, final DesktopCallback desktopCallback) {
        _cachedDragBitmap = loadBitmapFromView(view);

        if (HomeActivity.Companion.getLauncher() != null)
            HomeActivity._launcher.getItemOptionView().startDragNDropOverlay(view, item, action);

        if (desktopCallback != null)
            desktopCallback.setLastItem(item, view);
    }

    public static View.OnLongClickListener getLongClick(final Item item, final DragAction.Action action, final DesktopCallback desktopCallback) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (Setup.appSettings().getDesktopLock()) {
                    if (HomeActivity.Companion.getLauncher() != null && !DragAction.Action.SEARCH.equals(action)) {
                        if (Setup.appSettings().getGestureFeedback()) {
                            Tool.vibrate(view);
                        }
                        HomeActivity._launcher.getItemOptionView().showItemPopupForLockedDesktop(item, HomeActivity.Companion.getLauncher());
                        return true;
                    }
                    return false;
                }
                if (Setup.appSettings().getGestureFeedback()) {
                    Tool.vibrate(view);
                }
                startDrag(view, item, action, desktopCallback);
                return true;
            }
        };
    }

    private static Bitmap loadBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        String tempLabel = null;
        if (view instanceof AppItemView) {
            tempLabel = ((AppItemView) view).getLabel();
            ((AppItemView) view).setLabel(" ");
        }
        view.layout(0, 0, view.getWidth(), view.getHeight());
        view.draw(canvas);
        if (view instanceof AppItemView) {
            ((AppItemView) view).setLabel(tempLabel);
        }
        view.getParent().requestLayout();
        return bitmap;
    }
}