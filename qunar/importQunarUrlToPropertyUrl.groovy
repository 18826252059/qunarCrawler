
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfo
import com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfoUrl
import org.apache.commons.httpclient.methods.StringRequestEntity
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.criterion.Restrictions
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.firefox.FirefoxDriver

import javax.mail.Folder
import javax.mail.Message
import javax.mail.Store
import java.util.regex.Matcher
import java.util.regex.Pattern

@Grapes([
        @Grab(group = 'org.hibernate', module = 'hibernate-annotations', version = '3.4.0.GA'),
        @Grab(group = 'org.hibernate', module = 'hibernate-ehcache', version = '3.6.10.Final'),
//        @Grab(group = 'org.slf4j', module = 'slf4j-log4j12', version = '1.7.5'),
        @Grab(group = 'hsqldb', module = 'hsqldb', version = '1.8.0.7'),
        @Grab(group = 'javassist', module = 'javassist', version = '3.4.GA'),
        @Grab(group = 'net.sourceforge.htmlcleaner', module = 'htmlcleaner', version = '2.6.1'),
        @Grab(group = 'commons-validator', module = 'commons-validator', version = '1.4.0'),
        @Grab(group = 'commons-httpclient', module = 'commons-httpclient', version = '3.1'),
        @Grab(group = 'commons-collections', module = 'commons-collections', version = '3.2.1'),
        @Grab(group = 'commons-math', module = 'commons-math', version = '1.2'),
        @Grab(group = 'struts', module = 'struts', version = '1.2.9'),
        @Grab(group = 'org.apache.poi', module = 'poi', version = '3.9'),
        @Grab(group = 'org.ostermiller', module = 'utils', version = '1.07.00'),
        @Grab(group = 'mysql', module = 'mysql-connector-java', version = '5.1.28'),
        @Grab(group = 'org.ostermiller', module = 'utils', version = '1.07.00'),
        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-core', version = '2.3.3'),
        @Grab(group = 'org.mongodb', module = 'mongo-java-driver', version = '3.2.0'),
        @Grab(group = 'org.seleniumhq.selenium', module = 'selenium-java', version = '3.4.0'),
        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-databind', version = '2.3.3'),
        @Grab(group = 'org.json', module = 'json', version = '20180130'),
//        @Grab(group = 'influxdb-java', module = 'influxdb-java', version = '2.7'),
//        @Grab(group = 'spring-data-influxdb', module = 'spring-data-influxdb', version = '1.6'),
        @Grab(group = 'org.apache.cxf', module = 'cxf-bundle', version = '2.7.2'),
//        @Grab(group = 'sabre-iur', module = 'sabre-iur', version = '20131012'), // internal
//        @Grab(group = 'com.microsoft.sqlserver', module = 'sqljdbc4', version = '4.0'), // internal
//        @Grab(group = 'gallop', module = 'gallop', version = '20150318'), // internal
        @Grab(group = 'gallop', module = 'gallop', version = '20171029'), // internal
        @Grab(group = 'javax.mail', module = 'mail', version = '1.4'),
        @Grab(group = 'org.apache.poi', module = 'poi', version = '3.9'),
        @Grab(group = 'org.apache.poi', module = 'poi-ooxml', version = '3.9'),//need to run in Linux
])


def hibProps = [
        "hibernate.dialect"                  : "org.hibernate.dialect.MySQL5InnoDBDialect",
        "hibernate.connection.driver_class"  : "com.mysql.jdbc.Driver",
//        "hibernate.connection.url"           : "jdbc:mysql://192.168.10.105:3306/gallop?useUnicode=true&amp;characterEncoding=UTF-8",
//        "hibernate.connection.url"           : "jdbc:mysql://192.168.6.10:3306/giata?useUnicode=true&amp;characterEncoding=UTF-8",
//        "hibernate.connection.username"      : "celinecheung",
//        "hibernate.connection.password"      : "celine",
        "hibernate.connection.url"           : "jdbc:mysql://192.168.10.105:3306/giata?useUnicode=true&amp;characterEncoding=UTF-8",
        "hibernate.connection.username"      : "chengcsw",
        "hibernate.connection.password"      : "ccheng",
//        "hibernate.connection.url"           : "jdbc:mysql://127.0.0.1:3306/giata?useUnicode=true&amp;characterEncoding=UTF-8",
//        "hibernate.connection.username"      : "root",
//        "hibernate.connection.password"      : "cl0secfg",
        "hibernate.connection.pool_size"     : "1",
        "hibernate.jdbc.batch_size"          : "1000",
//        "hibernate.show_sql"          : "true",
//        "hibernate.hbm2ddl.auto": "validate",
//        hibernate.jdbc.batch_size 20
        "hibernate.connection.autocommit"    : "true",
        "hibernate.cache.provider_class"     : "org.hibernate.cache.EhCacheProvider",
        "hibernate.transaction.factory_class": "org.hibernate.transaction.JDBCTransactionFactory",
]

def configureHibernate(props) {
    def Configuration config = new Configuration()
    props.each { k, v -> config.setProperty(k, v) }
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
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfoUrl.class);
}
SessionFactory factory = this.configureHibernate(hibProps).buildSessionFactory();
Session session = factory.openSession();

// Download gecko drivers here: https://github.com/mozilla/geckodriver/releases
//System.setProperty("webdriver.gecko.driver", "/geckodriver-v0.16.1-win64/geckodriver.exe");
System.setProperty("webdriver.gecko.driver", "/selenium-server-standalone-3.10.0/geckodriver.exe");
//System.setProperty("webdriver.gecko.driver", "/opt/selenium-server-standalone-3.10.0/geckodriver");

public class SlackPostMessage {

    @JsonProperty("channel")
    String channel;
    @JsonProperty("username")
    String username;
    @JsonProperty("text")
    String text;
    @JsonProperty("icon_emoji")
    String icon_emoji;
    @JsonProperty("icon_url")
    String icon_url;
    @JsonProperty("attachments")
    List<SlackPostMessage.Attachment> attachments;

    public SlackPostMessage() {
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon_emoji() {
        return this.icon_emoji;
    }

    public void setIcon_emoji(String icon_emoji) {
        this.icon_emoji = icon_emoji;
    }

    public String getIcon_url() {
        return this.icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public List<SlackPostMessage.Attachment> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(List<SlackPostMessage.Attachment> attachments) {
        this.attachments = attachments;
    }

    public static class Attachment {
        @JsonProperty("fallback")
        String fallback;
        @JsonProperty("pretext")
        String pretext;
        @JsonProperty("color")
        String color;
        @JsonProperty("fields")
        List<SlackPostMessage.Attachment.Field> fields;

        public Attachment() {
        }

        public String getFallback() {
            return this.fallback;
        }

        public void setFallback(String fallback) {
            this.fallback = fallback;
        }

        public String getPretext() {
            return this.pretext;
        }

        public void setPretext(String pretext) {
            this.pretext = pretext;
        }

        public String getColor() {
            return this.color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public List<SlackPostMessage.Attachment.Field> getFields() {
            return this.fields;
        }

        public void setFields(List<SlackPostMessage.Attachment.Field> fields) {
            this.fields = fields;
        }

        public static class Field {
            @JsonProperty("title")
            String title;
            @JsonProperty("value")
            String value;
            @JsonProperty("short")
            Boolean isShort;

            public Field() {
            }

            public String getTitle() {
                return this.title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getValue() {
                return this.value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public Boolean getIsShort() {
                return this.isShort;
            }

            public void setIsShort(Boolean isShort) {
                this.isShort = isShort;
            }
        }
    }
}

String qunarUrl = "https://www.qunar.com/";

String phoneNumber = "13027915477";
String host = "zimbra.skywidetech.com";
String user = "sms13027915477@skywidetech.com";
String emailPassword = "pESMaUfU";
int startRow = 1;
short urlRow = 24;

//String importFileName = "/tmp/crawler/unmapped-2018-3-14(Korea).xls";
String filePath = "/tmp/crawler/qunarmapping/";
//String[] importFileList = ['AE.xlsx', 'AU.xlsx', 'DE.xlsx', 'EG.xlsx', 'FR.xlsx', 'ID.xlsx', 'IT.xlsx', 'JP.xlsx', 'KH.xlsx', 'KR.xlsx', 'LK.xlsx', 'MU.xlsx', 'MV.xlsx', 'MY.xlsx', 'NZ.xlsx', 'PH.xlsx', 'RU.xlsx', 'SG.xlsx', 'US.xlsx', 'VN.xlsx'];
String[] importFileList = ['25.xlsx'];

WebDriver driver = new FirefoxDriver();
driver.get(qunarUrl);
String loginUrl = driver.findElement(By.id("__headerInfo_login__")).getAttribute("href");
System.out.println("loginUrl = " + loginUrl);
driver.get(loginUrl);
WebElement loginByScanDiv = driver.findElement(By.cssSelector(".qrlayer"));
System.out.println("style=" + loginByScanDiv.getAttribute("style"));
if (loginByScanDiv.getAttribute("style") != null && !loginByScanDiv.getAttribute("style").equals("") && !loginByScanDiv.getAttribute("style").contains("none")) {
    WebElement changeToPasswordLogin = driver.findElement(By.cssSelector(".port-toggler"));
    changeToPasswordLogin.click();
}
WebElement loginBySMSRadio = driver.findElement(By.cssSelector("#radio_mobile"));
loginBySMSRadio.click();
driver.findElement(By.cssSelector("div.field-login:nth-child(10) > div:nth-child(1) > input:nth-child(2)")).sendKeys(phoneNumber);
WebElement sendSMS = driver.findElement(By.cssSelector("#getcode"));
sendSMS.click();
System.out.println("sleep...");
Thread.sleep(30000);
System.out.println("awake...");

Properties properties = System.getProperties();
javax.mail.Session mailSession = javax.mail.Session.getDefaultInstance(properties);
Store store = mailSession.getStore("imaps");
store.connect(host, user, emailPassword);
Folder inbox = store.getFolder("Inbox");
inbox.open(Folder.READ_ONLY);
Message[] messages = inbox.getMessages();
if (messages.length == 0) System.out.println("No messages found.");
String code = "";
for (int i = 0; i < messages.length; i++) {
    if (messages[i].getContent() instanceof javax.mail.internet.MimeMultipart) {
        javax.mail.internet.MimeMultipart content = messages[i].getContent();
        String message = content.getBodyPart(0).getContent();
        if (message.contains("去哪儿网")) {
            Pattern pattern = Pattern.compile("(\\d+)");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                code = matcher.group(0);
            }
        }

    } else {
        String content = messages[i].getContent();
//        System.out.println("content=" + content);
    }
}
//System.out.println("code=" + code);
driver.findElement(By.cssSelector("#loginForm > div:nth-child(10) > div.field-phone.clearfix > div > input")).sendKeys(code);
WebElement submitBtn = driver.findElement(By.cssSelector("#submit"));
submitBtn.click();

int count = 0;

for (String importFileName : importFileList) {

//    System.out.println(filePath + importFileName + " reading...");
//    File file = new File("/tmp/crawler/qunarmapping/AE.xlsx");
    File file = new File(filePath + importFileName);
    FileInputStream fis = new FileInputStream(file);
    XSSFWorkbook wb = new XSSFWorkbook(fis);
//    Workbook wb = WorkbookFactory.create(file);
//    System.out.println(filePath + importFileName + " read OK");
    Sheet sheet = wb.getSheetAt(0);
    long start = System.currentTimeMillis();
    Row headerRow = sheet.getRow(0);
//    System.out.println("FirstCellNum=" + headerRow.getFirstCellNum());
    for (short j = headerRow.getFirstCellNum(); j < headerRow.getLastCellNum(); j++) {
        Cell header = headerRow.getCell(j);
        if (header != null && header.getStringCellValue() != null && header.getStringCellValue().contains("qunar link")) {
            urlRow = j;
        }
    }

//    System.out.println("last row num =" + sheet.getLastRowNum());
    String url = "";
    try {
        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
            try {
//                System.out.println("--------------------");
                Row row = sheet.getRow(i);
                Cell ttiCodeCell = row.getCell(0);
                Cell urlCell = row.getCell(urlRow);
                String ttiCode = ttiCodeCell.getStringCellValue();
//                System.out.println("ttiCode=" + ttiCode);

                if (urlCell != null) {
                    url = urlCell.getStringCellValue();
//                    System.out.println("url=" + url);

                    if (url != null && url.contains("hotel.qunar.com")) {
                        PropertyBasicInfo propertyBasicInfo = session.createCriteria(PropertyBasicInfo.class)
                                .add(Restrictions.eq("TTIcode", ttiCode))
                                .uniqueResult();
//                        System.out.println("i = " + i);
                        count++;
//                        System.out.println("hasUrl count =" + count);

                        if ("none".equals(propertyBasicInfo.getQunarHotelCode())) {
                            PropertyBasicInfoUrl propertyBasicInfoUrl = null;
                            propertyBasicInfoUrl = session.createCriteria(PropertyBasicInfoUrl.class)
                                    .add(Restrictions.eq("TTIcode", ttiCode))
                                    .add(Restrictions.eq("urlSourceId", "qunar.com"))
                                    .uniqueResult();

                            if (propertyBasicInfoUrl == null) {
                                propertyBasicInfoUrl = new PropertyBasicInfoUrl();

                                propertyBasicInfoUrl.setTTIcode(ttiCode);
                                propertyBasicInfoUrl.setUrlSourceId("qunar.com");
                                propertyBasicInfoUrl.setUrl(url);
                                session.beginTransaction();
                                session.saveOrUpdate(propertyBasicInfoUrl);

                                // use URL to crawl hotelName and hotelAddress;
                                String hotelName = "";
                                String hotelAddress = "";
                                driver.get(url);
                                Thread.sleep(5000);
                                int maxErrorLimit = 0;
                                while (true) {
                                    try {
                                        if (maxErrorLimit > 30) {
                                            break;
                                        }
                                        WebElement hotelNameElement = driver.findElement(By.cssSelector(".htl-longname > span:nth-child(1)"));
                                        String qunarHotelName = hotelNameElement.getText();
//                                        System.out.println("qunarHotelName=" + qunarHotelName);
                                        //qunar HotelName 有很多格式，其中比较普遍的是两种格式，一种是中文+（英文），一种是直接中文 + 英文，而且这两种格式可能还混有其他符号，如“-”
                                        Pattern pattern1 = Pattern.compile("\\(.+?\\)");
                                        Pattern pattern2 = Pattern.compile("([^\\u4e00-\\u9fa5\\(\\)])+");
                                        Matcher matcher = null;
                                        if (qunarHotelName.contains("(") && qunarHotelName.contains(")")) {
                                            matcher = pattern1.matcher(qunarHotelName);
                                            if (matcher.find()) {
                                                String temp = matcher.group(0);
                                                hotelName = temp.substring(1, temp.length() - 1);
                                            }
                                        } else {
                                            matcher = pattern2.matcher(qunarHotelName);
                                            if (matcher.find()) {
                                                hotelName = matcher.group(0);
                                            }
                                        }
                                        WebElement hotelAddressElement = driver.findElement(By.cssSelector(".adress > span:nth-child(1)"));
                                        hotelAddress = hotelAddressElement.getAttribute("title");
                                        break;
                                    } catch (Exception e1) {
                                        System.out.println(e1.getMessage());
                                        maxErrorLimit++;
                                        Thread.sleep(2000);
                                        continue;
                                    }
                                }

//                                System.out.println("name=" + hotelName);
//                                System.out.println("hotelAddress=" + hotelAddress);
                                if (!hotelName.equals("") && !hotelAddress.equals("")) {
                                    propertyBasicInfo.setQunarHotelName(hotelName);
                                    propertyBasicInfo.setQunarAddress(hotelAddress);
                                    session.saveOrUpdate(propertyBasicInfo);
                                    session.getTransaction().commit();
                                }
                            }
                        }
                    } else {
                        PropertyBasicInfo propertyBasicInfo = null;
                        propertyBasicInfo = session.createCriteria(PropertyBasicInfo.class)
                                .add(Restrictions.eq("TTIcode", ttiCode))
                                .uniqueResult();
                        if (propertyBasicInfo != null) {
                            propertyBasicInfo.setQunarMappingStatus(-2);
                            session.beginTransaction();
                            session.saveOrUpdate(propertyBasicInfo);
                            session.getTransaction().commit();
                        }
//                        System.out.println("update QunarMappingStatus");
                    }
                } else {
                    PropertyBasicInfo propertyBasicInfo = null;
                    propertyBasicInfo = session.createCriteria(PropertyBasicInfo.class)
                            .add(Restrictions.eq("TTIcode", ttiCode))
                            .uniqueResult();
                    if (propertyBasicInfo != null) {
                        propertyBasicInfo.setQunarMappingStatus(-2);
                        session.beginTransaction();
                        session.saveOrUpdate(propertyBasicInfo);
                        session.getTransaction().commit();
                    }
//                    System.out.println("update QunarMappingStatus");
                }
//                System.out.println("--------------------");
            } catch (Exception e2) {
                System.out.println(e2.getMessage());
                continue;
            }
        }
    } catch (Exception e3) {
        System.out.println(e3.getMessage());
    }
    long end = System.currentTimeMillis();
    try {
        String webhookURL = "https://hooks.slack.com/services/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";
        String slackChannel = "#crawlers";
        String slackUserName = "webhookbot";

        org.apache.commons.httpclient.methods.PostMethod httpPost = new org.apache.commons.httpclient.methods.PostMethod(webhookURL);
        SlackPostMessage postMessage = new SlackPostMessage();
        postMessage.setChannel(slackChannel);
        postMessage.setUsername(slackUserName);

        String text = importFileName + " use time = " + (end - start) + " Milliseconds";
        postMessage.setText(text);
        postMessage.setIcon_emoji(":ghost:");

        ObjectMapper mapper = new ObjectMapper();
        StringRequestEntity requestEntity = new StringRequestEntity(mapper.writeValueAsString(postMessage));
        httpPost.setRequestEntity(requestEntity);
        org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
        client.executeMethod(httpPost);

    } catch (Exception e) {
        e.printStackTrace(System.out);
    }
}
System.out.println("total hasUrl count=" + count);
System.exit(0);