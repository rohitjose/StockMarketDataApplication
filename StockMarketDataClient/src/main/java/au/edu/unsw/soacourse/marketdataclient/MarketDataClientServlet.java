package au.edu.unsw.soacourse.marketdataclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

import au.edu.unsw.soacourse.stockmarketdata.ConvertMarketDataFaultMsg;
import au.edu.unsw.soacourse.stockmarketdata.ConvertMarketDataRequest;
import au.edu.unsw.soacourse.stockmarketdata.ConvertMarketDataResponse;
import au.edu.unsw.soacourse.stockmarketdata.DownloadFileFaultMsg;
import au.edu.unsw.soacourse.stockmarketdata.DownloadFileRequest;
import au.edu.unsw.soacourse.stockmarketdata.DownloadFileResponse;
import au.edu.unsw.soacourse.stockmarketdata.ExchangeRateRequest;
import au.edu.unsw.soacourse.stockmarketdata.ExchangeRateResponse;
import au.edu.unsw.soacourse.stockmarketdata.ImportMarketDataFaultMsg;
import au.edu.unsw.soacourse.stockmarketdata.ImportMarketDataRequest;
import au.edu.unsw.soacourse.stockmarketdata.ImportMarketDataResponse;
import au.edu.unsw.soacourse.stockmarketdata.ObjectFactory;
import au.edu.unsw.soacourse.stockmarketdata.ServiceFaultType;
import au.edu.unsw.soacourse.stockmarketdata.StockMarketData;
import au.edu.unsw.soacourse.stockmarketdata.StockMarketDataImplService;
import au.edu.unsw.soacourse.stockmarketdata.YahooExchangeRateFaultMsg;

public class MarketDataClientServlet extends HttpServlet {

	public static final String wsdlURL = "http://192.168.99.100:8888/StockMarketData/StockMarketData?wsdl";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
		rd.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		System.out.println(action);

		HttpSession session = request.getSession();
		if (session.getAttribute("eventSetIds") == null) {
			session.setAttribute("eventSetIds", new ArrayList<String>());
		}

		if (action.equals("importMarketData")) {
			importMarketData(request, response, session);
		} else if (action.equals("convertMarketData")) {
			convertMarketData(request, response, session);
		} else if (action.equals("downloadFile")) {
			downloadFile(request, response, session);
		} else if (action.equals("exchangeRate")) {
			exchangeRate(request, response, session);
		}
	}

	private void exchangeRate(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		// Get the request parameters
		String baseCurrencyCode = getRequestParameter(request, "baseCurrencyCode");
		String targetCurrencyCode = getRequestParameter(request, "targetCurrencyCode");
		String exchangeRate = new String();
		String dateValue = new String();
		String fileContents = new String();

		boolean errorIndicator = true;
		String errorMessage = new String();

		try {
			// Create the Request
			StockMarketDataImplService marketDataServiceImpl = new StockMarketDataImplService(new URL(wsdlURL));
			StockMarketData stockMarketServicePort = marketDataServiceImpl.getStockMarketDataImplPort();
			ObjectFactory objectFactory = new ObjectFactory();

			// call service
			ExchangeRateRequest serviceRequest = objectFactory.createExchangeRateRequest();
			serviceRequest.setBaseCurrencyCode(baseCurrencyCode);
			serviceRequest.setTargetCurrencyCode(targetCurrencyCode);
			ExchangeRateResponse serviceResponse = stockMarketServicePort.yahooExchangeRate(serviceRequest);

			// Parse the response
			exchangeRate = serviceResponse.getRate();
			dateValue = serviceResponse.getAsAt();
			fileContents = "Exchange Rate," + exchangeRate + "\n" + "As on Date," + dateValue;
			errorIndicator = false;
		} catch (MalformedURLException e) {
			// URL error
			errorMessage = "URL Parse error during execution" + e.getMessage();
		} catch (YahooExchangeRateFaultMsg e) {
			// Error message from yahooExchangeRate()
			ServiceFaultType serviceFault = e.getFaultInfo();
			errorMessage = serviceFault.getErrorDescription();
		}

		if (errorIndicator == false) {
			// Set the response attributes
			request.setAttribute("serviceResponseContents", fileContents);
			request.setAttribute("responseStatus", "success");

		} else {
			request.setAttribute("serviceResponseContents",
					"An error occurred while processing youre request: " + errorMessage);
			request.setAttribute("responseStatus", "error");
		}
		response.setContentType("text/html;charset=UTF-8");
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
		rd.forward(request, response);

	}

	private String generateSourceFile(String contents, String fileType) {
		String sourceFileData = new String();
		if (fileType.equals("csv")) {
			return contents;
		} else if (fileType.equals("HTML")) {
			String HTMLCode = new String();
			HTMLCode = "<table>\n";
			for (String line : contents.split("\n")) {
				HTMLCode = HTMLCode + "<tr>\n";
				for (String item : line.split(",", -1)) {
					HTMLCode = HTMLCode + "<td>" + item + "</td>\n";
				}
				HTMLCode = HTMLCode + "</tr>\n";

			}
			HTMLCode = HTMLCode + "</table>";

			sourceFileData = HTMLCode;
		} else {
			StringBuilder xmlDoc = new StringBuilder();
			xmlDoc.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<eventsSet>\n");

			for (String line : contents.split("\n")) {
				if (!line.contains("#RIC")) {
					xmlDoc.append("<event>\n");
					String[] dataComponents = line.split(",", -1);
					xmlDoc.append("<securityCode>" + dataComponents[0] + "</securityCode>\n");
					xmlDoc.append("<date>" + dataComponents[1] + "</date>\n");
					xmlDoc.append("<time>" + dataComponents[2] + "</time>\n");
					xmlDoc.append("<GMTOffset>" + dataComponents[3] + "</GMTOffset>\n");
					xmlDoc.append("<eventType>" + dataComponents[4] + "</eventType>\n");
					xmlDoc.append("<price>" + dataComponents[5] + "</price>\n");
					xmlDoc.append("<volume>" + dataComponents[6] + "</volume>\n");
					xmlDoc.append("<bidPrice>" + dataComponents[7] + "</bidPrice>\n");
					xmlDoc.append("<bidSize>" + dataComponents[8] + "</bidSize>\n");
					xmlDoc.append("<askPrice>" + dataComponents[9] + "</askPrice>\n");
					xmlDoc.append("<askSize>" + dataComponents[10] + "</askSize>\n");

					xmlDoc.append("</event>\n");
				}

			}
			xmlDoc.append("</eventsSet>");
			sourceFileData = xmlDoc.toString();

		}

		return sourceFileData;
	}

	private void downloadFile(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		// Get the request parameters
		String eventSetID = getRequestParameter(request, "eventSetId");
		String fileType = getRequestParameter(request, "filetype");

		String fileContents = new String();
		String sourceFileData = new String();
		boolean errorIndicator = true;
		String errorMessage = new String();

		try {
			// Create the Request
			StockMarketDataImplService marketDataServiceImpl = new StockMarketDataImplService(new URL(wsdlURL));
			StockMarketData stockMarketServicePort = marketDataServiceImpl.getStockMarketDataImplPort();
			ObjectFactory objectFactory = new ObjectFactory();

			// call downloadFile service to get the file contents
			DownloadFileRequest downloadFileServiceRequest = objectFactory.createDownloadFileRequest();
			downloadFileServiceRequest.setEventSetId(eventSetID);
			DownloadFileResponse downloadFileResponse = stockMarketServicePort.downloadFile(downloadFileServiceRequest);

			// Parse the response
			byte[] respReturnData = downloadFileResponse.getReturnData();
			String encoded = Base64.encodeBase64String(respReturnData);
			byte[] decoded = Base64.decodeBase64(encoded);
			fileContents = new String(decoded, "UTF-8");
			sourceFileData = generateSourceFile(fileContents, fileType);
			errorIndicator = false;
		} catch (MalformedURLException e) {
			// URL error
			errorMessage = "URL Parse error during execution" + e.getMessage();
		} catch (UnsupportedEncodingException e) {
			// Encoding error
			errorMessage = "Unsupported encoding  encountered during execution" + e.getMessage();
		} catch (DownloadFileFaultMsg e) {
			// Error message from downloadFile()
			ServiceFaultType serviceFault = e.getFaultInfo();
			errorMessage = serviceFault.getErrorDescription();
		}

		if (errorIndicator == false) {
			// Set the response attributes
			request.setAttribute("serviceResponseContents", fileContents);
			request.setAttribute("responseStatus", "success");
			request.setAttribute("sourceFileData", sourceFileData);

		} else {
			request.setAttribute("serviceResponseContents",
					"An error occurred while processing youre request: " + errorMessage);
			request.setAttribute("responseStatus", "error");
		}
		response.setContentType("text/html;charset=UTF-8");
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
		rd.forward(request, response);

	}

	private void convertMarketData(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		// Get the request parameters
		String eventSetID = getRequestParameter(request, "eventSetId");
		String targetCurrencyCode = getRequestParameter(request, "targetCurrencyCode");

		String fileContents = new String();
		boolean errorIndicator = true;
		String errorMessage = new String();

		try {
			// Create the Request
			StockMarketDataImplService marketDataServiceImpl = new StockMarketDataImplService(new URL(wsdlURL));
			StockMarketData stockMarketServicePort = marketDataServiceImpl.getStockMarketDataImplPort();
			ObjectFactory objectFactory = new ObjectFactory();

			ConvertMarketDataRequest convertMarketRequest = objectFactory.createConvertMarketDataRequest();
			convertMarketRequest.setEventSetId(eventSetID);
			convertMarketRequest.setTargetCurrency(targetCurrencyCode);
			ConvertMarketDataResponse convertMarketDataResponse = stockMarketServicePort
					.convertMarketData(convertMarketRequest);
			eventSetID = convertMarketDataResponse.getEventSetId();

			// call downloadFile service to get the file contents
			DownloadFileRequest downloadFileServiceRequest = objectFactory.createDownloadFileRequest();
			downloadFileServiceRequest.setEventSetId(eventSetID);
			DownloadFileResponse downloadFileResponse = stockMarketServicePort.downloadFile(downloadFileServiceRequest);

			// Parse the response
			byte[] respReturnData = downloadFileResponse.getReturnData();
			String encoded = Base64.encodeBase64String(respReturnData);
			byte[] decoded = Base64.decodeBase64(encoded);
			fileContents = new String(decoded, "UTF-8");
			errorIndicator = false;
		} catch (MalformedURLException e) {
			// URL error
			errorMessage = "URL Parse error during execution" + e.getMessage();
		} catch (UnsupportedEncodingException e) {
			// Encoding error
			errorMessage = "Unsupported encoding  encountered during execution" + e.getMessage();
		} catch (ConvertMarketDataFaultMsg e) {
			// Error message from downloadFile()
			ServiceFaultType serviceFault = e.getFaultInfo();
			errorMessage = serviceFault.getErrorDescription();
		} catch (DownloadFileFaultMsg e) {
			// Error message from downloadFile()
			ServiceFaultType serviceFault = e.getFaultInfo();
			errorMessage = serviceFault.getErrorDescription();
		}

		if (errorIndicator == false) {
			// Set the response attributes
			request.setAttribute("serviceResponseContents", fileContents);
			request.setAttribute("responseStatus", "success");
			// add the eventsetid to the session list
			ArrayList<String> eventIDList = (ArrayList<String>) session.getAttribute("eventSetIds");
			eventIDList.add(eventSetID);
			session.setAttribute("eventSetIds", eventIDList);
		} else {
			request.setAttribute("serviceResponseContents",
					"An error occurred while processing youre request: " + errorMessage);
			request.setAttribute("responseStatus", "error");
		}
		response.setContentType("text/html;charset=UTF-8");
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
		rd.forward(request, response);

	}

	private void importMarketData(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		// get the request parameters
		String SECCode = getRequestParameter(request, "SECCode");
		String StartDate = getRequestParameter(request, "StartDate");
		String EndDate = getRequestParameter(request, "EndDate");
		String targetCurrencyCode = getRequestParameter(request, "targetCurrencyCode");
		String dataSourceURL = getRequestParameter(request, "dataSourceURL");
		// response variables
		String eventSetID = new String();
		String fileContents = new String();
		boolean errorIndicator = true;
		String errorMessage = new String();

		// Create the Request
		StockMarketDataImplService marketDataServiceImpl = new StockMarketDataImplService(new URL(wsdlURL));
		StockMarketData stockMarketServicePort = marketDataServiceImpl.getStockMarketDataImplPort();
		ObjectFactory objectFactory = new ObjectFactory();
		ImportMarketDataRequest importMarketDataServiceRequest = objectFactory.createImportMarketDataRequest();
		// set the request variables
		importMarketDataServiceRequest.setSec(SECCode);
		importMarketDataServiceRequest.setStartDate(StartDate);
		importMarketDataServiceRequest.setEndDate(EndDate);
		importMarketDataServiceRequest.setDataSource(dataSourceURL);

		try {
			ImportMarketDataResponse importMarketDataServiceResponse = stockMarketServicePort
					.importMarketData(importMarketDataServiceRequest);
			eventSetID = importMarketDataServiceResponse.getEventSetId();

			// Call convertMarketData() service if conversion is specified
			if (targetCurrencyCode != null && !targetCurrencyCode.isEmpty()) {
				ConvertMarketDataRequest convertMarketRequest = objectFactory.createConvertMarketDataRequest();
				convertMarketRequest.setEventSetId(eventSetID);
				convertMarketRequest.setTargetCurrency(targetCurrencyCode);
				ConvertMarketDataResponse convertMarketDataResponse = stockMarketServicePort
						.convertMarketData(convertMarketRequest);
				eventSetID = convertMarketDataResponse.getEventSetId();

			}

			// call downloadFile service to get the file contents
			DownloadFileRequest downloadFileServiceRequest = objectFactory.createDownloadFileRequest();
			downloadFileServiceRequest.setEventSetId(eventSetID);
			DownloadFileResponse downloadFileResponse = stockMarketServicePort.downloadFile(downloadFileServiceRequest);

			// Parse the response
			byte[] respReturnData = downloadFileResponse.getReturnData();
			String encoded = Base64.encodeBase64String(respReturnData);
			byte[] decoded = Base64.decodeBase64(encoded);
			fileContents = new String(decoded, "UTF-8");
			errorIndicator = false;

		} catch (ImportMarketDataFaultMsg e) {
			// Error message from importMarketData()
			ServiceFaultType serviceFault = e.getFaultInfo();
			errorMessage = serviceFault.getErrorDescription();
		} catch (ConvertMarketDataFaultMsg e) {
			// Error message from convertMarketData()
			ServiceFaultType serviceFault = e.getFaultInfo();
			errorMessage = serviceFault.getErrorDescription();
		} catch (DownloadFileFaultMsg e) {
			// Error message from downloadFile()
			ServiceFaultType serviceFault = e.getFaultInfo();
			errorMessage = serviceFault.getErrorDescription();
		} catch (UnsupportedEncodingException e) {
			// Other error
			errorMessage = "Unexpected error during execution" + e.getMessage();
		}

		if (errorIndicator == false) {
			// Set the response attributes
			request.setAttribute("serviceResponseContents", fileContents);
			request.setAttribute("responseStatus", "success");
			// add the eventsetid to the session list
			ArrayList<String> eventIDList = (ArrayList<String>) session.getAttribute("eventSetIds");
			eventIDList.add(eventSetID);
			session.setAttribute("eventSetIds", eventIDList);
		} else {
			request.setAttribute("serviceResponseContents",
					"An error occurred while processing youre request: " + errorMessage);
			request.setAttribute("responseStatus", "error");
		}
		response.setContentType("text/html;charset=UTF-8");
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
		rd.forward(request, response);
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
