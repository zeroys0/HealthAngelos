package net.leelink.healthangelos.view;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleItemTouchCallBack extends ItemTouchHelper.Callback {
    private TouchCallBack touchCallBack;
    private boolean mSwipeEnable = true;

    public SimpleItemTouchCallBack(TouchCallBack touchCallBack) {
        this.touchCallBack = touchCallBack;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        int drag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;

        int swipe = ItemTouchHelper.LEFT;

        return makeMovementFlags(drag,swipe);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        touchCallBack.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        touchCallBack.onItemDelete(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return super.isLongPressDragEnabled();
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return mSwipeEnable;
    }

    public void setmSwipeEnable(boolean enable) {
        this.mSwipeEnable = enable;
    }
}
