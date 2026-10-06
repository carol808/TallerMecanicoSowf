import { createApp } from 'https://unpkg.com/vue@3/dist/vue.esm-browser.prod.js';
import { ApiClient } from './services/api-client.js';
import { AuthRepository } from './repositories/auth-repository.js';
import { ClientRepository } from './repositories/client-repository.js';
import { WorkshopRepository } from './repositories/workshop-repository.js';
import { AuthFacade } from './facades/auth-facade.js';
import { ClientFacade } from './facades/client-facade.js';
import { WorkshopFacade } from './facades/workshop-facade.js';

const api=new ApiClient(), authFacade=new AuthFacade(new AuthRepository(api));
const clientFacade=new ClientFacade(new ClientRepository(api),authFacade), workshopFacade=new WorkshopFacade(new WorkshopRepository(api),authFacade);
const emptyAddress=()=>({street:'',neighborhood:'',municipality:'',state:'',postalCode:''});
const emptyClient=()=>({firstName:'',paternalLastName:'',maternalLastName:'',curp:'',rfc:'',birthDate:'',age:null,email:'',personalPhone:'',contactPhone:'',workPhone:'',workshopId:'',address:emptyAddress()});
const emptyWorkshop=()=>({name:'',street:'',neighborhood:'',municipality:'',state:'',postalCode:'',businessName:'',phone:'',rfc:'',email:''});

createApp({
  data(){return {authenticated:false,session:null,loading:false,loginError:'',toast:'',active:'Registro de clientes',showRegister:false,showRecovery:false,recoveryEmail:'',recoveryMessage:'',loginForm:{email:'',password:''},registerForm:{name:'',email:'',password:'',role:'SECRETARIA'},workshops:[],showWorkshop:false,workshopForm:emptyWorkshop(),workshopPhoto:null,workshopError:'',showClient:false,editingId:null,clientForm:emptyClient(),clientPhoto:null,clientPhotoName:'',clientError:'',clients:[],selectedWorkshop:'',page:0,totalPages:0,direction:'asc',listError:'',transferWorkshop:{}};},
  computed:{canManageClients(){return authFacade.hasAnyRole(this.session,['ADMINISTRADOR','SECRETARIA']);},isAdmin(){return workshopFacade.canManage(this.session);}},
  methods:{
    async login(){this.loading=true;this.loginError='';try{this.session=await authFacade.login(this.loginForm.email,this.loginForm.password);this.authenticated=true;await this.loadWorkshops();}catch(e){this.loginError=e.message||'Credenciales inválidas.';}finally{this.loading=false;}},
    async registerUser(){this.loading=true;this.loginError='';try{await authFacade.register(this.registerForm);this.loginForm={email:this.registerForm.email,password:this.registerForm.password};this.showRegister=false;await this.login();}catch(e){this.loginError=e.message||'No se pudo crear el usuario.';}finally{this.loading=false;}},
    async requestRecovery(){this.loading=true;this.recoveryMessage='';try{const r=await authFacade.recovery(this.recoveryEmail);this.recoveryMessage=r.message;}catch(e){this.recoveryMessage=e.message;}finally{this.loading=false;}},
    logout(){authFacade.logout();this.authenticated=false;this.session=null;this.loginForm.password='';this.active='Registro de clientes';this.clients=[];this.workshops=[];},
    async loadWorkshops(){this.workshops=await workshopFacade.list();if(!this.selectedWorkshop&&this.workshops[0])this.selectedWorkshop=this.workshops[0].id;},
    openClient(){this.clientError='';this.editingId=null;this.clientForm=emptyClient();this.clientForm.workshopId=this.selectedWorkshop||'';this.clientPhoto=null;this.clientPhotoName='';this.showClient=true;},
    editClient(client){this.editingId=client.id;this.clientForm=JSON.parse(JSON.stringify(client));this.clientPhoto=null;this.clientPhotoName='';this.clientError='';this.showClient=true;},
    ageFromBirth(){if(!this.clientForm.birthDate)return;const b=new Date(`${this.clientForm.birthDate}T00:00:00`),n=new Date();let age=n.getFullYear()-b.getFullYear();if(n<new Date(n.getFullYear(),b.getMonth(),b.getDate()))age--;this.clientForm.age=age;},
    onClientPhoto(e){this.clientPhoto=e.target.files[0]||null;this.clientPhotoName=this.clientPhoto?.name||'';}, onWorkshopPhoto(e){this.workshopPhoto=e.target.files[0]||null;},
    async lookupAddress(target){const address=target==='client'?this.clientForm.address:this.workshopForm;if(address.postalCode?.length!==5)return;try{const result=await workshopFacade.postal(address.postalCode);address.state=result.state;address.municipality=result.municipality;if(result.neighborhoods.length===1)address.neighborhood=result.neighborhoods[0];}catch(e){this.clientError=target==='client'?e.message:this.clientError;}},
    async reverseAddress(target){const address=target==='client'?this.clientForm.address:this.workshopForm;if(!address.state||!address.municipality||!address.neighborhood)return;try{address.postalCode=(await workshopFacade.reverse(address)).postalCode;}catch(e){/* la validación final corresponde al backend */}},
    async saveClient(){this.loading=true;this.clientError='';try{const r=this.editingId?await clientFacade.update(this.editingId,this.clientForm,this.clientPhoto,this.session):await clientFacade.register(this.clientForm,this.clientPhoto,this.session);this.toast=this.editingId?'Cliente actualizado':'Cliente guardado';this.showClient=false;if(this.active==='Clientes registrados')await this.loadClients();return r;}catch(e){this.clientError=e.message||'No fue posible guardar el cliente.';}finally{this.loading=false;}},
    async saveWorkshop(){this.loading=true;this.workshopError='';try{const w=await workshopFacade.create(this.workshopForm,this.workshopPhoto,this.session);this.workshops.push(w);this.selectedWorkshop=w.id;this.showWorkshop=false;this.workshopForm=emptyWorkshop();this.workshopPhoto=null;this.toast='Taller guardado';}catch(e){this.workshopError=e.message||'No fue posible guardar el taller.';}finally{this.loading=false;}},
    async loadClients(reset=false){if(reset)this.page=0;if(!this.selectedWorkshop){this.clients=[];return;}this.loading=true;this.listError='';try{const r=await clientFacade.list(this.selectedWorkshop,this.page,this.direction,this.session);this.clients=r.content;this.totalPages=r.totalPages;}catch(e){this.listError=e.message;}finally{this.loading=false;}},
    async changeWorkshop(id){try{await clientFacade.transfer(id,this.transferWorkshop[id],this.session);this.toast='Cliente transferido';await this.loadClients();}catch(e){this.listError=e.message;}},
    async suspend(id){if(!confirm('¿Suspender este cliente?'))return;try{await clientFacade.suspend(id,this.session);this.toast='Cliente suspendido';await this.loadClients();}catch(e){this.listError=e.message;}},
    async changeSection(section){this.active=section;if(section==='Clientes registrados')await this.loadClients(true);}
  }
}).mount('#app');
