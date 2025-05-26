package vn.edu.tlu.cse.nhom21.appbansachbooknest.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.OrderDetail;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.R;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private Context context;
    private List<OrderDetail> orderDetails;

    public OrderDetailAdapter(Context context, List<OrderDetail> orderDetails) {
        this.context = context;
        this.orderDetails = orderDetails;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetail detail = orderDetails.get(position);

        holder.tvBookTitle.setText(detail.getTitle());
        holder.tvBookAuthor.setText("Tác giả: " + detail.getAuthor());
        holder.tvBookPrice.setText(String.format("Giá: %,.0f VNĐ", detail.getPrice()));
        holder.tvQuantity.setText("Số lượng: " + detail.getQuantity());

        if (detail.getImage() != null && !detail.getImage().isEmpty()) {
            int resourceId = context.getResources().getIdentifier(detail.getImage().replace(".jpg", ""), "drawable", context.getPackageName());
            if (resourceId != 0) {
                Glide.with(context)
                        .load(resourceId)
                        .placeholder(R.drawable.book_placeholder)
                        .error(R.drawable.book_placeholder)
                        .into(holder.ivBookImage);
            } else {
                holder.ivBookImage.setImageResource(R.drawable.book_placeholder);
            }
        } else {
            holder.ivBookImage.setImageResource(R.drawable.book_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return orderDetails != null ? orderDetails.size() : 0;
    }

    static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookImage;
        TextView tvBookTitle, tvBookAuthor, tvBookPrice, tvQuantity;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookImage = itemView.findViewById(R.id.ivBookImage);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvBookPrice = itemView.findViewById(R.id.tvBookPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}