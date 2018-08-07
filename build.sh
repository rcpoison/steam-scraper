#!/bin/sh
mvn clean install

cp steam-scraper.sh steam-scraper
cat target/steam-scraper-*-boot.jar >> steam-scraper
chmod +x steam-scraper
