import {Component, Input} from '@angular/core';
import {BladesService} from "../core/blades-navigation/service/blades.service";

@Component({
    selector: 'stub',
    templateUrl: 'stub.component.html',
    styleUrls: ['stub.component.scss']
})
export class StubComponent {
    @Input()
    text: string

    bladeId: number;

    constructor(private bladesService: BladesService) {
        this.text = 'world';
    }

    closeBlade() {
        this.bladesService.closeBlade(this.bladeId);
    }
}
