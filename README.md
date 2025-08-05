Morim

Morim is an innovative native Android application (Java) connecting students and teachers for both in-person and remote lessons. It offers a comprehensive platform for scheduling, messaging, ratings, and discovery—all within a clean, maintainable architecture.

🚀 Unique Value Proposition

- Geolocation-Driven Discovery: Interactive map displaying teachers’ locations with custom profile markers for quick, visual discovery.

- Dual-Role Scheduling: Teachers can not only teach but also upskill by booking other teachers as mentors or co-instructors.

- Offline-First Chat: Robust messaging powered by Room + Firestore sync ensures conversations persist and remain accessible without constant network connectivity.

- Seamless MVVM & DI: Clean architecture using Hilt and LiveData keeps UI logic separated from business logic, simplifying testing and maintenance.

- Adaptive UI/UX Enhancements: Features like scheduled camera recentering and dynamic status badges (Canceled/Completed/Upcoming) improve usability and provide clear feedback.


🏆 Competitors in Israel



🌟 Why Morim Stands Out

Local-Focused Map Integration: Visual discovery of Israeli teachers by neighborhood, supporting spontaneous in-person lessons.

Offline-First Chat: Robust messaging powered by Room + Firestore sync ensures conversations persist and remain accessible without constant network connectivity.
Unified Role Experience: A single app for both students and teachers—no need to switch platforms to book mentorship or conduct classes.

Open-Source Android Excellence: Transparent, production-ready codebase showcasing modern Android best practices.

🛠️ Architecture & Technology Stack

- Android (Java) with MVVM architecture and Hilt dependency injection.

- Firebase Auth for secure authentication; Firestore & Firebase Storage for data persistence and media.

- Room local database for offline caching and persistence, synchronized with Firestore.

- Picasso for efficient image loading and custom map marker creation.

- ScheduledExecutorService for periodic tasks (e.g., camera recentering).

- JUnit, Mockito, and Espresso for unit and instrumentation testing within a CI pipeline.

🔍 Key Code Highlights

1. ViewModel Injection & LiveData

@HiltViewModel
public class MainViewModel extends ViewModel {
    @Inject
    public MainViewModel(FirebaseUserManager um, FirebaseMeetingsManager mm) {
        teachers = um.listenTeachers();
        meetings = mm.listenMyMeetings();
    }
    // Additional business logic methods...
}

Illustrates dependency injection via Hilt, separating UI from data layers. fileciteturn0file0

2. Geolocation & Map Camera Recentring

ScheduledExecutorService executor = ...;  
executor.scheduleAtFixedRate(() -> {
    if (googleMap != null) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
    }
}, 20, 20, TimeUnit.SECONDS);

Keeps the map centered on the student’s location every 20 seconds, enhancing user experience. fileciteturn0file0

3. Offline-First Chat Synchronization

firestore.collection("chats").document(chatId)
    .addSnapshotListener((snapshot, error) -> {
        if (snapshot != null && snapshot.exists()) {
            chatDao.insert(convertToChat(snapshot));
        }
    });

Listens for real-time updates, persisting messages locally to guarantee offline access. fileciteturn0file0

4. Comprehensive Scheduling Flow

new ScheduleMeetingDialog(...)
    .setOnConfirm((date, subject, mode) -> {
        meetingsManager.schedule(new Meeting(...));
    });

Unified UI component for booking lessons, supporting both in-person and video modes.

📂 Project Structure Overview

com.example.morim
├─ ui/ # Fragments & Activities
│ ├─ auth/ # Registration, Login
│ └─ main/ # Map, favorites, My Meetings
├─ viewmodel/ # ViewModels exposing LiveData
├─ model/ # Domain models & data persistence (Room, Firestore)
├─ database/ # Firebase managers, Room DAOs
├─ dto/ # Data transfer objects (forms)
├─ util/ # Utilities (DateUtils, SimpleLocation wrapper)
└─ MorimApp.java # @HiltAndroidApp entry point


🎓 Getting Started

- Clone the repository and open in Android Studio.

- Configure Firebase by adding google-services.json to app/.

- Build and run on an Android device or emulator (min SDK 21+).

- Explore, test, and contribute!
