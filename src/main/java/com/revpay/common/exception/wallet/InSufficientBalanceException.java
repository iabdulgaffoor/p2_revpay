package com.revpay.common.exception.wallet;

import java.io.Serial;

public class InSufficientBalanceException extends RuntimeException {
	
	/**
	 * Because exceptions are serializable objects.
	 * serialVersionUID ensures version compatibility during serialization.
	 * Without it, JVM generates one automatically which may cause InvalidClassException if the class changes.
	 */
	@Serial
    private static final long serialVersionUID = 1L;

	public InSufficientBalanceException(String message) {
		super(message);
	}
	
}
