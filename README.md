# Quản lý kho hàng — Bài thực hành lập trình Android

Ứng dụng Android (Java + XML) giúp chủ cửa hàng nhỏ quản lý sản phẩm trong kho:
giá bán, số lượng tồn và cảnh báo sản phẩm sắp hết hàng.
Dữ liệu lưu bằng **SQLite** qua `SQLiteOpenHelper` (không dùng Room, Firebase hay API).

## 1. Phân tích đề bài

| Yêu cầu | Cách thực hiện trong app |
| --- | --- |
| 4 màn hình: Đăng nhập, Danh sách, Thêm, Chi tiết | 4 Activity: `LoginActivity`, `MainActivity`, `AddProductActivity`, `ProductDetailActivity`, chuyển màn bằng `Intent` (truyền `id`) |
| Lớp `Product` (tên, giá, số lượng, loại, trạng thái, mô tả) | `model/Product.java` |
| CSDL `warehouse.db` v1, 2 bảng `users` + `products` | `database/DatabaseHelper.java` (kế thừa `SQLiteOpenHelper`, tạo bảng + chèn dữ liệu mẫu trong `onCreate()`) |
| Đăng nhập kiểm tra bằng truy vấn bảng `users` | `DatabaseHelper.checkLogin()` — không cố định tài khoản trong code Activity |
| Không dùng ArrayList làm nơi lưu chính | ArrayList chỉ chứa kết quả `getAllProducts()` để hiển thị; mọi ghi/đọc đều qua CSDL |
| Thống kê: tổng SP, tổng giá trị kho, số SP sắp hết hàng | `getTotalValue()` = `SUM(price * quantity)`, `countLowStock()` = `COUNT WHERE quantity < 5`, tải lại trong `onResume()` |
| Cảnh báo sắp hết hàng (số lượng < 5) | Nhãn "Sắp hết hàng" + số lượng màu đỏ ở cả danh sách và chi tiết |
| Truy vấn có tham số, đóng Cursor | Dùng `?` + `selectionArgs` ở mọi truy vấn; `Cursor` luôn đóng trong `finally` |
| Tắt app mở lại dữ liệu vẫn còn | Dữ liệu nằm trong SQLite trên máy, không lưu trong bộ nhớ |

## 2. Bảng màu — khác với bản mẫu trong đề bài

Bản mẫu trong đề dùng **xanh lá (teal)** làm màu chính và **cam** cho nút "Bán 1".
App này đổi sang tông hoàn toàn khác (đề bài cho phép tự điều chỉnh màu sắc):

| Vai trò | Màu | Mã |
| --- | --- | --- |
| Màu chính (toolbar, nút chính, thẻ thống kê, logo) | Tím violet | `#6D28D9` |
| Màu chính đậm (chip loại SP, chữ nhấn) | Tím đậm | `#4C1D95` |
| Màu nhấn (nút "Bán 1") | Vàng gold | `#D97706` |
| Nền | Tím nhạt | `#F4F3FB` |
| Cảnh báo / xóa / "Sắp hết hàng" | Đỏ | `#DC2626` |

Toàn bộ màu nằm trong `app/src/main/res/values/colors.xml` — muốn đổi tông màu
chỉ cần sửa file này.

## 3. Cấu trúc thư mục

```
QuanLyKhoHang/
├── settings.gradle                 # Tên project + module app
├── build.gradle                    # Plugin Android (AGP 8.2.0)
├── gradle.properties
├── gradle/wrapper/                  # Gradle 8.2
├── README.md
└── app/
    ├── build.gradle                # compileSdk 34, minSdk 24, targetSdk 34
    │                               # dependencies: appcompat, material, recyclerview
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml     # LoginActivity là màn hình mở đầu (LAUNCHER)
        ├── java/com/example/quanlykhohang/
        │   ├── model/
        │   │   └── Product.java            # Lớp sản phẩm
        │   ├── database/
        │   │   └── DatabaseHelper.java     # SQLiteOpenHelper + 9 phương thức theo đề
        │   ├── adapter/
        │   │   └── ProductAdapter.java     # RecyclerView: 1 dòng = 1 sản phẩm
        │   ├── utils/
        │   │   └── FormatUtils.java        # Định dạng giá: 150000 -> "150.000 đ"
        │   └── ui/
        │       ├── LoginActivity.java      # Màn 1: đăng nhập
        │       ├── MainActivity.java       # Màn 2: danh sách + thống kê
        │       ├── AddProductActivity.java # Màn 3: thêm sản phẩm
        │       └── ProductDetailActivity.java  # Màn 4: chi tiết, nhập/bán/xóa
        └── res/
            ├── layout/
            │   ├── activity_login.xml
            │   ├── activity_main.xml
            │   ├── activity_add_product.xml
            │   ├── activity_product_detail.xml
            │   └── item_product.xml        # 1 dòng sản phẩm trong danh sách
            ├── values/
            │   ├── colors.xml              # Bảng màu tím violet + vàng gold
            │   ├── strings.xml             # Toàn bộ chữ tiếng Việt + mảng loại SP
            │   └── themes.xml
            └── drawable/
                ├── bg_button_primary.xml   # Nút tím bo tròn
                ├── bg_button_gold.xml      # Nút vàng gold (Bán 1)
                ├── bg_button_danger.xml    # Nút đỏ (Xóa)
                ├── bg_button_outline.xml   # Nút viền tím (Quay lại)
                ├── bg_card.xml             # Thẻ trắng bo tròn
                ├── bg_edittext.xml         # Ô nhập bo tròn
                ├── bg_stat_card.xml        # Thẻ thống kê tím
                ├── bg_badge_category.xml   # Chip loại sản phẩm
                ├── bg_badge_low.xml        # Nhãn "Sắp hết hàng"
                ├── bg_circle_primary.xml   # Logo tròn "SP"
                ├── ic_arrow_back.xml       # Mũi tên quay lại
                └── ic_launcher_foreground.xml  # Icon app (hộp kho)
```

## 4. Cách mở và chạy

1. Mở **Android Studio** → **Open** → chọn thư mục `QuanLyKhoHang`.
2. Chờ Gradle sync xong (lần đầu cần mạng để tải AGP 8.2.0 + Gradle 8.2).
3. Chạy trên máy ảo (AVD, API 24 trở lên) hoặc điện thoại thật (bật USB Debugging).
4. Đăng nhập bằng tài khoản mẫu: **admin / 123456**.

## 5. Dữ liệu mẫu (chèn sẵn trong `onCreate()`)

Tài khoản: `admin / 123456`.

| Tên sản phẩm | Giá bán | Loại | Số lượng |
| --- | --- | --- | --- |
| Chuột không dây | 150.000 đ | Điện tử | 20 |
| Bàn phím cơ | 450.000 đ | Điện tử | 3 → **Sắp hết hàng** |
| Sổ tay A5 | 25.000 đ | Văn phòng phẩm | 40 |

Thống kê lúc mở app lần đầu: **3 sản phẩm — 5.350.000 đ — 1 sắp hết hàng**.

## 6. Luồng sử dụng (kiểm thử nhanh)

1. Mở app → đăng nhập `admin / 123456` → vào danh sách.
2. Nhấn **+ Thêm sản phẩm** → nhập tên, giá > 0, số lượng ≥ 0, chọn loại,
   bật/tắt "Đang kinh doanh", nhập mô tả → **Lưu sản phẩm** → về danh sách,
   thống kê cập nhật.
3. Nhấn vào một sản phẩm → màn chi tiết: xem giá, số lượng, giá trị tồn kho
   (giá × số lượng), mô tả, trạng thái.
4. **+ Nhập thêm 1** tăng số lượng; **- Bán 1** giảm số lượng;
   số lượng = 0 mà nhấn Bán → Toast "Hết hàng", không giảm nữa.
5. **Xóa sản phẩm** → hộp thoại xác nhận → xóa khỏi CSDL, về danh sách.
6. **Quay lại** → danh sách và thống kê hiển thị đúng số lượng mới.
7. Tắt hẳn app rồi mở lại: sản phẩm đã thêm/sửa/xóa vẫn còn nguyên
   (dữ liệu trong SQLite).

## 7. Các trường hợp lỗi đã xử lý

- Bỏ trống tên đăng nhập/mật khẩu → hiện lỗi, không cho đăng nhập.
- Sai tài khoản → "Sai tên đăng nhập hoặc mật khẩu", ở lại màn đăng nhập.
- Thêm SP: tên trống, giá rỗng/≤ 0, số lượng rỗng/âm → `setError` ngay tại ô nhập.
- Bán khi số lượng = 0 → Toast "Hết hàng".
- Danh sách rỗng → hiện dòng "Chưa có sản phẩm nào…".
- Chi tiết mở với id không tồn tại → Toast + đóng màn hình.
