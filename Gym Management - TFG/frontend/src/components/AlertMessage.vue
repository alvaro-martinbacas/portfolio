<template>
    <Transition name="fade">
        <div v-if="mensajeAlerta.texto" 
             :class="['alert', `alert-${mensajeAlerta.tipo}`, 'text-center']" 
             role="alert"
             style="position: fixed; top: 20px; left: 50%; transform: translateX(-50%); z-index: 9999; min-width: 300px; max-width: 600px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); border-radius: 8px;">
            {{ mensajeAlerta.texto }}
        </div>
    </Transition>
</template>

<script setup>
import { useAlertMessage } from '../composables/useAlertMessage';

const { mensajeAlerta } = useAlertMessage();
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.5s ease, transform 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
    transform: translate(-50%, -20px);
}

.fade-enter-to,
.fade-leave-from {
    opacity: 1;
    transform: translate(-50%, 0);
}


.alert {
  padding: 1rem 1.5rem;
  font-size: 1.1rem;
  font-weight: 500;
  border-radius: 8px;
  border: 1px solid #ddd;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  background: #fff;
  color: #222;
  margin-bottom: 1rem;
  opacity: 0.98;
}
.alert-success {
  background: #d4edda;
  color: #155724;
  border-color: #c3e6cb;
}
.alert-error,
.alert-danger {
  background: #f8d7da;
  color: #721c24;
  border-color: #f5c6cb;
}
.alert-warning {
  background: #fff3cd;
  color: #856404;
  border-color: #ffeeba;
}
.alert-info {
  background: #d1ecf1;
  color: #0c5460;
  border-color: #bee5eb;
}
</style>
