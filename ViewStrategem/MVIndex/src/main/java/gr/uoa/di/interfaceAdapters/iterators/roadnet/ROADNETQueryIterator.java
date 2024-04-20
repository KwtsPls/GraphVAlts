package gr.uoa.di.interfaceAdapters.iterators.roadnet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import gr.uoa.di.interfaceAdapters.Resources;
import gr.uoa.di.interfaceAdapters.workloads.QueryIterator;


public class ROADNETQueryIterator implements QueryIterator, AutoCloseable {

    private static String fileWithRoadnetLog = Resources.fileWithRoadnetLog;
    private BufferedReader br;
    private String nextString;
    private String line;
    private boolean checked = false;
    private boolean moreQueries = true;
    private boolean lowerCase;

    ROADNETQueryIterator(boolean lowerCase) {
        this.lowerCase = lowerCase;
        try {
            br = new BufferedReader(new FileReader(fileWithRoadnetLog));
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
                nextString = queryString.toLowerCase();
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
        return "ROADNET";
    }

}
