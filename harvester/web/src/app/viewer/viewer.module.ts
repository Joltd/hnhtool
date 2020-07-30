import {NgModule} from "@angular/core";
import {ViewerService} from "./service/viewer.service";
import {WarehouseService} from "./service/warehouse.service";
import {ViewerComponent} from "./component/viewer.component";
import {CommonModule} from "@angular/common";
import {PathService} from "./service/path.service";
import {KnownObjectService} from "./service/known-object.service";
import {SpaceService} from "./service/space.service";
import {PreferencesService} from "./service/preferences.service";

@NgModule({
    declarations: [ViewerComponent],
    exports: [ViewerComponent],
    imports: [
        CommonModule
    ],
    providers: [
        ViewerService,
        WarehouseService,
        PathService,
        KnownObjectService,
        SpaceService,
        PreferencesService
    ]
})
export class ViewerModule {}