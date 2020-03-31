import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {WorldViewerComponent} from './world-viewer/world-viewer.component';
import {HttpClientModule} from '@angular/common/http';
import {BladesNavigationModule} from "./blades-navigation/blades-navigation.module";
import {StubComponent} from "./stub/stub.component";

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
    BladesNavigationModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
