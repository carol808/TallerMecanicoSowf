/** Fachada Registrar Cliente: valida permisos y fotografía antes de delegar al Repository REST. */
export class ClientFacade {
  constructor(repository, authFacade) { this.repository = repository; this.authFacade = authFacade; }
  async register(form, photo, session) {
    if (!this.authFacade.hasAnyRole(session, ['ADMINISTRADOR', 'SECRETARIA', 'RECEPCIONISTA'])) throw new Error('No tienes autorización para registrar clientes.');
    if (!photo) throw new Error('La fotografía es obligatoria.');
    if (photo.size > 15 * 1024 * 1024) throw new Error('La fotografía excede el límite de 15 MB.');
    if (!['image/jpeg', 'image/png', 'image/webp'].includes(photo.type)) throw new Error('Formato de fotografía no permitido. Usa JPG, PNG o WEBP.');
    return this.repository.create(form, photo, this.authFacade.authorization);
  }
}
