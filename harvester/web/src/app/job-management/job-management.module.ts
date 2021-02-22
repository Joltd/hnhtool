import {NgModule} from "@angular/core";
import {JobBrowserComponent} from "./component/job-browser/job-browser.component";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatTableModule} from "@angular/material/table";
import {MatSortModule} from "@angular/material/sort";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {FormsModule} from "@angular/forms";
import {MatPaginatorModule} from "@angular/material/paginator";
import {CommonModule} from "@angular/common";
import {JobService} from "./service/job.service";

@NgModule({
    declarations: [
        JobBrowserComponent
    ],
    imports: [
        MatProgressSpinnerModule,
        MatTableModule,
        MatSortModule,
        MatSlideToggleModule,
        FormsModule,
        MatPaginatorModule,
        CommonModule
    ],
    exports: [
        JobBrowserComponent
    ],
    providers: [
        JobService
    ]
})
export class JobManagementModule {}