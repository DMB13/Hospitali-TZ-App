# Hospitali-TZ-App
the android app for health tracing
### Technical Enhancements
- **AI Chatbot**: Interactive AI-powered assistant for answering health-related questions and providing guidance.
- **Data Analytics Dashboard**: Visualize health data with charts and insights for better health management.
- **Wearable Integration**: Sync data from wearable devices like Google Fit for real-time health monitoring.
- **Offline Mode**: Access essential features without internet connection.
- **Push Notifications**: Receive timely reminders for appointments, medication, and health tips.
- **Biometric Authentication**: Secure login using fingerprint or face recognition.

### User-Friendly for Beginners
- **Onboarding Tutorial**: Step-by-step guide for new users to get started.
- **Help Section**: Comprehensive help and FAQ section for easy navigation.
- **Simplified UI**: Clean, intuitive interface with dark mode support for better usability.
- **Voice Commands**: Hands-free interaction using speech-to-text for searching and scheduling.
- **Accessibility Features**: Screen reader support, high contrast mode, and adjustable font sizes.

### Impressive Features
- **Gamification**: Earn badges and rewards for completing health goals and activities.
- **Social Sharing**: Share health achievements and connect with other users for support.
- **AR Navigation**: Augmented reality guidance to navigate hospital facilities.
- **Emergency SOS**: Quick access to emergency contacts and services.
- **Personalized Health Insights**: AI-driven recommendations based on user data.

## Installation

1. Clone the repository:
   ```
   git clone https://github.com/your-repo/HospitalTZ.git
   cd HospitalTZ
   ```

2. Open the project in Android Studio.

3. Build and run the app on an Android device or emulator.

## Usage

### Getting Started
1. Launch the app and complete the onboarding tutorial.
2. Register or log in using your credentials or biometric authentication.
3. Explore the home screen for quick access to key features.

### Key Screens
- **Home**: Overview of health status, upcoming appointments, and quick actions.
- **Map**: Find nearby hospitals and navigate using AR.
- **Learn**: Access educational videos and articles on health topics.
- **Messages**: Communicate with healthcare providers.
- **Appointments**: Schedule and manage appointments.
- **Analytics**: View detailed health data and trends.
- **Profile**: Manage personal information and settings.
- **Gamification**: Check badges and participate in challenges.
- **Social**: Share progress and connect with community.
- **Emergency SOS**: Access emergency features.

### Voice Commands
- Tap the microphone icon in search or appointment screens.
- Say commands like "Schedule appointment" or "Search for hospitals".

### Dark Mode
- Go to Profile > Settings > Toggle Dark Mode.

### Offline Mode
- The app automatically caches data for offline access.
- Sync when internet is available.

## CI/CD

The project is configured for continuous integration and deployment using Codemagic.

- **Workflow**: android-workflow
- **Environment**: Java 11
- **Signing**: Configure keystore in Codemagic dashboard
- **Artifacts**: APK files generated in app/build/outputs/

## Technologies Used

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Database**: Room
- **Networking**: Retrofit
- **Authentication**: Firebase Auth
- **Notifications**: Firebase Messaging
- **Wearables**: Google Fit API
- **Maps**: Google Maps API
- **AR**: ARCore
- **AI**: Custom ML models for chatbot and insights
