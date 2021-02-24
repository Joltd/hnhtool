import {AfterViewInit, ChangeDetectorRef, Component, OnInit, ViewChild} from "@angular/core";
import {PageableDatasource} from "../../../core/datasource/pageable.datasource";
import {Job} from "../../model/job";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {JobService} from "../../service/job.service";
import {merge} from "rxjs";
import {tap} from "rxjs/operators";
import {MatDialog} from "@angular/material/dialog";
import {JobViewComponent} from "../job-view/job-view.component";

@Component({
    selector: 'job-browser',
    templateUrl: 'job-browser.component.html',
    styleUrls: ['job-browser.component.scss']
})
export class JobBrowserComponent implements OnInit,AfterViewInit {

    showingColumns: string[] = ['id', 'name', 'type', 'enabled', 'remove']
    dataSource: PageableDatasource<Job>;

    @ViewChild(MatPaginator)
    paginator: MatPaginator;

    @ViewChild(MatSort)
    sort: MatSort;

    job: Job;

    constructor(
        private jobService: JobService,
        private dialog: MatDialog,
        private cdr: ChangeDetectorRef
    ) {}

    ngOnInit() {
        this.dataSource = new PageableDatasource<Job>((page, size) => this.jobService.list(page, size));
        this.reload();
    }

    ngAfterViewInit() {
        merge(this.sort.sortChange, this.paginator.page)
            .pipe(tap(() => this.dataSource.load(
                this.paginator.pageIndex,
                this.paginator.pageSize
            )))
            .subscribe();
    }

    reload() {
        this.dataSource.load(0, 10);
    }

    new() {
        let data = new Job();
        this.dialog.open(JobViewComponent, {data})
            .afterClosed()
            .subscribe(job => {
                this.job = job;
                this.cdr.detectChanges();
            });
    }

    edit(job: Job) {
        if (this.job?.id == job.id) {
            this.job = null;
        } else {
            this.job = job;
        }
    }

}