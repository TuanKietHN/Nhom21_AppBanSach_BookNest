package vn.edu.tlu.cse.nhom21.appbansachbooknest.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.CartItem;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartList;
    private OnDeleteClickListener onDeleteClickListener;

    public CartAdapter(Context context, List<CartItem> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartList.get(position);
        holder.tvBookTitle.setText(cartItem.getBookTitle());
        holder.tvBookAuthor.setText("Tác giả: " + cartItem.getBookAuthor());
        holder.tvQuantity.setText("Số lượng: " + cartItem.getQuantity());
        holder.tvBookPrice.setText(String.format("%,.0f VNĐ", cartItem.getBookPrice()));

        // Load ảnh bìa sách
        String imageName = cartItem.getBookImage();
        if (imageName != null && imageName.contains(".")) {
            imageName = imageName.substring(0, imageName.lastIndexOf("."));
        }
        int resId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        if (resId != 0) {
            holder.ivBookImage.setImageResource(resId);
        } else {
            holder.ivBookImage.setImageResource(R.drawable.book_placeholder);
        }

        // Thiết lập sự kiện xóa
        holder.btnDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(cartItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList != null ? cartList.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookImage;
        TextView tvBookTitle, tvBookAuthor, tvQuantity, tvBookPrice;
        Button btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookImage = itemView.findViewById(R.id.ivBookImage);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvBookPrice = itemView.findViewById(R.id.tvBookPrice);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(CartItem cartItem);
    }
}