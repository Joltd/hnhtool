package com.hnh.auth;

public class AuthenticationResult {

	private byte[] cookie;
	private byte[] token;

	public AuthenticationResult(final byte[] cookie, final byte[] token) {
		this.cookie = cookie;
		this.token = token;
	}

	public byte[] getCookie() {
		return cookie;
	}

	public byte[] getToken() {
		return token;
	}
}