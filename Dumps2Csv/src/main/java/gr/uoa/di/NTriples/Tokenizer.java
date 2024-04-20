package gr.uoa.di.NTriples;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private int type;
    private String subject;
    private String predicate;
    private String object;

    public Tokenizer(String line){
        //Convert string to lowercase
        line = line.toLowerCase();
        //Break the string into (subject, predicate, object)
        String[] split_data = line.split("\\s+",3);
        this.subject = split_data[0].replaceAll("[<>]","");
        this.predicate = split_data[1].replaceAll("[<>]","");
        this.type=1;

        //If object is like '"data"@en' only keep data part
        if(split_data[2].indexOf('\"')!=-1) {
            object = split_data[2];
            object = object.replaceAll("\\\\\"","'");
            Pattern p = Pattern.compile("\"([^\"]*)\"");
            Matcher m = p.matcher(object);
            while (m.find()){
                this.object = m.group(1);
            }

            if(Objects.equals(this.object, "")){
                object = split_data[2];
            }


            this.object = this.object.replaceAll("\"","`");
            if(this.object.length()>32000)
                this.object = this.object.substring(0,32000);
            this.type=2;
        }
        else {
            this.object = split_data[2].replaceAll(" .", "").replaceAll("[<>]", "");
        }

        //Check if there needs to be a relationship or a label added
        if(this.predicate.contains("rdf-syntax-ns#type"))
            this.type=3;
    }

    public String packageTriple(char del){
        if(type==1 || type==2) return subject+del+object+del+predicate+"\n";
        else return subject+del+object+"\n";
    }

    public String getSubject() {
        return subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public String getObject() {
        return object;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

