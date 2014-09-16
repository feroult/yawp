package io.yawp.repository.response;

@Deprecated
public class ForbiddenResponse extends ErrorResponse {

	public ForbiddenResponse() {
		super(403);
	}

}
