In this code I use AndroidAnnotations framework. It makes code more cleaner with no side effect.

Most interesting part is in DocumentActivity - loadDocument and saveDocument. To replace placeholders with user input I use ParagraphRangeUpdater children (DatePicker, EditText).
To read/write document I use Apache POI library - it is most advanced (but abandoned) .doc java library. 

I did not implement image feature due to Apache POI limitation. It's .doc part does not support image insertion. New version (.docx) does support it, but source file is in old doc format.
 
Task was not very complicated, except for lack of POI documentation and image research, which I will continue for some time.
Nevertheless I spent about 22 hrs, mostly trying to get image into doc.