package com.mikiloz.fancyadapters;// Created by Miguel Vera Belmonte on 18/08/2016.

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public abstract class SelectableViewAdapter<T, VH extends SelectableViewAdapter.ViewHolder>
        extends SuperSelectableAdapter<T, VH> {

    protected SelectableViewBehavior selectableViewBehavior =
            SelectableViewBehavior.RESPOND_TO_CLICK_EVENTS;

    /**
     * The constructor takes the following parameters:
     *
     * @param items the items that are going to be represented in a {@link RecyclerView}
     * @param recyclerView a link to the {@link RecyclerView} that is going to be used
     * @param dragFlags the directions in which the item can be dragged
     * @param swipeFlags the directions in which the item can be swiped
     */
    public SelectableViewAdapter(List<T> items, RecyclerView recyclerView, int dragFlags, int swipeFlags) {
        super(items, recyclerView, dragFlags, swipeFlags);
        triggerOnDrop = false;
    }

    public abstract void onBindSelectableViewHolder(VH holder, int position);

    @Override
    public void onBindViewHolder(VH holder, int position) {

        if (isSelected(position)) {
            int index = holder.parent.indexOfChild(holder.selectableView);
            if (index != -1) {
                holder.parent.removeView(holder.selectableView);
                holder.selectedIndicatorView.setRotationY(180);
                holder.parent.addView(holder.selectedIndicatorView, index);
            }
        } else {
            int index = holder.parent.indexOfChild(holder.selectedIndicatorView);
            if (index != -1) {
                holder.parent.removeView(holder.selectedIndicatorView);
                holder.selectableView.setRotationY(0);
                holder.parent.addView(holder.selectableView, index);
                System.out.println("caca");
            }
        }

        onBindSelectableViewHolder(holder, position);

    }

    @Override
    public void onItemSelected(final VH holder, int position) {
        ObjectAnimator animator1;
        final ObjectAnimator animator2;
        animator1 = (ObjectAnimator) AnimatorInflater
                .loadAnimator(holder.itemView.getContext(), R.animator.flip_handle_1);
        animator2 = (ObjectAnimator) AnimatorInflater
                .loadAnimator(holder.itemView.getContext(), R.animator.flip_handle_2);
        animator1.setTarget(holder.selectableView);
        animator2.setTarget(holder.selectedIndicatorView);
        animator1.setDuration(75);
        animator2.setDuration(75);
        animator1.start();
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }
            @Override
            public void onAnimationEnd(Animator animator) {
                animator2.start();
                holder.selectableView.setRotationY(0);
                int index = holder.parent.indexOfChild(holder.selectableView);
                holder.parent.removeView(holder.selectableView);
                holder.parent.removeView(holder.selectedIndicatorView);
                holder.parent.addView(holder.selectedIndicatorView, index);
            }
            @Override
            public void onAnimationCancel(Animator animator) {

            }
            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public void onItemDeselected(final VH holder, int position) {
        ObjectAnimator animator1;
        final ObjectAnimator animator2;
        animator1 = (ObjectAnimator) AnimatorInflater
                .loadAnimator(holder.itemView.getContext(), R.animator.flip_handle_3);
        animator2 = (ObjectAnimator) AnimatorInflater
                .loadAnimator(holder.itemView.getContext(), R.animator.flip_handle_4);
        animator1.setTarget(holder.selectedIndicatorView);
        animator2.setTarget(holder.selectableView);
        animator1.setDuration(75);
        animator2.setDuration(75);
        animator1.start();
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }
            @Override
            public void onAnimationEnd(Animator animator) {
                int index = holder.parent.indexOfChild(holder.selectedIndicatorView);
                // There are times when we call adapter.notifyDataSetChanged() right after clicking
                // an ActionItem, because a backend data modification has been done (e.g.: we just
                // erased some elements). This will trigger a rebinding of ViewHolders, that will
                // all be set with selectableViews. At the same time, because the ActionMode just
                // ended, resetSelected() will also be called, which will in turn animate the
                // selectedIndicatorViews to selectableViews. But since the previous rebinding has
                // already replaced them with selectableViews, this animation process will collide
                // with the rebinding process, and wipe all selectableViews already in existence.
                // That is why we will only perform the second step of the animation if we can
                // find a selectedIncatorView to begin with, as demonstrated below.
                if (index != -1) {
                    animator2.start();
                    holder.selectedIndicatorView.setRotationY(0);
                    holder.parent.removeView(holder.selectedIndicatorView);
                    holder.parent.removeView(holder.selectableView);
                    holder.parent.addView(holder.selectableView, index);
                }
            }
            @Override
            public void onAnimationCancel(Animator animator) {

            }
            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public enum SelectableViewBehavior {
        RESPOND_TO_CLICK_EVENTS,
        IGNORE_CLICK_EVENTS
    }

    public void setSelectableViewBehavior(SelectableViewBehavior behavior) {
        selectableViewBehavior = behavior;
    }

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        protected View selectableView, selectedIndicatorView;
        protected ViewGroup parent;

        public ViewHolder(final SelectableViewAdapter adapter, View itemView) {
            super(itemView);
            selectableView = itemView.findViewById(getSelectableViewID());
            parent = (ViewGroup) selectableView.getParent();
            selectedIndicatorView = LayoutInflater.from(itemView.getContext())
                    .inflate(getSelectedIndicatorResourceID(), parent, false);
            selectedIndicatorView.setScaleX(-1);

            selectableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (adapter.selectableViewBehavior == SelectableViewBehavior.RESPOND_TO_CLICK_EVENTS) {
                        if (!adapter.isActionModeEnabled())
                            adapter.triggerSelectionMode(ViewHolder.this, getLayoutPosition());
                        else
                            adapter.toggleItem(ViewHolder.this, getLayoutPosition());
                    }
                }
            });

            selectedIndicatorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (adapter.selectableViewBehavior == SelectableViewBehavior.RESPOND_TO_CLICK_EVENTS) {
                        adapter.toggleItem(ViewHolder.this, getLayoutPosition());
                    }
                }
            });

            itemView.setHapticFeedbackEnabled(true);
        }

        /**
         * This method is meant to return the ID of the handle {@link View}. This View should be
         * contained within the element you're inflating as your {@link SelectableViewAdapter.ViewHolder}.
         * @return the ID of the handle {@link View}
         */
        public abstract int getSelectableViewID();

        /**
         * This method should return the resource ID of the {@link View} you'll be swapping your
         * handle into.
         * @return the resource ID of the {@link View} (in the form of R.layout.something)
         */
        public abstract int getSelectedIndicatorResourceID();

    }


}
