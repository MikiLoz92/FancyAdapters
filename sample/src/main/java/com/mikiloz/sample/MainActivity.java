package com.mikiloz.sample;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mikiloz.fancyadapters.SelectableViewAdapter;
import com.mikiloz.fancyadapters.SuperSelectableAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button selectableAdapterButton, superSelectableAdapterButton, selectableViewAdapterButton,
            handleAdapterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectableAdapterButton = (Button) findViewById(R.id.selectable_adapter_button);
        superSelectableAdapterButton = (Button) findViewById(R.id.super_selectable_adapter_button);
        selectableViewAdapterButton = (Button) findViewById(R.id.selectable_view_adapter_button);
        handleAdapterButton = (Button) findViewById(R.id.handle_adapter_button);

        selectableAdapterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SelectableAdapterActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        superSelectableAdapterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SuperSelectableAdapterActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        selectableViewAdapterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SelectableViewAdapterActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        handleAdapterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HandleAdapterActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

    }

}
