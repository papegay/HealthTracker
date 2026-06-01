# Setup Instructions

## 1. Firebase Project
1. Go to https://console.firebase.google.com and create a new project
2. Add an Android app with package name `com.genegebra.healthtracker`
3. Download `google-services.json` and place it in `app/`
4. Enable **Authentication → Email/Password** provider
5. Deploy Firestore rules: copy `firestore.rules` to Firebase console → Firestore → Rules

## 2. Firestore Indexes
Create a composite index in Firestore console (or via firebase CLI):
- Collection: `healthEntries`
- Fields: `userId ASC`, `createdAt DESC`
- Fields: `createdAt DESC` (for admin all-entries query)

## 3. reCAPTCHA
1. Go to https://www.google.com/recaptcha/admin and create a key (type: Android)
2. Add your package name `com.genegebra.healthtracker`
3. Copy the **site key** into `AuthViewModel.kt` → `recaptchaSiteKey`
4. The token is passed to Firebase Auth registration.
   If you add a backend later, verify the token server-side against the reCAPTCHA API.

## 4. Admin Account
After a user registers and verifies their email:
1. Open Firebase console → Firestore → `users` collection
2. Find the user's document
3. Set `isAdmin: true`

## 5. Build
```
./gradlew assembleDebug
```
