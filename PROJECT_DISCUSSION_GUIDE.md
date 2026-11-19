# Project Technical Discussion Guide

This guide details the technical architecture and module breakdown of **Simple Contacts** so you can present and defend your assigned project module with confidence.

---

## Module 1: Room Database & Persistence Layer
* **Architecture**:
  * Built using Android's Room Persistence Library on top of SQLite.
* **Key Components**:
  * **Entity (`Contact.kt`)**: Represents the `contacts` table schema using `@Entity(tableName = "contacts")` and `@PrimaryKey(autoGenerate = true)`.
  * **DAO (`ContactDao.kt`)**: Data Access Object declaring SQL query methods (`getAllContacts`, `searchContacts`, `getContactById`) and CRUD annotations (`@Insert`, `@Update`, `@Delete`).
  * **Database Singleton (`ContactDatabase.kt`)**: Thread-safe singleton implementation utilizing `@Volatile` and `synchronized(this)` locks to prevent multiple database instances.

---

## Module 2: UI View Architecture & Real-Time Filtering
* **View Binding**:
  * Traditional Android View layout system built with XML layouts and Kotlin `findViewById()` bindings.
* **Header & Info Management**:
  * Prominent student header `TextView` (`tv_student_info`) displaying student identification details.
* **Real-time Filter**:
  * An `EditText` search bar attached with a `TextWatcher` (`addTextChangedListener`) to dynamically query Room database records as the user types.

---

## Module 3: ListView & Custom Adapter
* **List Adapter Pattern**:
  * `ContactAdapter` extends `ArrayAdapter<Contact>` to bridge raw data models with XML views (`item_contact.xml`).
* **View Recycling**:
  * Efficiently inflates custom row views, populating contact initials, name, phone, and favorite status indicator inside `getView()`.
* **Dynamic Refresh**:
  * Calls `notifyDataSetChanged()` inside `updateData()` whenever records are added, updated, or removed.

---

## Module 4: Lifecycle Management, State & SharedPreferences
* **Activity Lifecycle**:
  * Full implementation of the 6 core activity lifecycle callbacks (`onCreate`, `onStart`, `onResume`, `onPause`, `onStop`, `onDestroy`) with detailed Logcat output (`Log.d("ActivityLifecycle", "...")`).
* **State Preservation**:
  * Saves active UI search state into a `Bundle` inside `onSaveInstanceState()` to maintain state across device rotation and screen orientation changes.
* **Persistent Preferences**:
  * Uses `SharedPreferences` (`getSharedPreferences("ContactsAppPrefs", Context.MODE_PRIVATE)`) to store lightweight configuration and last active search queries.

---

## Module 5: Explicit Navigation, Forms & Background Coroutines
* **Explicit Navigation**:
  * Navigates between screens using `Intent(this, TargetActivity::class.java)` and passes parameter data via intent extras (`intent.putExtra("CONTACT_ID", id)`).
* **Form Processing**:
  * Contact creation and editing forms with input validation for required fields.
* **Asynchronous Threading**:
  * Uses Kotlin Coroutines (`lifecycleScope.launch(Dispatchers.IO)`) to execute database read/write operations off the main thread, returning to `Dispatchers.Main` to safely update the UI.

---

## Module 6: System Integration, Actions & Intent Filters
* **System Implicit Actions**:
  * Launches external system apps using implicit intents:
    * **Dialer**: `Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))`
    * **Messaging**: `Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${contact.phone}"))`
    * **Email**: `Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${contact.email}"))`
    * **Share Sheet**: `Intent(Intent.ACTION_SEND)` wrapped with `Intent.createChooser()`.
* **Manifest Intent Filters**:
  * Declares `<intent-filter>` tags in `AndroidManifest.xml` to allow the app to receive shared text from external application actions.
