package org.thezero.wifipv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class Utils {
	public static String intro = "# Host file generated by HostsManager\n# Please do not modify it directly!\n\n";
	public static String outro = "\n# After this, there are your old Hosts Entry\n\n";
	
	public static String File_BackUp="lol";
	public static String File_Change="back";
	public static String File_Temp="temp";
	
	public static void Save(Context c, String path, String s) {
		FileOutputStream fos;
		try {
			fos = c.openFileOutput(path, Context.MODE_PRIVATE);
			fos.write(s.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void Append(Context c, String path, String s,boolean p) {
		FileOutputStream fos;
		try {
			fos = c.openFileOutput(path, Context.MODE_APPEND);
			if (p == true){
				fos.write("\n".getBytes());
			}
			fos.write(s.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String Load(Context c, String path) {
		FileInputStream fin = null;
		String s = null;
		try {
			fin = c.openFileInput(path);
			InputStream is = fin;
			s = convertStreamToString(is);
			fin.close();
		} catch (FileNotFoundException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	public static void WriteOutro(FileOutputStream fo){
		try {
			fo.write(outro.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void WriteOutro(Context c, String path){
		Append(c, path, outro, true);
	}
	
	public static void WriteIntro(FileOutputStream fo){
		try {
			fo.write(intro.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void WriteIntro(Context c, String path){
		Append(c, path, intro, true);
	}
	
	public static boolean CheckIntro(String s){
		if(s.indexOf("HostsManager")!=-1){
			return true;
		}
		return false;
	}
	
	public static boolean CheckFile(Context c, String path){
		boolean r=false;
		File file = c.getFileStreamPath(path);
		if(file.exists()){
			r=true;
		}
		return r;
	}
	
	public static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
}
