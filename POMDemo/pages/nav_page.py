from selenium.webdriver.common.by import By

# Class
class GlobalNav:
    def __init__(self, driver):
        self.driver = driver

    def open_page(self, url):
        self.driver.get(url)

    def click_global_nav(self, link_name):
        self.find_element(By.LINK_TEXT, link_name).click()  

    def test():
        print("test")
            
 