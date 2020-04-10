import {EventEmitter, Injectable, Type} from '@angular/core';
import {Blade} from "../model/blade";
import {BladeMeta} from "../model/blade-meta";

@Injectable()
export class BladesService {

    private identifierPointer: number = 1;
    public blades: Blade<any>[] = [];

    openBlade<T>(componentType: Type<T>): EventEmitter<T> {
        // @ts-ignore
        let bladeMeta: BladeMeta = componentType.meta;
        if (!bladeMeta || !(bladeMeta instanceof BladeMeta)) {
            throw `Provided component ${componentType.name} impossible to use as blade`;
        }

        if (bladeMeta.singleton) {
            let foundBladeIndex = this.blades.findIndex(blade => blade.component === componentType);
            if (foundBladeIndex >= 0) {
                let foundBlade = this.blades[foundBladeIndex];
                let bladeNotLast = foundBladeIndex < this.blades.length - 1;
                if (bladeNotLast) {
                    this.blades.splice(foundBladeIndex, 1);
                    this.blades.push(foundBlade);
                }
                return foundBlade.componentCreation;
            }
        }

        let blade = new Blade<T>(this.identifierPointer, componentType, bladeMeta);
        this.blades.push(blade);
        this.updateBladesState();
        this.identifierPointer++;
        return blade.componentCreation;
    }

    closeBlade(bladeId: number) {
        let foundBladeIndex = this.blades.findIndex(blade => blade.id === bladeId);
        if (foundBladeIndex >= 0) {
            this.blades.splice(foundBladeIndex, 1);
            this.updateBladesState();
        }
    }

    private updateBladesState() {

    }
}
