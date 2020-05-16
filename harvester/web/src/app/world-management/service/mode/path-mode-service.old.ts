import {Mode} from "./mode.service";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../environments/environment";
import {Entity} from "../../model/entity";
import {SelectableComponent} from "../../model/component/selectable.component";
import {HoverableComponent} from "../../model/component/hoverable.component";
import {Event, InputService} from "../input.service";
import {EntityService} from "../entity.service";
import {PointComponent} from "../../model/component/render/point.component";
import {LineComponent} from "../../model/component/render/line.component";
import {Command} from "../../model/command";
import {Point} from "../../model/point";

export class PathModeServiceOld implements Mode {

    private _edges: Entity[] = [];
    private _points: Entity[] = [];

    private _newEdge: Entity;
    private _newPointFrom: Entity;
    private _newPointTo: Entity;

    constructor(
        private http: HttpClient,
        private inputService: InputService,
        private entityService: EntityService
    ) {}

    commands(): Command[] {
        return [
            // new Command('', '', this.beginAddingEdge),
            // new Command('', '', this.remove),
            // new Command('', '', this.cancel),
            // new Command('', '', this.apply)
        ];
    }

    listener(event: Event): () => void {
        switch (event) {
            case "CANCEL":
                return this.cancelAddingEdge;
            case "CLICK":
                return this.onTargetSelected;
            case "MOVEMENT":
                return this.onMove;
            case "SELECTION":
                return this.onPointSelected;
            default:
        }
    }

    private load() {
        this.http.get<any>(environment.apiUrl + '/path')
            .subscribe(result => {
                for (let edge of result) {
                    let edgeEntity = this.entityService.createEntity();
                    let line = new LineComponent();
                    line.from = new Point(edge.from);
                    line.to = new Point(edge.to);
                    edgeEntity.addComponent(line);
                    edgeEntity.addComponent(new SelectableComponent());
                    edgeEntity.addComponent(new HoverableComponent());
                    this._edges.push(edgeEntity);
                }
            })
    }

    private apply() {
        this.cancel();
    }

    private cancel() {

    }

    private isAddingEdge(): boolean {
        return this._newEdge != null;
    }

    private beginAddingEdge() {
        this._edges.forEach(edge => {
            edge.removeComponent(SelectableComponent);
            edge.removeComponent(HoverableComponent);
        });
        this._newEdge = this.entityService.createEntity();
    }

    private completeAddingEdge() {
        this._edges.push(this._newEdge);
        this.cancelAddingEdge();
    }

    private cancelAddingEdge() {
        if (!this.isAddingEdge()) {
            return;
        }
        this._newEdge = null;
        this._newPointFrom = null;
        this._newPointTo = null;
    }

    private onTargetSelected() {
        if (!this.isAddingEdge()) {
            return;
        }
        let position = null;
        let point = this.entityService.createEntity();
        point.getComponent(PointComponent).position = position;

        this.handlePointSelected(point);
    }

    private onPointSelected() {
        if (!this.isAddingEdge()) {
            return;
        }

        let point = this.entityService.entities()
            .find(entity => {
                let selectableComponent = entity.getComponent(SelectableComponent);
                return selectableComponent && selectableComponent.selected;
            });
        if (!point) {
            return;
        }

        this.handlePointSelected(point);
    }

    private handlePointSelected(point: Entity) {
        let pointComponent = point.getComponent(PointComponent);
        let lineComponent = this._newEdge.getComponent(LineComponent);

        if (this._newPointFrom == null) {
            this._newPointFrom = point;
            lineComponent.from = pointComponent.position;
        } else {
            this._newPointTo = point;
            lineComponent.to = pointComponent.position;
            this.completeAddingEdge();
        }
    }

    private remove() {
        this._edges = this._edges.filter(edge => !edge.getComponent(SelectableComponent).selected);
        this._points = this._points.filter(point => !point.getComponent(SelectableComponent).selected);
    }

    private onMove() {
        if (this.isAddingEdge()) {
            return;
        }
        // all non selected but linked entities should be updated
    }

}