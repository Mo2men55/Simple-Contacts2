# Simple Contacts

A small Android contacts manager built with **Jetpack Compose**, **Room**, and **implicit Intents**.

## Features
- Add, view, edit, and delete contacts (full CRUD, stored locally with Room)
- Live search by name or phone number
- Mark contacts as favorites (favorites are listed first)
- Quick actions through Intents: call (dialer), SMS, email, and share via the system share sheet
- Form validation with inline error messages
- Responsive layout: adaptive grid on tablets/landscape, width-limited forms and details
- Material 3 with dynamic color, dark mode, and edge-to-edge display

## Architecture (MVVM)
```
data/
  Contact.kt            Room @Entity
  ContactDao.kt         Queries (Flow) + insert/update/delete
  ContactDatabase.kt    Room database singleton
  ContactRepository.kt  Single source of truth for the UI layer
ui/
  list/                 ContactListScreen + ContactListViewModel
  detail/               ContactDetailScreen + ContactDetailViewModel
  edit/                 ContactEditScreen + ContactEditViewModel (add & edit)
  navigation/           Navigation Compose graph
  components/           Reusable composables (ContactAvatar)
  ContactIntents.kt     Implicit Intents (ACTION_DIAL, ACTION_SENDTO, ACTION_SEND)
  AppViewModelProvider  ViewModel factory (manual DI)
ContactsApplication.kt  Holds the repository
```

Data flows one way: Room → `Flow` → Repository → ViewModel `StateFlow` → Compose UI.
User events go back up through ViewModel functions.

## CRUD mapping
| Operation | Where |
|-----------|-------|
| Create | `ContactEditViewModel.save()` → `ContactDao.insert` |
| Read   | `ContactDao.search` / `observeById` (reactive `Flow`) |
| Update | `ContactEditViewModel.save()` / favorite toggle → `ContactDao.update` |
| Delete | `ContactDetailViewModel.delete()` → `ContactDao.delete` |

## Build
Open in Android Studio and run the `app` configuration (min SDK 24).
