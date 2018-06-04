using namespace std;
#include<iostream>
#include<random>
#include<time.h>
#include "FastIntersectCheck.h"

int main()
{
	srand(time(NULL));
	int nop = 100;
	int* values = new int[nop];
	FastIntersectCheck check(4);
	vector<int> indices;
	for (int i = 0; i < 3; i++) indices.push_back(i);
	for (int i = 0; i < nop; i++)
	{
		for (int j = 0; j < 4; j++)
		{
			values[j] =  rand() % 2;
			cout <<endl<< values[j] << ' ';
		}
		check.add(values);
		cout <<endl<<i<< ", intersect: " << check.intersets(indices);
	}
	getchar();
	return 0;
}