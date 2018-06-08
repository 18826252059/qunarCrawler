#!/usr/bin/env groovy

import com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfo
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.criterion.Restrictions
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.firefox.*;

import java.sql.PreparedStatement
import java.sql.Timestamp

/* cp -a /usr/local/share/gallop/lib /usr/local/share/
* add gallop-*.jar to /usr/local/share/lib
* groovy -cp "/usr/local/share/lib/*" qunarHotaHotelCrawler.groovy
*/

@Grapes([
        @Grab(group = 'dom4j', module = 'dom4j', version = '1.6.1'),
        @Grab(group = 'org.hibernate', module = 'hibernate-annotations', version = '3.4.0.GA'),
        @Grab(group = 'org.hibernate', module = 'hibernate-ehcache', version = '3.6.10.Final'),
        @Grab(group = 'org.hibernate', module = 'hibernate-core', version = '3.6.10.Final'),
//        @Grab(group = 'hsqldb', module = 'hsqldb', version = '1.8.0.7'),
        @Grab(group = 'javassist', module = 'javassist', version = '3.4.GA'),
        @Grab(group = 'net.sourceforge.htmlcleaner', module = 'htmlcleaner', version = '2.6.1'),
        @Grab(group = 'commons-validator', module = 'commons-validator', version = '1.4.0'),
        @Grab(group = 'commons-httpclient', module = 'commons-httpclient', version = '3.1'),
        @Grab(group = 'commons-collections', module = 'commons-collections', version = '3.2.1'),
        @Grab(group = 'org.apache.commons', module = 'commons-lang3', version = '3.4'),
        @Grab(group = 'commons-math', module = 'commons-math', version = '1.2'),
//        @Grab(group = 'struts', module = 'struts', version = '1.2.9'),
        @Grab(group = 'org.apache.poi', module = 'poi', version = '3.9'),
//        @Grab(group = 'org.ostermiller', module = 'utils', version = '1.07.00'),
        @Grab(group = 'mysql', module = 'mysql-connector-java', version = '5.1.28'),
        @Grab(group = 'org.mongodb', module = 'mongo-java-driver', version = '3.2.0'),
//        @Grab(group = 'org.apache.cxf', module = 'cxf-bundle', version = '2.7.2'),
        @Grab(group = 'org.codehaus.woodstox', module = 'woodstox-core-asl', version = '4.2.1'),
        @Grab(group = 'org.codehaus.woodstox', module = 'woodstox-core-lgpl', version = '4.2.1'),
        @Grab(group = 'org.codehaus.woodstox', module = 'stax2-api', version = '3.1.1'),
        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-core', version = '2.3.3'),
        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-databind', version = '2.3.3'),
        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-databind', version = '2.3.3'),
        @Grab(group = 'org.apache.commons', module = 'commons-email', version = '1.3.1'),
        @Grab(group = 'org.slf4j', module = 'slf4j-nop', version = '1.7.5'),
        @Grab(group = 'org.seleniumhq.selenium', module='selenium-java', version='3.4.0'),
        @Grab(group = 'org.json', module='json', version='20180130'),
//        @Grab(group = 'net.sf.json-lib', module='json-lib', version='2.3'),
        @Grab(group = 'gallop', module = 'gallop', version = '20171029'), // internal
])

def hibProps = [
        "hibernate.dialect"                  : "org.hibernate.dialect.MySQL5InnoDBDialect",
        "hibernate.connection.driver_class"  : "com.mysql.jdbc.Driver",
        "hibernate.connection.url"           : "jdbc:mysql://192.168.10.105:3306/giata?useUnicode=true&amp;characterEncoding=UTF-8",
//        "hibernate.connection.url"           : "jdbc:mysql://192.168.6.10:3306/giata?useUnicode=true&amp;characterEncoding=UTF-8",
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
org.hibernate.classic.Session session = factory.openSession();
java.sql.Connection connection = session.connection();

System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog"); // disable Selenium log

String qunarOTAUrl = "http://hota.qunar.com/stats/ohtml/announcement/queryAnnouncements";
String username = "ha9ws:admin";
String password = "ChrisMarcos66";
int pageSize = 100; // maximum on the page is 100. If we set this more than 100, Qunar will be able to detect we use crawler

// Firefox
// Download gecko drivers here: https://github.com/mozilla/geckodriver/releases
//System.setProperty("webdriver.gecko.driver", "/geckodriver-v0.16.1-win64/geckodriver.exe"); // Firefox for Windows
//System.setProperty("webdriver.gecko.driver", "/usr/sbin/geckodriver");// Firefox for Linux
//FirefoxBinary firefoxBinary = new FirefoxBinary();
//firefoxBinary.addCommandLineOptions("--headless")
//FirefoxOptions firefoxOptions = new FirefoxOptions();
//firefoxOptions.setBinary(firefoxBinary);
//WebDriver driver = new FirefoxDriver(firefoxOptions);

// Chrome
//System.setProperty("webdriver.chrome.driver", "/chromedriver_win32/chromedriver.exe"); // Chrome for Windows
//WebDriver driver = new ChromeDriver();

// PhantomJS
DesiredCapabilities caps = new DesiredCapabilities();
caps.setJavascriptEnabled(true);
caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "C:\\Users\\Jack Zhong\\Desktop\\work\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "/usr/bin/phantomjs");
WebDriver driver = new PhantomJSDriver(caps);

// Login: Username and password
driver.get(qunarOTAUrl);
WebElement userNameInput = driver.findElement(By.cssSelector(".login-name > input:nth-child(1)"));
WebElement passwordInput = driver.findElement(By.cssSelector(".login-pwd > input:nth-child(1)"));
WebElement loginBtn = driver.findElement(By.cssSelector("#js-sub-login"));
userNameInput.sendKeys(username);
passwordInput.sendKeys(password);
loginBtn.click();

// Click on the menu
WebDriverWait wait = new WebDriverWait(driver,(long)5);
WebElement hotelManageElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("ul.menu:nth-child(1) > li:nth-child(3) > a:nth-child(1)")));
hotelManageElement.click();

// Search Hotels
int totalCount = 1000;
int i = 1;
int rowCount = 0;
int noneCount = 0;
long startTime = System.currentTimeMillis();
try {
    while (rowCount < totalCount) {

        String script = "var xhr = new XMLHttpRequest();\n" +
                "xhr.open('POST', 'http://hota.qunar.com/baseinfo/oapi/shotel/search', false);\n" +
                "xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');\n" +
                "var responseText2 = '';" +
                "xhr.onload = function () {\n" +
                "responseText2 = this.responseText" +
                "};\n" +
                "\n" +
                "xhr.send(JSON.stringify({distance: \"1\", page: " + i++ + ", pageSize: " + pageSize + ", supplierId: \"1237149\"})); return responseText2";
        System.out.println("page=" + i);
        Object response = null;
        while (response == null) {
            try {
                response = driver.executeScript(script);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        FileWriter fileWriter = new FileWriter("/tmp/" + (i-1) + ".json");
        fileWriter.write((String) response);
        fileWriter.close();
        Thread.sleep(100); // this is necessary

//        i++;
//        FileReader fileReader = new FileReader("/temp/" + (i-1) + ".json");
//        BufferedReader bufferreader = new BufferedReader(fileReader);
//        String response = bufferreader.readLine();

        org.json.JSONObject jsonObject = new org.json.JSONObject((String) response);
        org.json.JSONObject data = jsonObject.getJSONObject("data");
        org.json.JSONObject result = data.getJSONObject("result");
        org.json.JSONArray list = result.getJSONArray("list");
        totalCount = Integer.parseInt((String) result.getString("totalCount"));

        PreparedStatement pstmt = connection.prepareStatement("update giata.tti_property_basic_info set qunarHotelCode = ?, qunarMapped = ? where TTIcode = ?");
        PreparedStatement pstmt2 = connection.prepareStatement("update giata.tti_property_basic_info set qunarHotelCode = ?, qunarMapped = ?, qunarMappedDate = ? where TTIcode = ? and qunarMappedDate is NULL ");

        for (int j=0; j<list.length(); j++) {
            rowCount++;
            org.json.JSONObject item = list.getJSONObject(j);
            org.json.JSONObject bizInfo = item.getJSONObject("bizInfo");
            String partnerHotelId = bizInfo.getString("partnerHotelId");
            String hotelSeq = item.getString("hotelSeq");
            System.out.println("hotelSeq='" + hotelSeq + "' (" + rowCount + "/" + totalCount + ")");

            if (hotelSeq != null && !"".equals(hotelSeq)) {
                pstmt.setString(1, hotelSeq);
                pstmt.setBoolean(2, true);
                pstmt.setString(3, partnerHotelId);
                pstmt.addBatch();
            }
            else {
                pstmt2.setString(1, "none");
                pstmt2.setBoolean(2, false);
                pstmt2.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
                pstmt2.setString(4, partnerHotelId);
                pstmt2.addBatch();
                noneCount++;
            }
        }

        pstmt.executeBatch();
        pstmt2.executeBatch();
        System.out.println("noneCount=" + noneCount);
    }

} catch (Exception e) {
    System.out.println(e.getMessage());
}

long endTime = System.currentTimeMillis();
System.out.println("elapsedTime=" + ((endTime - startTime) / 1000) + "s");
driver.close();
driver.quit();

System.exit(0);