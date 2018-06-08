
import com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfo
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.hibernate.Criteria
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.criterion.ProjectionList
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions
import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver;

import java.text.SimpleDateFormat;
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
//        "hibernate.connection.url"           : "jdbc:mysql://192.168.10.105:3306/gallop?useUnicode=true&amp;characterEncoding=UTF-8",
        "hibernate.connection.url"           : "jdbc:mysql://192.168.6.10:3306/giata?useUnicode=true&amp;characterEncoding=UTF-8",
        "hibernate.connection.username"      : "chengcsw",
        "hibernate.connection.password"      : "ccheng",
        "hibernate.connection.pool_size"     : "1",
        "hibernate.jdbc.batch_size"          : "1000",
//        "hibernate.show_sql"          : "true",
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

    public Boolean isElementExsit(WebDriver driver, By locator){
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
//        WebDriver driver = new RemoteWebDriver(
//                new URL("http://localhost:4444/wd/hub"),
//                DesiredCapabilities.firefox());
//        System.out.println("hi");
//        FirefoxProfile fp = new FirefoxProfile();
//        DesiredCapabilities dc = new FirefoxOptions();
//        dc.setCapability(FirefoxDriver.PROFILE, fp);
        // WebDriver driver = new RemoteWebDriver(dc);
//        DesiredCapabilities capability = DesiredCapabilities.firefox();
        /*try {
            DesiredCapabilities capability = DesiredCapabilities.firefox();
            WebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capability);
            System.out.println(driver);

            System.out.println("hi");
            *//*
            FirefoxDriver driver = new FirefoxDriver();
            System.out.println("hi");
            HttpCommandExecutor executor = (HttpCommandExecutor) driver.getCommandExecutor();
            System.out.println("hi");
            URL url = executor.getAddressOfRemoteServer();
            System.out.println("hi");
            SessionId session_id = driver.getSessionId();
            System.out.println("hi");
            *//*
        }
        catch (Exception e) {
            e.printStackTrace(System.out)
        }

        driver.get("http://www.google.com");
        System.out.println("hi2");*/

        System.out.println("opening session")
        this.session = factory.openSession();
        System.out.println("opened session")

        List<HotelStay> hotelStays = new ArrayList<HotelStay>();

        String ourAgent = "旅驿订房";
        String fromDateStr = "2017-09-30";
        String toDateStr = "2017-09-30";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int count = 1;
// http://thiagomarzagao.com/2013/11/12/webscraping-with-selenium-part-1/
// http://www.guru99.com/xpath-selenium.html

        List<PropertyBasicInfo> propertyBasicInfoList = new ArrayList<PropertyBasicInfo>();
        if (cityName != null && !cityName.equals("")) {

            System.out.println("getting hotels - " + cityName);
            Criteria criteria = session.createCriteria(PropertyBasicInfo.class);
            criteria.add(Restrictions.like("qunarHotelCode", "%" + cityName + "%"));
            ProjectionList projectionList = Projections.projectionList();
            projectionList.add(Projections.property("qunarCityCode"));
            criteria.setProjection(Projections.distinct(projectionList));
            List<String> qunarCityCodes = criteria.list();
            System.out.println("relative qunarCityCode number =  - " + qunarCityCodes.size());

            propertyBasicInfoList = this.session.createCriteria(PropertyBasicInfo.class)
                    .add(Restrictions.in("qunarCityCode", qunarCityCodes))
//                        .add(Restrictions.eq("qunarCityCode", cityName ))
//                        .add(Restrictions.like("qunarHotelCode", "%" + cityName + "%")) //用like在6.10的数据库下的查询速度虽然慢，但可以接受，而且代码简单。
                    .add(Restrictions.eq("popularity", 1))
                    .list();
        } else if (hotelSEQs.length != 0) {
            propertyBasicInfoList = this.session.createCriteria(PropertyBasicInfo.class)
                    .add(Restrictions.in("qunarHotelCode", hotelSEQs))
//                        .add(Restrictions.eq("qunarCityCode", cityName ))
//                        .add(Restrictions.like("qunarHotelCode", "%" + cityName + "%")) //用like在6.10的数据库下的查询速度虽然慢，但可以接受，而且代码简单。
                    .add(Restrictions.eq("popularity", 1))
                    .list();
        }

        System.out.println("getting hotels DONE");

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFCellStyle dateCellStyle = wb.createCellStyle();
        HSSFSheet sheet1 = wb.createSheet("qunar");
        int rowNum = 0;
        HSSFRow headerRow = sheet1.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("hotelSEQ");
        headerRow.createCell(1).setCellValue("hotelName");
        headerRow.createCell(2).setCellValue("roomType");
        headerRow.createCell(3).setCellValue("agent");
        headerRow.createCell(4).setCellValue("product");
        headerRow.createCell(5).setCellValue("meal");
        headerRow.createCell(6).setCellValue("cancellationPolicy");
        headerRow.createCell(7).setCellValue("price");
        headerRow.createCell(8).setCellValue("our Price");
        headerRow.createCell(9).setCellValue("Our Rank");
        headerRow.createCell(10).setCellValue("difference(ourPrice - price)");
        headerRow.createCell(11).setCellValue("total comments");
        headerRow.createCell(12).setCellValue("qunar comments");

        HSSFRow row = null;

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
//            driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "t");
//            driver.switchTo().window(webDriver.getWindowHandles().toArray()[1].toString());
//            driver.get(homeUrl);
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
                HotelStay hotelStay = new HotelStay();
                hotelStays.add(hotelStay);
                hotelStay.hotelSEQ = propertyBasicInfo.getQunarHotelCode();
                hotelStay.hotelName = propertyBasicInfo.hotelName;
                boolean isUpdate = true;
                String updateHotelSEQ = hotelStay.hotelSEQ;
                WebElement jsCommentsCount = driver.findElement(By.id("js_commentsCount_nav"));
                String totalComments = jsCommentsCount.text;
                Boolean qunarCommentExsit = isElementExsit(driver,By.cssSelector("#jd_comments > div > div.b_ugcheader > div.b_ugcfilter > div:nth-child(2) > h3"));
                String qunarComments = "";
                if (qunarCommentExsit) {
                    qunarComments = driver.findElement(By.cssSelector("#jd_comments > div > div.b_ugcheader > div.b_ugcfilter > div:nth-child(2) > h3")).text;
                }

                if (totalComments != null && !totalComments.equals("")) {
                    hotelStay.totalCommentNumber = totalComments.substring(1, totalComments.length() - 1);
                } else {
                    hotelStay.totalCommentNumber = "0";
                }
                if (!qunarComments.equals(""))
                    hotelStay.qunarCommentNumber = qunarComments.substring(7,qunarComments.length()-3);
                else
                    hotelStay.qunarCommentNumber = "0";

                for (WebElement hotelQuoteElement : driver.findElements(By.className("hotel-quote-list"))) {
                    HotelStay.RoomStay roomStay = hotelStay.addNewRoomStay();
                    WebElement rtype = hotelQuoteElement.findElement(By.className("rtype"));
                    roomStay.roomType = (rtype.findElement(By.tagName("h2")).findElement(By.tagName("a")).getAttribute("title"));
                    System.out.println("title=" + rtype.findElement(By.tagName("h2")).findElement(By.tagName("a")).getAttribute("title"));
                    for (WebElement tblSameType : hotelQuoteElement.findElements(By.className("tbl-same-type"))) {
                        WebElement tr = tblSameType.findElement(By.tagName("tbody")).findElement(By.tagName("tr"));
                        HotelStay.RoomStay.Quote quote = roomStay.addNewQuote();
                        for (WebElement td : tr.findElements(By.tagName("td"))) {
                            switch (td.getAttribute("class")) {
                                case "e1 js-logo":
                                    quote.agent = td.findElement(By.tagName("div")).findElement(By.tagName("img")).getAttribute("alt");
                                    break;
                                case "e2":
                                    quote.product = td.text;
                                    break;
                                case "e4":
                                    quote.meal = td.text;
                                    break;
                                case "e5":
                                    quote.cancellationPolicy = td.text;
                                    break;
                                case "e6 js-price":
                                    for (WebElement div : td.findElement(By.tagName("div")).findElements(By.tagName("div"))) {
                                        if (div.text.contains("包含税费总价为¥")) {
                                            quote.price = div.findElement(By.className("price-act")).text.replace("包含税费总价为¥", "");
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    int rankIndex = 0;
                    int ourRank = 99;
                    Double ourPrice = 0d;
                    Double lowestPrice = null;
                    Collections.sort(roomStay.quotes, new SortByPrice());
                    for (HotelStay.RoomStay.Quote quote : roomStay.quotes) {
                        rankIndex++;
                        if (ourRank == 99 && ourAgent.equals(quote.agent) && quote.price != null) {
                            ourPrice = Double.parseDouble(quote.price);
                            ourRank = rankIndex;
                        }
                        if (lowestPrice == null && ourRank != 1 && quote.price != null) {
                            lowestPrice = Double.parseDouble(quote.price);
                        }
                    }
                    if (ourRank <= 5) isUpdate = false;

                    for (HotelStay.RoomStay.Quote quote : roomStay.quotes) {
                        row = sheet1.createRow(rowNum++);
                        row.createCell(0).setCellValue(hotelStay.hotelSEQ);
                        row.createCell(1).setCellValue(hotelStay.hotelName);
                        row.createCell(2).setCellValue(roomStay.roomType);
                        row.createCell(3).setCellValue(quote.agent);
                        row.createCell(4).setCellValue(quote.product);
                        row.createCell(5).setCellValue(quote.meal);
                        row.createCell(6).setCellValue(quote.cancellationPolicy);
                        row.createCell(7).setCellValue(quote.price);
                        row.createCell(8).setCellValue(ourPrice);
                        row.createCell(9).setCellValue(ourRank);

                        if (ourRank != 1 && lowestPrice != null && ourPrice != null) {
                            row.createCell(10).setCellValue(ourPrice - lowestPrice);
                        } else {
                            row.createCell(10).setCellValue(0);
                        }

                        row.createCell(11).setCellValue(hotelStay.totalCommentNumber);
                        row.createCell(12).setCellValue(hotelStay.qunarCommentNumber);
                    }
                }
                if (isUpdate == true) {
//                    this.session.beginTransaction();
//                    PropertyBasicInfo propertyBasicInfo1 = session.createCriteria(PropertyBasicInfo.class)
//                            .add(Restrictions.eq("qunarHotelCode", updateHotelSEQ)).uniqueResult();
//                    if (propertyBasicInfo1 != null) {
//                        propertyBasicInfo1.setPopularity(99);
//                    }
//                    this.session.saveOrUpdate(propertyBasicInfo1);
//                    this.session.getTransaction().commit();
                    System.out.println("--------------------update success------------");
                }
                row = sheet1.createRow(rowNum++);
                row.createCell(5).setCellValue("Date Range:");
                row.createCell(6).setCellValue(fromDateStr + " -- " + toDateStr);
                row.createCell(7).setCellValue("Completed Time:");
                row.createCell(8).setCellValue(sdf2.format(new Date()));
                row.createCell(9).setCellValue("Is Update");
                row.createCell(10).setCellValue(isUpdate);

                String filename = "/tmp/crawler/qunar-bySEQ.xls";
                if (cityName != null && !cityName.equals("")) {
                    filename = "/tmp/crawler/qunar-" + cityName + ".xls";
                }
                FileOutputStream fos = new FileOutputStream(filename)
                System.out.println(filename);
                System.out.println("comleted Time =" + sdf2.format(new Date()));
                wb.write(fos);
                fos.close();

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
                continue;
            }
        }
        return null;
    }
}

int threadPoolSize = 1000;
ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
String[] cityNames = [];
//String[] cityNames = ['koh_phuket_tha','surat_thani_th','bangkok','kuala_lumpur','singapore_city','seoul','sydney_nsw'];
//String[] hotelSEQs = ['seoul_2938', 'seoul_3445', 'pattaya_86', 'jeju_502', 'chiang_mai_2674', 'koh_phuket_tha_10693', 'bangkok_5323', 'seoul_3408', 'koh_phuket_tha_11012', 'koh_phuket_tha_3526', 'kuala_lumpur_2237', 'bangkok_3463', 'seoul_2109', 'singapore_city_1794', 'koh_phuket_tha_3534', 'seoul_1137', 'bangkok_4155', 'bangkok_5359'];
//String[] hotelSEQs = ['bangkok_3485', 'bangkok_5539', 'bangkok_5207','seoul_3483', 'koh_phuket_tha_3120', 'seoul_2584', 'koh_phuket_tha_10036', 'pattaya_2235','chiang_mai_3392','bangkok_791', 'bangkok_4584'];
String[] hotelSEQs = ['bali_11115',
                              'kuala_lumpur_2237',
                              'krabi_th_28',
                              'manila_1155',
                              'bangkok_528',
                              'bangkok_2840',
                              'bangkok_5198',
                              'bangkok_1029',
                              'bangkok_3727',
                              'bangkok_5384',
                              'koh_phuket_tha_4659',
                              'koh_phuket_tha_4642',
                              'koh_phuket_tha_4111',
                              'seoul_3218',
                              'surat_thani_th_3524',
                              'surat_thani_th_3025'];
//String[] cityNames = ['koh_phuket_tha','surat_thani_th','khanh_hoa_province','tokyo','saipan_mnp','busan_kr','sydney_nsw', 'singapore_city','bali', 'da_nang_vn'];
//String[] cityNames = ['hanoi_vn','sabah','kuala_lumpur','osaka', 'seoul', 'george_town_pen','koh_phuket_tha', 'surat_thani_th', 'krabi_thv', 'bangkok', 'pattaya', 'chiang_mai', 'ko_phi_phi_don', 'prachuap_khiri_khan_th', 'koh_chang_th、sapporo_hok', 'kobe_hyo', 'nagoya_aic'];
executor.execute(new FutureTask(new CrawlerTask(factory, hotelSEQs)));
//for (String name : cityNames) {
//    executor.execute(new FutureTask(new CrawlerTask(factory, name)));
//}