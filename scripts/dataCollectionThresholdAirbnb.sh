cd ..;
mvn compile exec:java@dcThresholdAirbnb -Dexec.args="-n 1000000 -d 12 -f data/airbnb_1million.csv -o";