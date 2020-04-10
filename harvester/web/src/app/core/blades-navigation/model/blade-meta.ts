export class BladeMeta {
    min: number = 1;
    max: number = 3;
    singleton: boolean = false;

    constructor(params: any) {
        Object.assign(this, params);
    }
}
