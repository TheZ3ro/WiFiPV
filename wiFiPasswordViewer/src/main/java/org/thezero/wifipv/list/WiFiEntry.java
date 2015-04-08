package org.thezero.wifipv.list;

public class WiFiEntry {
	private String ssid;
	private String psk;
	private String keytype;
    private String identity;
	
	public WiFiEntry(String s, String p, String k, String i){
        if(s==null) s="";
        if(p==null) p="";
        if(k==null) k="";
		if(!(s.isEmpty() && p.isEmpty() && k.isEmpty())){
			ssid = s;
			psk = p;
			keytype = k;
		}
        identity=i;
	}
 
	public WiFiEntry(String all) throws Exception {
		if(!(all.equals(" ") || all.equals("") || all==null)){
			String[] s = all.split(" ");
			if(s.length == 3){
				ssid = s[0];
				psk = s[1];
				keytype = s[2];
			} else if(s.length == 4) {
                ssid = s[0];
                psk = s[1];
                keytype = s[2];
                identity = s[3];
            } else {
				throw new Exception("Invalid String for Entry");
			}
		} else {
			throw new Exception("Invalid String for Entry");
		}
	}
	
	public String toString(){
		return "WIFI:S:"+ssid+";T:"+keytype+";P:"+psk+";;";
	}
	
	public String getSSID(){
		return ssid;
	}
	
	public String getPSK(){
		return psk;
	}
	
	public String getKeyType(){
		return keytype;
	}

    public String getIdentity(){
        return identity;
    }
}