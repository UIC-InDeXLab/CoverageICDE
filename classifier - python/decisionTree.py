from sklearn import tree
import numpy as np
from sklearn.metrics import accuracy_score
import graphviz 


def read_csv(filename):
	f = open(filename)
	f.readline()  # skip the header
	data = np.loadtxt(fname = f, delimiter = ',')

	varibles = data[:, 0:4]
	label = data[:,4]

	return varibles, label

folder = 'data3/'
filenames = ['RecidivismData_none.csv', 'RecidivismData_others.csv']
testfile = 'TestR2.csv'
test_variables, test_label = read_csv(folder+testfile)

for fn in filenames:
	train_variables, train_label = read_csv(folder + fn)

	clf = tree.DecisionTreeClassifier()
	clf = clf.fit(train_variables, train_label)
	y_pred = clf.predict(test_variables)
	print fn
	print "predict:", y_pred
	print "actual:", test_label
	print "accuracy", accuracy_score(test_label, y_pred)
	print 

	dot_data = tree.export_graphviz(clf, out_file=None, feature_names=['sex','age','race','marrital status'], class_names={0: 'no', 1: 'yes'}) 
	graph = graphviz.Source(dot_data) 
	graph.render(fn) 