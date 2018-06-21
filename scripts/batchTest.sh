cd ..;
mvn compile exec:java@batchTest -Dexec.args="-d 10 -n 100000 -t 200 -f data/airbnb_100000.csv -o";