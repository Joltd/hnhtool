export class TaskLog {
    message: string;
    severity: string;
    time: number;
    throwable: string;
    children: TaskLog[];
}