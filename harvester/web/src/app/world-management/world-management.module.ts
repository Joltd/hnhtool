import {NgModule} from "@angular/core";
import {WorldViewerComponent} from "./component/world-viewer/world-viewer.component";
import {CoreModule} from "../core/core.module";

@NgModule({
    declarations: [
        WorldViewerComponent
    ],
    imports: [
        CoreModule
    ],
    providers: []
})
export class WorldManagementModule {}
