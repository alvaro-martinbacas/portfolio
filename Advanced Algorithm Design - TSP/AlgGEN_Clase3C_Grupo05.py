import random
import time
from AlgGRE_Clase3C_Grupo05 import *
from leer_archivo import *
from distancia import *
from arcos import *

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

# Cargar configuración
configuracion = leer_configuracion('config.txt')
m_poblacion = int(configuracion['individuos_iniciales'])
k_worst_gen = int(configuracion['k_worst_gen'])
parada_iteraciones = int(configuracion['parada_iteraciones'])
porcent_cruce_gen = float(configuracion['porcent_cruce_gen'])
porcent_mutacion = float(configuracion['porcent_mutacion'])
individuos_iniciales_aleatorios = float(configuracion['individuos_iniciales_aleatorios'])
individuos_iniciales_greedy = float(configuracion['individuos_iniciales_greedy'])


def tiempo_expirado(inicio, limite_tiempo):
    return (time.time() - inicio) >= limite_tiempo

# Generar población inicial combinando greedy y aleatorios
def generar_poblacion_inicial(matriz):
    padres_iniciales = []
    for _ in range(int(individuos_iniciales_greedy * m_poblacion)):
        ruta, dist = greedy_aleatorio(matriz)
        padres_iniciales.append((ruta, dist))
    for _ in range(int(individuos_iniciales_aleatorios * m_poblacion)):
        ruta, dist = generar_aleatorio(matriz)
        padres_iniciales.append((ruta, dist))
    return padres_iniciales

# Generar ruta aleatoria que cierre ciclo
def generar_aleatorio(matriz):
    n_ciudades = len(matriz)
    ruta = random.sample(range(n_ciudades), n_ciudades)
    ruta.append(ruta[0])  # Cierra el ciclo
    dist = calcular_distancia_recorrida(ruta, matriz)
    return ruta, dist

# Selección de mejor individuo mediante torneo binario
def torneo_ganadores(poblacion, k_best):
    posibles_padres = random.sample(poblacion, k_best)
    posibles_padres.sort(key=lambda x: x[1])  # Ordena por distancia (aptitud)
    return posibles_padres[0]

# Selección del peor individuo mediante torneo para mantener variabilidad
def torneo_perdedores(poblacion, k_worst):
    posibles_perdedores = random.sample(poblacion, k_worst)
    posibles_perdedores.sort(key=lambda x: x[1], reverse=True)
    return posibles_perdedores[0]


# Cruce tipo OX2, devuelve dos hijos para incrementar variabilidad
def cruce_ox2(padre_1, padre_2, matriz):
    n_ciudades = len(padre_1[0]) - 1
    
    hijo = (padre_1[0][:], 0)
    posiciones_seleccionadas = [
    i for i in range(n_ciudades) if random.random() < 0.5
    ]    

    ciudades_seleccionadas = [padre_2[0][pos] for pos in posiciones_seleccionadas]
    for ciudad in ciudades_seleccionadas:
        indice = hijo[0].index(ciudad)
        hijo[0][indice] = -1
    indices_cambiados = [i for i, v in enumerate(hijo[0]) if v == -1]
    i = 0
    for indice in indices_cambiados:
        hijo[0][indice] = ciudades_seleccionadas[i]
        i += 1
    if 0 in indices_cambiados:
        hijo[0][n_ciudades] = hijo[0][0]
    if n_ciudades in indices_cambiados:
        hijo[0][0] = hijo[0][n_ciudades]
        
    
    hijo_1 = (hijo[0], calcular_distancia_recorrida(hijo[0], matriz))
    
    
    #Repetir para el segundo hijo
    hijo = (padre_2[0][:], 0)
    ciudades_seleccionadas = [padre_1[0][pos] for pos in posiciones_seleccionadas]
    for ciudad in ciudades_seleccionadas:
        indice = hijo[0].index(ciudad)
        hijo[0][indice] = -1
    indices_cambiados = [i for i, v in enumerate(hijo[0]) if v == -1]
    i = 0
    for indice in indices_cambiados:
        hijo[0][indice] = ciudades_seleccionadas[i]
        i += 1
    if 0 in indices_cambiados:
        hijo[0][n_ciudades] = hijo[0][0]
    if n_ciudades in indices_cambiados:
        hijo[0][0] = hijo[0][n_ciudades]
        
    hijo_2 = (hijo[0], calcular_distancia_recorrida(hijo[0], matriz))

    return hijo_1, hijo_2

def cruce_moc(padre_1, padre_2, matriz):
    n_ciudades = len(padre_1[0]) - 1
    punto_cruce = random.randint(1, n_ciudades - 1)

    # El primer hijo toma el segmento izquierdo de `Padre 1` y lo completa con `Padre 2`
    hijo_1 = padre_1[0][:punto_cruce]
    for ciudad in padre_2[0]:
        if ciudad not in hijo_1:
            hijo_1.append(ciudad)
    hijo_1 = hijo_1[:n_ciudades] + [hijo_1[0]]  # Cierra el ciclo

    # El segundo hijo toma el segmento izquierdo de `Padre 2` y lo completa con `Padre 1`
    hijo_2 = padre_2[0][:punto_cruce]
    for ciudad in padre_1[0]:
        if ciudad not in hijo_2:
            hijo_2.append(ciudad)
    hijo_2 = hijo_2[:n_ciudades] + [hijo_2[0]]

    distancia_hijo_1 = calcular_distancia_recorrida(hijo_1, matriz)
    distancia_hijo_2 = calcular_distancia_recorrida(hijo_2, matriz)

    #print(f"\n--- Cruce MOC ---")
    #print(f"Padre 1: {padre_1[0]}")
    #print(f"Padre 2: {padre_2[0]}")
    #print(f"Punto de cruce: {punto_cruce}")
    #print(f"Hijo 1 resultante: {hijo_1}")
    #print(f"Distancia del hijo 1: {distancia_hijo_1}")
    #print(f"Hijo 2 resultante: {hijo_2}")
    #print(f"Distancia del hijo 2: {distancia_hijo_2}")
    #print("--- Fin del Cruce MOC ---\n")

    hijo_1 = hijo_1, distancia_hijo_1
    hijo_2 = hijo_2, distancia_hijo_2

    return hijo_1, hijo_2

#MUTACION 2OPT
def mutacion_2opt(individuo, matriz):
    nCiudades = len(individuo[0]) - 1
    dist = individuo[1]
    i, j = sorted(random.sample(range(nCiudades), 2))
    arc_elim, arco_anad = calcular_arcos(individuo[0], i, j, matriz)
    swapRuta(individuo[0], i, j)
    dist = dist - arc_elim + arco_anad
    #print("Dist arcos:  ", dist)
    #print("Dist metodo:  ", calcular_distancia_recorrida(individuo[0], matriz))
    individuo = (individuo[0], dist)
    return individuo

def generacional(matriz, tipo_cruce, k_best, n_elites):
    poblacion = generar_poblacion_inicial(matriz)
    elites = []
    poblacion.sort(key=lambda x: x[1])
    inicio = time.time()
    parada_tiempo = int(configuracion['parada_tiempo'])

    for i in range(n_elites):
        dist = poblacion[i][1]
        elites.append((poblacion[i][0][:], dist))
        
    nueva_generacion = []
    evaluaciones = len(poblacion)
    while evaluaciones <= parada_iteraciones and (time.time() - inicio) < parada_tiempo:
        
        evaluaciones += 1
        logger.info("Se inicia una nueva generacion de individuos. ")
        # Mantener élites en la nueva generación
        for _ in range(m_poblacion):
            while True:
                padre_1 = torneo_ganadores(poblacion, k_best)
                padre_2 = torneo_ganadores(poblacion, k_best)
                dist1 = padre_1[1]
                dist2 = padre_2[1]
                if padre_1 != padre_2:
                    break
            # Aplicar cruce o mantener uno de los padres
            if random.random() < porcent_cruce_gen:
                if tipo_cruce == "OX2":
                    if tiempo_expirado(inicio, parada_tiempo):
                        parada_tiempo = 0
                        break
                    hijo1, hijo2 = cruce_ox2(padre_1, padre_2, matriz)
                else:
                    if tiempo_expirado(inicio, parada_tiempo):
                        parada_tiempo = 0
                        break 
                    hijo1, hijo2 = cruce_moc(padre_1, padre_2, matriz)
            else:
                rand = random.random()
                hijo1 = (padre_1[0].copy(), dist1) if rand < 0.5 else (padre_2[0].copy(), dist2)
                hijo2 = (padre_2[0].copy(), dist2) if rand < 0.5 else (padre_1[0].copy(), dist1)
            # Aplicar mutación con probabilidad dada
            if random.random() < porcent_mutacion:
                hijo1 = mutacion_2opt(hijo1, matriz)
            if random.random() < porcent_mutacion:
                hijo2 = mutacion_2opt(hijo2, matriz)
            # Agregar hijos a la nueva generación
            nueva_generacion.append(hijo1)
            nueva_generacion.append(hijo2)
            evaluaciones += 2

        if tiempo_expirado(inicio, parada_tiempo):
            parada_tiempo = 0
            break
        # Comprobar que los elites estan en la nueva generacion
        estan_elites = [0 for _ in range(n_elites)]
        for i in range(len(elites)):
            if elites[i] in nueva_generacion:
                estan_elites[i] = 1
                #print("Elite encontrado en la nueva generacion")
                logger.info(f"Se ha mantenido el elite en la nueva generacion")
        

        #Mientras falten elites por mantener, comprobar que se mantienen
        while 0 in estan_elites:
            logger.info(f"Todavia faltan elites por mantener en la nueva generacion")
            perdedor = torneo_perdedores(nueva_generacion, k_worst_gen)
            ind_elite = estan_elites.index(0)
            nueva_generacion.remove(perdedor)
            nueva_generacion.append(elites[ind_elite])
            logger.info(f"Se elimina al padre perdedor, que tenia una distancia de: {perdedor[1]}")
            estan_elites[ind_elite] = 1
            
        logger.info(f"Todos los elite estan ya en la nueva generacion")
        if tiempo_expirado(inicio, parada_tiempo):
            parada_tiempo = 0
            break 
        nueva_generacion.sort(key=lambda x: x[1])
        elites.clear()
        for i in range(n_elites):
            dist = poblacion[i][1]
            elites.append((poblacion[i][0], dist))
            logger.info(f"Se aniade la nueva mejor ruta de la poblacion a los elite, que tiene una distancia de: {dist}")
        
        poblacion = nueva_generacion[:]
        nueva_generacion.clear()
        mejor_dist = poblacion[0][1]
        logger.info(f"EVALUACION {evaluaciones} La mejor solucion encontrada hasta el momento es de: {mejor_dist}") if evaluaciones % 2000 == 0 else None
        #print("Iteración:", evaluaciones, "Mejor distancia:", mejor_dist) if evaluaciones % 1500 == 0 else None
    mejor_dist = poblacion[0][1]
    mejor_ruta = poblacion[0][0]
    logger.info(f"Total de evaluaciones: {evaluaciones}")
    final = time.time()

    logger.info(f"Total en ejecutar: {final - inicio}")
    print("Total en ejecutar: ", final - inicio, "evaluaciones: ", evaluaciones)

    return mejor_ruta, mejor_dist
