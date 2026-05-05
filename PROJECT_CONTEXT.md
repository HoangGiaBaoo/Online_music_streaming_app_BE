# PROJECT CONTEXT — App Nghe Nhạc Trực Tuyến

> File này dành cho Claude Code. Đọc toàn bộ trước khi làm bất cứ thứ gì.
> Đây là tóm tắt đầy đủ từ buổi thiết kế với developer, bao gồm tech stack,
> kiến trúc, database schema, và lộ trình phát triển.

---

## 1. Tổng quan dự án

Ứng dụng nghe nhạc trực tuyến tương tự Spotify, dành cho người dùng Việt Nam.
Đây là project học tập / sinh viên, ưu tiên đơn giản và dễ hiểu hơn là tối ưu hoá.

---

## 2. Tech Stack

| Thành phần       | Công nghệ                              |
|------------------|----------------------------------------|
| Mobile client    | Android Studio + Java                  |
| Backend / API    | Spring Boot + Java                     |
| Database         | SQL Server (Microsoft)                 |
| ORM              | JPA / Hibernate                        |
| Authentication   | Spring Security + JWT                  |
| HTTP client (Android) | Retrofit 2                        |
| Media player (Android) | ExoPlayer                        |
| File storage     | Thư mục local trên server Spring Boot  |

**Lưu ý quan trọng về file storage:**
- File `.mp3` và ảnh bìa KHÔNG lưu vào SQL Server
- Lưu vào thư mục trên máy chạy Spring Boot (ví dụ: `D:/music-files/audio/`)
- SQL Server chỉ lưu URL dạng text: `http://localhost:8080/audio/ten-file.mp3`
- Khi deploy production thì thay bằng Azure Blob Storage hoặc MinIO

---

## 3. Kiến trúc hệ thống (3 lớp)

```
┌─────────────────────────────────────────────┐
│           ANDROID CLIENT (Java)              │
│  Activities/Fragments → ViewModel            │
│  Retrofit (gọi REST API) + ExoPlayer (phát) │
└──────────────────┬──────────────────────────┘
                   │ REST API / JSON (HTTP)
                   │ Bearer JWT Token
┌──────────────────▼──────────────────────────┐
│         SPRING BOOT BACKEND (Java)           │
│  Controller → Service → Repository          │
│  Spring Security + JWT Filter                │
│  JPA / Hibernate → SQL Server               │
│  Static file serving (audio, images)         │
└──────────┬──────────────────┬───────────────┘
           │ JDBC/JPA         │ File I/O
┌──────────▼──────┐  ┌────────▼──────────────┐
│   SQL Server    │  │   File Storage        │
│  Dữ liệu cấu   │  │  /audio/*.mp3         │
│  trúc (12 bảng)│  │  /images/*.jpg        │
└─────────────────┘  └───────────────────────┘
```

---

## 4. Database Schema (SQL Server)

Script tạo bảng đầy đủ nằm ở file: `music_app_database.sql`

### Danh sách 12 bảng theo thứ tự tạo:

#### Nhóm 1 — Bảng gốc (không FK)
| Bảng | Mô tả |
|------|-------|
| `Users` | Tài khoản người dùng |
| `Artists` | Nghệ sĩ |
| `Genres` | Thể loại nhạc |

#### Nhóm 2 — Phụ thuộc bảng gốc
| Bảng | Mô tả |
|------|-------|
| `Subscriptions` | Gói đăng ký (free/premium/family) |
| `Albums` | Album nhạc |

#### Nhóm 3 — Trung gian và hoạt động
| Bảng | Mô tả |
|------|-------|
| `Tracks` | Bài hát (chứa `audio_url`) |
| `Track_Genres` | Bài hát ↔ Thể loại (N:M) |
| `Playlists` | Playlist của user |
| `Playlist_Tracks` | Playlist ↔ Bài hát (N:M, có `position`) |
| `Play_History` | Lịch sử nghe |
| `Liked_Tracks` | Bài hát yêu thích |
| `Followed_Artists` | Nghệ sĩ đang theo dõi |

### Quan hệ chính:
```
Users       1──N  Subscriptions
Users       1──N  Playlists
Users       1──N  Play_History
Users       N──M  Tracks         (qua Liked_Tracks)
Users       N──M  Artists        (qua Followed_Artists)
Artists     1──N  Albums
Artists     1──N  Tracks
Albums      1──N  Tracks
Tracks      N──M  Genres         (qua Track_Genres)
Playlists   N──M  Tracks         (qua Playlist_Tracks, có position)
```

### Các kiểu dữ liệu SQL Server cần chú ý:
- Dùng `NVARCHAR` (không phải `VARCHAR`) cho mọi text tiếng Việt
- Dùng `IDENTITY(1,1)` thay cho `AUTO_INCREMENT`
- Dùng `GETDATE()` thay cho `NOW()`
- Dùng `BIT` cho boolean (0/1)

---

## 5. Cấu trúc project Spring Boot (đề xuất)

```
music-app-backend/
├── src/main/java/com/musicapp/
│   ├── config/
│   │   ├── SecurityConfig.java       # Spring Security + JWT
│   │   └── FileStorageConfig.java    # Cấu hình thư mục lưu file
│   ├── controller/
│   │   ├── AuthController.java       # /api/auth/register, /login
│   │   ├── TrackController.java      # /api/tracks
│   │   ├── AlbumController.java      # /api/albums
│   │   ├── ArtistController.java     # /api/artists
│   │   ├── PlaylistController.java   # /api/playlists
│   │   └── FileController.java       # /audio/**, /images/**
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── TrackService.java
│   │   ├── AlbumService.java
│   │   ├── ArtistService.java
│   │   ├── PlaylistService.java
│   │   └── FileStorageService.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── TrackRepository.java
│   │   ├── AlbumRepository.java
│   │   ├── ArtistRepository.java
│   │   ├── PlaylistRepository.java
│   │   └── PlayHistoryRepository.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── Artist.java
│   │   ├── Album.java
│   │   ├── Track.java
│   │   ├── Genre.java
│   │   ├── Playlist.java
│   │   ├── PlayHistory.java
│   │   └── Subscription.java
│   ├── dto/                          # Request/Response objects
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── JwtResponse.java
│   │   ├── TrackDto.java
│   │   └── PlaylistDto.java
│   ├── security/
│   │   ├── JwtUtil.java
│   │   ├── JwtFilter.java
│   │   └── UserDetailsServiceImpl.java
│   └── MusicAppApplication.java
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

---

## 6. application.properties (cần cấu hình)

```properties
# SQL Server
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=MusicAppDB;encrypt=false
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect

# File storage
file.upload-dir=D:/music-files
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

# JWT
jwt.secret=your-very-long-secret-key-at-least-256-bits
jwt.expiration=86400000

# Port
server.port=8080
```

---

## 7. Dependencies cần trong pom.xml

```xml
<!-- Spring Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- SQL Server JDBC Driver -->
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
</dependency>

<!-- Lombok (tùy chọn, giảm boilerplate) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

---

## 8. Các API endpoint chính (REST)

### Auth
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/auth/register` | Đăng ký tài khoản |
| POST | `/api/auth/login` | Đăng nhập, trả JWT |

### Nhạc
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/tracks` | Danh sách bài hát |
| GET | `/api/tracks/{id}` | Chi tiết 1 bài |
| GET | `/api/artists` | Danh sách nghệ sĩ |
| GET | `/api/artists/{id}/albums` | Album của nghệ sĩ |
| GET | `/api/albums/{id}/tracks` | Bài hát trong album |
| GET | `/api/genres` | Danh sách thể loại |

### Playlist & Thư viện
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/playlists` | Playlist của user hiện tại |
| POST | `/api/playlists` | Tạo playlist mới |
| POST | `/api/playlists/{id}/tracks` | Thêm bài vào playlist |
| DELETE | `/api/playlists/{id}/tracks/{trackId}` | Xoá bài khỏi playlist |
| POST | `/api/tracks/{id}/like` | Like bài hát |
| GET | `/api/tracks/liked` | Bài hát đã like |
| POST | `/api/artists/{id}/follow` | Follow nghệ sĩ |

### File (audio & ảnh)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/audio/{filename}` | Stream file mp3 (hỗ trợ HTTP Range) |
| GET | `/images/{filename}` | Lấy ảnh bìa, avatar |
| POST | `/api/admin/tracks/upload` | Upload file mp3 (admin only) |

### Lịch sử & Tìm kiếm
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/history` | Ghi lại lượt nghe |
| GET | `/api/history` | Lịch sử nghe của user |
| GET | `/api/search?q={keyword}` | Tìm kiếm bài hát, nghệ sĩ |

---

## 9. Lộ trình phát triển (10 bước)

### Giai đoạn 1 — Nền tảng
- [x] **Bước 1:** Tạo database SQL Server (script `music_app_database.sql` đã xong)
- [ ] **Bước 2:** Tạo project Spring Boot, cấu hình `application.properties`
- [ ] **Bước 3:** Viết Entity → Repository → Service → Controller cho từng bảng

### Giai đoạn 2 — Tính năng cốt lõi
- [ ] **Bước 4:** API đăng ký / đăng nhập với JWT
- [ ] **Bước 5:** API âm nhạc (Artist, Album, Track) — test bằng Postman
- [ ] **Bước 6:** Upload mp3 + stream audio (HTTP Range Request)

### Giai đoạn 3 — Android client
- [ ] **Bước 7:** Tạo project Android Studio, thêm Retrofit + ExoPlayer
- [ ] **Bước 8:** Kết nối Android ↔ Spring Boot (đăng nhập, danh sách nhạc, phát nhạc)

### Giai đoạn 4 — Hoàn thiện
- [ ] **Bước 9:** Playlist, Like bài hát, lịch sử nghe
- [ ] **Bước 10:** Tìm kiếm, gợi ý, hoàn thiện UI

---

## 10. Quy ước code

- Package gốc: `com.musicapp`
- Tên class Entity: đúng với tên bảng (ví dụ: bảng `Tracks` → class `Track`)
- Annotation bắt buộc: `@Entity`, `@Table(name="...")`, `@Id`, `@GeneratedValue`
- Dùng `@JsonIgnore` trên `password_hash` trong entity `User`
- Mọi response trả về dạng `ResponseEntity<?>` từ Controller
- Lỗi trả về dạng JSON: `{ "error": "mô tả lỗi" }`
- Bước hiện tại: **Bước 2** — Tạo project Spring Boot

---

## 11. Lưu ý khi làm việc với project này

1. Database đã được tạo xong — KHÔNG chạy lại script tạo bảng, dùng `ddl-auto=validate`
2. Bắt đầu code theo thứ tự: Entity → Repository → Service → Controller
3. Thứ tự ưu tiên bảng: `User` và `Artist` trước (ít FK nhất), `Track` sau
4. Test từng API bằng Postman trước khi làm Android
5. JWT token phải được gửi kèm mọi request (trừ `/register` và `/login`) trong header:
   `Authorization: Bearer <token>`
6. File audio stream qua HTTP Range — ExoPlayer Android hỗ trợ sẵn, Spring Boot
   cần handle header `Range` trong `FileController`
