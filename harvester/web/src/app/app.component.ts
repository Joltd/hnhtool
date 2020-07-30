import {Component} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../environments/environment";

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss']
})
export class AppComponent {
  collapsed: boolean;

  constructor(private http: HttpClient) {}

  toggleCollapsed() {
    this.collapsed = !this.collapsed;
  }

  error() {
    this.http.get(environment.apiUrl + '/test/error').subscribe(() => {});
  }

  success() {
    this.http.get(environment.apiUrl + '/test/success').subscribe((result) => alert(result));
  }

}
