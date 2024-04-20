package gr.uoa.di.entities.viewSelection._steps;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.interfaceAdapters.Resources;
import gr.uoa.di.interfaceAdapters.controllers.randomAccessDiskMap.patterns.RNDAccessPatterns;
import gr.uoa.di.interfaceAdapters.workloads.JenaGraphQueryIterator;
import gr.uoa.di.translators.cypher.CypherQuery;
import gr.uoa.di.translators.cypher.CypherTranslator;
import gr.uoa.di.usecases.constants.experiments.ConstantForExperiments;
import gr.uoa.di.usecases.constants.materialization.ConstantsSharable;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Step_CreateCyclesAndPaths {
    public static <C extends ConstantsSharable & ConstantForExperiments> void materializePatterns(C constants,int pathNum,double edgePossibility) throws IOException {

        File f = new File(constants.getFileForExtractedQueries());
        if(!f.exists()) {

            HashMap<Integer, String> map = new HashMap<>();
            map.put(0, "http://dbpedia.org/ontology/wikipageredirects");
            map.put(1, "http://dbpedia.org/property/wikipageusestemplate");
            map.put(2, "http://xmlns.com/foaf/0.1/page");
            map.put(3, "http://www.w3.org/2002/07/owl#sameas");
            map.put(4, "http://xmlns.com/foaf/0.1/name");
            map.put(5, "http://www.w3.org/2000/01/rdf-schema#label");
            map.put(6, "http://dbpedia.org/ontology/wikipagewikilink");
            map.put(7, "http://www.w3.org/2004/02/skos/core#broader");
            map.put(8, "http://www.w3.org/2004/02/skos/core#subject");
            map.put(9, "http://dbpedia.org/property/officialname");
            map.put(10, "http://www.w3.org/2000/01/rdf-schema#subclassof");
            map.put(11, "http://ai.di.uoa.gr/ontology/");
            map.put(12, "http://dummy.com/");

            //File to write the generated SPARQL queries
            BufferedWriter log = new BufferedWriter(new FileWriter(constants.getFileForExtractedQueries()));

            Step_CreateCyclesAndPaths step = new Step_CreateCyclesAndPaths();
            step.generatePaths(log, pathNum, constants.getFileWithPaths());
            step.generateCycles(log, edgePossibility, map, constants.getFileWithCycles());

            log.close();
        }
    }

    private void generatePaths(BufferedWriter log,int pathNum, String path){
        //Load the files with the paths
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));


            String line = reader.readLine();
            while(line!=null){
                String triple = parseTriple(line);
                for(int i=0;i<pathNum;i++)
                    log.write(triple);
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateCycles(BufferedWriter log, Double edgePossibility, HashMap<Integer,String> map, String path){
        //Load the files with the paths
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));

            String line = reader.readLine();
            while(line!=null){
                String triple = parseTriple(line);
                for(int i=0;i<4000;i++) {
                    String cycleTriple = triple;
                    Random r = new Random();
                    double pe = r.nextDouble();
                    //Double edges
                    if(edgePossibility > pe){
                            int index = Step_CreateCyclesAndPaths.rand(0,11);
                            String predicate = map.get(index);
                            predicate = Step_CreateCyclesAndPaths.toPrefixForm(predicate);
                            cycleTriple = cycleTriple.replace(" }\n"," ?b0 "+predicate+" ?x0 . }\n");
                    }
                    if (edgePossibility / 2 > pe) {
                            int index = Step_CreateCyclesAndPaths.rand(0,11);
                            String predicate = map.get(index);
                        predicate = Step_CreateCyclesAndPaths.toPrefixForm(predicate);
                        cycleTriple = cycleTriple.replace(" }\n"," ?x2 "+predicate+" ?b1 . }\n");
                    }

                    log.write(cycleTriple);
                }
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String parseTriple(String line){
        Pattern p = Pattern.compile("\\((\\(*(?:[^)(]*|\\([^)]*\\))*\\)*)\\)");
        Matcher matcher = p.matcher(line);
        String prefix = "PREFIX dbo: <http://dbpedia.org/ontology/> PREFIX dbp: <http://dbpedia.org/property/> PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX foaf: <http://xmlns.com/foaf/0.1/> PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX purl: <http://purl.org/dc/terms/> PREFIX obov: <http://bio2rdf.org/obo_vocabulary> " +
                "PREFIX radlexowl: <http://www.owl-ontologies.com/radlex.owl> PREFIX purlelem: <http://purl.org/dc/elements/1.1/> PREFIX ncicb: <http://ncicb.nci.nih.gov/xml/owl/evs/ctcae.owl#> " +
                "PREFIX uoa: <http://ai.di.uoa.gr/ontology/> PREFIX dummy: <http://dummy.com/> ";

        StringBuilder builder = new StringBuilder(prefix + "SELECT ?x0 {");

        //Iterate through every triplet
        while( matcher.find() ) {
            //Separate the (s,p,o) triples into tokens
            List<String> parsedTriple = Arrays.asList(matcher.group(1).split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
            String subject = parseEntity(parsedTriple.get(0));
            String predicate = parsedTriple.get(1);
            if(predicate.endsWith("*")){
                predicate = predicate.substring(0,predicate.length()-1);
                predicate = Step_CreateCyclesAndPaths.toPrefixForm(predicate)+"*";
            }
            else {
                predicate = Step_CreateCyclesAndPaths.toPrefixForm(predicate);
            }
            String object = parseEntity(parsedTriple.get(2));

            String triple = subject + " " + predicate + " " + object + " . ";
            builder.append(" ").append(triple);
        }

        builder.append(" }\n");
        return builder.toString();
    }

    private String parseEntity(String entity){
        if(!entity.startsWith("?")){
            //check if object is value or uri
            try {
                URI u = new URI(entity);

                if (!u.isAbsolute()){
                    entity="\""+entity+"\"";
                }
                entity = "<"+entity+">";

            } catch (URISyntaxException e) {
                entity="\""+entity+"\"";
            }
        }
        return entity;
    }

    private static int rand(int min, int max) {
        Random r = new Random(System.currentTimeMillis());
        return r.nextInt((max - min) + 1) + min;
    }

    private static String toPrefixForm(String predicate){
        if(predicate.startsWith("http://dbpedia.org/ontology/"))
            return predicate.replace("http://dbpedia.org/ontology/","dbo:");
        else if(predicate.startsWith("http://dbpedia.org/property/"))
            return predicate.replace("http://dbpedia.org/property/","dbp:");
        else if(predicate.startsWith("http://www.w3.org/2004/02/skos/core#"))
            return predicate.replace("http://www.w3.org/2004/02/skos/core#","skos:");
        else if(predicate.startsWith("http://www.w3.org/2000/01/rdf-schema#"))
            return predicate.replace("http://www.w3.org/2000/01/rdf-schema#","rdfs:");
        else if(predicate.startsWith("http://xmlns.com/foaf/0.1/"))
            return predicate.replace("http://xmlns.com/foaf/0.1/","foaf:");
        else if(predicate.startsWith("http://www.w3.org/1999/02/22-rdf-syntax-ns#"))
            return predicate.replace("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:");
        else if(predicate.startsWith("http://purl.org/dc/terms/"))
            return predicate.replace("http://purl.org/dc/terms/", "purl:");
        else if(predicate.startsWith("http://bio2rdf.org/obo_vocabulary"))
            return predicate.replace("http://bio2rdf.org/obo_vocabulary", "obov:");
        else if(predicate.startsWith("http://www.owl-ontologies.com/radlex.owl"))
            return predicate.replace("http://www.owl-ontologies.com/radlex.owl","radlexowl:");
        else if(predicate.startsWith("http://purl.org/dc/elements/1.1/"))
            return predicate.replace("http://purl.org/dc/elements/1.1/","purlelem:");
        else if(predicate.startsWith("http://ncicb.nci.nih.gov/xml/owl/evs/ctcae.owl#"))
            return predicate.replace("http://ncicb.nci.nih.gov/xml/owl/evs/ctcae.owl#","ncicb:");
        else if(predicate.startsWith("http://ai.di.uoa.gr/ontology/"))
            return predicate.replace("http://ai.di.uoa.gr/ontology/","uoa:");
        else if(predicate.startsWith("http://dummy.com/"))
            return predicate.replace("http://dummy.com/","dummy:");
        else
            return predicate.replace("http://www.w3.org/2002/07/owl#","owl:");
    }
}
