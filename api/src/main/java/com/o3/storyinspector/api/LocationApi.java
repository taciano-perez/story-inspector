package com.o3.storyinspector.api;

import com.o3.storyinspector.db.BookDAO;
import com.o3.storyinspector.domain.Locations;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;
import com.o3.storyinspector.storydom.Location;
import com.o3.storyinspector.storydom.Modification;
import com.o3.storyinspector.storydom.constants.EntityType;
import com.o3.storyinspector.storydom.constants.ModificationType;
import com.o3.storyinspector.storydom.io.XmlReader;
import com.o3.storyinspector.storydom.io.XmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.StringReader;
import java.util.Optional;

@RestController
@RequestMapping("/api/location")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LocationApi {
    final Logger logger = LoggerFactory.getLogger(LocationApi.class);

    @Autowired
    private JdbcTemplate db;

    @GetMapping("/{bookId}")
    public Locations one(@PathVariable final Long bookId) {
        logger.trace("GET LocationS BOOK ID=[" + bookId + "]");
        final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);
        Locations locations;
        try {
            final String annotatedStoryDom = bookDAO.getAnnotatedStoryDom();
            final Book book = XmlReader.readBookFromXmlStream(new StringReader(annotatedStoryDom));
            locations = Locations.buildFromBook(book);
        } catch (final Exception e) {
            final String errMsg = "Unexpected error when building book structure report. Book bookId: " +
                    bookId + "Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            return null;
        }
        return locations;
    }

    @PutMapping("/{bookId}/{chapterId}/{LocationName}")
    public ResponseEntity<Long> putLocation(@PathVariable final Long bookId, @PathVariable final String chapterId, @PathVariable final String LocationName) {
        logger.trace("PUT Location BOOK ID: " + bookId + ", CHAPTER ID: " + chapterId + ", NAME: " + LocationName);
        final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);
        try {
            // unmarshal storydom
            final String annotatedStoryDom = bookDAO.getAnnotatedStoryDom();
            final Book book = XmlReader.readBookFromXmlStream(new StringReader(annotatedStoryDom));

            // add Location to optionalChapter
            Optional<Chapter> optionalChapter = book.getChapters().stream().filter(c -> chapterId.equals(c.getId())).findFirst();
            if (optionalChapter.isPresent()) {
                final Chapter chapter = optionalChapter.get();
                final Location newLocation = new Location();
                newLocation.setName(LocationName);
                chapter.getMetadata().getLocations().getLocations().add(newLocation);
            } else {
                final String errMsg = "Error: optionalChapter not found when putting new Location. Book bookId: " + bookId +
                        ", chapterId: " + chapterId +
                        ", LocationName: " + LocationName;
                logger.error(errMsg);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // update re-marshalled storydom in db
            BookDAO.updateBook(db, XmlWriter.exportBookToString(book), bookId);
        } catch (final Exception e) {
            final String errMsg = "Unexpected error when putting new Location. Book bookId: " + bookId +
                    ", chapterId: " + chapterId +
                    ", LocationName: " + LocationName + ", Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/rename/{bookId}/{LocationName}/{newLocationName}")
    public ResponseEntity<Long> renameLocation(@PathVariable final Long bookId, @PathVariable final String LocationName, @PathVariable final String newLocationName) {
        logger.trace("RENAME Location BOOK ID: " + bookId + ", NAME: " + LocationName + ", NEW NAME: " + newLocationName);
        final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);
        try {
            // unmarshal storydom
            final String annotatedStoryDom = bookDAO.getAnnotatedStoryDom();
            final Book book = XmlReader.readBookFromXmlStream(new StringReader(annotatedStoryDom));

            // add modification to storydom
            final Modification renameLocation = new Modification();
            renameLocation.setEntity(EntityType.LOCATION.asString());
            renameLocation.setTransformation(ModificationType.RENAME.asString());
            renameLocation.setName(LocationName);
            renameLocation.setNewName(newLocationName);
            book.getModifications().add(renameLocation);

            // update re-marshalled storydom in db
            BookDAO.updateBook(db, XmlWriter.exportBookToString(book), bookId);
        } catch (final Exception e) {
            final String errMsg = "Unexpected error when renaming Location. Book bookId: " +
                    bookId + " LocationName: " + LocationName + ", newLocationName: " + newLocationName +
                    ", Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            return null;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{bookId}/{LocationName}")
    public ResponseEntity<Long> deleteLocation(@PathVariable final Long bookId, @PathVariable final String LocationName) {
        logger.trace("DELETE Location BOOK ID: " + bookId + ", NAME: " + LocationName);
        final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);
        try {
            // unmarshal storydom
            final String annotatedStoryDom = bookDAO.getAnnotatedStoryDom();
            final Book book = XmlReader.readBookFromXmlStream(new StringReader(annotatedStoryDom));

            // add modification to storydom
            final Modification deleteLocation = new Modification();
            deleteLocation.setEntity(EntityType.LOCATION.asString());
            deleteLocation.setTransformation(ModificationType.REMOVE.asString());
            deleteLocation.setName(LocationName);
            book.getModifications().add(deleteLocation);

            // update re-marshalled storydom in db
            BookDAO.updateBook(db, XmlWriter.exportBookToString(book), bookId);
        } catch (final Exception e) {
            final String errMsg = "Unexpected error when deleting Location. Book bookId: " +
                    bookId + " LocationName: " + LocationName + ", Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            return null;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
