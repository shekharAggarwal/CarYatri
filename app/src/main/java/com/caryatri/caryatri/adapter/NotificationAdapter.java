package com.caryatri.caryatri.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.caryatri.caryatri.Database.Notification.NotificationDB;
import com.caryatri.caryatri.R;

import java.util.List;

import io.paperdb.Paper;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Activity context;
    private List<NotificationDB> notificationDBList;

    public NotificationAdapter(Activity context, List<NotificationDB> notificationDBList, FragmentManager fragment) {
        this.context = context;
        this.notificationDBList = notificationDBList;
        Paper.init(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.txtNotificationData.setText(notificationDBList.get(position).notificationData);
    }

    @Override
    public int getItemCount() {
        return notificationDBList.size();
    }

    public void removeItem(int position) {
        notificationDBList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(NotificationDB item, int position) {
        notificationDBList.add(position, item);
        notifyItemInserted(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout view_background;
        public LinearLayout view_foreground;
        TextView txtNotificationData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNotificationData = itemView.findViewById(R.id.notification_data);
            view_background = itemView.findViewById(R.id.view_background);
            view_foreground = itemView.findViewById(R.id.view_foreground);

        }
    }
}
