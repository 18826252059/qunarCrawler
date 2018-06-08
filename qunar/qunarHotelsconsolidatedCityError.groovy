
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
import com.skywidetech.gallop.thirdparty.tti.PropertyAmenity
import com.skywidetech.gallop.thirdparty.tti.PropertyCrossReference
import com.skywidetech.gallop.thirdparty.tti.PropertyAddress
import com.skywidetech.gallop.thirdparty.tti.PropertyEmail
import com.skywidetech.gallop.thirdparty.tti.PropertyPhone
import com.skywidetech.gallop.thirdparty.tti.PropertyPosition
import com.skywidetech.gallop.thirdparty.tti.PropertyGooglePlace
import com.skywidetech.gallop.thirdparty.tti.PropertyName
import com.skywidetech.gallop.thirdparty.tti.PropertyURL
import com.skywidetech.gallop.thirdparty.google.maps.GooglePlace
import com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfo
import com.skywidetech.gallop.thirdparty.tti.PropertyPhone
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
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
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
//        @Grab(group = 'sabre-iur', module = 'sabre-iur', version = '20131012'), // internal
//        @Grab(group = 'galileo-mir', module = 'galileo-mir', version = '20131012'), // internal
//        @Grab(group = 'amadeus-air', module = 'amadeus-air', version = '20131012'), // internal
//        @Grab(group = 'com.microsoft.sqlserver', module = 'sqljdbc4', version = '4.0'), // internal
//		@Grab(group = 'gallop', module = 'gallop', version = '20160822'), // internal
//		@Grab(group = 'giata-api', module = 'giata-api', version = '20160412'), // internal
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
println "hibernate initialized";



Workbook wb = new org.apache.poi.hssf.usermodel.HSSFWorkbook();

String filename = "/tmp/qunar_hotelsconsolidated_city_error-" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".xls";

FileOutputStream fileOut = new FileOutputStream(filename);

org.apache.poi.ss.usermodel.Sheet sheet1 = wb.createSheet("Hotelsconsolidated");
java.sql.Connection connection = session.connection();
int rowNumber = 0;
org.apache.poi.ss.usermodel.Row row1 = sheet1.createRow(rowNumber);
row1.createCell(0).setCellValue("TTIcode");
row1.createCell(1).setCellValue("hotelName");
row1.createCell(2).setCellValue("countryName");
row1.createCell(3).setCellValue("cityName");
row1.createCell(4).setCellValue("addressLine1");
row1.createCell(5).setCellValue("addressLine2");
row1.createCell(6).setCellValue("qunarHotelName");
row1.createCell(7).setCellValue("qunarHotelNameZH");
row1.createCell(8).setCellValue("qunarAddress");
row1.createCell(9).setCellValue("qunarHotelCode");
row1.createCell(10).setCellValue("qunarCityId");
row1.createCell(11).setCellValue("qunarCityCode");
row1.createCell(12).setCellValue("giataCityId");
rowNumber++;


org.apache.poi.ss.usermodel.Sheet nofoundsheet = wb.createSheet("hotelNotFoundInDatabase");
int rowNumber2 = 0;
org.apache.poi.ss.usermodel.Row nofoundRow = nofoundsheet.createRow(rowNumber2);
nofoundRow.createCell(0).setCellValue("hotelName");
rowNumber2++;

org.apache.poi.ss.usermodel.Sheet multiSheet = wb.createSheet("hotelOverOneInDatabase");
int rowNumber3 = 0;
org.apache.poi.ss.usermodel.Row multiRow = multiSheet.createRow(rowNumber3);
multiRow.createCell(0).setCellValue("TTIcode");
multiRow.createCell(1).setCellValue("hotelName");
multiRow.createCell(2).setCellValue("countryName");
multiRow.createCell(3).setCellValue("cityName");
multiRow.createCell(4).setCellValue("addressLine1");
multiRow.createCell(5).setCellValue("addressLine2");
multiRow.createCell(6).setCellValue("qunarHotelName");
multiRow.createCell(7).setCellValue("qunarHotelNameZH");
multiRow.createCell(8).setCellValue("qunarAddress");
multiRow.createCell(9).setCellValue("qunarHotelCode");
multiRow.createCell(10).setCellValue("qunarCityId");
multiRow.createCell(11).setCellValue("qunarCityCode");
multiRow.createCell(12).setCellValue("giataCityId");
rowNumber3++;



session.beginTransaction();
org.apache.poi.xssf.usermodel.XSSFWorkbook wb1 = new org.apache.poi.xssf.usermodel.XSSFWorkbook(new FileInputStream("/temp/qunar_city_incorrect.xlsx"));
XSSFSheet sheet = wb1.getSheetAt(0);


for (int rownum=0; rownum <= sheet.getLastRowNum(); rownum++) {
	XSSFRow row = sheet.getRow(rownum);
	if (row.getCell(0) != null) {

		String HotelName = row.getCell(0).getStringCellValue().trim();



		/*com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfo propertyBasicInfo = session.createCriteria(com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfo.class)
				.add(Restrictions.eq("hotelName", HotelName))
				.uniqueResult();*/

		List<PropertyBasicInfo> propertyBasicInfoList =session.createCriteria(PropertyBasicInfo.class)
				.add(Restrictions.eq("hotelName", HotelName)).list();

//		String sql = "SELECT TTIcode,hotelName,countryName,cityName,addressLine1,addressLine2,qunarHotelName,qunarHotelNameZH,qunarAddress,qunarHotelCode,qunarCityId,qunarCityCode FROM giata.tti_property_basic_info WHERE hotelName LIKE \"%"+HotelName+"%\"";
//		java.sql.ResultSet rs = connection.prepareStatement(sql).executeQuery();

//		System.out.println("propertyBasicInfo tticode="+propertyBasicInfo.getTTIcode());
//		System.out.println("propertyBasicInfo cityId ="+propertyBasicInfo.getQunarCityId());
/*		if (propertyBasicInfoList == null || propertyBasicInfoList.size()==0){
			org.apache.poi.ss.usermodel.Row row2 = nofoundsheet.createRow(rowNumber2);
			row2.createCell(0).setCellValue(HotelName);
			rowNumber2++;
		}*/

//		if (propertyBasicInfoList != null && propertyBasicInfoList.size()>1){
//			for (com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfo propertyBasicInfo : propertyBasicInfoList){
//
//				String TTIcode = propertyBasicInfo.getTTIcode();
//				String hotelName = propertyBasicInfo.getHotelName();
//				String countryName = propertyBasicInfo.getCountryName();
//				String cityName = propertyBasicInfo.getCityName();
//				String addressLine1 = propertyBasicInfo.getAddressLine1();
//				String addressLine2 = propertyBasicInfo.getAddressLine2();
//				String qunarHotelName = propertyBasicInfo.getQunarHotelName();
//				String qunarHotelNameZH = propertyBasicInfo.getQunarHotelNameZH();
//				String qunarAddress = propertyBasicInfo.getQunarAddress();
//				String qunarHotelCode = propertyBasicInfo.getQunarHotelCode();
//				String qunarCityId = propertyBasicInfo.getQunarCityId();
//				String qunarCityCode = propertyBasicInfo.getQunarCityCode();
//				String giataCityId = propertyBasicInfo.getQunarCityCode();
//
//				org.apache.poi.ss.usermodel.Row row2 = multiSheet.createRow(rowNumber3);
//				row2.createCell(0).setCellValue(TTIcode);
//				row2.createCell(1).setCellValue(hotelName);
//				row2.createCell(2).setCellValue(countryName);
//				row2.createCell(3).setCellValue(cityName);
//				row2.createCell(4).setCellValue(addressLine1);
//				row2.createCell(5).setCellValue(addressLine2);
//				row2.createCell(6).setCellValue(qunarHotelName);
//				row2.createCell(7).setCellValue(qunarHotelNameZH);
//				row2.createCell(8).setCellValue(qunarAddress);
//				row2.createCell(9).setCellValue(qunarHotelCode);
//				row2.createCell(10).setCellValue(qunarCityId);
//				row2.createCell(11).setCellValue(qunarCityCode);
//				row2.createCell(12).setCellValue(giataCityId);
//				rowNumber3++;
//			}
//		}


		if( propertyBasicInfoList.size()==1 ){
			PropertyBasicInfo propertyBasicInfo = propertyBasicInfoList.get(0);
			/*String TTIcode = propertyBasicInfo.getTTIcode();
			System.out.println("TTIcode3="+propertyBasicInfo.getTTIcode());
			System.out.println("HotelName3="+HotelName);
			String hotelName = propertyBasicInfo.getHotelName();
			String countryName = propertyBasicInfo.getCountryName();
			String cityName = propertyBasicInfo.getCityName();
			String addressLine1 = propertyBasicInfo.getAddressLine1();
			String addressLine2 = propertyBasicInfo.getAddressLine2();
			String qunarHotelName = propertyBasicInfo.getQunarHotelName();
			String qunarHotelNameZH = propertyBasicInfo.getQunarHotelNameZH();
			String qunarAddress = propertyBasicInfo.getQunarAddress();
			String qunarHotelCode = propertyBasicInfo.getQunarHotelCode();
			String qunarCityId = propertyBasicInfo.getQunarCityId();
			String qunarCityCode = propertyBasicInfo.getQunarCityCode();
			String giataCityId = propertyBasicInfo.getQunarCityCode();



			org.apache.poi.ss.usermodel.Row row2 = sheet1.createRow(rowNumber);
			row2.createCell(0).setCellValue(TTIcode);
			row2.createCell(1).setCellValue(hotelName);
			row2.createCell(2).setCellValue(countryName);
			row2.createCell(3).setCellValue(cityName);
			row2.createCell(4).setCellValue(addressLine1);
			row2.createCell(5).setCellValue(addressLine2);
			row2.createCell(6).setCellValue(qunarHotelName);
			row2.createCell(7).setCellValue(qunarHotelNameZH);
			row2.createCell(8).setCellValue(qunarAddress);
			row2.createCell(9).setCellValue(qunarHotelCode);
			row2.createCell(10).setCellValue(qunarCityId);
			row2.createCell(11).setCellValue(qunarCityCode);
			row2.createCell(12).setCellValue(giataCityId);
			rowNumber++;*/

//			propertyBasicInfo.setQunarMapped(false);
//			propertyBasicInfo.setLastUpdatedDate(new Timestamp(System.currentTimeMillis()));
//			session.update(propertyBasicInfo);
			System.out.println(propertyBasicInfo.getTTIcode() + ";" + propertyBasicInfo.getQunarCityCode() + ";" + propertyBasicInfo.getCountryCode() + ";" + propertyBasicInfo.getHotelName());
		}
	}
}

//row.createCell(3).setCellValue("addressLine2");
//row.createCell(4).setCellValue("cityName");
//row.createCell(5).setCellValue("countryName");

wb.write(fileOut);
fileOut.close();
session.getTransaction().commit();
System.out.println("done!");
