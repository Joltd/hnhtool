export class Command {

    private readonly _icon: string;
    private readonly _title: string;
    private readonly _tooltip: string;
    private readonly _key: string;
    private _enabled: boolean;
    private readonly _performer: () => void;

    constructor(
        icon: string,
        title: string,
        tooltip: string,
        key: string,
        performer: () => void
    ) {
        this._icon = icon;
        this._title = title;
        this._tooltip = tooltip;
        this._key = key;
        this._enabled = true;
        this._performer = performer;
    }

    get icon(): string {
        return this._icon;
    }

    get title(): string {
        return this._title;
    }

    get tooltip(): string {
        return this._tooltip;
    }

    get key(): string {
        return this._key;
    }

    get enabled(): boolean {
        return this._enabled;
    }

    set enabled(value: boolean) {
        this._enabled = value;
    }

    perform() {
        this._performer();
    }
    
}
