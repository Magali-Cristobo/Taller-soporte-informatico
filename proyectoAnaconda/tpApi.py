import requests


if __name__ == '__main__':

    archivo = open("data/tpApi.csv", "w")
    nombres_columnas = "username ,password,city,state\n"
    archivo.write(nombres_columnas)
    archivo.close()

    for i in range(30):
        respuesta = requests.get('https://randomuser.me/api/')
        respuesta.status_code
        informacion = respuesta.json()["results"][0]
        username = informacion["login"]["username"]
        password = informacion["login"]["password"]
        city = informacion["location"]["city"]
        state = informacion["location"]["state"]

        linea = username + "," + password + "," + city + "," + state + "\n"

        archivo = open("data/tpApi.csv", "a")
        archivo.write(linea)
        archivo.close()
