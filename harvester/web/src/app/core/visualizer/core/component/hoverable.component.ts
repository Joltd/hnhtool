export class HoverableComponent {

    private _hovered: boolean = false;

    get hovered(): boolean {
        return this._hovered;
    }

    set hovered(value: boolean) {
        this._hovered = value;
    }

}