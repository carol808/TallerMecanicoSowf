/** Cliente HTTP central: conserva detalles REST y convierte errores de API en mensajes para la vista. */
export class ApiClient {
  constructor(baseUrl = 'http://localhost:8082/api') { this.baseUrl = baseUrl; }
  async request(path, { method = 'GET', headers = {}, body, authorization } = {}) {
    let response;
    try {
      response = await fetch(`${this.baseUrl}${path}`, { method, headers: { ...headers, ...(authorization ? { Authorization: authorization } : {}) }, body });
    } catch {
      if (window.location.protocol === 'file:') {
        throw new Error('La aplicación no puede abrirse desde file://. Iníciala en http://localhost:4173/.');
      }
      throw new Error('No fue posible conectar con la API. Verifica que Spring Boot esté ejecutándose en http://localhost:8082.');
    }
    const payload = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(payload.message || payload.error || `La API respondió con estado ${response.status}.`);
    return payload;
  }
}
