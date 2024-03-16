from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Set up Chrome WebDriver (you need to have chromedriver installed)
driver = webdriver.Chrome()

# Open Apple website
driver.get("https://www.apple.com")

# Define wait
wait = WebDriverWait(driver, 10)

# Find and hover over the 'Mac' element in the menu bar
mac_element = wait.until(EC.visibility_of_element_located((By.XPATH, "//a[contains(@href, '/mac/')][contains(@class, 'ac-gn-link ac-gn-link-mac')]")))
ActionChains(driver).move_to_element(mac_element).perform()

# Wait for the sub-menu to appear
submenu = wait.until(EC.visibility_of_element_located((By.XPATH, "//a[contains(@href, '/macbook-pro/')][contains(@class, 'ac-gn-link ac-gn-link-macbook-pro')]")))

# Click on the MacBook Pro link
submenu.click()
