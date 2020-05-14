import {Entity} from "../model/entity";

export class EntityService {

    private _generatedId: number = 1;

    private _entities: Entity[] = [];

    public entities(): Entity[] {
        return this._entities;
    }

    public createEntity(): Entity {
        let entity = new Entity(this._generatedId++);
        this._entities.push(entity);
        return entity;
    }

    // dispose entity

}