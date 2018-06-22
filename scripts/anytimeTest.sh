cd ..;
mvn compile exec:java@anytimeTest -Dexec.args="-d 16 -n 1000000 -t 0.01 -f data/airbnb_1million.csv -o";