package com.example.quanlykhohang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlykhohang.R;
import com.example.quanlykhohang.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private TextView tvError;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tvError = findViewById(R.id.tvError);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Ẩn lỗi cũ trước khi kiểm tra lại.
        tvError.setVisibility(View.GONE);

        // Không cho phép đăng nhập khi bỏ trống.
        if (username.isEmpty() || password.isEmpty()) {
            tvError.setText(R.string.error_empty_login);
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        if (dbHelper.checkLogin(username, password)) {
            // Đúng tài khoản: sang màn hình danh sách, finish() để nút Back
            // không quay lại màn hình đăng nhập.
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // Sai tài khoản: báo lỗi, ở lại màn hình đăng nhập.
            tvError.setText(R.string.error_wrong_login);
            tvError.setVisibility(View.VISIBLE);
        }
    }
}
