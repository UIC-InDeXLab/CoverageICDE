cd ..;
mvn compile exec:java@dcThresholdAirbnb -Dexec.args="-n 1000000 -d 15 -f data/airbnb_1million.csv -o";