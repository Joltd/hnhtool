export class Command {

    private readonly _icon: string;
    private readonly _title: string;
    private readonly _performer: () => void;

    constructor(icon: string, title: string, performer: () => void) {
        this._icon = icon;
        this._title = title;
        this._performer = performer;
    }

    get icon(): string {
        return this._icon;
    }

    get title(): string {
        return this._title;
    }

    perform() {
        this._performer();
    }
}