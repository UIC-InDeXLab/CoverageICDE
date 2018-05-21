cd ..;
mvn compile exec:java@test -Dexec.args="-d 10 -n 100000 -t 200 -f data/airbnb_100000.csv -a hybrid -o";
mvn compile exec:java@test -Dexec.args="-d 10 -n 100000 -t 200 -f data/airbnb_100000.csv -a greedy -o";
mvn compile exec:java@test -Dexec.args="-d 10 -n 100000 -t 200 -f data/airbnb_100000.csv -a PatternBreaker -o";
mvn compile exec:java@test -Dexec.args="-d 10 -n 100000 -t 200 -f data/airbnb_100000.csv -a PatternCombiner -o";
mvn compile exec:java@test -Dexec.args="-d 10 -n 100000 -t 200 -f data/airbnb_100000.csv -a PatternBreakerOriginal -o";