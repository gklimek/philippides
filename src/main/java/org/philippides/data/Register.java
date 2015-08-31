package org.philippides.data;

public class Register {
    private Register() {
    }
    
    public static void registerEncodings() {
        Binary.registerEncodings();
        Boolean.registerEncodings();
        Null.registerEncodings();
        Ubyte.registerEncodings();
        Uint.registerEncodings();
        Ulong.registerEncodings();
        Ushort.registerEncodings();
        String.registerEncodings();
        Symbol.registerEncodings();
        List.registerEncodings();
        Accepted.registerEncodings();
        Attach.registerEncodings();
        Begin.registerEncodings();
        Close.registerEncodings();
        Data.registerEncodings();
        Detach.registerEncodings();
        Disposition.registerEncodings();
        Error.registerEncodings();
        Flow.registerEncodings();
        Open.registerEncodings();
        Properties.registerEncodings();
        SaslInit.registerEncodings();
        Source.registerEncodings();
        Target.registerEncodings();
        Transfer.registerEncodings();
    }
}
