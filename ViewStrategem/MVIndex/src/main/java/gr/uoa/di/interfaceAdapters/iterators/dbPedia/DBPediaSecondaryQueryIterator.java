package gr.uoa.di.interfaceAdapters.iterators.dbPedia;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import gr.uoa.di.interfaceAdapters.Resources;
import gr.uoa.di.interfaceAdapters.workloads.QueryIterator;

public class DBPediaSecondaryQueryIterator implements QueryIterator, AutoCloseable {

    private static String fileWithDBPediaLog = Resources.fileWithDBPediaExtractedQueries;
    private BufferedReader br;
    private String nextString;
    private String line;
    private boolean checked = false;
    private boolean moreQueries = true;
    private boolean lowerCase;

    DBPediaSecondaryQueryIterator() {
        this.lowerCase = true;
        try {
            br = new BufferedReader(new FileReader(fileWithDBPediaLog));
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
                nextString = line;
                if (lowerCase)
                    nextString.toLowerCase();
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
        return "DBPediaCyclesPaths";
    }

}
