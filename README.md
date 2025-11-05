# HOSPITAL TZ

An offline-first Android app for hospital services tracking and patient progress notification with educational content and community features.

## Introduction

Hospital TZ addresses the delay and inadequate medical information access in Tanzania by providing comprehensive hospital service tracking, educational content, and community features. It serves both normal users (free access) and professional staff users (premium features) to bridge the information gap in healthcare.

### Problem Solved
- Lack of timely medical information access
- Difficulty tracking personal medical progress
- Limited access to medical education
- Poor communication between healthcare providers and patients
- Inadequate medical center navigation

### Solution
Hospital TZ provides a centralized platform where users can:
- Track their medical progress across hospitals
- Access educational medical content
- Navigate to nearby medical facilities
- Communicate with healthcare professionals
- Stay informed about health announcements

## App Structure and Features

The app features five main sections accessible via bottom navigation:

### 🏠 Home Section
- Animated updates and announcements from hospitals and professionals
- Large search button for tracing medical progress
- Hospital search functionality
- Patient ID-based progress tracking

### 🗺️ Map Section
- Interactive world map with Tanzania focus
- Offline map download capability
- Display of all medical centers (hospitals, dispensaries, pharmacies)
- Nearest medical center search and navigation
- Location-based services

### 📚 Learn Section
- Video content from approved professionals and developers
- Full video playback controls
- Comment, message, follow, and share functionality
- Download and save options
- AI-powered content moderation (detects non-medical content)
- Content recommendation based on user preferences
- Staff users can post videos (premium features with limits)

### 💬 Messages Section
- Private messaging between users
- Follow-based chat access
- Medical-themed chat interface
- File, contact, emoji, and text attachments
- Third-party app integration for file handling
- Search functionality for finding users

### 👤 Profile Section
- User profile management and editing
- Profile completion validation
- Settings subsection (themes, privacy, notifications)
- Help and privacy policy access
- Secure data storage

## User Types

### Normal User Account (Free)
- View medical progress and status
- Access reminders and feedback features
- View educational content
- Participate in community messaging
- No posting capabilities in Learn section

### Staff User Account (Premium)
- All normal user features
- Post medical content in Learn section
- Video posting with limits (10 videos/day, 10 minutes total)
- Professional verification required (national ID, license)
- Automatic notifications to followers

## Security Features

- **Database Encryption**: SQLCipher encryption with secure passphrase
- **Biometric Authentication**: Fingerprint/face unlock support
- **Secure Shared Preferences**: Encrypted sensitive data storage
- **Input Validation**: Protection against SQL injection and XSS
- **Root Detection**: App exits on rooted/jailbroken devices
- **ProGuard Obfuscation**: Code obfuscation in release builds
- **Certificate Pinning**: Secure HTTPS connections
- **Backup Disabled**: Prevents data leakage
- **Anti-Reverse Engineering**: Multiple protection layers
- **Privacy Policy**: Required agreement on first signup

## Hospital Integration

### Read-Only API System
- Secure integration with hospital management systems
- Automatic data synchronization
- Offline data access
- Timestamp-based update tracking
- Background sync service

### Supported Hospitals
- Bugando Medical Centre
- Muhimbili National Hospital
- Kilimanjaro Christian Medical Centre
- Regional and district hospitals across Tanzania

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Database**: Room with SQLCipher encryption
- **Security**: AndroidX Security Crypto, Biometric authentication
- **Maps**: Mapbox SDK
- **Video**: ExoPlayer
- **Networking**: Retrofit with Gson
- **Background Tasks**: WorkManager
- **Notifications**: Firebase Cloud Messaging
- **Analytics**: Firebase Analytics
- **AI/ML**: Google ML Kit for content moderation
- **Image Loading**: Glide
- **Barcode**: ZXing
- **Email/SMS**: JavaMail API

## Setup and Build Instructions

### Prerequisites
- Android Studio (latest version recommended)
- Android SDK API 34
- JDK 11 or higher
- Android device or emulator (API 21+)

### Steps to Build and Run

1. **Clone or Download the Project**
   - Place the project folder in a suitable location (e.g., B:\HospitalTZ)

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the project folder and select it

3. **Sync Project with Gradle Files**
   - Android Studio should automatically sync
   - Ensure all dependencies are downloaded

4. **Configure SDK and Build Tools**
   - File > Project Structure
   - Verify SDK locations
   - Compile SDK: 34, Build Tools: latest

5. **Build the Project**
   - Build > Make Project (Ctrl+F9)
   - Or: `./gradlew build`

6. **Run on Device/Emulator**
   - Run > Run 'app' (Shift+F10)
   - Select target device

## Testing Instructions

### Critical-Path Testing
1. **User Registration & Login**
   - Test both user types registration
   - Verify biometric authentication
   - Check privacy policy agreement

2. **Navigation**
   - Test all 5 bottom navigation sections
   - Verify smooth transitions

3. **Core Features**
   - Hospital search and patient tracking
   - Map functionality and navigation
   - Video playback in Learn section
   - Messaging between users

4. **Security**
   - Verify database encryption
   - Test input validation
   - Check root detection

### Thorough Testing
- All user flows and edge cases
- Offline/online synchronization
- Performance with large datasets
- UI responsiveness across devices
- Security penetration testing

## API Integration

### Hospital API Endpoints
```
GET /api/patients?updated_since={timestamp}
Authorization: Bearer {READONLY_TOKEN}
```

### Response Format
```json
{
  "last_sync": "2025-10-08T10:15:00Z",
  "patients": [
    {
      "id": "PT-9321",
      "name": "John Doe",
      "diagnosis": "Malaria",
      "status": "Admitted",
      "last_update": "2025-10-08T09:40:00Z"
    }
  ]
}
```

## Deployment

- **APK Build**: Build > Build Bundle(s)/APK(s) > Build APK(s)
- **Beta Testing**: Distribute to hospitals for pilot testing
- **Production**: Update API endpoints and security configurations

## Contributing

1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, email: support@hospitaltz.com
Or visit: https://hospitaltz.com/support
