package com.o3.storyinspector.api;

import com.o3.storyinspector.api.user.GoogleId;
import com.o3.storyinspector.api.user.UserInfo;
import com.o3.storyinspector.db.BookDAO;
import com.o3.storyinspector.domain.Characters;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;
import com.o3.storyinspector.storydom.Character;
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
@RequestMapping("/api/character")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CharacterApi {
    final Logger logger = LoggerFactory.getLogger(CharacterApi.class);

    @Autowired
    private JdbcTemplate db;

    @Autowired
    private GoogleId userValidator;

    @GetMapping("/{bookId}")
    public Characters one(@PathVariable final Long bookId, @RequestParam("id_token") final String idToken) {
        logger.trace("GET CHARACTERS BOOK ID=[" + bookId + "]");
        final UserInfo user = userValidator.retrieveUserInfo(idToken);
        final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);
        if (!user.isAdmin()) user.emailMatches(bookDAO.getUserEmail());
        Characters characters;
        try {
            final String annotatedStoryDom = bookDAO.getAnnotatedStoryDom();
            final Book book = XmlReader.readBookFromXmlStream(new StringReader(annotatedStoryDom));
            characters = Characters.buildFromBook(book);
        } catch (final Exception e) {
            final String errMsg = "Unexpected error when building book structure report. Book bookId: " +
                    bookId + "Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            return null;
        }
        return characters;
    }

    @PutMapping("/{bookId}/{chapterId}/{characterName}/{id_token}")
    public ResponseEntity<Long> putCharacter(@PathVariable final Long bookId,
                                             @PathVariable final String chapterId,
                                             @PathVariable final String characterName,
                                             @PathVariable("id_token") final String idToken) {
        logger.trace("PUT CHARACTER BOOK ID: " + bookId + ", CHAPTER ID: " + chapterId + ", NAME: " + characterName);
        final UserInfo user = userValidator.retrieveUserInfo(idToken);
        final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);
        if (!user.isAdmin()) user.emailMatches(bookDAO.getUserEmail());
        try {
            // unmarshal storydom
            final String annotatedStoryDom = bookDAO.getAnnotatedStoryDom();
            final Book book = XmlReader.readBookFromXmlStream(new StringReader(annotatedStoryDom));

            // add character to optionalChapter
            Optional<Chapter> optionalChapter = book.getChapters().stream().filter(c -> chapterId.equals(c.getId())).findFirst();
            if (optionalChapter.isPresent()) {
                final Chapter chapter = optionalChapter.get();
                final Character newCharacter = new Character();
                newCharacter.setName(characterName);
                chapter.getMetadata().getCharacters().getCharacters().add(newCharacter);
            } else {
                final String errMsg = "Error: optionalChapter not found when putting new character. Book bookId: " + bookId +
                        ", chapterId: " + chapterId +
                        ", characterName: " + characterName;
                logger.error(errMsg);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // update re-marshalled storydom in db
            BookDAO.updateBook(db, XmlWriter.exportBookToString(book), bookId);
        } catch (final Exception e) {
            final String errMsg = "Unexpected error when putting new character. Book bookId: " + bookId +
                    ", chapterId: " + chapterId +
                    ", characterName: " + characterName + ", Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/rename/{bookId}/{characterName}/{newCharacterName}/{id_token}")
    public ResponseEntity<Long> renameCharacter(@PathVariable final Long bookId,
                                                @PathVariable final String characterName,
                                                @PathVariable final String newCharacterName,
                                                @PathVariable("id_token") final String idToken) {
        logger.trace("RENAME CHARACTER BOOK ID: " + bookId + ", NAME: " + characterName + ", NEW NAME: " + newCharacterName);
        final UserInfo user = userValidator.retrieveUserInfo(idToken);
        final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);
        if (!user.isAdmin()) user.emailMatches(bookDAO.getUserEmail());
        try {
            // unmarshal storydom
            final String annotatedStoryDom = bookDAO.getAnnotatedStoryDom();
            final Book book = XmlReader.readBookFromXmlStream(new StringReader(annotatedStoryDom));

            // add modification to storydom
            final Modification renameCharacter = new Modification();
            renameCharacter.setEntity(EntityType.CHARACTER.asString());
            renameCharacter.setTransformation(ModificationType.RENAME.asString());
            renameCharacter.setName(characterName);
            renameCharacter.setNewName(newCharacterName);
            book.getModifications().add(renameCharacter);

            // update re-marshalled storydom in db
            BookDAO.updateBook(db, XmlWriter.exportBookToString(book), bookId);
        } catch (final Exception e) {
            final String errMsg = "Unexpected error when renaming character. Book bookId: " +
                    bookId + " characterName: " + characterName + ", newCharacterName: " + newCharacterName +
                    ", Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{bookId}/{characterName}/{id_token}")
    public ResponseEntity<Long> deleteCharacter(@PathVariable final Long bookId,
                                                @PathVariable final String characterName,
                                                @PathVariable("id_token") final String idToken) {
        logger.trace("DELETE CHARACTER BOOK ID: " + bookId + ", NAME: " + characterName);
        final UserInfo user = userValidator.retrieveUserInfo(idToken);
        final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);
        if (!user.isAdmin()) user.emailMatches(bookDAO.getUserEmail());
        try {
            // unmarshal storydom
            final String annotatedStoryDom = bookDAO.getAnnotatedStoryDom();
            final Book book = XmlReader.readBookFromXmlStream(new StringReader(annotatedStoryDom));

            // add modification to storydom
            final Modification deleteCharacter = new Modification();
            deleteCharacter.setEntity(EntityType.CHARACTER.asString());
            deleteCharacter.setTransformation(ModificationType.REMOVE.asString());
            deleteCharacter.setName(characterName);
            book.getModifications().add(deleteCharacter);

            // update re-marshalled storydom in db
            BookDAO.updateBook(db, XmlWriter.exportBookToString(book), bookId);
        } catch (final Exception e) {
            final String errMsg = "Unexpected error when deleting character. Book bookId: " +
                    bookId + " characterName: " + characterName + ", Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            return null;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
