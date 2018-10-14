from sklearn import tree
import numpy as np
from sklearn.metrics import accuracy_score
import graphviz 
from sklearn.preprocessing import OneHotEncoder
from sklearn.metrics import classification_report
from sklearn.decomposition import TruncatedSVD


def read_csv(filename):
	f = open(filename)
	f.readline()  # skip the header
	data = np.loadtxt(fname = f, delimiter = ',')

	X = data[:, 0:4]
	y = data[:,4]

	return X, y

folder = 'data3/'

whole_train_filename = 'RecidivismData_rand.csv'
whole_test_filename = 'testRand.csv'
X_test_whole, y_test_whole = read_csv(folder+whole_test_filename)

filenames = ['RecidivismData_none.csv', 'RecidivismData_20.csv', 'RecidivismData_40.csv', 'RecidivismData_60.csv', 'RecidivismData_others.csv']
testfile = 'TestR2.csv'
X_test_orig, y_test_orig = read_csv(folder+testfile)


# One hot encoding
X_train_enc, y_train_enc = read_csv(folder + whole_train_filename)
enc = OneHotEncoder(handle_unknown='ignore')
enc.fit(X_train_enc)
X_train_enc = enc.transform(X_train_enc)

# # PCA
# pca = TruncatedSVD(n_components=8)
# pca.fit(X_train_enc)


for fn in filenames:
	X_train, y_train = read_csv(folder + fn)

	clf = tree.DecisionTreeClassifier()


	X_train = enc.transform(X_train).toarray()
	# X_train = pca.transform(X_train)

	# Training
	clf = clf.fit(X_train, y_train)

	# One-hot encoding
	X_test = enc.transform(X_test_orig).toarray()
	# X_test = pca.transform(X_test)
	
	# Testing
	y_pred = clf.predict(X_test)
	print fn
	print classification_report(y_test_orig, y_pred)
	print 

	# dot_data = tree.export_graphviz(clf, out_file=None, feature_names=['sex','age','race','marrital status'], class_names={0: 'no', 1: 'yes'}) 
	# graph = graphviz.Source(dot_data) 
	# graph.render(fn) 



# Use the whole dataset
X_train, y_train = read_csv(folder + whole_train_filename)

clf = tree.DecisionTreeClassifier()


X_train = enc.transform(X_train).toarray()
# X_train = pca.transform(X_train)

# Training
clf = clf.fit(X_train, y_train)

# One-hot encoding
X_test = enc.transform(X_test_whole).toarray()
# X_test = pca.transform(X_test)

# Testing
y_pred = clf.predict(X_test)
print whole_train_filename
print classification_report(y_test_whole, y_pred)
print 