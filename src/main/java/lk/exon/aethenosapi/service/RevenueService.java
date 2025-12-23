package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.payload.request.GetInstructorMonthlyRevenueByMonthRequest;
import lk.exon.aethenosapi.payload.request.GetInstructorRevenueForMonthByTodayRequest;
import lk.exon.aethenosapi.payload.response.*;

import java.util.List;

public interface RevenueService {
    InstructorTotalRevenueResponse getInstructorTotalRevenueForThisMonth();

    GetInstructorRevenueOverviewResponse getInstructorRevenueOverview();

   GetInstructorRevenueReportResponse getInstructorRevenueReport();

    InstructorMonthlyRevenueReportResponse getInstructorMonthlyRevenueExpandedReport(String month);

    List<GetAllInstructorsPaypalOrPayoneerDetailsForManagePaymentsResponse> getAllInstructorsPaypalOrPayoneerDetailsForManagePayments();

    List<GetAllInstructorsUkBankDetailsForManagePaymentsResponse> getAllInstructorsUkBankDetailsForManagePayments();

    GetInstructorRevenueReportChartResponse getInstructorRevenueReportChart();

    GetInstructorChartForThisMonthResponse getInstructorChartDetailsForThisMonth();

    List<GetAllInstructorDetailsResponse> getAllInstructorDetails();

    GetInstructorChartForThisMonthResponse getInstructorMonthlyRevenueByMonth(GetInstructorMonthlyRevenueByMonthRequest getInstructorMonthlyRevenueByMonthRequest);

    List<GetThreeMonthRevenueResponse> getInstructorRevenueReportForThreeMonth(GetInstructorMonthlyRevenueByMonthRequest getInstructorMonthlyRevenueByMonthRequest);

    List<GetTwelveMonthRevenueResponse> getInstructorRevenueReportFortwelveMonth(GetInstructorMonthlyRevenueByMonthRequest getInstructorMonthlyRevenueByMonthRequest);

    GetInstructorRevenueForMonthByTodayResponse getInstructorRevenueForMonthByToday(GetInstructorRevenueForMonthByTodayRequest getInstructorRevenueForMonthByTodayRequest);
}
