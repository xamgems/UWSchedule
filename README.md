UWSchedule
==========

An Android interface for sharing and viewing UW course registration information.

## Setting Up the Project

The current implementation of the project supports Android Studio 0.2.0 and above. Please ensure you have updated
to the latest version before proceeding.

First change your `local.properties` file in the project root to reflect your android sdk directory. This should
point to the one in your Android Studio directory. For example:

<pre>
sdk.dir=/usr/android-studio/sdk
</pre>

Next do a clean build with `gradlew`, in the project root:

```bash
  ./gradelw clean build
```

Your done! Open up android studio, select `Open Project` and choose the UWSchedule project directory.


