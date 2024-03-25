from selenium.webdriver.common.by import By

# Class
class GlobalNav:
    def __init__(self, driver, link_name):
       self.driver = driver
       self.link_name.find_element(By.LINK_TEXT, link_name)

    def open_page(self, url):
        self.driver.get(url)

     

   
            
 