package gr.uoa.di.TextDump;

import java.io.*;
import java.util.HashSet;

public class TextParser {
    String path;
    String outputPath;
    static String uri = "http://ai.di.uoa.gr/ontology/";
    static String resource = "http://ai.di.uoa.gr/resource/";
    static char delimiter=6;


    public TextParser(String path, String outputPath){
        this.path = path;
        this.outputPath = outputPath;
    }

    public void parse(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));

            BufferedWriter nodes = new BufferedWriter(new FileWriter(outputPath+File.separator+"nodes.csv"));
            nodes.write("uri:ID"+delimiter+":LABEL\n");

            BufferedWriter relationships = new BufferedWriter(new FileWriter(outputPath+File.separator+"rels.csv"));
            relationships.write(":START_ID" + delimiter + ":END_ID" + delimiter + ":TYPE\n");

            HashSet<String> uniqueNodes = new HashSet<>();

            String line = reader.readLine();
            while(line!=null){
                if(!line.startsWith("#") && !line.startsWith("%")){
                    String[] tokens = line.split("\\s+",-1);
                    if(tokens.length==2){
                        uniqueNodes.add(tokens[0]);
                        uniqueNodes.add(tokens[1]);

                        String s = resource + tokens[0];
                        String p = uri + "connects";
                        String o = resource + tokens[1];

                        relationships.write(s + delimiter + o + delimiter + p + "\n");
                    }
                }

                line = reader.readLine();
            }
            relationships.close();

            for(String node:uniqueNodes){
                nodes.write(resource+node+delimiter+"Resource\n");
            }
            nodes.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
