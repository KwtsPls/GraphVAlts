package gr.uoa.di;

import gr.uoa.di.NTriples.EntityLoader;
import gr.uoa.di.NTriples.NTriplesExtractor;
import gr.uoa.di.NTriples.Tokenizer;
import gr.uoa.di.NTriples.ValueLoader;
import gr.uoa.di.TextDump.TextParser;

import java.io.*;

public class WorkflowManager {
    public static void main(String[] args) {
        if(args.length == 1) {
            NTriplesExtractor extractor = new NTriplesExtractor();
            extractor.transform(args[0]);
        }
        else if(args.length == 3 && args[0].equals("queries")){
            NTriplesExtractor extractor = new NTriplesExtractor();
            extractor.getLsqQueries(args[1],args[2]);
        }
        else if(args.length == 2 && args[0].equals("standard")){
            int batches = Integer.parseInt(args[0]);
            String path = args[1];

            EntityLoader entityLoader = new EntityLoader();
            ValueLoader valueLoader = new ValueLoader();
            for(int i=1;i<=batches;i++){
                String num = i<10?"0"+i:String.valueOf(i);
                String batchPath = path+"/Batch"+num+"/target";

                entityLoader.mergeCSV(batchPath+"/uris.csv");
                valueLoader.mergeCSV(batchPath+"/values.csv");
            }

            entityLoader.export(path);
            valueLoader.export(path);
        }
        else if(args.length == 2 && args[0].equals("alt")){
            char d=6;
            BufferedWriter writer = null;
            try {
                //Head of the csv file
                String relPath = args[1]+"/all_rels.csv";
                writer = new BufferedWriter(new FileWriter(relPath));
                writer.write(":START_ID" + d + ":END_ID" + d + ":TYPE\n");

                File dir = new File(args[1]);
                File[] directoryListing = dir.listFiles();
                assert directoryListing != null;
                for (File child : directoryListing) {
                    if(child.getName().equals("all_rels.csv"))
                        continue;

                    if(!child.getName().equals("target") && !child.getName().equals("all_rels.csv")) {
                        System.out.println("Processing: " + child.getName());
                        try (BufferedReader br = new BufferedReader(new FileReader(child))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                if (line.charAt(0) != '#') {
                                    Tokenizer tokenizer = new Tokenizer(line);
                                    if (tokenizer.getType() != 3) {
                                        String rel = tokenizer.packageTriple(d);
                                        writer.write(rel);
                                    }
                                }
                            }
                        }
                    }
                }
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(args.length==3 && args[0].equals("text")){
            TextParser textParser = new TextParser(args[1],args[2]);
            textParser.parse();
        }
    }
}