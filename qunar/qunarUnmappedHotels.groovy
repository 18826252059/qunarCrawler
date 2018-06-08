#!/usr/bin/env groovy

import org.apache.commons.lang.time.DateFormatUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.mail.EmailAttachment
import org.apache.commons.mail.HtmlEmail
import org.apache.commons.validator.GenericValidator
import org.apache.poi.ss.usermodel.Workbook
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.htmlcleaner.CleanerProperties
import org.htmlcleaner.DomSerializer
import org.htmlcleaner.HtmlCleaner
import org.htmlcleaner.TagNode

import javax.mail.Folder
import javax.mail.Message
import javax.mail.Store
import javax.mail.internet.MimeMultipart
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import java.sql.Date
import java.sql.PreparedStatement

@Grapes([
        @Grab(group = 'dom4j', module = 'dom4j', version = '1.6.1'),
        @Grab(group = 'org.hibernate', module = 'hibernate-annotations', version = '3.4.0.GA'),
        @Grab(group = 'org.hibernate', module = 'hibernate-ehcache', version = '3.6.10.Final'),
        @Grab(group = 'org.hibernate', module = 'hibernate-core', version = '3.6.10.Final'),
        @Grab(group = 'hsqldb', module = 'hsqldb', version = '1.8.0.7'),
        @Grab(group = 'javassist', module = 'javassist', version = '3.4.GA'),
        @Grab(group = 'net.sourceforge.htmlcleaner', module = 'htmlcleaner', version = '2.6.1'),
        @Grab(group = 'commons-validator', module = 'commons-validator', version = '1.4.0'),
        @Grab(group = 'commons-httpclient', module = 'commons-httpclient', version = '3.1'),
        @Grab(group = 'commons-collections', module = 'commons-collections', version = '3.2.1'),
        @Grab(group = 'org.apache.commons', module = 'commons-lang3', version = '3.4'),
        @Grab(group = 'commons-math', module = 'commons-math', version = '1.2'),
        @Grab(group = 'struts', module = 'struts', version = '1.2.9'),
        @Grab(group = 'org.apache.poi', module = 'poi', version = '3.9'),
        @Grab(group = 'org.ostermiller', module = 'utils', version = '1.07.00'),
        @Grab(group = 'mysql', module = 'mysql-connector-java', version = '5.1.28'),
        @Grab(group = 'org.mongodb', module = 'mongo-java-driver', version = '3.2.0'),
        @Grab(group = 'org.apache.cxf', module = 'cxf-bundle', version = '2.7.2'),
        @Grab(group = 'org.codehaus.woodstox', module = 'woodstox-core-asl', version = '4.2.1'),
        @Grab(group = 'org.codehaus.woodstox', module = 'woodstox-core-lgpl', version = '4.2.1'),
        @Grab(group = 'org.codehaus.woodstox', module = 'stax2-api', version = '3.1.1'),
        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-core', version = '2.3.3'),
        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-databind', version = '2.3.3'),
        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-databind', version = '2.3.3'),
        @Grab(group = 'org.apache.commons', module = 'commons-email', version = '1.3.1'),
        @Grab(group = 'org.slf4j', module = 'slf4j-nop', version = '1.7.5'),
])

def hibProps = [
        "hibernate.dialect": "org.hibernate.dialect.MySQL5InnoDBDialect",
        "hibernate.connection.driver_class": "com.mysql.jdbc.Driver",
        "hibernate.connection.url": "jdbc:mysql://192.168.10.105:3306/gallop?useUnicode=true&amp;characterEncoding=UTF-8",
        "hibernate.connection.username": "chengcsw",
        "hibernate.connection.password": "ccheng",
        "hibernate.connection.pool_size": "1",
        "hibernate.jdbc.batch_size": "1000",
//        hibernate.jdbc.batch_size 20
//        "hibernate.connection.autocommit": "true",
        "hibernate.connection.useUnicode": "true",
        "hibernate.connection.characterEncoding": "utf-8",
        "hibernate.cache.provider_class": "org.hibernate.cache.EhCacheProvider",
        "hibernate.transaction.factory_class": "org.hibernate.transaction.JDBCTransactionFactory",
]

// mail server connection parameters
String host = "zimbra.skywidetech.com";
String user = "products@roomstays.travel";
String password = "asdf1234";

// Open hibernate session
org.hibernate.cfg.Configuration config = new org.hibernate.cfg.Configuration()
hibProps.each { k, v -> config.setProperty(k, v) }
SessionFactory factory = config.buildSessionFactory();
org.hibernate.StatelessSession session = factory.openStatelessSession();
//println "hibernate initialized";
session.beginTransaction();
java.sql.Connection connection = session.connection();


// connect to my imap inbox
/*
Properties properties = System.getProperties();
//properties.put("mail.smtp.starttls.enable","true");
//properties.put("mail.smtp.auth", "true");  // If you need to authenticate
//// Use the following if you need SSL
//properties.put("mail.smtp.socketFactory.port", 465);
//properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//properties.put("mail.smtp.socketFactory.fallback", "false");
//properties.put("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//properties.put("mail.pop3.socketFactory.fallback", "false");
//properties.put("mail.pop3.host", host );
//properties.put("mail.pop3.user", user);
//properties.put("mail.pop3.password", password);
//properties.put("mail.pop3.ssl.enable", "true");
//properties.put("mail.pop3.port", "445" );
//properties.put("mail.pop3.auth", "true" );
//properties.put("mail.pop3.starttls.enable", "false");

javax.mail.Session mailSession = javax.mail.Session.getDefaultInstance(properties);
Store store = mailSession.getStore("imaps");
store.connect(host, user, password);
Folder inbox = store.getFolder("Inbox");
inbox.open(Folder.READ_ONLY);

// get the list of inbox messages
Integer hotelCount = 0;
Message[] messages = inbox.getMessages();
if (messages.length == 0) System.out.println("No messages found.");
for (int i = 0; i < messages.length; i++) {
//    System.out.println("Message " + (i + 1));
//    System.out.println("From : " + messages[i].getFrom()[0]);
//    System.out.println("Subject : " + messages[i].getSubject());
//    System.out.println("Sent Date : " + messages[i].getSentDate());
    // MimeMultipart multipart = messages[i].getContent();
    String subject = messages[i].getSubject();
    if (subject.startsWith(DateFormatUtils.format(new GregorianCalendar(), "yyyy-MM-dd")) && messages[i].getFrom()[0].toString().contains("zhangping.liang@qunar.com")) {
        List<String> ttiCodes = new ArrayList();
        // for (int j = 0; j < multipart.getCount(); j++) {
            // com.sun.mail.imap.IMAPBodyPart imapBodyPart = multipart.getBodyPart(j);
            // java.io.InputStream is = imapBodyPart.getInputStream();
            HtmlCleaner cleaner = new HtmlCleaner();
            // TagNode tagNode = cleaner.clean(is);
			TagNode tagNode = cleaner.clean(messages[i].getContent());
            CleanerProperties props = cleaner.getProperties();
            org.w3c.dom.Document document = new DomSerializer(props, true).createDOM(tagNode);
            XPath xpath = XPathFactory.newInstance().newXPath();
            org.w3c.dom.NodeList nodeList = (org.w3c.dom.NodeList) xpath.evaluate("html//body//table", document, XPathConstants.NODESET);
            for (int k = 0; k < nodeList.getLength(); k++) {
                org.w3c.dom.Element tableElement = (org.w3c.dom.Element) nodeList.item(k);
                org.w3c.dom.NodeList trElements = tableElement.getElementsByTagName("tr");
                for (int l = 0; l < trElements.getLength(); l++) {
                    org.w3c.dom.Element trElement = (org.w3c.dom.Element) trElements.item(l);
                    String ttiCode = null;
                    String hotelName = null;
                    org.w3c.dom.NodeList tdElements = trElement.getElementsByTagName("td");
                    for (int m = 0; m < tdElements.getLength(); m++) {
                        org.w3c.dom.Element tdElement = (org.w3c.dom.Element) tdElements.item(m);
                        switch (m) {
                            case 0:
                                ttiCode = tdElement.getTextContent().trim();
                                if (GenericValidator.isInt(ttiCode)) {
                                    ttiCodes.add(ttiCode);
                                }
                                break;
                            case 1:
                                hotelName = tdElement.getTextContent().trim();
                                break;
                        }
                    }
//                System.out.println(ttiCode + "=" + hotelName);
                    hotelCount++;
                }
            }
        // }

//        System.out.println(ttiCodes);
        String sql0 = "update giata.tti_property_basic_info set qunarMapped = 1 where qunarMapped = 0";
        PreparedStatement pstmt1 = connection.prepareStatement(sql0);
        pstmt1.executeUpdate();
        connection.commit();

        String sql = "update giata.tti_property_basic_info set qunarMapped = 0, qunarMappedDate = ? where ttiCode in ( " + StringUtils.join(ttiCodes, ",") + ")";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setDate(1, new java.sql.Date(System.currentTimeMillis()));
//        pstmt.setString(2, StringUtils.join(ttiCodes, ","));
        int updated = pstmt.executeUpdate();
//        System.out.println("updated=" + updated);
        connection.commit();
    }
}

inbox.close(true);
store.close();
*/

// Export the files to Excel
String filename = "/tmp/qunarUnmappedHotelsByCountries.xls";
FileOutputStream fileOut = new FileOutputStream(filename);
Workbook wb = new org.apache.poi.hssf.usermodel.HSSFWorkbook();

// Sheet 1: Unmatched
org.apache.poi.ss.usermodel.Sheet sheet1 = wb.createSheet("Unmatched");
int rowNumber = 0;
PreparedStatement pstmt = connection.prepareStatement("select * from giata.qunar_unmapped_hotels_by_countries");
java.sql.ResultSet rs = pstmt.executeQuery();
while (rs.next()) {
    String countryCode = rs.getString(1);
    String countryName = rs.getString(2);
    Integer count = rs.getInt(3);
    org.apache.poi.ss.usermodel.Row row = sheet1.createRow(rowNumber);
    row.createCell(0).setCellValue(countryCode);
    row.createCell(1).setCellValue(countryName);
    row.createCell(2).setCellValue(count);
    rowNumber++;
}

// Sheet 2: Incomplete
org.apache.poi.ss.usermodel.Sheet sheet2 = wb.createSheet("Incomplete");
rowNumber = 0;
int incompleteCount = 0;
row = sheet2.createRow(rowNumber);
row.createCell(0).setCellValue("TTICode");
row.createCell(1).setCellValue("hotelName");
row.createCell(2).setCellValue("hotelNameZHCN");
row.createCell(3).setCellValue("addressLine1");
row.createCell(4).setCellValue("addressLine2");
row.createCell(5).setCellValue("cityName");
row.createCell(6).setCellValue("countryName");
row.createCell(7).setCellValue("countryCode");
row.createCell(8).setCellValue("telephoneNumber");
row.createCell(9).setCellValue("website");
//row.createCell(10).setCellValue("tripAdvisorLocationName");
//row.createCell(11).setCellValue("tripAdvisorLocationAddress");
row.createCell(10).setCellValue("qunarCityCode");
row.createCell(11).setCellValue("qunarHotelName");
row.createCell(12).setCellValue("qunarHotelNameZH");
row.createCell(13).setCellValue("qunarAddress");
row.createCell(14).setCellValue("qunarHotelCode");
row.createCell(15).setCellValue("qunarTelephoneNumber");
rowNumber++;
pstmt = connection.prepareStatement("select * from giata.qunar_hotels_incomplete_details");
rs = pstmt.executeQuery();
while (rs.next()) {
    String TTICode = rs.getString("TTICode");
    String hotelName = rs.getString("hotelName");
    String hotelNameZHCN = rs.getString("hotelNameZHCN");
    String addressLine1 = rs.getString("addressLine1");
    String addressLine2 = rs.getString("addressLine2");
    String cityName = rs.getString("cityName");
    String countryName = rs.getString("countryName");
    String countryCode = rs.getString("countryCode");
    String telephoneNumber = rs.getString("telephoneNumber");
    String website = rs.getString("website");
//    String tripAdvisorLocationName = rs.getString("tripAdvisorLocationName");
//    String tripAdvisorLocationAddress = rs.getString("tripAdvisorLocationAddress");
    String qunarCityCode = rs.getString("qunarCityCode");
    String qunarHotelName = rs.getString("qunarHotelName");
    String qunarHotelNameZH = rs.getString("qunarHotelNameZH");
    String qunarAddress = rs.getString("qunarAddress");
    String qunarHotelCode = rs.getString("qunarHotelCode");
    String qunarTelephoneNumber = rs.getString("qunarTelephoneNumber");
    row = sheet2.createRow(rowNumber);
    row.createCell(0).setCellValue(TTICode);
    row.createCell(1).setCellValue(hotelName);
    row.createCell(2).setCellValue(hotelNameZHCN);
    row.createCell(3).setCellValue(addressLine1);
    row.createCell(4).setCellValue(addressLine2);
    row.createCell(5).setCellValue(cityName);
    row.createCell(6).setCellValue(countryName);
    row.createCell(7).setCellValue(countryCode);
    row.createCell(8).setCellValue(telephoneNumber);
    row.createCell(9).setCellValue(website);
//    row.createCell(10).setCellValue(tripAdvisorLocationName);
//    row.createCell(11).setCellValue(tripAdvisorLocationAddress);
    row.createCell(10).setCellValue(qunarCityCode);
    row.createCell(11).setCellValue(qunarHotelName);
    row.createCell(12).setCellValue(qunarHotelNameZH);
    row.createCell(13).setCellValue(qunarAddress);
    row.createCell(14).setCellValue(qunarHotelCode);
    row.createCell(15).setCellValue(qunarTelephoneNumber);
    rowNumber++;
    incompleteCount++;
}
System.out.println("incompleteCount=" + incompleteCount);

// Sheet 3: Missing
org.apache.poi.ss.usermodel.Sheet sheet3 = wb.createSheet("Missing");
rowNumber = 0;
int missingCount = 0;
row = sheet3.createRow(rowNumber);
row.createCell(0).setCellValue("TTICode");
row.createCell(1).setCellValue("hotelName");
row.createCell(2).setCellValue("hotelNameZHCN");
row.createCell(3).setCellValue("addressLine1");
row.createCell(4).setCellValue("addressLine2");
row.createCell(5).setCellValue("cityName");
row.createCell(6).setCellValue("countryCode");
row.createCell(7).setCellValue("countryName");
row.createCell(8).setCellValue("telephoneNumber");
row.createCell(9).setCellValue("qunarCityCode");
row.createCell(10).setCellValue("qunarHotelName");
row.createCell(11).setCellValue("qunarHotelNameZH");
row.createCell(12).setCellValue("qunarAddress");
row.createCell(13).setCellValue("qunarHotelCode");
row.createCell(14).setCellValue("qunarTelephoneNumber");
row.createCell(15).setCellValue("qunarMappedDate");
rowNumber++;
String sql3 = "select * from giata.tti_property_basic_info \n" +
        "where status = 1 and closed is null\n" +
        "and countryCode IN ('AE','AU','CA','DE','EG','FR','GB','ID','IT','JP','KH','KR','LK','MU','MV','MY','NZ','PH','RU','SG','TH','US','VN')\n" +
        "and (qunarHotelCode is null or qunarHotelCode = '')";
pstmt = connection.prepareStatement(sql3);
rs = pstmt.executeQuery();
while (rs.next()) {
    String TTICode = rs.getString("TTICode");
    String hotelName = rs.getString("hotelName");
    String hotelNameZHCN = rs.getString("hotelNameZHCN");
    String addressLine1 = rs.getString("addressLine1");
    String addressLine2 = rs.getString("addressLine2");
    String cityName = rs.getString("cityName");
    String countryCode = rs.getString("countryCode");
    String countryName = rs.getString("countryName");
    String telephoneNumber = rs.getString("telephoneNumber");
    String qunarCityCode = rs.getString("qunarCityCode");
    String qunarHotelName = rs.getString("qunarHotelName");
    String qunarHotelNameZH = rs.getString("qunarHotelNameZH");
    String qunarAddress = rs.getString("qunarAddress");
    String qunarHotelCode = rs.getString("qunarHotelCode");
    String qunarTelephoneNumber = rs.getString("qunarTelephoneNumber");
    java.sql.Date qunarMappedDate = rs.getDate("qunarMappedDate");
    row = sheet3.createRow(rowNumber);
    row.createCell(0).setCellValue(TTICode);
    row.createCell(1).setCellValue(hotelName);
    row.createCell(2).setCellValue(hotelNameZHCN);
    row.createCell(3).setCellValue(addressLine1);
    row.createCell(4).setCellValue(addressLine2);
    row.createCell(5).setCellValue(cityName);
    row.createCell(6).setCellValue(countryCode);
    row.createCell(7).setCellValue(countryName);
    row.createCell(8).setCellValue(telephoneNumber);
    row.createCell(9).setCellValue(qunarCityCode);
    row.createCell(10).setCellValue(qunarHotelName);
    row.createCell(11).setCellValue(qunarHotelNameZH);
    row.createCell(12).setCellValue(qunarAddress);
    row.createCell(13).setCellValue(qunarHotelCode);
    row.createCell(14).setCellValue(qunarTelephoneNumber);
    if (qunarMappedDate != null)
        row.createCell(14).setCellValue(org.apache.commons.lang3.time.DateFormatUtils.format(qunarMappedDate, "yyyy-MM-dd"));
    rowNumber++;
    missingCount++;
}
System.out.println("missingCount=" + missingCount);

// Sheet 4: Unmapped
org.apache.poi.ss.usermodel.Sheet sheet4 = wb.createSheet("Unmapped");
rowNumber = 0;
row = sheet4.createRow(rowNumber);
int columnId = 0;
row.createCell(columnId++).setCellValue("TTICode");
row.createCell(columnId++).setCellValue("hotelName");
row.createCell(columnId++).setCellValue("hotelNameZHCN");
row.createCell(columnId++).setCellValue("propertyClass");
row.createCell(columnId++).setCellValue("addressLine1");
row.createCell(columnId++).setCellValue("addressLine2");
row.createCell(columnId++).setCellValue("cityName");
row.createCell(columnId++).setCellValue("countryName");
row.createCell(columnId++).setCellValue("countryCode");
row.createCell(columnId++).setCellValue("telephoneNumber");
row.createCell(columnId++).setCellValue("website");
row.createCell(columnId++).setCellValue("chainName");
row.createCell(columnId++).setCellValue("tripAdvisorLocationId");
row.createCell(columnId++).setCellValue("tripAdvisorLocationName");
row.createCell(columnId++).setCellValue("tripAdvisorLocationAddress");
row.createCell(columnId++).setCellValue("tripAdvisorWebURL");
row.createCell(columnId++).setCellValue("qunarCityCode");
row.createCell(columnId++).setCellValue("qunarHotelName");
row.createCell(columnId++).setCellValue("qunarHotelNameZH");
row.createCell(columnId++).setCellValue("qunarAddress");
row.createCell(columnId++).setCellValue("qunarHotelCode");
row.createCell(columnId++).setCellValue("qunarTelephoneNumber");
row.createCell(columnId++).setCellValue("qunarUrl");
row.createCell(columnId++).setCellValue("qunarStatus");
rowNumber++;
String sql4 = "select tti_property_basic_info.*, tripadvisor_location.*, opentravel.ota_property_class_type.description as propertyClassTypeName, tti_property_basic_info_url.url " +
        "from giata.tti_property_basic_info " +
        "left join (select * from tripadvisor.tripadvisor_location where languageCode = 'en') as tripadvisor_location on tripAdvisorLocationId = locationId  " +
        "left join (select * from giata.tti_property_basic_info_url where urlSourceId = 'qunar.com') as tti_property_basic_info_url on giata.tti_property_basic_info.TTIcode = tti_property_basic_info_url.TTIcode " +
        "left join opentravel.ota_property_class_type on opentravel.ota_property_class_type.propertyClassTypeId = tti_property_basic_info.propertyClassType " +
        "where qunarHotelCode = 'none'";
pstmt = connection.prepareStatement(sql4);
rs = pstmt.executeQuery();
Integer unmappedCount = 0;
while (rs.next()) {
//    missingCount = rs.getInt(1);
    rs = pstmt.executeQuery();
    while (rs.next()) {
        String TTICode = rs.getString("TTICode");
        String hotelName = rs.getString("hotelName");
        String hotelNameZHCN = rs.getString("hotelNameZHCN");
        String propertyClassType = rs.getString("propertyClassTypeName");
        String addressLine1 = rs.getString("addressLine1");
        String addressLine2 = rs.getString("addressLine2");
        String cityName = rs.getString("cityName");
        String countryName = rs.getString("countryName");
        String countryCode = rs.getString("countryCode");
        String telephoneNumber = rs.getString("telephoneNumber");
        String website = rs.getString("website");
        String chainName = rs.getString("chainName");
        String locationId = rs.getString("tripAdvisorLocationId");
        String tripAdvisorLocationName = rs.getString("name");
        String tripAdvisorLocationAddress = rs.getString("addressString");
        String tripAdvisorWebURL = rs.getString("webURL");
        String qunarCityCode = rs.getString("qunarCityCode");
        String qunarHotelName = rs.getString("qunarHotelName");
        String qunarHotelNameZH = rs.getString("qunarHotelNameZH");
        String qunarAddress = rs.getString("qunarAddress");
        String qunarHotelCode = rs.getString("qunarHotelCode");
        String qunarTelephoneNumber = rs.getString("qunarTelephoneNumber");
        String qunarUrl = rs.getString("url");
        Integer qunarMappingStatus = rs.getInt("qunarMappingStatus");
        row = sheet4.createRow(rowNumber);
        columnId = 0;
        row.createCell(columnId++).setCellValue(TTICode);
        row.createCell(columnId++).setCellValue(hotelName);
        row.createCell(columnId++).setCellValue(hotelNameZHCN);
        row.createCell(columnId++).setCellValue(propertyClassType);
        row.createCell(columnId++).setCellValue(addressLine1);
        row.createCell(columnId++).setCellValue(addressLine2);
        row.createCell(columnId++).setCellValue(cityName);
        row.createCell(columnId++).setCellValue(countryName);
        row.createCell(columnId++).setCellValue(countryCode);
        row.createCell(columnId++).setCellValue(telephoneNumber);
        row.createCell(columnId++).setCellValue(website);
        row.createCell(columnId++).setCellValue(chainName);
        row.createCell(columnId++).setCellValue(locationId);
        row.createCell(columnId++).setCellValue(tripAdvisorLocationName);
        row.createCell(columnId++).setCellValue(tripAdvisorLocationAddress);
        row.createCell(columnId++).setCellValue(tripAdvisorWebURL);
        row.createCell(columnId++).setCellValue(qunarCityCode);
        row.createCell(columnId++).setCellValue(qunarHotelName);
        row.createCell(columnId++).setCellValue(qunarHotelNameZH);
        row.createCell(columnId++).setCellValue(qunarAddress);
        row.createCell(columnId++).setCellValue(qunarHotelCode);
        row.createCell(columnId++).setCellValue(qunarTelephoneNumber);
        row.createCell(columnId++).setCellValue(qunarUrl);
        if (qunarMappingStatus != null) {
            switch (qunarMappingStatus) {
                case 1:
                    row.createCell(columnId++).setCellValue("mapped");
                    break;
                case 0:
                    row.createCell(columnId++).setCellValue("not_mapped");
                    break;
                case -1:
                    row.createCell(columnId++).setCellValue("address_unmatched");
                    break;
                case -2:
                    row.createCell(columnId++).setCellValue("hotel_not_found");
                    break;
                case -3:
                    row.createCell(columnId++).setCellValue("closed");
                    break;
                case -4:
                    row.createCell(columnId++).setCellValue("failed");
                    break;
            }

        }
        rowNumber++;
        unmappedCount++;
    }
}
System.out.println("unmappedCount=" + unmappedCount);

wb.write(fileOut);
fileOut.close();

String sql = "select count(*) as totalCount from giata.tti_property_basic_info where countryCode NOT IN ('HK', 'MO', 'CN') and status = 1 and closed is null";
pstmt = connection.prepareStatement(sql);
rs = pstmt.executeQuery();
Integer totalCount = 0;
while (rs.next()) {
    totalCount = rs.getInt(1);
}
System.out.println("totalCount=" + totalCount);

sql = "select count(*) as totalCount from giata.tti_property_basic_info where countryCode IN ('AE','AU','CA','DE','EG','FR','GB','ID','IT','JP','KH','KR','LK','MU','MV','MY','NZ','PH','RU','SG','TH','US','VN') and status = 1 and closed is null and qunarCityCode is not null and addressLine1 is not null";
pstmt = connection.prepareStatement(sql);
rs = pstmt.executeQuery();
Integer selectedCount = 0;
while (rs.next()) {
    selectedCount = rs.getInt(1);
}
System.out.println("selectedCount=" + selectedCount);

EmailAttachment attachment = new EmailAttachment();
attachment.setPath(filename);
attachment.setDisposition(EmailAttachment.ATTACHMENT);
attachment.setName(filename);

HtmlEmail email = new HtmlEmail();
email.setHostName("192.168.10.107");
email.addTo("products@roomstays.travel", "Product Manager");
email.setFrom("products@roomstays.travel", "Product Manager");
email.setSubject("Qunar Unmapped Hotels");
String htmlMsg =
        "Total properties of all countries available to Qunar: " + totalCount + "<br/>" + // OK
                "Properties of AE,AU,CA,DE,EG,FR,GB,ID,IT,JP,KH,KR,LK,MU,MV,MY,NZ,PH,RU,SG,TH,US,VN available to Qunar : " + selectedCount + "<br/><br/>" +
//                "Properties either not found on Qunar or address unmatched with Qunar : " + qunarMappingError + "<br/>" +
//                "Properties reported by Qunar having mapping errors: " + hotelCount + "<br/>" +
//                "Properties that we haven't processed yet after mapping errors: " + unprocessedCount + "<br/>" +
                "Properties with missing address, telephone or Qunar city code: " + incompleteCount + "<br/>" + // OK
//                "Properties with duplicated codes: " + duplicatedCount + "<br/><br/>";
                "Properties missing in hota.qunar.com: " + missingCount + "<br/>" +
                "Properties cannot be mapped in hota.qunar.com: " + unmappedCount;

/*
htmlMsg += "Unmapped by countries:<br/>";
int count = 0;
for (String key : map.keySet()) {
    htmlMsg += key + "=" + map.get(key) + "<br/>";
    count += map.get(key);
}
htmlMsg += "Subotal: " + count;
*/

email.setHtmlMsg(htmlMsg);
email.attach(attachment);
email.send();
