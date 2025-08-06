# Morim

**Morim** is an innovative native Android application (Java) connecting students and teachers for lessons. It offers a comprehensive platform for scheduling, messaging, ratings, and discovery - within a clean, maintainable architecture.

---

## 🚀 Features & Unique Value Proposition

- 🔍 **Search** teachers by subject, location & price  
- 📍 **Geolocation-Driven Discovery**: interactive map with custom markers for quick identification  
- 📅 **Scheduling & Dual-Role Booking**: plan in-person or virtual lessons; teachers can also book peers as mentors/co-instructors  
- 💬 **Offline-First Chat**: robust messaging using Room + Firestore sync for seamless, always-on communication  
- ⏳ **Offline-First Scheduling**: scheduled lessons cached locally with automatic Firestore synchronization  
- ⚙️ **Transparent MVVM & DI**: clean separation of UI & business logic via Hilt & LiveData  
- 🎨 **Adaptive UI/UX Enhancements**: automatic map recentering, dynamic status badges (Canceled / Completed / Upcoming)  
- ⭐ **Ratings & Reviews**: user feedback on teacher profiles  
- 🔔 **Push Notifications**: built-in reminders for upcoming lessons  
- 📁 **Media Upload**: share exercise files, PDFs, images  

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

## 🏆 Competitors in Israel

- LimoudNaim 
Israel’s largest private-tutor board offering free tutor profiles and lessons; lacks a native Android app, interactive map discovery and offline booking.  

- Lessons
Instant web-based tutor search with advanced filters and student reviews; no native mobile app, no built-in scheduling UI, and no offline-first capability.

---

## 🌟 Why Morim Stands Out

1. **Local-Focused Map Integration**: Visual discovery of Israeli teachers by neighborhood to facilitate spontaneous in-person lessons.  
2. **Resilient Offline Experience**: Core features like chat (snippet 3) and scheduling (snippet 4) work seamlessly offline and automatically synchronize when connectivity returns.  
3. **Unified Role Experience**: One app for both students and teachers—no need to switch platforms to book or teach lessons.  
4. **Open-Source Android Excellence**: Transparent, production-ready codebase demonstrating modern Android best practices.



---

## 📦 Installation

```bash
git clone https://github.com/OdeliaAbbou/Morim_AndroidApplication.git
cd Morim_AndroidApplication
```

- Open the project in Android Studio Arctic Fox or higher.

- Make sure you have Java 11, minSdkVersion 21, targetSdkVersion 33.

---

## 🔧 Configuration

- Place your `google-services.json` in the `app/` folder.
- Create a `secret.properties` file at the project root:
  ```properties
  GOOGLE_PLACES_API_KEY=<YOUR_PLACES_KEY>
  FIREBASE_APP_CHECK_SECRET=<YOUR_APPCHECK_SECRET>
  ```
Enable Google Places API and Play Integrity App Check in the Google Cloud Console.

---

## 🛠️ Architecture & Technology Stack

- **Android (Java)** with **MVVM** architecture and **Hilt** dependency injection.  
- **Firebase Auth** for secure authentication; **Firestore** & **Firebase Storage** for data persistence and media.  
- **Room** local database for offline caching and persistence, synchronized with Firestore.  
- **Picasso** for efficient image loading and custom map marker creation.  
- **ScheduledExecutorService** for periodic tasks (e.g., camera recentering).  
- **JUnit**, **Mockito**, and **Espresso** for unit and instrumentation testing in a CI pipeline.

---

## 📂 Project Structure Overview
```java
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
```


---




## 🗄️ Local Database (Room)

- Entities

- ChatEntity (id, senderId, message, timestamp)

- MeetingEntity (id, teacherId, studentId, date, mode, status)

- DAOs (in database/local)

- ChatDao: insert, delete, chronological queries

- MeetingDao: CRUD operations on scheduled lessons



## 🔩 Dependency Injection (Hilt) Modules

- UserModule: provides UserDao, FirebaseUserManager

- MeetingModule: provides MeetingDao, FirebaseMeetingsManager

- FavoritesModule: injects the favorites repository

- MorimModule: configures Google Places & App Check




## 🎨 Custom Components & Utilities

- NACalendarView & TeacherCalendar: custom calendar views for slot selection

- SubjectSpinner: dynamic subject dropdown loaded from MorimApp.getSubjects()

- NotificationHelper: manages notification channels and reminders

- ScreenUtils: prevents screenshots and screen recordings

- DecimalDigitsInputFilter: enforces numeric input formats


## 🔍 Key Code Highlights

## 1. ViewModel Injection & LiveData

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
```
Illustrates dependency injection via Hilt, separating UI from data layers.



## 2. Geolocation & Map Camera Recentring

```java

ScheduledExecutorService executor = ...;
executor.scheduleAtFixedRate(() -> {
    if (googleMap != null) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
    }
}, 20, 20, TimeUnit.SECONDS);
```

Keeps the map centered on the student’s location every 20 seconds, enhancing user experience. fileciteturn0file0



## 3. Offline-First Chat Synchronization

```java
firestore.collection("chats").document(chatId)
    .addSnapshotListener((snapshot, error) -> {
        if (snapshot != null && snapshot.exists()) {
            chatDao.insert(convertToChat(snapshot));
        }
    });
```

Listens for real-time updates, persisting messages locally to guarantee offline access. fileciteturn0file0



## 4. Comprehensive Scheduling Flow

```java
new ScheduleMeetingDialog(...)
    .setOnConfirm((date, subject, mode) -> {
        meetingsManager.schedule(new Meeting(...));
    });
```
Unified UI component for booking lessons.



## 🎓 Getting Started

- Clone the repository and open in Android Studio.

- Configure Firebase by adding google-services.json to app/.

- Build and run on an Android device or emulator (min SDK 21+).

- Explore, test, and contribute!
