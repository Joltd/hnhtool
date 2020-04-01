import {Type} from '@angular/core';

export class Blade {
    id: number;
    // requirements
    component: Type<any>;
    large: boolean;
    // manager guides
    visible: boolean;
    fill: boolean;

    constructor(id: number, component: Type<any>, large: boolean) {
        this.id = id;
        this.component = component;
        this.large = large;
    }
}
