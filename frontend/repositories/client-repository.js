/** Repository REST para el ciclo de vida de clientes. */
export class ClientRepository {
  constructor(api) { this.api = api; }
  multipart(client, photo) { const form=new FormData(); form.append('client',new Blob([JSON.stringify(client)],{type:'application/json'})); if(photo)form.append('photo',photo); return form; }
  create(client,photo,authorization){return this.api.request('/clients',{method:'POST',body:this.multipart(client,photo),authorization});}
  update(id,client,photo,authorization){return this.api.request(`/clients/${id}`,{method:'PUT',body:this.multipart(client,photo),authorization});}
  list(workshopId,page,direction,authorization){return this.api.request(`/clients?workshopId=${encodeURIComponent(workshopId)}&page=${page}&direction=${direction}`,{authorization});}
  transfer(id,workshopId,authorization){return this.api.request(`/clients/${id}/workshop?workshopId=${encodeURIComponent(workshopId)}`,{method:'PATCH',authorization});}
  suspend(id,authorization){return this.api.request(`/clients/${id}/suspend`,{method:'PATCH',authorization});}
}
