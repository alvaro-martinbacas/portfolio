import sys
from AlgEST_Clase3C_Grupo05 import *
from AlgGEN_Clase3C_Grupo05 import *
from AlgGRE_Clase3C_Grupo05 import *

id_ejecucion = uuid.uuid4()
logging.basicConfig(
    filename='registro.log',
    level=logging.INFO,
    format='%(asctime)s - %(id)s - %(levelname)s - %(message)s'
)

class ContextFilter(logging.Filter):
    def filter(self, record):
        record.id = id_ejecucion
        return True
    
logger = logging.getLogger()
logger.addFilter(ContextFilter())

#se lee la config del archivo
configuracion = leer_configuracion('config.txt')

# obtener los valores de configuración
archivos = configuracion['Archivos'].split()
semillas = list(map(int, configuracion['Semillas'].split()))
algoritmos = configuracion['Algoritmos'].split()
tipo_cruce = configuracion['tipo_cruce'].split()
k_best_gen = list(map(int, configuracion['k_best_gen'].split()))
k_best_est = int(configuracion['k_best_est'])
n_max_elites = list(map(int, configuracion['numero_max_elites'].split()))
m_poblacion = int(configuracion['individuos_iniciales'])
k_worst_gen = configuracion['k_worst_gen']
k_worst_est = configuracion['k_worst_est']
def obtener_ruta_actual(ruta_relativa):
    if getattr(sys, 'frozen', False):
        base_path = sys._MEIPASS
    else:
        base_path = os.path.dirname(os.path.abspath(__file__))
    return os.path.join(base_path, ruta_relativa)


def obtener_ruta_resources():
    if getattr(sys, 'frozen', False):
        ruta_base = os.path.dirname(sys.executable)
        ruta_resources = os.path.join(ruta_base, './resources/')

    else:
        ruta_base = os.path.dirname(os.path.abspath(__file__))
        ruta_resources = os.path.join(ruta_base, 'resources/')


    return ruta_resources


algoritmos_dict = {
    "generacional": generacional,
    "estacionario": estacionario
}


ruta_resources = obtener_ruta_resources()
#print(f"Ruta a la carpeta resources: {ruta_resources}")

ejec = 0

for alg in algoritmos:
    algoritmo = algoritmos_dict[alg]
    
    if algoritmo == generacional:
        for archivo_nombre in archivos:
            archivo_a_ejecutar = obtener_ruta_actual(ruta_resources + archivo_nombre)
            coordenadas = leer_coordenadas(archivo_a_ejecutar)
            matriz_dist = construir_matriz_distancias(coordenadas)
            for semilla in semillas:
                for cruce in tipo_cruce:
                    for n_elites in n_max_elites:
                        for k_best in k_best_gen:
                            ejec += 1
                            random.seed(semilla)
                            np.random.seed(semilla)
                            sol, dist = generacional(matriz_dist, cruce, k_best, n_elites)
                            logger.info(f"EJEC {ejec} GEN. FILE = {archivo_nombre} SEED = {semilla} CRUCE = {cruce} M = {m_poblacion} E = {n_elites} kBest = {k_best} kWorst = {k_worst_gen}")
                            logger.info(f"Solución: {dist}")
                            print(f"EJEC {ejec} GEN. FILE = {archivo_nombre} SEED = {semilla} CRUCE = {cruce} M = {m_poblacion} E = {n_elites} kBest = {k_best} kWorst = {k_worst_gen}")
                            print(f"Distancia: {dist}")
    
    if algoritmo == estacionario:
        for archivo_nombre in archivos:
            archivo_a_ejecutar = obtener_ruta_actual(ruta_resources + archivo_nombre)
            coordenadas = leer_coordenadas(archivo_a_ejecutar)
            matriz_dist = construir_matriz_distancias(coordenadas)
            for semilla in semillas:
                for cruce in tipo_cruce:
                    ejec += 1
                    random.seed(semilla)
                    np.random.seed(semilla)
                    sol, dist = estacionario(matriz_dist, cruce)
                    logger.info(f"EJEC {ejec} EST. FILE = {archivo_nombre} SEED = {semilla} CRUCE = {cruce} M = {m_poblacion} kBest = {k_best_est} kWorst = {k_worst_est}")
                    logger.info(f"Solución: {dist}")
                    print(f"EJEC {ejec} EST. FILE = {archivo_nombre} SEED = {semilla} CRUCE = {cruce} M = {m_poblacion} kBest = {k_best_est} kWorst = {k_worst_est}")
                    print(f"Distancia: {dist}")
