import {NgModule} from "@angular/core";
import {WorldViewerComponent} from "./component/world-viewer/world-viewer.component";
import {CommonModule} from "@angular/common";
import {ViewerService} from "./service/viewer.service";

@NgModule({
    declarations: [WorldViewerComponent],
    exports: [WorldViewerComponent],
    imports: [
        CommonModule
    ],
    providers: [
        ViewerService
    ]
})
export class WorldManagementModule {}