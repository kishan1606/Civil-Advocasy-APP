package com.example.civiladvocacyapp.Offices;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.civiladvocacyapp.R;

public class OfficesViewHolder extends RecyclerView.ViewHolder {

    TextView off;
    TextView name;

    OfficesViewHolder(View view){
        super(view);
        off = view.findViewById(R.id.rec_office);
        name = view.findViewById(R.id.rec_name);
    }
}
