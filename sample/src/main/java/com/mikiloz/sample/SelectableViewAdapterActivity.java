package com.mikiloz.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mikiloz.fancyadapters.SelectableViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectableViewAdapterActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_nopadding);

        List<String> items = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            items.add("Item " + i);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new CustomAdapter(items, recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

    }

    public class CustomAdapter extends SelectableViewAdapter<String, CustomAdapter.CustomViewHolder> {

        public CustomAdapter(List<String> items, RecyclerView recyclerView) {
            super(items, recyclerView, ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END);
        }

        @Override
        public CustomViewHolder onCreateSelectableViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_view_selectable_view, parent, false);
            return new CustomViewHolder(itemView);
        }

        @Override
        public void onBindSelectableViewHolder(CustomViewHolder holder, int position) {
            holder.position.setText(String.valueOf(position));
            holder.textView.setText(items.get(position));
        }

        @Override
        public ActionMode startActionMode() {
            return startSupportActionMode(new AdapterActionModeCallback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    mode.setTitle("1 item(s)");
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.star:
                            Toast.makeText(getBaseContext(),
                                    "Adding " + getSelected().size() + " element(s) to favourites",
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onExitActionMode(ActionMode mode) {

                }
            });
        }

        @Override
        public void updateActionMode(ActionMode mode, int selectedCount) {
            mode.setTitle(selectedCount + " item(s)");
        }

        @Override
        public void onMove(CustomViewHolder viewHolder, CustomViewHolder target) {
            //Toast.makeText(getBaseContext(), "Item " + viewHolder.getAdapterPosition() + " moved", Toast.LENGTH_SHORT).show();
            String s = items.remove(viewHolder.getAdapterPosition());
            items.add(target.getAdapterPosition(), s);
        }

        @Override
        public void onSwipe(CustomViewHolder viewHolder, int direction) {
            //Toast.makeText(getBaseContext(), "Item " + viewHolder.getAdapterPosition() + " swiped", Toast.LENGTH_SHORT).show();
            items.remove(viewHolder.getAdapterPosition());
        }

        public class CustomViewHolder extends SelectableViewAdapter.ViewHolder {

            TextView textView;
            TextView position;

            public CustomViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.textview);
                position = (TextView) itemView.findViewById(R.id.position);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (!isActionModeEnabled()) {
                            triggerSelectionMode(CustomViewHolder.this, getLayoutPosition());
                            return true;
                        } else return false;
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isActionModeEnabled()) toggleItem(CustomViewHolder.this, getLayoutPosition());
                    }
                });

            }

            @Override
            public int getSelectableViewID() {
                return R.id.position;
            }

            @Override
            public int getSelectedIndicatorResourceID() {
                return R.layout.selected_indicator_lesspadding;
            }
        }

    }

}
