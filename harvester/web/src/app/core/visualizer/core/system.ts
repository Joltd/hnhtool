import {Entity} from "./entity";

export interface System {

    process(entities: Entity[]);

}
