import {NgModule} from "@angular/core";
import {WorldViewerComponent} from "./component/world-viewer/world-viewer.component";
import {CommonModule} from "@angular/common";
import {ViewerService} from "./service/viewer.service";
import {RenderService} from "./service/render.service";

@NgModule({
    declarations: [WorldViewerComponent],
    exports: [WorldViewerComponent],
    imports: [
        CommonModule
    ],
    providers: [
        ViewerService,
        RenderService
    ]
})
export class WorldManagementModule {}