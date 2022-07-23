package com.sounak.coronavirustracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.sounak.coronavirustracker.models.LocationStats;

@Service
public class CoronavirusDataService {

	private List<LocationStats> allStats= new ArrayList<LocationStats>();

	public List<LocationStats> getAllStats() {
		return allStats;
	}

	public static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException {
		List<LocationStats> newStats = new ArrayList<LocationStats>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().
				uri(URI.create(VIRUS_DATA_URL))
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		StringReader csvReader = new StringReader(response.body());
		
		Iterable<CSVRecord> records = CSVFormat.Builder.create(CSVFormat.DEFAULT)
		        .setHeader()
		        .setSkipHeaderRecord(true).build().parse(csvReader);
		for (CSVRecord record : records) {
			LocationStats stat = new LocationStats();
		    stat.setState( record.get("Province/State"));
		    stat.setCountry(record.get("Country/Region"));
			int currentCases = Integer.parseInt(record.get(record.size() - 1));
			int prevCases = Integer.parseInt(record.get(record.size() - 2));
		    stat.setLatestTotalCases(currentCases - prevCases);

			System.out.println(stat);
		    
		    newStats.add(stat);
		}
		this.allStats = newStats;		
	}
}
