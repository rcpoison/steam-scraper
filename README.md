# steam-scraper
Scrapes game data from the Steam shop API and adds categories from the shop categories and genres.

Does not yet write the file directly, pipe it to a temp file and overwrite it yourself.

Shop API is rate limited and suffers from non-deterministic failures, run the script multiple times to gather more data.
