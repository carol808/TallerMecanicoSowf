/** Fachada para el catálogo postal y la administración de talleres. */
export class WorkshopFacade {
  constructor(repository,auth){this.repository=repository;this.auth=auth;}
  canManage(session){return this.auth.hasAnyRole(session,['ADMINISTRADOR']);}
  list(){return this.repository.list(this.auth.authorization);}
  postal(code){return this.repository.postal(code,this.auth.authorization);}
  reverse(address){return this.repository.reverse(address,this.auth.authorization);}
  create(data,photo,session){if(!this.canManage(session))throw new Error('Solo un administrador puede registrar talleres.');if(!photo)throw new Error('La fotografía es obligatoria.');if(photo.size>15*1024*1024)throw new Error('La fotografía excede el límite de 15 MB.');return this.repository.create(data,photo,this.auth.authorization);}
}
