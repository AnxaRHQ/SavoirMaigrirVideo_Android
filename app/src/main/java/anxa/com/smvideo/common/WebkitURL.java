package anxa.com.smvideo.common;

/**
 * Created by aprilanxa on 19/05/2017.
 */

public class WebkitURL {

    public static String domainURL = "http://savoir-maigrir.aujourdhui.com";

    //for QC
    //public static String domainURL = "http://qc.savoir-maigrir.aujourdhui.com";
    //for dev use
    /**public static String domainURL = "http://dev.savoir-maigrir.aujourdhui.com";**/

    public static String registrationURL = "/1dirparjour/registration?v=99&sid=221";
    public static String registrationDoneURL = "/1dirparjour/";
    public static String loginURL = "/3actparjour/login";
    public static String offerURL = "/1dirparjour/registration/offer";
    public static String forgetPw = "/3actparjour/forgot-password";

    public static String conditionsURL = "/3actparjour/login?u=%d&p=%password&redirect=/1dirparjour/aide/cgv";
    public static String privacyURL = "/3actparjour/login?u=%d&p=%password&redirect=/1dirparjour/aide/privacy";
    public static String contactURL = "/3actparjour/login?u=%d&p=%password&redirect=/1dirparjour/aide/contact";

    public static String free_conditionsURL = "/1dirparjour/minisite/cgv";
    public static String free_privacyURL = "/1dirparjour/minisite/privacy";
    public static String free_contactURL = "/1dirparjour/minisite/contact";

    public static String webinarURL = "/1dirparjour/webinar";

    public static String webinarAutoLoginURL = "/3actparjour/login?u=%d&p=%password&redirect=/1dirparjour/webinar/broadcast";

    public static String webinarWebkitUrl = "/5minparjour/mobile/conference?regId=%regId&sig=%sig";
    public static String fichesWebkitUrl = "/5minparjour/mobile/fichespratiques?regId=%regId&sig=%sig";
    public static String ambassadriceWebkitUrl = "/5minparjour/mobile/community/SuperAmbassadeur?regId=%regId&sig=%sig";
    public static final String ANXAMATS_URL = "http://api.anxa.com/anxamats";

}
