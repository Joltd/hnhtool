import {Injectable, Type} from '@angular/core';
import {Blade} from "../model/blade";

@Injectable()
export class BladesService {

    private identifierPointer: number = 1;
    public blades: Blade[] = [];

    openBlade(blade: Type<any>, large: boolean) {
        this.blades.push(new Blade(this.identifierPointer, blade, large));
        this.updateBladesState();
        this.identifierPointer++;
    }

    closeBlade(bladeId: number) {
        let foundBladeIndex = this.blades.findIndex(blade => blade.id === bladeId);
        if (foundBladeIndex >= 0) {
            this.blades.splice(foundBladeIndex, 1);
            this.updateBladesState();
        }
    }

    private updateBladesState() {
        let length = this.blades.length;
        if (length === 0) {
            return;
        }

        let first = this.blades[length - 1];
        first.visible = true;
        first.fill = true;

        if (length > 1) {
            let second = this.blades[length - 2];
            second.visible = !first.large || !second.large;
            second.fill = false;
        }

        if (length > 2) {
            let second = this.blades[length - 2];
            let third = this.blades[length - 3];
            third.visible = !first.large && !second.large;
            third.fill = false;
        }

        for (let index = 0; index < length - 3; index++) {
            this.blades[index].visible = false;
            this.blades[index].fill = false;
        }
    }
}
