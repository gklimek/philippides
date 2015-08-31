package org.philippides;

public class PhilippidesException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PhilippidesException(Exception e) {
        super(e); 
    }
}
