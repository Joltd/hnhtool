import {Component, OnInit} from "@angular/core";
import {BladesService} from "../../../core/blades-navigation/service/blades.service";
import {AgentService} from "../../service/agent.service";
import {Agent} from "../../model/agent";
import {AgentEditorComponent} from "../agent-editor/agent-editor.component";
import {BladeMeta} from "../../../core/blades-navigation/model/blade-meta";

@Component({
    selector: 'agent-browser',
    templateUrl: 'agent-browser.component.html',
    styleUrls: ['agent-browser.component.scss']
})
export class AgentBrowserComponent implements OnInit{

    public static meta: BladeMeta = new BladeMeta({singleton: true});

    bladeId: number;

    agents: Agent[]

    constructor(private bladesService: BladesService, private agentService: AgentService) {}

    ngOnInit(): void {
        this.load();
    }

    load() {
        this.agentService.list().subscribe(result => this.agents = result);
    }

    close() {
        this.bladesService.closeBlade(this.bladeId);
    }

    openAgent(id: number) {
        this.bladesService.openBlade(AgentEditorComponent)
            .subscribe(componentInstance => {

            })
    }

    toggleAgentState() {}

}
