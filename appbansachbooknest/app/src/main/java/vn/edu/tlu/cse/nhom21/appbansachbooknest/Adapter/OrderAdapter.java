package vn.edu.tlu.cse.nhom21.appbansachbooknest.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.Order;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.R;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnConfirmClickListener confirmClickListener;

    public interface OnConfirmClickListener {
        void onConfirmClick(int orderId);
    }

    // Constructor cho nhân viên (có listener)
    public OrderAdapter(Context context, List<Order> orderList, OnConfirmClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.confirmClickListener = listener;
    }

    // Constructor cho khách hàng (không cần listener)
    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.confirmClickListener = null;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvOrderId.setText("Mã đơn hàng: #" + order.getOrderId());
        holder.tvUsername.setText("Khách hàng: " + order.getUsername());
        holder.tvOrderDate.setText("Ngày đặt: " + order.getOrderDate());
        holder.tvOrderStatus.setText("Trạng thái: " + (order.getStatus().equals("confirmed") ? "Đã xác nhận" : "Chưa xác nhận"));

        // Thiết lập RecyclerView con để hiển thị chi tiết đơn hàng
        OrderDetailAdapter detailAdapter = new OrderDetailAdapter(context, order.getOrderDetails());
        holder.rvOrderDetails.setLayoutManager(new LinearLayoutManager(context));
        holder.rvOrderDetails.setAdapter(detailAdapter);

        // Ẩn nút "Xác nhận" nếu không có listener (dành cho khách hàng)
        if (confirmClickListener == null) {
            holder.btnConfirmOrder.setVisibility(View.GONE);
        } else {
            if (order.getStatus().equals("confirmed")) {
                holder.btnConfirmOrder.setVisibility(View.GONE);
            } else {
                holder.btnConfirmOrder.setVisibility(View.VISIBLE);
                holder.btnConfirmOrder.setOnClickListener(v -> confirmClickListener.onConfirmClick(order.getOrderId()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvUsername, tvOrderDate, tvOrderStatus;
        RecyclerView rvOrderDetails;
        Button btnConfirmOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            rvOrderDetails = itemView.findViewById(R.id.rvOrderDetails);
            btnConfirmOrder = itemView.findViewById(R.id.btnConfirmOrder);
        }
    }
}