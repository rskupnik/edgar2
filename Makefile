.PHONY: deploy, build-db, build-core, build-http, build-app, build, ship, start, stop, restart, ssh

deploy:
	cp ./app/build/libs/app.jar ./app/target/edgar2.jar
	#ssh pi@edgarmaster bash /home/pi/edgarStop.sh
	scp ./app/target/edgar2.jar pi@edgarmaster:/home/pi
	scp ./app/src/main/resources/demo.json pi@edgarmaster:/home/pi
	scp ./app/src/main/resources/device-config.json pi@edgarmaster:/home/pi
	ssh pi@edgarmaster 'nohup java -jar edgar2.jar --dashboard="/home/pi/demo.json" --deviceConfig="/home/pi/device-config.json" > edgar.log 2>&1 &'

start:
	ssh pi@edgarmaster 'nohup java -jar edgar2.jar --dashboard="/home/pi/demo.json" --deviceConfig="/home/pi/device-config.json" > edgar.log 2>&1 &'

start-debug:
	ssh pi@edgarmaster 'PATH="/opt/jdk-17.0.4.1+1/bin:$$PATH" nohup java -jar edgar2.jar -Dagentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 --dashboard="/home/pi/demo.json" --deviceConfig="/home/pi/device-config.json" > edgar.log 2>&1 &'

stop:
	ssh pi@edgarmaster bash /home/pi/edgarStop.sh

restart: stop start

restart-debug: stop start-debug

ssh:
	ssh pi@edgarmaster 

log:
	ssh pi@edgarmaster tail -f /home/pi/edgar.log

build-db:
	./gradlew :db:build

build-core:
	./gradlew :core:build

build-http:
	./gradlew :http:build

build-app:
	./gradlew :app:build

build: build-core build-db build-http build-app

ship: build deploy
