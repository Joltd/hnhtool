import {Component} from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss']
})
export class AppComponent {
  collapsed: boolean;

  constructor() {}

  toggleCollapsed() {
    this.collapsed = !this.collapsed;
  }

}
