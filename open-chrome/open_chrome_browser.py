from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait


options = Options()
options.add_experimental_option("detach", True)

driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

driver.get("https://www.apple.com")
#driver.find_element
#driver.find_element_by_css_selector("button:contains('Mac')").click()
#links= driver.find_elements("xpath", "//a[@href]")
#for link in links:
#    print(link.get_attribute("innerHTML"))


link1 = driver.find_element(By.LINK_TEXT, "iPad")
#link1 = WebDriverWait(driver, 10)
link1.click()

link2 = driver.find_element(By.LINK_TEXT, "iPhone")
link2.click()

link3 = driver.find_element(By.LINK_TEXT, "Mac")
link3.click()

link4 = driver.find_element(By.LINK_TEXT, "iMac")
link4.click()
"""
try:
    element = WebDriverWait(driver, 10).until(EC.presence_of_all_elements_located(By.LINK_TEXT, "Mac"))
    element.click()
except:
    #driver.quit() 
    print("Do nothing")  


driver.find_element(by=By.LINK_TEXT, value ="Mac").click()

driver.find_element(by=By.LINK_TEXT, value ="iPad").click()
driver.find_element(by=By.LINK_TEXT, value ="iPhone").click()
driver.find_element(by=By.LINK_TEXT, value ="Watch").click()
driver.find_element(by=By.LINK_TEXT, value ="Vision").click()
driver.find_element(by=By.LINK_TEXT, value ="AirPods").click()
driver.find_element(by=By.LINK_TEXT, value ="TV & Home").click()
driver.find_element(by=By.LINK_TEXT, value ="Entertainment").click()
driver.find_element(by=By.LINK_TEXT, value ="Accessories").click()
driver.find_element(by=By.LINK_TEXT, value ="Apple").click()

link = driver.find_element

print("End Test")

"""



driver.close()