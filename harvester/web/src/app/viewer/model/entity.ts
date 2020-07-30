import {Type} from "@angular/core";

export class Entity {

    private readonly _id: number;
    private components: Map<string, any> = new Map<string, any>();

    constructor(id: number) {
        this._id = id;
    }

    get id(): number {
        return this._id;
    }

    add<T extends any>(component: T): T {
        type A = typeof component;

        this.components.set(component.constructor.name, component);
        return component;
    }

    remove<T extends any>(type: Type<T>) {
        this.components.delete(type.name);
    }

    get<T extends any>(type: Type<T>): T {
        return <T> this.components.get(type.name);
    }

    has<T extends any>(type: Type<T>): boolean {
        return this.components.has(type.name);
    }

}