#!/usr/bin/env groovy

import com.skywidetech.gallop.thirdparty.tti.PropertyDescription
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import com.aerospike.client.*;
import com.aerospike.client.policy.*
import org.hibernate.criterion.Order

import java.sql.PreparedStatement;

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
        @Grab(group = 'struts', module = 'struts', version = '1.2.9'),
        @Grab(group = 'org.apache.poi', module = 'poi', version = '3.9'),
        @Grab(group = 'org.ostermiller', module = 'utils', version = '1.07.00'),
        @Grab(group = 'mysql', module = 'mysql-connector-java', version = '5.1.28'),
        @Grab(group = 'org.apache.cxf', module = 'cxf-bundle', version = '2.7.2'),
        @Grab(group = 'org.mongodb', module = 'mongo-java-driver', version = '3.2.0'),
        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-core', version = '2.3.3'),
        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-databind', version = '2.3.3'),
        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-annotations', version = '2.3.3'),
        @Grab(group = 'com.aerospike', module = 'aerospike-client', version = '3.0.22'),
        @Grab(group = 'org.gnu', module = 'gnu-crypto', version = '2.0.1'),
        @Grab(group = 'org.luaj', module='luaj-jse', version='3.0.1'),
//        @Grab(group = 'sabre-iur', module = 'sabre-iur', version = '20131012'), // internal
//        @Grab(group = 'galileo-mir', module = 'galileo-mir', version = '20131012'), // internal
//        @Grab(group = 'amadeus-air', module = 'amadeus-air', version = '20131012'), // internal
//        @Grab(group = 'com.microsoft.sqlserver', module = 'sqljdbc4', version = '4.0'), // internal
//        @Grab(group = 'gallop', module = 'gallop', version = '20170709'), // internal
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
def configureHibernate(props) {
    def Configuration config = new Configuration()
    props.each { k, v -> config.setProperty(k, v) }
//    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.tti.CityCrossReference.class);
//    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.City.class);
    return config;
}

// Open hibernate session
SessionFactory factory = this.configureHibernate(hibProps).buildSessionFactory();
org.hibernate.StatelessSession session = factory.openStatelessSession();
println "hibernate initialized";

String importFileName = "/tmp/qunar_city_relocation.xls";
File file = new File(importFileName);
//FileInputStream fis = new FileInputStream(file);
org.apache.poi.ss.usermodel.Workbook wb = org.apache.poi.ss.usermodel.WorkbookFactory.create(file);
System.out.println("read OK");
org.apache.poi.ss.usermodel.Sheet sheet = wb.getSheetAt(0);

for (int i=1; i<=sheet.getLastRowNum(); i++) {
    org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
//    System.out.println(row.getCell(0).getStringCellValue());
//    System.out.println(row.getCell(1).getStringCellValue());

    session.beginTransaction();

    PreparedStatement pstmt1 = session.connection().prepareStatement("update giata.tti_property_basic_info set qunarCityCode = ? where qunarCityCode = ?");
    pstmt1.setString(1, row.getCell(1).getStringCellValue());
    pstmt1.setString(2, row.getCell(0).getStringCellValue());
    pstmt1.execute();

    PreparedStatement pstmt2 = session.connection().prepareStatement("update giata.giata_city_cross_reference set destinationCode = ? where destinationCode = ? and code = 'QUNAR'");
    pstmt2.setString(1, row.getCell(1).getStringCellValue());
    pstmt2.setString(2, row.getCell(0).getStringCellValue());
    pstmt2.execute();

    session.connection().commit();
}

System.exit(0);
