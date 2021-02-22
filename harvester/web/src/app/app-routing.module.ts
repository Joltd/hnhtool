import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ResourceBrowserComponent} from "./resource-management/component/resource-browser/resource-browser.component";
import {AgentBrowserComponent} from "./agent-management/component/agent-browser/agent-browser.component";
import {ViewerComponent} from "./viewer/component/viewer/viewer.component";
import {TaskBrowserComponent} from "./task-management/component/task-browser/task-browser.component";
import {JobBrowserComponent} from "./job-management/component/job-browser/job-browser.component";


const routes: Routes = [
  { path: 'resource', component: ResourceBrowserComponent },
  { path: 'agent', component: AgentBrowserComponent },
  { path: 'job', component: JobBrowserComponent },
  { path: 'task', component: TaskBrowserComponent },
  { path: 'world', component: ViewerComponent },
  { path: '', redirectTo: '/resource', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
