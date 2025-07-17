import os

def listar_archivos(carpeta):
    archivos = os.listdir(carpeta)
    return archivos


def leer_coordenadas(archivo):
    coordenadas = []

    with open(archivo, 'r') as f:
        leer_datos = False
        for linea in f:
            if "NODE_COORD_SECTION" in linea:
                leer_datos = True
                continue

            if "EOF" in linea:
                break

            if leer_datos:
                partes = linea.split()
                if len(partes) == 3:
                    x = float(partes[1])
                    y = float(partes[2])
                    coordenadas.append((x, y))

    return coordenadas
#Mostrar detalles del problema que se va a ejecutar
def extraer_detalles_archivo(archivo):
    detalles = {}
    with open(archivo, 'r') as f:
        for linea in f:
            if linea.startswith('NAME'):
                detalles['NAME'] = linea.split(':')[1].strip()
            elif linea.startswith('COMMENT'):
                detalles['COMMENT'] = linea.split(':')[1].strip()
            elif linea.startswith('TYPE'):
                detalles['TYPE'] = linea.split(':')[1].strip()
            elif linea.startswith('DIMENSION'):
                detalles['DIMENSION'] = int(linea.split(':')[1].strip())
            elif linea.startswith('EDGE_WEIGHT_TYPE'):
                detalles['EDGE_WEIGHT_TYPE'] = linea.split(':')[1].strip()
    return detalles   

def leer_configuracion(archivo_config):
    configuracion = {}
    with open(archivo_config, 'r') as f:
        for linea in f:
            clave, valor = linea.strip().split('=')
            configuracion[clave] = valor
    return configuracion

