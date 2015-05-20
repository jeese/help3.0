package model;

public class SosInfo {
	
	public SosInfo(){
	}
	
	public SosInfo(String Uid, String Nickname, String Phonenum, String Time, String Soscontent, String Lat, String Lng){
		uid = Uid;
		nickname = Nickname;
		phonenum = Phonenum;
		time = Time;
		soscontent = Soscontent;
		lat = Lat;
		lng = Lng;
		
	}
	
	private int id;
	public String uid;
	public String nickname;
	public String phonenum;
	public String time;
	public String soscontent;
	public String lat;
	public String lng;
	
	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
	public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    
	public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
	public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }
    
	public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    
	public String getSoscontent() {
        return soscontent;
    }

    public void setSoscontent(String soscontent) {
        this.soscontent = soscontent;
    }
    
	public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
    
	public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
    
}
