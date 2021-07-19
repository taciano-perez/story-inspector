package com.o3.storyinspector.batch;

import com.o3.storyinspector.api.task.AnnotateBookTask;
import com.o3.storyinspector.db.BookDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Command line tool to process all validated unprocessed books.
 */
@SpringBootApplication
public class BookProcessorBatch implements CommandLineRunner {

    private static Logger LOG = LoggerFactory.getLogger(BookProcessorBatch.class);

    @Autowired
    private JdbcTemplate db;

    @Autowired
    private JavaMailSender emailSender;

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        new SpringApplicationBuilder(BookProcessorBatch.class)
                .web(WebApplicationType.NONE)
                .run(args)
                .close();   // exit when we're done
        LOG.info("APPLICATION FINISHED");
    }

    public void run(String... args) throws InterruptedException {
        LOG.info("EXECUTING : command line runner");

        for (int i = 0; i < args.length; ++i) {
            LOG.info("args[{}]: {}", i, args[i]);
        }

        processBooks();
        Thread.sleep(5 * 1000); // prevents error if there were no books to process
        LOG.info("EXITING : command line runner");
    }

    @Transactional
    private void processBooks() {
        // TODO: prevent two different computing engines from trying to process books at the same time
        final List<BookDAO> validatedUnprocessed = BookDAO.findValidatedUnprocessed(db);
        validatedUnprocessed.stream().forEach(this::processEachBook);
    }

    private void processEachBook(final BookDAO bookDAO) {
        LOG.debug("BATCH Processing book " + bookDAO.getTitle());

        final AnnotateBookTask task = new AnnotateBookTask(db, bookDAO.getId(), emailSender);
        task.annotateBook(); // execute synchronously

        LOG.debug("BATCH Finished processing book " + bookDAO.getTitle());
    }
}
