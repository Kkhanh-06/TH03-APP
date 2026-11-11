package com.example.quanlykhohang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlykhohang.R;
import com.example.quanlykhohang.adapter.ProductAdapter;
import com.example.quanlykhohang.database.DatabaseHelper;
import com.example.quanlykhohang.model.Product;
import com.example.quanlykhohang.utils.FormatUtils;

import java.util.List;

/**
 * Màn hình 2: Danh sách sản phẩm.
 * Hiển thị khối thống kê (tổng số sản phẩm, tổng giá trị kho,
 * số sản phẩm sắp hết hàng) và danh sách sản phẩm.
 * Nhấn vào một dòng để mở màn hình chi tiết (truyền id qua Intent).
 * Tải lại danh sách + thống kê trong onResume() sau khi thêm/xóa/
 * đổi số lượng ở màn hình khác.
 */
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ProductAdapter adapter;
    private TextView tvTotalProducts;
    private TextView tvTotalValue;
    private TextView tvLowStock;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStock = findViewById(R.id.tvLowStock);
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(productId -> {
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, productId);
            startActivity(intent);
        });
        rvProducts.setAdapter(adapter);

        findViewById(R.id.btnAdd).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddProductActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    /** Đọc lại toàn bộ dữ liệu từ CSDL và cập nhật giao diện. */
    private void loadData() {
        List<Product> products = dbHelper.getAllProducts();
        adapter.setProducts(products);

        // Xử lý trường hợp danh sách rỗng.
        tvEmpty.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);

        tvTotalProducts.setText(String.valueOf(products.size()));
        tvTotalValue.setText(FormatUtils.formatPrice(dbHelper.getTotalValue()));
        tvLowStock.setText(String.valueOf(dbHelper.countLowStock()));
    }
}
