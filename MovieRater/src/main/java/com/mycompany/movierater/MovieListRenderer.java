package com.mycompany.movierater;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.JList;

/**
 * Custom ListCellRenderer that sets the display to be text and icon(poster)
 * @author agnes
 */
public class MovieListRenderer extends DefaultListCellRenderer{
    public MovieListRenderer() {
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    @Override
    public Component getListCellRendererComponent(JList<?> list,
                                                   Object value,
                                                   int index,
                                                   boolean isSelected,
                                                   boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        Movie movie = (Movie) value;
        setText(movie.getTitle());
        setIcon(movie.getPoster());
        return this;
    }
    
}
