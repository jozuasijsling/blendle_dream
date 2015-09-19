# blendle_dream
A simple day dream that displays article manifests from Blendle's static Most Recent API.

Uses GCM Network Manager from Google Play Services to schedule periodic downloads, to keep its content up to date. 
Downloads are stored in a database behind a content provider, which helped to simplify observing data changes from 
the day dream.

The design has been inspired by Flipboard's day dream, which shows snippets of its feed with a Ken Burns animation effect. 
At the moment, the feed is shown and animated, but it might be nice to make this day dream interactive. For instance to 
allow deep linking into Blendle's native app.
