#import sys
# sys.path.append("/pages")
from selenium import webdrive
import time
from POMDemo.pages.nav_page import GlobalNav



# Main driver
links = ["Store", "Mac", "iPad", "iPhone", "Watch", "Vision", "AirPods", "TV & Home", "Entertainment", "Accessories", "Apple"]

driver = webdriver.Chrome()
url = "http://www.apple.com"

#driver = Browsers.chrome
nav = GlobalNav(driver)
nav.open_page(url)

for link in links:
   nav.link(link)
   time.sleep(1)   