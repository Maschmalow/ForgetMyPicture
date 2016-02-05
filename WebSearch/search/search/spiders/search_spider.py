import scrapy
import logging
from search.items import SearchItem
from itertools import combinations
from urlparse import urlparse
from urlparse import parse_qs

class SearchSpider(scrapy.Spider):
    name = "search"
    allowed_domains = ["google.fr"]
    start_urls = [
        "http://www.google.fr/search?tbm=isch&q="
    ]
    
    
    def __init__(self, keywords, *args, **kwargs):
        super(SearchSpider, self).__init__(*args, **kwargs)
        all_keywords = keywords.split(" ")
        self.name = all_keywords[:2]
        self.keywords = all_keywords[2:]
        self.done = False
        
    def start_requests(self):
        requests = []
        for nb_keywords in range(len(self.keywords)):
            for cur_keywords in combinations(self.keywords, nb_keywords):
                logging.info("request URL: " + self.start_urls[0]  +  ''.join(self.name) + ''.join(cur_keywords))
                if not self.done:
                    requests.append(scrapy.http.Request(self.start_urls[0]  +  ''.join(self.name) + ''.join(cur_keywords)))
                    self.done = True
                
        return requests
            
    
    def parse(self, response):
        for result in response.xpath('//div[@data-ri and @data-row]/a'):
            item = SearchItem()
            link_url_query = parse_qs(urlparse(result.xpath('@href').extract()[0]).query)
            
            item['img_url'] = link_url_query.get('imgurl')[0]
            item['site_url'] = link_url_query.get('imgrefurl')[0]
            yield item
        