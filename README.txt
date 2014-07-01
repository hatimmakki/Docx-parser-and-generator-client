In this code I use AndroidAnnotations framework. It makes code more cleaner with no side effect.

Most interesting part is in DocumentActivity - loadDocument and saveDocument. 
To replace placeholders with user input I use SegmentValueUpdater children (DatePicker, EditText).

WAS: >> To read/write document I use Apache POI library - it is most advanced (but abandoned) .doc java library. 
NOW: >> Apache POI can not be run on Android because of huge dependency library (oo-schemas) which breaks app build process. 
I use API to server (written on Spark micro framework) to parse and build result document. So android device used just to collect user's input and fetch/save file to Dropbox.

Server code is here: https://github.com/nord-ua/Docx-parser-and-generator-server
