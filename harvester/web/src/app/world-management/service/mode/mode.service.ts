import {Command} from "../../model/command";

export class ModeService {

    private mode: Mode;
    private deferredModes: Mode[] = [];

    getCurrentMode(): Mode {
        return this.mode;
    }

    setupMode(mode: Mode) {
        if (this.mode) {
            this.deferredModes.push(this.mode);
        }
        this.mode = mode;
    }

    removeActiveMode() {
        this.mode = this.deferredModes.pop();
    }

}

export interface Mode {
    commands(): Command[];
}