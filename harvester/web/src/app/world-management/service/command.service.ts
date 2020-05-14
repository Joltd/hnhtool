import {Command} from "./command";
import {ModeService} from "./mode/mode.service";

export class CommandService {

    constructor(private modeService: ModeService) {}

    commandList(): Command[] {
        let currentMode = this.modeService.getCurrentMode();
        return currentMode.commands();
    }

}