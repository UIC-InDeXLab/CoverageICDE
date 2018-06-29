cd ..;
mvn compile exec:java@dcThresholdAirbnb -Dexec.args="-n 1000000 -d 13 -f data/airbnb_1million.csv -o";