import {Command} from "../command";
import {Event} from "../input.service";
import {Mode} from "./mode.service";

export class BaseModeService implements Mode {
    listener(event: Event): () => void {
        return () => {};
    }

    commands(): Command[] {
        return [];
    }

}