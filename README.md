# DayLimit 📱💰

> Mobile-first Daily Spend Tracker for Indian users paying via UPI. It only tracks and reminds; it never moves money.

## 🌟 Why Did GitHub Pages Show a 404 Error?

GitHub Pages is a static web hosting platform that searches for an **`index.html`** file in the root directory (or `/docs`) of your repository. 

Because DayLimit was originally built as a native Android app (Kotlin + Jetpack Compose with Gradle), it did not include an `index.html` at the repository root, leading to GitHub's default **404 Not Found** error page.

### ✅ What We Added to Fix This:
1. **`index.html`**: A fully functional, mobile-responsive interactive web application of DayLimit with the exact Black & Gray glassmorphism aesthetic, dynamic budget rebalancing, and calendar view.
2. **`.nojekyll`**: Tells GitHub Pages to skip Jekyll processing so all static assets and subfolders load properly.
3. **`manifest.json`**: Enables Progressive Web App (PWA) installation directly from the browser on mobile devices.

---

## 🚀 How to Enable GitHub Pages

1. Push your latest code changes to your GitHub repository (`main` or `master` branch).
2. Go to your repository on GitHub.
3. Click on **Settings** (top tab) ➔ **Pages** (in the left sidebar).
4. Under **Build and deployment**:
   - **Source**: Select **Deploy from a branch**.
   - **Branch**: Select your main branch (`main` or `master`) and set the folder to **`/ (root)`**.
5. Click **Save**.
6. Wait 1–2 minutes for GitHub to deploy. Your live app will be accessible at:
   ```
   https://<your-username>.github.io/<your-repository-name>/
   ```

---

## 📲 Native Android App

In addition to the web app on GitHub Pages, the repository contains the full native Android app:

- **Source Code**: `app/src/main/java/com/example/`
- **UI Framework**: Jetpack Compose + Material Design 3
- **Architecture**: MVVM with Room Database, StateFlow, Coroutines
- **Build Tool**: Gradle (`./gradlew assembleDebug` or `gradle assembleDebug`)
