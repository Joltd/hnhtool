import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'page-counter',
    templateUrl: 'page-counter.component.html',
    styleUrls: ['page-counter.component.scss']
})
export class PageCounterComponent {

    @Input()
    page: number = 0;

    @Output()
    pageChange: EventEmitter<any> = new EventEmitter<any>();

    constructor() {}

    prev() {
        if (this.page <= 0) {
            return;
        }

        this.page--;
        this.pageChange.emit(this.page);
    }

    next() {
        this.page++;
        this.pageChange.emit(this.page);
    }

}
