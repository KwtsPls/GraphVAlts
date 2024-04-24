package gr.uoa.di.NTriples;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EntityLoader {
    HashMap<String, HashSet<String>> uris;

    public EntityLoader(){
        this.uris = new HashMap<>();
    }

    public void add(String uri){
        if(!uris.containsKey(uri)) {
            uris.put(uri, new HashSet<>());
        }
    }

    public void add(String uri,String label){
        HashSet<String> labels = this.uris.get(uri);
        if(labels==null)
            labels = new HashSet<>();
        labels.add(label);
        this.uris.put(uri,labels);
    }

    //Method to export the uris in csv format into a given directory
    public void export(String path){

        try {
            Path dir = Paths.get(path+"/target/");
            //java.nio.file.Files;
            Files.createDirectories(dir);
        } catch (IOException e) {
            System.out.println("Target already exists. Rewriting results");
        }

        path += "/target/uris.csv";
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path, true));
            char d=6;
            writer.write("uri:ID"+d+":LABEL\n");

            for (Map.Entry<String, HashSet<String>> set :
                    this.uris.entrySet()) {
                String uri = set.getKey();
                HashSet<String> labels = set.getValue();

                StringBuilder stringBuilder= null;
                for(String label: labels.stream().toList()){
                    if(stringBuilder==null) stringBuilder = new StringBuilder(label);
                    else stringBuilder.append(";").append(label);
                }

                String label = stringBuilder != null ? stringBuilder +";Resource\n" : "Resource\n";
                writer.write(uri+d+label);
            }

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void mergeCSV(String csvFile){
        BufferedReader reader;
        char del=6;
        String arr_del=";";

        try {
            reader = new BufferedReader(new FileReader(csvFile));
            String line = reader.readLine();
            //skip first line
            line = reader.readLine();

            while (line != null) {
                String[] columns = line.split(String.valueOf(del));
                String uri = columns[0];

                if(columns.length == 2) {
                    String[] labels = columns[1].split(arr_del);
                    for (String label : labels) {
                        this.add(uri, label);
                    }
                }
                else
                    this.add(uri);

                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
