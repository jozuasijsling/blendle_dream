# Blendle Dream
A simple day dream that displays article manifests from Blendle's static Most Recent API.

Uses GCM Network Manager from Google Play Services to schedule periodic downloads, to keep its content up to date. 
Downloads are stored in a database behind a content provider, which helped to simplify observing data changes from 
the day dream. The use of GCM is purely experimental, the day dream might actually be better off without it.

The design has been inspired by Flipboard's day dream, which shows snippets of its feed with a Ken Burns animation effect. 
At the moment, the feed is shown and animated, but it might be nice to make this day dream interactive. For instance to 
allow deep linking into Blendle's native app.


While I don't expect to continue work on this project, in case anyone wants to pick it up, here's a list of issues that will need a bit more thought: 

* The KenBurns effect is often zooms in too much making the background display very ugly.
* The body is a collection of spannables, but it's indexes seem to be set incorrectly. This probably has to do with Html.fromHtml() messing up the character indexes.
* When starting the day dream for the first time, GCM waits up to 30 seconds before downloading the issues. This can be fixed by replacing the OneOffTask by a simple AsyncTask or Loader. 
