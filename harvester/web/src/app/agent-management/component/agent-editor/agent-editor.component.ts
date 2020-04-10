import {Component} from "@angular/core";
import {Agent} from "../../model/agent";
import {AgentService} from "../../service/agent.service";
import {BladesService} from "../../../core/blades-navigation/service/blades.service";
import {BladeMeta} from "../../../core/blades-navigation/model/blade-meta";

@Component({
    selector: 'agent-editor',
    templateUrl: 'agent-editor.component.html',
    styleUrls: ['agent-editor.component.scss']
})
export class AgentEditorComponent {

    public static meta: BladeMeta = new BladeMeta({singleton: true});

    bladeId: number;

    agent: Agent = new Agent();

    constructor(private bladesService: BladesService, private agentService: AgentService) {}

    save() {
        this.agentService.update(this.agent)
            .subscribe(() => this.close());
    }

    close() {
        this.bladesService.closeBlade(this.bladeId);
    }

    selectCharacter() {

    }

}
