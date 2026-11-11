package com.example.quanlykhohang.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.quanlykhohang.model.Product;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "warehouse.db";
    public static final int DATABASE_VERSION = 1;

    // ---- Bảng users ----
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    // ---- Bảng products ----
    public static final String TABLE_PRODUCTS = "products";
    public static final String COL_PRODUCT_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_PRICE = "price";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_CATEGORY = "category";
    public static final String COL_ACTIVE = "active";
    public static final String COL_DESCRIPTION = "description";

    /** Ngưỡng cảnh báo "Sắp hết hàng": số lượng dưới 5. */
    public static final int LOW_STOCK_THRESHOLD = 5;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bảng users
        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COL_PASSWORD + " TEXT NOT NULL)");

        // Bảng products
        db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + " ("
                + COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_PRICE + " INTEGER NOT NULL, "
                + COL_QUANTITY + " INTEGER NOT NULL, "
                + COL_CATEGORY + " TEXT NOT NULL, "
                + COL_ACTIVE + " INTEGER NOT NULL, "
                + COL_DESCRIPTION + " TEXT)");

        // Tài khoản mẫu
        insertUser(db, "admin", "123456");

        // 3 sản phẩm mẫu (đang kinh doanh: active = 1)
        insertProduct(db, new Product("Chuột không dây", 150000, 20, "Điện tử", true, ""));
        insertProduct(db, new Product("Bàn phím cơ", 450000, 3, "Điện tử", true, ""));
        insertProduct(db, new Product("Sổ tay A5", 25000, 40, "Văn phòng phẩm", true, ""));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ==================== users ====================

    /** Thêm tài khoản mới vào bảng users. */
    public long insertUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return insertUser(db, username, password);
        } finally {
            db.close();
        }
    }

    private long insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    /**
     * Kiểm tra đăng nhập: trả về true nếu có dòng khớp
     * username + password trong bảng users.
     */
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS,
                    new String[]{COL_USER_ID},
                    COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?",
                    new String[]{username, password},
                    null, null, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    // ==================== products ====================

    /** Thêm sản phẩm, trả về id vừa thêm (trả về -1 nếu lỗi). */
    public long insertProduct(Product product) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return insertProduct(db, product);
        } finally {
            db.close();
        }
    }

    private long insertProduct(SQLiteDatabase db, Product product) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, product.getName());
        values.put(COL_PRICE, product.getPrice());
        values.put(COL_QUANTITY, product.getQuantity());
        values.put(COL_CATEGORY, product.getCategory());
        values.put(COL_ACTIVE, product.isActive() ? 1 : 0);
        values.put(COL_DESCRIPTION, product.getDescription());
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    /** Lấy toàn bộ sản phẩm để hiển thị danh sách. */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null,
                    COL_PRODUCT_ID + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    products.add(cursorToProduct(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return products;
    }

    /** Lấy một sản phẩm theo id (trả về null nếu không tìm thấy). */
    public Product getProductById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_PRODUCTS, null,
                    COL_PRODUCT_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursorToProduct(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    /**
     * Cập nhật số lượng (dùng cho nút "Nhập thêm 1" / "Bán 1").
     * Trả về true nếu cập nhật thành công.
     */
    public boolean updateQuantity(int id, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_QUANTITY, quantity);
            int rows = db.update(TABLE_PRODUCTS, values,
                    COL_PRODUCT_ID + " = ?",
                    new String[]{String.valueOf(id)});
            return rows > 0;
        } finally {
            db.close();
        }
    }

    /** Xóa sản phẩm theo id. Trả về true nếu xóa thành công. */
    public boolean deleteProduct(int id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            int rows = db.delete(TABLE_PRODUCTS,
                    COL_PRODUCT_ID + " = ?",
                    new String[]{String.valueOf(id)});
            return rows > 0;
        } finally {
            db.close();
        }
    }

    /** Tổng giá trị kho: SUM(price * quantity). */
    public long getTotalValue() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT SUM(" + COL_PRICE + " * " + COL_QUANTITY + ") FROM " + TABLE_PRODUCTS,
                    null);
            if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) {
                return cursor.getLong(0);
            }
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    /** Số sản phẩm sắp hết hàng: COUNT với quantity < 5. */
    public int countLowStock() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM " + TABLE_PRODUCTS
                            + " WHERE " + COL_QUANTITY + " < " + LOW_STOCK_THRESHOLD,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    /** Chuyển một dòng Cursor thành đối tượng Product. */
    private Product cursorToProduct(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
        long price = cursor.getLong(cursor.getColumnIndexOrThrow(COL_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY));
        boolean active = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTIVE)) == 1;
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION));
        return new Product(id, name, price, quantity, category, active,
                description == null ? "" : description);
    }
}
