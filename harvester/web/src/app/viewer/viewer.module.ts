import {NgModule} from "@angular/core";
import {ViewerService} from "./service/viewer.service";
import {WarehouseService} from "./service/warehouse.service";
import {ViewerComponent} from "./component/viewer/viewer.component";
import {CommonModule} from "@angular/common";
import {PathService} from "./service/path.service";
import {KnownObjectService} from "./service/known-object.service";
import {SpaceService} from "./service/space.service";
import {AreaService} from "./service/area.service";
import {CoreModule} from "../core/core.module";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatSelectModule} from "@angular/material/select";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {AgentListComponent} from "./component/agent-list/agent-list.component";
import {MatCardModule} from "@angular/material/card";
import {AreaEditComponent} from "./component/area-edit/area-edit.component";
import {MatDialogModule} from "@angular/material/dialog";
import {MatInputModule} from "@angular/material/input";
import {FormsModule} from "@angular/forms";

@NgModule({
    declarations: [
        ViewerComponent,
        AgentListComponent,
        AreaEditComponent
    ],
    exports: [ViewerComponent],
    imports: [
        CommonModule,
        CoreModule,
        MatFormFieldModule,
        MatSelectModule,
        MatIconModule,
        MatButtonModule,
        MatCardModule,
        MatDialogModule,
        MatInputModule,
        FormsModule
    ],
    providers: [
        ViewerService,
        WarehouseService,
        PathService,
        KnownObjectService,
        SpaceService,
        AreaService
    ]
})
export class ViewerModule {}