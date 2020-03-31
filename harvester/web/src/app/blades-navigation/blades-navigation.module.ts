import {NgModule} from '@angular/core';
import {BladesManagerComponent} from "./component/blades-manager/blades-manager.component";
import {BladeComponent} from "./component/blade/blade.component";
import {CommonModule} from "@angular/common";
import {BladesService} from "./service/blades.service";

@NgModule({
    declarations: [
        BladesManagerComponent,
        BladeComponent
    ],
    imports: [
        CommonModule
    ],
    exports: [
        BladesManagerComponent
    ],
    providers: [
        BladesService
    ]
})
export class BladesNavigationModule {}
