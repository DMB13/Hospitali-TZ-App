# HOSPITAL TZ #

An offline-first Android app for hospital services tracking and along with patient progress and result notification.
it is the upgraded version of LabTrace TZ app ( An offline-first Android app for medical lab sample tracking and patient result notification.)

## INTRODUCTION


### problem
in tanzania there is the delay and inadequate of important medical information to people. example:
    - when am i sick
    - will i sick can i sick
    - where do i get medical assistance
    - where will i get medical assistance
    - how will i get medical assistance
    - who will give me medical assistance
    - how is my medication progressing
    - when are my results ready
    - as well as important medical eduction.
### solution 
i need to reduce even a small chance of loosing man power by imprementing adquate and accurate transfer of medical information to the peouple thanks to HOSPITAL TZ app where people will get all the medical information just in their hands.
#### history
i traced most of hopitals use various digtal/computerised system to crate and trace patients service information. almost all patients in Tanzania had no access to their infomation which led to miss judging and miss understanding what is going on abnd what is next.

#### what do we do
the app will be having two kinds of accounts as follows

##### normal user accout 
- this is for non professional pesonel who will be using the app to get informed and educated
- thi is a free acount no payment needed (no suvbscription)
- this user account will be unable to post through learn 
- the users will be tracing their info as released by medical centres, viewing and interacting with the learn but no posting in public, chating with each other through message section

##### staff user account
- this is for professional pesonel 
- it will need a coppy of national ID card and profession licence to create this account
- this will be a fremium account where the free usser will be able to post videos not more than ten with the total of ten minutes all per day, while the subscribed users of this account will have no limitation.
- the user will be able to post health and medical content in the learn section 
up on posting user will be able to notify their followers through an automatic message when allowed by the usser
- sers will need to convert the photos into videos before posting.
- user will be able to access the features accesed by normal user

## Features

- User account creation and login on first open with the email or google account or any other login type
- normal account users can view status (viewing their service history and progress.), reminders, and feedback
- patient record creation ID provided by the hospital
- Status update workflow (will depend on the hosptial's workflow system)
- Patient view by entering normal account users ID as provided by the hospital
- Local persistence with Room/SQLite
- Barcode generation and scanning integration (ZXing) this is for easy sharing of the account info and progress at a certain time frame.
- Multilingual UI (English + Kiswahili)
- CSV export for hosptal managers
- Reminders screen for normal account users
- Feedback submission for services
- Auto-send SMS via email notification (when online)
- Improved visual interface with Material Design icons and medical-themed colors aas well as medical images in background
- the user will have to first search the hospital where to trace his/her info then his/her ID as given by the hospital or medical centre.

## app structure and appearance
The app have five main sections at the bottom with relevant section butons for easy navigation. The sections are as follows;

> Home section:

= This sections button is in the bottom left corner of the screen in the app.
= It has the home/house like icon.
= For normal usser (normal account users or customs) it will be shoeing animated updates and matangazo from both hospitals, registered professionals like doctors and all medical related ones.
= The home section will also have a large search button up on clicking the usser will be able trace hisn /her progress.

> map section 
= this will be along side with home section as it is in the right side of the home button.
= this section will be having a world map with the option to download that of tanzania for offline use , 
the map is showing all medical centres like hospitals, dispensaries,farmacies etc.
= The map section will allow the usser to navigte and search the nearest medical centre through the app.
= the section wshould have all other standards of being a free section in the app as a map section.  

> learn section
= this is the free section where the user will be able to play short and long videos posted by approved proffesionals and medical centres personel as well as this app developer (me)
= thie user will have access to posted videos with full playback control.
= The following buttons shoulbe there;
    >> coment button with relevant icon
    >> message button (openn message section up on clic)
    >> follow button (to let usser follow the account of the one who posted a video)
    >> three dots (option button with share(through in app sharing or third part apps like xender etc), download, save to phone. )
= the section button shoulb be at the centre bottom side of app screen next to map section button.
= it should have relevant button icon
= the section should have all other standard features of the free learn section in the app
= the section shoulb be intergrated with AI that auto detect non medical content and remove them with accuracy of 1.0 and the one which detects the contents users like most and bring them closer

> messages section
= this is the chatting section which will provide private chatings between user 
= All usser are able to chat with the ones they follwed each other through follow option avilable in learn and after searching people in the search bar
= the section should have impressive chatting theme related to medical with the senders masssage in the right side and the received massage in the left side of the screen in the section
= the section allows attaching files, contacts, imoges , texts etc
= opening sent or r3eceived files through common third part apps
= the section should have all other standard features of message section as a free section in the app.

> profile section
= this is the section where the users can view and edit their profiles
= some profile data are collected and saved during first sign up
= other profile data like picture, description , etc are added eventuary as the usser wish but if the profile info are not full filled, the app shoul pop up the massage " please complete your profire "
= in the profile section , there sould be a setting sub section for over all app settings like themes, privacy, etc
= there should be help sub-section and privacy policy

## Security Features

- **Database Encryption**: All data encrypted at rest using SQLCipher with passphrase
- **Biometric Authentication**: Fingerprint/face unlock support for secure login
- **Secure Shared Preferences**: Sensitive data encrypted with AndroidX Security Crypto
- **Input Validation**: Protection against SQL injection and XSS attacks
- **Root Detection**: App exits on rooted/jailbroken devices to prevent tampering
- **ProGuard Obfuscation**: Code obfuscated in release builds to prevent reverse engineering
- **Certificate Pinning**: Network security config for secure HTTPS connections
- **Backup Disabled**: Automatic backups disabled to prevent data leakage
- **Secure Permissions**: Minimal permissions required, biometric permissions added
- **no reverse engering** no any kind of penetration and hacking
- there should be a privacy policy at first sign up for users to agree with before using an app
## Tech Stack

- Kotlin + Jetpack Compose
- Room with SQLCipher encryption
- AndroidX Security Crypto
- Biometric authentication
- ZXing for barcode scanning
- JavaMail for email notifications
- Android Jetpack components

## Setup and Build Instructions

### Prerequisites
- Android Studio (latest version recommended)
- Android SDK API 34
- JDK 11 or higher
- Android device or emulator (API 21+)

NOTE: all project files are milion lined codes

🧠 System Design: Read-Only, Auto-Synced Hospital Integration App
🎯 Your Goal

Build an Android app that connects to each hospital’s management system through API integration, where:

You register a hospital once.

The app fetches hospital data (patients, results, doctors, etc.) securely.

Users can view data but cannot edit it.

When the hospital updates patient information, the app auto-syncs once online.

🧩 1. Core Principles
Goal	Technical Strategy
Read-only access	Only use GET API endpoints — no POST, PUT, or DELETE.
Automatic sync	Use a background sync service triggered by connectivity.
Offline availability	Cache hospital data locally using Room Database (SQLite).
Real-time updates	When online, fetch new data and update the cache.
Secure connection	Use API key + OAuth token for each registered hospital.
⚙️ 2. Data Flow Overview
 ┌────────────────────────────┐
 │   Hospital Management System │
 │ (Official Backend Database) │
 └──────────────┬──────────────┘
                │  (Read-only API)
          HTTPS / REST + JSON
                │
 ┌──────────────┴──────────────┐
 │      Cloud Sync Gateway     │
 │  (Your App’s Sync Server)   │
 └──────────────┬──────────────┘
                │
                │ Firebase / HTTPS
                │
 ┌──────────────┴──────────────┐
 │      Android App (Client)   │
 │  - Local Room Database      │
 │  - Background Sync Service  │
 │  - Offline View Capability  │
 └─────────────────────────────┘

🏥 3. Workflow Step-by-Step
A. Hospital Registration

Admin (you) registers a hospital once on your system.

The app stores:

Hospital name

Unique API endpoint (e.g., https://hospitalX.com/api/v1/)

Secure token for read-only access.

Each hospital gets a read-only API key generated on their HMS.

B. Data Synchronization

When the user opens the app:

App checks internet connection.

If online → calls hospital’s HMS API (GET endpoints only).
Example:

GET https://hospitalX.com/api/patients?updated_since=2025-10-01
Authorization: Bearer READONLY_TOKEN


The app compares timestamps in the local database.

If new or changed data is found → app updates the local cache.

When offline → app uses cached data from Room Database.

C. No Edit Guarantee

To make sure no one edits hospital data:

Disable all input fields or editing buttons in the app.

Do not expose any POST, PUT, or DELETE endpoints in the app code.

Sign all app builds (so users can’t tamper with them).

Use read-only Firebase rules or direct HTTPS fetching with tokens.

🔐 4. Security Architecture
Component	Security Mechanism
Authentication	Each hospital uses an API key & OAuth 2.0 token.
Encryption	HTTPS + SSL pinning to prevent man-in-the-middle attacks.
Local Data	AES-encrypted Room Database.
App Access	Fingerprint/Face unlock (optional).
Cloud Gateway	Role-based access — only admins can add hospitals.
🧠 5. Example JSON Response (From Hospital API)
{
  "patients": [
    {
      "id": "PT-9034",
      "name": "John M. Nyanda",
      "age": 45,
      "diagnosis": "Malaria",
      "status": "Admitted",
      "last_update": "2025-10-08T15:30:00Z"
    }
  ]
}


Your app parses this and stores it in the local Room database, marking records as updated.

📱 6. Example Android UI Screens

Hospital List Screen — shows registered hospitals.

Patient List Screen — auto-synced data from HMS.

Patient Details Screen — view-only, with “Last Updated” timestamp.

Settings Screen — “Sync Now” & “Offline Mode” options.

🔄 7. Background Sync Logic (Simplified Pseudocode)
if isOnline():
    newData = api.fetchUpdatedPatients(lastSyncTime)
    localDb.updatePatients(newData)
    setLastSyncTime(currentTime)
else:
    display(localDb.getCachedPatients())

🌍 8. Realistic Example for Tanzania

Imagine integrating with:

Bugando Medical Centre API

Muhimbili HMS

KCMC HMS

Your app acts as a national patient info viewer, allowing hospitals to share updates with labs, insurance, or ministry health officers — automatically synced, view-only, and safe.

# Data Synchronization Module (Detailed Specification)
🎯 Objective

Ensure that patient data in the Android app always matches the hospital’s latest records without allowing any data modification from the app side.
The app must remain functional offline while automatically syncing updates whenever an internet connection is available.

⚙️ Workflow Overview
Step	Action	Description
1. Connectivity Check	App checks network status.	Uses Android ConnectivityManager to detect Wi-Fi or mobile data availability.
2. Request Initialization	App sends secure GET request to hospital API.	Endpoint includes a timestamp query (updated_since) to fetch only new/changed records.
3. Authentication	Hospital verifies the app’s token.	The token is a read-only Bearer Token issued by the hospital’s system admin.
4. Data Comparison	App compares timestamps of fetched vs local data.	Only records with newer timestamps are updated in the local cache.
5. Local Database Update	Local Room Database is updated with new data.	Uses upsert() logic (update if exists, insert if new).
6. Offline Mode	If no connection, cached data is shown.	The app displays data from the last successful sync.
7. Sync Log	Logs every sync operation with a timestamp.	Stored locally for audit and debugging.
🔐 API Request Example
GET https://hospitalX.or.tz/api/patients?updated_since=2025-10-01T00:00:00Z
Authorization: Bearer READONLY_TOKEN
Accept: application/json


Response:

{
  "last_sync": "2025-10-08T10:15:00Z",
  "patients": [
    {
      "id": "PT-9321",
      "name": "Maria Joseph",
      "gender": "Female",
      "age": 36,
      "diagnosis": "Typhoid",
      "ward": "Female Medical",
      "status": "Discharged",
      "last_update": "2025-10-08T09:40:00Z"
    },
    {
      "id": "PT-9322",
      "name": "John Mnyika",
      "gender": "Male",
      "age": 29,
      "diagnosis": "Malaria",
      "ward": "Male Medical",
      "status": "Admitted",
      "last_update": "2025-10-08T09:45:00Z"
    }
  ]
}

🧠 Android Logic Flow (Simplified Pseudocode)
if (isOnline()) {
    List<Patient> newPatients = api.fetchPatients(lastSyncTime);
    for (Patient p : newPatients) {
        localDb.upsert(p);  // Update if exists, insert if new
    }
    setLastSyncTime(currentTime);
} else {
    display(localDb.getCachedPatients());
}

🗃️ Local Database Structure (Room)
Column	Type	Description
patient_id	TEXT	Unique ID from hospital system
name	TEXT	Patient name
gender	TEXT	Gender
age	INTEGER	Age
diagnosis	TEXT	Main diagnosis
status	TEXT	Current condition
last_update	DATETIME	Last updated timestamp

All entries are marked with isSynced = true once updated successfully.

📡 Sync Frequency

Manual: User can tap “Sync Now” button.

Automatic:

On app launch

Every 6 hours (background service)

Whenever the device regains connectivity

🔒 Security Enforcements

All API requests must use HTTPS (TLS 1.3)

Tokens are stored encrypted using Android Keystore

App has no write permissions on the API (read-only endpoints)

Optional: SSL Pinning to ensure data authenticity

⚠️ Error Handling
Scenario	Response
No Internet	Use cached data + show “Offline mode” banner.
Invalid Token	Prompt: “Hospital API access expired. Contact admin.”
API Timeout	Retry after 60 seconds.
Data Conflict	Always prefer the hospital’s latest timestamp.