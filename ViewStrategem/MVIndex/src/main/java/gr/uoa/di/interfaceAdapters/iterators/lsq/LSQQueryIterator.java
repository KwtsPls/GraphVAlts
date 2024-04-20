package gr.uoa.di.interfaceAdapters.iterators.lsq;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import gr.uoa.di.interfaceAdapters.Resources;
import gr.uoa.di.interfaceAdapters.workloads.QueryIterator;

public class LSQQueryIterator implements QueryIterator, AutoCloseable {

    private static String fileWithLsqLog = Resources.fileWithLsqLog;
    private BufferedReader br;
    private String nextString;
    private String line;
    private boolean checked = false;
    private boolean moreQueries = true;
    private boolean lowerCase;

    LSQQueryIterator(boolean lowerCase) {
        this.lowerCase = lowerCase;
        try {
            br = new BufferedReader(new FileReader(fileWithLsqLog));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasNext() {
        checked = true;
        try {
            while (moreQueries) {
                line = br.readLine();
                if (line == null) {
                    br.close();
                    moreQueries = false;
                    return false;
                }
                String queryString = line;
                nextString = queryString.replaceAll("\\\\","").replaceAll("\"\"","\"")
                                .replaceAll("\" \"","\"").replaceAll("isLiteral\\(\\?o\\)","")
                                .replaceAll(" FILTER \\( ! {2}\\) {3}}","}")
                                .replaceAll("FILTER {4}}","}")
                                .replaceAll("FILTER\\s+}","}")
                                .replaceAll("langMatches\\(lang\\(\\?o\\), \"\\)","langMatches\\(lang\\(\\?o\\), \"en\"\\)")
                                .replaceAll("\\(\\s+!\\s+\\) &&","")
                                .replaceAll("FILTER\\s+\\(\\s+!\\s+\\)","")
                                .replaceAll("\\?P\\s+\"@en","?P \"temp\"@en");
                //if (lowerCase)
                nextString = nextString.toLowerCase();
                return true;
            }
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return hasNext();
        } catch (IOException e) {
            e.printStackTrace();
            return hasNext();
        }
    }

    @Override
    public String next() {
        if (!checked)
            hasNext();
        checked = false;
        return nextString;
    }

    @Override
    public void close() throws IOException {
        br.close();
    }

    @Override
    public String getName() {
        return "LSQ";
    }

}
