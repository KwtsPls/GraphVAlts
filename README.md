# View Materialization Strategies in Property-Graph Databases

## Introduction

Property-Graph Database Management Systems (PGdmss) have
gained significant popularity due to their inherent capability to
represent information from diverse domains in the form of property
graphs. Significant challenges arise when handling complex queries
within these systems, necessitating the development of techniques
to expedite query execution.
In this work, we address the challenge of view materialization
to expedite query answering in property-graph databases. Based
on the structural elements of a native PGdms–that is, nodes, edges,
labels, and properties–we analyze various view materialization
strategies and provide query rewriting techniques to efficiently
handle subsequent queries by leveraging these precomputed views.
Selecting the appropriate view alternative depends on the characteristics of the queries that need to be materialized as well as the
characteristics of the underlying dataset. We show that adopting a
flexible view model yields significant improvements in performance
and storage efficiency, while providing a roadmap for selecting the
appropriate view alternative depending on the characteristics of
the query patterns we need to expedite

## Prerequisites

1. Install maven 3.6.3:

        wget https://mirrors.estointernet.in/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz \
        && tar -xvf apache-maven-3.6.3-bin.tar.gz \
        && mv apache-maven-3.6.3 /opt/
        
        M2_HOME='/opt/apache-maven-3.6.3'
        PATH="$M2_HOME/bin:$PATH"
        export PATH

2. Install Java 17:

        sudo apt-get install openjdk-17-jdk openjdk-17-jre

3. Unzip the file resources.zip located in ViewStrategem/GSpan/resources/, which contains the query workloads

4. In the file ViewStrategem/MVIndex/src/main/java/gr/uoa/di/interfaceAdapters/Resources.java, add the required fields to run the experiments:
   1. uriNeo4jMain: url of the main neo4j database
   2. uriNeo4jView: url of the neo4j database used for subgraph views
   3. fileWithCsvViews: path to the main neo4j database's import directory ( Directory where .csv files are exported)
   4. usernameNeo4jxMain: username for the neo4j database for x dataset
   5. passwordNeo4jxMain:  password for the neo4j database for x dataset
   6. usernameNeo4jxView: username for the neo4j view database for x dataset
   7. passwordNeo4jxView:  password for the neo4j view database for x dataset

## Compilation

Enter the ViewStrategem/ directory and execute:

         mvn clean package shade:shade

## Execution

1. Follow the instructions in the Dumps2Csv directory to create and load the datasets
2. Execute the following command:

        java -jar GSpan/target/GSpan-1.0.0.jar views [dbpedia/bio2rdf/colours]