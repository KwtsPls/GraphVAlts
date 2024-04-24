# Dumps2Csv

This project provides a utility tools for transforming data files into .csv files able to be loaded by Noe4j's admin import tool

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

## Compilation

         mvn clean package shade:shade

## Dataset Locations

1. Colours dataset is located inside the /extra directory of the current repository
2. Bio2RDF in https://download.bio2rdf.org/#/current/ (Only download BioModels and BioPortal)
3. DBpedia in http://downloads.dbpedia.org/3.9/en/

NTriple files of the same dataset must be in the same directory.

## Execution

1. Execute the following command
   
            java -jar target/Dumps2Csv-1.0-SNAPSHOT.jar [dir_with_nt]
2. Run the Neo4j admin import tool with the following command

            neo4j-admin.bat import --delimiter=U+0006 --array-delimiter=";" --nodes=[path]/uris.csv --nodes=[path]/values.csv --relationships=[path]/rels0.csv --skip-bad-relationships=true --skip-duplicate-nodes=true --bad-tolerance=200000

If the data is larger than the available system memory, files can be split into directories of the following form:

DataDir<br>
&nbsp;&nbsp;&nbsp;&nbsp;|_ Batch01<br>
&nbsp;&nbsp;&nbsp;&nbsp;|_ Batch02<br>
&nbsp;&nbsp;&nbsp;&nbsp;.<br>
&nbsp;&nbsp;&nbsp;&nbsp;.<br>
&nbsp;&nbsp;&nbsp;&nbsp;|_ BatchXX<br>

Run the command on each Batch directory:

         java -jar target/Dumps2Csv-1.0-SNAPSHOT.jar [dir_with_nt]

And finally execute the command

         java -jar target/Dumps2Csv-1.0-SNAPSHOT.jar standard [dir_with_batches] [num_of_batches]

Run the command for the Neo4j admin import tool again:

      neo4j-admin.bat import --delimiter=U+0006 --array-delimiter=";" --nodes=/target/uris.csv --nodes=/target/values.csv --relationships=/Batch0*/rels0.csv --skip-bad-relationships=true --skip-duplicate-nodes=true --bad-tolerance=200000
