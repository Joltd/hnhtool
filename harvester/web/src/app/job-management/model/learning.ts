import {Job} from "./job";

export class Learning extends Job {

    area: number;
    agent: number;

    fill(job: Job) {
        this.id = job.id;
        this.name = job.name;
        this.type = job.type;
        this.enabled = job.enabled;
    }

}