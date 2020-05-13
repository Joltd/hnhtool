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

    public addComponent<T extends any>(component: T, type?: Type<T>) {
        this.components.set(component.constructor, component);
        if (type) {
            this.components.set(type, component);
        }
    }

    public removeComponent<T extends any>(type: Type<T>) {
        this.components.delete(type);
    }

    public getComponent<T extends any>(type: Type<T>): T {
        return <T> this.components.get(type);
    }

}
