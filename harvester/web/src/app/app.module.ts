import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {ResourceManagementModule} from "./resource-management/resource-management.module";
import {CoreModule} from "./core/core.module";
import {ErrorInterceptor} from "./core/error-hub/service/error-interceptor";
import {AgentManagementModule} from "./agent-management/agent-management.module";
import {ViewerModule} from "./viewer/viewer.module";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    CoreModule,
    ResourceManagementModule,
    AgentManagementModule,
    ViewerModule,
    BrowserAnimationsModule
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
