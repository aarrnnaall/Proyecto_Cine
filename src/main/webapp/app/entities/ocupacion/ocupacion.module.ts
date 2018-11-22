import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ProyectoCineSharedModule } from 'app/shared';
import {
    OcupacionComponent,
    OcupacionDetailComponent,
    OcupacionUpdateComponent,
    OcupacionDeletePopupComponent,
    OcupacionDeleteDialogComponent,
    ocupacionRoute,
    ocupacionPopupRoute
} from './';

const ENTITY_STATES = [...ocupacionRoute, ...ocupacionPopupRoute];

@NgModule({
    imports: [ProyectoCineSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        OcupacionComponent,
        OcupacionDetailComponent,
        OcupacionUpdateComponent,
        OcupacionDeleteDialogComponent,
        OcupacionDeletePopupComponent
    ],
    entryComponents: [OcupacionComponent, OcupacionUpdateComponent, OcupacionDeleteDialogComponent, OcupacionDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProyectoCineOcupacionModule {}
