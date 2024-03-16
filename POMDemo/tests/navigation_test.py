from selenium import webdriver
from pages import GlobalNav
import time



links = ["Mac", "iPad", "iPhone", "Watch"]

driver = webdriver.Chrome()
def test_global_nav(driver):

    url = "http//:www.apple.com"
    nav = GlobalNav(driver)
    nav.open_page(url)
    for link in links:
        nav.click_global_nav(link)
        time.sleep(1)

    