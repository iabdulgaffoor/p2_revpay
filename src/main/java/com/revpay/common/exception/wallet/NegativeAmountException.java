package com.revpay.common.exception.wallet;

public class NegativeAmountException extends RuntimeException {

	/**
	 * Because exceptions are serializable objects.
	 * serialVersionUID ensures version compatibility during serialization.
	 * Without it, JVM generates one automatically which may cause InvalidClassException if the class changes.
	 */
	private static final long serialVersionUID = 1L;

	
	public NegativeAmountException(String message) {
		super(message);
	}
	
}
