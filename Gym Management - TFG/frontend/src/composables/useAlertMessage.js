import { ref } from 'vue';

const mensajeAlerta = ref({ texto: '', tipo: '' });

export function useAlertMessage() {
    const mostrarMensaje = (texto, tipo) => {
        mensajeAlerta.value = { texto, tipo };
        setTimeout(() => {
            mensajeAlerta.value = { texto: '', tipo: '' };
        }, 4000);
    };

    return {
        mensajeAlerta,
        mostrarMensaje
    };
}
