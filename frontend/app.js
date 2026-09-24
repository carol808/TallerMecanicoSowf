const { createApp } = Vue;
createApp({
  data() { return { authenticated: false, showRegister: false, loading: false, loginError: '', loginForm: {email:'',password:''}, registerForm: {name:'',email:'',password:'',role:'ADMINISTRADOR'}, active: 'Inicio', showReception: false, gainPeriod: 'Semanal', menu: [
    {name:'Inicio',icon:'bi bi-grid-1x2-fill'}, {name:'Órdenes de trabajo',icon:'bi bi-clipboard2-check',badge:9}, {name:'Clientes',icon:'bi bi-people'}, {name:'Inventario',icon:'bi bi-box-seam'}, {name:'Cotizaciones',icon:'bi bi-receipt'}, {name:'Ganancias',icon:'bi bi-graph-up-arrow'}, {name:'Usuarios y roles',icon:'bi bi-shield-lock'}
  ], orders: [
    {time:'09:00',client:'Carlos Ramírez',car:'Mazda 3 2021',plate:'RFG-219-B',service:'Afinación mayor',mechanic:'Miguel Torres',initials:'MT',status:'En proceso',statusClass:'progress',color:'blue'},
    {time:'10:30',client:'Laura Hernández',car:'Kia Rio 2020',plate:'KDM-882-C',service:'Cambio de frenos',mechanic:'Jorge López',initials:'JL',status:'En espera',statusClass:'waiting',color:'orange'},
    {time:'12:00',client:'Roberto Díaz',car:'Nissan Versa 2019',plate:'DTR-105-A',service:'Diagnóstico eléctrico',mechanic:'Miguel Torres',initials:'MT',status:'En proceso',statusClass:'progress',color:'violet'},
    {time:'15:30',client:'Sofía García',car:'VW Jetta 2022',plate:'SFG-760-D',service:'Servicio de aceite',mechanic:'Ana Silva',initials:'AS',status:'Listo',statusClass:'ready',color:'green'}
  ], team: [
    {name:'Miguel Torres',initials:'MT',task:'2 vehículos asignados',available:false,color:'blue'}, {name:'Jorge López',initials:'JL',task:'1 vehículo asignado',available:false,color:'orange'}, {name:'Ana Silva',initials:'AS',task:'Sin asignaciones',available:true,color:'violet'}
  ], parts: [
    {name:'Aceite sintético 5W-30',code:'LUB-5W30-01',qty:24,unit:'pzas.',icon:'bi bi-droplet-half'}, {name:'Pastillas de freno',code:'FRN-DEL-18',qty:3,unit:'juegos',low:true,icon:'bi bi-disc'}, {name:'Filtro de aceite',code:'FIL-AC-22',qty:18,unit:'pzas.',icon:'bi bi-funnel'}
  ], bars: [{day:'Lun',value:52},{day:'Mar',value:74},{day:'Mié',value:45},{day:'Jue',value:88},{day:'Vie',value:67},{day:'Sáb',value:92},{day:'Dom',value:28}] }; },
  computed: { gainValue(){ return {Semanal:'$78,640',Quincenal:'$152,980',Mensual:'$304,560'}[this.gainPeriod]; } },
  methods: { async login(){this.loading=true;this.loginError='';try{const authorization='Basic '+btoa(`${this.loginForm.email}:${this.loginForm.password}`);const response=await fetch('http://localhost:8082/api/dashboard',{headers:{Authorization:authorization}});if(!response.ok)throw new Error();this.authenticated=true;}catch{this.loginError='Credenciales inválidas o la API no está disponible.';}finally{this.loading=false;}}, async register(){this.loading=true;this.loginError='';try{const response=await fetch('http://localhost:8082/api/auth/register',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(this.registerForm)});if(!response.ok){const result=await response.json();throw new Error(result.message);}this.loginForm.email=this.registerForm.email;this.loginForm.password=this.registerForm.password;this.showRegister=false;await this.login();}catch(error){this.loginError=error.message||'No se pudo crear el usuario.';}finally{this.loading=false;}} }
}).mount('#app');
