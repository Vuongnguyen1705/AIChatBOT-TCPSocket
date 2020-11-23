package service.weather;

/**
 *
 * @author HUNGVUONG
 */
public class Coordinates {
    
    private String code;
    private double longitude;
    private double latitude;
    private String name;

    public Coordinates(String code, double longitude, double latitude, String name) {
        this.code = code;
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }   

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
