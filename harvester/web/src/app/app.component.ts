import {Component} from '@angular/core';
import {BladesService} from "./core/blades-navigation/service/blades.service";
import {StubComponent} from "./stub/stub.component";
import {ResourceManagementComponent} from "./resource-management/component/resource-management/resource-management.component";

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss']
})
export class AppComponent {
  collapsed: boolean;

  constructor(private bladesService: BladesService) {}

  toggleCollapsed() {
    this.collapsed = !this.collapsed;
  }

  openResourceManagement() {
    this.bladesService.openBlade(ResourceManagementComponent, true);
  }

  openStub(message: string) {
    this.bladesService.openBlade(StubComponent, false);
  }
}
