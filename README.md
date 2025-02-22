# SafeSpot - Real-Time Safety Reporting App

[![License](https://img.shields.io/badge/license-Apache%202.0-blue?style=flat-square)](https://github.com/ybitite/SafeSpot/blob/master/LICENSE)
## Description

SafeSpot is an Android application designed to enhance community safety by allowing users to report incidents and view validated reports on a map in real-time. The app utilizes Google Maps and clustering technology to display reports efficiently, even in areas with high report density. Users can add new reports, including descriptions, locations, and images, and view the locations of validated reports.

## Key Features

*   Real-Time Reporting: Users can quickly submit new reports with details about incidents.
*   Location-Based: Reports are tied to specific locations, allowing users to see what's happening around them.
*   Image Support: Users can add images to their reports for better context.
*   Clustered Map View: The app uses clustering to group nearby reports, making it easier to view areas with many incidents.
*   Customizable Cluster Markers: Cluster markers dynamically change color and size based on the number of reports they represent.
*   Validated Reports: The app distinguishes between user-submitted reports and validated reports.
*   User-Friendly Interface: The app is designed to be intuitive and easy to use.
* Current location: The user can center the map on his current location.

## Technologies Used

*   Java
*   Android SDK
*   Google Maps SDK for Android
*   Google Maps Android Clustering Utility
*   Android Jetpack:
    *   AppCompat
    *   Lifecycle
    *   View Binding
* View Model
* Live Data

## Setup/Installation

1.  Clone the Repository:bash git clone [https://github.com/ybitite/SafeSpot]
2.  2.  Open in Android Studio: Open the project in Android Studio.
3.  Google Maps API Key:
    *   You'll need a Google Maps API key to use the map features.
    *   Create a project in the Google Cloud Console.
    *   Enable the "Maps SDK for Android" API.
    *   Create an API key and restrict it to Android apps.
    *   Add the API key to your `local.properties` file : MAPS_API_KEY="YOUR_API_KEY_HERE"
    *   Or add the API key in your `AndroidManifest.xml` file.
4.  Build and Run: Build and run the project on an Android emulator or a physical device.

## Usage

1.  View Reports: Open the app to see a map with clustered markers representing reported incidents.
2.  Explore Clusters: Zoom in to see individual reports within a cluster.
3.  Add a New Report: Tap the "Add Report" button to submit a new report.
4.  Fill in Details: Provide a description, location (or use your current location), and optionally add an image.
5.  Submit: Submit the report.
6. Center the map: Click on the button to center the map on your current location.
7. Validated reports: Click on the button to see only the validated reports.

## Screenshots/GIFs



## Contributing

Contributions are welcome! If you'd like to contribute to SafeSpot, please follow these steps:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix: `git checkout -b feature/your-feature-name`
3.  Make your changes and commit them: `git commit -m "Add your commit message here"`
4.  Push your changes to your forked repository: `git push origin feature/your-feature-name`
5.  Submit a pull request to the main repository.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

[BITITE Youness] - [younes.bitite@gmail.com] - [y.bitite.ch]
