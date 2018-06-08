#!/usr/groovy/latest/bin/groovy

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import org.apache.commons.validator.GenericValidator
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import java.sql.PreparedStatement

@Grapes([
        @Grab(group = 'org.hibernate', module = 'hibernate-annotations', version = '3.4.0.GA'),
        @Grab(group = 'org.hibernate', module = 'hibernate-ehcache', version = '3.6.10.Final'),
        @Grab(group = 'hsqldb', module = 'hsqldb', version = '1.8.0.7'),
        @Grab(group = 'javassist', module = 'javassist', version = '3.4.GA'),
//        @Grab(group = 'net.sourceforge.htmlcleaner', module = 'htmlcleaner', version = '2.6.1'),
        @Grab(group = 'commons-validator', module = 'commons-validator', version = '1.4.0'),
        @Grab(group = 'commons-httpclient', module = 'commons-httpclient', version = '3.1'),
        @Grab(group = 'commons-collections', module = 'commons-collections', version = '3.2.1'),
        @Grab(group = 'commons-math', module = 'commons-math', version = '1.2'),
        @Grab(group = 'org.apache.commons', module = 'commons-lang3', version = '3.2.1'),
//        @Grab(group = 'org.mongodb', module = 'mongo-java-driver', version = '3.2.0'),
//        @Grab(group = 'struts', module = 'struts', version = '1.2.9'),
//        @Grab(group = 'org.apache.poi', module = 'poi', version = '3.9'),
//        @Grab(group = 'org.apache.poi', module = 'poi-ooxml', version = '3.9'),//need to run in Linux
//        @Grab(group = 'org.ostermiller', module = 'utils', version = '1.07.00'),
        @Grab(group = 'org.slf4j', module = 'slf4j-nop', version = '1.7.5'),
        @Grab(group = 'mysql', module = 'mysql-connector-java', version = '5.1.28'),
        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-core', version = '2.7.4'),
        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-databind', version = '2.7.4'),
        @Grab(group = 'com.fasterxml.jackson.dataformat', module = 'jackson-dataformat-xml', version = '2.8.0'),
])

def mysqlProps = [
        "hibernate.dialect": "org.hibernate.dialect.MySQL5InnoDBDialect",
        "hibernate.connection.driver_class": "com.mysql.jdbc.Driver",
        "hibernate.connection.url": "jdbc:mysql://192.168.10.105:3306/gallop?useUnicode=true&amp;characterEncoding=UTF-8",
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
    return config;
}

@JacksonXmlRootElement(localName = "list")
public class HotelListRS {

    @JacksonXmlProperty(isAttribute = false, localName = "hotel")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<Hotel> hotelList = new ArrayList<>();

    public List<Hotel> getHotelList() {
        return hotelList;
    }

    public void setHotelList(List<Hotel> hotelList) {
        this.hotelList = hotelList;
    }

    public Hotel addNewHotel() {
        Hotel hotel = new Hotel();
        hotelList.add(hotel);
        return hotel;
    }

    public static class Hotel {

        @JacksonXmlProperty(isAttribute = true, localName = "id")
        String id;

        @JacksonXmlProperty(isAttribute = true, localName = "city")
        String city;

        @JacksonXmlProperty(isAttribute = true, localName = "tel")
        String tel;

        @JacksonXmlProperty(isAttribute = true, localName = "address")
        String address;

        @JacksonXmlProperty(isAttribute = true, localName = "name")
        String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}


SessionFactory sessionFactory = this.configureHibernate(mysqlProps).buildSessionFactory();
org.hibernate.classic.Session session = sessionFactory.openSession();
def java.sql.Connection connection = session.connection();

void export(java.sql.Connection connection, String viewName, String exportPath) {
    HotelListRS hotelListRS = new HotelListRS();
    String sql = "select * from " + viewName;
    PreparedStatement pstmt = connection.prepareStatement(sql);
    java.sql.ResultSet rs = pstmt.executeQuery();

   String countryString = "AE,AU,CA,DE,EG,FR,GB,ID,IT,JP,KH,KR,LK,MU,MV,MY,NZ,PH,RU,SG,TH,US,VN";

    List<String> countryCodes = Arrays.asList(countryString.split(","));
    while (rs.next()) {
        String TTIcode = rs.getString("TTIcode");
        String hotelName = rs.getString("hotelName");
        String hotelNameZhCN = rs.getString("hotelNameZhCN");
        String addressLine1 = rs.getString("addressLine1");
        String addressLine2 = rs.getString("addressLine2");
        String chineseAddressLine1 = rs.getString("chineseAddressLine1");
        String cityName = rs.getString("cityName");
        String countryCode = rs.getString("countryCode");
        String countryName = rs.getString("countryName");
        Boolean closed = rs.getBoolean("closed");
        Boolean online = rs.getBoolean("online");
        Boolean status = rs.getBoolean("status");
        String telephoneNumber = rs.getString("telephoneNumber");
        String qunarCityCode = rs.getString("qunarCityCode");
        String qunarHotelName = rs.getString("qunarHotelName");
//    String qunarCityName = rs.getString("qunarCityName");
        String qunarAddress = rs.getString("qunarAddress");
        String qunarTelephoneNumber = rs.getString("qunarTelephoneNumber");
        Integer qunarMappingStatus = rs.getInt("qunarMappingStatus");

        HotelListRS.Hotel hotel = hotelListRS.addNewHotel();
        hotel.setId(TTIcode);
        hotel.setTel(telephoneNumber);
        hotel.setCity(qunarCityCode.trim());
        if (!GenericValidator.isBlankOrNull(qunarAddress) && !GenericValidator.isBlankOrNull(qunarHotelName)) {
            hotel.setName(qunarHotelName);
            hotel.setAddress(qunarAddress);
            if (!GenericValidator.isBlankOrNull(qunarTelephoneNumber)){
                hotel.setTel(qunarTelephoneNumber);
            }
        } else {
            hotel.setName(hotelName);
            hotel.setAddress(addressLine1 + ", " + cityName + ", " + countryName);
            if (!GenericValidator.isBlankOrNull(addressLine2))
                hotel.setAddress(addressLine1 + ", " + addressLine2 + "," + cityName + ", " + countryName);
        }
    }


    System.out.println("hotelListRS size=" + hotelListRS.getHotelList().size())

    XmlMapper mapper = new XmlMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    String xml = mapper.writeValueAsString(hotelListRS);
    FileWriter writer = new FileWriter(exportPath);
    writer.write(xml);
    writer.close();
}

//export(connection, "giata.qunar_room51_property_basic_info", "/var/www/hosts/download.room51.cn/hotelListRS.xml");
export(connection, "giata.qunar_roomstays_property_basic_info", "/var/www/hosts/download.roomstays.travel/hotelListRS.xml");
