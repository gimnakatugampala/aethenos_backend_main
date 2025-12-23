package lk.exon.aethenosapi.security;

import lk.exon.aethenosapi.config.PasswordEncoderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private JwtUserDetailsServicePassword jwtUserDetailsServicePassword;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsServicePassword)
                .passwordEncoder(PasswordEncoderConfig.passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                // dont authenticate this particular request
                .authorizeRequests().antMatchers(
                        "/",
                        "/css/**",
                        "/images/**",
                        "/assignment/assignment-resources/**",
                        "/authentication/login",
                        "/authentication/studentLoginWithloginToken/{loginToken}",
                        "/authentication/student",
                        "/authentication/admin",
                        "/authentication/instructor",
                        "/authentication/getAccountValidation",
                        "/register/add",
                        "/course/getCourseCategory",
                        "/managecourse/getAllLanguage",
                        "/managecourse/getAllCourseLevels",
                        "/managecourse/getAllCourseSubCategory",
                        "/managecourse/getAllDiscountType",
                        "/managecourse/getAllPromotionType",
                        "/managecourse/getcountries",
                        "/managecourse/getCoursesUsingLinkName/{linkName}",
                        "/managecourse/getCoursesUsingSubLinkName/{subLinkName}",
                        "/course/getCategorynameBylinkName/{linkName}",
                        "/course/getTopicBylinkName/{linkName}",
                        "/course/getCoursesData",
                        "/course/getNewCourses/{linkName}",
                        "/course/search",
                        "/course/getCourseByStudent/{CourseCode}",
                        "/RecentCourses/view",
                        "/course/getAllCategorySubCategoryTopics",
                        "/course/getSubCategoryByCourseLinkName/{linkName}",
                        "/course/getMostPopularCourses/{linkName}",
                        "/course/getCategoryAndSubCategorynameBylinkName/{linkSubName}",
                        "/course/getTrendingByCourseLinkName/{linkName}",
                        "/course/getInstructorDetails/{userCode}",
                        "/course/getPopularInstructors/{linkName}",
                        "/course/getAllcoursesViewByLinkName/{linkName}",
                        "/course/getPopularTopicByLinkName/{linkName}",
                        "/course/getAllcoursesViewByInstructor/{userCode}",
                        "/course/getNewCoursesBySubCategory/{sublinkName}",
                        "/course/getMostPopularCoursesBySubCategory/{sublinkName}",
                        "/course/getTrendingBySubCategory/{sublinkName}",
                        "/course/getPopularInstructorsBySubCategory/{sublinkName}",
                        "/course/getAllcoursesViewBySubLinkName/{sublinkName}",
                        "/course/getPopularTopicBySubLinkName/{sublinkName}",
                        "/RecentCourses/view",
                        "/displayCourse/searchCourses/{keyword}",
                        "/displayCourse/searchNewCourses/{keyword}",
                        "/displayCourse/getRelatedCategoriesByTopicLinkName/{topic}",
                        "/displayCourse/getBeginnerFavoritesCoursesByTopicLinkName/{topicLinkName}",
                        "/displayCourse/getAllCourses",
                        "/displayCourse/getAllFreeCourses",
                        "/displayCourse/getCourseContent/{courseCode}",
                        "/managecourse/getCoursesUsingTopicLinkName/{topicLinkName}",
                        "/displayCourse/getTopSubCategoryCoursesByTopicLinkName/{topicLinkName}",
                        "/displayCourse/getTopicCategorySubCategoryByTopic/{topicLinkName}",
                        "/displayCourse/getLimitedCountCoursesForHomeByLinkName/{linkName}",
                        "/displayCourse/getRelatedTopicsByTopicLinkName/{topicLinkName}",
                        "/course/getMostPopularCoursesByTopic/{linkName}",
                        "/course/getNewCoursesByTopic/{topicLinkName}",
                        "/payment/getCouponValidationByCode/{code}",
                        "/course/getTrendingCoursesByTopic/{topicLinkName}",
                        "/studentProfile/getTopics",
                        "/common/exceltobase64ByexcelFileName",
                        "/common/getVat",
                        "/common/searchCourseAndInstructorDetails/{keyword}",
                        "/payment/getReviewsByCourseCode/{courseCode}",
                        "/course/getAllCoursesByInstructorCode/{userCode}",
                        "/studentProfile/forgotPasswords/{email}",
                        "/studentProfile/verifyVerificationCode",
                        "/studentProfile/updatePassword",
                        "/register/studentRegistration",
                        "/register/instructorRegistration",
                        "/revenue/calculateInstructorRevenue",
                        "/register/checkUserEmailVerificationCode",
                        "/register/resendUserEmailVerificationCode/{email}",
                        "/videoStreming/video",
                        "/course/checkReferralCodeValidation/{referralCode}",
                        "/common/sendEmail",
                        "/common/downloadFile",
                        "/api/files/upload"
                ).permitAll().
                // all other requests need to be authenticated
                        anyRequest().authenticated().and().
                // make sure we use stateless session; session won't be used to
                // store user's state.
                        exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add a filter to validate the tokens with every request
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
