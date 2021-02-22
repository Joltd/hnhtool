import {AfterViewInit, Component, OnInit, ViewChild} from "@angular/core";
import {Task} from "../../model/task";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {TaskService} from "../../service/task.service";
import {BehaviorSubject, merge} from "rxjs";
import {tap} from "rxjs/operators";
import {NestedTreeControl, TreeControl} from "@angular/cdk/tree";
import {TaskLog} from "../../model/task-log";
import {PageableDatasource} from "../../../core/datasource/pageable.datasource";
import {ArrayDataSource} from "@angular/cdk/collections";

@Component({
    selector: 'task-browser',
    templateUrl: 'task-browser.component.html',
    styleUrls: ['task-browser.component.scss']
})
export class TaskBrowserComponent implements OnInit,AfterViewInit {

    dataSource: PageableDatasource<Task>;

    @ViewChild(MatPaginator)
    paginator: MatPaginator;

    @ViewChild(MatSort)
    sort: MatSort;

    scripts: string[] = [];
    logControl: TreeControl<TaskLog> = new NestedTreeControl<TaskLog>(taskLog => taskLog.children)
    logSubject: BehaviorSubject<TaskLog[]> = new BehaviorSubject<TaskLog[]>([]);
    logDataSource: ArrayDataSource<TaskLog> = new ArrayDataSource<TaskLog>(this.logSubject.asObservable());

    task: Task;
    script: string;

    constructor(private taskService: TaskService) {}

    ngOnInit() {
        this.dataSource = new PageableDatasource<Task>(((page, size) => this.taskService.list(page, size)));
        this.reload();
        this.taskService.loadScripts()
            .subscribe(scripts => this.scripts = scripts);
    }

    ngAfterViewInit() {
        merge(this.sort.sortChange, this.paginator.page)
            .pipe(tap(() => this.dataSource.load(
                this.paginator.pageIndex,
                this.paginator.pageSize
            )))
            .subscribe();
    }

    rowClicked(task: Task) {
        if (task.id == this.task?.id) {
            this.task = null;
            this.logSubject.next([]);
        } else {
            this.task = task;
            this.taskService.loadLog(task.id)
                .subscribe(result => this.logSubject.next(result));
        }
    }

    severityClass(taskLog: TaskLog) {
        return {
            'info-severity': taskLog.severity == 'INFO',
            'error-severity': taskLog.severity == 'ERROR'
        }
    }

    reload() {
        this.dataSource.load(0, 10);
    }

    selectScript(script: string) {
        this.script = script;
    }

    schedule() {
        if (this.script) {
            this.taskService.schedule(this.script)
                .subscribe(() => this.reload());
        }
    }

}