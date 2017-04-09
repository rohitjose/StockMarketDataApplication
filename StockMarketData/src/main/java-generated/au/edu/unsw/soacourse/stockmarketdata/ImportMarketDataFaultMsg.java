
package au.edu.unsw.soacourse.stockmarketdata;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.0.4
 * 2017-04-02T20:20:25.554+10:00
 * Generated source version: 3.0.4
 */

@WebFault(name = "importMarketDataFault", targetNamespace = "http://stockmarketdata.soacourse.unsw.edu.au")
public class ImportMarketDataFaultMsg extends Exception {
    
    private au.edu.unsw.soacourse.stockmarketdata.ServiceFaultType importMarketDataFault;

    public ImportMarketDataFaultMsg() {
        super();
    }
    
    public ImportMarketDataFaultMsg(String message) {
        super(message);
    }
    
    public ImportMarketDataFaultMsg(String message, Throwable cause) {
        super(message, cause);
    }

    public ImportMarketDataFaultMsg(String message, au.edu.unsw.soacourse.stockmarketdata.ServiceFaultType importMarketDataFault) {
        super(message);
        this.importMarketDataFault = importMarketDataFault;
    }

    public ImportMarketDataFaultMsg(String message, au.edu.unsw.soacourse.stockmarketdata.ServiceFaultType importMarketDataFault, Throwable cause) {
        super(message, cause);
        this.importMarketDataFault = importMarketDataFault;
    }

    public au.edu.unsw.soacourse.stockmarketdata.ServiceFaultType getFaultInfo() {
        return this.importMarketDataFault;
    }
}
