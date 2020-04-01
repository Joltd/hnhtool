import {Component, OnInit} from '@angular/core';
import {Resource} from "../../model/resource";
import {ResourceService} from "../../service/resource.service";

@Component({
    selector: 'resource-management',
    templateUrl: 'resource-management.component.html',
    styleUrls: ['resource-management.component.scss']
})
export class ResourceManagementComponent implements OnInit {

    page: number = 0;
    name: string;
    unknown: boolean;

    resources: Resource[];

    constructor(private resourceService: ResourceService) {}

    ngOnInit(): void {
        this.load();
    }

    onPaging() {
        this.load();
    }

    load() {
        this.resourceService.list(this.page, this.name, this.unknown)
            .subscribe(result => this.resources = result);
    }

}
