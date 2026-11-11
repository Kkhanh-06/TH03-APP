package com.example.quanlykhohang.utils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Các hàm định dạng dùng chung cho toàn app.
 */
public final class FormatUtils {

    private FormatUtils() {
        // Không cho khởi tạo.
    }

    /**
     * Định dạng giá tiền theo kiểu Việt Nam, ví dụ: 150000 -> "150.000 đ".
     */
    public static String formatPrice(long price) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(price) + " đ";
    }
}
