# Morim

**Morim** est une application Android native innovante (Java) mettant en relation étudiants et enseignants pour des cours en présentiel et à distance. Elle offre une plateforme complète pour la planification, la messagerie, les évaluations et la découverte, le tout dans une architecture propre et maintenable.

---

## 🚀 Proposition de valeur unique

- **Découverte géolocalisée** : Carte interactive affichant la position des enseignants avec des marqueurs personnalisés pour une identification visuelle rapide.  
- **Planification à double rôle** : Les enseignants peuvent non seulement donner des cours, mais aussi se former en réservant d’autres enseignants comme mentors ou co-instructeurs.  
- **Chat hors ligne prioritaire** : Messagerie robuste reposant sur Room et synchronisation Firestore, garantissant la persistance des conversations même sans connexion.  
- **Planification hors ligne prioritaire** : Les leçons programmées sont mises en cache localement via Room et synchronisées avec Firestore, assurant la continuité sans réseau.  
- **Architecture MVVM & DI transparente** : Architecture claire avec Hilt et LiveData, séparant la logique d’interface de la logique métier pour simplifier les tests et la maintenance.  
- **Améliorations UI/UX adaptatives** : Fonctionnalités comme le recentrage programmé de la caméra et les badges de statut dynamiques (Annulé/Terminé/À venir) améliorent l’expérience utilisateur.

---

## 🏆 Concurrents en Israël

----------------------add--------------
---

## 🌟 Pourquoi Morim se démarque

1. **Intégration cartographique locale** : Découverte visuelle des enseignants israéliens par quartier pour faciliter les cours en présentiel spontanés.  
2. **Expérience hors ligne résiliente** : Les fonctionnalités essentielles telles que le chat (snippet 3) et la planification (snippet 4) fonctionnent de façon fluide hors ligne et se synchronisent automatiquement dès que la connexion revient.  
3. **Expérience de rôle unifiée** : Une seule application pour étudiants et enseignants — plus besoin de basculer entre plusieurs plateformes pour réserver ou dispenser un cours.  
4. **Excellence open-source Android** : Code transparent et prêt pour la production, démontrant les meilleures pratiques Android modernes.

---

## 🛠️ Architecture & Stack technologique

- **Android (Java)** avec architecture **MVVM** et injection de dépendances **Hilt**.  
- **Firebase Auth** pour l’authentification sécurisée ; **Firestore** et **Firebase Storage** pour la persistance des données et des médias.  
- **Room** comme base locale pour la mise en cache et la persistance hors ligne, synchronisée avec Firestore.  
- **Picasso** pour le chargement efficace des images et la création de marqueurs de carte personnalisés.  
- **ScheduledExecutorService** pour les tâches périodiques (ex. recentrage de la caméra).  
- **JUnit**, **Mockito** et **Espresso** pour les tests unitaires et d’instrumentation dans un pipeline CI.

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
```java


🎓 Getting Started

- Clone the repository and open in Android Studio.

- Configure Firebase by adding google-services.json to app/.

- Build and run on an Android device or emulator (min SDK 21+).

- Explore, test, and contribute!
