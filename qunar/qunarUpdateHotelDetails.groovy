
import com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfo
import org.apache.commons.lang.time.DateFormatUtils
import org.apache.commons.lang.time.DateUtils
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.hibernate.Criteria
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.criterion.Disjunction
import org.hibernate.criterion.Projection
import org.hibernate.criterion.ProjectionList
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.phantomjs.*
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.NoSuchElementException;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask


@Grapes([
/*
//        @Grab(group = 'dom4j', module = 'dom4j', version = '1.6.1'),
        @Grab(group = 'org.hibernate', module = 'hibernate-annotations', version = '3.4.0.GA'),
        @Grab(group = 'org.hibernate', module = 'hibernate-ehcache', version = '3.6.10.Final'),
        @Grab(group = 'org.hibernate', module = 'hibernate-core', version = '3.6.10.Final'),
//        @Grab(group = 'hsqldb', module = 'hsqldb', version = '1.8.0.7'),
        @Grab(group = 'javassist', module = 'javassist', version = '3.4.GA'),
//        @Grab(group = 'net.sourceforge.htmlcleaner', module = 'htmlcleaner', version = '2.6.1'),
        @Grab(group = 'commons-validator', module = 'commons-validator', version = '1.4.0'),
        @Grab(group = 'commons-httpclient', module = 'commons-httpclient', version = '3.1'),
        @Grab(group = 'commons-collections', module = 'commons-collections', version = '3.2.1'),
        @Grab(group = 'commons-math', module = 'commons-math', version = '1.2'),
        @Grab(group = 'org.apache.commons', module = 'commons-lang3', version = '3.1'),
//        @Grab(group = 'struts', module = 'struts', version = '1.2.9'),
        @Grab(group = 'org.apache.poi', module = 'poi', version = '3.9'),
//        @Grab(group = 'org.ostermiller', module = 'utils', version = '1.07.00'),
        @Grab(group = 'mysql', module = 'mysql-connector-java', version = '5.1.28'),
        @Grab(group = 'org.seleniumhq.selenium', module = 'selenium-java', version = '3.4.0'),
//        @Grab(group = 'org.apache.cxf', module = 'cxf-bundle', version = '2.7.2'),
        @Grab(group = 'org.mongodb', module = 'mongo-java-driver', version = '3.2.0'),
//        @Grab(group = 'org.codehaus.woodstox', module = 'woodstox-core-asl', version = '4.2.1'),
//        @Grab(group = 'org.codehaus.woodstox', module = 'woodstox-core-lgpl', version = '4.2.1'),
//        @Grab(group = 'org.codehaus.woodstox', module = 'stax2-api', version = '3.1.1'),
        @Grab(group = 'org.slf4j', module = 'slf4j-nop', version = '1.7.5'),
        @Grab(group = 'gallop', module = 'gallop', version = '20170530'), // internal
*/
])

def hibProps = [
        "hibernate.dialect"                  : "org.hibernate.dialect.MySQL5InnoDBDialect",
        "hibernate.connection.driver_class"  : "com.mysql.jdbc.Driver",
//        "hibernate.connection.url"           : "jdbc:mysql://192.168.10.173:3306/gallop?useUnicode=true&amp;characterEncoding=UTF-8",
        "hibernate.connection.url"           : "jdbc:mysql://192.168.6.10:3306/giata?useUnicode=true&amp;characterEncoding=UTF-8",
        "hibernate.connection.username"      : "chengcsw",
        "hibernate.connection.password"      : "ccheng",
        "hibernate.connection.pool_size"     : "1",
        "hibernate.jdbc.batch_size"          : "1000",
        "hibernate.show_sql"                 : "true",
//        "hibernate.hbm2ddl.auto": "validate",
//        hibernate.jdbc.batch_size 20
        "hibernate.connection.autocommit"    : "true",
        "hibernate.cache.provider_class"     : "org.hibernate.cache.EhCacheProvider",
        "hibernate.transaction.factory_class": "org.hibernate.transaction.JDBCTransactionFactory",
]

def Configuration config = new Configuration()
hibProps.each { k, v -> config.setProperty(k, v) }
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.Chain.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.Channel.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.City.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.CityName.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.CityCrossReference.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.Country.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.Locale.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.Property.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.PropertyAddress.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.PropertyAmenity.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfo.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.PropertyCrossReference.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.PropertyEmail.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.PropertyName.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.PropertyPhone.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.PropertyPosition.class);
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.PropertyURL.class);
SessionFactory factory = config.buildSessionFactory();

// Download gecko drivers here: https://github.com/mozilla/geckodriver/releases
System.setProperty("webdriver.gecko.driver", "/geckodriver-v0.16.1-win64/geckodriver.exe");
//System.setProperty("webdriver.chrome.driver", "/chromedriver_win32/chromedriver.exe");

// > yum install python-pip python Xvfb
// > pip install pyvirtualdisplay selenium
//System.setProperty("webdriver.gecko.driver", "/usr/sbin/geckodriver");

// DesiredCapabilities capability = DesiredCapabilities.firefox();
// RemoteWebDriver driver = new (new URL("http://192.168.10.102:4444/wd/hub"), capability);

//WebDriver driver = new ChromeDriver();

class SortByPrice implements Comparator {
    public int compare(Object o1, Object o2) {
        HotelStay.RoomStay.Quote s1 = (HotelStay.RoomStay.Quote) o1;
        HotelStay.RoomStay.Quote s2 = (HotelStay.RoomStay.Quote) o2;
        if (s1.price != null && s2.price != null && (Integer.parseInt(s1.price) > Integer.parseInt(s2.price)))
            return 1;
        return -1;
    }
}

class HotelStay {

    public String hotelSEQ;

    public String hotelName;

    public String totalCommentNumber;

    public String qunarCommentNumber;

    public List<RoomStay> roomStays = new ArrayList<HotelStay>();

    public RoomStay addNewRoomStay() {
        RoomStay roomStay = new RoomStay();
        roomStays.add(roomStay);
        return roomStay;
    }

    public static class RoomStay {

        public String roomType;

        public List<Quote> quotes = new ArrayList<HotelStay>();

        public Quote addNewQuote() {
            Quote quote = new Quote();
            quotes.add(quote);
            return quote;
        }

        public static class Quote {

            public String agent;

            public String product;

            public String meal;

            public String cancellationPolicy;

            public String price;

        }
    }
}

class CrawlerTask implements Callable {

    SessionFactory factory;

    org.hibernate.classic.Session session;

    String cityName;

    String[] hotelSEQs;

    WebDriver driver;

    String qunarUrl = "https://www.qunar.com/";

    public CrawlerTask(SessionFactory factory, String cityName) {
        this.cityName = cityName;
        this.factory = factory;
    }

    public CrawlerTask(SessionFactory factory, String[] hotelSEQs) {
        this.hotelSEQs = hotelSEQs;
        this.factory = factory;
    }

    public Boolean isElementExsit(WebDriver driver, By locator) {
        try {
            driver.findElement(locator);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public String call() throws Exception {

        WebDriver driver = new FirefoxDriver(); // open a new firefox

        System.out.println("opening session")
        this.session = factory.openSession();
        System.out.println("opened session")

        List<HotelStay> hotelStays = new ArrayList<HotelStay>();

        String ourAgent = "旅驿订房";
        String fromDateStr = "2017-09-12";
        String toDateStr = "2017-09-12";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int count = 1;

        List<PropertyBasicInfo> propertyBasicInfoList = new ArrayList<PropertyBasicInfo>();
        propertyBasicInfoList = this.session.createCriteria(PropertyBasicInfo.class)
                .add(Restrictions.isNotNull("qunarHotelCode "))
                .add(Restrictions.ne("qunarHotelCode", ""))
                .add(Restrictions.ne("qunarHotelCode", "qunar_hotel_not_found"))
                .add(Restrictions.isNotNull("qunarHotelName"))
                .add(Restrictions.ne("qunarHotelName", ""))
                .add(Restrictions.ne("qunarMappingStatus", 1))
                .list();

        System.out.println("getting hotels DONE,hotels' size =" + propertyBasicInfoList.size());

        try {
            driver.get(qunarUrl);
            String loginUrl = driver.findElement(By.id("__headerInfo_login__")).getAttribute("href");
            System.out.println("loginUrl = " + loginUrl);
            driver.get(loginUrl);
            String qqLoginUrl = driver.findElement(By.className("login_qq")).getAttribute("href");
            System.out.println("qqLoginUrl = " + qqLoginUrl);
            driver.get(qqLoginUrl);
            driver.switchTo().frame("ptlogin_iframe");
            WebElement qqSpan = driver.findElement(By.xpath("//a[@tabindex=2]"));
            qqSpan.click();
            Thread.sleep(20000);
            driver.switchTo().defaultContent();
            System.out.println("DRIVE TITLE = " + driver.getTitle());
            String homeUrl = driver.findElement(By.className("q_header_uname")).getAttribute("href");
            driver.get(homeUrl);
            System.out.println("homeUrl = " + homeUrl);
            System.out.println("DRIVE TITLE = " + driver.getTitle());
            driver.findElement(By.id("x_form_login")).findElement(By.className("textbox")).sendKeys("946669258");
            driver.findElement(By.id("x_btn_login")).click();
            Thread.sleep(30000);
        }
        catch (Exception e1) {
            System.out.println(e1.getMessage());
        }

        for (PropertyBasicInfo propertyBasicInfo : propertyBasicInfoList) {

            try {
                long totalStart = System.currentTimeMillis();

                String qunarCityCode = propertyBasicInfo.getQunarHotelCode().substring(0, propertyBasicInfo.getQunarHotelCode().lastIndexOf("_"));
                System.out.println(qunarCityCode + " No." + count++);
                String hotelCode = propertyBasicInfo.getQunarHotelCode().substring(propertyBasicInfo.getQunarHotelCode().lastIndexOf("_") + 1);
                System.out.println("QunarHotelCode=" + propertyBasicInfo.getQunarHotelCode());
                System.out.println("hotelCode=" + hotelCode);
                System.out.println("qunarCityCode=" + qunarCityCode);

                if (fromDateStr.equals("") || toDateStr.equals("")) {
                    Date currentDate = new Date();
                    Calendar rightNow = Calendar.getInstance();
                    rightNow.setTime(currentDate);
                    rightNow.add(Calendar.DAY_OF_YEAR, 20);//当前日期加20天
                    Date fromDate = rightNow.getTime();
                    fromDateStr = sdf.format(fromDate);
                    rightNow.add(Calendar.DAY_OF_YEAR, 2);//当前日期加22天
                    Date toDate = rightNow.getTime();
                    toDateStr = sdf.format(toDate);
                    System.out.println(fromDateStr + "----" + toDateStr);
                }

//    String url = "http://hotel.qunar.com/city/hongkong_city/dt-2062/?_=1#tag=hongkong_city&fromDate=2017-06-27&toDate=2017-05-28&q=&from=qunarHotel&fromFocusList=0&filterid=1ccae231-d3e4-4b0b-bcb7-23d636d56e47_A&showMap=0&qptype=&QHFP=ZSS_A811125E&cityurl=hongkong_city&HotelSEQ=hongkong_city_2062&rnd=1495810756527&sgroup=1&roomNum=1";
                String url = "http://hotel.qunar.com/city/" + qunarCityCode + "/dt-" + hotelCode + "/?_=1#tag=" + qunarCityCode + "&fromDate=" + fromDateStr + "&toDate=" + toDateStr + "&q=&from=qunarHotel" +
                        "&fromFocusList=0&filterid=1ccae231-d3e4-4b0b-bcb7-23d636d56e47_A&showMap=0&qptype=&QHFP=ZSS_A811125E" +
                        "&cityurl=" + qunarCityCode + "&HotelSEQ=" + propertyBasicInfo.getQunarHotelCode() + "&rnd=1495810756527&sgroup=1&roomNum=1";
                System.out.println("getting " + url);

                long getStart = System.currentTimeMillis();
                driver.get(url);
                System.out.println("count=" + count);

                Thread.sleep(20000);
//                new WebDriverWait(driver, 5).until(ExpectedConditions.presenceOfElementLocated(By.className("hotel-quote-list")));
                long getEnd = System.currentTimeMillis();
                System.out.println("getTime=" + (getEnd - getStart));

                long findStart = System.currentTimeMillis();
                String hotelLongName = driver.findElement(By.cssSelector("#detail_pageHeader > h2 > span > font > font")).text;
                String address = driver.findElement(By.cssSelector("#detail_pageHeader > p > span:nth-child(1)")).getAttribute("title");
                System.out.println("hotelLongName=" + hotelLongName);
                System.out.println("address=" + address);

                String[] splitHotelName = hotelLongName.trim().substring(0, hotelLongName.length() - 1).split("\\(");
                if (splitHotelName != null && splitHotelName.size() > 1) {
                    propertyBasicInfo.setQunarHotelName(splitHotelName[1]);
                    propertyBasicInfo.setQunarHotelNameZH(splitHotelName[0]);
                }
                if (address != null)
                    propertyBasicInfo.setQunarAddress(address);

                this.session.saveOrUpdate(propertyBasicInfo);

                long findEnd = System.currentTimeMillis();
                System.out.println("findTime=" + (findEnd - findStart));
                long totalEnd = System.currentTimeMillis();
                System.out.println("totalTime=" + (totalEnd - totalStart));
            }
            catch (Exception e) {
                e.printStackTrace(System.out);
                if (this.session.getTransaction().isActive()) {
                    this.session.getTransaction().rollback();
                }
            }
        }
        return null;
    }
}

org.hibernate.classic.Session session;

String qunarUrl = "https://www.qunar.com/";

WebDriver driver = new FirefoxDriver(); // open a new firefox

System.out.println("opening session")
session = factory.openSession();
System.out.println("opened session")

List<HotelStay> hotelStays = new ArrayList<HotelStay>();

String ourAgent = "旅驿订房";
String fromDateStr = "2017-09-12";
String toDateStr = "2017-09-12";
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

int count = 1;

List<PropertyBasicInfo> propertyBasicInfoList = new ArrayList<PropertyBasicInfo>();
propertyBasicInfoList = session.createCriteria(PropertyBasicInfo.class)
        .add(Restrictions.isNotNull("qunarHotelCode"))
        .add(Restrictions.ne("qunarHotelCode", ""))
        .add(Restrictions.ne("qunarHotelCode", "qunar_hotel_not_found"))
        .add(Restrictions.or(Restrictions.isNull("qunarHotelName"), Restrictions.eq("qunarHotelName", "")))
        .add(Restrictions.ne("qunarMappingStatus", 1))
        .list();

System.out.println("getting hotels DONE,hotels' size =" + propertyBasicInfoList.size());

try {
    driver.get(qunarUrl);
    String loginUrl = driver.findElement(By.id("__headerInfo_login__")).getAttribute("href");
    System.out.println("loginUrl = " + loginUrl);
    driver.get(loginUrl);
    String qqLoginUrl = driver.findElement(By.className("login_qq")).getAttribute("href");
    System.out.println("qqLoginUrl = " + qqLoginUrl);
    driver.get(qqLoginUrl);
    driver.switchTo().frame("ptlogin_iframe");
    WebElement qqSpan = driver.findElement(By.xpath("//a[@tabindex=2]"));
    qqSpan.click();
    Thread.sleep(5000);
    driver.switchTo().defaultContent();
    System.out.println("DRIVE TITLE = " + driver.getTitle());
    String homeUrl = driver.findElement(By.className("q_header_uname")).getAttribute("href");
    driver.get(homeUrl);
    System.out.println("homeUrl = " + homeUrl);
    System.out.println("DRIVE TITLE = " + driver.getTitle());
    driver.findElement(By.id("x_form_login")).findElement(By.className("textbox")).sendKeys("946669258");
    driver.findElement(By.id("x_btn_login")).click();
    Thread.sleep(5000);
}
catch (Exception e1) {
    System.out.println(e1.getMessage());
}

for (PropertyBasicInfo propertyBasicInfo : propertyBasicInfoList) {

    try {
        long totalStart = System.currentTimeMillis();

        String qunarCityCode = propertyBasicInfo.getQunarHotelCode().substring(0, propertyBasicInfo.getQunarHotelCode().lastIndexOf("_"));
        System.out.println(qunarCityCode + " No." + count++);
        String hotelCode = propertyBasicInfo.getQunarHotelCode().substring(propertyBasicInfo.getQunarHotelCode().lastIndexOf("_") + 1);
        System.out.println("QunarHotelCode=" + propertyBasicInfo.getQunarHotelCode());
        System.out.println("hotelCode=" + hotelCode);
        System.out.println("qunarCityCode=" + qunarCityCode);

        if (fromDateStr.equals("") || toDateStr.equals("")) {
            Date currentDate = new Date();
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(currentDate);
            rightNow.add(Calendar.DAY_OF_YEAR, 20);//当前日期加20天
            Date fromDate = rightNow.getTime();
            fromDateStr = sdf.format(fromDate);
            rightNow.add(Calendar.DAY_OF_YEAR, 2);//当前日期加22天
            Date toDate = rightNow.getTime();
            toDateStr = sdf.format(toDate);
            System.out.println(fromDateStr + "----" + toDateStr);
        }

//    String url = "http://hotel.qunar.com/city/hongkong_city/dt-2062/?_=1#tag=hongkong_city&fromDate=2017-06-27&toDate=2017-05-28&q=&from=qunarHotel&fromFocusList=0&filterid=1ccae231-d3e4-4b0b-bcb7-23d636d56e47_A&showMap=0&qptype=&QHFP=ZSS_A811125E&cityurl=hongkong_city&HotelSEQ=hongkong_city_2062&rnd=1495810756527&sgroup=1&roomNum=1";
        String url = "http://hotel.qunar.com/city/" + qunarCityCode + "/dt-" + hotelCode + "/?_=1#tag=" + qunarCityCode + "&fromDate=" + fromDateStr + "&toDate=" + toDateStr + "&q=&from=qunarHotel" +
                "&fromFocusList=0&filterid=1ccae231-d3e4-4b0b-bcb7-23d636d56e47_A&showMap=0&qptype=&QHFP=ZSS_A811125E" +
                "&cityurl=" + qunarCityCode + "&HotelSEQ=" + propertyBasicInfo.getQunarHotelCode() + "&rnd=1495810756527&sgroup=1&roomNum=1";
        System.out.println("getting " + url);

        long getStart = System.currentTimeMillis();
        driver.capabilities.javascriptEnabled = false;
        driver.get(url);
        System.out.println("count=" + count);

//        Thread.sleep(5000);
//                new WebDriverWait(driver, 5).until(ExpectedConditions.presenceOfElementLocated(By.className("hotel-quote-list")));
        long getEnd = System.currentTimeMillis();
        System.out.println("getTime=" + (getEnd - getStart));

        long findStart = System.currentTimeMillis();
        String hotelLongName = driver.findElement(By.cssSelector(".htl-longname > span:nth-child(1)")).text;
        String address = driver.findElement(By.cssSelector(".adress > span:nth-child(1)")).getAttribute("title");

        System.out.println("hotelLongName=" + hotelLongName);
        System.out.println("address=" + address);

        String[] splitHotelName = hotelLongName.trim().substring(0, hotelLongName.length() - 1).split("\\(");
        System.out.println("hotelName=" + splitHotelName[1]);
        System.out.println("hotelNameZH=" + splitHotelName[0]);
        if (splitHotelName != null && splitHotelName.size() > 1) {
            propertyBasicInfo.setQunarHotelName(splitHotelName[1]);
            propertyBasicInfo.setQunarHotelNameZH(splitHotelName[0]);
        }
        if (address != null)
            propertyBasicInfo.setQunarAddress(address);

        session.beginTransaction();
        session.saveOrUpdate(propertyBasicInfo);
        session.getTransaction().commit();

        long findEnd = System.currentTimeMillis();
        System.out.println("findTime=" + (findEnd - findStart));
        long totalEnd = System.currentTimeMillis();
        System.out.println("totalTime=" + (totalEnd - totalStart));
    }
    catch (Exception e) {
        e.printStackTrace(System.out);
    }
}