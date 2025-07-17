import time
import numpy as np
import random
from leer_archivo import *
from distancia import *
import logging
import uuid
import matplotlib.pyplot as plt

id_ejecucion = uuid.uuid4()
logging.basicConfig(
    filename='registro.log',
    level=logging.INFO,
    format='%(asctime)s - %(id)s - %(filename)s- %(levelname)s - %(message)s'
)
class ContextFilter(logging.Filter):
    def filter(self, record):
        record.id = id_ejecucion
        return True
    
logger = logging.getLogger()
logger.addFilter(ContextFilter())

configuracion = leer_configuracion('config.txt')

K_GREEDY = int(configuracion['K_GREEDY'])

def calcular_distancia_a_ciudades(matriz):
    # suma la fila de la amtriz completa
    suma_filas = matriz.sum(axis=1)
    #print(suma_filas)
    # se crea una lista de tuplas -> (ciudad, suma de distancias) que sera la que se devuelva y se ordena
    vector_ciudades = sorted(enumerate(suma_filas), key=lambda x: x[1])
    #print("vector ciudades: ", vectorCiudades)

    return vector_ciudades


""" Algoritmo greedy para la practica 1
"""
def greedy_aleatorio(matriz):
    logging.info("GREEDY ALEATORIO. ")
    ruta = []
    start = time.time()    

    # calcular la distancia a todas las ciudades del problema
    vector_ciudades = calcular_distancia_a_ciudades(matriz)

    # Seleccionar una ciudad aleatoria de las 5 más prometedoras:
    primera_eleccion = random.randint(0, min(K_GREEDY - 1, len(vector_ciudades) - 1))  # Por si el número de ciudades es < kgreedy
    primera_ciudad = vector_ciudades[primera_eleccion][0]  

    ruta.append(primera_ciudad)
    vector_ciudades.remove(vector_ciudades[primera_eleccion])
    vector_ciudades.sort(key=lambda x:x[1])
    """
    ciudades, distancias = zip(*vectorCiudades)

    # Visualizar con matplotlib
    plt.figure(figsize=(8, 6))
    plt.bar(ciudades, distancias, color='blue')
    plt.xlabel('Ciudades')
    plt.ylabel('Distancia Total')
    plt.title('Distancia Total de cada Ciudad')
    plt.xticks(ciudades, [f"{i}" for i in ciudades])

    # Mostrar gráfico
    plt.show()
    """
    
    while vector_ciudades:

        eleccion = random.randint(0, min(K_GREEDY - 1, len(vector_ciudades) - 1))
        siguiente_ciudad = vector_ciudades[eleccion][0] 

        ruta.append(siguiente_ciudad)
        
        vector_ciudades.remove(vector_ciudades[eleccion])
        #vectorCiudades.sort(key=lambda x:x[1])

        

    ruta.append(ruta[0])  # ciudad inicial al final ya que es  restriccion del problema
    dist = calcular_distancia_recorrida(ruta, matriz) #devolver distancia tmbien

    end = time.time()
    exec_time = end - start
    logging.info(f"Tiempo en ejecutar Greedy Aleatorio: {exec_time}")
    #logging.info(f"Solucion devuelta por greedy: {ruta}")
    logging.info(f"Coste: {float(dist)}")         
      
    return ruta, dist

