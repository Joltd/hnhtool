export class Command {

    private _icon: string;
    private _action: () => void;

    constructor(icon: string, action: () => void) {
        this._icon = icon;
        this._action = action;
    }

    get icon(): string {
        return this._icon;
    }

    execute() {
        this._action();
    }
}