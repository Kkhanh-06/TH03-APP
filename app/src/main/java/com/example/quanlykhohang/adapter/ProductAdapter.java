package com.example.quanlykhohang.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlykhohang.R;
import com.example.quanlykhohang.database.DatabaseHelper;
import com.example.quanlykhohang.model.Product;
import com.example.quanlykhohang.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(int productId);
    }

    private List<Product> products = new ArrayList<>();
    private final OnProductClickListener listener;

    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products == null ? new ArrayList<>() : products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(holder.itemView.getContext().getString(
                R.string.product_price_format, FormatUtils.formatPrice(product.getPrice())));
        holder.tvCategory.setText(product.getCategory());
        holder.tvQuantity.setText(holder.itemView.getContext().getString(
                R.string.product_quantity_format, product.getQuantity()));

        boolean lowStock = product.getQuantity() < DatabaseHelper.LOW_STOCK_THRESHOLD;
        holder.tvLowStock.setVisibility(lowStock ? View.VISIBLE : View.GONE);
        holder.tvQuantity.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                lowStock ? R.color.danger : R.color.text_primary));

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product.getId()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvPrice;
        TextView tvCategory;
        TextView tvLowStock;
        TextView tvQuantity;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvLowStock = itemView.findViewById(R.id.tvLowStock);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}
