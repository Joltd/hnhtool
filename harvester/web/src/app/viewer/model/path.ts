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
    private _from: Position;
    private _to: Position;

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