package com.tfg.gestion_gimnasios.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class HorarioFueraRangoGimnasio extends RuntimeException {
}
