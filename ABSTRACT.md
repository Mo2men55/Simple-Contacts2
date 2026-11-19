# Simple Contacts: A Lightweight Contact Manager Built with Jetpack Compose and Room

**Team:** _Team #__ — Member 1, Member 2, Member 3_
**Course:** _Mobile Application Development_

> Draft content for the 2-page abstract. Paste it into the official ACM template
> (`acmart`, `sigconf` format — available on Overleaf as "ACM Conference Proceedings Primary Article Template") and export to PDF.

## Abstract
We present *Simple Contacts*, an Android application for creating, organizing, and acting on personal contacts.
The app is written entirely in Kotlin using Jetpack Compose for a declarative UI, Room for local persistence,
and Android implicit Intents to integrate with the system dialer, messaging, email, and share features. It follows
the Model-View-ViewModel (MVVM) architecture with unidirectional data flow, which keeps the UI reactive and the code
testable. The result is a small but complete application that demonstrates the core concepts of modern Android development.

**CCS Concepts:** Human-centered computing → Ubiquitous and mobile computing systems and tools; Software and its engineering → Software design engineering.

**Keywords:** Android, Jetpack Compose, Room, MVVM, Intents, Kotlin

## 1 Introduction
Managing contacts is one of the most common tasks on a smartphone. Our goal was to build a focused contact manager that
is fast, easy to use, and works offline, while serving as a practical exercise in modern Android practices.
The application lets users add, view, edit, search, favorite, and delete contacts, and lets them call, text, email, or share a
contact with a single tap.

## 2 System Design
**Architecture.** The app is split into a data layer and a UI layer. The data layer contains a Room entity (`Contact`), a DAO
exposing reactive `Flow` queries, a database singleton, and a repository. The UI layer contains three screens (list,
detail, edit), each backed by a `ViewModel` that exposes immutable `StateFlow` state. A lightweight manual dependency-injection
container in the `Application` class provides the repository to ViewModels through a `viewModelFactory`.

**Navigation.** Navigation Compose connects the three screens; the contact ID is passed as a typed route argument and read
by ViewModels through `SavedStateHandle`, so state survives configuration changes and process death.

**Persistence (CRUD).** Create and update are performed by the edit screen, read operations are reactive queries
(search by name/phone, observe by ID), and delete is available from the detail screen after a confirmation dialog.
Because queries return `Flow`, every change in the database is automatically reflected in the UI.

**Intents.** The app uses implicit Intents: `ACTION_DIAL` (call), `ACTION_SENDTO` with `smsto:` and `mailto:` URIs
(message, email), and `ACTION_SEND` with a chooser (share). Missing handlers are caught and reported to the user.

## 3 User Interface and Experience
The UI uses Material 3 with dynamic color and dark mode, and draws edge-to-edge. The contact list uses an adaptive grid
(one column on phones, multiple on tablets and in landscape); the detail and edit screens limit content width for readability
on large screens. The edit form provides appropriate keyboard types, IME actions, and inline validation messages. Empty and
no-results states guide the user, and favorites are sorted to the top.

## 4 Code Quality
The code favors immutability (`val`, immutable data classes, `copy`), lambdas and function references for event handling,
stateless composables with state hoisting, and previews for every screen. Strings are externalized in resources.

## 5 Conclusion and Future Work
*Simple Contacts* demonstrates how Compose, Room, and Intents combine into a clean, reactive application. Future work includes
contact photos, groups/labels, import from the system contacts provider, cloud backup, and automated UI tests.

## 6 Team Contributions
| Member | Contribution |
|--------|--------------|
| Member 1 | Room data layer (entity, DAO, database, repository) |
| Member 2 | List and detail screens, Intents |
| Member 3 | Edit screen, validation, navigation, abstract |

## References
1. Android Developers. *Jetpack Compose*. https://developer.android.com/compose
2. Android Developers. *Save data in a local database using Room*. https://developer.android.com/training/data-storage/room
3. Android Developers. *Intents and intent filters*. https://developer.android.com/guide/components/intents-filters
4. Android Developers. *Guide to app architecture*. https://developer.android.com/topic/architecture
