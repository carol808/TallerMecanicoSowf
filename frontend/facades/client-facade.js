/** Fachada Cliente: aplica permisos de interfaz antes de llamar al Repository REST. */
export class ClientFacade {
  constructor(repository, authFacade) { this.repository = repository; this.authFacade = authFacade; }
  verify(session) { if (!this.authFacade.hasAnyRole(session, ['ADMINISTRADOR', 'SECRETARIA'])) throw new Error('No tienes autorización para administrar clientes.'); }
  validatePhoto(photo, required=true) { if (!photo && required) throw new Error('La fotografía es obligatoria.'); if (!photo) return; if (photo.size > 15 * 1024 * 1024) throw new Error('La fotografía excede el límite de 15 MB.'); if (!['image/jpeg', 'image/png', 'image/webp'].includes(photo.type)) throw new Error('Formato de fotografía no permitido. Usa JPG, PNG o WEBP.'); }
  register(form, photo, session) { this.verify(session); this.validatePhoto(photo); return this.repository.create(form, photo, this.authFacade.authorization); }
  update(id,form,photo,session){this.verify(session);this.validatePhoto(photo,false);return this.repository.update(id,form,photo,this.authFacade.authorization);}
  list(workshopId,page,direction,session){this.verify(session);return this.repository.list(workshopId,page,direction,this.authFacade.authorization);}
  transfer(id,workshopId,session){this.verify(session);return this.repository.transfer(id,workshopId,this.authFacade.authorization);}
  suspend(id,session){if(!this.authFacade.hasAnyRole(session,['ADMINISTRADOR']))throw new Error('Solo un administrador puede suspender clientes.');return this.repository.suspend(id,this.authFacade.authorization);}
}
