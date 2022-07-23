package com.sounak.coronavirustracker.controllers;

import com.sounak.coronavirustracker.models.LocationStats;
import com.sounak.coronavirustracker.services.CoronavirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronavirusDataService coronavirusDataService;
    @GetMapping("/")
    public String Home(Model model)
    {

        List<LocationStats> allStats = coronavirusDataService.getAllStats();
        int totalReportedCases = allStats.stream().mapToInt(LocationStats::getLatestTotalCases).sum();
        model.addAttribute("totalCases", totalReportedCases);
        model.addAttribute("location", allStats);
        return "home";
    }
}
