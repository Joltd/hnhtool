import {Component} from "./component";
import {Type} from "@angular/core";

export class Entity {
    private components: Map<string,Component> = new Map<string, Component>();

    public addComponent(component: Component) {
        this.components.set(component.constructor.name, component);
    }

    public removeComponent(component: Component) {
        this.components.delete(component.constructor.name);
    }

    public getComponent<T extends Component>(type: Type<T>): T {
        return <T> this.components.get(type.name);
    }

    public hasComponent<T extends Component>(type: Type<T>): boolean {
        return this.components.has(type.name);
    }

    public getComponents(): IterableIterator<Component> {
        return this.components.values();
    }
}
