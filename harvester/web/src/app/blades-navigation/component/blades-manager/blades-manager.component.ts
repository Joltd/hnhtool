import {Component} from '@angular/core';
import {BladesService} from "../../service/blades.service";

@Component({
    selector: 'blades-manager',
    templateUrl: 'blades-manager.component.html',
    styleUrls: ['blades-manager.component.scss']
})
export class BladesManagerComponent {

    constructor(public bladesService: BladesService) {}

}
