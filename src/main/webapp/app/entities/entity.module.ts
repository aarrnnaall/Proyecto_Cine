import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { ProyectoCineClienteModule } from './cliente/cliente.module';
import { ProyectoCineTicketModule } from './ticket/ticket.module';
import { ProyectoCineEntradaModule } from './entrada/entrada.module';
import { ProyectoCineOcupacionModule } from './ocupacion/ocupacion.module';
import { ProyectoCineButacaModule } from './butaca/butaca.module';
import { ProyectoCineSalaModule } from './sala/sala.module';
import { ProyectoCineFuncionModule } from './funcion/funcion.module';
import { ProyectoCinePeliculaModule } from './pelicula/pelicula.module';
import { ProyectoCineCalificacionModule } from './calificacion/calificacion.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        ProyectoCineClienteModule,
        ProyectoCineTicketModule,
        ProyectoCineEntradaModule,
        ProyectoCineOcupacionModule,
        ProyectoCineButacaModule,
        ProyectoCineSalaModule,
        ProyectoCineFuncionModule,
        ProyectoCinePeliculaModule,
        ProyectoCineCalificacionModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProyectoCineEntityModule {}
