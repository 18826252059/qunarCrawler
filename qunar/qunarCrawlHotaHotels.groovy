
import com.gargoylesoftware.htmlunit.util.NameValuePair
import com.skywidetech.gallop.thirdparty.tti.Chain
import com.skywidetech.gallop.thirdparty.tti.Channel
import com.skywidetech.gallop.thirdparty.tti.City
import com.skywidetech.gallop.thirdparty.tti.CityCrossReference
import com.skywidetech.gallop.thirdparty.tti.CityName
import com.skywidetech.gallop.thirdparty.tti.Country
import com.skywidetech.gallop.thirdparty.tti.Locale
import com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfo
import com.skywidetech.gallop.thirdparty.tti.PropertyAddress
import com.skywidetech.gallop.thirdparty.tti.PropertyAmenity
import com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfo
import com.skywidetech.gallop.thirdparty.tti.PropertyCrossReference
import com.skywidetech.gallop.thirdparty.tti.PropertyEmail
import com.skywidetech.gallop.thirdparty.tti.PropertyName
import com.skywidetech.gallop.thirdparty.tti.PropertyPhone
import com.skywidetech.gallop.thirdparty.tti.PropertyPosition
import com.skywidetech.gallop.thirdparty.tti.PropertyURL
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.StringRequestEntity
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.criterion.Restrictions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.phantomjs.*
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import org.openqa.selenium.chrome.*;

import javax.xml.ws.WebEndpoint
import java.nio.charset.Charset
import java.sql.PreparedStatement;

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
        @Grab(group = 'com.fasterxml.jackson.core', module = 'jackson-databind', version = '2.3.3'),
        @Grab(group = 'org.apache.commons', module = 'commons-email', version = '1.3.1'),
        @Grab(group = 'org.slf4j', module = 'slf4j-nop', version = '1.7.5'),
])

def hibProps = [
        "hibernate.dialect"                  : "com.skywidetech.gallop.util.hibernate.SQLServerDialect",
        "hibernate.connection.driver_class"  : "com.mysql.jdbc.Driver",
        "hibernate.connection.url"           : "jdbc:mysql://192.168.10.105:3306/gallop?useUnicode=true&amp;characterEncoding=UTF-8",
        "hibernate.connection.username"      : "chengcsw",
        "hibernate.connection.password"      : "ccheng",
        "hibernate.connection.pool_size"     : "1",
        "hibernate.jdbc.batch_size"          : "1000",
        "hibernate.connection.characterEncoding": "utf-8",
//        "hibernate.hbm2ddl.auto": "validate",
//        hibernate.jdbc.batch_size 20
        "hibernate.connection.autocommit": "true",
        "hibernate.cache.provider_class"     : "org.hibernate.cache.EhCacheProvider",
        "hibernate.transaction.factory_class": "org.hibernate.transaction.JDBCTransactionFactory",
]

def Configuration config = new Configuration()
hibProps.each { k, v -> config.setProperty(k, v) }
config.addAnnotatedClass(PropertyBasicInfo.class);
config.addAnnotatedClass(PropertyCrossReference.class);
config.addAnnotatedClass(com.skywidetech.gallop.party.party.Vendor.class);
SessionFactory factory = config.buildSessionFactory();
org.hibernate.classic.Session session = factory.openSession();
java.sql.Connection connection = session.connection();

WebClient webClient = new WebClient();

URL url = new URL("https://shanghu.qunar.com/passport/doLogin");
//String supplierId = "1237149";
String supplierId = "1237150";
WebRequest requestSettings = new WebRequest(url, HttpMethod.POST);
List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
nameValuePairList.add(new NameValuePair("username", "ha9ws:admin"));
nameValuePairList.add(new NameValuePair("password", "qunar.com"));
nameValuePairList.add(new NameValuePair("captcha", ""));
nameValuePairList.add(new NameValuePair("remember", "false"));
requestSettings.setRequestParameters(nameValuePairList);
//requestSettings.setAdditionalHeader("Accept", "*/*");
//requestSettings.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//requestSettings.setAdditionalHeader("Referer", "REFURLHERE");
//requestSettings.setAdditionalHeader("Accept-Language", "en-US,en;q=0.8");
//requestSettings.setAdditionalHeader("Accept-Encoding", "gzip,deflate,sdch");
//requestSettings.setAdditionalHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
//requestSettings.setAdditionalHeader("X-Requested-With", "XMLHttpRequest");
//requestSettings.setAdditionalHeader("Cache-Control", "no-cache");
//requestSettings.setAdditionalHeader("Pragma", "no-cache");
//requestSettings.setAdditionalHeader("Origin", "https://YOURHOST");
Page page = webClient.getPage(requestSettings);
//System.out.println(page.getWebResponse().getContentAsString(Charset.defaultCharset()));
for (int j = 1; j<2; j++) {
    i = 1;
//    for (int i = 1; i < 9450; i++) {
        try {
            System.out.println("page=" + i);
            url = new URL("http://hota.qunar.com/baseinfo/oapi/shotel/search");
            requestSettings = new WebRequest(url, HttpMethod.POST);
            requestSettings.setAdditionalHeader("Content-Type", "application/json;charset=UTF-8");
            supplierId = j;
            requestSettings.setRequestBody("{\"distance\":\"1\",\"page\":" + i + ",\"pageSize\":15,\"supplierId\":\"" + supplierId + "\"}");
            page = webClient.getPage(requestSettings);
            String response = page.getWebResponse().getContentAsString(Charset.defaultCharset());
            System.out.println(response);
            JSONObject jsonObject = JSONObject.fromObject((String) response);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject result = data.getJSONObject("result");
            JSONArray list = result.getJSONArray("list");
            String supplierName = null;
            for (ListIterator iterator = list.listIterator(); iterator.hasNext();) {
                JSONObject item = (JSONObject) iterator.next();
//                System.out.println(item.getString("supplierId"));
//                System.out.println(item.getString("supplierName"));
                if (supplierName == null) {
//                    String supplierId = item.getString("supplierId");
                    supplierName = item.getString("supplierName");
                    com.skywidetech.gallop.party.party.Vendor vendor = new com.skywidetech.gallop.party.party.Vendor();
                    vendor.setVendorCode(String.valueOf(supplierId));
                    vendor.setVendorName(supplierName);
                    session.beginTransaction();
                    session.saveOrUpdate(vendor);
                    session.getTransaction().commit();
                }
                    //                System.out.println(item.getString("id"));
//                JSONObject propInfo = item.getJSONObject("propInfo");
//                propInfo.getString("name");
//                propInfo.getString("address");
//                JSONObject bizInfo = item.getJSONObject("bizInfo");
//                String TTIcode = bizInfo.getString("partnerHotelId");
//                String hotelSeq = item.getString("hotelSeq");
//                System.out.println("hotelSeq=" + item.getString("hotelSeq"));
//                System.out.println("TTIcode=" + TTIcode);
//                PropertyBasicInfo propertyBasicInfo = session.createCriteria(PropertyBasicInfo.class).add(Restrictions.eq("TTIcode", TTIcode)).uniqueResult();
//                propertyBasicInfo.setQunarHotelCode(hotelSeq);
//                session.beginTransaction();
//                session.saveOrUpdate(propertyBasicInfo);
//                session.getTransaction().commit();
            }
        }
        catch (Exception e) {
            i--;
            e.printStackTrace(System.out)
        }
//    }
}


