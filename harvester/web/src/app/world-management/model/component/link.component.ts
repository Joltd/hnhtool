import {Entity} from "../entity";

export class LinkComponent {

    public readonly LINK = 'LINK';

    private _entities: Entity[] = [];

    public add(entity: Entity) {
        this._entities.push(entity)
    }

    public remove(id: number) {
        this._entities = this._entities.filter(entity => entity.id != id);
    }

}