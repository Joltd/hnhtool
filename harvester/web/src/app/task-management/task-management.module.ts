import {NgModule} from "@angular/core";
import {TaskBrowserComponent} from "./component/task-browser/task-browser.component";
import {MatTableModule} from "@angular/material/table";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatSortModule} from "@angular/material/sort";
import {MatPaginatorModule} from "@angular/material/paginator";
import {TaskService} from "./service/task.service";
import {CommonModule} from "@angular/common";
import {MatTreeModule} from "@angular/material/tree";
import {MatListModule} from "@angular/material/list";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatSelectModule} from "@angular/material/select";

@NgModule({
    declarations: [
        TaskBrowserComponent
    ],
    imports: [
        MatTableModule,
        MatProgressSpinnerModule,
        MatSortModule,
        MatPaginatorModule,
        CommonModule,
        MatTreeModule,
        MatListModule,
        MatButtonModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule
    ],
    exports: [
        TaskBrowserComponent
    ],
    providers: [
        TaskService
    ]
})
export class TaskManagementModule {}