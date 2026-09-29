/** Repository REST para crear clientes a través de un request multipart. */
export class ClientRepository {
  constructor(api) { this.api = api; }
  create(client, photo, authorization) {
    const form = new FormData();
    form.append('client', new Blob([JSON.stringify(client)], { type: 'application/json' }));
    form.append('photo', photo);
    return this.api.request('/clients', { method: 'POST', body: form, authorization });
  }
}
