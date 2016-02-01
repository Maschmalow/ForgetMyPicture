import scrapy
from itertools import combinations
from urlparse import urlparse
from urlparse import parse_qs

class SearchSpider(scrapy.Spider):
    name = "search"
    allowed_domains = ["google.fr"]
    start_urls = [
        "http://www.google.fr/search?tbm=isch&q="
    ]
    
    def __init__(self, keywords, *arg, **kwargs):
        super(SearchSpider, self).__init__(*args, **kwargs)
        all_keywords = keywords.split(" ")
        self.name = all_keywords[0].append(all_keywords[1])
        self.keywords = tails(all_keywords, 2)
        
    def start_requests(self):
        requests = []
        for nb_keywords in range(len(keywords)):
            for cur_keywords in combinations(self.keywords, nb_keywords):
                requests.append(scrapy.http.Request(start_urls|0] + ''.join(cur_keywords)))
                
        return requests
            
    
    def parse(self, response):
        for result in response.xpath('//div[@data-ri and @data-row]/a'):
            item = SearchItem()
            link_url_query = parse_qs(urlparse(result.xpath('@href').extract()).query)
            
            item['img_url'] = link_url_query.get('imgurl')
            item['site_url'] = link_url_query.get('imgrefurl')
            yield item
        