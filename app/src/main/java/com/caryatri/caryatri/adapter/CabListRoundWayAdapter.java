package com.caryatri.caryatri.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Database.DataSource.RoundWay.CabRoundWay;
import com.caryatri.caryatri.Database.OneWay.CabOneWay;
import com.caryatri.caryatri.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CabListRoundWayAdapter extends RecyclerView.Adapter<CabListRoundWayAdapter.ViewHolder> {

    Context context;
    List<CabRoundWay> cabs;
    List<String> img;


    public CabListRoundWayAdapter(Context context, List<CabRoundWay> cabs) {
        this.context = context;
        this.cabs = cabs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cab_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.txtCabBrand.setText(cabs.get(position).cabBrand);
        holder.txtCabName.setText(cabs.get(position).cabModel);
        holder.txtCabSitting.setText("" + cabs.get(position).cabSitting);

        img = new Gson().fromJson(cabs.get(position).cabImage, new TypeToken<List<String>>() {
        }.getType());

        Picasso.get().load(img.get(0)).into(holder.img_car);


        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CabRoundWay cab = cabs.get(position);
                final int index = position;
                String name = cabs.get(position).cabModel;
                //deleteItem
                removeItem(position);
                Common.cabRoundWayRepository.deleteCabRoundWayItem(cab);

                Snackbar snackbar = Snackbar.make(Common.rootLayout, new StringBuilder(name).append("Remove from Cab list").toString(),
                        Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restoreItem(cab, index);
                        Common.cabRoundWayRepository.insertToCabRoundWay(cab);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cabs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_car, img_delete;
        TextView txtCabBrand, txtCabName, txtCabSitting;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_car = itemView.findViewById(R.id.img_car);
            img_delete = itemView.findViewById(R.id.img_delete);
            txtCabBrand = itemView.findViewById(R.id.txtCabBrand);
            txtCabName = itemView.findViewById(R.id.txtCabName);
            txtCabSitting = itemView.findViewById(R.id.txtCabSitting);

        }
    }

    public void removeItem(int position) {
        cabs.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(CabRoundWay item, int position) {
        cabs.add(position, item);
        notifyItemInserted(position);
    }
}
