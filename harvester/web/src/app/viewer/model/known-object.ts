export class KnownObject {
    private _id: number;
    private _resource: string;
    private _player: boolean;
    private _doorway: boolean;
    private _heap: boolean;
    private _box: boolean;

    get id(): number {
        return this._id;
    }
    set id(value: number) {
        this._id = value;
    }

    get resource(): string {
        return this._resource;
    }
    set resource(value: string) {
        this._resource = value;
    }

    get player(): boolean {
        return this._player;
    }
    set player(value: boolean) {
        this._player = value;
    }

    get doorway(): boolean {
        return this._doorway;
    }
    set doorway(value: boolean) {
        this._doorway = value;
    }

    get heap(): boolean {
        return this._heap;
    }
    set heap(value: boolean) {
        this._heap = value;
    }

    get box(): boolean {
        return this._box;
    }
    set box(value: boolean) {
        this._box = value;
    }
}