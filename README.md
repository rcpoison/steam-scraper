# steam-scraper
Scrapes categories/genres/user tags from the Steam shop API/store pages and adds them as categories in the Steam client.

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
 -i <file>                whitelist for tags to include
 -I                       remove all existing tags not in specified
                          whitelist
 -p                       print all available tags (respects -c, -g and
                          -u)
 -r,--remove <category>   remove categories
 -u                       add user tags
 -w                       directly overwrite sharedconfig.vdf (quit steam
                          before running!)
```

### Example
Remove silly tags and overwrite sharedconfig.vdf directly (might want to quit steam first)

```
java -jar steam-scraper-*.one-jar.jar --remove "Steam Achievements" "Steam Trading Cards" "Steam Cloud" "Steam Leaderboards" "Valve Anti-Cheat enabled" "Steam Workshop" "Includes Source SDK" "Commentary available" "Captions available" "Stats" -w
```


Build Whitelist:
```
java -jar steam-scraper-*.one-jar.jar -u -p > whitelist.txt
```
Now edit the whitelist.txt with your sacred editor of choice and run:
```
java -jar steam-scraper-*.one-jar.jar -u -i whitelist.txt -w
```
Or, if you want to also get rid of existing categories in your sharedconfig which are not in the whitelist:
```
java -jar steam-scraper-*.one-jar.jar -u -i whitelist.txt -I -w
```


## Build dependencies
* maven 3
* openjdk 7/8

### Building
```mvn clean install```

Binaries will be in the `target` directory


## Donation
BTC address: 172B8DJvbEJito89MjRQA7vwGhfqgm6Q6s
