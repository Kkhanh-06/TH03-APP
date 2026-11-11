package com.example.quanlykhohang.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.quanlykhohang.R;
import com.example.quanlykhohang.database.DatabaseHelper;
import com.example.quanlykhohang.model.Product;

public class AddProductActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtPrice;
    private EditText edtQuantity;
    private Spinner spinnerCategory;
    private Switch switchActive;
    private EditText edtDescription;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);

        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtQuantity = findViewById(R.id.edtQuantity);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        switchActive = findViewById(R.id.switchActive);
        edtDescription = findViewById(R.id.edtDescription);
        Button btnSave = findViewById(R.id.btnSave);

        // Mặc định: sản phẩm đang kinh doanh.
        switchActive.setChecked(true);

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = edtName.getText().toString().trim();
        if (name.isEmpty()) {
            edtName.setError(getString(R.string.error_empty_name));
            edtName.requestFocus();
            return;
        }

        long price = parseLongOrNegative(edtPrice.getText().toString().trim());
        if (price <= 0) {
            edtPrice.setError(getString(R.string.error_invalid_price));
            edtPrice.requestFocus();
            return;
        }

        int quantity = parseIntOrNegative(edtQuantity.getText().toString().trim());
        if (quantity < 0) {
            edtQuantity.setError(getString(R.string.error_invalid_quantity));
            edtQuantity.requestFocus();
            return;
        }

        String category = spinnerCategory.getSelectedItem().toString();
        boolean active = switchActive.isChecked();
        String description = edtDescription.getText().toString().trim();

        Product product = new Product(name, price, quantity, category, active, description);
        long id = dbHelper.insertProduct(product);

        if (id > 0) {
            Toast.makeText(this, R.string.toast_added, Toast.LENGTH_SHORT).show();
            // Quay về danh sách; MainActivity sẽ tải lại trong onResume().
            finish();
        } else {
            Toast.makeText(this, R.string.toast_save_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private long parseLongOrNegative(String text) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int parseIntOrNegative(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
