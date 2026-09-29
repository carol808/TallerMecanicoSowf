/** Cliente HTTP central: conserva detalles REST y convierte errores de API en mensajes para la vista. */
export class ApiClient {
  constructor(baseUrl = 'http://localhost:8082/api') { this.baseUrl = baseUrl; }
  async request(path, { method = 'GET', headers = {}, body, authorization } = {}) {
    const response = await fetch(`${this.baseUrl}${path}`, { method, headers: { ...headers, ...(authorization ? { Authorization: authorization } : {}) }, body });
    const payload = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(payload.message || 'No fue posible completar la operación.');
    return payload;
  }
}
