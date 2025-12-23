package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.payload.request.GetInstructorMonthlyRevenueByMonthRequest;
import lk.exon.aethenosapi.payload.request.GetInstructorRevenueForMonthByTodayRequest;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/revenue")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RevenueController {
    @Autowired
    private RevenueService revenueService;

    @GetMapping("/getInstructorTotalRevenueForThisMonth")
    public InstructorTotalRevenueResponse getInstructorTotalRevenueForThisMonth() {
        return revenueService.getInstructorTotalRevenueForThisMonth();
    }

    @GetMapping("/getInstructorRevenueOverview")
    public GetInstructorRevenueOverviewResponse getInstructorRevenueOverview() {
        return revenueService.getInstructorRevenueOverview();
    }

    @GetMapping("/getInstructorRevenueReport")
    public GetInstructorRevenueReportResponse getInstructorRevenueReport() {
        return revenueService.getInstructorRevenueReport();
    }
    @GetMapping("/getInstructorRevenueReportForThreeMonth")
    public List<GetThreeMonthRevenueResponse> getInstructorRevenueReportForThreeMonth(GetInstructorMonthlyRevenueByMonthRequest getInstructorMonthlyRevenueByMonthRequest) {
        return revenueService.getInstructorRevenueReportForThreeMonth(getInstructorMonthlyRevenueByMonthRequest);
    }
    @GetMapping("/getInstructorRevenueReportFortwelveMonth")
    public List<GetTwelveMonthRevenueResponse> getInstructorRevenueReportFortwelveMonth(GetInstructorMonthlyRevenueByMonthRequest getInstructorMonthlyRevenueByMonthRequest) {
        return revenueService.getInstructorRevenueReportFortwelveMonth(getInstructorMonthlyRevenueByMonthRequest);
    }

    @GetMapping("/getInstructorMonthlyRevenueExpandedReport/{month}")
    public InstructorMonthlyRevenueReportResponse getInstructorMonthlyRevenueExpandedReport(@PathVariable("month") String month) {
        return revenueService.getInstructorMonthlyRevenueExpandedReport(month);
    }

    @GetMapping("/getAllInstructorsPaypalOrPayoneerDetailsForManagePayments")
    public List<GetAllInstructorsPaypalOrPayoneerDetailsForManagePaymentsResponse> getAllInstructorsPaypalOrStripeDetailsForManagePayments() {
        return revenueService.getAllInstructorsPaypalOrPayoneerDetailsForManagePayments();
    }

    @GetMapping("/getAllInstructorsUkBankDetailsForManagePayments")
    public List<GetAllInstructorsUkBankDetailsForManagePaymentsResponse> getAllInstructorsUkBankDetailsForManagePayments() {
        return revenueService.getAllInstructorsUkBankDetailsForManagePayments();
    }

    @GetMapping("/getInstructorRevenueReportChart")
    public GetInstructorRevenueReportChartResponse getInstructorRevenueReportChart() {
        return revenueService.getInstructorRevenueReportChart();
    }

    @GetMapping("/getInstructorChartDetailsForThisMonth")
    public GetInstructorChartForThisMonthResponse getInstructorChartDetailsForThisMonth() {
        return revenueService.getInstructorChartDetailsForThisMonth();
    }
    @GetMapping("/getAllInstructorDetails")
    public List<GetAllInstructorDetailsResponse> getAllInstructorDetails() {
        return revenueService.getAllInstructorDetails();
    }
    @GetMapping("/getInstructorMonthlyRevenueByMonth")
    public GetInstructorChartForThisMonthResponse getInstructorMonthlyRevenueByMonth(GetInstructorMonthlyRevenueByMonthRequest getInstructorMonthlyRevenueByMonthRequest) {
        return revenueService.getInstructorMonthlyRevenueByMonth(getInstructorMonthlyRevenueByMonthRequest);
    }
    @GetMapping("/getInstructorRevenueForMonthByToday")
    public GetInstructorRevenueForMonthByTodayResponse getInstructorRevenueForMonthByToday(GetInstructorRevenueForMonthByTodayRequest getInstructorRevenueForMonthByTodayRequest) {
        return revenueService.getInstructorRevenueForMonthByToday(getInstructorRevenueForMonthByTodayRequest);
    }

}
