import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Component, Inject} from "@angular/core";

@Component({
    selector: 'area-edit',
    templateUrl: 'area-edit.component.html'
})
export class AreaEditComponent {

    constructor(
        public dialogRef: MatDialogRef<AreaEditComponent>,
        @Inject(MAT_DIALOG_DATA) public name: string
    ) {}

}