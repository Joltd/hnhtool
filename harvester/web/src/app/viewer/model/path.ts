import {Position} from "./components";

export class Path {
    private _edges: Edge[] = [];

    get edges(): Edge[] {
        return this._edges;
    }
    set edges(value: Edge[]) {
        this._edges = value;
    }
}

export class Edge {
    private _id: number;
    private _from: Position;
    private _to: Position;

    get id(): number {
        return this._id;
    }
    set id(value: number) {
        this._id = value;
    }

    get from(): Position {
        return this._from;
    }
    set from(value: Position) {
        this._from = value;
    }

    get to(): Position {
        return this._to;
    }
    set to(value: Position) {
        this._to = value;
    }
}