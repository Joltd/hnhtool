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
    blade: Blade;

    @ViewChild('container', {read: ViewContainerRef, static: true})
    container: ViewContainerRef;

    private componentRef: ComponentRef<any>;

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {}

    ngOnInit(): void {
        let componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.blade.component);
        this.componentRef = this.container.createComponent(componentFactory);
        this.componentRef.instance.bladeId = this.blade.id;
    }

    ngOnDestroy(): void {
        this.componentRef.destroy();
        this.componentRef = null;
    }

}
