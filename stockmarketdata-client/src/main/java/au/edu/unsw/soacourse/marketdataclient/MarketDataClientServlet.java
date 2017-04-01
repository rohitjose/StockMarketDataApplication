package au.edu.unsw.soacourse.marketdataclient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import au.edu.unsw.soacourse.stockmarketdata.ConvertMarketDataFaultMsg;
import au.edu.unsw.soacourse.stockmarketdata.ConvertMarketDataRequest;
import au.edu.unsw.soacourse.stockmarketdata.ConvertMarketDataResponse;
import au.edu.unsw.soacourse.stockmarketdata.DownloadFileFaultMsg;
import au.edu.unsw.soacourse.stockmarketdata.DownloadFileRequest;
import au.edu.unsw.soacourse.stockmarketdata.DownloadFileResponse;
import au.edu.unsw.soacourse.stockmarketdata.ImportMarketDataFaultMsg;
import au.edu.unsw.soacourse.stockmarketdata.ImportMarketDataRequest;
import au.edu.unsw.soacourse.stockmarketdata.ImportMarketDataResponse;
import au.edu.unsw.soacourse.stockmarketdata.ObjectFactory;
import au.edu.unsw.soacourse.stockmarketdata.StockMarketData;
import au.edu.unsw.soacourse.stockmarketdata.StockMarketDataImplService;

public class MarketDataClientServlet extends HttpServlet {

	public static final String wsdlURL = "http://localhost:8080/StockMarketData-1.0-SNAPSHOT/StockMarketData?wsdl";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/jsp/index.jsp");
		rd.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		System.out.println(action);
		
		System.out.println(System.getProperty("java.io.tmpdir"));

		HttpSession session = request.getSession();

		if (action.equals("importMarketData")) {
			importMarketData(request, response, session);
		} else if (action.equals("convertMarketData")) {
			convertMarketData(request, response, session);
		} else if (action.equals("downloadFile")) {
			downloadFile(request, response, session);
		} else if (action.equals("exchangeRate")) {
			exchangeRate(request, response, session);
		}

		response.setContentType("text/html;charset=UTF-8");
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/jsp/index.jsp");
		rd.forward(request, response);
	}

	private void exchangeRate(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// TODO Auto-generated method stub

	}

	private void downloadFile(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// TODO Auto-generated method stub

	}

	private void convertMarketData(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// TODO Auto-generated method stub

	}

	private void importMarketData(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws MalformedURLException {
		// get the request parameters
		String SECCode = getRequestParameter(request, "SECCode");
		String StartDate = getRequestParameter(request, "StartDate");
		String EndDate = getRequestParameter(request, "EndDate");
		String targetCurrencyCode = getRequestParameter(request, "targetCurrencyCode");
		String dataSourceURL = getRequestParameter(request, "dataSourceURL");
		//response variables
		String eventSetID = new String();
		String fileContents = new String();
		
		//Create the Request
		StockMarketDataImplService marketDataServiceImpl = new StockMarketDataImplService(new URL(wsdlURL));
		StockMarketData stockMarketServicePort = marketDataServiceImpl.getStockMarketDataImplPort();
		ObjectFactory objectFactory = new ObjectFactory();
		ImportMarketDataRequest importMarketDataServiceRequest = objectFactory.createImportMarketDataRequest();
		//set the request variables
		importMarketDataServiceRequest.setSec(SECCode);
		importMarketDataServiceRequest.setStartDate(StartDate);
		importMarketDataServiceRequest.setEndDate(EndDate);
		importMarketDataServiceRequest.setDataSource(dataSourceURL);
		
		try {
			ImportMarketDataResponse importMarketDataServiceResponse = stockMarketServicePort.importMarketData(importMarketDataServiceRequest);
			eventSetID = importMarketDataServiceResponse.getEventSetId();
			
			//Call convertMarketData() service if conversion is specified
			if(targetCurrencyCode!=null){
				ConvertMarketDataRequest convertMarketRequest = objectFactory.createConvertMarketDataRequest();
				convertMarketRequest.setEventSetId(eventSetID);
				convertMarketRequest.setTargetCurrency(targetCurrencyCode);
				ConvertMarketDataResponse convertMarketDataResponse = stockMarketServicePort.convertMarketData(convertMarketRequest);
				eventSetID = convertMarketDataResponse.getEventSetId();
				
			}
			
			//call downloadFile service to get the file contents
			DownloadFileRequest downloadFileServiceRequest = objectFactory.createDownloadFileRequest();
			downloadFileServiceRequest.setEventSetId(eventSetID);
			DownloadFileResponse downloadFileResponse = stockMarketServicePort.downloadFile(downloadFileServiceRequest);
			
			fileContents = downloadFileResponse.getReturnData().toString();
			
		} catch (ImportMarketDataFaultMsg e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConvertMarketDataFaultMsg e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DownloadFileFaultMsg e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(fileContents);
	}

	private String getRequestParameter(HttpServletRequest request, String parameter) {
		String value = request.getParameter(parameter);

		if (value != null) {
			return value.trim();
		} else {
			return new String();
		}
	}

}
