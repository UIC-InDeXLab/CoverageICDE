cd ..;
mvn compile exec:java@thresholdTestOnBlueNile -Dexec.args="-d 7 -f data/BlueNile_categorical.csv -o";