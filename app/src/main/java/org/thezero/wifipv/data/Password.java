package org.thezero.wifipv.data;

public class Password {
    private String ssid;
    private String psk;
    private String keytype;
    private String identity;

    public Password(String s, String p, String k, String i){
        if (s == null) s = "";
        if (p == null) p = "";
        if (k == null) k = "";
        if (!(s.isEmpty() && p.isEmpty() && k.isEmpty())){
            ssid = s;
            psk = p;
            keytype = k;
        }
        identity = i;
    }

    public String toString(){
        return "WIFI:S:"+ssid+";T:"+keytype+";P:"+psk+";;";
    }

    String getSSID(){
        return ssid;
    }

    String getPSK(){
        return psk;
    }

    String getKeyType(){
        return keytype;
    }

    String getIdentity(){
        return identity;
    }
}