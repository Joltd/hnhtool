import {
    Component,
    ComponentFactoryResolver,
    ComponentRef,
    Input,
    OnDestroy,
    OnInit,
    ViewChild,
    ViewContainerRef
} from '@angular/core';
import {Blade} from "../../model/blade";

@Component({
    selector: 'blade',
    template: '<ng-container #container></ng-container>',
    styleUrls: ['blade.component.scss']
})
export class BladeComponent implements OnInit,OnDestroy {

    @Input()
    blade: Blade<any>;

    @ViewChild('container', {read: ViewContainerRef, static: true})
    container: ViewContainerRef;

    private componentRef: ComponentRef<any>;

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {}

    ngOnInit(): void {
        debugger
        let componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.blade.component);
        this.componentRef = this.container.createComponent(componentFactory);
        this.componentRef.instance.bladeId = this.blade.id;
        this.blade.componentCreation.emit(this.componentRef.instance);
        console.log('Blade init done');
    }

    ngOnDestroy(): void {
        this.componentRef.destroy();
        this.componentRef = null;
    }

}
