import {NgModule} from "@angular/core";
import {WorldViewerComponent} from "./component/world-viewer/world-viewer.component";
import {InputService} from "./service/input.service";
import {ViewportService} from "./service/viewport.service";
import {CommandService} from "./service/command.service";
import {BaseModeService} from "./service/mode/base-mode.service";
import {CommonModule} from "@angular/common";
import {EntityService} from "./service/entity.service";
import {PathModeService} from "./service/mode/path-mode.service";
import {ModeService} from "./service/mode/mode.service";
import {ViewerService} from "./service/viewer.service";

@NgModule({
    declarations: [WorldViewerComponent],
    exports: [WorldViewerComponent],
    imports: [
        CommonModule
    ],
    providers: [
        ViewerService,
        InputService,
        ViewportService,
        CommandService,
        EntityService,
        BaseModeService,
        PathModeService,
        ModeService
    ]
})
export class WorldManagementModule {}