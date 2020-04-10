import {Component, OnInit} from '@angular/core';
import {Resource} from "../../model/resource";
import {ResourceService} from "../../service/resource.service";

@Component({
    selector: 'resource-browser',
    templateUrl: 'resource-browser.component.html',
    styleUrls: ['resource-browser.component.scss']
})
export class ResourceBrowserComponent implements OnInit {

    page: number = 0;
    size: number = 16;
    name: string;
    unknown: boolean;

    resources: Resource[] = [];
    editResource: Resource;
    changeGroup: Resource;

    constructor(private resourceService: ResourceService) {}

    ngOnInit(): void {
        this.load();
    }

    load() {
        this.resourceService.list(this.page, this.size, this.name, this.unknown)
            .subscribe(result => this.resources = result);
    }

    doEditResource(resource: Resource) {
        this.closeChangeGroup();
        this.editResource = resource;
    }

    closeEditResource() {
        this.editResource = null;
    }

    doChangeGroup(resource: Resource) {
        this.closeEditResource();
        this.changeGroup = resource;
    }

    closeChangeGroup() {
        this.changeGroup = null;
    }

}
