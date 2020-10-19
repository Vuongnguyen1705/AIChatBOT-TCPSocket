package client;

import java.io.Serializable;

/**
 *
 * @author HUNGVUONG
 */
public class DataClient implements Serializable {

    private String fullName;
    private String message;
    private String option;
    private String optionDetail;
    private String date;

    public DataClient(String fullName, String message, String option, String optionDetail, String date) {
        this.fullName = fullName;
        this.message = message;
        this.option = option;
        this.optionDetail = optionDetail;
        this.date = date;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getOptionDetail() {
        return optionDetail;
    }

    public void setOptionDetail(String optionDetail) {
        this.optionDetail = optionDetail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
