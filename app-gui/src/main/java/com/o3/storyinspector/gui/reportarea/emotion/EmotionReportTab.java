package com.o3.storyinspector.gui.reportarea.emotion;

import com.o3.storyinspector.gui.utils.IconUtils;
import com.o3.storyinspector.storydom.Book;
import javafx.scene.control.Tab;
import org.controlsfx.glyphfont.FontAwesome;

public class EmotionReportTab extends Tab {

    public EmotionReportTab(Book book) {
        super(book.getTitle() + " (Emotions)");
        this.setGraphic(IconUtils.getIcon(FontAwesome.Glyph.HEART));
    }
}
