# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

This is a **multi-module Maven project** with 6 modules. All commands should be run from the root directory unless specified.

### Essential Maven Commands
```bash
# Build all modules
mvn clean compile

# Run all tests
mvn test

# Package all modules
mvn clean package

# Run specific module tests
mvn test -pl annotation-engine
mvn test -pl api

# Run specific test class
mvn test -Dtest=AnnotationEngineTest
```

### Running Applications

**API Server (Spring Boot):**
```bash
cd api && mvn spring-boot:run
# Runs on http://localhost:8081 (dev profile, H2 database)
```

**Desktop GUI (JavaFX):**
```bash
cd app-gui && mvn javafx:run
```

**Command Line Tools:**
```bash
# Book Importer
cd book-importer && mvn clean package
java -cp target/book-importer-1.0-SNAPSHOT.jar com.o3.storyinspector.bookimporter.BookImporterCLI -T plain -I input.txt -O output.xml

# Annotation Engine  
cd annotation-engine && mvn clean package
java -cp target/annotation-engine-1.0-SNAPSHOT.jar com.o3.storyinspector.annotation.AnnotationEngineCLI -I input.xml -O annotated.xml

# Visualization Tool
cd viz-tool && mvn clean package
java -cp target/viz-tool-1.0-SNAPSHOT.jar com.o3.storyinspector.viztool.VizToolCLI -I annotated.xml -O report.html
```

### Maven Profiles
- **dev** (default): H2 database, port 8081
- **gcp-compengine**: MySQL for Google Cloud deployment

## Architecture Overview

### Core Data Flow
```
Raw Text → Book Importer → StoryDOM → Annotation Engine → Annotated StoryDOM → API/GUI/Reports
```

### Module Dependencies
- **story-dom**: Foundation module - contains XML schema and domain model (JAXB-generated POJOs)
- **book-importer**: Converts EPUB/DOCX/plain text to StoryDOM XML
- **annotation-engine**: NLP processing pipeline using Stanford CoreNLP + EmoLex
- **viz-tool**: Generates charts and reports from annotated data
- **api**: Spring Boot REST API with async processing and user management
- **app-gui**: JavaFX desktop application

### StoryDOM Format
The central data model is defined by `/story-dom/resources/xsd/story-dom.xsd`. Structure:
```xml
<Book title="..." author="...">
  <Chapter id="..." title="...">
    <Block id="..." wordCount="..." sentimentScore="..." fkGrade="...">
      <Emotion type="anger" score="0.5"/>
      <Body>Text content...</Body>
    </Block>
  </Chapter>
</Book>
```

Key design: Books → Chapters → Blocks (~250 words) → Sentences with metadata at each level.

### NLP Processing Pipeline (annotation-engine)
1. **Block Splitting**: Divides chapters into ~250-word analysis units
2. **Word Counting**: Basic statistics  
3. **Readability**: Flesch-Kincaid grade levels
4. **Sentiment**: Stanford CoreNLP sentiment scoring
5. **Emotions**: EmoLex lexicon (8 emotions: anger, anticipation, disgust, fear, joy, sadness, surprise, trust)
6. **Named Entity Recognition**: Characters and locations using Stanford CoreNLP + OpenNLP fallback

### Database Schema (api module)
Single `books` table stores:
- User metadata (Google OAuth integration)
- Raw input text
- StoryDOM XML (structured)
- Annotated StoryDOM XML (with NLP analysis)
- Processing status and progress tracking

### Key Libraries
- **Stanford CoreNLP 4.4.0**: Primary NLP toolkit
- **Apache OpenNLP 1.9.2**: Backup NER models
- **EmoLex**: Word-emotion association lexicon
- **Spring Boot 2.3.4**: API framework
- **JavaFX**: Desktop GUI
- **JAXB**: XML binding for StoryDOM
- **JFreeChart**: Visualization components

## Java Version Requirements
- Most modules: Java 8+
- API and GUI modules: Java 11+ (required)

## Important Configuration Files
- `/pom.xml`: Root Maven configuration
- `/api/src/main/resources/application.yml`: API configuration with profiles
- `/story-dom/resources/xsd/story-dom.xsd`: Core domain schema
- `/story-dom/resources/global.xjb`: JAXB binding customization

## Extension Points
- **New book formats**: Add importers in book-importer module
- **Additional NLP analysis**: Create new inspectors in annotation-engine
- **Custom visualizations**: Extend viz-tool with new chart types
- **API endpoints**: Add REST controllers in api module
- **Emotion types**: Modify EmoLex configuration for additional emotions