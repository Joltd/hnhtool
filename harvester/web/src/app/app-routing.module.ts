import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ResourceBrowserComponent} from "./resource-management/component/resource-browser/resource-browser.component";
import {AgentBrowserComponent} from "./agent-management/component/agent-browser/agent-browser.component";
import {WorldViewerComponent} from "./world-management/component/world-viewer/world-viewer.component";


const routes: Routes = [
  { path: 'resource', component: ResourceBrowserComponent },
  { path: 'agent', component: AgentBrowserComponent },
  { path: 'world', component: WorldViewerComponent },
  { path: '', redirectTo: '/resource', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
