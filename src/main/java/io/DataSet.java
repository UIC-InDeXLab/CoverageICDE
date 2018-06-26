package io;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import pattern.Pattern;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class DataSet {
	char[][] data;
	BitSet[] dataBitVec;

	int[] occurences;
	int numOfRecords;
	int dimensions;

	char[][] dataToAccess;
	public int[] cardinalities;
	int[] selectedAttrIds;
	public List<double[]> coveragePercentageOfEachValueInEachAttr;

	public DataSet(String fileName, int[] cardinalities, int[] selectedAttrIds,
			int size) {

		Map<String, Integer> recordCount = new HashMap<String, Integer>();
		this.cardinalities = cardinalities;
		this.selectedAttrIds = selectedAttrIds;

		this.dimensions = selectedAttrIds.length;

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
			int count = 0;
			for (CSVRecord csvRecord : csvParser) {
				if (count++ >= size)
					break;

				char[] row = new char[dimensions];
				int i = 0;
				for (int attrId : selectedAttrIds) {
					row[i++] = csvRecord.get(attrId).charAt(0);
				}

				if (recordCount.containsKey(String.valueOf(row)))
					recordCount.put(String.valueOf(row),
							recordCount.get(String.valueOf(row)) + 1);
				else
					recordCount.put(String.valueOf(row), 1);
			}

			csvParser.close();
			List<String> uniquePatternList = new ArrayList<String>(
					recordCount.keySet());

			// Covert list to array
			data = new char[dimensions][uniquePatternList.size()];
			dataToAccess = new char[uniquePatternList.size()][dimensions];
			occurences = new int[uniquePatternList.size()];

			for (int n = 0; n < uniquePatternList.size(); n++) {
				for (int d = 0; d < dimensions; d++) {
					data[d][n] = uniquePatternList.get(n).charAt(d);
					dataToAccess[n][d] = uniquePatternList.get(n).charAt(d);
					occurences[n] = recordCount.get(uniquePatternList.get(n));
				}
			}

			dataBitVec = new BitSet[IntStream.of(this.cardinalities).sum()];
			for (int i = 0; i < dataBitVec.length; i++) {
				dataBitVec[i] = new BitSet(uniquePatternList.size());
			}
			for (int n = 0; n < uniquePatternList.size(); n++) {
				for (int d = 0; d < dimensions; d++) {
					int rowIdx = checkRowIdxInDataBitVec(d,
							uniquePatternList.get(n).charAt(d));
					dataBitVec[rowIdx].set(n);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// for (int i = 0; i < dataBitVec.length; i++) {
		// System.out.println(dataBitVec[i]);
		// }

		numOfRecords = IntStream.of(occurences).sum();

		updateCoveragePercentage();
	}

	private int checkRowIdxInDataBitVec(int dimensionId, char c) {
		return c - 48 + sumOfArray(this.cardinalities, dimensionId);
	}

	/**
	 * Update the coverage percentage
	 */
	public void updateCoveragePercentage() {
		Pattern root = Pattern.getRootPattern(getDimension());
		this.coveragePercentageOfEachValueInEachAttr = new ArrayList<double[]>();
		for (int i = 0; i < getDimension(); i++) {
			double[] covereagePercentageArrayForAttr = new double[cardinalities[i]];

			int j = 0;
			for (char val : getValueRange(i)) {
				Pattern patternCheckingCovereageFoVal = new Pattern(root.data,
						i, val);
				covereagePercentageArrayForAttr[j++] = checkCoveragePercentage(
						patternCheckingCovereageFoVal);
			}
			coveragePercentageOfEachValueInEachAttr
					.add(covereagePercentageArrayForAttr);
		}
	}

	/**
	 * Get row of data by row id
	 * 
	 * @param rowId
	 * @return
	 */
	public Map<Pattern, Integer> getPatternAndOccurences() {
		Map<Pattern, Integer> patternCount = new HashMap<Pattern, Integer>(
				dataToAccess.length);
		for (int i = 0; i < dataToAccess.length; i++) {
			patternCount.put(new Pattern(dataToAccess[i]), occurences[i]);
		}
		return patternCount;
	}

	/**
	 * Sum of the int array
	 * 
	 * @param intArray
	 * @return
	 */
	public static int sumOfArray(int[] intArray, int beforeEndIdx) {
		int sum = 0;
		for (int i = 0; i < intArray.length && i < beforeEndIdx; i++) {
			sum += intArray[i];
		}
		return sum;
	}

	/**
	 * Get number of data records
	 * 
	 * @return
	 */
	public int getNumRecords() {
		return this.numOfRecords;
	}

	/**
	 * Get number of dimensions
	 * 
	 * @return
	 */
	public int getDimension() {
		return this.dimensions;
	}

	private BitSet createBitVec(int numOfUniquePatterns, Pattern curPattern,
			int dimensionId) {
		BitSet coverageBitVectorPerDimension = new BitSet(numOfUniquePatterns);
		for (int n = 0; n < numOfUniquePatterns; n++) {
			if (curPattern.data[dimensionId] == 'x'
					|| curPattern.data[dimensionId] == data[dimensionId][n]) {
				coverageBitVectorPerDimension.set(n);
			}
		}
		return coverageBitVectorPerDimension;
	}

	/**
	 * Given a pattern p, check how many records are covered.
	 * 
	 * @param p
	 * @param numRecords
	 * @return
	 */
	public int checkCoverage(Pattern p) {
		int numUniquePatterns = dataToAccess.length;
		BitSet coverageBitVector = new BitSet(numUniquePatterns);
		coverageBitVector.set(0, numUniquePatterns);

		// for (int i = 0; i < getDimension(); i++) {
		// BitSet coverageBitVectorPerDimension =
		// createBitVec(numUniquePatterns, p, i);
		// coverageBitVector.and(coverageBitVectorPerDimension);
		// }

		for (int i = 0; i < getDimension(); i++) {
			if (p.data[i] != 'x') {
				coverageBitVector
						.and(dataBitVec[checkRowIdxInDataBitVec(i, p.data[i])]);
			}
		}

		int coverage = 0;
		for (int i = coverageBitVector.nextSetBit(
				0); i != -1; i = coverageBitVector.nextSetBit(i + 1)) {
			coverage += occurences[i];
		}
		return coverage;
	}

	/**
	 * Check coverage percentage for pattern p
	 * 
	 * @param p
	 * @return
	 */
	public double checkCoveragePercentage(Pattern p) {

		return (double) checkCoverage(p) / getNumRecords();
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
	 * Get children next level
	 * 
	 * @param parentPattern
	 * @return
	 */
	public Set<Pattern> getChildrenRule1(Pattern parentPattern) {
		Set<Pattern> childPatterns = new HashSet<Pattern>();

		// Find the right most deterministic cell
		int rightMostDeterministicIdx = parentPattern
				.findRightMostDeterministicIndex();

		// Sequentially create new patterns by replacing each
		// position with all possible values in that position
		for (int i = rightMostDeterministicIdx + 1; i < parentPattern
				.getDimension(); i++) {
			for (char valueToReplace : getValueRange(i)) {
				Pattern childPattern = new Pattern(parentPattern.data, i,
						valueToReplace, coveragePercentageOfEachValueInEachAttr,
						parentPattern.covereagePercentage);
				childPattern.parentPattern = parentPattern;
				childPattern.parentDominatesMups = parentPattern.selfDominatesMups;
				childPattern.parentVisitId = parentPattern.visitId;
				childPatterns.add(childPattern);
			}
		}

		return childPatterns;
	}

	/**
	 * We generate all children at the next level for this pattern.
	 * 
	 * @param parentPattern
	 * @return
	 */
	public Set<Pattern> getAllChildren(Pattern parentPattern) {
		Set<Pattern> childPatterns = new HashSet<Pattern>();

		// Sequentially create new patterns by replacing each
		// position with all possible values in that position
		for (int i = 0; i < parentPattern.getDimension(); i++) {
			if (parentPattern.data[i] == 'x')
				for (char valueToReplace : getValueRange(i)) {
					Pattern childPattern = new Pattern(parentPattern.data, i,
							valueToReplace,
							coveragePercentageOfEachValueInEachAttr,
							parentPattern.covereagePercentage);
					childPattern.parentPattern = parentPattern;
					childPattern.parentDominatesMups = parentPattern.selfDominatesMups;
					childPattern.parentVisitId = parentPattern.visitId;
					childPatterns.add(childPattern);
				}
		}

		return childPatterns;
	}

	public Set<Pattern> getPeakPatterns(Pattern peakPattern,
			Pattern patternToCheck) {
		Set<Pattern> peakPatterns = new HashSet<Pattern>();

		for (int i = 0; i < patternToCheck.getDimension(); i++) {
			if (peakPattern.getValue(i) != patternToCheck.getValue(i)) {
				for (char valueToReplace : getValueRange(i)) {
					if (valueToReplace != patternToCheck.getValue(i)) {
						Pattern childPattern = new Pattern(peakPattern.data, i,
								valueToReplace,
								coveragePercentageOfEachValueInEachAttr,
								peakPattern.covereagePercentage);
						childPattern.parentPattern = peakPattern;
						childPattern.parentDominatesMups = peakPattern.selfDominatesMups;
						childPattern.parentVisitId = peakPattern.visitId;
						peakPatterns.add(childPattern);
					}
				}
			}
			else if (peakPattern.getValue(i) == 'x'){
				for (char valueToReplace : getValueRange(i)) {
					if (valueToReplace != patternToCheck.getValue(i)) {
						Pattern childPattern = new Pattern(peakPattern.data, i,
								valueToReplace,
								coveragePercentageOfEachValueInEachAttr,
								peakPattern.covereagePercentage);
						childPattern.parentPattern = peakPattern;
						childPattern.parentDominatesMups = peakPattern.selfDominatesMups;
						childPattern.parentVisitId = peakPattern.visitId;
						peakPatterns.add(childPattern);
					}
				}
			}
		}

		return peakPatterns;
	}

	/**
	 * Enumerate all possible values based on different cardinalities in each
	 * column
	 * 
	 * @return
	 */
	public List<Pattern> enumAllValues() {
		List<char[]> valueList = new LinkedList<char[]>();
		for (int col = 0; col < getDimension(); col++) {
			List<char[]> tempValueList = new LinkedList<char[]>();
			for (char valueForCol : getValueRange(col)) {
				if (valueList.isEmpty()) {
					tempValueList.add(new char[]{valueForCol});
				} else {
					for (char[] originalValue : valueList) {
						char[] newValue = new char[originalValue.length + 1];

						System.arraycopy(originalValue, 0, newValue, 0,
								originalValue.length);
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
