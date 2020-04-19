export class SelectableComponent {

    private _selected: boolean;

    get selected(): boolean {
        return this._selected;
    }

    set selected(value: boolean) {
        this._selected = value;
    }
    
}