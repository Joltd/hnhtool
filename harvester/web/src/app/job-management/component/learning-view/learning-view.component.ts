import {AfterViewInit, Component, Input, OnInit, ViewChild} from "@angular/core";
import {PageableDatasource} from "../../../core/datasource/pageable.datasource";
import {Learning} from "../../model/learning";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {LearningService} from "../../service/learning.service";
import {LearningStat} from "../../model/learning-stat";
import {merge} from "rxjs";
import {tap} from "rxjs/operators";
import {Record} from "../../../core/model/record";
import {JobService} from "../../service/job.service";
import {Job} from "../../model/job";

@Component({
    selector: 'learning-view',
    templateUrl: 'learning-view.component.html',
    styleUrls: ['learning-view.component.scss']
})
export class LearningViewComponent implements OnInit,AfterViewInit {

    showingColumns: string[] = ['id', 'date', 'agent', 'learningPoints', 'experiencePoints', 'mentalWeights', 'task'];
    dataSource: PageableDatasource<LearningStat>;

    @ViewChild(MatPaginator)
    paginator: MatPaginator;

    @ViewChild(MatSort)
    sort: MatSort;

    @Input()
    job: Job;
    learning: Learning = new Learning();
    agents: Record[] = [];
    areas: Record[] = [];

    constructor(
        private jobService: JobService,
        private learningService: LearningService
    ) {}

    ngOnInit() {
        this.dataSource = new PageableDatasource<LearningStat>((page, size) => {
            return this.learningService.listStat(this.learning.id, page, size);
        });
    }

    ngAfterViewInit() {
        merge(this.sort.sortChange, this.paginator.page)
            .pipe(tap(() => this.dataSource.load(
                this.paginator.pageIndex,
                this.paginator.pageSize
            )))
            .subscribe();
        this.jobService.listAgent().subscribe(result => this.agents = result);
        this.jobService.listArea().subscribe(result => this.areas = result);
        if (this.job.id) {
            this.learningService.byId(this.job.id).subscribe(result => {
                this.learning = result;
                this.reload();
            });
        } else {
            this.learning.fill(this.job);
            this.learning.agent = null;
            this.learning.area = null;
        }
    }

    reload() {
        this.dataSource.load(0, 10);
    }

    save() {
        this.learningService.update(this.learning).subscribe();
    }

}