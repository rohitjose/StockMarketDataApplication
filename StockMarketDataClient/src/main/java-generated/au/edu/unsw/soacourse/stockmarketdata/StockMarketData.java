package au.edu.unsw.soacourse.stockmarketdata;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 3.0.4
 * 2017-04-02T20:12:38.792+10:00
 * Generated source version: 3.0.4
 * 
 */
@WebService(targetNamespace = "http://stockmarketdata.soacourse.unsw.edu.au", name = "StockMarketData")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface StockMarketData {

    @WebMethod(action = "http://stockmarketdata.soacourse.unsw.edu.au/yahooExchangeRate")
    @WebResult(name = "ExchangeRateResponse", targetNamespace = "http://stockmarketdata.soacourse.unsw.edu.au", partName = "parameters")
    public ExchangeRateResponse yahooExchangeRate(
        @WebParam(partName = "parameters", name = "ExchangeRateRequest", targetNamespace = "http://stockmarketdata.soacourse.unsw.edu.au")
        ExchangeRateRequest parameters
    ) throws YahooExchangeRateFaultMsg;

    @WebMethod(action = "http://stockmarketdata.soacourse.unsw.edu.au/importMarketData")
    @WebResult(name = "importMarketDataResponse", targetNamespace = "http://stockmarketdata.soacourse.unsw.edu.au", partName = "parameters")
    public ImportMarketDataResponse importMarketData(
        @WebParam(partName = "parameters", name = "importMarketDataRequest", targetNamespace = "http://stockmarketdata.soacourse.unsw.edu.au")
        ImportMarketDataRequest parameters
    ) throws ImportMarketDataFaultMsg;

    @WebMethod(action = "http://stockmarketdata.soacourse.unsw.edu.au/convertMarketData")
    @WebResult(name = "convertMarketDataResponse", targetNamespace = "http://stockmarketdata.soacourse.unsw.edu.au", partName = "parameters")
    public ConvertMarketDataResponse convertMarketData(
        @WebParam(partName = "parameters", name = "convertMarketDataRequest", targetNamespace = "http://stockmarketdata.soacourse.unsw.edu.au")
        ConvertMarketDataRequest parameters
    ) throws ConvertMarketDataFaultMsg;

    @WebMethod(action = "http://stockmarketdata.soacourse.unsw.edu.au/downloadFile")
    @WebResult(name = "downloadFileResponse", targetNamespace = "http://stockmarketdata.soacourse.unsw.edu.au", partName = "parameters")
    public DownloadFileResponse downloadFile(
        @WebParam(partName = "parameters", name = "downloadFileRequest", targetNamespace = "http://stockmarketdata.soacourse.unsw.edu.au")
        DownloadFileRequest parameters
    ) throws DownloadFileFaultMsg;
}
