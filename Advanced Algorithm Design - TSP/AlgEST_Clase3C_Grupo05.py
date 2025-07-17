import random
import time
from AlgGRE_Clase3C_Grupo05 import *
from leer_archivo import *
from distancia import *
from arcos import *
from AlgGEN_Clase3C_Grupo05 import *

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
k_best_est = int(configuracion['k_best_est'])
k_worst_est = int(configuracion['k_worst_est'])
parada_tiempo = int(configuracion['parada_tiempo'])
porcent_mutacion = float(configuracion['porcent_mutacion'])
individuos_iniciales_aleatorios = float(configuracion['individuos_iniciales_aleatorios'])
individuos_iniciales_greedy = float(configuracion['individuos_iniciales_greedy'])


def estacionario(matriz, tipo_cruce):
    poblacion = generar_poblacion_inicial(matriz)
    inicio = time.time()
    evaluaciones = 0
    parada_tiempo = int(configuracion['parada_tiempo'])

    while evaluaciones < parada_iteraciones and (time.time() - inicio) < parada_tiempo:
        
        logger.info("Se comienza una nueva iteracion. ")
        evaluaciones += 1
        while True:
            padre_1 = torneo_ganadores(poblacion, k_best_est)
            padre_2 = torneo_ganadores(poblacion, k_best_est)
            
            if padre_1 != padre_2:
                break
        if tipo_cruce == "OX2":
            if tiempo_expirado(inicio, parada_tiempo):
                break
            hijo_1, hijo_2 = cruce_ox2(padre_1, padre_2, matriz)
        else:
            if tiempo_expirado(inicio, parada_tiempo):
                break
            hijo_1, hijo_2 = cruce_moc(padre_1, padre_2, matriz)
        if random.random() < porcent_mutacion:
            hijo_1 = mutacion_2opt(hijo_1, matriz)
        if random.random() < porcent_mutacion:
            hijo_2 = mutacion_2opt(hijo_2, matriz)

        if tiempo_expirado(inicio, parada_tiempo):
            break 
        perdedor = torneo_perdedores(poblacion, k_worst_est)
        poblacion.remove(perdedor)
        dist1 = perdedor[1]
        perdedor = torneo_perdedores(poblacion, k_worst_est)
        poblacion.remove(perdedor)
        dist2 = perdedor[1]
        logger.info(f"Se han eliminado a los perdedores, que tenian una distancia de: {dist1} y {dist2}")
        poblacion.append(hijo_1)
        poblacion.append(hijo_2)
        logger.info(f"Se añaden los hijos con distancias de: {hijo_1[1]}, {hijo_2[1]}")
        evaluaciones += 2

        mejor_solucion = min(poblacion, key=lambda x: x[1])
        logger.info(f"EVALUACION {evaluaciones} La mejor solucion encontrada hasta el momento es de: {mejor_solucion[1]}") if evaluaciones % 2000 == 0 else None

        #print("Iteración:", evaluaciones, "Mejor distancia:", mejor_solucion[1]) if evaluaciones % 1000 == 0 else None
    logger.info(f"Total de evaluaciones: {evaluaciones}")

    final = time.time()
    logger.info(f"Total en ejecutar: {final - inicio}")
    print("Total en ejecutar: ", final - inicio)
    return mejor_solucion[0], mejor_solucion[1]
