# A5-TicTacToe App

## About Us

This app was built by:

- **Aditya Ashok Tailor**  
  Student ID: 2022A7PS0389G  
  Email: [f20220389@goa.bits-pilani.ac.in](mailto:f20220389@goa.bits-pilani.ac.in)

- **S Mohammed Ashraf**  
  Student ID: 2022A7PS0645G  
  Email: [f20220645@goa.bits-pilani.ac.in](mailto:f20220645@goa.bits-pilani.ac.in)



## Brief Description

The **TicTacToe App** is a mobile game that allows users to play the classic **Tic-Tac-Toe** game either in **single-player** or **two-player mode**. The app features a simple and intuitive interface, keeping the focus on gameplay and allowing users to track their wins. The app integrates a **Firebase** backend to store player data and scores.

**Key Features:**

- **Single-Player Mode**: Play against the AI in a challenging Tic-Tac-Toe game.
- **Two-Player Mode**: Play with another user logged into the app.
- **Firebase Integration**: Sync and store user scores in real-time.
- **Score Tracking**: View win counts for both players and overall game statistics.
- **User Authentication**: Users can log in and register using Firebase Authentication.
- **Smooth UI/UX Design**: Designed with an easy-to-use interface for a seamless gaming experience.

  
## Overview of Task Completion

1. **Game Logic Implementation**:
    - *AI Player* : Developed a simple AI opponent for the single-player mode that uses the randomized algorithm for decision-making.
    - *Two-Player Mode* : Implemented an online two-player mode where users take turns playing on a their individual devices.

2. **Firebase Integration**:
    - *User Authentication* : Integrated Firebase Authentication to allow users to log in or register with their email and password.
    - *Score Tracking* : Stored player scores in Firebase Firestore, ensuring real-time sync between players' scores.
    
3. **UI Updates**:
    - *GameFragment* :  Designed the main game screen with a 3x3 grid where players can make their moves. The game board updates dynamically.
    - *Game Over Screen* : Displayed game over messages when a player wins or the game results in a draw. The screen shows options to play again or go back to the main screen.
    - *DashboardFragment* : Made a comprehensive Dashboard that allows users to see their Total Wins/Losses and also the Open Games for them to play in Two-Player Mode.
    - *LoginFragment* : Made a Login page that allows authenticated users to login to the game, and at the same time also allows new users to register to the app. 

4. **Accessibility Features**:
    - *Talk Back Experience* : Ran the app using Talk Back to evaluate the accessibility of the UI. The experience was positive, as all interactive elements were properly labeled, making navigation intuitive for visually impaired users.
    - *Accessibility Scanner Report* : Conducted a report using the Accessibility Scanner, identifying potential accessibility issues, which were addressed to improve user experience.
    - *Espresso Tests* : Developed Espresso test cases that not only verify the functionality of UI components but also ensure accessibility compliance, such as checking text visibility and navigation.
  
5. **UI Tests for Navigation**:
    - Created comprehensive UI tests to ensure proper navigation flows between fragments, confirming that all transitions operate smoothly and the user experience remains intact.
    
---

## Testing

Once the logic for the app was built, we wrote various test cases to ensure that the gameplay mechanics, user authentication, and Firebase interactions were functioning as expected.

### Key Testing Highlights:

1. **JUnit Tests**:
    - Added comprehensive **JUnit tests** for testing the logic of the `GameFragment`.
    - The tests also focused on checking the correct winner determination and tie conditions.

2. **Instrumented Tests**:
   - The instrumented tests focused on the core app functionality, particularly the navigation between screens, and validating game state updates.
     1) *GameFlow*:
        - Starts the game and verifies the correct functionality of the LoginFrgment
        - Ensures that on clicking the Login button, the Dashboard is displayed with the user details and List of open games.
     2) *Single-Player Wins*:
        - Clicks on the New Game option and selects One-Player option.
        - Mocks the behaviour of the AI and lets the user win and checks if the user's Win Count is updated or not.
     3) *Single-Player Loss*:
        - Clicks on the New Game option and selects One-Player option.
        - Mocks the behaviour of the AI and lets the user lose and checks if the user's Loss Count is updated or not.
     4) *Quit Button functionality*:
        - Ensures that on clicking the Quit button in the One Player mode, the total wins and loss count remains the same.

3. **Espresso Tests**:
     1) *Button Accessibility Checks*:
        - Ensures all buttons (start game, play again, etc.) are accessible and clickable with proper content descriptions.
     2) *Navigation Tests*:
        - Verifies that tapping the play button navigates to the Tic-Tac-Toe grid, and the "Quit" button correctly returns the user to the login or main screen.
     3) *UI Elements Validation*:
        - Ensures that UI elements such as the grid, score counters, and action buttons are displayed correctly and respond to user inputs as expected.
          
4. **Bug Fixes and Verification**:
   - After implementing new functionality, we tested for potential bugs, ensuring that everything from Firebase sync to game logic worked as intended.


## Accessibility Enhancement  
- **Accessibility Scanner Report:**
    - We tested the app on Accessiblity Scanner to identify and address potential accessibility issues, ensuring that users with disabilities can navigate and interact with the app effectively.
    - Upon using the Accessibility Scanner we received the following feedbacks for improvements on our UI in order to make it even more accessible to differently abled users:
 
      ![Screenshot (92)](https://github.com/user-attachments/assets/9256c2f0-4eb7-4be1-91a3-103b1044ab5e)
      
      ![Screenshot (93)](https://github.com/user-attachments/assets/2d039c94-51d2-433a-8dbc-1a3fbb840484)
 
      ![Screenshot (94)](https://github.com/user-attachments/assets/3b7500e6-c313-48a0-b167-22c3c52830ce)

     - These are some of the images containing the analysis of the UI as done by the Accessibility Scanner. Similarly we had a total of 8 screens analysed.
     - Accordingly, we changed the Text Contrast Ratio and added Content Descriptions for all the UI elements.


## Time Taken

The project took approximately **28** hours to code, test, and refine.

## Pair Programming

We engaged in pair programming throughout the project, allowing us to collaboratively tackle challenges, review code, and ensure thorough documentation.

## Difficulty Level

We would rate the difficulty of this assignment as **9** out of 10. Some of the challenges faced included writing the Instrumented Testcases, integrating the Firebase and ensuring data sync in real-time, and making sure that UI had a good accessibility score.

## Bibliography

1. **ChatGPT and Claude:**  
   Used ChatGPT for understanding how to integrate the various libraries and familiarize ourselves with their syntax.
   
2. **Android Documentation:**  
   Referred to [Android Documentation](https://developer.android.com/docs) for implementing MenuBar action items and Fragments.
   
3. **Swaroop Sir's course site:**
   We referred to [Swaroop Sir's website](https://swaroopjoshi.in/courses/mobile-app-dev/13-firebase/) in order to understand how Firebase works.
