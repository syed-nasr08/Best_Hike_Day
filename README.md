# Best Hike Day

An Android application that helps you find the best day for hiking by analyzing weather forecasts. The app uses OpenWeatherMap API to fetch weather data and calculates a "hike score" for each day based on weather conditions.

## Features

- 🌤️ **Weather Forecast**: View 5-day weather forecasts for any location
- 🏔️ **Best Day Calculator**: Automatically calculates and highlights the best day for hiking based on weather conditions
- 📍 **Location Search**: Search for any city or location worldwide
- 📱 **Location Services**: Uses your device's GPS to get weather for your current location
- 🎨 **Modern UI**: Built with Jetpack Compose and Material Design 3

## Screenshots

*Add screenshots of your app here*

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK (API level 24 or higher)
- Kotlin
- An OpenWeatherMap API key (free tier available)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd "Best Hike Day"
```

### 2. Get an OpenWeatherMap API Key

1. Visit [OpenWeatherMap](https://openweathermap.org/api)
2. Sign up for a free account
3. Navigate to the API keys section
4. Generate a new API key (it may take a few minutes to activate)

### 3. Configure the API Key

1. Open the `local.properties` file in the root directory
2. Add your API key:

```properties
OPENWEATHER_API_KEY=your_api_key_here
```

**Note**: The `local.properties` file is already in `.gitignore` and will not be committed to version control. This keeps your API key secure.

### 4. Build and Run

1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or physical device

## Project Structure

```
app/src/main/java/com/example/besthikeday/
├── data/
│   ├── api/              # Retrofit API services
│   └── model/            # Data models
├── ui/
│   ├── navigation/       # Navigation setup
│   ├── screens/          # Compose screens
│   └── theme/           # App theme and styling
├── util/                 # Utility classes (LocationHelper)
└── viewmodel/           # ViewModels for state management
```

## Technologies Used

- **Kotlin**: Programming language
- **Jetpack Compose**: Modern UI toolkit
- **Material Design 3**: Design system
- **Retrofit**: HTTP client for API calls
- **Gson**: JSON parsing
- **Coroutines**: Asynchronous programming
- **ViewModel**: Architecture component for UI-related data
- **Google Play Services Location**: Location services

## API Usage

This app uses the OpenWeatherMap API:
- **Forecast API**: For 5-day weather forecasts
- **Geocoding API**: For location search

## Permissions

The app requires the following permissions:
- `INTERNET`: To fetch weather data
- `ACCESS_FINE_LOCATION`: To get your current location
- `ACCESS_COARSE_LOCATION`: For approximate location

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source and available under the MIT License.

## Acknowledgments

- Weather data provided by [OpenWeatherMap](https://openweathermap.org/)
- Built with [Jetpack Compose](https://developer.android.com/jetpack/compose)

