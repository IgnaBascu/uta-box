/**
 * Archivo central de reglas de validación para Vuetify.
 * Una regla es simplemente una función que devuelve `true` o un `string` de error.
 */

// Regla: El campo debe tener X mínimo de longitud.
export const minLength = (length) => {
  // Devuelve una *función* de regla
  return (value) => {
    return (value && value.length >= length) || `Debe tener al menos ${length} caracteres`;
  };
};

// Regla: El campo no puede estar vacío
export const required = (value) => {
  // El truco de '!!' convierte el valor (incluso un número 0) a booleano
  return !!value || 'Este campo no puede estar vacío';
};

// Regla: Debe ser un email válido (con una regex)
export const email = (value) => {
  const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return pattern.test(value) || 'Debe ser un email válido';
};

// Regla: Validación del password
export const passwordStrength = (value) => {
  if (!value) return 'Este campo es requerido';
  
  const hasUpperCase = /[A-Z]/.test(value);
  const hasLowerCase = /[a-z]/.test(value);
  const hasNumber = /[0-9]/.test(value);
  
  if (value.length < 8) return 'Debe tener al menos 8 caracteres.';
  if (!hasUpperCase) return 'Debe incluir al menos una mayúscula (A-Z).';
  if (!hasLowerCase) return 'Debe incluir al menos una minúscula (a-z).';
  if (!hasNumber) return 'Debe incluir al menos un número (0-9).';
 
  
  return true; // Pasa todas las validaciones
};

