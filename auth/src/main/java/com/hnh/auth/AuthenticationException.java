package com.hnh.auth;

final class AuthenticationException extends RuntimeException {

	AuthenticationException(final String message) {
		super(message);
	}

	AuthenticationException(final String template, final Object... args) {
		this(String.format(template, args));
	}

	AuthenticationException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
