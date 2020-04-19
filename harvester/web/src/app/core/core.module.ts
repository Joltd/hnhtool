import {NgModule} from '@angular/core';
import {ErrorHubService} from "./error-hub/service/error-hub.service";
import {PagingComponent} from "./paging/paging.component";
import {ErrorInterceptor} from "./error-hub/service/error-interceptor";
import {FormsModule} from "@angular/forms";
import {VisualizerComponent} from "./visualizer/component/visualizer.component";

@NgModule({
    declarations: [PagingComponent, VisualizerComponent],
    exports: [PagingComponent, VisualizerComponent],
    imports: [FormsModule],
    providers: [ErrorHubService, ErrorInterceptor]
})
export class CoreModule {}
