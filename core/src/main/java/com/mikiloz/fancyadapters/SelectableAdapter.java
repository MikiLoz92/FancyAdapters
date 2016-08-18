package com.mikiloz.fancyadapters;

import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * The SelectableAdapter is an extendable adapter that is fitted for layouts that require being able
 * to select its items through click events. These click events on its ViewHolders trigger an
 * {@link android.support.v7.view.ActionMode} that is used to perform actions on the items selected.
 * The ViewHolders need to have a view that indicates that they are, in fact, selected. This is
 * accomplished, generally, by putting an icon indicator somewhere in the ViewHolder.
 *
 * @param <T>  the type of items the adapter is going to handle
 * @param <VH> a custom {@link RecyclerView.ViewHolder}, incorporating a {@link View} that
 *             represents whether the element is selected or not.
 * @author Miguel Vera Belmonte (MikiLoz92)
 */
public abstract class SelectableAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    protected List<T> items;
    protected List<Boolean> selected = new ArrayList<>();
    protected List<VH> viewHolders = new ArrayList<>();
    protected RecyclerView recyclerView;
    protected ActionMode actionMode;

    /**
     * The constructor takes the following parameters:
     *
     * @param items        the items that are going to be represented in a {@link RecyclerView}
     */
    public SelectableAdapter(List<T> items) {
        this.items = items;
        for (int i = 0; i < items.size(); i++) selected.add(false);
        registerAdapterDataObserver(new DataChangeObserver());
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH viewHolder = onCreateSelectableViewHolder(parent, viewType);
        viewHolders.add(viewHolder);
        return viewHolder;
    }

    public abstract VH onCreateSelectableViewHolder(ViewGroup parent, int viewType);

    /**
     * This method should be implemented to provide an adapter-specific way of handling item
     * selection, like visual indicators.
     *
     * @param holder   the {@link VH} currently binding
     * @param position the position of the {@link VH}
     */
    @Override
    public abstract void onBindViewHolder(final VH holder, int position);

    @Override
    public int getItemCount() {
        return items.size();
    }

    public boolean isSelected(int position) {
        return selected.get(position);
    }

    public List<T> getSelected() {
        List<T> l = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i)) l.add(items.get(i));
        }
        return l;
    }

    public void resetSelected() {
        // First, deselect the ViewHolders cleanly (only those that are selected, though)
        for (VH v: viewHolders) {
            int position = v.getAdapterPosition();
            if (position != -1 && selected.get(position)) {
                selected.set(position, false);
                onItemDeselected(v, position);
            }
        }
        // Then, set every item's selection status to false
        for (int i = 0; i < selected.size(); i++) selected.set(i, false);
    }

    public void toggleItem(final VH holder, int position) {
        if (!isActionModeEnabled()) {
            selected.set(position, true);
            onItemSelected(holder, position);
            actionMode = startActionMode();
        } else {
            selected.set(position, !selected.get(position));
            if (selected.get(position)) onItemSelected(holder, position);
            else onItemDeselected(holder, position);
            checkActionModeEnded();
        }

    }

    public void triggerSelectionMode(final VH holder, int position) {
        if (!isActionModeEnabled()) {
            selected.set(position, true);
            onItemSelected(holder, position);
            actionMode = startActionMode();
        }
    }

    public void selectItem(final VH holder, int position) {
        selected.set(position, true);
        onItemSelected(holder, position);
        if (!isActionModeEnabled()) actionMode = startActionMode();
    }

    public void deselectItem(final VH holder, int position) {
        if (isActionModeEnabled()) {
            selected.set(position, false);
            onItemDeselected(holder, position);
            checkActionModeEnded();
        }
    }

    public abstract void onItemSelected(final VH holder, int position);
    public abstract void onItemDeselected(final VH holder, int position);

    public void checkActionModeEnded() {
        int selectedElements = 0;
        for (Boolean b : selected) if (b) selectedElements++;
        if (selectedElements == 0) {
            finishActionMode();
        } else {
            updateActionMode(actionMode, selectedElements);
        }
    }

    private class DataChangeObserver extends RecyclerView.AdapterDataObserver {

        @Override
        public void onChanged() {
            while (items.size() != selected.size()) {
                int size = items.size();
                if (size < selected.size()) {
                    selected.remove(selected.size() - 1);
                } else if (size > selected.size()) {
                    selected.add(false);
                }
            }
        }
    }

    public abstract class AdapterActionModeCallback implements ActionMode.Callback {

        @Override
        public abstract boolean onCreateActionMode(ActionMode mode, Menu menu);

        @Override
        public abstract boolean onPrepareActionMode(ActionMode mode, Menu menu);

        @Override
        public abstract boolean onActionItemClicked(final ActionMode mode, MenuItem item);

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            resetSelected();
            actionMode = null;
            onExitActionMode(mode);
        }

        public abstract void onExitActionMode(ActionMode mode);
    }

    public abstract ActionMode startActionMode();

    public abstract void updateActionMode(ActionMode mode, int selectedCount);

    public void finishActionMode() {
        actionMode.finish();
        actionMode = null;
    }

    public boolean isActionModeEnabled() {
        return actionMode != null;
    }

}