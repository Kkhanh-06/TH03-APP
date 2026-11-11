package com.example.quanlykhohang.model;

/**
 * Lớp Product biểu diễn một sản phẩm trong kho.
 * Khớp với bảng "products" trong cơ sở dữ liệu (xem DatabaseHelper).
 */
public class Product {

    private int id;
    private String name;
    private long price;        // Giá bán (VNĐ), lớn hơn 0
    private int quantity;      // Số lượng tồn kho, không âm
    private String category;   // Điện tử, Thực phẩm, Văn phòng phẩm, Gia dụng, Khác
    private boolean active;    // true: đang kinh doanh, false: ngừng kinh doanh
    private String description;

    /** Dùng khi thêm sản phẩm mới (chưa có id, id do SQLite tự tăng). */
    public Product(String name, long price, int quantity, String category,
                   boolean active, String description) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.active = active;
        this.description = description;
    }

    /** Dùng khi đọc sản phẩm từ cơ sở dữ liệu (đã có id). */
    public Product(int id, String name, long price, int quantity, String category,
                   boolean active, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.active = active;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
