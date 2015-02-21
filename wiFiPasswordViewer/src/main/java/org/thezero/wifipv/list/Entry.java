package org.thezero.wifipv.list;

public class Entry {
	private String ssid;
	private String psk;
	private String keytype;
	
	public Entry(String s, String p, String k){
		if(!(s.isEmpty() && p.isEmpty() && k.isEmpty())){
			ssid = s;
			psk = p;
			keytype = k;
		}
	}
 
	public Entry(String all) throws Exception {
		if(!(all.equals(" ") || all.equals("") || all==null)){
			String[] s = all.split(" ");
			if(s.length == 3){
				ssid = s[0];
				psk = s[1];
				keytype = s[2];
			} else {
				throw new Exception("Invalid String for Entry");
			}
		} else {
			throw new Exception("Invalid String for Entry");
		}
	}
	
	public String toString(){
		return ssid+" "+psk+" "+keytype;
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
}