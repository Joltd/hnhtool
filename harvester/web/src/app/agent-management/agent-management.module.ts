import {NgModule} from "@angular/core";
import {AgentBrowserComponent} from "./component/agent-browser/agent-browser.component";
import {AgentEditorComponent} from "./component/agent-editor/agent-editor.component";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {BladesNavigationModule} from "../core/blades-navigation/blades-navigation.module";
import {AgentService} from "./service/agent.service";

@NgModule({
    declarations: [
        AgentBrowserComponent,
        AgentEditorComponent
    ],
    imports: [
        CommonModule,
        FormsModule,
        BladesNavigationModule
    ],
    exports: [
        AgentBrowserComponent,
        AgentEditorComponent
    ],
    providers: [
        AgentService
    ]
})
export class AgentManagementModule {}
