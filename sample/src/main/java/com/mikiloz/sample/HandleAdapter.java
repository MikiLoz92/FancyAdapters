package com.mikiloz.sample;// Created by Miguel Vera Belmonte on 18/08/2016.

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikiloz.fancyadapters.SuperSelectableAdapter;

import java.util.List;


public abstract class HandleAdapter<T, VH extends HandleAdapter.ViewHolder>
        extends SuperSelectableAdapter<T, VH> {

    private boolean actionCancelled = false;    // Whether the element was just dropped

    /**
     * The constructor takes the following parameters:
     *
     * @param items        the items that are going to be represented in a {@link RecyclerView}
     * @param recyclerView a link to the {@link RecyclerView} that is going to be used
     * @param dragFlags
     * @param swipeFlags
     */
    public HandleAdapter(List<T> items, RecyclerView recyclerView, int dragFlags, int swipeFlags) {
        super(items, recyclerView, dragFlags, swipeFlags);
        triggerOnDrop = false;
    }

    public abstract void onBindHandleViewHolder(VH holder, int position);

    @Override
    public void onBindViewHolder(VH holder, int position) {

        if (isSelected(position)) {
            int index = holder.parent.indexOfChild(holder.handleView);
            if (index != -1) {
                holder.parent.removeView(holder.handleView);
                holder.selectedIndicatorView.setRotationY(180);
                holder.parent.addView(holder.selectedIndicatorView, index);
            }
        } else {
            int index = holder.parent.indexOfChild(holder.selectedIndicatorView);
            if (index != -1) {
                holder.parent.removeView(holder.selectedIndicatorView);
                holder.handleView.setRotationY(0);
                holder.parent.addView(holder.handleView, index);
            }
        }

        onBindHandleViewHolder(holder, position);

    }

    @Override
    public void onItemSelected(final VH holder, int position) {
        ObjectAnimator animator1;
        final ObjectAnimator animator2;
        animator1 = (ObjectAnimator) AnimatorInflater
                .loadAnimator(holder.itemView.getContext(), R.animator.flip_handle_1);
        animator2 = (ObjectAnimator) AnimatorInflater
                .loadAnimator(holder.itemView.getContext(), R.animator.flip_handle_2);
        animator1.setTarget(holder.handleView);
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
                holder.handleView.setRotationY(0);
                int index = holder.parent.indexOfChild(holder.handleView);
                holder.parent.removeView(holder.handleView);
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
        animator2.setTarget(holder.handleView);
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
                holder.selectedIndicatorView.setRotationY(0);
                int index = holder.parent.indexOfChild(holder.selectedIndicatorView);
                holder.parent.removeView(holder.selectedIndicatorView);
                holder.parent.removeView(holder.handleView);
                holder.parent.addView(holder.handleView, index);
            }
            @Override
            public void onAnimationCancel(Animator animator) {

            }
            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        protected View handleView, selectedIndicatorView;
        protected ViewGroup parent;

        public ViewHolder(View itemView) {
            super(itemView);
            handleView = itemView.findViewById(getHandleViewID());
            parent = (ViewGroup) handleView.getParent();
            selectedIndicatorView = LayoutInflater.from(itemView.getContext())
                    .inflate(getSelectedIndicatorResourceID(), parent, false);
            selectedIndicatorView.setScaleX(-1);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isActionModeEnabled())
                        if (selectClick) {
                            toggleElement(HandleViewHolder.this);
                        } else HandleViewHolder.this.onClick();
                    else {
                        if (actionModeStartClick) {
                            selectFirstElement(HandleViewHolder.this);
                        } else if (dragStartClick) {
                            startDrag(HandleViewHolder.this);
                        } else HandleViewHolder.this.onClick();
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (isActionModeEnabled())
                        if (selectLongClick) {
                            toggleElement(HandleViewHolder.this);
                            return true;
                        }
                        else return HandleViewHolder.this.onLongClick();
                    else
                    if (dragStartLongClick) {
                        startDrag(HandleViewHolder.this);
                        return true;
                    } else if (actionModeStartLongClick) {
                        selectFirstElement(HandleViewHolder.this);
                        return true;
                    } else return HandleViewHolder.this.onLongClick();
                }
            });*/
            handleView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_MOVE) {
                        if (!isActionModeEnabled()) {
                            startDrag((VH)ViewHolder.this, getLayoutPosition());
                        }
                    } else if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP) {
                        actionCancelled = false;
                        if (isActionModeEnabled()) {
                            toggleItem((VH)ViewHolder.this, getLayoutPosition());
                        } else {
                            triggerSelectionMode((VH)ViewHolder.this, getLayoutPosition());
                        }
                    } else if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_CANCEL) {
                        actionCancelled = true;
                    }
                    return true;
                }
            });
            selectedIndicatorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleItem((VH)ViewHolder.this, getLayoutPosition());
                }
            });
            itemView.setHapticFeedbackEnabled(true);
        }

        /**
         * This method is meant to return the ID of the handle {@link View}. This View should be
         * contained within the element you're inflating as your {@link HandleAdapter.ViewHolder}.
         * @return the ID of the handle {@link View}
         */
        public abstract int getHandleViewID();

        /**
         * This method should return the resource ID of the {@link View} you'll be swapping your
         * handle into.
         * @return the resource ID of the {@link View} (in the form of R.layout.something)
         */
        public abstract int getSelectedIndicatorResourceID();

    }


}
