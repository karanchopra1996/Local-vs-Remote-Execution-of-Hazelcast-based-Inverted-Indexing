InvertedIndexingLocal.java
Synopsis: Read all files from a given directory into IMap, find which cluster node contains which <key, value> items, and locally print out all file names that contain a given keyword.
Textbook: p11 and p71
Compile:  ./compileAll.sh
Usage:   
cssmpi1h: ./console.sh
cssmpi2h: ./console.sh
cssmpi3h: ./console.sh
cssmpi4h: ./run.sh DBCreator ~css534/prog3/rfc
cssmpi5h: ./run.sh LocalKeyViewer
cssmpi5h: ./run.sh InvertedIndexingLocal TCP

InvertedIndexingRmote.java and InvertedIndexingEach.java
Synopsis: Read all files from a given directory into IMap, let a callable object run at each remote machine, examin <key, value> items local to that remote machine, and return all file names that contain a given keyword.
Textbook: p11 and p71
Compile:  ./compileAll.sh
Usage:   
cssmpi1h: ./console.sh
cssmpi2h: ./console.sh
cssmpi3h: ./console.sh
cssmpi4h: ./run.sh DBCreator ~css534/prog3/rfc
cssmpi5h: ./run.sh LocalKeyViewer
cssmpi5h: ./run.sh InvertedIndexingLocal TCP



