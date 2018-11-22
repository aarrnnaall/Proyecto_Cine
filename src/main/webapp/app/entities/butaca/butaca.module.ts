import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ProyectoCineSharedModule } from 'app/shared';
import {
    ButacaComponent,
    ButacaDetailComponent,
    ButacaUpdateComponent,
    ButacaDeletePopupComponent,
    ButacaDeleteDialogComponent,
    butacaRoute,
    butacaPopupRoute
} from './';

const ENTITY_STATES = [...butacaRoute, ...butacaPopupRoute];

@NgModule({
    imports: [ProyectoCineSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [ButacaComponent, ButacaDetailComponent, ButacaUpdateComponent, ButacaDeleteDialogComponent, ButacaDeletePopupComponent],
    entryComponents: [ButacaComponent, ButacaUpdateComponent, ButacaDeleteDialogComponent, ButacaDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProyectoCineButacaModule {}
