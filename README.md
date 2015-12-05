# steam-scraper
Scrapes game data from the Steam shop API and adds categories from the shop categories and genres.

![Categories](/steamCategories.jpg?raw=true "Categories")


## Usage

Shop API is rate limited and suffers from non-deterministic failures, run the script multiple times to gather more data.
Please exit steam before running.

```
usage: java -jar steam-scraper-*.one-jar.jar
 -c                       don't add categories
 -f <file>                absolute path to sharedconfig.vdf to use
 -g                       don't add genres
 -h,--help                show this help and print paths
 -r,--remove <category>   remove categories
 -w                       directly overwrite sharedconfig.vdf (potentially
                          dangerous)
```

### Example
Remove silly tags and overwrite sharedconfig.vdf directly (might want to quit steam first)

```
java -jar steam-scraper-*.one-jar.jar --remove "Steam Achievements" "Steam Trading Cards" "Steam Cloud" "Steam Leaderboards" "Valve Anti-Cheat enabled" "Steam Workshop" "Includes Source SDK" "Commentary available" "Captions available" "Stats" -w
```


## Build dependencies:
* maven 3
* openjdk 7/8

### Building:
```mvn clean install```

Binaries will be in the `target` directory

