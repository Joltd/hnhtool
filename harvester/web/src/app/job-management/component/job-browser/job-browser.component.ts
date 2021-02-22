import {AfterViewInit, Component, OnInit, ViewChild} from "@angular/core";
import {PageableDatasource} from "../../../core/datasource/pageable.datasource";
import {Job} from "../../model/job";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {JobService} from "../../service/job.service";
import {merge} from "rxjs";
import {tap} from "rxjs/operators";

@Component({
    selector: 'job-browser',
    templateUrl: 'job-browser.component.html',
    styleUrls: ['job-browser.component.scss']
})
export class JobBrowserComponent implements OnInit,AfterViewInit {

    dataSource: PageableDatasource<Job>;

    @ViewChild(MatPaginator)
    paginator: MatPaginator;

    @ViewChild(MatSort)
    sort: MatSort;

    constructor(private jobService: JobService) {}

    ngOnInit() {
        this.dataSource = new PageableDatasource<Job>(((page, size) => this.jobService.list(page, size)));
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

}