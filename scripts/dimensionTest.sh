cd ..;
mvn compile exec:java@dimensionTest -Dexec.args="-n 1000000 -t 0.001 -f data/airbnb_1million.csv -o";