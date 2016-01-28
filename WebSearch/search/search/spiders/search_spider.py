import scrapy
from urlparse import urlparse
from urlparse import parse_qs

class SearchSpider(scrapy.Spider):
    name = "search"
    allowed_domains = ["google.fr"]
    start_urls = [
        "http://www.google.com/search"
    ]

    def parse(self, response):
        for result in response.xpath('//div[@data-ri and @data-row]/a'):
            item = SearchItem()
            link_url_query = parse_qs(urlparse(result.xpath('@href').extract()).query)
            
            item['img_url'] = link_url_query.get('imgurl')
            item['site_url'] = link_url_query.get('imgrefurl')
            yield item
        