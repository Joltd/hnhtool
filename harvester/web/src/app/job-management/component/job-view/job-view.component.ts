import {Component, Inject, OnInit} from "@angular/core";
import {Job} from "../../model/job";
import {JobService} from "../../service/job.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
    selector: 'job-view',
    templateUrl: 'job-view.component.html',
    styleUrls: ['job-view.component.scss']
})
export class JobViewComponent implements OnInit {

    types: string[] = [];

    constructor(
        private jobService: JobService,
        private dialogRef: MatDialogRef<JobViewComponent>,
        @Inject(MAT_DIALOG_DATA) public job: Job
    ) {}

    ngOnInit() {
        this.jobService.listType().subscribe(result => this.types = result);
    }

    save() {
        this.dialogRef.close(this.job);
    }

    close() {
        this.dialogRef.close();
    }

}