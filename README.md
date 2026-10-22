# ECOMER – quản lý sản phẩm Firebase

Ứng dụng Android Studio đáp ứng bài kiểm tra: hiển thị/thêm/sửa/xóa sản phẩm, hình ảnh theo từng sản phẩm, đăng nhập/đăng xuất và phân quyền giao diện. Ảnh được nén và lưu Base64 trong Firestore, nên dùng được với gói Firebase Spark miễn phí, không cần Cloud Storage.

## Chuẩn bị Firebase

1. Tạo Firebase project, thêm Android app có package `com.thanhnhat.ecomer`.
2. Chép tệp `google-services.json` tải từ Firebase Console vào `app/google-services.json` (tệp này không được kèm trong source vì chứa định danh dự án riêng).
3. Bật **Authentication → Email/Password**, tạo tài khoản `admin@ecomer.com` (hoặc thay email tại `MainActivity.kt`).
4. Tạo **Cloud Firestore**. Không cần bật Cloud Storage.
5. Mở bằng Android Studio, cho Gradle đồng bộ, sau đó chạy trên máy/emulator API 24+.

## Cấu trúc dữ liệu Firestore

Mỗi document của `products` gồm đúng bốn trường nghiệp vụ: `name`, `price`, `category`, `imageUrl` (ảnh Base64 sau khi đã nén). ID document do Firestore tạo và không phải trường nhập liệu.

## Quy tắc Firebase mẫu (demo lớp học)

Thay `ADMIN_UID` bằng UID của tài khoản admin trong Firebase Authentication:

```text
// Firestore rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /products/{id} {
      allow read: if request.auth != null;
      allow create, update, delete: if request.auth != null && request.auth.uid == 'ADMIN_UID';
    }
  }
}

```
