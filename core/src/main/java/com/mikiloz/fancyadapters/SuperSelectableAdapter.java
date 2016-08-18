package com.mikiloz.fancyadapters;// Created by Miguel Vera Belmonte on 16/08/2016.

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.List;

public abstract class SuperSelectableAdapter<T, VH extends RecyclerView.ViewHolder>
        extends SelectableAdapter<T, VH> {

    protected ItemTouchHelper itemTouchHelper;
    protected int dragFlags;
    protected int swipeFlags;
    protected boolean dragging = false;     // Whether an element is currently being dragged
    protected int draggedElementPos = 0;    // The initial position of the dragged element
    protected boolean swiping = false;      // Whether an element is currently being swiped
    protected int swipedElementPos = 0;     // The initial position of the swiped element
    protected boolean elementMoved = false; // Whether the element was moved from its original position
    protected boolean triggerOnDrop = true; // Whether you can select an item by dragging & dropping
                                            // it on the same spot

    /**
     * The constructor takes the following parameters:
     *
     * @param items        the items that are going to be represented in a {@link RecyclerView}
     */
    public SuperSelectableAdapter(List<T> items, int dragFlags, int swipeFlags) {
        super(items);
        this.dragFlags = dragFlags;
        this.swipeFlags = swipeFlags;
        this.itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void setTriggerSelectionModeByDroppingViewHolder(boolean triggerOnDrop) {
        this.triggerOnDrop = triggerOnDrop;
    }

    public boolean isDragging() { return dragging; }

    public void startDrag(final VH holder, int position) {
        if (!isActionModeEnabled())
            itemTouchHelper.startDrag(holder);
    }

    public abstract void onMove(VH viewHolder, VH target);
    public abstract void onSwipe(VH viewHolder, int direction);

    private class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return !isActionModeEnabled();
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            elementMoved = true;
            dragging = true;
            int fromPos = viewHolder.getAdapterPosition();
            int toPos = target.getAdapterPosition();
            SuperSelectableAdapter.this.onMove((VH)viewHolder, (VH)target);
            notifyItemMoved(fromPos, toPos);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getLayoutPosition();
            SuperSelectableAdapter.this.onSwipe((VH)viewHolder, direction);
            notifyItemRemoved(position);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState == 2) { // Drag start
                dragging = true;
                draggedElementPos = viewHolder.getLayoutPosition();
                elementMoved = false;
            } else if (actionState == 1) {
                swiping = true;
                swipedElementPos = viewHolder.getLayoutPosition();
            } else if (actionState == 0) { // Drop
                if (dragging) {
                    dragging = false;
                    if (!elementMoved && triggerOnDrop) {
                        VH vh = (VH) recyclerView.findViewHolderForAdapterPosition(draggedElementPos);
                        triggerSelectionMode(vh, draggedElementPos);
                    }
                } else if (swiping) {
                    swiping = false;
                }

            }
        }
    }

}
