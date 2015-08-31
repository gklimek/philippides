package org.philippides.frame;

public enum Type {
    AMQP,
    SASL,
    UNKNOWN;
    
    private static final int AMQP_PROTOCOL_CODE = 0;
    private static final int SASL_PROTOCOL_CODE = 3;

    public static Type fromCode(int code) {
        switch (code) {
        case 0: return AMQP;
        case 1: return SASL;
        default: return UNKNOWN;
        }
    }
    
    public int toCode() {
        switch (this) {
        case AMQP: return 0;
        case SASL: return 1;
        case UNKNOWN:
        default: throw new IllegalStateException("No code for UNKNOWN");
        }
    }

    public static Type fromProtocolCode(int code) {
        switch (code) {
        case AMQP_PROTOCOL_CODE: return AMQP;
        case SASL_PROTOCOL_CODE: return SASL;
        default: return UNKNOWN;
        }
    }
    
    public int toProtocolCode() {
        switch (this) {
        case AMQP: return AMQP_PROTOCOL_CODE;
        case SASL: return SASL_PROTOCOL_CODE;
        case UNKNOWN:
        default: throw new IllegalStateException("No code for UNKNOWN");
        }
    }
}