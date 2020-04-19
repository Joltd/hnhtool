import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {ResourceManagementModule} from "./resource-management/resource-management.module";
import {CoreModule} from "./core/core.module";
import {ErrorInterceptor} from "./core/error-hub/service/error-interceptor";
import {AgentManagementModule} from "./agent-management/agent-management.module";
import {RouterModule, Routes} from "@angular/router";
import {ResourceBrowserComponent} from "./resource-management/component/resource-browser/resource-browser.component";
import {AgentBrowserComponent} from "./agent-management/component/agent-browser/agent-browser.component";
import {WorldViewerComponent} from "./world-management/component/world-viewer/world-viewer.component";
import {WorldManagementModule} from "./world-management/world-management.module";

const appRoutes: Routes = [
  { path: 'resource', component: ResourceBrowserComponent },
  { path: 'agent', component: AgentBrowserComponent },
  { path: 'world', component: WorldViewerComponent },
  { path: '', redirectTo: '/resource', pathMatch: 'full'}
]

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    RouterModule.forRoot(appRoutes),
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    CoreModule,
    ResourceManagementModule,
    AgentManagementModule,
    WorldManagementModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
