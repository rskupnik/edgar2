.PHONY: deploy, build-db, build-core, build-http, build-app, build, ship

deploy:
	cp ./app/target/edgar2-0.0.1-SNAPSHOT.jar ./app/target/edgar2.jar
	ssh pi@192.168.0.154 bash /home/pi/edgarStop.sh
	scp ./app/target/edgar2.jar pi@192.168.0.154:/home/pi
	ssh pi@192.168.0.154 nohup java -jar edgar2.jar > edgar.log &

build-db:
	mvn clean install -pl :edgar2-db;

build-core:
	mvn clean install -pl :edgar2-core;

build-http:
	mvn clean install -pl :edgar2-http;

build-app:
	mvn clean install -pl app;

build: build-core build-db build-http build-app

ship: build deploy