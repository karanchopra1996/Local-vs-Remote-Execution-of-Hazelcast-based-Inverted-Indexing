# Local-vs-Remote-Execution-of-Hazelcast-based-Inverted-Indexing
This project implements two versions of inverted indexing program using Hazelcast’s distributed map.

This project implements two versions of inverted indexing program using Hazelcast’s distributed map
that maintains a database of <key, value> items, each with key = a file name and value = its text data. One
version is InvertedIndexingLocal.java that retrieves each file from the database, counts the occurrences of
a given word, and prints out the file name and the number of occurrences. The other version is
InvertedIndexingRemote.java that dispatches InvertedIndexingEach.class to each remote cluster node
where it counts the occurrences of a given word in only files local to that remote node. The purpose of
this assignment is to understand Hazelcast’s mechanism of remote execution and to measure its execution
performance.


DBCreator.java 
Receives a directory of text files; joins a Hazelcast environment;
creates a IMap<String, String> named “files” where key = a file
name and value = entire file contents; read each text file from the
directory; add this file name and its contents to the IMap as a new
key-value tuple.

RemoteKeyViewer.java 
Joins a Hazelcasst environment; retrieves a set of cluster members;
and dispatches a RemoteKeySearchCallable program to each
member. Upon a response and a completion of each cluster
member’s execution of RemoteKeySearchCallable, it prints out the
execution output in onResponse( ) and onComplete( ) respectively.
The output is the number of files and their names local to each
cluster member.

RemoteKeySearchCallable.java 
Invokes the call( ) method at each cluster members; accesses the
shared IMap named “files”; retrieves the keys, (i.e., file names) of
only tuples <file name, contents> local to that cluster member; and
returns the number of local files and their names.

InvertedIndexingLocal.java 
Receives an inverted-indexing keyword in args[0]; join a Hazelcast
environment; starts a timer for performance measurement; retrieves
the shared IMap named “files”; retrieves an iterator of all key-value
tuples; counts the occurrence of the keyword of each file; stops the
timer; and prints out “File[name] has #occurences”.

InvertedIndexingRemote.java
Receives an inverted-indexing keyword in args[0]; joins a Hazelcasst
environment; starts a timer for performance measurement; retrieves a
set of cluster members; and dispatches a InvertedIndexingEach
program to each member. Upon a completion of all cluster members’
execution of InvertedIndexingEach, it moves each cluster member’s
<filename, occurrences> tuples to the local hash; stops the timer; and
prints out this local hash contents as “File[name] has #occurences”.

InvertedIndexingEach.java 
Invokes the call( ) method at each cluster members; accesses the
shared IMap named “files”; retrieves the keys, (i.e., file names) of
only tuples <file name, contents> local to that cluster member;
counts the occurrence of the keyword of each file; and return a
String that includes “filename1 count1 filename2 count2 ….”.


Explanation------------------->

InvertedIndexingLocal.java: 
The main method checks for a command-line argument and proceeds if provided.
The keyword is stored, and a Hashtable is created to hold document frequencies.
A Hazelcast instance is created, and a start time is recorded. 
The code retrieves a map of files and iterates over the keys.
For each document, the contents are split into words, and the frequency of the keyword is counted. 
If the frequency is non-zero, it is stored in the documentFrequencyMap. 
The end time is recorded, and the document frequencies are printed. In the end, the elapsed time is calculated and displayed.


• InvertedIndexingRemote.java: 
When a command-line argument is present, the main method checks for it and starts working. 
The keyword is stored, and a Hashtable is created to hold document frequencies. 
A Hazelcast instance is created, and a start time is recorded. 
The code retrieves an executor service from the Hazelcast instance and prepares an InvertedIndexingEach task with the keyword. 
A MultiExecutionCallback is defined to handle the responses from each member. 
The code submits the task to all members of the Hazelcast cluster using the executor service and processes the responses. 
For each response, the code splits it into an array of strings and stores the document ID and frequency in the documentFrequencyMap.
Once all responses are processed, the end time is recorded. 
The code then iterates over the documentFrequencyMap to print the file name and its corresponding frequency. Then elapsed time is calculated and displayed.


• InvertedIndexingEach.java:
Defines a class called InvertedIndexingEach, which implements the Callable interface, HazelcastInstanceAware, and Serializable. 
The class represents a task that will be executed by each member of the Hazelcast cluster in a distributed manner.
The call() method is the main entry point of the task. 
It first initializes a Hashtable called documentFrequencyMap to store document IDs and their corresponding frequencies. 
It retrieves a map called "files" from the Hazelcast instance and iterates over the local keys of the map.For each document, 
it retrieves the document contents, splits them into words, and counts the frequency of the keyword provided. 
If the frequency is greater than 0, it adds the document ID and frequency to the documentFrequencyMap. 
After processing all documents, the code constructs a result string by iterating over the keys of the documentFrequencyMap and
appending the document ID and frequency to the result string. Finally, the result string is printed to the console and 
returned as the result of the call() method.





