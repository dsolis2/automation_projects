 
from selenium import webdriver 

url = input('Enter URL ')
driver = webdriver.Firefox(executable_path = '/path/to/geckodriver') 
driver.get(url) 
