import axios from 'axios';

// 1. Creamos una instancia de Axios
const api = axios.create({
  baseURL: 'http://localhost:8082/api', // <-- Al core-service
});

// 2. Creamos un "Interceptor"
// (Esto se ejecuta ANTES de cada petición)
api.interceptors.request.use(
  (config) => {
    // 3. Obtenemos el token de localStorage
    const token = localStorage.getItem('token');
    
    // 4. Si el token existe, lo añadimos a la cabecera
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api;