package DTO;

import java.io.Serializable;

/**
 *
 * @author HUNGVUONG
 */
public class DataClient implements Serializable {

    private int type;
    private String name;
    private String message;
    private String option;
    private String optionDetail;
    private String date;

    public DataClient(int type, String name, String message, String option, String optionDetail, String date) {
        this.type = type;
        this.name = name;
        this.message = message;
        this.option = option;
        this.optionDetail = optionDetail;
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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