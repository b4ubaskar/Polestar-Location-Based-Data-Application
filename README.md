# Polestar Location-Based Data Application

## Overview
Application to present location-based data (restaurants, charging stations, etc.) based on car location.
Includes filtering algorithms for different car models and historical data storage.

## Architecture
- **Domain Layer**: Core business logic and entities
- **Algorithm Layer**: Filtering strategies for different car models
- **Data Layer**: Repository implementations and data sources
- **Service Layer**: Application services coordinating the flow

## Key Features
- ✅ Location filtering with car-model-specific algorithms
- ✅ In-memory historical data storage (persists while app runs)
- ✅ Point of Interest (POI) discovery
- ✅ Session management
- ✅ Extensible design for new car models

## Requirements
- Java 17 or higher
- Gradle 8.x

## Build & Run
```bash
# Build the project
./gradlew build

# Run the application
./gradlew run

## Assumptions
1. Algorithms will be implemented by neighbouring team (interfaces provided)
2. External API calls are mocked (comments indicate where real calls would go)
3. Data storage is in-memory only (no database required)
4. Application is explicitly started by user