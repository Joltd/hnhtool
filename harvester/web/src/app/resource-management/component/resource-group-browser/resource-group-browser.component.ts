import {Component, EventEmitter, Input, Output} from "@angular/core";
import {ResourceService} from "../../service/resource.service";
import {ResourceGroup} from "../../model/resource-group";
import {Resource} from "../../model/resource";

@Component({
    selector: 'resource-group-browser',
    templateUrl: 'resource-group-browser.component.html',
    styleUrls: ['resource-group-browser.component.scss']
})
export class ResourceGroupBrowserComponent {

    page: number = 0;
    size: number = 10;
    name: string;

    groups: ResourceGroup[] = [];

    private _resource: Resource;

    @Output()
    onClose: EventEmitter<any> = new EventEmitter<any>();

    constructor(private resourceService: ResourceService) {}

    @Input()
    set resource(value: Resource) {
        this._resource = value;
        this.load();
    }

    close() {
        this.onClose.emit();
    }

    load() {
        this.resourceService.listGroups(this.page, this.size, this.name)
            .subscribe(result => this.groups = result);
    }

    contains(group: ResourceGroup): boolean {
        return group.resources.indexOf(this._resource.name) >= 0
    }

    addToGroup(group: ResourceGroup) {
        this.resourceService.updateGroup(this._resource.id, group.id)
            .subscribe(() => this.load());
    }

    addToNewGroup() {
        this.resourceService.updateGroup(this._resource.id, -1)
            .subscribe(() => this.load());
    }

    deleteFromGroup(group: ResourceGroup) {
        this.resourceService.updateGroup(this._resource.id, null)
            .subscribe(() => this.load());
    }

}
