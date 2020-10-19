package service.simsimi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Request {

    @SerializedName("utext")
    @Expose
    private String utext;
    @SerializedName("lang")
    @Expose
    private String lang;

    public Request(String utext, String lang) {
        this.utext = utext;
        this.lang = lang;
    } 
    
    public String getUtext() {
        return utext;
    }

    public void setUtext(String utext) {
        this.utext = utext;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

}
