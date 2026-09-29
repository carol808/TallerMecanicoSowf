import { createApp } from 'https://unpkg.com/vue@3/dist/vue.esm-browser.prod.js';
import { ApiClient } from './services/api-client.js';
import { AuthRepository } from './repositories/auth-repository.js';
import { ClientRepository } from './repositories/client-repository.js';
import { AuthFacade } from './facades/auth-facade.js';
import { ClientFacade } from './facades/client-facade.js';
const api=new ApiClient(), authFacade=new AuthFacade(new AuthRepository(api)), clientFacade=new ClientFacade(new ClientRepository(api),authFacade);
const emptyClient=()=>({fullName:'',alternateContactName:'',age:null,birthDate:'',personalPhone:'',workPhone:'',email:'',workEmail:'',address:{street:'',neighborhood:'',municipality:'',state:'',postalCode:''}});
createApp({
  data(){return{authenticated:false,session:null,showRegister:false,loading:false,loginError:'',toast:'',active:'Inicio',showClientForm:false,photo:null,photoName:'',clientError:'',clientForm:emptyClient(),loginForm:{email:'',password:''},registerForm:{name:'',email:'',password:'',role:'ADMINISTRADOR'},menu:[{name:'Inicio',icon:'bi bi-grid-1x2-fill'},{name:'Clientes',icon:'bi bi-people'},{name:'Órdenes de trabajo',icon:'bi bi-clipboard2-check'},{name:'Inventario',icon:'bi bi-box-seam'},{name:'Cotizaciones',icon:'bi bi-receipt'},{name:'Ganancias',icon:'bi bi-graph-up-arrow'},{name:'Usuarios y roles',icon:'bi bi-shield-lock'}]};},
  computed:{canRegisterClient(){return authFacade.hasAnyRole(this.session,['ADMINISTRADOR','SECRETARIA','RECEPCIONISTA']);}},
  methods:{
    /** Autentica con AuthFacade y conserva identidad/roles únicamente durante la sesión actual. */
    async login(){this.loading=true;this.loginError='';try{this.session=await authFacade.login(this.loginForm.email,this.loginForm.password);this.authenticated=true;}catch(error){this.loginError=error.message||'Credenciales inválidas o API no disponible.';}finally{this.loading=false;}},
    /** Registra usuario vía Facade y valida las nuevas credenciales mediante login. */
    async registerUser(){this.loading=true;this.loginError='';try{await authFacade.register(this.registerForm);this.loginForm={email:this.registerForm.email,password:this.registerForm.password};this.showRegister=false;await this.login();}catch(error){this.loginError=error.message||'No se pudo crear el usuario.';}finally{this.loading=false;}},
    /** Captura la fotografía escogida para la validación y envío multipart. */
    onPhotoChange(event){this.photo=event.target.files[0]||null;this.photoName=this.photo?.name||'';this.clientError='';},
    /** Guarda el cliente; muestra Cliente guardado solo tras respuesta exitosa del backend. */
    async saveClient(){this.loading=true;this.clientError='';try{const saved=await clientFacade.register(this.clientForm,this.photo,this.session);this.toast=saved.message;this.showClientForm=false;this.clientForm=emptyClient();this.photo=null;this.photoName='';setTimeout(()=>{this.toast='';},3500);}catch(error){this.clientError=error.message||'No fue posible guardar el cliente.';}finally{this.loading=false;}},
    /** Abre el formulario de clientes bajo la política de roles de fase 2. */
    openClientForm(){if(!this.canRegisterClient){this.clientError='No tienes autorización para registrar clientes.';return;}this.clientError='';this.showClientForm=true;}
  }
}).mount('#app');
