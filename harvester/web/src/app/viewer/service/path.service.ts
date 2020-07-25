import {Injectable} from "@angular/core";
import {Edge, Path} from "../model/path";
import {HttpClient} from "@angular/common/http";
import {ViewerService} from "./viewer.service";
import {Entity} from "../model/entity";
import {Disabled, FollowCursor, Hoverable, Movement, Position, Primitive, Selectable} from "../model/components";
import {Command} from "../model/command";

@Injectable()
export class PathService {

    private _path: Entity;
    private _nodes: Entity[] = [];

    private _dummyNode: Entity;
    private _targetNode: Entity;
    private _dummyEdge: Entity;

    constructor(
        private http: HttpClient,
        private viewerService: ViewerService
    ) {
        this.viewerService.onModeChanged.subscribe((mode) => {
            if (mode == 'COMMON') {
                this.viewerService.commands.push(new Command('timeline', () => this.enter()));
            }
        })
    }

    load() {
        this._path = this.viewerService.createEntity();
        this._path.add(new Path());
    }

    enter() {
        this.viewerService.commands = [
            new Command('add', () => this.startAddNode()),
            new Command('remove', () => this.removeNode()),
            new Command('done', () => this.apply()),
            new Command('close', () => this.cancel())
        ];
        this.viewerService.mode = 'PATH';
    }

    startAddNode() {
        this._dummyNode = this.viewerService.createEntity();
        this._dummyNode.add(new FollowCursor());
        this._dummyNode.add(new Primitive());
        this._dummyNode.add(new Position());
        this._targetNode = this._dummyNode;
        for (let node of this._nodes) {
            node.add(new Disabled());
        }

        this.viewerService.onClick = () => this.addNode();
        this.viewerService.onHover = (hovered) => this.switchTarget(hovered);
        this.viewerService.onCancel = () => this.stopAddNode();
    }

    private switchTarget(hovered: Entity[]) {
        if (hovered.length == 0 && (!this._targetNode || this._targetNode.id != this._dummyNode.id)) {
            this._targetNode = this._dummyNode;
            this.viewerService.addEntity(this._dummyNode);
        } else {
            this._targetNode = hovered[0];
            this.viewerService.removeEntity(this._dummyNode);
        }
    }

    private addNode() {
        this._nodes.push(this._targetNode);
        this._targetNode.add(new Hoverable());
        this._targetNode.add(new Selectable());
        this._targetNode.add(new Movement());
        this._targetNode.add(new Disabled());
        this._targetNode.remove(FollowCursor);

        let nodePosition = this._targetNode.get(Position);

        if (this._dummyEdge) {
            let edge = this._dummyEdge.get(Path).edges[0];
            this._path.get(Path).edges.push(edge);
            this.viewerService.removeEntity(this._dummyEdge);
        }

        this.createDummyEdge().from = nodePosition;

        this.startAddNode();

        this._dummyEdge.get(Path).edges[0].to = this._dummyNode.get(Position);
    }

    private stopAddNode() {
        if (this._dummyEdge) {
            this.viewerService.removeEntity(this._dummyEdge);
            this._dummyEdge = null;
        }
        this.viewerService.removeEntity(this._dummyNode);
        this._dummyNode = null;
        this._targetNode = null;
        for (let node of this._nodes) {
            node.remove(Disabled);
        }
        this.viewerService.onClick = null;
        this.viewerService.onHover = null;
        this.viewerService.onCancel = null;
    }

    removeNode() {
        let newNodes = [];
        for (let node of this._nodes) {
            let selectable = node.get(Selectable);
            if (selectable && selectable.value) {
                this.viewerService.removeEntity(node);
                this.removeEdgeByNode(node);
            } else {
                newNodes.push(node);
            }
        }
        this._nodes = newNodes;
    }

    private removeEdgeByNode(node: Entity) {
        let position = node.get(Position);
        let path = this._path.get(Path);
        path.edges = path.edges.filter(edge => !edge.from.value.equals(position.value) && !edge.to.value.equals(position.value));
    }

    startRemoveEdge() {

    }

    removeEdge() {

    }

    apply() {
        this.cancel();
    }

    cancel() {
        for (let node of this._nodes) {
            this.viewerService.removeEntity(node);
        }
        this.viewerService.mode = 'COMMON';
    }

    private createDummyEdge(): Edge {
        let edge = new Edge();
        this._dummyEdge = this.viewerService.createEntity();
        this._dummyEdge.add(new Path())
            .edges
            .push(edge);
        return edge;
    }

}