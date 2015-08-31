package org.philippides.data;

public class Role extends Boolean {
    public static final Role SENDER = new Role(Boolean.FALSE);
    public static final Role RECEIVER = new Role(Boolean.TRUE);

    public Role(Boolean value) {
        super(value);
    }
}
