cd ..;
mvn compile exec:java@sizeTest -Dexec.args="-d 15 -t 0.01 -f data/airbnb_1million.csv -o";