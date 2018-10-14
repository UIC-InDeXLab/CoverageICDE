from sklearn.naive_bayes import GaussianNB
import numpy as np
from sklearn.metrics import accuracy_score

def read_csv(filename):
	f = open(filename)
	f.readline()  # skip the header
	data = np.loadtxt(fname = f, delimiter = ',')

	varibles = data[:, 0:4]
	label = data[:,4]

	return varibles, label

folder = 'data2/'
filenames = ['RecidivismData_sub_none.csv', 'RecidivismData_sub.csv']
testfile = 'TestR.csv'
test_variables, test_label = read_csv(folder+testfile)

for fn in filenames:
	train_variables, train_label = read_csv(folder + fn)

	clf = GaussianNB()
	clf = clf.fit(train_variables, train_label)
	y_pred = clf.predict(test_variables)
	print fn
	print "accuracy", accuracy_score(test_label, y_pred)