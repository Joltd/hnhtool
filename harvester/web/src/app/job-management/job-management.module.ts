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
import {LearningViewComponent} from "./component/learning-view/learning-view.component";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatSelectModule} from "@angular/material/select";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {JobViewComponent} from "./component/job-view/job-view.component";
import {MatDialogModule} from "@angular/material/dialog";
import {MatInputModule} from "@angular/material/input";
import {LearningService} from "./service/learning.service";

@NgModule({
    declarations: [
        JobBrowserComponent,
        JobViewComponent,
        LearningViewComponent
    ],
    imports: [
        MatProgressSpinnerModule,
        MatTableModule,
        MatSortModule,
        MatSlideToggleModule,
        FormsModule,
        MatPaginatorModule,
        CommonModule,
        MatFormFieldModule,
        MatSelectModule,
        MatButtonModule,
        MatIconModule,
        MatDialogModule,
        MatInputModule
    ],
    exports: [
        JobBrowserComponent
    ],
    providers: [
        JobService,
        LearningService
    ]
})
export class JobManagementModule {}