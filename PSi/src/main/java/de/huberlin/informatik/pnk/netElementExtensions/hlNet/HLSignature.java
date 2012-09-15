package de.huberlin.informatik.pnk.netElementExtensions.hlNet;

import de.huberlin.informatik.pnk.kernel.*;

public class HLSignature extends Signature {
    public Object[] HLVar[] =
    {
        {"x", null},
        {"y", null}
    };
    private Object[] HLFkt[] =
    {
        {"mal2", "de.huberlin.informatik.pnk.netElementExtensions.hlNet.HLFkt",
         "mal2(java.lang.Integer)", null},
        {"plus", "de.huberlin.informatik.pnk.netElementExtensions.hlNet.HLFkt",
         "plus(java.lang.Integer,java.lang.Integer)", null},
    };

    private String[] tokenType[] =
    {
        {"Integer", "java.lang.Integer"},
    };

    public HLSignature(Extendable extendable) {
        super(extendable);
        varField = HLVar;
        fktField = HLFkt;
        typeField = tokenType;
        System.out.println("Varfield gesetzt");
    }

    public HLSignature(Extendable extendable, String signature) {
        super(extendable, signature);
        varField = HLVar;
        fktField = HLFkt;
        typeField = tokenType;
        System.out.println("Varfield gesetzt");
    }
}