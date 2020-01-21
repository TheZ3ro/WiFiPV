package org.thezero.wifipv;

import org.thezero.wifipv.data.Password;

import java.util.ArrayList;
import java.util.List;

class SupplicantParser {
    private List<Password> networks = new ArrayList<>();

    SupplicantParser(List<String> content) {
        String ssid = null, psk = null, kt = null, idt = null;
        boolean block = false;
        for (int i=0; i<content.size(); i++) {
            String line = content.get(i);
            if(line.startsWith("network={")) {
                block = true;
            } else if(line.startsWith("\tssid=") && block){
                ssid = line.split("=")[1];
                ssid = ssid.substring(1, ssid.length()-1);
            } else if(line.startsWith("\tpsk=") && block){
                psk = line.split("=")[1];
                psk = psk.substring(1, psk.length()-1);
            } else if(line.startsWith("\tkey_mgmt=") && block) {
                kt = line.split("=")[1];
            } else if(line.startsWith("\tidentity=") && block) {
                idt = line.split("=")[1];
                idt = idt.substring(1, idt.length()-1);
            } else if(line.startsWith("\tpassword=") && block){
                psk = line.split("=")[1];
                psk = psk.substring(1, psk.length()-1);
            } else if(line.startsWith("}")) {
                block = false;
                Password test = new Password(ssid, psk, kt, idt);
                networks.add(test);
                ssid=null; psk=null; kt=null; idt=null;
            }
        }
    }

    List<Password> getPasswords() {
        return networks;
    }
}
