import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ProyectoCineSharedModule } from 'app/shared';
import {
    FuncionComponent,
    FuncionDetailComponent,
    FuncionUpdateComponent,
    FuncionDeletePopupComponent,
    FuncionDeleteDialogComponent,
    funcionRoute,
    funcionPopupRoute
} from './';

const ENTITY_STATES = [...funcionRoute, ...funcionPopupRoute];

@NgModule({
    imports: [ProyectoCineSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        FuncionComponent,
        FuncionDetailComponent,
        FuncionUpdateComponent,
        FuncionDeleteDialogComponent,
        FuncionDeletePopupComponent
    ],
    entryComponents: [FuncionComponent, FuncionUpdateComponent, FuncionDeleteDialogComponent, FuncionDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProyectoCineFuncionModule {}
