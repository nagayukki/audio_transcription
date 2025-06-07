# Audio Transcription

This project is an Android application that converts voice input into text using [Mimi](https://mimi.fm/) services. It is built with Kotlin, Jetpack Compose and Hilt and stores voice commands locally using Room.

## Project Structure

```
app/
  src/main/java/app/naga/audiotranscription/
    data/      # Data layer (API clients, database, repositories)
    domain/    # Domain models and repository interfaces
    feature/   # UI features built with Jetpack Compose
    ui/        # Application theme and UI utilities
```

- **data** contains implementations for network access (`api`), WebSocket communication (`websocket`), local database (`db`), and repositories.
- **domain** defines the application models and repository interfaces.
- **feature** includes UI screens such as the main recording screen and a voice command management screen.

## Building the App

1. Clone this repository and open it with Android Studio.
2. Before building, set your Mimi application credentials in
   `AuthRepositoryImpl`:

   ```kotlin
   companion object {
       private val applicationId: String = "" // Your application id
       private val clientId: String = ""       // Your client id
       private val clientSecret: String = ""   // Your client secret
   }
   ```

   These values are required to obtain an access token from Mimi. See the
   [authentication guide](https://mimi.readme.io/docs/firststep-auth) for how to create them and obtain a token.

3. Build and run the project on an Android device (minimum SDK 27).

The app connects to Mimi's
[ASR WebSocket service](https://mimi.readme.io/docs/asr-websocket-service)
for real‑time speech recognition. Detected text can trigger custom voice
commands which are stored in the local database.

## License

This repository is provided as-is for demonstration purposes.
