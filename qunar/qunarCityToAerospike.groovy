#!/usr/bin/env groovy

import com.skywidetech.gallop.thirdparty.tti.PropertyDescription
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import com.aerospike.client.*;
import com.aerospike.client.policy.*
import org.hibernate.criterion.Order;

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
        @Grab(group='org.luaj', module='luaj-jse', version='3.0.1'),
//        @Grab(group = 'sabre-iur', module = 'sabre-iur', version = '20131012'), // internal
//        @Grab(group = 'galileo-mir', module = 'galileo-mir', version = '20131012'), // internal
//        @Grab(group = 'amadeus-air', module = 'amadeus-air', version = '20131012'), // internal
//        @Grab(group = 'com.microsoft.sqlserver', module = 'sqljdbc4', version = '4.0'), // internal
        @Grab(group = 'gallop', module = 'gallop', version = '20170709'), // internal
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
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.City.class);
}

//String host = "192.168.10.125";
String host = "192.168.10.168";
Integer port = 3000;
String user = "";
String password = "";

// Open hibernate session
SessionFactory factory = this.configureHibernate(hibProps).buildSessionFactory();
org.hibernate.StatelessSession session = factory.openStatelessSession();
println "hibernate initialized";

ClientPolicy policy = new ClientPolicy();
policy.failIfNotConnected = true;

WritePolicy writePolicy = new WritePolicy();
writePolicy.timeout = 100000;
writePolicy.expiration = -1;

System.out.println("connecting");
AerospikeClient aerospikeClient = new AerospikeClient(policy, host, port); // thread safe
System.out.println("connected");

List<com.skywidetech.gallop.thirdparty.qunar.City> cities = session.createCriteria(com.skywidetech.gallop.thirdparty.qunar.City.class)
//        .setFirstResult(0)
//        .setMaxResults(100)
//        .addOrder(Order.asc("ttiCode"))
        .list();

System.out.println("cities size=" + cities.size())

String namespace = "qunar";
String set = "city";
String binName = "value";

long startTime = System.currentTimeMillis();

for (com.skywidetech.gallop.thirdparty.qunar.City city : cities) {
    Key key = new Key(namespace, set, city.getId());
    Bin bin = new Bin(binName, city);
    aerospikeClient.put(writePolicy, key, bin);
}

long endTime = System.currentTimeMillis();
System.out.println("it takes = " + (endTime - startTime));

System.out.println("done!")

