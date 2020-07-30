export class ErrorInfo {

    private readonly _message: string;
    private readonly _error: Error;

    constructor(error: string | Error) {
        if (error instanceof Error) {
            this._error = error;
            this._message = error.message;
        } else {
            this._message = error;
        }
    }

    get message(): string {
        return this._message;
    }

    get error(): Error {
        return this._error;
    }

}