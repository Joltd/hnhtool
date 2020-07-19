import {Type} from "@angular/core";

export class Entity {

    private readonly _id: number;
    private components: Map<Type<any>, any> = new Map<Type<any>, any>();

    constructor(id: number) {
        this._id = id;
    }

    get id(): number {
        return this._id;
    }

    add<T extends any>(component: T): T {
        this.components.set(component.constructor, component);
        return component;
    }

    remove<T extends any>(type: Type<T>) {
        this.components.delete(type);
    }

    get<T extends any>(type: Type<T>): T {
        return <T> this.components.get(type);
    }

    has<T extends any>(type: Type<T>): boolean {
        return this.components.has(type);
    }

}