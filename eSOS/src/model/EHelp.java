package model;

import java.util.ArrayList;
import java.util.List;

public class EHelp {
	
	private static final EHelp ehelp = new EHelp();
	private List<SosInfo> list_sosinfo = new ArrayList<SosInfo>();
	
	private EHelp(){
		
	}
	
	public static EHelp getInstance(){
		return ehelp;
	}
	
	public void setSosInfoList(List<SosInfo> list){
		list_sosinfo = list;
	}
	
	public List<SosInfo> getSosInfoList(){
		return list_sosinfo;
	}

}
