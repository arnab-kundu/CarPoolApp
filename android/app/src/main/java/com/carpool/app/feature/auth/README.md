# Mock authentication

The app starts at login. Use username `admin` and password `admin`.
Registration validates required fields, unique usernames, a four-character minimum
password, and matching confirmation, then signs in to the demo home.
Registered accounts and the session live in a ViewModel: they survive rotation
but reset when the app process closes. Password fields are not saved to disk.
Profile displays the signed-in name and provides Sign out. Back from registration
returns to login; signed-in users cannot navigate back to the auth forms.
This is an offline UI prototype with no backend authentication.
