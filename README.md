# Morim

**Morim** is an innovative native Android application (Java) connecting students and teachers for lessons. It offers a comprehensive platform for scheduling, messaging, ratings, and discovery—within a clean, maintainable architecture.

---
<p align="center">
  <img src="https://github.com/user-attachments/assets/adf49b0c-0899-4a00-a168-1e80c0d714c6" width="200" alt="screenshot 1" />
  
  
  <img src="https://github.com/user-attachments/assets/b790ca3f-02c0-4d02-958a-929351acfcdf" width="200" alt="screenshot 2" />
  
  <img src="https://github.com/user-attachments/assets/dd936f50-7515-4783-847b-3d7f4530e7e9" width="200" alt="screenshot 3" />
  
  <img src="https://github.com/user-attachments/assets/c952353e-7a9d-4c4d-ab73-021a637d3e15" width="200" alt="screenshot 4" />
  
  <img src="https://github.com/user-attachments/assets/bee5756c-7f3f-4e24-97cc-af3e9b853acb" width="200" alt="screenshot 5" />
  
  <img src="https://github.com/user-attachments/assets/a9139d2f-e54c-4737-957b-88b2ef79162f" width="200" alt="screenshot 8" />

  
  <img src="https://github.com/user-attachments/assets/95a7218b-3654-4c60-a463-74b47442c3dc" width="200" alt="screenshot 6" />
  
  <img src="https://github.com/user-attachments/assets/cee3be21-9abd-4e88-8247-9d4c38648fcd" width="200" alt="screenshot 7" />
  
</p>

---

## 🚀 Unique Value Proposition

- **Geolocation-Driven Discovery**: Interactive map showing teachers’ locations with custom markers for quick visual identification.  
- **Dual-Role Scheduling**: Teachers can not only teach but also upskill by booking other teachers as mentors or co-instructors.  
- **Offline-First Chat**: Robust messaging using Room with Firestore synchronization, ensuring conversations persist even without connectivity.  
- **Offline-First Scheduling**: Scheduled lessons are cached locally via Room and synchronized with Firestore, ensuring continuity without a network.  
- **Transparent MVVM & DI**: Clear architecture with Hilt and LiveData, separating UI logic from business logic to simplify testing and maintenance.  
- **Adaptive UI/UX Enhancements**: Features like scheduled camera recentering and dynamic status badges (Canceled/Completed/Upcoming) enhance user experience.

---

## 🏆 Competitors in Israel

0000000000000000000000
---

## 🌟 Why Morim Stands Out

1. **Local-Focused Map Integration**: Visual discovery of Israeli teachers by neighborhood to facilitate spontaneous in-person lessons.  
2. **Resilient Offline Experience**: Core features like chat (snippet 3) and scheduling (snippet 4) work seamlessly offline and automatically synchronize when connectivity returns.  
3. **Unified Role Experience**: One app for both students and teachers—no need to switch platforms to book or teach lessons.  
4. **Open-Source Android Excellence**: Transparent, production-ready codebase demonstrating modern Android best practices.

---

## 🛠️ Architecture & Technology Stack

- **Android (Java)** with **MVVM** architecture and **Hilt** dependency injection.  
- **Firebase Auth** for secure authentication; **Firestore** & **Firebase Storage** for data persistence and media.  
- **Room** local database for offline caching and persistence, synchronized with Firestore.  
- **Picasso** for efficient image loading and custom map marker creation.  
- **ScheduledExecutorService** for periodic tasks (e.g., camera recentering).  
- **JUnit**, **Mockito**, and **Espresso** for unit and instrumentation testing in a CI pipeline.


---

## 🔍 Key Code Highlights

1. ViewModel Injection & LiveData

```java
@HiltViewModel
public class MainViewModel extends ViewModel {
    @Inject
    public MainViewModel(FirebaseUserManager um, FirebaseMeetingsManager mm) {
        teachers = um.listenTeachers();
        meetings = mm.listenMyMeetings();
    }
    // Additional business logic methods...
}
Illustrates dependency injection via Hilt, separating UI from data layers.



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

Unified UI component for booking lessons.



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
```java


🎓 Getting Started

- Clone the repository and open in Android Studio.

- Configure Firebase by adding google-services.json to app/.

- Build and run on an Android device or emulator (min SDK 21+).

- Explore, test, and contribute!
