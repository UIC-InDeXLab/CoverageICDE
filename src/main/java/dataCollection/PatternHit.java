package dataCollection;
import java.util.ArrayList;

public class PatternHit {
	public char[] vcomb;
	public ArrayList<Integer> patternsIndices;
	public PatternHit(char[] vcomb, ArrayList<Integer> p)
	{
		this.vcomb = vcomb;
		this.patternsIndices = p;
	}
}
