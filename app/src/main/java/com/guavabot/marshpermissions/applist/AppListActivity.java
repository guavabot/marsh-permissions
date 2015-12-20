package com.guavabot.marshpermissions.applist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guavabot.marshpermissions.BaseActivity;
import com.guavabot.marshpermissions.R;
import com.guavabot.marshpermissions.model.App;
import com.guavabot.marshpermissions.settings.SettingsActivity;
import com.guavabot.marshpermissions.widget.DividerItemDecoration;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Displays the screen with the list of apps that target Marshmallow.
 */
public class AppListActivity extends BaseActivity implements AppListView {

    @Inject
    AppListPresenter mAppListPresenter;

    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        mAdapter = new Adapter();
        recyclerView.setAdapter(mAdapter);

        initializeInjector();
    }

    private void initializeInjector() {
        AppListComponent appListComponent = DaggerAppListComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .appListModule(new AppListModule(this))
                .build();
        appListComponent.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppListPresenter.load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_save) {
            Intent intent = SettingsActivity.getStartIntent(this);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startAppInfo(String packageName) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void setItems(List<App> apps) {
        mAdapter.setItems(apps);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private List<App> mApps = Collections.emptyList();

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(AppListActivity.this);
            View view = inflater.inflate(R.layout.list_item, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            App app = getItem(position);
            holder.mText1.setText(app.getPackage());
            holder.mHideBtn.setVisibility(mAppListPresenter.isDisplayItemButton() ?
                    View.GONE : View.VISIBLE);
        }

        void setItems(List<App> apps) {
            mApps = apps;
            notifyDataSetChanged();
        }

        App getItem(int position) {
            return mApps.get(position);
        }

        @Override
        public int getItemCount() {
            return mApps.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            final TextView mText1;
            final TextView mHideBtn;

            public Holder(View itemView) {
                super(itemView);
                mText1 = (TextView) itemView.findViewById(R.id.text1);
                mHideBtn = (TextView) itemView.findViewById(R.id.hide);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAppListPresenter.onItemClicked(mAdapter.getItem(getAdapterPosition()));
                    }
                });
                mHideBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAppListPresenter.onItemButtonClicked(mAdapter.getItem(getAdapterPosition()));
                    }
                });
            }
        }
    }

}