cd ..;
mvn compile exec:java@dimensionTest -Dexec.args="-n 1000000 -t 0.01 -f data/airbnb_1million.csv -o";