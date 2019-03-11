package com.hnh.auth;

public class AuthenticationException extends RuntimeException {

	public AuthenticationException(final String message) {
		super(message);
	}

	public AuthenticationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public static AuthenticationException throwPasswordHashingFailed(final Exception cause) {
		return new AuthenticationException("An error has occurred during calculating password hash", cause);
	}

	public static void throwInitializationFailed(final Exception cause) {
		throw new AuthenticationException("An error has occurred during initialization stage", cause);
	}

	public static void throwAuthenticationFailed(final String description) {
		throw new AuthenticationException(description);
	}

	public static void throwUnknownServerAnswer(final String details) {
		throw new AuthenticationException(String.format("Unknown answer from server: \"%s\"", details));
	}

}
