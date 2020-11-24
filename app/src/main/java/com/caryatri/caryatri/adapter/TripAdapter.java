package com.caryatri.caryatri.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.caryatri.caryatri.AfterRide;
import com.caryatri.caryatri.Interface.IItemClickListener;
import com.caryatri.caryatri.R;
import com.caryatri.caryatri.model.Trip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    Context context;
    List<Trip> tripList;
    FragmentManager fragment;

    public TripAdapter(Context context, List<Trip> tripList, FragmentManager fragment) {
        this.context = context;
        this.tripList = tripList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mytrip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.txtFrom.setText(tripList.get(position).getSourceAddress() + ", " + tripList.get(position).getSource());
        holder.txtTo.setText(tripList.get(position).getDestinationAddress() + ", " + tripList.get(position).getDestination());
        float price = Float.parseFloat(tripList.get(position).getCabFare()) + Float.parseFloat(tripList.get(position).getTripToll());
        holder.txtPrice.setText("₹" + price);
        holder.txtDateTime.setText(getDate(tripList.get(position).getStartTrip()) + " " + context.getResources().getString(R.string.arrow) + " " + getDate(tripList.get(position).getDropTrip()));

        holder.setiItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {
                if (tripList.get(position).getCabDriver() != null) {
                    AfterRide afterRide = new AfterRide(tripList.get(position).getCabDriver(), tripList.get(position));
                    afterRide.showNow(fragment, "SS");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtFrom, txtTo, txtPrice, txtDateTime;

        IItemClickListener iItemClickListener;

        public void setiItemClickListener(IItemClickListener iItemClickListener) {
            this.iItemClickListener = iItemClickListener;
        }


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtFrom = itemView.findViewById(R.id.txtFrom);
            txtTo = itemView.findViewById(R.id.txtTo);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtDateTime = itemView.findViewById(R.id.txtDateTime);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            iItemClickListener.onClick(v);
        }
    }

    //converting 24 hr into 12 hrs
    private String getAMPM(int hours, int mintus) {
        int hour = hours;
        int minutes = mintus;
        String timeSet = "";
        if (hour > 12) {
            hour -= 12;
            timeSet = "PM";
        } else if (hour == 0) {
            hour += 12;
            timeSet = "AM";
        } else if (hour == 12) {
            timeSet = "PM";
        } else {
            timeSet = "AM";
        }

        String min = "";
        if (minutes < 10) {
            min = "0" + minutes;
        } else {
            min = String.valueOf(minutes);
        }
        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hour).append(':')
                .append(min).append(" ").append(timeSet).toString();

        return aTime;
    }

    //splitting date and time
    private String getDate(String date) {
        String[] arr = date.split(" ", 2);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        try {
            date1 = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date1 != null)
            return arr[0] + "/" + getAMPM(date1.getHours(), date1.getMinutes());
        else
            return arr[0] + "/" + arr[1];
    }
}
