
import requests

while True:
    API_KEY = 'Paste your key here'
    URL = 'http://api.openweathermap.org/data/2.5/weather'

    location = input('Enter a city name: ')
    # fstring 
    request_url = f'{URL}?appid={API_KEY}&q={location}'
    response = requests.get(request_url)

    def convert_temp(c):
        f = ((int(c) * 1.8) + 32)
        return f

    if response.status_code == 200:
        data = response.json()
        weather = data['weather'][0]['description']
        print('The weather now is: ', weather)
        tempeture = round(data['main']['temp'] - 273.15, 1)
        ftemp = round(convert_temp(tempeture), 1)
        print('The tempature is',tempeture, 'celcius,', ftemp, 'Fahrenheit')
    else:
        print('An error occurred')   
    if input('Do You Want To Continue? ') != 'y':
        print("Have a nice day!")
        break

