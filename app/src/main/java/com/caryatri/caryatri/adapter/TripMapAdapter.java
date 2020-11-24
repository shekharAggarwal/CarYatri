package com.caryatri.caryatri.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverDB;
import com.caryatri.caryatri.Interface.IItemClickListener;
import com.caryatri.caryatri.MapDriver;
import com.caryatri.caryatri.R;
import com.caryatri.caryatri.model.Cabs;
import com.caryatri.caryatri.model.Trip;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripMapAdapter extends RecyclerView.Adapter<TripMapAdapter.ViewHolder> {

    Context context;
    List<CurrentDriverDB> list;
    ICarYatri mService;

    public TripMapAdapter(Context context, List<CurrentDriverDB> list) {
        this.context = context;
        this.list = list;
        mService = Common.getAPI();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_current_cab, parent, false);
        return new TripMapAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if (list.get(position).CabDetails != null) {
            Trip trip = new Gson().fromJson(list.get(position).CabDetails, Trip.class);
            Cabs cab = new Gson().fromJson(trip.getCabs(), Cabs.class);
            holder.txtCarName.setText(cab.cabModel);
            holder.txtFrom.setText(trip.getSourceAddress());
            holder.txtTo.setText(trip.getDestinationAddress());
        } else
            mService.getTripDetail(Common.currentUser.getPhone(), list.get(position).DriverPhone).enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    if (response.body() != null) {
                        Common.currentDriverRepository.updateCabDetail(new Gson().toJson(response.body()), list.get(position).DriverPhone);
                        Cabs cab = new Gson().fromJson(response.body().getCabs(), Cabs.class);
                        Log.d("ERROR", response.body().getCabs());
                        holder.txtCarName.setText(cab.cabModel);
                        holder.txtFrom.setText(response.body().getSourceAddress());
                        holder.txtTo.setText(response.body().getDestinationAddress());
                    }
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                }
            });

        holder.setiItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapDriver.class);
                intent.putExtra("Phone", list.get(position).DriverPhone);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtCarName, txtFrom, txtTo;

        IItemClickListener iItemClickListener;

        public void setiItemClickListener(IItemClickListener iItemClickListener) {
            this.iItemClickListener = iItemClickListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCarName = itemView.findViewById(R.id.txtCarName);
            txtFrom = itemView.findViewById(R.id.txtFrom);
            txtTo = itemView.findViewById(R.id.txtTo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iItemClickListener.onClick(view);
        }
    }
}
