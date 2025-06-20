Mobile Alert App

Overview

Mobile Alert App is a mobile security alert application designed to enhance personal safety during emergencies. By rapidly pressing the phone’s power button three times, the app automatically sends a distress message with the user’s current location to selected emergency contacts. This enables users to discreetly alert loved ones or authorities when they are in danger or under threat.

Features

i). Triple Power Button Trigger: Instantly sends an SOS message by pressing the power button three times.
ii). RealTime Location Sharing: Captures and shares the user's current GPS coordinates.
iii). Emergency Contacts: Users can set up custom contacts to receive SOS alerts.
iv). PrivacyOriented: Only accesses location and SMS when triggered.
v). Background Operation: Runs quietly in the background without interrupting regular phone use.
vi). Automatic Reset: Resets trigger after each emergency to prevent repeated alerts.

Technologies Used

i. Kotlin — Core language for Android development
ii. Android SDK — For systemlevel interaction
iii. FusedLocationProviderClient — For accessing device GPS
iv. Broadcast Receivers & Services — For detecting power button presses and sending background messages
v. ViewModel & LiveData — For state management (if used)
vi. Room Database (optional) — For storing contacts (if used)

Installation and Setup

Prerequisites

i. Android Studio installed
ii. Android SDK (API 23 or above recommended)
iii. Kotlin Plugin
iv. An Android phone or emulator

How to Use

1. Open the app and add emergency contact numbers.
2. Allow the necessary permissions when prompted.
3. Exit the app (it runs in the background).
4. In an emergency, press the power button three times rapidly.
5. A predefined SOS message along with your location will be sent to the registered contacts.
