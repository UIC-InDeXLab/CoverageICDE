from sklearn.svm import SVC
import numpy as np
from sklearn.metrics import accuracy_score

def read_csv(filename):
	f = open(filename)
	f.readline()  # skip the header
	data = np.loadtxt(fname = f, delimiter = ',')

	varibles = data[:, 0:4]
	label = data[:,4]

	return varibles, label

folder = 'data3/'
filenames = ['RecidivismData_none.csv', 'RecidivismData_20.csv', 'RecidivismData_40.csv', 'RecidivismData_60.csv', 'RecidivismData_others.csv']
testfile = 'TestR2.csv'
test_variables, test_label = read_csv(folder+testfile)

for fn in filenames:
	train_variables, train_label = read_csv(folder + fn)

	clf = SVC(gamma='auto')
	clf = clf.fit(train_variables, train_label)
	y_pred = clf.predict(test_variables)
	print fn
	print "accuracy", accuracy_score(test_label, y_pred)