package DTO;

/**
 *
 * @author HUNGVUONG
 */
public class InfoClient {

    String hostName;
    String hostAddress;
    int port;

    public InfoClient(String hostName, String hostAddress, int port) {
        this.hostName = hostName;
        this.hostAddress = hostAddress;
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
