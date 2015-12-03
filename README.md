# steam-scraper
Scrapes game data from the Steam shop API and adds categories from the shop categories and genres.

Shop API is rate limited and suffers from non-deterministic failures, run the script multiple times to gather more data.

## Usage

```
usage: java -jar steam-scraper-*.one-jar.jar
 -c                       don't add categories
 -f <file>                required if using multiple accounts: absolute
                          path to desired sharedconfig.vdf
 -g                       don't add genres
 -h,--help                show this help
 -r,--remove <category>   remove categories
 -w                       directly overwrite sharedconfig.vdf (potentially
                          dangerous)
```

### Example
Remove silly tags and overwrite sharedconfig.vdf directly (might want to quit steam first)

```
java -jar steam-scraper-*.one-jar.jar --remove "Steam Achievements" "Steam Trading Cards" "Steam Cloud" "Steam Leaderboards" "Valve Anti-Cheat enabled" "Steam Workshop" -w
```


## Build dependencies:
* maven 3
* openjdk 7/8

### Building:
```mvn clean install```

Binaries will be in the `target` directory

