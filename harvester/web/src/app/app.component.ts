import {Component} from '@angular/core';
import {BladesService} from "./blades-navigation/service/blades.service";
import {StubComponent} from "./stub/stub.component";

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

  openStub(message: string) {
    this.bladesService.openBlade(StubComponent, false);
  }
}
