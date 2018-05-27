using namespace std;
#include <iostream>
#include <vector>

namespace Beaver {
	class FastIntersectCheck
	{
	private:
		int num_bit_vecs; // the number of rows
		int num_patterns; // the number of columns
		vector<unsigned long>* bit_vecs; // the bit_vecs themselves
	public:
		FastIntersectCheck(int num_bit_vecs)
		{
			this->num_bit_vecs = num_bit_vecs;
			num_patterns = 0;
			bit_vecs = new vector<unsigned long>[num_bit_vecs];
		}
		void add(int* newval)
		{
			int bitsize = 8 * sizeof(unsigned long); // the size of variable in bits
			int rem = num_patterns%bitsize;
			if (!rem) // bit_vecs are full
				for (int i = 0; i < num_bit_vecs; i++) bit_vecs[i].push_back(0);
			int np = num_patterns / bitsize; // points to the last element of the bit_vecs
			unsigned long mask = 1 << (bitsize - rem - 1);
			for (int i = 0; i < num_bit_vecs; i++)
				if (newval[i] == 1) bit_vecs[i][np] |= mask; 

			num_patterns++; // one column added
		}
		void add(string new_pattern)
		{
	
		}
		bool intersect(vector<int> indices)
		{
			int bitsize = 8 * sizeof(unsigned long);
			if (indices.size() == 0 || num_patterns == 0) return false; // error
			int np = (num_patterns-1) / bitsize; // the index of the last vairable in the list
			for (int i = 0; i <= np; i++)
			{
				unsigned long x = bit_vecs[indices[0]][i];
				for(int j = 1; j < indices.size(); j++)
				{
					x &= bit_vecs[indices[j]][i];
					if (!x) break;
				}
				if(x>0) return true;
			}
			return false;
		}
	};
}