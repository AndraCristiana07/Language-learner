
<!-- PROJECT LOGO -->
<br />
<div align="center">

  <h3 align="center">Recipe finder </h3>

  <p align="center">
    Search tonight's recipe!
    <br />
   
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
         <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#acknowledgments">Acknoledgments</a></li>
    
    
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

A language learner app where the user can learn a new language using fun ways.

**Fronted**

What can you do? You have lessons on animals, colors, fruits and vegetables and more, lessons on grammar, on the way sentences work and many more

Now for a full description of the activities:

* Login / Signup: 
    - a login page where the user needs to input the email and the password
    - if the user does not have an account, they can click on the Sign Up button that will take them to that screen where they need to input a name, email, password, confiramtion of password and a phone number
    - after you click on register, it will take you to the login page again

* Lessons screen:
    - the screen has a header with the users image and their name and a bottom navigation bar
    - here the screen is split into 3 fragments: home, review and profile

* Home fragment:
    - here we have all the lessons listed (animals, fruits, vegetables ...) as well as the special lessons (sentences, speech, grammar) and all their progress bars
    - in the lessons there are images with radio buttons to choose from based on some questions
    - in the sentences lesson you will have to drag and drop the words to form the correct answer
    - in the speech lesson you will hear a sentence in the foreign language it will alse ve written in english and you will have to write the foreign sentence on your own after what you heard
    - in the grammar lesson ypu will have to fill in the blank space with the missing word in the foreign language
    
* Review fragment:
    - here we have ways to review what you've learned so far
    - flashcards where you have a question and when it's flipped it gives you the answer
    - quick quiz that is a 15 questions quizz from the lessons
    - knowledge cards where you can see all the translations for all the categories

* Profile fragment:
    - here you can see the users profile information
    - there's a button to edit this information 
    - a log out button


**Backend**

The backend uses jsons files for the lessons and the sentences and they are stored in a CouchDB database. 
The users are stored in a sqlite database.
All the post, get, put functions are done in the app.js file


### Built With

These are what I used to build the bot:

* ![Android Studio](https://img.shields.io/badge/android%20studio-346ac1?style=for-the-badge&logo=android%20studio&logoColor=white)

* ![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)

* ![JavaScript](https://img.shields.io/badge/JavaScript%20-%23F7DF1E.svg?style=for-the-badge&logo=javascript&logoColor=black)
 
* ![SQLite](https://img.shields.io/badge/sqlite-%2307405e.svg?style=for-the-badge&logo=sqlite&logoColor=white)

* ![CouchDB](https://ziadoua.github.io/m3-Markdown-Badges/badges/CouchDB/couchdb1.svg)


   


<!-- GETTING STARTED -->
## Getting Started

To be able to use this project you will need some stuff first.

### Prerequisites

* You need to install npm and node.js
* Also install [Android studio](https://developer.android.com/studio)
* Install [CouchDB](https://couchdb.apache.org/)

### Installation

If you want to make your own app like this you need to:

1. Clone the repo
   ```sh
   git clone https://github.com/AndraCristiana07/Language-learner.git
   ```
2. To run the frontend use android studio with an [emulator](https://developer.android.com/studio/run/managing-avds). I used the Pixel 3a one with the "UpsideDownCake 34 (API level)" system image.
3. Install dependencies for backend
   ```sh
   npm install
   ```
4. Run the backend
    ```sh
   node ./app.js
   ```


<!-- ROADMAP -->
## Roadmap

- [x] Make home screen (lessons page)
- [x] Make bottom navigation bar
- [x] Login, SignUp screens and navigation
- [x] Make backend for login and signup
- [x] Set up sqlite for users
- [x] Make json for questions
- [x] Set up couchDB for questions
- [x] Make backend functions for questions
- [x] Make screen for lessons
- [x] Modify frontend to make home page buttons dynamically
- [x] Make review fragment
- [x] Make flashcards
- [x] Make animation for flashcards
- [x] Make quick quiz with 10 random questions from questions.json
- [x] Make knowledge cards
- [x] Make profile fragment
- [x] Add name, email, phone
- [x] Add edit profile and log out button
- [x] Make username to appear in lessons activity header
- [x] Make username, email and phone appear in profile fragment
- [x] Add edit profile screen
- [x] Make backend functions for it
- [x] Make sentences json 
- [x] Make backend for it and couchDB
- [x] Make drag and drop lesson quiz for sentences
- [x] Make speech lesson with text to speech
- [x] Add 5 random sentences to quick quiz
- [x] Add images for questions
- [x] Add images for lessons in frontend
- [x] Make grammar lesson
- [ ] Make editable profile image
- [ ] Make overview page
- [ ] Make an XP 
- [ ] Make achievements
- [ ] Count total lessons and total progress
- [ ] Count total days "played"
- [ ] Mark on calendar







<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

Some things that helped me while making this project :
* [Android](https://developer.android.com/develop#core-areas)
* [Retrofit](https://square.github.io/retrofit/)
* [SQLite](https://www.npmjs.com/package/sqlite#install-sqlite3)
* [Nano for CouchDB](https://www.npmjs.com/package/nano)
* [CouchDB](https://docs.couchdb.org/en/stable/)




