import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Resource} from "../../model/resource";
import {ResourceService} from "../../service/resource.service";
import {ActivatedRoute} from "@angular/router";

@Component({
    selector: 'resource-editor',
    templateUrl: 'resource-editor.component.html',
    styleUrls: ['resource-editor.component.scss']
})
export class ResourceEditorComponent {

    @Input()
    resource: Resource = new Resource();

    @Output()
    onClose: EventEmitter<any> = new EventEmitter<any>();

    constructor(private route: ActivatedRoute, private resourceService: ResourceService) {}

    save() {
        this.resourceService.update(this.resource).subscribe(result => this.close());
    }

    close() {
        this.onClose.emit();
    }

}
