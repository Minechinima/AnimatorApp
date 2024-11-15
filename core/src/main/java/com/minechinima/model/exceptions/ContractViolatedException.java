package com.minechinima.model.exceptions;

public class ContractViolatedException extends RuntimeException {
    public ContractViolatedException(String message) {
        super(message);
    }
}
