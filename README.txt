VOID CLIENT (Fabric, Minecraft 1.21.11)

Getting the .jar (no installs needed, GitHub builds it for you):
 1. Make a new GitHub repository and upload everything in this folder
    (including the hidden ".github" folder).
 2. Open the repo's "Actions" tab -> "Build Void Client" -> latest run.
 3. Download the "voidclient-jar" artifact at the bottom, unzip it,
    and use voidclient-1.0.0.jar (NOT the -sources one).
 4. Put that jar AND Fabric API (for 1.21.11) in your .minecraft/mods folder,
    with Fabric Loader 0.18.4 or newer.

Building locally instead: install JDK 21 + Gradle 9.2, then run "gradle build".
The jar appears in build/libs/.

In game: F4 = Void Client menu, C (hold) = zoom. Both rebindable in Controls.
Settings save to config/voidclient.json.
