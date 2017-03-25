package com.duy.pascal.frontend.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.duy.pascal.frontend.R;
import com.duy.pascal.frontend.code.CodeSample;
import com.duy.pascal.frontend.data.Preferences;
import com.duy.pascal.frontend.view.code_view.CodeView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Duy on 12-Mar-17.
 */

public class SelectThemeActivity extends AbstractAppCompatActivity {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_theme);
        ButterKnife.bind(this);
        setupActionBar();

        CodeThemeAdapter codeThemeAdapter = new CodeThemeAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(codeThemeAdapter);
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        setTitle(R.string.theme);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);

    }

    public class CodeThemeAdapter extends RecyclerView.Adapter<CodeThemeAdapter.ViewHolder> {
        private String[] mThemes;
        private LayoutInflater inflater;
        private Preferences mPreferences;

        public CodeThemeAdapter(Context context) {
            mThemes = context.getResources().getStringArray(R.array.code_themes);
            inflater = LayoutInflater.from(context);
            mPreferences = new Preferences(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.code_theme_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.codeView.setTextHighlighted(CodeSample.DEMO_THEME);
            holder.codeView.setTheme(mThemes[position]);
            holder.txtTitle.setText(mThemes[position]);
            holder.btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPreferences.put(getString(R.string.key_code_theme), mThemes[position]);
                    SelectThemeActivity.this.finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mThemes.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.code_view)
            CodeView codeView;
            @BindView(R.id.txt_name)
            TextView txtTitle;
            @BindView(R.id.btn_select)
            Button btnSelect;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}