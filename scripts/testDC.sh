cd ..;
mvn compile exec:java@testdc -Dexec.args="-d 5 -n 100000 -t 200 -f data/airbnb_100000.csv  -o";