import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {WorldViewerComponent} from './world-viewer/world-viewer.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {BladesNavigationModule} from "./core/blades-navigation/blades-navigation.module";
import {StubComponent} from "./stub/stub.component";
import {ResourceManagementModule} from "./resource-management/resource-management.module";
import {CoreCommonModule} from "./core/common/core-common.module";
import {ErrorInterceptor} from "./core/common/error-hub/service/error-interceptor";

@NgModule({
  declarations: [
    AppComponent,
    WorldViewerComponent,
    StubComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    BladesNavigationModule,
    CoreCommonModule,
    ResourceManagementModule
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
