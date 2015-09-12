package io.yawp.commons.http;

@Deprecated
public class ForbiddenResponse extends ErrorResponse {

	public ForbiddenResponse() {
		super(403);
	}

}
