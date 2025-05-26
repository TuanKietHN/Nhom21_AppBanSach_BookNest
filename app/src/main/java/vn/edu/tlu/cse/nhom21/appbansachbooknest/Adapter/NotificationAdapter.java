package vn.edu.tlu.cse.nhom21.appbansachbooknest.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse.nhom21.appbansachbooknest.Database.DatabaseHelper;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.Notification;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private Context context;
    private List<Notification> notificationList;
    private DatabaseHelper dbHelper;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList != null ? notificationList : new ArrayList<>();
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.tvMessage.setText(notification.getMessage());
        holder.tvDate.setText(notification.getDate());

        // Đánh dấu thông báo là đã đọc khi hiển thị
        if (!notification.isRead()) {
            dbHelper.markNotificationAsRead(notification.getId());
        }

        // Đổi màu nếu chưa đọc
        if (notification.isRead()) {
            holder.tvMessage.setTextColor(context.getResources().getColor(android.R.color.black));
        } else {
            holder.tvMessage.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void updateList(List<Notification> newList) {
        this.notificationList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvDate;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}