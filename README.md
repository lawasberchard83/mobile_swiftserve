# ⚡ SwiftServe Android App

A professional Android application built with **Kotlin** and **Retrofit** that integrates with a REST API for user management including authentication, profile management, and account security.

---

## 📱 Features

| Feature | Description |
|---|---|
| 🔐 Register | Create a new account with name, email & password |
| 🔑 Login | Authenticate with email & password, stores Bearer Token |
| 🏠 Dashboard | Home screen with user stats and quick actions |
| 👤 Profile | View complete user profile with photo |
| ✏️ Update Profile | Edit name, email, phone, address & change photo |
| 🔒 Change Password | Securely update account password |

---

## 🛠️ Tech Stack

- **Language**: Kotlin
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: Activity-based with separation of concerns
- **Networking**: Retrofit 2.9.0 + OkHttp + Gson
- **Image Loading**: Glide 4.16.0
- **UI**: Material Design 3 (MaterialComponents)
- **Session**: SharedPreferences (Bearer Token storage)

---

## 🌐 API Integration

### Base URL Configuration

Open `app/src/main/java/com/swiftserve/app/data/api/RetrofitClient.kt` and update:

```kotlin
private const val BASE_URL = "https://your-api-url.com/"
```

### API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/register` | ❌ | Register new user |
| `POST` | `/api/login` | ❌ | Login and get token |
| `POST` | `/api/logout` | ✅ Bearer | Logout user |
| `GET` | `/api/dashboard` | ✅ Bearer | Get dashboard data |
| `GET` | `/api/profile` | ✅ Bearer | Get user profile |
| `PUT` | `/api/profile` | ✅ Bearer | Update profile info |
| `POST` | `/api/profile/photo` | ✅ Bearer | Upload profile photo |
| `PUT` | `/api/change-password` | ✅ Bearer | Change password |

### Request / Response Examples

#### POST /api/login
**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```
**Response (200 OK):**
```json
{
  "token": "1|abc123xyz...",
  "message": "Login successful",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "user@example.com",
    "phone": "+1234567890",
    "address": "123 Main St",
    "photo": "https://example.com/photo.jpg",
    "created_at": "2024-01-15T10:00:00Z"
  }
}
```

#### POST /api/register
**Request:**
```json
{
  "name": "John Doe",
  "email": "user@example.com",
  "password": "password123",
  "password_confirmation": "password123"
}
```
**Response (201 Created):**
```json
{
  "message": "Registration successful",
  "user": { "id": 1, "name": "John Doe", "email": "user@example.com" }
}
```

#### PUT /api/change-password
**Request:**
```json
{
  "current_password": "oldpassword",
  "new_password": "newpassword123",
  "new_password_confirmation": "newpassword123"
}
```
**Response (200 OK):**
```json
{
  "message": "Password changed successfully"
}
```

---

## ⚠️ Error Handling

The app handles all required error cases:

| Case | Handling |
|------|----------|
| 🔴 No internet | Shows "No internet connection" toast, loads cached data |
| 🔴 API failure (4xx/5xx) | Parses error body, shows user-friendly message |
| 🔴 Invalid credentials (401) | Shows "Unauthorized" message, redirects to login |
| 🔴 Validation error (422) | Extracts field-level errors from response |
| 🔴 Server error (500) | Shows "Server error. Please try again later." |
| 🔴 Network timeout | Shows network error message |

---

## 📦 Dependencies

```groovy
// Retrofit (required)
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// OkHttp logging
implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'

// Image loading
implementation 'com.github.bumptech.glide:glide:4.16.0'

// Circular profile image
implementation 'de.hdodenhof:circleimageview:3.1.0'

// Material Design
implementation 'com.google.android.material:material:1.11.0'
```

---

## 🚀 Setup & Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/mobile_swiftserve.git
   cd mobile_swiftserve
   ```

2. **Open in Android Studio**
   - File → Open → Select the project folder

3. **Set your API Base URL**
   - Open `RetrofitClient.kt`
   - Replace `https://your-api-url.com/` with your actual backend URL

4. **Sync Gradle**
   - Click "Sync Now" in the notification bar

5. **Run the app**
   - Connect an Android device or start an emulator
   - Click ▶️ Run

---

## 📁 Project Structure

```
app/src/main/java/com/swiftserve/app/
├── data/
│   ├── api/
│   │   ├── ApiService.kt          # Retrofit interface (all endpoints)
│   │   └── RetrofitClient.kt      # Centralized Retrofit instance
│   └── model/
│       └── Models.kt              # Request/Response data classes
├── ui/
│   ├── auth/
│   │   ├── SplashActivity.kt      # Entry point, session check
│   │   ├── LoginActivity.kt       # Login screen
│   │   └── RegisterActivity.kt    # Register screen
│   ├── dashboard/
│   │   └── DashboardActivity.kt   # Dashboard with stats
│   └── profile/
│       ├── ProfileActivity.kt     # View profile
│       ├── UpdateProfileActivity.kt # Edit profile + photo upload
│       └── ChangePasswordActivity.kt # Change password
└── utils/
    ├── SessionManager.kt          # SharedPreferences token/user storage
    └── NetworkUtils.kt            # Connectivity check + error parsing
```

---

## 📸 Screenshots

### Register
> _Screenshot: Registration form with name, email, password fields_
<img width="1919" height="1030" alt="image" src="https://github.com/user-attachments/assets/83d9596b-fbf6-4288-a3ac-795a52bc3083" />

### Login
> _Screenshot: Login screen with email and password_
<img width="1919" height="1027" alt="image" src="https://github.com/user-attachments/assets/7b6170b2-d11a-4b75-8636-e489b04168c7" />

### Dashboard
> _Screenshot: Dashboard showing user greeting and stats cards_
<img width="1919" height="1033" alt="image" src="https://github.com/user-attachments/assets/40a348d4-65a1-4ea7-8df9-4a8a19c8e9fb" />

### Profile
> _Screenshot: Profile page with photo, name, email, phone, address_
<img width="1919" height="1027" alt="image" src="https://github.com/user-attachments/assets/433e4370-ad84-41fa-8ffc-44c120c6e304" />

### Update Profile
> _Screenshot: Edit form with photo picker and input fields_
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/22ba8601-6bce-4304-9a29-ed40d20c4995" />

### Change Password
> _Screenshot: Password change form with current and new password fields_
<img width="1919" height="1029" alt="image" src="https://github.com/user-attachments/assets/1d87bcd9-1087-4c3e-9d02-10765ff1a96a" />

---

## 🔐 Authentication Flow

```
App Launch
    └── SplashActivity (2s)
         ├── Token exists → DashboardActivity
         └── No token    → LoginActivity
                              └── Login Success → Save Token → DashboardActivity
                              └── Register → RegisterActivity → LoginActivity
```

All protected routes automatically include:
```
Authorization: Bearer <token>
```

---

## 📝 License

This project is built for educational purposes as part of a Mobile Development course.
