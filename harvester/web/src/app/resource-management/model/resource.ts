export class Resource {
    id: number;
    name: string;
    visual: ResourceVisual;
    unknown: boolean;

    constructor(id: number, name: string, visual: ResourceVisual, unknown: boolean) {
        this.id = id;
        this.name = name;
        this.visual = visual;
        this.unknown = unknown;
    }
}

export type ResourceVisual = 'PROP' | 'WIDGET'
