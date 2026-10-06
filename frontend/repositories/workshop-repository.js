/** Repository REST de talleres. */
export class WorkshopRepository {
  constructor(api){this.api=api;}
  list(authorization){return this.api.request('/workshops',{authorization});}
  create(workshop,photo,authorization){const form=new FormData();form.append('workshop',new Blob([JSON.stringify(workshop)],{type:'application/json'}));form.append('photo',photo);return this.api.request('/workshops',{method:'POST',body:form,authorization});}
  postal(code,authorization){return this.api.request(`/postal-codes/${encodeURIComponent(code)}`,{authorization});}
  reverse(address,authorization){const query=new URLSearchParams(address);return this.api.request(`/postal-codes?${query}`,{authorization});}
}
