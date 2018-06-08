
import com.skywidetech.gallop.thirdparty.tti.CityCrossReference
import com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfo
import org.apache.commons.validator.GenericValidator
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.criterion.Restrictions

import java.sql.ResultSet
import java.sql.Timestamp

/**
 * Created by Celine Cheung on 2016/8/16.
 */


/*@Grapes([
		@Grab(group = 'org.hibernate', module = 'hibernate-annotations', version = '3.4.0.GA'),
		@Grab(group = 'org.hibernate', module = 'hibernate-ehcache', version = '3.6.10.Final'),
		@Grab(group = 'hsqldb', module = 'hsqldb', version = '1.8.0.7'),
		@Grab(group = 'javassist', module = 'javassist', version = '3.4.GA'),
		@Grab(group = 'net.sourceforge.htmlcleaner', module = 'htmlcleaner', version = '2.6.1'),
		@Grab(group = 'commons-validator', module = 'commons-validator', version = '1.4.0'),
		@Grab(group = 'commons-httpclient', module = 'commons-httpclient', version = '3.1'),
		@Grab(group = 'commons-collections', module = 'commons-collections', version = '3.2.1'),
		@Grab(group = 'commons-math', module = 'commons-math', version = '1.2'),
		@Grab(group = 'org.apache.commons', module = 'commons-lang3', version = '3.2.1'),
		@Grab(group = 'org.mongodb', module = 'mongo-java-driver', version = '3.2.0'),
		@Grab(group = 'struts', module = 'struts', version = '1.2.9'),
		@Grab(group = 'org.apache.poi', module = 'poi', version = '3.9'),
		@Grab(group = 'org.apache.poi', module = 'poi-ooxml', version = '3.9'),//need to run in Linux
		@Grab(group = 'org.ostermiller', module = 'utils', version = '1.07.00'),
		@Grab(group = 'mysql', module = 'mysql-connector-java', version = '5.1.28'),

//        @Grab(group = 'org.apache.cxf', module = 'cxf-bundle', version = '2.7.2'),
//        @Grab(group = 'org.mongodb', module = 'mongo-java-driver', version = '3.2.0'),
//        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-core', version = '2.3.3'),
//        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-databind', version = '2.3.3'),
//        @Grab(group = 'sabre-iur', module = 'sabre-iur', version = '20131012'), // internal
//        @Grab(group = 'galileo-mir', module = 'galileo-mir', version = '20131012'), // internal
//        @Grab(group = 'amadeus-air', module = 'amadeus-air', version = '20131012'), // internal
//        @Grab(group = 'com.microsoft.sqlserver', module = 'sqljdbc4', version = '4.0'), // internal
//		@Grab(group = 'gallop', module = 'gallop', version = '20160714'), // internal
])*/

def hibProps = [
		"hibernate.dialect": "com.skywidetech.gallop.util.hibernate.SQLServerDialect",
		"hibernate.connection.driver_class": "com.mysql.jdbc.Driver",
		"hibernate.connection.url": "jdbc:mysql://192.168.10.168:3306/gallop?useUnicode=true&amp;characterEncoding=UTF-8",
		"hibernate.connection.username": "celinecheung",
		"hibernate.connection.password": "celine",
		"hibernate.connection.pool_size": "1",
		"hibernate.jdbc.batch_size": "1000",
//        hibernate.jdbc.batch_size 20
//        "hibernate.connection.autocommit": "true",
		"hibernate.connection.useUnicode": "true",
		"hibernate.connection.characterEncoding": "utf-8",//need to Import Chinese
		"hibernate.cache.provider_class": "org.hibernate.cache.EhCacheProvider",
		"hibernate.transaction.factory_class": "org.hibernate.transaction.JDBCTransactionFactory",
]
def configureHibernate(props) {
	def Configuration config = new Configuration()
	props.each { k, v -> config.setProperty(k, v) }
	config.addAnnotatedClass(CityCrossReference.class);
	config.addAnnotatedClass(PropertyBasicInfo.class);
}

// Open hibernate session
SessionFactory factory = this.configureHibernate(hibProps).buildSessionFactory();
org.hibernate.StatelessSession session = factory.openStatelessSession();
java.sql.Connection connection = session.connection();
println "hibernate initialized";

//PreparedStatement pstmt = connection.prepareStatement("select count(*) from giata.tti_property_basic_info where TTIcode= ? ");
session.beginTransaction();
org.apache.poi.xssf.usermodel.XSSFWorkbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook(new FileInputStream("/temp/12.xlsx"));
XSSFSheet sheet = wb.getSheetAt(0);
for (int rownum=1; rownum <= sheet.getLastRowNum(); rownum++) {
	XSSFRow row = sheet.getRow(rownum);
	if (row.getCell(0) != null) {

		Integer ttiCode = row.getCell(0).getNumericCellValue();
		String newTTICode = String.valueOf(ttiCode);

		String qunarHotelCode = row.getCell(6).getStringCellValue();

		PropertyBasicInfo propertyBasicInfo = session.createCriteria(PropertyBasicInfo.class)
				.add(Restrictions.eq("TTIcode", newTTICode))
				.uniqueResult();

		/*String cityName = null;
		if (row.getCell(5))
			cityName = row.getCell(5).getStringCellValue();*/

		String countryName = null;
		if (row.getCell(6))
			countryName = row.getCell(6).getStringCellValue();

		String qunarCityCode = null;
		if (row.getCell(8))
			qunarCityCode = row.getCell(8).getStringCellValue();

		System.out.println("countryName="+countryName)
		System.out.println("qunarCityCode="+qunarCityCode)

		propertyBasicInfo.setLastUpdatedDate(new Timestamp(System.currentTimeMillis()));
		propertyBasicInfo.setCountryName(countryName);
		propertyBasicInfo.setQunarCityCode(qunarCityCode);
		session.update(propertyBasicInfo);


		/*if (row.getCell(10)==null || (row.getCell(10)!=null && GenericValidator.isBlankOrNull(row.getCell(10).getStringCellValue()))){

			propertyBasicInfo.setClosed(null);
			session.update(propertyBasicInfo);
			if (row.getCell(7) != null && row.getCell(9) != null){

				String qunarHotelchinesname = null;
				if (row.getCell(8) != null )
					qunarHotelchinesname = row.getCell(8).getStringCellValue();

				String qunarHotelname = row.getCell(7).getStringCellValue();
				String qunarHoteladdress = row.getCell(9).getStringCellValue();
				System.out.println("TTICODE 1 ="+newTTICode);
				System.out.println("qunarHotelCode 1 ="+qunarHotelCode);
				propertyBasicInfo.setQunarHotelName(qunarHotelname);
				propertyBasicInfo.setQunarAddress(qunarHoteladdress);
				propertyBasicInfo.setQunarHotelCode(qunarHotelCode);
				propertyBasicInfo.setQunarHotelNameZH(qunarHotelchinesname);
				propertyBasicInfo.setLastUpdatedDate(new Timestamp(System.currentTimeMillis()));
				session.update(propertyBasicInfo);
			}else{

				if (row.getCell(11) != null){

					String officialURL=null;
					String officialHotelname =null;
					String officialHotelchinesname =null;
					String officialHoteladdress = null;

					System.out.println("TTICODE 2 ="+newTTICode);

					if (row.getCell(11) != null)
						officialURL = row.getCell(11).getStringCellValue();

					if (row.getCell(12) != null)
						officialHotelname = row.getCell(12).getStringCellValue();

					if (row.getCell(13) != null)
						officialHotelchinesname = row.getCell(13).getStringCellValue();

					if (row.getCell(14) != null)
						officialHoteladdress = row.getCell(14).getStringCellValue();

					propertyBasicInfo.setWebsite(officialURL);
					propertyBasicInfo.setQunarHotelName(officialHotelname);
					propertyBasicInfo.setQunarAddress(officialHoteladdress);
					propertyBasicInfo.setQunarHotelNameZH(officialHotelchinesname);
					propertyBasicInfo.setQunarHotelCode(qunarHotelCode);
					propertyBasicInfo.setLastUpdatedDate(new Timestamp(System.currentTimeMillis()));
					propertyBasicInfo.setClosed(null);
					session.update(propertyBasicInfo);
				}

			}


		}else if (row.getCell(10)!=null && !GenericValidator.isBlankOrNull(row.getCell(10).getStringCellValue())){
			System.out.println("TTICODE 3 ="+newTTICode);
			propertyBasicInfo.setClosed(true);
			propertyBasicInfo.setLastUpdatedDate(new Timestamp(System.currentTimeMillis()));
			session.update(propertyBasicInfo);
		}*/







//		if (!GenericValidator.isBlankOrNull(row.getCell(9).getStringCellValue()) && !GenericValidator.isBlankOrNull(row.getCell(10).getStringCellValue())){
		/*	if (row.getCell(9)!=null && row.getCell(10)!=null) {
				String qunarHotelName = row.getCell(9).getStringCellValue();
				String qunarAddress = row.getCell(10).getStringCellValue();
				System.out.println("propertyBasicInfo TTIcode = " + propertyBasicInfo.getTTIcode())
				System.out.println("newTTICodew =" + newTTICode);
				System.out.println("qunarHotelName= " + qunarHotelName);
				System.out.println("qunarAddress= " + qunarAddress);
				propertyBasicInfo.setQunarHotelName(qunarHotelName);
				propertyBasicInfo.setQunarAddress(qunarAddress);
				propertyBasicInfo.setQunarHotelCode(null);
				propertyBasicInfo.setLastUpdatedDate(new Timestamp(System.currentTimeMillis()))
	//				session.beginTransaction();
				session.update(propertyBasicInfo);
	//				session.getTransaction().commit();
			} else {
				System.out.println("propertyBasicInfo TTIcode2 = " + propertyBasicInfo.getTTIcode())
				System.out.println("newTTICodew2 =" + newTTICode);
				System.out.println("reason =========================" + reason);
				propertyBasicInfo.setQunarHotelCode(reason);
				propertyBasicInfo.setQunarHotelName(null);
				propertyBasicInfo.setQunarAddress(null);
				propertyBasicInfo.setLastUpdatedDate(new Timestamp(System.currentTimeMillis()))
	//				session.beginTransaction();
				session.update(propertyBasicInfo);
	//				session.getTransaction().commit();
	//			session.getTransaction().commit();
			}*/
	}
}
session.getTransaction().commit();
System.out.println("done!");
