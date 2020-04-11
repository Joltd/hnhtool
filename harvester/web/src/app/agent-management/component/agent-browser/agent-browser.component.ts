import {Component, OnInit} from "@angular/core";
import {AgentService} from "../../service/agent.service";
import {Agent} from "../../model/agent";

@Component({
    selector: 'agent-browser',
    templateUrl: 'agent-browser.component.html',
    styleUrls: ['agent-browser.component.scss']
})
export class AgentBrowserComponent implements OnInit{

    agents: Agent[] = [];
    agent: Agent;

    constructor(private agentService: AgentService) {}

    ngOnInit(): void {
        this.load();
    }

    load() {
        this.agentService.list().subscribe(result => this.agents = result);
    }

    addAgent() {
        this.agent = new Agent();
    }

    editAgent(agent: Agent) {
        this.agent = agent;
    }

    closeEditAgent() {
        this.agent = null;
    }

    toggleAgentState(agent: Agent) {
        this.agentService.updateEnabled(agent.id, agent.enabled)
            .subscribe(() => {});
    }

}
