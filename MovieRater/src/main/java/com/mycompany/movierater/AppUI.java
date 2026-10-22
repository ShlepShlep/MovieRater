package com.mycompany.movierater;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.*;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.event.*;

public class AppUI extends javax.swing.JFrame {

    private final MovieLibrary library;

    Movie[] movieDatabase;//our filled movie array
    DefaultListModel<Movie> movieGridModel = new DefaultListModel(); //this is so we can edit the dumb ass JList, because JList is read only
    JList<Movie> movieGrid = new JList<>(movieGridModel); //create the JList, we need it so the Jscroll panel can display the movies
    APIclient client = new APIclient();
    
    String[] statuses = {Movie.WATCHED, Movie.WATCHING, Movie.WANT_TO_WATCH};
    JComboBox<String> listChooser = new JComboBox<>(statuses); //decides which list is shown
    
    JPopupMenu suggestionPopup = new JPopupMenu();
    DefaultListModel<Movie> suggestionModel = new DefaultListModel<>();
    JList<Movie> suggestionList = new JList<>(suggestionModel);
    boolean fillingText = false; //true while we put a chosen title in the box, so that doesn't trigger a new search

    /**
     * Creates new form AppUI
     */
    public AppUI(Movie[] movieDatabase) { //CONSTRUCTORRRRRR
        this.movieDatabase = movieDatabase; //gets the filled database
        library = new MovieLibrary(movieDatabase, MovieLibrary.defaultSaveFile());
        
        initComponents();
        setupAutocomplete();
        setupMovieGrid();
        setupListChooser();

        loadSavedMovies();
        refreshList();
    }
    
    private void setupMovieGrid() {
        int width = 130;
        int height = 200;
        movieGrid.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        movieGrid.setVisibleRowCount(-1);//used so the Jlist doesnt assign a set row value, so it can change based on the window size
        movieGrid.setFixedCellWidth(width); //set width to keep everything uniform
        movieGrid.setFixedCellHeight(height); //set height
        movieGrid.setCellRenderer(new MovieListRenderer()); //setting a custom renderer so that posters can also be shown
        movieGrid.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = movieGrid.locationToIndex(e.getPoint());
                if (index < 0) {
                    return;
                }
                //locationToIndex gives the nearest cell even if you click empty space
                Rectangle cell = movieGrid.getCellBounds(index, index);
                if (cell == null || !cell.contains(e.getPoint())) {
                    return;
                }
                showReview(movieGridModel.get(index));
            }
        });
    }
    
    private void setupListChooser() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.add(new JLabel("Show list:"));
        header.add(listChooser);
        movieScrollPane.setColumnHeaderView(header); //puts it above the posters, right under the search bar
        listChooser.addActionListener(e -> refreshList());
        movieScrollPane.setViewportView(movieGrid); //shows the list
    }

    private void addMovie() {
        String movieTitle = searchField.getText().trim(); //gets the entered title and removes any unneccecery spaces

        if (movieTitle.isEmpty()) {
            return; //if its empty
        }

        Movie foundMovie = null; //variable for the movie we should find but havent found yet

        for (int i = 0; i < movieDatabase.length; i++) { //searching for mathcing titles
            if (movieTitle.equalsIgnoreCase(movieDatabase[i].getTitle())) { //ignores all upper and lowercase and looks if the titles match
                foundMovie = movieDatabase[i]; //assign a movie to the foundMovie
                break;
            }
        }

        if (foundMovie == null) { //if we didnt find the movie in the database
            showMessageDialog(this, "Movie not found: " + movieTitle);
        } else if (library.getMovies().contains(foundMovie)) { //if movie is already in the rated list
            showMessageDialog(this, "Movie already in list");
        } else {
            //the dialog starts on whichever list you're currently looking at
            if (!askForDetails(foundMovie, (String) listChooser.getSelectedItem())) {
                return; //user cancelled, so don't add it
            }
            library.addMovie(foundMovie);
            searchField.setText("");
            listChooser.setSelectedItem(foundMovie.getStatus()); //jump to the list it was added to
            refreshList();
            loadPoster(foundMovie); //loads the poster
        }
    }

    private void loadPoster(Movie movie) {
        new SwingWorker<ImageIcon, Void>() { //swingworker can run code in the background, but cannot change the gui while its in the background
            @Override
            protected ImageIcon doInBackground() throws Exception { //does the code in background
                String posterUrl = client.getPosterUrl(movie.getImdbID()); //gets the poster link
                if (posterUrl == null) { //if link is empty
                    return null;
                } else {
                    Image poster = ImageIO.read(new URI(posterUrl).toURL()); //gets image that is connected to the link
                    return new ImageIcon(poster.getScaledInstance(100, 150, Image.SCALE_SMOOTH)); //sets the same size for all images, makes it smooth and not pixelated
                }
            }

            @Override
            protected void done() {
                try {
                    movie.setPoster(get());//get function gets the return from doInBackground
                    int index = movieGridModel.indexOf(movie); //gets index of the movie
                    if (index >= 0) { //checks if the movie hasnt been deleted by this point
                        movieGridModel.set(index, movie); //sets the movie again so that the size changes to accomodate the poster
                    }
                } catch (Exception e) {
                    System.out.println("Could not load poster");
                }
            }
        }.execute();
    }
    
    private void refreshList() {
        String chosen = (String) listChooser.getSelectedItem();
        movieGridModel.clear();
        for (Movie m : library.getMovies()) {
            if (chosen.equals(m.getStatus())) {
                movieGridModel.addElement(m);
            }
        }
    }
    
    private void loadSavedMovies() {
        try {
            library.load();
            for(Movie m : library.getMovies()) {
                loadPoster(m);
            }
        } catch (IOException e) {
            showMessageDialog(this, "Could not read a saved list");
        }
    }
    
    private void saveMovies() {
        try {
            showMessageDialog(this, "Saved " + library.save() + " movies");
        } catch(IOException e) {
            showMessageDialog(this, "Could not save");
        }
    }

//used both when adding a movie and when editing one, returns false if cancelled
    private boolean askForDetails(Movie movie, String startStatus) {
        JComboBox<String> statusBox = new JComboBox<>(statuses);
        statusBox.setSelectedItem(startStatus);

        JComboBox<Integer> ratingBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        ratingBox.setSelectedItem(movie.getUserRating() > 0 ? movie.getUserRating() : 3);

        JTextArea reviewArea = new JTextArea(movie.getUserReview(), 6, 25) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    g.setColor(Color.GRAY);
                    g.setFont(getFont());
                    Insets in = getInsets();
                    g.drawString("Write a review...", in.left, in.top + g.getFontMetrics().getAscent());
                }
            }
        };
        reviewArea.setLineWrap(true);
        reviewArea.setWrapStyleWord(true);

        //rating and review only work when the category is "Watched"
        Runnable updateEnabled = () -> {
            boolean watched = Movie.WATCHED.equals(statusBox.getSelectedItem());
            ratingBox.setEnabled(watched);
            reviewArea.setEnabled(watched);
        };
        statusBox.addActionListener(e -> updateEnabled.run());
        updateEnabled.run();

        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusRow.add(new JLabel("Category:"));
        statusRow.add(statusBox);

        JPanel ratingRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingRow.add(new JLabel("Your rating (out of 5):"));
        ratingRow.add(ratingBox);

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.add(statusRow);
        top.add(ratingRow);

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(reviewArea), BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel,
                movie.getTitle(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        String chosen = (String) statusBox.getSelectedItem();
        movie.setStatus(chosen);
        if (chosen.equals(Movie.WATCHED)) {
            movie.setUserRating((int) ratingBox.getSelectedItem());
            movie.setUserReview(reviewArea.getText().trim());
        }
        return true;
    }

    private void showReview(Movie movie) {
        boolean watched = movie.getStatus().equals(Movie.WATCHED);

        JPanel top = new JPanel(new GridLayout(0, 1));
        top.add(new JLabel("Category: " + movie.getStatus()));

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(top, BorderLayout.NORTH);

        if (watched) {
            String stars = "★".repeat(movie.getUserRating()) + "☆".repeat(5 - movie.getUserRating());
            top.add(new JLabel("Your rating: " + stars + " (" + movie.getUserRating() + "/5)"));

            JTextArea reviewText = new JTextArea(movie.getUserReview().isEmpty() ? "(no review written)" : movie.getUserReview(), 6, 25);
            reviewText.setEditable(false);
            reviewText.setLineWrap(true);
            reviewText.setWrapStyleWord(true);
            panel.add(new JScrollPane(reviewText), BorderLayout.CENTER);
        }

        Object[] buttons = {"Edit", "Delete", "Close"};
        int choice = JOptionPane.showOptionDialog(this, panel, movie.getTitle(),
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[2]);

        if (choice == 0) { //"Edit" was clicked
            if (askForDetails(movie, movie.getStatus())) {
                refreshList(); //if the category changed, the movie moves out of this list
            }
        } else if (choice == 1) {
            int reply = JOptionPane.showConfirmDialog(this, "Remove " + movie.getTitle() + " from your list?", "Delete", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                library.removeMovie(movie);
                refreshList();
            }
        }
    }
    
    private void setupAutocomplete() {
        suggestionList.setFocusable(false); //so the popup never steals focus from the text field
        suggestionPopup.setFocusable(false);
        suggestionPopup.add(suggestionList);

        //show "Title (year)" in the suggestions
        suggestionList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Movie m = (Movie) value;
                setText(m.getTitle() + " (" + m.getYear() + ")");
                return this;
            }
        });

        //update suggestions on every keystroke
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        //arrow keys move through the suggestions, escape closes them
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!suggestionPopup.isVisible()) {
                    return;
                }
                int size = suggestionModel.size();
                int selected = suggestionList.getSelectedIndex();
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    suggestionList.setSelectedIndex(Math.min(selected + 1, size - 1));
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    suggestionList.setSelectedIndex(Math.max(selected - 1, 0));
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    suggestionPopup.setVisible(false);
                }
            }
        });

        //clicking a suggestion puts its title in the box
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = suggestionList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    suggestionList.setSelectedIndex(index);
                    acceptSuggestion();
                }
            }
        });

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                searchField.selectAll(); //so typing replaces the "Title here" text
            }

            @Override
            public void focusLost(FocusEvent e) {
                suggestionPopup.setVisible(false);
            }
        });
    }

    private void updateSuggestions() {
        if (fillingText) {
            return;
        }
        
        String query = searchField.getText();
            
        if (query.length() < 2) {
            suggestionPopup.setVisible(false);
            return;
        }

        //score every title, lower score = better match, -1 = no match
        HashMap<Movie, Integer> scores = new HashMap<>();
        ArrayList<Movie> matches = new ArrayList<>();
        for (Movie m : movieDatabase) {
            int score = matchScore(m.getTitle().toLowerCase(), query);
            if (score >= 0) {
                scores.put(m, score);
                matches.add(m);
            }
        }

        //best score first, and if equal then the higher rated movie first
        matches.sort((a, b) -> {
            int byScore = Integer.compare(scores.get(a), scores.get(b));
            return byScore != 0 ? byScore : Double.compare(b.getRating(), a.getRating());
        });

        int maxSuggestions = 6;
        int minWidth = 260;
        suggestionModel.clear();
        for (int i = 0; i < Math.min(maxSuggestions, matches.size()); i++) {
            suggestionModel.addElement(matches.get(i));
        }

        if (suggestionModel.isEmpty()) {
            suggestionPopup.setVisible(false);
            return;
        }
        suggestionList.setFixedCellWidth(Math.max(searchField.getWidth(), minWidth));
        if (suggestionPopup.isVisible()) {
            suggestionPopup.pack(); //resize for the new number of rows
        } else {
            suggestionPopup.show(searchField, 0, searchField.getHeight());
        }
    }

    private void acceptSuggestion() {
        Movie chosen = suggestionList.getSelectedValue();
        suggestionPopup.setVisible(false);
        if (chosen != null) {
            fillingText = true;
            searchField.setText(chosen.getTitle());
            fillingText = false;
        }
        searchField.requestFocusInWindow();
    }

    //0 = title starts with the query, 1 = a word in the title starts with it, 2 = title contains it,
    //3+ = close but with typos, -1 = no match
    private int matchScore(String title, String query) {
        if (title.startsWith(query)) {
            return 0;
        }
        for (int s = 1; s < title.length(); s++) {
            if (title.charAt(s - 1) == ' ' && title.startsWith(query, s)) {
                return 1;
            }
        }
        if (title.contains(query)) {
            return 2;
        }

        //how many typos we allow depends on how much was typed
        int maxTypos = query.length() <= 3 ? 0 : (query.length() <= 6 ? 1 : 2);
        if (maxTypos == 0) {
            return -1;
        }

        //compare the query to the start of the title and the start of every word,
        //using chunks about as long as the query
        int best = Integer.MAX_VALUE;
        for (int s = 0; s < title.length(); s++) {
            if (s != 0 && title.charAt(s - 1) != ' ') {
                continue;
            }
            for (int len = query.length() - 1; len <= query.length() + 1; len++) {
                if (len > 0 && s + len <= title.length()) {
                    best = Math.min(best, editDistance(query, title.substring(s, s + len)));
                }
            }
        }
        return best <= maxTypos ? 3 + best : -1;
    }

    //number of single-letter changes (add, remove, replace, swap two neighbours) to turn a into b
    private int editDistance(String a, String b) {
        int[][] d = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            d[0][j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + cost);
                if (i > 1 && j > 1 && a.charAt(i - 1) == b.charAt(j - 2) && a.charAt(i - 2) == b.charAt(j - 1)) {
                    d[i][j] = Math.min(d[i][j], d[i - 2][j - 2] + 1); //swapped letters like "lr" vs "rl"
                }
            }
        }
        return d[a.length()][b.length()];
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        movieScrollPane = new javax.swing.JScrollPane();
        searchField = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        searchField.addActionListener(this::searchFieldActionPerformed);

        addButton.setText("ADD");
        addButton.addActionListener(this::addButtonActionPerformed);

        saveButton.setText("SAVE");
        saveButton.addActionListener(this::saveButtonActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(movieScrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveButton, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(searchField)
                    .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(saveButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(movieScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        addMovie();
    }//GEN-LAST:event_addButtonActionPerformed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        if (suggestionPopup.isVisible() && suggestionList.getSelectedIndex() >= 0) {
            acceptSuggestion(); //enter on a highlighted suggestion fills the box
        } else {
            suggestionPopup.setVisible(false);
            addMovie();
        }
    }//GEN-LAST:event_searchFieldActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        saveMovies();
    }//GEN-LAST:event_saveButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JScrollPane movieScrollPane;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField searchField;
    // End of variables declaration//GEN-END:variables
}
