package gr.uoa.di.NTriples;

import java.io.*;
import java.util.HashSet;
import java.util.Map;

public class ValueLoader {
    HashSet<String> values;

    public ValueLoader(){
        this.values = new HashSet<>();
    }

    public void add(String value){
        this.values.add(value);
    }

    //Method to export the values in csv format into a given directory
    public void export(String path){
        path += "/target/values.csv";
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path, true));
            char d=6;
            writer.write("value:ID"+d+":LABEL\n");

            for(String value:this.values.stream().toList()){
                writer.write(value+d+"Resource;Literal\n");
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
                String value = columns[0];

                this.add(value);

                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
