import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ResourceManagementComponent} from "./component/resource-management/resource-management.component";
import {FormsModule} from "@angular/forms";
import {CoreCommonModule} from "../core/common/core-common.module";
import {ResourceService} from "./service/resource.service";

@NgModule({
    declarations: [
        ResourceManagementComponent
    ],
    imports: [
        CommonModule,
        FormsModule,
        CoreCommonModule
    ],
    exports: [
        ResourceManagementComponent
    ],
    providers: [
        ResourceService
    ]
})
export class ResourceManagementModule {}
