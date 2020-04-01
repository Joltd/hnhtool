import {NgModule} from '@angular/core';
import {ErrorHubService} from "./error-hub/service/error-hub.service";
import {PageCounterComponent} from "./page-counter/page-counter.component";
import {ErrorInterceptor} from "./error-hub/service/error-interceptor";

@NgModule({
    declarations: [PageCounterComponent],
    exports: [PageCounterComponent],
    providers: [ErrorHubService, ErrorInterceptor]
})
export class CoreCommonModule {}
