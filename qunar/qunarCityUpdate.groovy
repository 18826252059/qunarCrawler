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
import com.giatamedia.tticodes.service.PropertyService
import com.skywidetech.gallop.thirdparty.tti.Chain
import com.skywidetech.gallop.thirdparty.tti.PropertyCrossReference
import com.skywidetech.gallop.thirdparty.tti.PropertyAddress
import com.skywidetech.gallop.thirdparty.tti.PropertyEmail
import com.skywidetech.gallop.thirdparty.tti.PropertyPhone
import com.skywidetech.gallop.thirdparty.tti.PropertyPosition
import com.skywidetech.gallop.thirdparty.tti.PropertyURL
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.UsernamePasswordCredentials
import org.apache.commons.httpclient.auth.AuthScope
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.lang3.time.DateFormatUtils

//import com.Ostermiller.util.ExcelCSVParser
import org.apache.cxf.jaxrs.client.WebClient
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.criterion.Restrictions

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
        @Grab(group = 'commons-validator', module = 'commons-validator', version = '1.4.0'),
        @Grab(group = 'commons-httpclient', module = 'commons-httpclient', version = '3.1'),
        @Grab(group = 'commons-collections', module = 'commons-collections', version = '3.2.1'),
        @Grab(group = 'commons-math', module = 'commons-math', version = '1.2'),
        @Grab(group = 'org.apache.commons', module = 'commons-lang3', version = '3.1'),
        @Grab(group = 'struts', module = 'struts', version = '1.2.9'),
        @Grab(group = 'org.apache.poi', module = 'poi', version = '3.9'),
        @Grab(group = 'org.ostermiller', module = 'utils', version = '1.07.00'),
        @Grab(group = 'org.slf4j', module = 'slf4j-nop', version = '1.7.5'),
        @Grab(group = 'mysql', module = 'mysql-connector-java', version = '5.1.28'),
        @Grab(group = 'org.apache.cxf', module = 'cxf-bundle', version = '2.7.2'),
        @Grab(group = 'org.mongodb', module = 'mongo-java-driver', version = '3.2.0'),
        @Grab(group = 'org.codehaus.woodstox', module = 'woodstox-core-asl', version = '4.2.1'),
        @Grab(group = 'org.codehaus.woodstox', module = 'woodstox-core-lgpl', version = '4.2.1'),
        @Grab(group = 'org.codehaus.woodstox', module = 'stax2-api', version = '3.1.1'),
        @Grab(group = 'com.microsoft.sqlserver', module = 'sqljdbc4', version = '4.0'), // internal
        @Grab(group = 'gallop', module = 'gallop', version = '20160714'), // internal
        @Grab(group = 'giata-api', module = 'giata-api', version = '20151223'), // internal
])

def hibProps = [
        "hibernate.dialect"                  : "com.skywidetech.gallop.util.hibernate.SQLServerDialect",
        "hibernate.connection.driver_class"  : "com.mysql.jdbc.Driver",
        "hibernate.connection.url"           : "jdbc:mysql://192.168.10.105:3306/giata?useUnicode=true&amp;characterEncoding=UTF-8",
        "hibernate.connection.username"      : "chengcsw",
        "hibernate.connection.password"      : "ccheng",
        "hibernate.connection.pool_size"     : "1",
        "hibernate.jdbc.batch_size"          : "1000",
//        "hibernate.hbm2ddl.auto": "validate",
//        hibernate.jdbc.batch_size 20
//        "hibernate.connection.autocommit": "true",
        "hibernate.cache.provider_class"     : "org.hibernate.cache.EhCacheProvider",
        "hibernate.transaction.factory_class": "org.hibernate.transaction.JDBCTransactionFactory",
]

def Configuration config = new Configuration()
hibProps.each { k, v -> config.setProperty(k, v) }

// Open hibernate session
SessionFactory factory = config.buildSessionFactory();
org.hibernate.classic.Session session = factory.openSession();
java.sql.Connection connection = session.connection();
println "hibernate initialized";

String sql1 = "update giata.tti_property_basic_info set qunarCityCode = NULL where qunarCityCode = ''";
connection.prepareStatement(sql1).executeUpdate();

String sql2 = "select DISTINCT qunarCityCode, giataCityId\n" +
        "from tti_property_basic_info \n" +
        "where giataCityId not in (\n" +
        "select giataCityId from giata_city_cross_reference \n" +
        "where giata_city_cross_reference.`code` = 'QUNAR' \n" +
        ")\n" +
        "and tti_property_basic_info.qunarCityCode is not NULL and giataCityId is not NULL";
java.sql.ResultSet rs = connection.prepareStatement(sql2).executeQuery();
while (rs.next()) {
    String qunarCityCode = rs.getString("qunarCityCode");
    Integer giataCityId = rs.getInt("giataCityId");
    String sql3 = "insert into giata.giata_city_cross_reference (code, destinationCode, giataCityId) values (?, ?, ?)";
    PreparedStatement pstmt3 = connection.prepareStatement(sql3);
    pstmt3.setString(1, "QUNAR");
    pstmt3.setString(2, qunarCityCode);
    pstmt3.setInt(3, giataCityId);
    pstmt3.executeUpdate();
}

String sql4 = "update giata.tti_property_basic_info set qunarCityCode =\n" +
        "(select destinationCode from giata.giata_city_cross_reference where code = 'QUNAR' \n" +
        "and giata.tti_property_basic_info.giataCityId = giata.giata_city_cross_reference.giataCityId limit 1)\n" +
        "where qunarCityCode is NULL";
PreparedStatement pstmt4 = connection.prepareStatement(sql4);
pstmt4.executeUpdate();

connection.commit();
connection.close();