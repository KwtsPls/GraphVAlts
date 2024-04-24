package gr.uoa.di.NTriples;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;

import java.net.URI;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class NTriplesExtractor {
    HashSet<String> classNames;
    EntityLoader entityLoader;
    ValueLoader valueLoader;

    public NTriplesExtractor(){
        classNames = new HashSet<>();
        classNames.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        this.entityLoader = new EntityLoader();
        this.valueLoader = new ValueLoader();
    }

    //Method to extract all labels from a file
    public void transform(String path){
        File root = new File(path);

        try {
            Path dir = Paths.get(path+"/target/");
            //java.nio.file.Files;
            Files.createDirectories(dir);
        } catch (IOException e) {
            System.out.println("Target already exists. Rewriting results");
        }

        int count=0;

        for (File ntfile : Objects.requireNonNull(root.listFiles())) {
            System.out.println(ntfile.getName());
            if(!ntfile.getName().equals("target")) {
                String relPath = path + "/target/rels" + count + ".csv";
                count++;
                char d = 6;
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter(relPath));
                    writer.write(":START_ID" + d + ":END_ID" + d + ":TYPE\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                FileInputStream is = null;
                try {
                    is = new FileInputStream(ntfile.getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                NxParser nxp = new NxParser();
                nxp.parse(is);
                for (Node[] nx : nxp){
                    Tokenizer tokenizer = new Tokenizer(nx[0]+ " " + nx[1] + " " + nx[2] + " .");
                    String s = tokenizer.getSubject();
                    String p = tokenizer.getPredicate();
                    String o = tokenizer.getObject();

                    //Object is label
                    if (this.classNames.contains(p)) {
                        try {
                            entityLoader.add(s,o);
                        }
                        catch (UnsupportedOperationException e){
                            e.printStackTrace();
                            continue;
                        }
                    } else {
                        try {
                            URL objectURI = new URL(o);
                            objectURI.toURI();
                            if(objectURI.toURI().isAbsolute()) {
                                entityLoader.add(s);
                                entityLoader.add(o);
                                //Write the relationship
                                try {
                                    writer.write(s + d + o + d + p + "\n");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            else throw new MalformedURLException();
                        } catch (MalformedURLException | URISyntaxException e) {
                            entityLoader.add(s);
                            String value = o;
                            if (value.length() > 32700) value = value.substring(0, 32699);
                            value = value.replaceAll("\"","'");

                            valueLoader.add(value);
                            //Write the relationship
                            try {
                                writer.write(s + d  + value  + d + p + "\n");
                            } catch (IOException ioe) {
                                throw new RuntimeException(ioe);
                            }
                        }
                    }
                }

                try {
                    writer.close();
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        entityLoader.export(path);
        valueLoader.export(path);
    }

    //Method to get queries from n-triples lsq formats
    public void getLsqQueries(String path,String output){

        BufferedWriter writer = null;
        try {
            FileInputStream is = new FileInputStream(path);
            NxParser nxp = new NxParser();
            nxp.parse(is);

            writer = new BufferedWriter(new FileWriter(output));
            int count=0;
            for (Node[] nx : nxp){
                String query = nx[2].toString().replace("\\n"," ");
                query = query.replace("\\r"," ");
                query = query.replace("\n"," ");
                query = query.replace("\r"," ");
                query = query.substring(1,query.length()-1);
                writer.write(query+"\n");
                count++;
            }

            System.out.println(count);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
