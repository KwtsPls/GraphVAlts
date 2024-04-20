package gr.uoa.di.interfaceAdapters.iterators.dbPedia;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import gr.uoa.di.interfaceAdapters.Resources;
import gr.uoa.di.interfaceAdapters.workloads.QueryIterator;

class DBPediaQueryIterator implements QueryIterator, AutoCloseable {

	private static String fileWithDBPediaLog = Resources.fileWithDBPediaLog;
	private BufferedReader br;
	private String nextString;
	private String line;
	private boolean checked = false;
	private boolean moreQueries = true;
	private boolean lowerCase;

	DBPediaQueryIterator(boolean lowerCase) {
		this.lowerCase = lowerCase;
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
				String queryString = line.split(" ")[6].replace("\"", "").split("query=")[1].split("&")[0];
				nextString = URLDecoder.decode(queryString, "UTF-8").toLowerCase();
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
		return "DBPedia";
	}

}
