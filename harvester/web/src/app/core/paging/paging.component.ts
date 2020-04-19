import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'paging',
    templateUrl: 'paging.component.html',
    styleUrls: ['paging.component.scss']
})
export class PagingComponent {

    @Input()
    page: number = 0;

    @Output()
    pageChange: EventEmitter<any> = new EventEmitter<any>();

    @Input()
    size: number = 10;

    @Output()
    sizeChange: EventEmitter<any> = new EventEmitter<any>();

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

    onSizeChange() {
        this.sizeChange.emit(this.size);
    }
}
