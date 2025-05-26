package vn.edu.tlu.cse.nhom21.appbansachbooknest.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

import vn.edu.tlu.cse.nhom21.appbansachbooknest.Database.DatabaseHelper;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.Book;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.R;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private Context context;
    private List<Book> bookList;
    private boolean isEditMode; // Tham số boolean để kiểm soát chế độ chỉnh sửa
    private OnItemClickListener onItemClickListener;
    private DatabaseHelper dbHelper; // Thêm DatabaseHelper
    private int userId; // Thêm userId để sử dụng khi thêm vào giỏ hàng

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    public BookAdapter(Context context, List<Book> bookList, boolean isEditMode, int userId) {
        this.context = context;
        this.bookList = bookList;
        this.isEditMode = isEditMode;
        this.userId = userId; // Nhận userId từ constructor
        this.dbHelper = new DatabaseHelper(context); // Khởi tạo DatabaseHelper
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        // Hiển thị thông tin sách
        holder.tvBookTitle.setText(book.getTitle());
        holder.tvBookAuthor.setText("Tác giả: " + book.getAuthor());
        holder.tvBookGenre.setText("Thể loại: " + book.getGenre());
        holder.tvBookPrice.setText(String.format("Giá: %,.0f VNĐ", book.getPrice()));

        // Tải ảnh bìa sách (nếu có)
        if (book.getImage() != null && !book.getImage().isEmpty()) {
            // Sử dụng tên file ảnh từ res/drawable
            int resourceId = context.getResources().getIdentifier(book.getImage().replace(".jpg", ""), "drawable", context.getPackageName());
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

        // Ẩn/hiện nút "Thêm vào giỏ hàng" dựa trên chế độ
        if (isEditMode) {
            holder.btnAddToCart.setVisibility(View.GONE); // Ẩn nút trong chế độ chỉnh sửa
            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(book);
                }
            });
        } else {
            holder.btnAddToCart.setVisibility(View.VISIBLE); // Hiển thị nút trong chế độ xem
            holder.btnAddToCart.setOnClickListener(v -> {
                // Logic thêm vào giỏ hàng (dành cho khách hàng)
                if (userId == -1) {
                    Toast.makeText(context, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean success = dbHelper.addToCart(userId, book.getId(), 1); // Thêm 1 cuốn vào giỏ hàng
                if (success) {
                    Toast.makeText(context, "Đã thêm " + book.getTitle() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Thêm vào giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    public void updateList(List<Book> newList) {
        this.bookList = newList;
        notifyDataSetChanged();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookImage;
        TextView tvBookTitle, tvBookAuthor, tvBookGenre, tvBookPrice;
        Button btnAddToCart;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookImage = itemView.findViewById(R.id.ivBookImage);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvBookGenre = itemView.findViewById(R.id.tvBookGenre);
            tvBookPrice = itemView.findViewById(R.id.tvBookPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}