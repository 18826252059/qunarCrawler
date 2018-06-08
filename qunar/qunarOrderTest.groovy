#!/usr/bin/env groovy

import com.skywidetech.gallop.thirdparty.tti.CityName
import com.skywidetech.gallop.thirdparty.giata.Property
import com.skywidetech.gallop.thirdparty.tti.PropertyName
import com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.criterion.Restrictions
import main.java.org.json.JSONObject

import java.sql.PreparedStatement

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
        @Grab(group = 'com.microsoft.sqlserver', module = 'sqljdbc4', version = '4.0'), // internal
        @Grab(group = 'gallop', module = 'gallop', version = '20160507'), // internal
])

def hibProps = [
        "hibernate.dialect": "com.skywidetech.gallop.util.hibernate.SQLServerDialect",
        "hibernate.connection.driver_class": "com.mysql.jdbc.Driver",
        "hibernate.connection.url": "jdbc:mysql://192.168.10.105:3306/gallop?useUnicode=true&amp;characterEncoding=UTF-8",
        "hibernate.connection.username": "chengcsw",
        "hibernate.connection.password": "ccheng",
        "hibernate.connection.pool_size": "1",
        "hibernate.jdbc.batch_size": "1000",
        "hibernate.hbm2ddl.auto": "validate",
//        hibernate.jdbc.batch_size 20
//        "hibernate.connection.autocommit": "true",
        "hibernate.cache.provider_class": "org.hibernate.cache.EhCacheProvider",
        "hibernate.transaction.factory_class": "org.hibernate.transaction.JDBCTransactionFactory",
]

def configureHibernate(props) {
    def Configuration config = new Configuration()
    props.each { k, v -> config.setProperty(k, v) }
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.Invoice.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.Room.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.Room.BedType.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.Room.BedType.Bed.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.Room.Meal.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.Room.Meal.Breakfast.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.Room.Meal.Lunch.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.Room.Meal.Dinner.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.Room.Refund.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.Room.Refund.RefundRule.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.Room.Refund.NonRefundableRange.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.Room.Remark.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.Room.OptionRule.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.Room.PromotionRule.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.Room.ExtraProperty.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.CustomerInfo.class);
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.HotelOrderInfo.CustomerInfo.Customer.class);
}

// Open hibernate session
SessionFactory factory = this.configureHibernate(hibProps).buildSessionFactory();
org.hibernate.Session session = factory.openSession();
println "hibernate initialized";

HotelOrderInfo hotelOrderInfo = new HotelOrderInfo();
//hotelOrderInfo.setOrderNum("12345");
HotelOrderInfo.Invoice invoice = hotelOrderInfo.addNewInvoice();
invoice.setCity("hongkong");
HotelOrderInfo.Room room = hotelOrderInfo.addNewRoom();
room.setName("hongkong");
room.addNewBedType().addNewBed().setCode("TEST");
HotelOrderInfo.Room.Meal meal = room.addNewMeal();
meal.addNewBreakfast().setDesc("TEST");
meal.addNewLunch().setDesc("TEST");
meal.addNewDinner().setDesc("TEST");
HotelOrderInfo.Room.Refund refund = room.addNewRefund();
refund.addNewNonRefundableRange().setFromDate("TEST");
refund.addNewRefundRule().setType("TEST");
room.addNewRemark().setValue("TEST");
room.addNewOptionRule().setDesc("TEST");
room.addNewExtraProperty().setValue("TEST");
HotelOrderInfo.CustomerInfo customerInfo = hotelOrderInfo.addNewCustomerInfo();
customerInfo.setChildrenAges("TEST");
HotelOrderInfo.CustomerInfo.Customer customer = customerInfo.addNewCustomer();
customer.setFirstName("TEST");

session.beginTransaction();
session.saveOrUpdate(hotelOrderInfo);
session.getTransaction().commit();

