/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ProyectoCineTestModule } from '../../../test.module';
import { FuncionDeleteDialogComponent } from 'app/entities/funcion/funcion-delete-dialog.component';
import { FuncionService } from 'app/entities/funcion/funcion.service';

describe('Component Tests', () => {
    describe('Funcion Management Delete Component', () => {
        let comp: FuncionDeleteDialogComponent;
        let fixture: ComponentFixture<FuncionDeleteDialogComponent>;
        let service: FuncionService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ProyectoCineTestModule],
                declarations: [FuncionDeleteDialogComponent]
            })
                .overrideTemplate(FuncionDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(FuncionDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(FuncionService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
