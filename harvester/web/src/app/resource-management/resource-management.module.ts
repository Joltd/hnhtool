import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ResourceBrowserComponent} from "./component/resource-browser/resource-browser.component";
import {FormsModule} from "@angular/forms";
import {CoreModule} from "../core/core.module";
import {ResourceService} from "./service/resource.service";
import {ResourceEditorComponent} from "./component/resource-editor/resource-editor.component";
import {RouterModule} from "@angular/router";
import {ResourceGroupBrowserComponent} from "./component/resource-group-browser/resource-group-browser.component";

@NgModule({
    declarations: [
        ResourceBrowserComponent,
        ResourceEditorComponent,
        ResourceGroupBrowserComponent
    ],
    imports: [
        CommonModule,
        FormsModule,
        CoreModule,
        RouterModule
    ],
    exports: [ResourceBrowserComponent],
    providers: [ResourceService]
})
export class ResourceManagementModule {}
