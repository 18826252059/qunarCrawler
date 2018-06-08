
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration

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
//org.hibernate.Session session = factory.openSession();

File file = new File("/tmp/crawler/qunarmapping/AE.xlsx");
FileInputStream fis = new FileInputStream(file);
XSSFWorkbook wb = new XSSFWorkbook(fis);

System.out.println("read ok");


