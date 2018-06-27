cd ..;
mvn compile exec:java@sizeTest -Dexec.args="-d 15 -t 0.001 -f data/airbnb_1million.csv -o";