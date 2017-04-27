/*
 *  Copyright 2017 Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.pascal.frontend.view.code_view;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.duy.pascal.frontend.R;

import java.util.ArrayList;

/**
 * Created by Duy on 26-Apr-17.
 */

public class CodeSuggestAdapter extends ArrayAdapter<SuggestItem> {
    private static final String TAG = "CodeSuggestAdapter";
    private final Context context;
    private LayoutInflater inflater;
    private ArrayList<SuggestItem> items;
    private ArrayList<SuggestItem> itemsAll;
    private ArrayList<SuggestItem> suggestion;
    private int resourceID;
    private Filter codeFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((SuggestItem) resultValue).getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            suggestion.clear();
            if (constraint != null) {
                for (SuggestItem item : itemsAll) {
                    if (item.getName().startsWith(constraint.toString())) {
                        suggestion.add(item);
                    }
                }
                filterResults.values = suggestion;
                filterResults.count = suggestion.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<SuggestItem> filteredList = (ArrayList<SuggestItem>) results.values;
            clear();
            if (results != null && results.count > 0) {
                for (SuggestItem suggestItem : filteredList) {
                    add(suggestItem);
                }
            }
            notifyDataSetChanged();
        }
    };


    public CodeSuggestAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<SuggestItem> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.items = objects;
        this.itemsAll = (ArrayList<SuggestItem>) items.clone();
        this.suggestion = new ArrayList<>();
        this.resourceID = resource;
    }

    public ArrayList<SuggestItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<SuggestItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resourceID, null);
        }
        final SuggestItem item = items.get(position);

        TextView txtName = (TextView) convertView.findViewById(R.id.txt_title);
        txtName.setText(item.getName());
        View btnInfo = convertView.findViewById(R.id.img_info);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, item.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return codeFilter;
    }

    public void clearAll() {
        clear();
        items.clear();
        itemsAll.clear();
    }
}