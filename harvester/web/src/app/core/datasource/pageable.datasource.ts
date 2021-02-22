import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {BehaviorSubject, Observable} from "rxjs";
import {finalize} from "rxjs/operators";
import {Page} from "../page";

export class PageableDatasource<T> extends DataSource<T> {

    private dataSubject: BehaviorSubject<T[]> = new BehaviorSubject<T[]>([]);
    private loadingSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    private totalSubject: BehaviorSubject<number> = new BehaviorSubject<number>(0);
    private loader: (page: number, size: number) => Observable<Page<T>> = this.emptyLoader;

    public loading: Observable<boolean> = this.loadingSubject.asObservable();
    public total: Observable<number> = this.totalSubject.asObservable();

    constructor(loader: (page: number, size: number) => Observable<Page<T>>) {
        super();
        this.loader = loader;
    }

    connect(collectionViewer: CollectionViewer): Observable<T[] | ReadonlyArray<T>> {
        return this.dataSubject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.dataSubject.complete();
        this.loadingSubject.complete();
    }

    load(page: number, size: number) {
        this.loadingSubject.next(true);
        this.loader(page, size)
            .pipe(finalize(() => this.loadingSubject.next(false)))
            .subscribe(result => {
                this.dataSubject.next(result.data);
                this.totalSubject.next(result.total);
            });
    }

    private emptyLoader(): Observable<Page<T>> {
        return new Observable<Page<T>>(subscriber => subscriber.complete());
    }

}