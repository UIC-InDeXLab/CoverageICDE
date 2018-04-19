package io;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import Pattern.Pattern;

public class DataSet {
    int[][] data;

    public DataSet(String fileName) {
	List<int[]> listOfRecords = new ArrayList<int[]>();
	int dimensions = -1;
	try {
	    Reader reader = Files.newBufferedReader(Paths.get(fileName));
	    CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader()); // parse
												      // the
												      // csv
												      // file
												      // with
												      // header

	    // Read CSV file row by row
	    for (CSVRecord csvRecord : csvParser) {
		dimensions = dimensions < 0 ? csvRecord.size() : dimensions;
		int[] row = new int[dimensions];

		for (int i = 0; i < dimensions; i++) {
		    row[i] = csvRecord.get(i).charAt(0);
		}
		listOfRecords.add(row);
	    }

	    csvParser.close();

	    // Covert list to array
	    data = new int[listOfRecords.size()][dimensions];
	    data = listOfRecords.toArray(data);

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public int checkCoverage(Pattern p) {

	return 0;
    }

}
