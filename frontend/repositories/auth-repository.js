/** Repository REST para autenticación. */
export class AuthRepository {
  constructor(api) { this.api = api; }
  me(authorization) { return this.api.request('/auth/me', { authorization }); }
  register(user) { return this.api.request('/auth/register', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(user) }); }
  recovery(email) { return this.api.request('/auth/recovery', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email }) }); }
}
