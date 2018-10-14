using namespace std;
#include <iostream>
#include <vector>

class FastIntersectCheck
{
private:
	int numberoflists; // the number of rows
	int n; // the number of columns
	vector<unsigned long>* lists; // the lists themselves
public:
	FastIntersectCheck(int numberoflists)
	{
		this->numberoflists = numberoflists;
		n = 0;
		lists = new vector<unsigned long>[numberoflists];
	}
	void add(int* newval)
	{
		int bitsize = 8 * sizeof(unsigned long); // the size of variable in bits
		int rem = n%bitsize;
		if (!rem) // lists are full
			for (int i = 0; i < numberoflists; i++) lists[i].push_back(0);
		int np = n / bitsize; // points to the last element of the lists
		unsigned long mask = 1 << (bitsize - rem - 1);
		for (int i = 0; i < numberoflists; i++)
			if (newval[i] == 1) lists[i][np] |= mask; 

		n++; // one column added
	}
	bool intersets(vector<int> indices)
	{
		int bitsize = 8 * sizeof(unsigned long);
		if (indices.size() == 0 || n == 0) return false; // error
		int np = (n-1) / bitsize; // the index of the last vairable in the list
		for (int i = 0; i <= np; i++)
		{
			unsigned long x = lists[indices[0]][i];
			for(int j = 1; j < indices.size(); j++)
			{
				x &= lists[indices[j]][i];
				if (!x) break;
			}
			if(x>0) return true;
		}
		return false;
	}
};