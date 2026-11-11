package com.example.quanlykhohang.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.quanlykhohang.R;
import com.example.quanlykhohang.database.DatabaseHelper;
import com.example.quanlykhohang.model.Product;
import com.example.quanlykhohang.utils.FormatUtils;

/**
 * Màn hình 4: Chi tiết sản phẩm.
 * Nhận id qua Intent, truy vấn lại cơ sở dữ liệu theo id để hiển thị.
 * Cho phép nhập thêm 1, bán 1, quay lại, xóa sản phẩm.
 */
public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "product_id";

    private int productId;
    private Product product;
    private DatabaseHelper dbHelper;

    private TextView tvName;
    private TextView tvCategory;
    private TextView tvPrice;
    private TextView tvQuantity;
    private TextView tvStockValue;
    private TextView tvDescription;
    private TextView tvStatus;
    private TextView tvLowStockBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);
        if (productId == -1) {
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);

        tvName = findViewById(R.id.tvName);
        tvCategory = findViewById(R.id.tvCategory);
        tvPrice = findViewById(R.id.tvPrice);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvStockValue = findViewById(R.id.tvStockValue);
        tvDescription = findViewById(R.id.tvDescription);
        tvStatus = findViewById(R.id.tvStatus);
        tvLowStockBadge = findViewById(R.id.tvLowStockBadge);

        Button btnRestock = findViewById(R.id.btnRestock);
        Button btnSell = findViewById(R.id.btnSell);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnDelete = findViewById(R.id.btnDelete);

        btnRestock.setOnClickListener(v -> {
            dbHelper.updateQuantity(productId, product.getQuantity() + 1);
            loadProduct();
            Toast.makeText(this, R.string.toast_restocked, Toast.LENGTH_SHORT).show();
        });

        btnSell.setOnClickListener(v -> {
            if (product.getQuantity() == 0) {
                Toast.makeText(this, R.string.toast_out_of_stock, Toast.LENGTH_SHORT).show();
                return;
            }
            dbHelper.updateQuantity(productId, product.getQuantity() - 1);
            loadProduct();
        });

        btnBack.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(getString(R.string.delete_confirm_message, product.getName()))
                .setPositiveButton(R.string.delete_yes, (dialog, which) -> {
                    dbHelper.deleteProduct(productId);
                    Toast.makeText(this, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show());

        loadProduct();
    }

    /** Truy vấn lại sản phẩm theo id và cập nhật toàn bộ giao diện. */
    private void loadProduct() {
        product = dbHelper.getProductById(productId);
        if (product == null) {
            Toast.makeText(this, R.string.toast_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvName.setText(product.getName());
        tvCategory.setText(product.getCategory());
        tvPrice.setText(FormatUtils.formatPrice(product.getPrice()));
        tvQuantity.setText(String.valueOf(product.getQuantity()));
        tvStockValue.setText(FormatUtils.formatPrice(
                product.getPrice() * product.getQuantity()));
        tvDescription.setText(product.getDescription().isEmpty()
                ? getString(R.string.no_description)
                : product.getDescription());
        tvStatus.setText(product.isActive()
                ? R.string.status_active : R.string.status_inactive);

        boolean lowStock = product.getQuantity() < DatabaseHelper.LOW_STOCK_THRESHOLD;
        tvLowStockBadge.setVisibility(lowStock ? View.VISIBLE : View.GONE);
        tvQuantity.setTextColor(ContextCompat.getColor(this,
                lowStock ? R.color.danger : R.color.text_primary));
    }
}
