import { ref } from 'vue';

export function useGimnasioClasesUsuarios() {
  const gimnasios = ref([]);
  const clases = ref([]);
  const usuarios = ref([]);

  async function cargarGimnasios() {
    const token = localStorage.getItem('token');
    const res = await fetch('http://localhost:8080/gestiongimnasios/gimnasios/todos', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    gimnasios.value = res.ok ? await res.json() : [];
  }

  async function cargarClases(idGimnasio) {
    if (!idGimnasio) { clases.value = []; return; }
    const token = localStorage.getItem('token');
    const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}/clasescolectivas`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    clases.value = res.ok ? await res.json() : [];
  }

  async function cargarUsuarios(idGimnasio) {
    if (!idGimnasio) { usuarios.value = []; return; }
    const token = localStorage.getItem('token');
    const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}/usuarios`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    usuarios.value = res.ok ? await res.json() : [];
  }

  return { gimnasios, clases, usuarios, cargarGimnasios, cargarClases, cargarUsuarios };
}
