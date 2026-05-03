// Formatea hora a HH:mm (sin segundos)
export function formatearHora(hora) {
    if (!hora) return '';
    // Si ya está en formato HH:mm
    if (/^\d{2}:\d{2}$/.test(hora)) return hora;
    // Si viene como HH:mm:ss
    if (/^\d{2}:\d{2}:\d{2}$/.test(hora)) return hora.substring(0,5);
    // Si viene como string con fecha y hora
    const match = hora.match(/(\d{2}:\d{2})/);
    return match ? match[1] : hora;
}

export const diasSemana = {
    'MONDAY': 'Lunes',
    'TUESDAY': 'Martes',
    'WEDNESDAY': 'Miércoles',
    'THURSDAY': 'Jueves',
    'FRIDAY': 'Viernes',
    'SATURDAY': 'Sábado',
    'SUNDAY': 'Domingo'
};

export function formatearDiaSemana(dia) {
    return diasSemana[dia] || dia;
}
