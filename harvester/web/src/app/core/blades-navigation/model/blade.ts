import {EventEmitter, Type} from '@angular/core';
import {BladeMeta} from "./blade-meta";

export class Blade<T> {
    id: number;
    component: Type<T>;
    meta: BladeMeta;

    visible: boolean = true;

    componentCreation: EventEmitter<T> = new EventEmitter<T>();

    constructor(id: number, component: Type<T>, meta: BladeMeta) {
        this.id = id;
        this.component = component;
        this.meta = meta;
    }

}
