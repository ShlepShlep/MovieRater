# Movie tracker
An app to track and rate movies/TV-shows

## Description
This project is a CBL submission for the 2IP90 course in TU/e, made by Agne and Amelia.
The applications functions as a simple movie/TV-show list, where the user can add, remove and rate the titles they choose.

The movies/TV-shows are sorted into three lists:
- **Watched**: (elements in the **Watched** list can be rated and reviewed)
- **Watching**
- **Want to watch**
  
Every title is shown with its poster for easier recognition and prettier UI. The posters are downloaded from the OMDB API.
The saved movie/TV-show list can saved in a file (placed in the users home folder) and loaded again once the application starts.

## Requirements
- **Java Development Kit (JDK) 26**
- **Apache NetBeans IDE 31**
- **An internet connection**

## How to build and run
1. Install Java Development Kit (JDK) 26
2. Install Apache NetBeans IDE 31
3. Open the project
   - Download the submitted zip file and extract it
   - Using Apache NetBeans, open the project and click "Run Project" or press F6
  
## How to use
- **Searching for a movie/TV-show**: type in at least two letters into the text field at the top left of the app. Suggestions will appear
- **Choosing a suggestion**: click the suggestion with a mouse or use the arrow keys and press the Enter button
- **Adding the movie**: after choosing a title, press Enter or click the ADD button. A dialog will open with options to choose a list(Watched, Watching, Want to watch). For watched elements the user can give a rating (1-5 stars) and write a review
- **Switching between lists**: the displayed lists can be changed by pressing the combo box above the posters and choosing the desired list
- **Accessing movie details**: click the poster with a mouse to edit or delete the element
- **Saving the current lists**: click SAVE to save your lists in a JSON file. Changes are only kept after clicking SAVE

**The save file** is saved as a `savedMovies.json` in the users home folder:
- `C:\Users\<your name>\savedMovies.json` on Windows
- `/home/<your name>/savedMovies.json` on Linux

# Used libraries
- Jackson Databind: reading and writing JSON
- FlatLaf: dark mode and colorful accents
- OMDb API: fetching movie posters

## Project structure
 Class | Action |
|---|---|
| `MovieRater` | Starts the app: sets the dark mode with accents, loads the movie database and opens the window |
| `AppUI` | The main window: search field, buttons, poster grid and dialogs |
| `Movie` | Default data for movie/TV-show with added user's status, rating and review |
| `MovieLibrary` | The user's movies; adds/removes them and saves/loads them as JSON |
| `SavedMovie` | Data written to the save file (IMDb ID, status, rating, review) |
| `MovieSearch` | Finds movies matching the search text, ranked and typo-tolerant |
| `APIclient` | Gets poster links from the OMDb API |
| `MovieListRenderer` | Draws each movie in the grid as a poster with its title |
 
The movie catalog (2000 titles) is stored in `src/main/resources/titles.json`.



