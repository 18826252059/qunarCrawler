/**
 * Created by Celine Cheung on 2016/8/15.
 */

/**
 * Created by Celine Cheung on 2016/6/20.
 */

//#!/usr/bin/env groovy


import com.skywidetech.gallop.thirdparty.tti.City
import com.skywidetech.gallop.thirdparty.tti.CityCrossReference
import com.skywidetech.gallop.thirdparty.tti.CityName
import com.skywidetech.gallop.thirdparty.tti.Country
import com.skywidetech.gallop.thirdparty.tti.Locale
import com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfo
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.criterion.Restrictions

import java.sql.ResultSet

@Grapes([
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
		@Grab(group = 'org.apache.poi', module = 'poi-ooxml', version = '3.9'),
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
		@Grab(group = 'gallop', module = 'gallop', version = '20161229'), // internal
])

def hibProps = [
		"hibernate.dialect": "com.skywidetech.gallop.util.hibernate.SQLServerDialect",
		"hibernate.connection.driver_class": "com.mysql.jdbc.Driver",
		"hibernate.connection.url": "jdbc:mysql://192.168.10.105:3306/gallop?useUnicode=true&amp;characterEncoding=UTF-8",
		"hibernate.connection.username": "celinecheung",
		"hibernate.connection.password": "celine",
		"hibernate.connection.pool_size": "1",
		"hibernate.jdbc.batch_size": "1000",
		"hibernate.connection.characterEncoding": "utf-8",
//        hibernate.jdbc.batch_size 20
//        "hibernate.connection.autocommit": "true",
		"hibernate.cache.provider_class": "org.hibernate.cache.EhCacheProvider",
		"hibernate.transaction.factory_class": "org.hibernate.transaction.JDBCTransactionFactory",
]

def configureHibernate(props) {
	def Configuration config = new Configuration()
	props.each { k, v -> config.setProperty(k, v) }
	config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.City.class);
	config.addAnnotatedClass(CityCrossReference.class);
	config.addAnnotatedClass(City.class);
	config.addAnnotatedClass(CityName.class);
	config.addAnnotatedClass(Locale.class);
	config.addAnnotatedClass(Country.class);
	config.addAnnotatedClass(PropertyBasicInfo.class);
}

// Open hibernate session
SessionFactory factory = this.configureHibernate(hibProps).buildSessionFactory();
org.hibernate.StatelessSession session = factory.openStatelessSession();
java.sql.Connection connection = session.connection();
println "hibernate initialized";

//PreparedStatement pstmt = connection.prepareStatement("select count(*) from giata.tti_property_basic_info where TTIcode= ? ");
session.beginTransaction();
org.apache.poi.xssf.usermodel.XSSFWorkbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook(new FileInputStream("/temp/20170109_unmapping.xlsx"));
XSSFSheet sheet = wb.getSheetAt(0);
for (int rownum=1; rownum <= sheet.getLastRowNum(); rownum++) {
	XSSFRow row = sheet.getRow(rownum);
	if (row.getCell(0)!=null){

		Integer ttiCode = row.getCell(0).getNumericCellValue();
		String newTTICode=String.valueOf(ttiCode);

//		pstmt.setString(1,newTTICode);
//		ResultSet resultSet = pstmt.executeQuery();
//		resultSet.next();
//
//		int numberOfRows = resultSet.getInt(1);


		PropertyBasicInfo propertyBasicInfo = session.createCriteria(PropertyBasicInfo.class)
				.add(Restrictions.eq("TTIcode", newTTICode))
				.uniqueResult();


//		if (propertyBasicInfo != null && numberOfRows!=0 ){
			if (propertyBasicInfo != null){
			System.out.println("newTTICodew =" +newTTICode);
			System.out.println("rownum =" +rownum);
			propertyBasicInfo.setQunarMapped(false);
//				propertyBasicInfo.setOnline(true);

/*				propertyBasicInfo.setQunarMappingStatus(-2);
				propertyBasicInfo.setQunarAddress(null);
				propertyBasicInfo.setQunarHotelName(null);
				propertyBasicInfo.setQunarHotelCode(null);
				propertyBasicInfo.setQunarHotelNameZH(null);
				propertyBasicInfo.setQunarAddress(null);
				propertyBasicInfo.setQunarTelephoneNumber(null);*/
				session.update(propertyBasicInfo);

		}

	}
}
session.getTransaction().commit();
System.out.println("done!");
