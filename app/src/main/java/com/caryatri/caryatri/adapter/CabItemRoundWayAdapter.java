package com.caryatri.caryatri.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caryatri.caryatri.Database.DataSource.RoundWay.CabRoundWay;
import com.caryatri.caryatri.R;

import java.util.List;

public class CabItemRoundWayAdapter extends RecyclerView.Adapter<CabItemRoundWayAdapter.ViewHolder> {

    Context context;
    List<CabRoundWay> cabRoundWays;

    public CabItemRoundWayAdapter(Context context, List<CabRoundWay> cabRoundWays) {
        this.context = context;
        this.cabRoundWays = cabRoundWays;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cab_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.cab_name.setText(cabRoundWays.get(position).cabModel);
        holder.cab_per_km.setText(String.valueOf(cabRoundWays.get(position).cabPrice));
        holder.cab_price.setText(String.valueOf(cabRoundWays.get(position).cabFare));
    }

    @Override
    public int getItemCount() {
        return cabRoundWays.size();
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
