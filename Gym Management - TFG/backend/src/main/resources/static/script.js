document.addEventListener('DOMContentLoaded', function() {
    fetch('http://localhost:8080/')
        .then(response => response.text())
        .then(data => {
            document.getElementById('mensaje').textContent = data;
        })
        .catch(error => {
            console.error('Error al obtener el saludo:', error);
            document.getElementById('mensaje').textContent = 'Error al cargar el mensaje.';
        });
});