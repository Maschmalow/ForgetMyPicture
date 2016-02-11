Scraper done using scrapy.

To install (see http://doc.scrapy.org/en/1.0/intro/install.html for details)
Requirement: Python 2.7, pip
>pip install Scrapy

'search' spider take keywords as argument (name and forname mandatory) and
launch a google image search for every combination of keywords, with different user-agent every time.
it returns a set of items that contains:
* an image url (the direct url of the image result)
* a site url (the url of the webpage that host the picture)

the parsing step has been tested and is working:
with the search result page given, it produce the desired items