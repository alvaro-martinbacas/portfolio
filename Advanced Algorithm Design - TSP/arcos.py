from distancia import *
from leer_archivo import *
from AlgGRE_Clase3C_Grupo05 import *

"""
ruta_actual: ruta a la que se le va a aplicar el cambio de posiciones del opt2
i, j: indices de la ruta que se van a intercambiar
matrizDist: matriz de distancias
"""
def calcular_arcos(ruta_actual, i, j, matrizDist):
    #print("Ruta inicial en calcular arcos: ", ruta_actual)
    nCiudades = len(ruta_actual) - 1  # -1 porque la ruta incluye la vuelta a la primera ciudad
    ciudad_i = ruta_actual[i]
    ciudad_j = ruta_actual[j]
    #ruta_aux = np.array(ruta_actual)

    ciudad_antes_i = ruta_actual[i - 1] if i > 0 else ruta_actual[-2]  # penúltima
    ciudad_despues_i = ruta_actual[i + 1] if i < nCiudades - 1 else ruta_actual[0]  

    ciudad_antes_j = ruta_actual[j - 1] if j > 0 else ruta_actual[-2]
    ciudad_despues_j = ruta_actual[j + 1] if j < nCiudades - 1 else ruta_actual[0]

    arcos_eliminados = (
        matrizDist[ciudad_antes_i][ciudad_i] +
        matrizDist[ciudad_i][ciudad_despues_i] +
        matrizDist[ciudad_antes_j][ciudad_j] +
        matrizDist[ciudad_j][ciudad_despues_j]
    )

    #cambia la propia ruta
    swapRuta(ruta_actual, i, j)

    ciudad_antes_i = ruta_actual[i - 1] if i > 0 else ruta_actual[-2]  
    ciudad_despues_i = ruta_actual[i + 1] if i < nCiudades - 1 else ruta_actual[0]  

    ciudad_antes_j = ruta_actual[j - 1] if j > 0 else ruta_actual[-2]
    ciudad_despues_j = ruta_actual[j + 1] if j < nCiudades - 1 else ruta_actual[0]

    arcos_anadidos = (
        matrizDist[ciudad_antes_i][ciudad_j] +  # ciudad_i ahora es ciudad_j
        matrizDist[ciudad_j][ciudad_despues_i] +  # ciudad_j ahora en la posición de ciudad_i
        matrizDist[ciudad_antes_j][ciudad_i] +  # ciudad_j ahora es ciudad_i
        matrizDist[ciudad_i][ciudad_despues_j]  # ciudad_i ahora en la posición de ciudad_j
    )

    #volver a ruta inicial
    swapRuta(ruta_actual, i, j)
    #print("Ruta final en calcular arcos: ", ruta_actual)

    #ruta_actual = cambiarRuta(ruta_actual, i, j)
    return arcos_eliminados, arcos_anadidos

"""
Método que coge una ruta actual y le intercambia las posiciones de los índices
"""
def cambiarRuta(ruta_actual, i, j):
    nueva_ruta = ruta_actual[:]
    swapRuta(nueva_ruta, i, j)
    return nueva_ruta

def swapRuta(ruta_actual, i, j):
    ruta_actual[i], ruta_actual[j] = ruta_actual[j], ruta_actual[i]
    if i == 0:
        ruta_actual[-1] = ruta_actual[0]
    if j == len(ruta_actual)-1:
        ruta_actual[0] = ruta_actual[-1]    

