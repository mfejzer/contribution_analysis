# contribution_analysis
This repository contains implementation of experiments from "Open source is a continual bugfixing by a few" paper.
Java 8 and Apache Maven are required to compile the project.

To run experiments:
* Download "MSR 2014 Mining Challenge Dataset" http://ghtorrent.org/msr14.html and import both databases
* Compile the project via "mvn clean package"
* Run ```java -jar ~/contribution_analysis-0.0.1-SNAPSHOT.jar PROJECT_ID PATH_TO_RESULT_DIR``` where PROJECT_ID corresponds to id from project table from https://ghtorrent.org/relational.html 
