/** Fachada de autenticación: coordina la vista Vue y el Repository REST. */
export class AuthFacade {
  constructor(repository) { this.repository = repository; this.authorization = null; }
  async login(email, password) { this.authorization = `Basic ${btoa(`${email}:${password}`)}`; try { return await this.repository.me(this.authorization); } catch (error) { this.authorization = null; throw error; } }
  register(user) { return this.repository.register(user); }
  hasAnyRole(session, roles) { return Boolean(session?.roles?.some(role => roles.includes(role))); }
}
