import {Command} from "../../model/command";
import {Event} from "../input.service";

export class BaseModeService {
    listener(event: Event): () => void {
        return () => {};
    }

    commands(): Command[] {
        return [];
    }

}