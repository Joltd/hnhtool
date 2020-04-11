import {Component, EventEmitter, Input, Output} from "@angular/core";
import {Agent} from "../../model/agent";
import {AgentService} from "../../service/agent.service";

@Component({
    selector: 'agent-editor',
    templateUrl: 'agent-editor.component.html',
    styleUrls: ['agent-editor.component.scss']
})
export class AgentEditorComponent {

    @Input()
    agent: Agent = new Agent();

    @Output()
    onClose: EventEmitter<any> = new EventEmitter<any>();

    selectCharacter: boolean = false;

    constructor(private agentService: AgentService) {}

    save() {
        this.agentService.update(this.agent).subscribe(() => this.close());
    }

    close() {
        this.onClose.emit();
    }

    doSelectCharacter() {
        this.selectCharacter = true;
    }

    onSelectCharacter(character: string) {
        if (character) {
            this.agent.character = character;
        }
        this.selectCharacter = false;
    }

}
