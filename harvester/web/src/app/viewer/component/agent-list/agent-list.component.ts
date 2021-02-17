import {Component, OnInit} from "@angular/core";
import {Agent} from "../../../agent-management/model/agent";
import {AgentService} from "../../../agent-management/service/agent.service";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../environments/environment";
import {plainToClass} from "class-transformer";
import {Point} from "../../model/point";
import {ViewerService} from "../../service/viewer.service";

@Component({
    selector: 'agent-list',
    templateUrl: 'agent-list.component.html',
    styleUrls: ['agent-list.component.scss']
})
export class AgentListComponent implements OnInit {

    agents: Agent[] = [];

    constructor(
        private http: HttpClient,
        private agentService: AgentService,
        private viewerService: ViewerService
    ) {}

    ngOnInit(): void {
        this.agentService.list()
            .subscribe(result => this.agents = result);
    }

    moveToAgent(id: number) {
        this.http.get<any>(environment.apiUrl + '/agent/' + id + '/position')
            .subscribe(result => {
                if (result) {
                    let point = plainToClass(Point, result);
                    this.viewerService.moveViewportTo(point);
                }
            })
    }

}