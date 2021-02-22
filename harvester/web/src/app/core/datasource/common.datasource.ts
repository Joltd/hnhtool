import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {BehaviorSubject, Observable} from "rxjs";
import {finalize} from "rxjs/operators";

export class CommonDatasource<T> extends DataSource<T> {

    private dataSubject: BehaviorSubject<T[]> = new BehaviorSubject<T[]>([]);
    private loadingSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    private loader: () => Observable<T[]> = this.emptyLoader;

    public loading: Observable<boolean> = this.loadingSubject.asObservable();

    constructor(loader: () => Observable<T[]>) {
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

    load() {
        this.loadingSubject.next(true);
        this.loader()
            .pipe(finalize(() => this.loadingSubject.next(false)))
            .subscribe(result => {
                console.log(result);
                this.dataSubject.next(result)
            });
    }

    private emptyLoader(): Observable<T[]> {
        return new Observable<T[]>(subscriber => subscriber.complete());
    }

}