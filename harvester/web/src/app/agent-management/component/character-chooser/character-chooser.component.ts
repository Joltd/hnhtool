import {Component, EventEmitter, Input, Output} from "@angular/core";
import {AgentService} from "../../service/agent.service";
import {Agent} from "../../model/agent";

@Component({
    selector: 'character-chooser',
    templateUrl: 'character-chooser.component.html',
    styleUrls: ['character-chooser.component.scss']
})
export class CharacterChooserComponent {

    private _agent: Agent;

    @Output()
    onSelect: EventEmitter<string> = new EventEmitter<string>();

    characters: string[] = [];

    constructor(private agentService: AgentService) {}

    @Input()
    set agent(value: Agent) {
        this._agent = value;
        this.load();
    }

    load() {
        this.agentService.listCharacter(this._agent.id)
            .subscribe(result => this.characters = result);
    }

}
