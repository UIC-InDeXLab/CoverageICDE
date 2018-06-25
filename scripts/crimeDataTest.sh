cd ..;
mvn compile exec:java@crimeDataTest -Dexec.args="-d 4 -f data/RecidivismData_Original-categorized.csv -o";