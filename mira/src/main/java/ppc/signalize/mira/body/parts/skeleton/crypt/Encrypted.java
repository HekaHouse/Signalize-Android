package ppc.signalize.mira.body.parts.skeleton.crypt;

public class Encrypted {
    public String key;
    public String encrypted;
    public String iv;
    public Encrypted(String keyed, String enc, String ivd) {
        key=keyed;
        encrypted = enc;
        iv= ivd;
    }
}
