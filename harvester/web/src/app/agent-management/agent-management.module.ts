import {NgModule} from "@angular/core";
import {AgentBrowserComponent} from "./component/agent-browser/agent-browser.component";
import {AgentEditorComponent} from "./component/agent-editor/agent-editor.component";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {AgentService} from "./service/agent.service";
import {CharacterChooserComponent} from "./component/character-chooser/character-chooser.component";

@NgModule({
    declarations: [
        AgentBrowserComponent,
        AgentEditorComponent,
        CharacterChooserComponent
    ],
    imports: [
        CommonModule,
        FormsModule
    ],
    exports: [AgentBrowserComponent],
    providers: [AgentService]
})
export class AgentManagementModule {}
