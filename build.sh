#!/bin/sh
mvn clean install

cp steam-scraper.sh steam-scraper
cat target/steam-scraper.jar >> steam-scraper
chmod +x steam-scraper
