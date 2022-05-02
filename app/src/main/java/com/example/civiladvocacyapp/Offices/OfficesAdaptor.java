package com.example.civiladvocacyapp.Offices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.civiladvocacyapp.MainActivity;
import com.example.civiladvocacyapp.R;

import java.util.List;

public class OfficesAdaptor extends RecyclerView.Adapter<OfficesViewHolder> {

    private final List<Offices> officeslist;
    private final MainActivity mainAct;

    public OfficesAdaptor(List<Offices> officeslist, MainActivity mainAct) {
        this.officeslist = officeslist;
        this.mainAct = mainAct;
    }

    @NonNull
    @Override

    public OfficesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.officials_recview, parent, false);

        itemView.setOnClickListener(mainAct);           //onclick method

        return new OfficesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficesViewHolder holder, int position) {
        Offices offices = officeslist.get(position);
        holder.name.setText(offices.getName() + " (" + offices.getParty() + ")");
        holder.off.setText(offices.getOff());
    }

    @Override
    public int getItemCount() {
        return officeslist.size();
    }
}
