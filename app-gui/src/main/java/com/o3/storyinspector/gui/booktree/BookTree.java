package com.o3.storyinspector.gui.booktree;

import com.o3.storyinspector.gui.core.*;
import com.o3.storyinspector.gui.utils.I18N;
import com.o3.storyinspector.gui.utils.IconUtils;
import com.o3.storyinspector.storydom.Book;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.controlsfx.glyphfont.FontAwesome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public class BookTree extends TreeView implements BookEventListener {

    static final Logger LOGGER = LoggerFactory.getLogger(BookTree.class);

    static String BOOK_TREE_MY_LIB = "bookTreeMyLibrary";
    static String BOOK_BY = "bookBy";

    TreeItem<String> rootItem;

    public BookTree() throws IOException, JAXBException {
        // root
        rootItem = new TreeItem<> (I18N.stringFor(BOOK_TREE_MY_LIB),
                IconUtils.getIcon(FontAwesome.Glyph.LIST_UL));
        rootItem.setExpanded(true);
        this.setRoot(rootItem);

        // books
        final List<Book> books = BookManager.getAllBooks();
        for (final Book book : books) {
            addBook(book);
        }

        // handle clicks
        EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
            handleMouseClicked(event);
        };
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);

        // listen to library changes
        BookManager.registerEventListener(this);
    }

    private void addBook(final Book book) {
        final BookTreeItem<String> item = new BookTreeItem<> (
                book.getTitle() + " " + I18N.stringFor(BOOK_BY) + " " + book.getAuthor(),
                IconUtils.getIcon(FontAwesome.Glyph.BOOK), book);
        rootItem.getChildren().add(item);
    }

    @Override
    public void handleEvent(BookEvent event) {
        if (event.getType() == BookEvent.BOOK_ADDED) {
            addBook(event.getBook());
        }
    }

    private void handleMouseClicked(MouseEvent event) {
        final Node node = event.getPickResult().getIntersectedNode();
        // Accept clicks only on node cells, and not on empty spaces of the TreeView
        if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
            final BookTreeItem clickedItem = (BookTreeItem) this.getSelectionModel().getSelectedItem();
            final String name = (String) clickedItem.getValue();
            LOGGER.info("Node click: " + name);
            ReportManager.fireReportEvent(new ReportEvent(ReportEvent.OPEN_REPORT_BOOK_STRUCTURE, clickedItem.getBook()));
        }
    }
}
