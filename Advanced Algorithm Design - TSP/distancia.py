import numpy as np
from scipy.spatial.distance import cdist

def construir_matriz_distancias(coordenadas):
    coordenadas = np.array(coordenadas) 
    
    # cdist calcula la distancia euclideana entre cada par de dos colecciones de numeros
    matriz_distancias = cdist(coordenadas, coordenadas, metric='euclidean')
    
    #print("Matriz construida")
    return matriz_distancias

def calcular_distancia_recorrida(ruta, matriz):

    return np.sum(matriz[ruta[:-1], ruta[1:]])

