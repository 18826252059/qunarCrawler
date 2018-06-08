#!/usr/bin/env groovy

import com.giatamedia.tticodes.AddressType
import com.giatamedia.tticodes.ChainType
import com.giatamedia.tticodes.Chains
import com.giatamedia.tticodes.Channel
import com.giatamedia.tticodes.Channels
import com.giatamedia.tticodes.Country
import com.giatamedia.tticodes.CrossReferenceType
import com.giatamedia.tticodes.Geography
import com.giatamedia.tticodes.PhoneType
import com.giatamedia.tticodes.TTI_Property
import com.giatamedia.tticodes.TTI_PropertyCodes
import com.giatamedia.tticodes.service.ChainService
import com.skywidetech.gallop.thirdparty.tti.Chain
import com.skywidetech.gallop.thirdparty.tti.City
import com.skywidetech.gallop.thirdparty.tti.CityCrossReference
import com.skywidetech.gallop.thirdparty.tti.CityName
import com.skywidetech.gallop.thirdparty.tti.Locale
import com.skywidetech.gallop.thirdparty.tti.PropertyAddress
import com.skywidetech.gallop.thirdparty.tti.PropertyAmenity
import com.skywidetech.gallop.thirdparty.tti.PropertyCrossReference
import com.skywidetech.gallop.thirdparty.tti.PropertyAddress
import com.skywidetech.gallop.thirdparty.tti.PropertyEmail
import com.skywidetech.gallop.thirdparty.tti.PropertyName
import com.skywidetech.gallop.thirdparty.tti.PropertyPhone
import com.skywidetech.gallop.thirdparty.tti.PropertyGooglePlace
import com.skywidetech.gallop.thirdparty.tti.PropertyPosition
import com.skywidetech.gallop.thirdparty.tti.PropertyURL
import com.skywidetech.gallop.thirdparty.google.maps.GooglePlace
import com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfo
import com.skywidetech.gallop.thirdparty.tti.PropertyCrossReference
import com.skywidetech.gallop.thirdparty.tti.PropertyPhone
import com.skywidetech.gallop.thirdparty.tti.PropertyURL
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.NameValuePair
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.lang3.StringUtils
import org.apache.commons.mail.EmailAttachment
import org.apache.commons.mail.MultiPartEmail
import org.apache.commons.validator.GenericValidator

//import com.Ostermiller.util.ExcelCSVParser
import org.apache.cxf.jaxrs.client.WebClient
import org.apache.poi.ss.usermodel.Workbook
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.criterion.Restrictions

import javax.mail.internet.InternetAddress
import javax.xml.bind.JAXBContext
import javax.xml.bind.Unmarshaller

//import org.hibernate.Hibernate
//import org.hibernate.SessionFactory
//import org.hibernate.StatelessSession
//import org.hibernate.cfg.Configuration
//import org.hibernate.criterion.Projections
//import org.hibernate.criterion.Restrictions

import java.sql.Clob
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.text.SimpleDateFormat;

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
		@Grab(group = 'commons-email', module = 'commons-email', version = '1.1'),
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
		@Grab(group = 'org.slf4j', module = 'slf4j-nop', version = '1.7.5'),
//        @Grab(group = 'sabre-iur', module = 'sabre-iur', version = '20131012'), // internal
//        @Grab(group = 'galileo-mir', module = 'galileo-mir', version = '20131012'), // internal
//        @Grab(group = 'amadeus-air', module = 'amadeus-air', version = '20131012'), // internal
//        @Grab(group = 'com.microsoft.sqlserver', module = 'sqljdbc4', version = '4.0'), // internal
		@Grab(group = 'gallop', module = 'gallop', version = '20160822'), // internal
		@Grab(group = 'giata-api', module = 'giata-api', version = '20160412'), // internal
])

def mysqlProps = [
		"hibernate.dialect": "org.hibernate.dialect.MySQL5InnoDBDialect",
		"hibernate.connection.driver_class": "com.mysql.jdbc.Driver",
		"hibernate.connection.url": "jdbc:mysql://192.168.10.105:3306/gallop?useUnicode=true&amp;characterEncoding=utf-8",
		"hibernate.connection.username": "chengcsw",
		"hibernate.connection.password": "ccheng",
		"hibernate.connection.pool_size": "1",
		"hibernate.connection.autocommit": "true",
		"hibernate.connection.useUnicode": "true",
		"hibernate.connection.characterEncoding": "utf-8",
		"hibernate.cache.provider_class": "org.hibernate.cache.EhCacheProvider",
//    "hibernate.show_sql": "true",
		"hibernate.transaction.factory_class": "org.hibernate.transaction.JDBCTransactionFactory",
//    "hibernate.current_session_context_class": "thread"
]

def configureHibernate(mysqlProps) {
	def Configuration config = new Configuration()
	mysqlProps.each { k, v -> config.setProperty(k, v) }
	config.addAnnotatedClass(Chain.class);
	config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.Channel.class);
	config.addAnnotatedClass(City.class);
	config.addAnnotatedClass(CityCrossReference.class);
	config.addAnnotatedClass(CityName.class);
	config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.Country.class);
	config.addAnnotatedClass(Locale.class);
	config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.giata.Property.class);
	config.addAnnotatedClass(PropertyAddress.class);
	config.addAnnotatedClass(PropertyAmenity.class);
	config.addAnnotatedClass(PropertyBasicInfo.class);
	config.addAnnotatedClass(PropertyCrossReference.class);
	config.addAnnotatedClass(PropertyEmail.class);
	config.addAnnotatedClass(PropertyName.class);
	config.addAnnotatedClass(PropertyPhone.class);
	config.addAnnotatedClass(PropertyPosition.class);
	config.addAnnotatedClass(PropertyURL.class);
	config.addAnnotatedClass(PropertyGooglePlace.class);
	config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.hanatour.Product);
	config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.google.maps.GooglePlace.class);
	config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.google.maps.GooglePlaceAlt.class);
	config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.google.maps.GooglePlacePhoto.class);
	config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.google.maps.GooglePlaceType.class);
	return config;
}

// Open hibernate session
SessionFactory factory = this.configureHibernate(mysqlProps).buildSessionFactory();
org.hibernate.classic.Session session = factory.openSession();
//println "hibernate initialized";

Workbook wb = new org.apache.poi.hssf.usermodel.HSSFWorkbook();

String filename = "/tmp/qunar_missing_hotels-" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".xls";

FileOutputStream fileOut = new FileOutputStream(filename);

org.apache.poi.ss.usermodel.Sheet sheet1 = wb.createSheet("QUNAR");
java.sql.Connection connection = session.connection();
String sql = "SELECT TTIcode,hotelName,addressLine1,addressLine2,cityName,countryName,telephoneNumber FROM giata.tti_property_basic_info WHERE qunarMappingStatus < 0 and qunarMappingStatus > -3";
java.sql.ResultSet rs = connection.prepareStatement(sql).executeQuery();
int rowNumber = 0;
org.apache.poi.ss.usermodel.Row row = sheet1.createRow(rowNumber);
row.createCell(0).setCellValue("TTIcode");
row.createCell(1).setCellValue("hotelName");
row.createCell(2).setCellValue("address");
//row.createCell(3).setCellValue("addressLine2");
//row.createCell(4).setCellValue("cityName");
//row.createCell(5).setCellValue("countryName");
row.createCell(3).setCellValue("telephoneNumber");
rowNumber++;
while (rs.next()) {
	String TTIcode = rs.getString(1);
	String hotelName = rs.getString(2);
	String addressLine1 = rs.getString(3);
//	String addressLine2 = rs.getString(4);
	String cityName = rs.getString(5);
	String countryName = rs.getString(6);
	String telephoneNumber = rs.getString(7);

	String address = addressLine1;
	address += ", " + cityName + ", " + countryName;

	org.apache.poi.ss.usermodel.Row row2 = sheet1.createRow(rowNumber);
	row2.createCell(0).setCellValue(TTIcode);
	row2.createCell(1).setCellValue(hotelName);
	row2.createCell(2).setCellValue(address);
//	row2.createCell(3).setCellValue(addressLine2);
//	row2.createCell(4).setCellValue(cityName);
//	row2.createCell(5).setCellValue(countryName);
	row2.createCell(3).setCellValue(telephoneNumber);
	rowNumber++;
}

org.apache.poi.ss.usermodel.Sheet sheet2 = wb.createSheet("QUNAR_BUG");
sql = "SELECT TTIcode,hotelName,addressLine1,addressLine2,cityName,countryName,telephoneNumber,qunarHotelCode,qunarHotelName,qunarAddress,qunarTelephoneNumber,qunarCityCode FROM giata.tti_property_basic_info WHERE qunarMappingStatus = -4";
rs = connection.prepareStatement(sql).executeQuery();
rowNumber = 0;
row = sheet2.createRow(rowNumber);
row.createCell(0).setCellValue("TTIcode");
row.createCell(1).setCellValue("hotelName");
row.createCell(2).setCellValue("address");
row.createCell(3).setCellValue("telephoneNumber");
row.createCell(4).setCellValue("qunarHotelCode");
row.createCell(5).setCellValue("qunarHotelName");
row.createCell(6).setCellValue("qunarAddress");
row.createCell(7).setCellValue("qunarTelephoneNumber");
row.createCell(8).setCellValue("qunarCityCode");
rowNumber++;
while (rs.next()) {
	String TTIcode = rs.getString(1);
	String hotelName = rs.getString(2);
	String addressLine1 = rs.getString(3);
    String addressLine2 = rs.getString(4);
    String cityName = rs.getString(5);
    String countryName = rs.getString(6);
    String telephoneNumber = rs.getString(7);
    String qunarHotelCode = rs.getString(8);
	String qunarHotelName = rs.getString(9);
	String qunarAddress = rs.getString(10);
	String qunarTelephoneNumber = rs.getString(11);
	String qunarCityCode = rs.getString(12);

	String address = addressLine1;
	if (!GenericValidator.isBlankOrNull(addressLine2)) {
		address += ", " + addressLine2;
	}
//	address += ", " + cityName + ", " + countryName;

	org.apache.poi.ss.usermodel.Row row2 = sheet2.createRow(rowNumber);
	row2.createCell(0).setCellValue(TTIcode);
	row2.createCell(1).setCellValue(hotelName);
	row2.createCell(2).setCellValue(address);
    row2.createCell(3).setCellValue(telephoneNumber);
    row2.createCell(4).setCellValue(qunarHotelCode);
	row2.createCell(5).setCellValue(qunarHotelName);
    row2.createCell(6).setCellValue(qunarAddress);
    row2.createCell(7).setCellValue(qunarTelephoneNumber);
    row2.createCell(8).setCellValue(qunarCityCode);
	rowNumber++;
}


wb.write(fileOut);


fileOut.close();

// Create the attachment
EmailAttachment attachment = new EmailAttachment();
attachment.setPath(filename);


attachment.setDisposition(EmailAttachment.ATTACHMENT);
attachment.setDescription("qunar_missing_hotels.xls");
attachment.setName("qunar_missing_hotels.xls");

// Create the email message
MultiPartEmail email = new MultiPartEmail();
email.setHostName("192.168.10.105");

email.addTo("celine.cheung@skywidetech.com", "Celine Cheung");
email.addCc("yoyoj.zhang@qunar.com", "Yoyo Zhang");
email.addCc("melody@skywidetech.com", "Melody Lin")
email.setFrom("celine.cheung@skywidetech.com", "Celine Cheung");
email.setSubject("QUNAR找不到的酒店");
email.setMsg("亲爱的晶晶，\n这是在你们QUNAR找不到的酒店，请麻烦你们检查，并添加到QUNAR网站上面。");

// add the attachment
email.attach(attachment);

// send the email
email.send();
