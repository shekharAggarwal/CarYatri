package com.caryatri.caryatri.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caryatri.caryatri.Database.OneWay.CabOneWay;
import com.caryatri.caryatri.R;

import java.util.List;

public class CabItemOneWayAdapter extends RecyclerView.Adapter<CabItemOneWayAdapter.ViewHolder> {

    Context context;
    List<CabOneWay> cabOneWayList;

    public CabItemOneWayAdapter(Context context, List<CabOneWay> cabOneWayList) {
        this.context = context;
        this.cabOneWayList = cabOneWayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cab_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cab_name.setText(cabOneWayList.get(position).cabModel);
        holder.cab_per_km.setText(String.valueOf(cabOneWayList.get(position).cabPrice));
        holder.cab_price.setText(String.valueOf(cabOneWayList.get(position).cabFare));
    }

    @Override
    public int getItemCount() {
        return cabOneWayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView cab_name, cab_per_km, cab_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cab_name = itemView.findViewById(R.id.cab_name);
            cab_per_km = itemView.findViewById(R.id.cab_per_km);
            cab_price = itemView.findViewById(R.id.cab_price);
        }
    }
}
