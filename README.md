# League of Legends Statistics Tracker

## Overview
The League of Legends Statistics Tracker is a Java-based application that allows users to retrieve and display real-time data related to League of Legends. By integrating APIs from Twilio, Riot Games, and Reddit, the application provides comprehensive and up-to-date statistics, news, and insights. This project highlights advanced problem-solving skills and showcases proficiency in Java and JavaFX, as well as API integration and concurrency management.

## Features
- **Real-time Data Retrieval**: Seamless integration with Twilio, Riot Games, and Reddit APIs for up-to-the-minute information.
- **Enhanced User Experience**: Leveraged Java concurrency techniques to improve loading animations and backend data processing, resulting in a 15% increase in search efficiency.
- **User-Friendly Interface**: Built using JavaFX, the application provides a clean and intuitive user interface for an engaging user experience.
- **Comprehensive Statistics**: Access detailed statistics for players

## Getting Started
To get a local copy of the project up and running, follow these steps.

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- An IDE that supports Java (e.g., IntelliJ IDEA, Eclipse)
- Internet connection for API access

### Installation
1. **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/league-of-legends-statistics-tracker.git
    cd league-of-legends-statistics-tracker
    ```

2. **Set up environment variables**:
    - Register and obtain API keys from Twilio, Riot Games, and Reddit.
    - Set the environment variables in your system:

      For Windows users, set environment variables using the Command Prompt or PowerShell:
      ```cmd
      set TWILIO_API_KEY=your_twilio_api_key
      set RIOT_API_KEY=your_riot_games_api_key
      set REDDIT_API_KEY=your_reddit_api_key


      For MacOS or Linux users, set environment variables in the terminal:
      ```bash
      export TWILIO_API_KEY=your_twilio_api_key
      export RIOT_API_KEY=your_riot_games_api_key
      export REDDIT_API_KEY=your_reddit_api_key
      ```

3. **Build and run the project**:
    - Open the project in your IDE.
    - Build the project and resolve any dependencies by running:
      ```bash
      ./gradlew build
      ```
    - Run the application:
      ```bash
      ./gradlew run
      ```



