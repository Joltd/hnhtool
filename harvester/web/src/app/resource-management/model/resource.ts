export class Resource {
    id: number;
    name: string;
    visual: ResourceVisual;
    unknown: boolean;
    box: boolean;
    heap: boolean;
    item: boolean;
    x: number;
    y: number;
}

export type ResourceVisual = 'PROP' | 'WIDGET'
