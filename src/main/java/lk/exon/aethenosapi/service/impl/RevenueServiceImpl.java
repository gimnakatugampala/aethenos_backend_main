package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.GetInstructorMonthlyRevenueByMonthRequest;
import lk.exon.aethenosapi.payload.request.GetInstructorRevenueForMonthByTodayRequest;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.repository.*;
import lk.exon.aethenosapi.service.RevenueService;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.*;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RevenueServiceImpl implements RevenueService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private InstructorProfileRepository instructorProfileRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private OrderHasCourseRepository orderHasCourseRepository;
    @Autowired
    private VatRepository vatRepository;
    @Autowired
    private InstructorRevenueRepository instructorRevenueRepository;
    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private RefundsRepository refundsRepository;
    @Autowired
    private InstructorPaymentsRepository instructorPaymentsRepository;
    @Autowired
    private RevenueSplitRepository revenueSplitRepository;
    @Autowired
    private RevenueSplitTypeRepository revenueSplitTypeRepository;
    @Autowired
    private InstructorCourseRevenueRepository instructorCourseRevenueRepository;
    @Autowired
    private ApprovedRefundsRepository approvedRefundsRepository;
    @Autowired
    private RevenueRepository revenueRepository;
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;
    @Autowired
    private StudentBuyCouponCourseRepository studentBuyCouponCourseRepository;

    private static final String API_URL = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/{currencyCode}.json";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter updatedFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    DateTimeFormatter dateUpdatedFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    private Map<String, Double> getExchangeRates(String currencyCode) {
        try {
            String url = API_URL.replace("{currencyCode}", currencyCode.toLowerCase());
            Map<String, Map<String, Double>> response = restTemplate.getForObject(url, Map.class);
            return response != null ? response.get(currencyCode.toLowerCase()) : new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private LocalDate convertToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public InstructorTotalRevenueResponse getInstructorTotalRevenueForThisMonth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                if (instructorProfile != null) {
                    LocalDate currentDate = LocalDate.now();
                    YearMonth twoMonthsAgoYearMonth = YearMonth.from(currentDate).minusMonths(2);
                    InstructorRevenue instructorRevenue = instructorRevenueRepository.getInstructorRevenueByInstructorProfileAndRevenueForMonthAndRevenueForYear(instructorProfile, twoMonthsAgoYearMonth.getMonthValue(), twoMonthsAgoYearMonth.getYear());
                    InstructorTotalRevenueResponse instructorTotalRevenueResponse = new InstructorTotalRevenueResponse();
                    if (instructorRevenue != null) {
                        instructorTotalRevenueResponse.setGrossRevenue(instructorRevenue.getGrossRevenue());
                        instructorTotalRevenueResponse.setNetRevenue(instructorRevenue.getNetRevenue());
                        instructorTotalRevenueResponse.setRevenueCalculateDate(instructorRevenue.getRevenueCalculateDate());
                        instructorTotalRevenueResponse.setRevenueForYear(instructorRevenue.getRevenueForYear());
                        instructorTotalRevenueResponse.setRevenueForMonth(instructorRevenue.getRevenueForMonth());
                    }
                    return instructorTotalRevenueResponse;
                } else {
                    throw new ErrorException("No such profile found", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GetInstructorRevenueOverviewResponse getInstructorRevenueOverview() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                if (instructorProfile != null) {
                    List<InstructorCourseRevenue> instructorCourseRevenues = instructorCourseRevenueRepository.getInstructorCourseRevenueByInstructorProfile(instructorProfile);
                    double totalRevenue = 0;

                    LocalDate currentDate = LocalDate.now();
                    YearMonth currentYearMonth = YearMonth.from(currentDate);

                    LocalDate startOfMonth = currentYearMonth.atDay(1);
                    LocalDate endOfMonth = currentYearMonth.atEndOfMonth();
                    double thisMonthRevenue = 0;
                    for (InstructorCourseRevenue instructorCourseRevenue : instructorCourseRevenues) {
                        if (!instructorCourseRevenue.getRevenue().isRefunded()) {

                            // Using usdRate for all conversions since raw DB data is in Student Currency
                            double earning = instructorCourseRevenue.getInstructorShare() * instructorCourseRevenue.getRevenue().getTransaction().getUsdRate();
                            totalRevenue += earning;

                            LocalDate createdDate = convertToLocalDate(instructorCourseRevenue.getRevenue().getCreatedDate());
                            if (createdDate != null && !createdDate.isBefore(startOfMonth) && !createdDate.isAfter(endOfMonth)) {
                                thisMonthRevenue += earning;
                            }
                        }
                    }

                    GetInstructorRevenueOverviewResponse getInstructorRevenueOverviewResponse = new GetInstructorRevenueOverviewResponse();
                    getInstructorRevenueOverviewResponse.setTotalRevenue(totalRevenue);
                    getInstructorRevenueOverviewResponse.setThisMonthRevenue(thisMonthRevenue);

                    List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);
                    int totalEnrollments = 0;
                    int thisMonthEnrollments = 0;
                    double instructorRating = 0;
                    int thisMonthRating = 0;
                    int countRating = 0;
                    for (Course course : courses) {
                        List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);
                        for (OrderHasCourse orderHasCourse : orderHasCourses) {

                            Review review = reviewRepository.getReviewsByOrderHasCourse(orderHasCourse);

                            if (review != null) {
                                instructorRating += review.getRating();
                                countRating++;
                                Date reviewDate = review.getDate();
                                LocalDate reviewLocalDate = convertToLocalDate(reviewDate);
                                if (reviewLocalDate != null && (reviewLocalDate.isEqual(startOfMonth) || reviewLocalDate.isAfter(startOfMonth))
                                        && (reviewLocalDate.isEqual(endOfMonth) || reviewLocalDate.isBefore(endOfMonth))) {
                                    thisMonthRating++;
                                }
                            }

                            LocalDate buyDate = convertToLocalDate(orderHasCourse.getOrder().getBuyDate());

                            if (buyDate != null && (buyDate.isEqual(startOfMonth) || buyDate.isAfter(startOfMonth))
                                    && (buyDate.isEqual(endOfMonth) || buyDate.isBefore(endOfMonth))) {
                                thisMonthEnrollments++;
                            }

                            totalEnrollments++;
                        }
                    }
                    getInstructorRevenueOverviewResponse.setTotalEnrollments(totalEnrollments);
                    getInstructorRevenueOverviewResponse.setThisMonthEnrollments(thisMonthEnrollments);
                    getInstructorRevenueOverviewResponse.setInstructorRating(instructorRating / countRating);
                    getInstructorRevenueOverviewResponse.setThisMonthRating(thisMonthRating);
                    return getInstructorRevenueOverviewResponse;
                } else {
                    throw new ErrorException("No such profile found", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GetInstructorRevenueReportResponse getInstructorRevenueReport() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }

        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }

        InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);

        if (instructorProfile != null) {
            List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);

            GetInstructorRevenueReportResponse getInstructorRevenueReportResponse = new GetInstructorRevenueReportResponse();

            Map<YearMonth, Double> monthlyEarningsMap = new LinkedHashMap<>();
            Map<YearMonth, LocalDate> representativeDateMap = new HashMap<>();
            double totalLifeTimeEarning = 0;

            for (Course course : courses) {
                List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);

                for (OrderHasCourse orderHasCourse : orderHasCourses) {
                    Revenue revenue = revenueRepository.getRevenueByOrderHasCourse(orderHasCourse);
                    if (revenue != null && !revenue.isRefunded()) {
                        LocalDate createdLocalDate = convertToLocalDateViaInstant(revenue.getCreatedDate());
                        InstructorCourseRevenue instructorCourseRevenue = instructorCourseRevenueRepository.getInstructorCourseRevenueByRevenue(revenue);

                        // Using usdRate for conversion
                        double earning = instructorCourseRevenue.getInstructorShare() * revenue.getTransaction().getUsdRate();
                        totalLifeTimeEarning += earning;

                        YearMonth ym = YearMonth.from(createdLocalDate);
                        monthlyEarningsMap.put(ym, monthlyEarningsMap.getOrDefault(ym, 0.0) + earning);
                        representativeDateMap.putIfAbsent(ym, createdLocalDate);
                    }
                }
            }

            List<InstructorRevenueReportResponse> instructorRevenueReportResponseList = new ArrayList<>();
            for (Map.Entry<YearMonth, Double> entry : monthlyEarningsMap.entrySet()) {
                YearMonth ym = entry.getKey();
                double monthTotalEarning = entry.getValue();
                LocalDate representativeDate = representativeDateMap.get(ym);

                InstructorRevenueReportResponse reportResponse = createInstructorRevenueReportResponse(
                        ym.getMonth(),
                        monthTotalEarning,
                        representativeDate
                );

                instructorRevenueReportResponseList.add(reportResponse);
            }

            instructorRevenueReportResponseList.sort((r1, r2) -> {
                LocalDate d1 = LocalDate.parse(r1.getMonth() + " 01", DateTimeFormatter.ofPattern("MMMM yyyy dd", Locale.ENGLISH));
                LocalDate d2 = LocalDate.parse(r2.getMonth() + " 01", DateTimeFormatter.ofPattern("MMMM yyyy dd", Locale.ENGLISH));
                return d2.compareTo(d1);
            });

            getInstructorRevenueReportResponse.setInstructorRevenueReportResponses(instructorRevenueReportResponseList);
            getInstructorRevenueReportResponse.setTotalLifeTimeEarning("USD " + decimalFormat.format(totalLifeTimeEarning));
            getInstructorRevenueReportResponse.setDate(getToday());

            return getInstructorRevenueReportResponse;
        } else {
            throw new ErrorException("No such profile found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private InstructorRevenueReportResponse createInstructorRevenueReportResponse(Month month, double monthTotalEarning, LocalDate createdDate) {
        InstructorRevenueReportResponse instructorRevenueReportResponse = new InstructorRevenueReportResponse();

        String monthYear = month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + createdDate.getYear();
        instructorRevenueReportResponse.setId(month.getValue());
        instructorRevenueReportResponse.setMonth(monthYear);
        instructorRevenueReportResponse.setYourRevenue(decimalFormat.format(monthTotalEarning));

        instructorRevenueReportResponse.setExpectedPaymentDate(
                createdDate.withDayOfMonth(1).plusMonths(2).withDayOfMonth(7).format(updatedFormatter)
        );

        return instructorRevenueReportResponse;
    }

    private String getToday() {
        LocalDate today = new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        Month month = today.getMonth();
        int day = today.getDayOfMonth();
        int year = today.getYear();
        return month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + day + " " + year;
    }

    private static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    @Override
    public InstructorMonthlyRevenueReportResponse getInstructorMonthlyRevenueExpandedReport(String month) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }

        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }

        InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);

        if (instructorProfile == null) {
            throw new ErrorException("No such profile found", VarList.RSP_NO_DATA_FOUND);
        }

        LocalDate startOfMonth = parseMonthYearToLocalDate(month);
        LocalDate endOfMonth = getEndOfMonth(startOfMonth);

        List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);

        List<GetInstructorMonthlyRevenueExpandedReportResponse> monthlyRevenueReports = new ArrayList<>();
        List<GetMonthlyInstructorRefundsResponse> monthlyRefunds = new ArrayList<>();

        double totalPurchases = 0;
        double totalRefunds = 0;

        for (Course course : courses) {
            List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);

            for (OrderHasCourse orderHasCourse : orderHasCourses) {
                Revenue revenue = revenueRepository.getRevenueByOrderHasCourse(orderHasCourse);
                if (revenue != null) {
                    InstructorCourseRevenue instructorCourseRevenue = instructorCourseRevenueRepository.getInstructorCourseRevenueByRevenue(revenue);
                    if (instructorCourseRevenue != null) {
                        LocalDate createdLocalDate = revenue.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                        if (!createdLocalDate.isBefore(startOfMonth) && !createdLocalDate.isAfter(endOfMonth)) {
                            if (revenue.isRefunded()) {
                                Refunds refunds = refundsRepository.getRefundsByOrderHasCourse(revenue.getOrderHasCourse());
                                if (refunds != null) {
                                    GetMonthlyInstructorRefundsResponse refundResponse = new GetMonthlyInstructorRefundsResponse();
                                    refundResponse.setDate(refunds.getRequestDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(updatedFormatter));
                                    refundResponse.setCustomerName(refunds.getGeneralUserProfile().getFirstName() + " " + refunds.getGeneralUserProfile().getLastName());
                                    refundResponse.setCourse(refunds.getOrderHasCourse().getCourse().getCourseTitle());

                                    double refundAmount = instructorCourseRevenue.getInstructorShare() * revenue.getTransaction().getUsdRate();

                                    refundResponse.setRefundsAmount("USD " + decimalFormat.format(refundAmount));
                                    refundResponse.setChangeToYourRevenue("-" + "USD " + decimalFormat.format(refundAmount));
                                    monthlyRefunds.add(refundResponse);
                                    totalRefunds += refundAmount;
                                }
                            }

                            GetInstructorMonthlyRevenueExpandedReportResponse revenueResponse = new GetInstructorMonthlyRevenueExpandedReportResponse();
                            revenueResponse.setDate(revenue.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(dateUpdatedFormatter));
                            revenueResponse.setCourse(revenue.getOrderHasCourse().getCourse().getCourseTitle());

                            double exRate = revenue.getTransaction().getUsdRate();
                            double pricePaid = instructorCourseRevenue.getInstructorShare() * exRate;


                            String stripeCurrency = revenue.getTransaction().getStripe_pf_currency();
                            double rawFeeInDb = revenue.getProcessingFee();
                            double stripeRate = revenue.getTransaction().getStripe_pf_exchange_rate();

//                            System.out.println("--- TRANSACTION DEBUG ---");
//                            System.out.println("Raw Processing Fee in DB: " + rawFeeInDb);
//                            System.out.println("USD Rate (exRate): " + exRate);
//                            System.out.println("Stripe PF Currency: " + stripeCurrency);
//                            System.out.println("Stripe PF Exchange Rate: " + stripeRate);
//                            System.out.println("-------------------------");

// --- BULLETPROOF PROCESSING FEE LOGIC ---
                            double paymentProcessingFees = 0.0;


// 1. Safely check if currency is GBP (using .trim() to remove accidental spaces like "gbp ")
                            boolean isGBP = stripeCurrency != null && stripeCurrency.trim().equalsIgnoreCase("gbp");

// 2. Safely check if USD rate is 1
                            boolean isUsdRateOne = Math.abs(exRate - 1.0) < 0.001;

// Rule 1: If usd_rate == 1 AND currency == "gbp"
                            if (isUsdRateOne && isGBP) {
                                paymentProcessingFees = revenue.getProcessingFee() * revenue.getTransaction().getStripe_pf_exchange_rate();
                            }
// Rule 2 & 3: If currency is null, OR usd_rate is not 1
                            else {
                                paymentProcessingFees = revenue.getProcessingFee() * exRate;
                            }
// ----------------------------------------

                            double tax = revenue.getTax() * exRate;
                            double netRevenue = revenue.getNetSale() * exRate;

                            revenueResponse.setPricePaid(revenue.getOrderHasCourse().getCurrrency() + " " + decimalFormat.format(revenue.getOrderHasCourse().getItemPrice()));
                            revenueResponse.setPaymentProcessingFees("USD " + decimalFormat.format(paymentProcessingFees));
                            revenueResponse.setTax("USD " + decimalFormat.format(tax));
                            revenueResponse.setNetRevenue("USD " + decimalFormat.format(netRevenue));

                            revenueResponse.setAppleOrGoogleFees(decimalFormat.format(0));
                            revenueResponse.setCustomerName(revenue.getOrderHasCourse().getOrder().getGeneralUserProfile().getFirstName() + " " + revenue.getOrderHasCourse().getOrder().getGeneralUserProfile().getLastName());

                            String couponCode = "N/A";
                            if (revenue.getOrderHasCourse().getCoursePurchaseType() != null && revenue.getOrderHasCourse().getCoursePurchaseType().getId() == 3) {
                                StudentBuyCouponCourse studentBuyCouponCourse = studentBuyCouponCourseRepository.getStudentBuyCouponCourseByOrderHasCourse(orderHasCourse);
                                if (studentBuyCouponCourse != null) {
                                    couponCode = studentBuyCouponCourse.getCoupon().getCode();
                                } else {
                                    couponCode = "Coupon code included";
                                }
                            }
                            revenueResponse.setCouponCode(couponCode);
                            revenueResponse.setPlatform("Web");

                            String percentageSplit = "0";
                            if (revenue.getOrderHasCourse().getCoursePurchaseType() != null) {
                                int purchaseTypeId = revenue.getOrderHasCourse().getCoursePurchaseType().getId();
                                if (purchaseTypeId == 3 || purchaseTypeId == 4) {
                                    percentageSplit = String.valueOf(revenue.getTransaction().getRevenueSplitHistory().getInstructorRevenueReferralLinkSplit());
                                } else if (purchaseTypeId == 1) {
                                    percentageSplit = String.valueOf(revenue.getTransaction().getRevenueSplitHistory().getInstructorRevenueAethenosSplit());
                                }
                            }
                            revenueResponse.setYourRevenue("USD " + decimalFormat.format(pricePaid) + " (" + percentageSplit + "% )");
                            revenueResponse.setChannel(revenue.getOrderHasCourse().getCoursePurchaseType().getPurchaseType());
                            totalPurchases += pricePaid;
                            monthlyRevenueReports.add(revenueResponse);
                        }
                    }
                }
            }
        }

        try {
            monthlyRevenueReports.sort(Comparator.comparing((GetInstructorMonthlyRevenueExpandedReportResponse r) ->
                            LocalDateTime.parse(r.getDate(), dateUpdatedFormatter))
                    .reversed());

            monthlyRefunds.sort(Comparator.comparing((GetMonthlyInstructorRefundsResponse r) ->
                            LocalDateTime.parse(r.getDate(), dateUpdatedFormatter))
                    .reversed());
        } catch (DateTimeParseException e) {
            throw new ErrorException("Invalid date format in revenue or refund response", VarList.RSP_ERROR);
        }

        InstructorMonthlyRevenueReportResponse response = new InstructorMonthlyRevenueReportResponse();
        response.setPurchases(monthlyRevenueReports);
        response.setTotalPurchases("USD " + decimalFormat.format(totalPurchases));
        response.setRefunds(monthlyRefunds);
        response.setTotalRefunds("USD " + decimalFormat.format(totalRefunds));
        response.setTimePeriod(startOfMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + startOfMonth.getYear());

        return response;
    }

    private LocalDate getEndOfMonth(LocalDate startOfMonth) {
        YearMonth yearMonth = YearMonth.from(startOfMonth);
        return yearMonth.atEndOfMonth();
    }

    private LocalDate parseMonthYearToLocalDate(String monthYear) {
        String[] parts = monthYear.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Input must be in 'Month Year' format");
        }

        String monthName = parts[0];
        int year;
        try {
            year = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Year must be a valid integer", e);
        }

        Month month;
        try {
            month = Month.valueOf(monthName.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Month must be a valid month name", e);
        }

        return LocalDate.of(year, month, 1);
    }

    @Override
    public List<GetAllInstructorsPaypalOrPayoneerDetailsForManagePaymentsResponse> getAllInstructorsPaypalOrPayoneerDetailsForManagePayments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getGupType().getId() != 3) {
            throw new ErrorException("You cannot access it because you are not an administrator", VarList.RSP_NO_DATA_FOUND);
        }

        List<InstructorProfile> instructorProfiles = instructorProfileRepository.getInstructorProfileByIsVerifiedAndIsProfileCompleted(Byte.valueOf("1"), Byte.valueOf("1"));
        List<GetAllInstructorsPaypalOrPayoneerDetailsForManagePaymentsResponse> paymentResponses = new ArrayList<>();
        LocalDate twoMonthsAgo = LocalDate.now().minusMonths(2);

        for (InstructorProfile instructorProfile : instructorProfiles) {
            List<InstructorCourseRevenue> instructorCourseRevenues = instructorCourseRevenueRepository.getInstructorCourseRevenueByInstructorProfile(instructorProfile);
            double totalRevenue = 0;

            for (InstructorCourseRevenue instructorCourseRevenue : instructorCourseRevenues) {
                LocalDate revenueLocalDate = convertToLocalDate(instructorCourseRevenue.getRevenue().getCreatedDate());

                if (YearMonth.from(revenueLocalDate).equals(YearMonth.from(twoMonthsAgo))) {
                    InstructorPayments instructorPayments = instructorPaymentsRepository.getInstructorPaymentsByInstructorProfile(instructorProfile);
                    if (instructorPayments != null && (instructorPayments.getPaymentMethod().getId() == 2 || instructorPayments.getPaymentMethod().getId() == 4)) {
                        totalRevenue += instructorCourseRevenue.getInstructorShare() * instructorCourseRevenue.getRevenue().getTransaction().getUsdRate();
                    }
                }
            }

            if (totalRevenue > 0) {
                InstructorPayments instructorPayments = instructorPaymentsRepository.getInstructorPaymentsByInstructorProfile(instructorProfile);
                if (instructorPayments != null) {
                    GetAllInstructorsPaypalOrPayoneerDetailsForManagePaymentsResponse response = new GetAllInstructorsPaypalOrPayoneerDetailsForManagePaymentsResponse();
                    response.setInstructorName(instructorProfile.getGeneralUserProfile().getFirstName() + " " + instructorProfile.getGeneralUserProfile().getLastName());
                    response.setAccountType(instructorPayments.getPaymentMethod().getMethod());

                    if (instructorPayments.getPaymentMethod().getId() == 4) { // Payoneer
                        response.setUserName(instructorPayments.getPayoneerUserName());
                        response.setEmail(instructorPayments.getPayoneerEmail());
                    } else if (instructorPayments.getPaymentMethod().getId() == 2) { // PayPal
                        response.setUserName(instructorPayments.getPaypalUserName());
                        response.setEmail(instructorPayments.getPaypalEmail());
                    }

                    response.setAmount("USD" + decimalFormat.format(totalRevenue));
                    response.setMonthOfSale(twoMonthsAgo.format(formatter3));
                    paymentResponses.add(response);
                } else {
                    throw new ErrorException("Instructor payment details not found for: " + instructorProfile.getGeneralUserProfile().getEmail(), VarList.RSP_NO_DATA_FOUND);
                }
            }
        }

        return paymentResponses;
    }


    @Override
    public List<GetAllInstructorsUkBankDetailsForManagePaymentsResponse> getAllInstructorsUkBankDetailsForManagePayments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getGupType().getId() != 3) {
            throw new ErrorException("You cannot access it because you are not an administrator", VarList.RSP_NO_DATA_FOUND);
        }

        List<InstructorProfile> instructorProfiles = instructorProfileRepository.getInstructorProfileByIsVerifiedAndIsProfileCompleted(Byte.valueOf("1"), Byte.valueOf("1"));
        List<GetAllInstructorsUkBankDetailsForManagePaymentsResponse> paymentResponses = new ArrayList<>();
        LocalDate twoMonthsAgo = LocalDate.now().minusMonths(2);

        for (InstructorProfile instructorProfile : instructorProfiles) {
            List<InstructorCourseRevenue> instructorCourseRevenues = instructorCourseRevenueRepository.getInstructorCourseRevenueByInstructorProfile(instructorProfile);
            double totalRevenue = 0;

            for (InstructorCourseRevenue instructorCourseRevenue : instructorCourseRevenues) {
                LocalDate revenueLocalDate = convertToLocalDate(instructorCourseRevenue.getRevenue().getCreatedDate());

                if (YearMonth.from(revenueLocalDate).equals(YearMonth.from(twoMonthsAgo))) {
                    InstructorPayments instructorPayments = instructorPaymentsRepository.getInstructorPaymentsByInstructorProfile(instructorProfile);
                    if (instructorPayments != null && instructorPayments.getPaymentMethod().getId() == 5) {
                        totalRevenue += instructorCourseRevenue.getInstructorShare() * instructorCourseRevenue.getRevenue().getTransaction().getUsdRate();
                    }
                }
            }

            if (totalRevenue > 0) {
                InstructorPayments instructorPayments = instructorPaymentsRepository.getInstructorPaymentsByInstructorProfile(instructorProfile);
                if (instructorPayments != null) {
                    GetAllInstructorsUkBankDetailsForManagePaymentsResponse response = new GetAllInstructorsUkBankDetailsForManagePaymentsResponse();
                    response.setInstructorName(instructorProfile.getGeneralUserProfile().getFirstName() + " " + instructorProfile.getGeneralUserProfile().getLastName());
                    response.setSortCode(instructorPayments.getSort1() + " " + instructorPayments.getSort2() + " " + instructorPayments.getSort3());
                    response.setAccountNo(instructorPayments.getAccountNumber());
                    response.setBankAccountName(instructorPayments.getBankAccountName());
                    response.setAmount("USD" + decimalFormat.format(totalRevenue));
                    response.setMonthOfSale(twoMonthsAgo.format(formatter3));
                    paymentResponses.add(response);
                } else {
                    throw new ErrorException("Instructor payment details not found for: " + instructorProfile.getGeneralUserProfile().getEmail(), VarList.RSP_NO_DATA_FOUND);
                }
            }
        }

        return paymentResponses;
    }

    @Override
    public GetInstructorRevenueReportChartResponse getInstructorRevenueReportChart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                if (instructorProfile != null) {
                    return getInstructorRevenueReportChartDataSet(instructorProfile);
                } else {
                    throw new ErrorException("No such profile found", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private GetInstructorRevenueReportChartResponse getInstructorRevenueReportChartDataSet(InstructorProfile instructorProfile) {

        List<AmountDataSetsResponse> aethenosDataSets = new ArrayList<>();
        List<AmountDataSetsResponse> refundsDataSets = new ArrayList<>();
        List<AmountDataSetsResponse> referalLinkDataSets = new ArrayList<>();
        List<AmountDataSetsResponse> couponDataSets = new ArrayList<>();
        List<InstructorCourseRevenue> instructorCourseRevenues = instructorCourseRevenueRepository.getInstructorCourseRevenueByInstructorProfile(instructorProfile);
        for (InstructorCourseRevenue instructorCourseRevenue : instructorCourseRevenues) {
            if (!instructorCourseRevenue.getRevenue().isRefunded()) {
                LocalDateTime dateTime = LocalDateTime.ofInstant(instructorCourseRevenue.getRevenue().getCreatedDate().toInstant(), ZoneId.systemDefault());
                int buyYear = dateTime.getYear();
                int buyMonth = dateTime.getMonthValue();

                double value = instructorCourseRevenue.getInstructorShare() * instructorCourseRevenue.getRevenue().getTransaction().getUsdRate();

                if (instructorCourseRevenue.getRevenue().getOrderHasCourse().getCoursePurchaseType() != null && instructorCourseRevenue.getRevenue().getOrderHasCourse().getCoursePurchaseType().getId() == 1) {
                    AmountDataSetsResponse aethenosData = new AmountDataSetsResponse();
                    aethenosData.setTimestamp(instructorCourseRevenue.getRevenue().getCreatedDate());
                    aethenosData.setAmount(value);
                    aethenosDataSets.add(aethenosData);
                } else if (instructorCourseRevenue.getRevenue().getOrderHasCourse().getCoursePurchaseType() != null && instructorCourseRevenue.getRevenue().getOrderHasCourse().getCoursePurchaseType().getId() == 4) {
                    AmountDataSetsResponse referalLinkData = new AmountDataSetsResponse();
                    referalLinkData.setTimestamp(instructorCourseRevenue.getRevenue().getCreatedDate());
                    referalLinkData.setAmount(value);
                    referalLinkDataSets.add(referalLinkData);
                } else if (instructorCourseRevenue.getRevenue().getOrderHasCourse().getCoursePurchaseType() != null && instructorCourseRevenue.getRevenue().getOrderHasCourse().getCoursePurchaseType().getId() == 3) {
                    AmountDataSetsResponse couponData = new AmountDataSetsResponse();
                    couponData.setTimestamp(instructorCourseRevenue.getRevenue().getCreatedDate());
                    couponData.setAmount(value);
                    couponDataSets.add(couponData);
                }
            }
        }

        List<ApprovedRefunds> approvedRefundsList = approvedRefundsRepository.findAll();
        for (ApprovedRefunds approvedRefunds : approvedRefundsList) {
            if (approvedRefunds.getRefunds().getRefundStatus().getId() == 4 || approvedRefunds.getRefunds().getRefundStatus().getId() == 5) {
                LocalDateTime dateTime = LocalDateTime.ofInstant(approvedRefunds.getRefunds().getRequestDate().toInstant(), ZoneId.systemDefault());
                int refundYear = dateTime.getYear();
                int refundMonth = dateTime.getMonthValue();
                if (approvedRefunds.getRefunds().getOrderHasCourse().getCourse().getInstructorId().equals(instructorProfile)) {
                    Revenue revenue = revenueRepository.getRevenueByOrderHasCourse(approvedRefunds.getRefunds().getOrderHasCourse());
                    InstructorCourseRevenue instructorCourseRevenue = instructorCourseRevenueRepository.getInstructorCourseRevenueByRevenue(revenue);
                    AmountDataSetsResponse refundsData = new AmountDataSetsResponse();
                    refundsData.setTimestamp(approvedRefunds.getRefunds().getRequestDate());
                    refundsData.setAmount(instructorCourseRevenue.getInstructorShare() * instructorCourseRevenue.getRevenue().getTransaction().getUsdRate());
                    refundsDataSets.add(refundsData);
                }
            }
        }

        GetInstructorRevenueReportChartResponse dataSet = new GetInstructorRevenueReportChartResponse();
        dataSet.setAethenosDataSets(aethenosDataSets);
        dataSet.setRefundsDataSets(refundsDataSets);
        dataSet.setReferalLinkDataSets(referalLinkDataSets);
        dataSet.setCouponDataSets(couponDataSets);

        return dataSet;
    }

    @Override
    public GetInstructorChartForThisMonthResponse getInstructorChartDetailsForThisMonth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }

        InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
        if (instructorProfile == null) {
            throw new ErrorException("No such profile found", VarList.RSP_NO_DATA_FOUND);
        }

        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.from(today);
        int daysInMonth = yearMonth.lengthOfMonth();

        // Arrays to hold our daily data
        double[] revenue = new double[daysInMonth];
        int[] enrollment = new int[daysInMonth];
        int[] rating = new int[daysInMonth];
        int[] days = new int[daysInMonth];

        // Pre-fill the days array (1 to 31)
        for (int i = 0; i < daysInMonth; i++) {
            days[i] = i + 1;
        }

        // FAST LOGIC: Fetch courses ONCE and iterate ONCE
        List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);

        for (Course course : courses) {
            List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);

            for (OrderHasCourse orderHasCourse : orderHasCourses) {

                // 1. Process Revenue & Enrollment
                Revenue rev = revenueRepository.getRevenueByOrderHasCourse(orderHasCourse);
                if (rev != null && !rev.isRefunded()) {
                    LocalDate revDate = rev.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    // Only process if the transaction happened this month
                    if (YearMonth.from(revDate).equals(yearMonth)) {
                        int dayIndex = revDate.getDayOfMonth() - 1; // Array is 0-indexed (Day 1 goes to index 0)

                        InstructorCourseRevenue icr = instructorCourseRevenueRepository.getInstructorCourseRevenueByRevenue(rev);
                        if (icr != null) {
                            enrollment[dayIndex]++;
                            double earning = icr.getInstructorShare() * rev.getTransaction().getUsdRate();
                            revenue[dayIndex] += earning;
                        }
                    }
                }

                // 2. Process Ratings
                Review review = reviewRepository.getReviewsByOrderHasCourse(orderHasCourse);
                if (review != null && review.getDate() != null) {
                    LocalDate ratingDate = review.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    if (YearMonth.from(ratingDate).equals(yearMonth)) {
                        int dayIndex = ratingDate.getDayOfMonth() - 1;
                        rating[dayIndex]++;
                    }
                }
            }
        }

        // Optional: Round all revenue values to 2 decimal places to match your dashboard
        for(int i = 0; i < revenue.length; i++) {
            revenue[i] = Math.round(revenue[i] * 100.0) / 100.0;
        }

        GetInstructorChartForThisMonthResponse response = new GetInstructorChartForThisMonthResponse();
        response.setDays(days);
        response.setRevenue(revenue);
        response.setEnrollment(enrollment);
        response.setRating(rating);

        return response;
    }

    @Override
    public List<GetAllInstructorDetailsResponse> getAllInstructorDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(email);

        if (profile == null)
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);

        if (profile.getGupType().getId() != 3)
            throw new ErrorException("Only admin can access", VarList.RSP_NO_DATA_FOUND);

        List<InstructorProfile> instructorProfiles = instructorProfileRepository.getInstructorProfileByIsVerifiedAndIsProfileCompleted((byte) 1, (byte) 1);
        List<GetAllInstructorDetailsResponse> getAllInstructorDetailsResponses = new ArrayList<>();
        for (InstructorProfile instructorProfile : instructorProfiles) {
            GetAllInstructorDetailsResponse getAllInstructorDetailsResponse = new GetAllInstructorDetailsResponse();
            getAllInstructorDetailsResponse.setUserCode(instructorProfile.getGeneralUserProfile().getUserCode());
            getAllInstructorDetailsResponse.setName(instructorProfile.getGeneralUserProfile().getFirstName() + " " + instructorProfile.getGeneralUserProfile().getLastName());
            getAllInstructorDetailsResponse.setJoinDate(instructorProfile.getCreated_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(updatedFormatter));
            List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);
            int studentCount = 0;
            for (Course course : courses) {
                studentCount += orderHasCourseRepository.getOrderHasCoursesByCourse(course).size();
            }
            getAllInstructorDetailsResponse.setTotalStudents(studentCount);
            getAllInstructorDetailsResponse.setEmail(instructorProfile.getGeneralUserProfile().getEmail());
            getAllInstructorDetailsResponse.setActive(instructorProfile.getGeneralUserProfile().getIsActive() == 0 ? false : true);
            getAllInstructorDetailsResponses.add(getAllInstructorDetailsResponse);
        }
        return getAllInstructorDetailsResponses;
    }

    @Override
    public GetInstructorChartForThisMonthResponse getInstructorMonthlyRevenueByMonth(GetInstructorMonthlyRevenueByMonthRequest getInstructorMonthlyRevenueByMonthRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(email);

        if (profile == null)
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);

        if (profile.getGupType().getId() != 3)
            throw new ErrorException("Only admin can access", VarList.RSP_NO_DATA_FOUND);

        final String month = getInstructorMonthlyRevenueByMonthRequest.getMonth();
        final String userCode = getInstructorMonthlyRevenueByMonthRequest.getUserCode();

        if (month == null || month.isEmpty() || userCode == null || userCode.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        GeneralUserProfile generalUserProfile = generalUserProfileRepository.getGeneralUserProfileByUserCode(userCode);
        if (generalUserProfile == null)
            throw new ErrorException("Invalid user code", VarList.RSP_NO_DATA_FOUND);
        InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(generalUserProfile);

        YearMonth yearMonth;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            yearMonth = YearMonth.parse(month, formatter);
        } catch (DateTimeParseException e) {
            throw new ErrorException("Invalid month format. Use 'MMMM yyyy' (e.g., 'November 2024')", VarList.RSP_NO_DATA_FOUND);
        }

        int daysInMonth = yearMonth.lengthOfMonth();

        GetInstructorChartForThisMonthResponse getInstructorChartForThisMonthResponse = new GetInstructorChartForThisMonthResponse();

        double[] revenue = new double[daysInMonth];
        int[] enrollment = new int[daysInMonth];
        int[] rating = new int[daysInMonth];
        int[] days = new int[daysInMonth];

        for (int day = 0; day < daysInMonth; day++) {
            days[day] = day + 1;
            CalculateRevenueAndEnrollmentForThisMonthResponse calculateRevenueAndEnrollmentForThisMonthResponse = calculateRevenueAndEnrollmentForAnyMonth(instructorProfile, day + 1, yearMonth);
            revenue[day] = calculateRevenueAndEnrollmentForThisMonthResponse.getRevenue();
            enrollment[day] = calculateRevenueAndEnrollmentForThisMonthResponse.getEnrollment();
            rating[day] = calculateRatingForThisMonth(instructorProfile, day + 1);
        }

        getInstructorChartForThisMonthResponse.setDays(days);
        getInstructorChartForThisMonthResponse.setRevenue(revenue);
        getInstructorChartForThisMonthResponse.setEnrollment(enrollment);
        getInstructorChartForThisMonthResponse.setRating(rating);

        return getInstructorChartForThisMonthResponse;
    }

    private CalculateRevenueAndEnrollmentForThisMonthResponse calculateRevenueAndEnrollmentForThisMonth
            (InstructorProfile instructorProfile, int day) {
        int totalEnrollments = 0;
        double netRevenue = 0.0;
        YearMonth currentYearMonth = YearMonth.now();

        LocalDate targetDate = currentYearMonth.atDay(day);

        List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);
        for (Course course : courses) {
            List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);
            for (OrderHasCourse orderHasCourse : orderHasCourses) {
                Revenue revenue = revenueRepository.getRevenueByOrderHasCourse(orderHasCourse);
                if (revenue != null && !revenue.isRefunded()) {
                    LocalDate revenueDate = revenue.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (revenueDate.equals(targetDate)) {
                        InstructorCourseRevenue instructorCourseRevenue = instructorCourseRevenueRepository.getInstructorCourseRevenueByRevenue(revenue);
                        if (instructorCourseRevenue != null) {
                            totalEnrollments++;
                            netRevenue += instructorCourseRevenue.getInstructorShare() * instructorCourseRevenue.getRevenue().getTransaction().getUsdRate();
                        }
                    }
                }
            }
        }
        CalculateRevenueAndEnrollmentForThisMonthResponse calculateRevenueAndEnrollmentForThisMonthResponse = new CalculateRevenueAndEnrollmentForThisMonthResponse();
        double roundedRevenue = BigDecimal.valueOf(netRevenue).setScale(2, RoundingMode.HALF_UP).doubleValue();
        calculateRevenueAndEnrollmentForThisMonthResponse.setRevenue(roundedRevenue);
        calculateRevenueAndEnrollmentForThisMonthResponse.setEnrollment(totalEnrollments);
        return calculateRevenueAndEnrollmentForThisMonthResponse;
    }

    private int calculateRatingForThisMonth(InstructorProfile instructorProfile, int day) {
        int rating = 0;
        YearMonth currentYearMonth = YearMonth.now();

        LocalDate targetDate = currentYearMonth.atDay(day);

        List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);

        for (Course course : courses) {
            List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);
            for (OrderHasCourse orderHasCourse : orderHasCourses) {

                Review review = reviewRepository.getReviewsByOrderHasCourse(orderHasCourse);

                if (review != null) {
                    LocalDate ratingDate = review.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (ratingDate.equals(targetDate)) {
                        rating++;
                    }
                }
            }
        }
        return rating;
    }

    private CalculateRevenueAndEnrollmentForThisMonthResponse calculateRevenueAndEnrollmentForAnyMonth
            (InstructorProfile instructorProfile, int day, YearMonth yearMonth) {
        int totalEnrollments = 0;
        double netRevenue = 0.0;

        LocalDate targetDate = yearMonth.atDay(day);

        List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);
        for (Course course : courses) {
            List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);
            for (OrderHasCourse orderHasCourse : orderHasCourses) {
                Revenue revenue = revenueRepository.getRevenueByOrderHasCourse(orderHasCourse);
                if (revenue != null && !revenue.isRefunded()) {
                    LocalDate revenueDate = revenue.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (revenueDate.equals(targetDate)) {
                        InstructorCourseRevenue instructorCourseRevenue = instructorCourseRevenueRepository.getInstructorCourseRevenueByRevenue(revenue);
                        if (instructorCourseRevenue != null) {
                            totalEnrollments++;
                            netRevenue += instructorCourseRevenue.getInstructorShare() * revenue.getTransaction().getUsdRate();
                        }
                    }
                }
            }
        }
        CalculateRevenueAndEnrollmentForThisMonthResponse calculateRevenueAndEnrollmentForThisMonthResponse = new CalculateRevenueAndEnrollmentForThisMonthResponse();
        calculateRevenueAndEnrollmentForThisMonthResponse.setRevenue(netRevenue);
        calculateRevenueAndEnrollmentForThisMonthResponse.setEnrollment(totalEnrollments);
        return calculateRevenueAndEnrollmentForThisMonthResponse;
    }

    @Override
    public List<GetThreeMonthRevenueResponse> getInstructorRevenueReportForThreeMonth(GetInstructorMonthlyRevenueByMonthRequest getInstructorMonthlyRevenueByMonthRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getGupType().getId() != 3) {
            throw new ErrorException("Your are not an administrator", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }

        final String userCode = getInstructorMonthlyRevenueByMonthRequest.getUserCode();
        final String month = getInstructorMonthlyRevenueByMonthRequest.getMonth();

        if (userCode == null || userCode.isEmpty() || month == null || month.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

        GeneralUserProfile userProfile = generalUserProfileRepository.getGeneralUserProfileByUserCode(userCode);

        InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(userProfile);
        if (instructorProfile == null) {
            throw new ErrorException("No such profile found", VarList.RSP_NO_DATA_FOUND);
        }

        List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);

        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        LocalDate startMonth;
        try {
            startMonth = YearMonth.parse(month, monthFormatter).atDay(1);
        } catch (DateTimeParseException e) {
            throw new ErrorException("Invalid month format. Use 'MMMM yyyy'", VarList.RSP_ERROR);
        }

        List<YearMonth> targetMonths = new ArrayList<>();
        targetMonths.add(YearMonth.from(startMonth));
        targetMonths.add(YearMonth.from(startMonth.minusMonths(1)));
        targetMonths.add(YearMonth.from(startMonth.minusMonths(2)));

        List<GetThreeMonthRevenueResponse> threeMonthRevenueResponses = new ArrayList<>();

        for (YearMonth yearMonth : targetMonths) {
            Map<Integer, Double> dailyRevenueMap = new HashMap<>();

            int daysInMonth = yearMonth.lengthOfMonth();
            for (int day = 1; day <= daysInMonth; day++) {
                dailyRevenueMap.put(day, 0.00);
            }

            for (Course course : courses) {
                List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);

                for (OrderHasCourse orderHasCourse : orderHasCourses) {
                    Revenue revenue = revenueRepository.getRevenueByOrderHasCourse(orderHasCourse);

                    if (revenue != null && !revenue.isRefunded()) {
                        LocalDate createdLocalDate = convertToLocalDateViaInstant(revenue.getCreatedDate());

                        if (YearMonth.from(createdLocalDate).equals(yearMonth)) {
                            InstructorCourseRevenue instructorCourseRevenue = instructorCourseRevenueRepository.getInstructorCourseRevenueByRevenue(revenue);

                            double earning = instructorCourseRevenue.getInstructorShare() * revenue.getTransaction().getUsdRate();
                            earning = Math.round(earning * 100.0) / 100.0;

                            int dayOfMonth = createdLocalDate.getDayOfMonth();
                            double previousEarnings = dailyRevenueMap.get(dayOfMonth);
                            dailyRevenueMap.put(dayOfMonth, previousEarnings + earning);
                        }
                    }
                }
            }

            List<GetMonthRevenueResponse> dayWiseRevenues = dailyRevenueMap.entrySet().stream()
                    .map(entry -> {
                        GetMonthRevenueResponse dayRevenueResponse = new GetMonthRevenueResponse();
                        dayRevenueResponse.setDay(entry.getKey());
                        dayRevenueResponse.setRevenue(entry.getValue());
                        return dayRevenueResponse;
                    })
                    .sorted(Comparator.comparingInt(GetMonthRevenueResponse::getDay))
                    .collect(Collectors.toList());

            GetThreeMonthRevenueResponse monthRevenueResponse = new GetThreeMonthRevenueResponse();
            monthRevenueResponse.setMonth(yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            monthRevenueResponse.setRevenues(dayWiseRevenues);

            threeMonthRevenueResponses.add(monthRevenueResponse);
        }

        return threeMonthRevenueResponses;
    }

    @Override
    public List<GetTwelveMonthRevenueResponse> getInstructorRevenueReportFortwelveMonth(GetInstructorMonthlyRevenueByMonthRequest getInstructorMonthlyRevenueByMonthRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getGupType().getId() != 3) {
            throw new ErrorException("Your are not an administrator", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }

        final String userCode = getInstructorMonthlyRevenueByMonthRequest.getUserCode();
        final String month = getInstructorMonthlyRevenueByMonthRequest.getMonth();

        if (userCode == null || userCode.isEmpty() || month == null || month.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

        GeneralUserProfile userProfile = generalUserProfileRepository.getGeneralUserProfileByUserCode(userCode);

        InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(userProfile);
        if (instructorProfile == null) {
            throw new ErrorException("No such profile found", VarList.RSP_NO_DATA_FOUND);
        }

        List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);

        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        LocalDate startMonth;
        try {
            startMonth = YearMonth.parse(month, monthFormatter).atDay(1);
        } catch (DateTimeParseException e) {
            throw new ErrorException("Invalid month format. Use 'MMMM yyyy'", VarList.RSP_ERROR);
        }

        List<YearMonth> targetMonths = new ArrayList<>();
        targetMonths.add(YearMonth.from(startMonth));
        for (int i = 1; i < 12; i++) {
            targetMonths.add(YearMonth.from(startMonth.minusMonths(i)));
        }

        List<GetTwelveMonthRevenueResponse> getTwelveMonthRevenueResponses = new ArrayList<>();
        double totalMonthrevenue;

        for (YearMonth yearMonth : targetMonths) {
            totalMonthrevenue = 0.0;
            for (Course course : courses) {
                List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);

                for (OrderHasCourse orderHasCourse : orderHasCourses) {
                    Revenue revenue = revenueRepository.getRevenueByOrderHasCourse(orderHasCourse);

                    if (revenue != null && !revenue.isRefunded()) {
                        LocalDate createdLocalDate = convertToLocalDateViaInstant(revenue.getCreatedDate());

                        if (YearMonth.from(createdLocalDate).equals(yearMonth)) {
                            InstructorCourseRevenue instructorCourseRevenue = instructorCourseRevenueRepository.getInstructorCourseRevenueByRevenue(revenue);

                            double earning = instructorCourseRevenue.getInstructorShare() * revenue.getTransaction().getUsdRate();
                            earning = Math.round(earning * 100.0) / 100.0;

                            totalMonthrevenue += earning;
                        }
                    }
                }
            }

            GetTwelveMonthRevenueResponse getTwelveMonthRevenueResponse = new GetTwelveMonthRevenueResponse();
            getTwelveMonthRevenueResponse.setMonth(yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            getTwelveMonthRevenueResponse.setRevenue(totalMonthrevenue);

            getTwelveMonthRevenueResponses.add(getTwelveMonthRevenueResponse);
        }

        return getTwelveMonthRevenueResponses;
    }

    @Override
    public GetInstructorRevenueForMonthByTodayResponse getInstructorRevenueForMonthByToday(GetInstructorRevenueForMonthByTodayRequest getInstructorRevenueForMonthByTodayRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getGupType().getId() != 3) {
            throw new ErrorException("Your are not an administrator", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }

        final String userCode = getInstructorRevenueForMonthByTodayRequest.getUserCode();
        if (userCode == null || userCode.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

        GeneralUserProfile userProfile = generalUserProfileRepository.getGeneralUserProfileByUserCode(userCode);

        InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(userProfile);
        if (instructorProfile == null) {
            throw new ErrorException("No such profile found", VarList.RSP_NO_DATA_FOUND);
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(30);

        List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);
        int daysInRange = (int) ChronoUnit.DAYS.between(startDate, today) + 1;
        int[] days = new int[daysInRange];
        double[] revenue = new double[daysInRange];

        for (int i = 0; i < daysInRange; i++) {
            LocalDate date = today.minusDays(i);

            days[i] = date.getDayOfMonth();
            revenue[i] = calculateRevenueForDay(courses, date);
        }
        reverseArray(days);
        reverseArray(revenue);

        GetInstructorRevenueForMonthByTodayResponse response = new GetInstructorRevenueForMonthByTodayResponse();
        response.setDays(days);
        response.setRevenue(revenue);

        return response;

    }

    private void reverseArray(int[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            int temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }

    private void reverseArray(double[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            double temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }

    private double calculateRevenueForDay(List<Course> courses, LocalDate date) {
        double dailyRevenue = 0.0;
        for (Course course : courses) {
            List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);

            for (OrderHasCourse orderHasCourse : orderHasCourses) {
                Revenue revenue = revenueRepository.getRevenueByOrderHasCourse(orderHasCourse);

                if (revenue != null && !revenue.isRefunded()) {
                    LocalDate createdLocalDate = convertToLocalDateViaInstant(revenue.getCreatedDate());

                    if (createdLocalDate.equals(date)) {
                        InstructorCourseRevenue instructorCourseRevenue = instructorCourseRevenueRepository.getInstructorCourseRevenueByRevenue(revenue);

                        double earning = instructorCourseRevenue.getInstructorShare() * revenue.getTransaction().getUsdRate();
                        dailyRevenue += earning;
                    }
                }
            }
        }
        return dailyRevenue;
    }
}