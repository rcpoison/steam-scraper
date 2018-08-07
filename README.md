# steam-scraper
Scrapes categories/genres/user tags from the Steam shop API/store pages and adds them as categories in the Steam client.

![Categories](/steamCategories.jpg?raw=true "Categories")


## Usage


Please exit steam before running.

```
usage: steam-scraper
 -c                       don't add categories
 -f <file>                absolute path to sharedconfig.vdf to use
 -g                       don't add genres
 -h,--help                show this help and print paths
 -i <file>                whitelist for tags to include (one tag per line)
 -I                       remove all existing tags not in specified
                          whitelist
 -p                       print all available tags (respects -c, -g and
                          -u)
 -r,--remove <category>   remove categories
 -R <file>                file containing replacements (one replacement
                          per line, in the format original=replacement)
 -t <arg>                 number of threads
 -u                       add user tags
 -v                       verbose output
 -w                       directly overwrite sharedconfig.vdf (quit steam
                          before running!
```

### Example
Remove silly tags and overwrite sharedconfig.vdf directly (might want to quit steam first)

```
steam-scraper --remove "Steam Achievements" "Steam Trading Cards" "Steam Cloud" "Steam Leaderboards" "Valve Anti-Cheat enabled" "Steam Workshop" "Includes Source SDK" "Commentary available" "Captions available" "Stats" -w
```


Build whitelist:
```
steam-scraper -u -p > whitelist.txt
```
Will write a file which contains all available tags.
Now edit the whitelist.txt with your sacred editor of choice, remove the tags you do not want and run:
```
steam-scraper -u -i whitelist.txt -w
```
Or, if you want to also get rid of existing categories in your sharedconfig which are not in the whitelist:
```
steam-scraper -u -i whitelist.txt -I -w
```


Replacement file example:
```
1990's = 1990s
Rogue-lite = Rogue-like
Single-player = Singleplayer
```

## Configuration file
A configuration based on the CLI parameters will be created if none exists yet.
The configuration file is located in ~/.config/steam-scraper/steam-scraper.conf

```
# paths to sharedconfig.vdf files, seperated by ':'
sharedConfigPaths=/path/to/sharedconfig.vdf

# tag types to scrape
tagTypes=GENRE,CATEGORY,USER,VR

# whitelist file path
whiteList=/path/to/whitelist.txt

# replacements file
replacements=/path/to/replacements.txt

# whether to remove all tags, including existing, which are not in whitelist. WARNING: will also remove favorite tags if not in whitelist!
removeNotWhiteListed=false

# number of threads to use for downloading/parsing, defaults to #CPUs+1
downloadThreads=8

# number of days before cached store pages expire, defaults to 7
cacheExpiryDays=7
```


## Build dependencies
* sh
* maven 3
* openjdk 8

### Building
```./build.sh```

Binary called ```steam-scraper``` will be in the project directory.


## Donation
BTC address: 172B8DJvbEJito89MjRQA7vwGhfqgm6Q6s
