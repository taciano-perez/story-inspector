#  ![Story Inspector](documentation/images/logo-small.png) Story Inspector
A tool for analyzing book structure using NLP techniques. See the forest instead of the trees.

Story Inspector consists of the following components:

![Components of Story Inspector](https://raw.githubusercontent.com/taciano-perez/story-inspector/master/documentation/images/architecture_overview.jpg)

- An **ETL Tool** (Extraction, Transform, Load) that takes books in various formats (HTML, ePUB, PDF, etc.) as input and produces a Story Inspector DOM (Document Object Model) file breaking down the book in its structural elements (parts, chapters, and paragraphs).
* An **Annotation Engine** that uses NLP to produce metadata from Story Inspector DOM files, enriching the files with metadata annotations. Examples of metadata include:
    * Word Count
    * Characters
    * Locations
    * Sentiment Score
    * Etc.
* A *Visualization Tool* for authors to interpret the results.
  
  Story Inspector is a work in progress, and is not yet available for use.
