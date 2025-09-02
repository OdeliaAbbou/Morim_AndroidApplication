# Morim

**Morim** is an innovative native Android application (Java) connecting students and teachers for lessons. It offers a comprehensive platform for scheduling, messaging, ratings, and discovery - within a clean, maintainable architecture.

---

## 🚀 Features & Unique Value Proposition

-🔍 Teacher Discovery: Search teachers by subject, location & price

-👥 Dual-Role Support: Teachers can also act as students to learn from other teachers

-📅 Meeting Scheduling: Book lessons with custom calendar component for date selection

-💬 Chat System: Real-time messaging between students and teachers

-⭐ Rating & Reviews: Students can rate teachers after completed lessons

-❤️ Favorites: Save preferred teachers for quick access

-📱 Offline Support: Core data cached locally for offline viewing




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

 ## 🎥 **Demo Video**:
 https://afekacollege-my.sharepoint.com/:f:/g/personal/odelia_abbou_s_afeka_ac_il/EuNsBoD3_XFKiKwqSEkrXFUBC7ZG92J2k2S-5YqEIqZpWA?e=Xg1Y3a

---

## 🏆 Competitors in Israel

- **LimoudNaim** :
Israel’s largest private-tutor board offering free tutor profiles and lessons; lacks a native Android app, interactive map discovery and offline booking.  

- **Lessons** :
Instant web-based tutor search with advanced filters and student reviews; no native mobile app, no built-in scheduling UI, and no offline-first capability.

---

## 🌟 Why Morim Stands Out

1. **Local-Focused Map Integration**: Visual discovery of Israeli teachers by neighborhood to facilitate spontaneous in-person lessons.  
2. **Resilient Offline Experience**: Users can still browse their teachers, favorites, chats, and scheduled meetings without internet.  
Room caches previously loaded data locally.  
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
- **Location & Maps**  Google Places API (+ Maps Utils) 📍  
- **Picasso** for efficient image loading and custom map marker creation.  
- **ScheduledExecutorService** for periodic tasks (e.g., camera recentering).  
- **JUnit** and **Espresso** for unit and instrumentation testing in a CI pipeline.

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

- Entities: **ChatEntity**, **MessageEntity**, **MeetingEntity**, **TeacherEntity**, **UserEntity**, **FavoriteEntity**, **LocationEntity**, **CommentEntity**.
- DAOs (in `database/local`): **ChatDao**, **MeetingDao**, **TeacherDao**, **StudentDao**, **UserDao**, **FavoritesDao**,



## 🔩 Dependency Injection (Hilt) Modules

- UserModule: provides UserDao, FirebaseUserManager

- MeetingModule: provides MeetingDao, FirebaseMeetingsManager

- FavoritesModule: injects the favorites repository

- MorimModule: configures Google Places & App Check




## 🔍 Key Code Highlights

## 1. MVVM Architecture with Hilt Injection


```java
@HiltViewModel
public class MainViewModel extends ViewModel {
    @Inject
    public MainViewModel(
        UserRepository userRepository,
        MeetingRepository meetingRepository,
        CurrentUserRepository currentUserRepository,
        FavoritesRepository favoritesRepository
    ) {
        teachers = userRepository.listenTeachers();
        myMeetings = meetingRepository.listenMyMeetings();
        myFavorites = favoritesRepository.listenFavorites();
    }
}
```
Demonstrates clean dependency injection separating UI from business logic.


## 2. Geolocation & Map Camera Recentring

```java

        mapFuture = executor.scheduleAtFixedRate(() -> {
            LatLng curr = currentLocation();
            if (googleMap != null && simpleLocation.hasLocationEnabled() && !curr.equals(lastLocation)) {
                lastLocation = curr;
                moveCameraToCurrent();
            }
        }, 0, 20, TimeUnit.SECONDS);
```
Track the user's current location and update the map if it changes.



## 3. Real-time Chat with Local Persistence

```java
remoteDb.listenChat(chatId, new OnDataCallback<Chat>() {
                    @Override
                    public void onData(Chat value) {
                        executor.execute(() -> {
                            chatDao.insert(value);
                        });
                    }
```
Ensures messages are available offline while maintaining real-time updates.



## 4. Meeting Conflict Detection

```java
                    boolean hasConflict = myMeetings.stream()
                            .anyMatch(meeting ->
                                    !meeting.isCanceled() &&
                                            meeting.getMeetingDate() == meetingDate &&
                                            (meeting.getStudentId().equals(myId) || meeting.getTeacherId().equals(myId))
                            );

                    if (hasConflict) {
                        Toast.makeText(context,
                                "You already have a meeting scheduled for that date and time. Please choose another time slot.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
```
Prevents double-booking by checking existing meetings for conflicts.



## 🎓 Getting Started

- Clone the repository and open in Android Studio.

- Configure Firebase by adding google-services.json to app/.

- Build and run on an Android device or emulator (min SDK 21+).

- Explore, test, and contribute!

