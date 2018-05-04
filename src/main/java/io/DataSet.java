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
import java.util.BitSet;
import java.util.LinkedList;

import Pattern.Pattern;

public class DataSet {
	char[][] data;
	char[][] dataToAccess;
	int[] cardinalities;
	int[] selectedAttrIds;

	public DataSet(String fileName, int[] cardinalities,
			int[] selectedAttrIds) {
		List<char[]> listOfRecords = new ArrayList<char[]>();
		this.cardinalities = cardinalities;
		this.selectedAttrIds = selectedAttrIds;

		int dimensions = selectedAttrIds.length;
		try {
			Reader reader = Files.newBufferedReader(Paths.get(fileName));
			CSVParser csvParser = new CSVParser(reader,
					CSVFormat.DEFAULT.withFirstRecordAsHeader()); // parse
			// the
			// csv
			// file
			// with
			// header

			// Read CSV file row by row
			for (CSVRecord csvRecord : csvParser) {
				char[] row = new char[dimensions];
				int i = 0;
				for (int attrId : selectedAttrIds) {
					row[i++] = csvRecord.get(attrId).charAt(0);
				}
				listOfRecords.add(row);
			}

			csvParser.close();

			// Covert list to array
			data = new char[dimensions][listOfRecords.size()];
			dataToAccess = new char[listOfRecords.size()][dimensions];

			for (int n = 0; n < listOfRecords.size(); n++) {
				for (int d = 0; d < dimensions; d++) {
					data[d][n] = listOfRecords.get(n)[d];
					dataToAccess[n][d] = listOfRecords.get(n)[d];
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get row of data by row id
	 * @param rowId
	 * @return
	 */
	public char[] getRow(int rowId) {
		return dataToAccess[rowId];
	}

	/**
	 * Get number of data records
	 * 
	 * @return
	 */
	public int getNumRecords() {
		return data[0].length;
	}

	/**
	 * Get number of dimensions
	 * 
	 * @return
	 */
	public int getDimension() {
		return data.length;
	}

	/**
	 * Get the actual data
	 * 
	 * @return
	 */
	public char[][] getData() {
		return data;
	}

	/**
	 * Given a pattern p, check how many records are covered.
	 * 
	 * @param p
	 * @param numRecords
	 * @return
	 */
	public int checkCoverage(Pattern p) {
		int numRecords = getNumRecords();
		BitSet coverageBitVector = new BitSet(numRecords);
		coverageBitVector.set(0, numRecords);

		for (int i = 0; i < getDimension(); i++) {
			BitSet coverageBitVectorPerDimension = new BitSet(numRecords);
			for (int n = 0; n < numRecords; n++) {
				if (p.data[i] == 'x' || p.data[i] == data[i][n]) {
					coverageBitVectorPerDimension.set(n);
				}
			}
			coverageBitVector.and(coverageBitVectorPerDimension);
		}
		return coverageBitVector.cardinality();
	}

	/**
	 * Create an array of integers from 0 to d - 1 for column number #columnId
	 * 
	 * @param columnId
	 * @return
	 */
	public char[] getValueRange(int columnId) {
		char[] range = new char[this.cardinalities[columnId]];

		for (int i = 0; i < this.cardinalities[columnId]; i++)
			range[i] = (char) (i + 48);

		return range;
	}
	
	/**
	 * Enumerate all possible values based on different cardinalities in each column
	 * @return
	 */
	public List<Pattern> enumAllValues() {
		List<char[]> valueList = new LinkedList<char[]>();
		for (int col = 0; col < getDimension(); col++) {
			List<char[]> tempValueList = new LinkedList<char[]>();
			for (char valueForCol : getValueRange(col)) {
				if (valueList.isEmpty()) {
					tempValueList.add(new char[]{valueForCol});
				}
				else {
					for (char[] originalValue : valueList) {
						char[] newValue = new char[originalValue.length + 1];
						
						System.arraycopy(originalValue, 0, newValue, 0, originalValue.length);
						newValue[newValue.length - 1] = valueForCol;
						tempValueList.add(newValue);
					}
				}
			}
			
			valueList = tempValueList;
		}
		
		List<Pattern> patternList = new LinkedList<Pattern>();
		for (char[] value : valueList) {
			patternList.add(new Pattern(value));
		}
		
		return patternList;
	}

	@Override
	public String toString() {
		String msg = "";
		for (int i = 0; i < getNumRecords(); i++) {
			for (int j = 0; j < getDimension(); j++) {
				msg += data[j][i] + ",";
			}

			msg += "\n";
		}
		return msg;
	}
}
