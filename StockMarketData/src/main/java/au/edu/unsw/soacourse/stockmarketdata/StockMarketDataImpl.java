package au.edu.unsw.soacourse.stockmarketdata;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.jws.WebService;

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
import au.edu.unsw.soacourse.stockmarketdata.YahooExchangeRateFaultMsg;

@WebService(endpointInterface = "au.edu.unsw.soacourse.stockmarketdata.StockMarketData")
public class StockMarketDataImpl implements StockMarketData {

	// Object factory declaration
	ObjectFactory objfactory = new ObjectFactory();

	public ServiceFaultType checkForTempFile(String eventSetId) {
		String fileName = System.getProperty("java.io.tmpdir") + File.separator + eventSetId + ".csv";
		File requestedFile = new File(fileName);
		ServiceFaultType faultResponse = objfactory.createServiceFaultType();

		if (!requestedFile.exists()) {
			faultResponse.setErrorCode("503");
			faultResponse.setErrorDescription(
					"InvalidEventSetId - the input eventSetId is not known on the server repository");
			return faultResponse;
		}

		return null;
	}

	public ServiceFaultType checkForProcessedTempFile(String eventSetId) {
		String fileName = System.getProperty("java.io.tmpdir") + File.separator + eventSetId + "_PROCESSED.csv";
		File requestedFile = new File(fileName);
		ServiceFaultType faultResponse = objfactory.createServiceFaultType();

		if (requestedFile.exists()) {
			faultResponse.setErrorCode("401");
			faultResponse.setErrorDescription("InvalidEventSetId - The file already contains converted prices");
			return faultResponse;
		}

		return null;
	}

	public ImportMarketDataResponse importMarketData(ImportMarketDataRequest req) throws ImportMarketDataFaultMsg {

		// Create and set ImportMarketResponse object
		ImportMarketDataResponse resp = objfactory.createImportMarketDataResponse();

		// Variables to read the file off the internet
		URL sourceURL;
		InputStream dataInputStream = null;
		DataInputStream sourceInputStream;
		String sourceLine;

		// Variable to generate a randomized file name
		UUID uuid = UUID.randomUUID();
		String tempFileName = uuid.toString();
		BufferedWriter tempFileWriter = null;

		// Variables for the validation check
		boolean validSecCode = false;
		boolean validFileRead = false;

		// Creating the temp file
		try {
			File temp = File.createTempFile(tempFileName, ".csv");
			tempFileName = temp.getName();
			tempFileName = tempFileName.substring(0, tempFileName.length() - 4);

			tempFileWriter = new BufferedWriter(new FileWriter(temp));

			// Input stream for the data source specified
			sourceURL = new URL(req.getDataSource());
			dataInputStream = sourceURL.openStream();
			sourceInputStream = new DataInputStream(new BufferedInputStream(dataInputStream));

			// Date format declaration
			SimpleDateFormat recordParseFormat = new SimpleDateFormat("dd-MMM-yyyy");
			SimpleDateFormat requestParseFormat = new SimpleDateFormat("dd-MM-yyyy");
			// Parse the request dates to native java dates
			Date requestStartDate = requestParseFormat.parse(req.getStartDate());
			Date requestEndDate = requestParseFormat.parse(req.getEndDate());

			// check if the start date is after the end date
			if (requestStartDate.after(requestEndDate)) {
				ServiceFaultType faultResponse = objfactory.createServiceFaultType();
				faultResponse.setErrorCode("400");
				faultResponse.setErrorDescription("InvalidDates - the end date must be before the start date");
				ImportMarketDataFaultMsg faultResp = new ImportMarketDataFaultMsg("Parse Error", faultResponse);
				throw faultResp;
			}

			// Write the first header line
			tempFileWriter
					.write("#RIC,Date[G],Time[G],GMT Offset,Type,Price,Volume,Bid Price,Bid Size,Ask Price,Ask Size\n");

			// Read content from the data source
			while ((sourceLine = sourceInputStream.readLine()) != null) {

				// Split the source file line to components
				String[] dataComponent = sourceLine.split(",", -1);
				if (req.getSec().equals(dataComponent[0])) {
					validSecCode = true;// set that the SEC code is valid

					try {
						// parse the record line date to a java native date
						Date recordLineDate = recordParseFormat.parse(dataComponent[1]);
						// check if the record is within the date range
						if ((recordLineDate.after(requestStartDate) && recordLineDate.before(requestEndDate))
								|| (recordLineDate.equals(requestStartDate))
								|| (recordLineDate.equals(requestEndDate))) {
							tempFileWriter.write(sourceLine + "\n");
							validFileRead = true;// set that there was atleast
													// one record written to a
													// file
						}
					} catch (ParseException e) {
						// Parse exception when the date conversion fails for a
						// record line date
						ServiceFaultType faultResponse = objfactory.createServiceFaultType();
						faultResponse.setErrorCode("422");
						faultResponse.setErrorDescription(
								"InvalidDateFormat - the date value read from the data source file is of an invalid format");
						ImportMarketDataFaultMsg faultResp = new ImportMarketDataFaultMsg("Parse Error", faultResponse);
						throw faultResp;
					}
				}
			}

			// Check to see if the service retrieved a
			// valid response for the SEC code sent in the request
			if (!validSecCode) {
				ServiceFaultType faultResponse = objfactory.createServiceFaultType();
				faultResponse.setErrorCode("400");
				faultResponse.setErrorDescription(
						"InvalidSECCode - the input sec is not known on the server repository, or not in the expected format");
				ImportMarketDataFaultMsg faultResp = new ImportMarketDataFaultMsg("Validation Error", faultResponse);
				throw faultResp;
			}
			// Check to see if the input request combination
			// retrieved atleast a row to be written to the file
			if (!validFileRead) {
				ServiceFaultType faultResponse = objfactory.createServiceFaultType();
				faultResponse.setErrorCode("400");
				faultResponse.setErrorDescription(
						"InvalidInputContent - the input combination did not retrieve any data from the server repository");
				ImportMarketDataFaultMsg faultResp = new ImportMarketDataFaultMsg("Validation Error", faultResponse);
				throw faultResp;
			}

			// Close the data streams
			dataInputStream.close();
			tempFileWriter.close();

		} catch (MalformedURLException mue) {
			// Error in parsing the dataSource [URL]
			ServiceFaultType faultResponse = objfactory.createServiceFaultType();
			faultResponse.setErrorCode("400");
			faultResponse.setErrorDescription("InvalidURL - the URL of the request message is malformed");
			ImportMarketDataFaultMsg faultResp = new ImportMarketDataFaultMsg("Validation Error", faultResponse);
			throw faultResp;
		} catch (ParseException e1) {
			// Error in parsing the input start date or end date
			ServiceFaultType faultResponse = objfactory.createServiceFaultType();
			faultResponse.setErrorCode("400");
			faultResponse.setErrorDescription("InvalidDate - the input value for startDate or endDate is invalid");
			ImportMarketDataFaultMsg faultResp = new ImportMarketDataFaultMsg("Validation Error", faultResponse);
			throw faultResp;
		} catch (ImportMarketDataFaultMsg faultMsg) {
			// Propagates any error thrown in the try block
			ServiceFaultType faultResponse = faultMsg.getFaultInfo();
			ImportMarketDataFaultMsg faultResp = new ImportMarketDataFaultMsg("Validation Error", faultResponse);
			throw faultResp;
		} catch (Exception ioe) {
			// Handles any internal errors
			ServiceFaultType faultResponse = objfactory.createServiceFaultType();
			faultResponse.setErrorCode("500");
			faultResponse
					.setErrorDescription("ProgramError - an unexpected error occurred while processing the request: "
							+ ioe.getLocalizedMessage());
			ImportMarketDataFaultMsg faultResp = new ImportMarketDataFaultMsg("Program Error", faultResponse);
			throw faultResp;
		}

		// Set the response
		resp.setEventSetId(tempFileName);

		// return the response to the user
		return resp;

	}

	public DownloadFileResponse downloadFile(DownloadFileRequest req) throws DownloadFileFaultMsg {
		String eventSetId = req.getEventSetId();
		String filePath = System.getProperty("java.io.tmpdir") + File.separator + req.getEventSetId();
		String fileContent = null;

		// Check if the file exists - either processed or unprocessed
		ServiceFaultType unprocessed = checkForTempFile(eventSetId);
		ServiceFaultType processed = checkForProcessedTempFile(eventSetId);
		if (unprocessed != null && processed == null) {
			DownloadFileFaultMsg faultResp = new DownloadFileFaultMsg("Validation Error", unprocessed);
			throw faultResp;
		}

		// Set the file path
		if (unprocessed == null) {
			filePath = filePath + ".csv";
		} else {
			filePath = filePath + "_PROCESSED.csv";
		}

		// Read file if it exists
		try {
			fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (Exception e) {
			// Handles any internal errors
			ServiceFaultType faultResponse = objfactory.createServiceFaultType();
			faultResponse.setErrorCode("500");
			faultResponse
					.setErrorDescription("ProgramError - an unexpected error occurred while processing the request: "
							+ e.getLocalizedMessage());
			DownloadFileFaultMsg faultResp = new DownloadFileFaultMsg("Program Error", faultResponse);
			throw faultResp;
		}

		DownloadFileResponse resp = objfactory.createDownloadFileResponse();
		resp.setReturnData(fileContent.getBytes());
		return resp;

	}

	public String convertDateFormat(String dateValue) throws ParseException {
		DateFormat originalFormat = new SimpleDateFormat("MM/dd/yyy", Locale.ENGLISH);
		DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = originalFormat.parse(dateValue);
		String formattedDate = targetFormat.format(date);

		return formattedDate;

	}

	@Override
	public ExchangeRateResponse yahooExchangeRate(ExchangeRateRequest req) throws YahooExchangeRateFaultMsg {
		ExchangeRateResponse resp = objfactory.createExchangeRateResponse();

		String yahooURL = "http://finance.yahoo.com/d/quotes.csv?e=.csv&f=sl1d1t1&s=" + req.getBaseCurrencyCode().trim()
				+ req.getTargetCurrencyCode().trim() + "=X";

		// Variables to read the file off the Internet
		URL sourceURL;
		InputStream dataInputStream = null;
		DataInputStream sourceInputStream;
		String sourceLine;

		try {
			sourceURL = new URL(yahooURL);
			dataInputStream = sourceURL.openStream();
			sourceInputStream = new DataInputStream(new BufferedInputStream(dataInputStream));
			while ((sourceLine = sourceInputStream.readLine()) != null) {
				// Split the source file line to components
				String[] dataComponent = sourceLine.split(",");
				if (dataComponent.length < 2 || dataComponent[1].contains("N/A")) {
					ServiceFaultType faultResponse = objfactory.createServiceFaultType();
					faultResponse.setErrorCode("400");
					faultResponse.setErrorDescription(
							"InvalidCurrencyCode - the input currency code is not one of the expected values");
					YahooExchangeRateFaultMsg faultResp = new YahooExchangeRateFaultMsg("Validation Error",
							faultResponse);
					throw faultResp;
				}
				resp.setRate(dataComponent[1]);// set the rate
				resp.setAsAt(convertDateFormat(dataComponent[2].replaceAll("\"", "")));
			}
		} catch (MalformedURLException e) {
			// Handles the message for an Invalid URL
			ServiceFaultType faultResponse = objfactory.createServiceFaultType();
			faultResponse.setErrorCode("400");
			faultResponse.setErrorDescription("InvalidURL - " + e.getMessage());
			YahooExchangeRateFaultMsg faultResp = new YahooExchangeRateFaultMsg("Validation Error", faultResponse);
			throw faultResp;
		} catch (YahooExchangeRateFaultMsg faultMsg) {
			ServiceFaultType faultResponse = faultMsg.getFaultInfo();
			YahooExchangeRateFaultMsg faultResp = new YahooExchangeRateFaultMsg("Validation Error", faultResponse);
			throw faultResp;

		} catch (Exception e) {
			// Handles any internal errors
			ServiceFaultType faultResponse = objfactory.createServiceFaultType();
			faultResponse.setErrorCode("500");
			faultResponse
					.setErrorDescription("ProgramError - an unexpected error occurred while processing the request: "
							+ e.getLocalizedMessage());
			YahooExchangeRateFaultMsg faultResp = new YahooExchangeRateFaultMsg("Program Error", faultResponse);
			throw faultResp;
		}
		return resp;
	}

	public String calculateExchangeRate(String value, Float exchangeRate, String targetCurrency) {
		// Return the exchange rate value
		if (value != null && !value.isEmpty()) {
			Float convertedCurrencyAmount = Float.parseFloat(value) * exchangeRate;
			return targetCurrency + " " + String.format("%.2f", convertedCurrencyAmount);
		}
		return "";
	}

	@Override
	public ConvertMarketDataResponse convertMarketData(ConvertMarketDataRequest req) throws ConvertMarketDataFaultMsg {
		// Temporary file creation
		String tempFileName = null;
		String fileName = System.getProperty("java.io.tmpdir") + File.separator + req.getEventSetId();

		File requestedFile = new File(fileName + ".csv");

		// Check for a processed file
		ServiceFaultType processedFaultResponse = checkForProcessedTempFile(req.getEventSetId());
		ServiceFaultType unprocessedFaultResponse = checkForTempFile(req.getEventSetId());

		if (processedFaultResponse != null) {
			ConvertMarketDataFaultMsg faultResp = new ConvertMarketDataFaultMsg("Validation Error",
					processedFaultResponse);
			throw faultResp;
		} else if (unprocessedFaultResponse != null) {
			ConvertMarketDataFaultMsg faultResp = new ConvertMarketDataFaultMsg("Validation Error",
					unprocessedFaultResponse);
			throw faultResp;
		}

		try {
			// Create the request for the service yahooExchangeRate()
			ExchangeRateRequest yahooRequest = objfactory.createExchangeRateRequest();
			yahooRequest.setBaseCurrencyCode("AUD");
			yahooRequest.setTargetCurrencyCode(req.getTargetCurrency());

			// Get the current exchange rate value
			ExchangeRateResponse yahooResponse = yahooExchangeRate(yahooRequest);
			Float exchangeRate = Float.parseFloat(yahooResponse.getRate());

			// Read requested file
			FileReader fileReader = new FileReader(requestedFile);
			BufferedReader buffer = new BufferedReader(fileReader);
			String sCurrentLine;
			buffer.readLine();// Skip the first line

			// Variable to generate a randomized file name
			UUID uuid = UUID.randomUUID();
			tempFileName = uuid.toString();
			BufferedWriter tempFileWriter = null;
			File temp = File.createTempFile(tempFileName, "_PROCESSED.csv");
			tempFileWriter = new BufferedWriter(new FileWriter(temp));

			tempFileName = temp.getName();
			tempFileName = tempFileName.substring(0, tempFileName.length() - 14);

			// write the header
			tempFileWriter
					.write("#RIC,Date[G],Time[G],GMT Offset,Type,Price,Volume,Bid Price,Bid Size,Ask Price,Ask Size\n");

			while ((sCurrentLine = buffer.readLine()) != null) {
				// Split the line to components
				String[] dataComponent = sCurrentLine.split(",", -1);
				// Convert currency value

				dataComponent[5] = calculateExchangeRate(dataComponent[5], exchangeRate, req.getTargetCurrency());
				dataComponent[7] = calculateExchangeRate(dataComponent[7], exchangeRate, req.getTargetCurrency());
				dataComponent[9] = calculateExchangeRate(dataComponent[9], exchangeRate, req.getTargetCurrency());
				// Write to file
				String data = Arrays.toString(dataComponent);
				tempFileWriter.write(data.substring(1, data.length() - 1) + "\n");

			}
			buffer.close();
			tempFileWriter.close();

		} catch (YahooExchangeRateFaultMsg e) {
			// Error propagation from the yahooExchangeRate() service
			ServiceFaultType faultResp = e.getFaultInfo();
			ConvertMarketDataFaultMsg faultRes = new ConvertMarketDataFaultMsg("Validation Error", faultResp);
			throw faultRes;
		} catch (Exception e) {
			// Handles any internal error
			ServiceFaultType faultRes = objfactory.createServiceFaultType();
			faultRes.setErrorCode("500");
			faultRes.setErrorDescription("ProgramError - an unexpected error occurred while processing the request: "
					+ e.getLocalizedMessage());
			ConvertMarketDataFaultMsg faultResp = new ConvertMarketDataFaultMsg("Program Error", faultRes);
			throw faultResp;
		}

		ConvertMarketDataResponse resp = objfactory.createConvertMarketDataResponse();
		resp.setEventSetId(tempFileName);
		return resp;

	}
}