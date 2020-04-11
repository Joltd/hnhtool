import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {WorldViewerComponent} from './world-viewer/world-viewer.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {StubComponent} from "./stub/stub.component";
import {ResourceManagementModule} from "./resource-management/resource-management.module";
import {CoreCommonModule} from "./core/common/core-common.module";
import {ErrorInterceptor} from "./core/common/error-hub/service/error-interceptor";
import {AgentManagementModule} from "./agent-management/agent-management.module";
import {RouterModule, Routes} from "@angular/router";
import {ResourceBrowserComponent} from "./resource-management/component/resource-browser/resource-browser.component";
import {AgentBrowserComponent} from "./agent-management/component/agent-browser/agent-browser.component";

const appRoutes: Routes = [
  { path: 'resource', component: ResourceBrowserComponent },
  { path: 'agent', component: AgentBrowserComponent },
  { path: '', redirectTo: '/resource', pathMatch: 'full'}
]

@NgModule({
  declarations: [
    AppComponent,
    WorldViewerComponent,
    StubComponent
  ],
  imports: [
    RouterModule.forRoot(appRoutes),
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    CoreCommonModule,
    ResourceManagementModule,
    AgentManagementModule
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
