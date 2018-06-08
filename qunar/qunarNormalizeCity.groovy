#!/usr/bin/env groovy

import com.skywidetech.gallop.thirdparty.tti.CityName
import com.skywidetech.gallop.thirdparty.giata.Property
import com.skywidetech.gallop.thirdparty.tti.PropertyName
import com.skywidetech.gallop.thirdparty.qunar.City
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
//        hibernate.jdbc.batch_size 20
//        "hibernate.connection.autocommit": "true",
        "hibernate.cache.provider_class": "org.hibernate.cache.EhCacheProvider",
        "hibernate.transaction.factory_class": "org.hibernate.transaction.JDBCTransactionFactory",
]

def configureHibernate(props) {
    def Configuration config = new Configuration()
    props.each { k, v -> config.setProperty(k, v) }
    config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.City.class);
}

// Open hibernate session
SessionFactory factory = this.configureHibernate(hibProps).buildSessionFactory();
org.hibernate.StatelessSession session = factory.openStatelessSession();
println "hibernate initialized";

java.sql.Connection connection = session.connection();
session.beginTransaction();

List<com.skywidetech.gallop.thirdparty.qunar.City> qunarCities =
        session.createCriteria(com.skywidetech.gallop.thirdparty.qunar.City.class)
//                .add(Restrictions.eq("cityType", "city"))
//                .add(Restrictions.isNull("countryId"))
                .list();

Map<String, com.skywidetech.gallop.thirdparty.qunar.City> cityMap = new HashMap<String, City>();
for (com.skywidetech.gallop.thirdparty.qunar.City qunarCity : qunarCities) {
    cityMap.put(qunarCity.getId(), qunarCity);
}

int count = 0;
for (com.skywidetech.gallop.thirdparty.qunar.City qunarCity : qunarCities) {

    String areaPath = qunarCity.getAreaPath();
    StringTokenizer st = new StringTokenizer(areaPath, ".");
    List<String> areas = new ArrayList<String>();
    while (st.hasMoreElements()) {
        areas.add(st.nextElement());
    }
//    System.out.println(areas.toArray());

    if (qunarCity.getCityType().equals("city")) {

        String countryId = areas.get(1);
        qunarCity.setCountryId(countryId);
        com.skywidetech.gallop.thirdparty.qunar.City country = cityMap.get(countryId);
        System.out.println("countryId=" + countryId);
        qunarCity.setCountryName(country.getEnName());

        if (areas.size() > 2) {
            String provinceId = areas.get(2);
            qunarCity.setProvinceId(provinceId);
            com.skywidetech.gallop.thirdparty.qunar.City province = cityMap.get(provinceId);
            qunarCity.setProvinceName(province.getEnName());
        }

        if (areas.size() > 3) {
            String cityLevel1Id = areas.get(3);
            qunarCity.setCityLevel1Id(cityLevel1Id);
            com.skywidetech.gallop.thirdparty.qunar.City city1 = cityMap.get(cityLevel1Id);
            qunarCity.setCityLevel1Name(city1.getEnName());
        }

        if (areas.size() > 4) {
            String cityLevel2Id = areas.get(4);
            qunarCity.setCityLevel2Id(cityLevel2Id);
            com.skywidetech.gallop.thirdparty.qunar.City city2 = cityMap.get(cityLevel2Id);
            qunarCity.setCityLevel2Name(city2.getEnName());
        }

        if (areas.size() > 5) {
            String cityLevel3Id = areas.get(5);
            qunarCity.setCityLevel3Id(cityLevel3Id);
            com.skywidetech.gallop.thirdparty.qunar.City city3 = cityMap.get(cityLevel3Id);
            qunarCity.setCityLevel3Name(city3.getEnName());
        }

        if (areas.size() > 6) {
            String cityLevel4Id = areas.get(6);
            qunarCity.setCityLevel4Id(cityLevel4Id);
            com.skywidetech.gallop.thirdparty.qunar.City city4 = cityMap.get(cityLevel4Id);
            qunarCity.setCityLevel4Name(city4.getEnName());
        }

        if (areas.size() > 7) {
            String cityLevel5Id = areas.get(7);
            qunarCity.setCityLevel5Id(cityLevel5Id);
            com.skywidetech.gallop.thirdparty.qunar.City city5 = cityMap.get(cityLevel5Id);
            qunarCity.setCityLevel5Name(city5.getEnName());
        }
    }

    session.update(qunarCity);

    count++;

    System.out.println("count=" + count);
}

session.getTransaction().commit();

//update city set countryName = (select en_name from city2 where city.countryId = city2.id) where countryName is null and countryId is not null;
//update city set provinceName = (select en_name from city2 where city.provinceId = city2.id) where provinceName is null and provinceId is not null;
//update city set cityLevel1Name = (select en_name from city2 where city.cityLevel1Id = city2.id);
//update city set cityLevel2Name = (select en_name from city2 where city.cityLevel2Id = city2.id);
//update city set cityLevel3Name = (select en_name from city2 where city.cityLevel3Id = city2.id);
//update city set cityLevel4Name = (select en_name from city2 where city.cityLevel4Id = city2.id);
//update city set cityLevel5Name = (select en_name from city2 where city.cityLevel5Id = city2.id);