#  ![Story Inspector](documentation/images/logo-small.png) Story Inspector
A tool for analyzing book structure using NLP techniques. See the forest instead of the trees.

Story Inspector consists of the following components:

![Components of Story Inspector](https://raw.githubusercontent.com/taciano-perez/story-inspector/master/documentation/images/architecture_overview.jpg)

- An **ETL Tool** (Extraction, Transform, Load) that takes books in various formats (HTML, ePUB, PDF, etc.) as input and produces a StoryDOM (Document Object Model) file breaking down the book in its structural elements (parts, chapters, and paragraphs).
* An **Annotation Engine** that uses NLP to produce metadata from StoryDOM files, enriching the files with metadata annotations. Examples of metadata include:
    * Word Count
    * Characters
    * Locations
    * Sentiment Score
    * Emotion Score
    * Etc.
* A **Visualization Tool** for authors to interpret the results.
* A **Web Visualization Tool** is hosted at https://github.com/taciano-perez/story-inspector-web.
  
Note: Story Inspector is a work in progress, and not fully functional yet.

Limitations
- Story Inspector cannot analyze sentiment nor extract named entities (locations, characters) from sentences larges than 250 words.

\* [Logo image designed by macrovector / Freepik](http://www.freepik.com)
