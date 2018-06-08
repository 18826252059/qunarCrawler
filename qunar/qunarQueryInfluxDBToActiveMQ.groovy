#!/usr/bin/env groovy
package main.groovy.influx

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.skywidetech.gallop.thirdparty.tti.PropertyBasicInfo
import okhttp3.OkHttpClient
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.commons.httpclient.methods.StringRequestEntity
import org.apache.commons.validator.GenericValidator
import org.bson.Document
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.criterion.Restrictions
import org.influxdb.dto.QueryResult
import com.mongodb.MongoClient
import org.json.JSONObject

import javax.jms.Connection
import javax.jms.DeliveryMode
import javax.jms.MessageProducer
import javax.jms.TextMessage
import java.util.concurrent.TimeUnit

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
        @Grab(group = 'org.apache.cxf', module = 'cxf-bundle', version = '2.7.2'),
        @Grab(group = 'gallop', module = 'gallop', version = '20180329'), // internal
        @Grab(group = 'javax.mail', module = 'mail', version = '1.4'),
        @Grab(group='org.apache.activemq', module='activemq-client', version='5.12.0'),
        @Grab(group='com.squareup.okhttp3', module='okhttp', version='3.8.1'),
        @Grab(group='org.influxdb', module='influxdb-java', version='2.7'),
])

def hibProps = [
        "hibernate.dialect"                     : "org.hibernate.dialect.MySQL5InnoDBDialect",
        "hibernate.connection.driver_class"     : "com.mysql.jdbc.Driver",
        "hibernate.connection.url"              : "jdbc:mysql://192.168.10.105:3306/giata?useUnicode=true&amp;characterEncoding=UTF-8",
//        "hibernate.connection.url"           : "jdbc:mysql://192.168.6.10:3306/giata?useUnicode=true&amp;characterEncoding=UTF-8",
//        "hibernate.connection.username"      : "celinecheung",
//        "hibernate.connection.password"      : "celine",
//        "hibernate.connection.url"              : "jdbc:mysql://218.213.148.105:3306/giata?useUnicode=true&amp;characterEncoding=UTF-8",
        "hibernate.connection.username"         : "chengcsw",
        "hibernate.connection.password"         : "ccheng",
//        "hibernate.connection.url"           : "jdbc:mysql://127.0.0.1:3306/giata?useUnicode=true&amp;characterEncoding=UTF-8",
//        "hibernate.connection.username"      : "root",
//        "hibernate.connection.password"      : "cl0secfg",
        "hibernate.connection.pool_size"        : "1",
        "hibernate.jdbc.batch_size"             : "1000",
//        "hibernate.show_sql"          : "true",
//        "hibernate.hbm2ddl.auto": "validate",
//        hibernate.jdbc.batch_size 20
        "hibernate.connection.useUnicode"       : "true",
        "hibernate.connection.characterEncoding": "utf-8",
        "hibernate.connection.autocommit"       : "true",
        "hibernate.cache.provider_class"        : "org.hibernate.cache.EhCacheProvider",
        "hibernate.transaction.factory_class"   : "org.hibernate.transaction.JDBCTransactionFactory",
]

def Configuration config = new Configuration()
hibProps.each { k, v -> config.setProperty(k, v) }
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
config.addAnnotatedClass(com.skywidetech.gallop.thirdparty.qunar.Rooms.class);
SessionFactory factory = config.buildSessionFactory();
Session session = factory.openSession();

public class SlackPostMessage {

    @JsonProperty("channel")
    String channel;
    @JsonProperty("username")
    String username;
    @JsonProperty("text")
    String text;
    @JsonProperty("icon_emoji")
    String icon_emoji;
    @JsonProperty("icon_url")
    String icon_url;
    @JsonProperty("attachments")
    List<SlackPostMessage.Attachment> attachments;

    public SlackPostMessage() {
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon_emoji() {
        return this.icon_emoji;
    }

    public void setIcon_emoji(String icon_emoji) {
        this.icon_emoji = icon_emoji;
    }

    public String getIcon_url() {
        return this.icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public List<SlackPostMessage.Attachment> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(List<SlackPostMessage.Attachment> attachments) {
        this.attachments = attachments;
    }

    public static class Attachment {
        @JsonProperty("fallback")
        String fallback;
        @JsonProperty("pretext")
        String pretext;
        @JsonProperty("color")
        String color;
        @JsonProperty("fields")
        List<SlackPostMessage.Attachment.Field> fields;

        public Attachment() {
        }

        public String getFallback() {
            return this.fallback;
        }

        public void setFallback(String fallback) {
            this.fallback = fallback;
        }

        public String getPretext() {
            return this.pretext;
        }

        public void setPretext(String pretext) {
            this.pretext = pretext;
        }

        public String getColor() {
            return this.color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public List<SlackPostMessage.Attachment.Field> getFields() {
            return this.fields;
        }

        public void setFields(List<SlackPostMessage.Attachment.Field> fields) {
            this.fields = fields;
        }

        public static class Field {
            @JsonProperty("title")
            String title;
            @JsonProperty("value")
            String value;
            @JsonProperty("short")
            Boolean isShort;

            public Field() {
            }

            public String getTitle() {
                return this.title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getValue() {
                return this.value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public Boolean getIsShort() {
                return this.isShort;
            }

            public void setIsShort(Boolean isShort) {
                this.isShort = isShort;
            }
        }
    }
}

ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.10.105:61616");
// Create a Connection
Connection connection = connectionFactory.createConnection();
connection.start();
// Create a Session
javax.jms.Session jmsSession = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
// Create the destination (Topic or Queue)
javax.jms.Queue destination = jmsSession.createQueue("plivo.sms.hotProperty");
// Create a MessageProducer from the Session to the Topic or Queue
MessageProducer producer = jmsSession.createProducer(destination);
producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

long connectTimeout = 10;
long writeTimeout = 10;
long readTimeout = 5000;

String openurl = "http://192.168.10.138:8086";
String username = "root";
String password = "root";
String database = "qunar";

System.out.println("start get list");
OkHttpClient.Builder client = (new OkHttpClient.Builder()).connectTimeout(connectTimeout, TimeUnit.SECONDS).writeTimeout(writeTimeout, TimeUnit.SECONDS).readTimeout(readTimeout, TimeUnit.SECONDS);
org.influxdb.InfluxDB influxDB = org.influxdb.InfluxDBFactory.connect(openurl, username, password, client);
influxDB.createDatabase(database);
//String command = "SELECT count(qunarSeq)  FROM \"qunar\".\"one_month_only\".\"hit\"  WHERE time > now() - 7d  AND qunarSeq_tag =~ /.*/ GROUP BY qunarSeq_tag";
String command = "SELECT count(qunarSeq)  FROM \"qunar\".\"one_month_only\".\"return\"  WHERE time > now() - 7d  AND roomCount > 0 AND hourInt > 3 AND qunarSeq_tag =~ /.*/ GROUP BY qunarSeq_tag";
org.influxdb.dto.QueryResult results = influxDB.query(new org.influxdb.dto.Query(command, database));

int countLimit = 10;

System.out.println("start insert mongodb");
String serverAddress = "192.168.10.114";
MongoClient mongoClient = new MongoClient(serverAddress, 27017);
MongoDatabase db = mongoClient.getDatabase("qunar");  // like mysql database
MongoCollection collTemp = db.getCollection("HotProperty");  // like mysql table
collTemp.drop();
db.createCollection("HotProperty");
MongoCollection coll = db.getCollection("HotProperty");
List<String> lists = new ArrayList<String>();
for (org.influxdb.dto.QueryResult.Result result : results.getResults()) {
    List<QueryResult.Series> series = result.getSeries();
    for (QueryResult.Series serie : series) {
        List<List<Object>> values = serie.getValues();
        int count = (int) values.get(0).get(1);
        String qunarSeq = serie.getTags().get("qunarSeq_tag");
        if (count >= countLimit && !GenericValidator.isBlankOrNull(qunarSeq)) {
            lists.add(qunarSeq);
            // Insert ActiveMQ
//            TextMessage message = jmsSession.createTextMessage(qunarSeq);
//            producer.send(message);
            //insert mongoDB
            Map qunarSeqMap = new HashMap();
            qunarSeqMap.put("hotelSeq", qunarSeq);
            JSONObject jsonObject = new JSONObject(qunarSeqMap);
            Document dbObject = (Document) Document.parse(jsonObject.toString());
            coll.insertOne(dbObject);
        }
    }
}

System.out.println("list's size=" + lists.size());

String[] countryCodeList = ['TH', 'VN', 'SG', 'MY', 'ID'];
List<PropertyBasicInfo> propertyBasicInfoList = session.createCriteria(PropertyBasicInfo.class)
//        .add(Restrictions.in("qunarHotelCode", lists.toArray()))
//        .add(Restrictions.in("countryCode", countryCodeList))
        .add(Restrictions.and(Restrictions.in("qunarHotelCode", lists.toArray()),Restrictions.in("countryCode", countryCodeList)))
        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
        .list();
System.out.println("propertyBasicInfoList's size=" + propertyBasicInfoList.size());
int messageCount = 0;
for (PropertyBasicInfo propertyBasicInfo : propertyBasicInfoList) {
    // Insert ActiveMQ
    if (!GenericValidator.isBlankOrNull(propertyBasicInfo.getQunarHotelCode()) && !"none".equals(propertyBasicInfo.getQunarHotelCode())) {
        System.out.println("add to message = " + propertyBasicInfo.getQunarHotelCode());
        TextMessage message = jmsSession.createTextMessage(propertyBasicInfo.getQunarHotelCode());
        producer.send(message);
        messageCount++;
    }
}
System.out.println("MESSAGE's size=" + messageCount);
/*try {
    String webhookURL = "https://hooks.slack.com/services/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";
    String slackChannel = "#crawlers";
    String slackUserName = "webhookbot";

    org.apache.commons.httpclient.methods.PostMethod httpPost = new org.apache.commons.httpclient.methods.PostMethod(webhookURL);
    SlackPostMessage postMessage = new SlackPostMessage();
    postMessage.setChannel(slackChannel);
    postMessage.setUsername(slackUserName);

    String text = "Qunar Query InfluxDB To ActiveMQ done! hotel list's size=" + lists.size();
    postMessage.setText(text);
    postMessage.setIcon_emoji(":ghost:");

    ObjectMapper mapper = new ObjectMapper();
    StringRequestEntity requestEntity = new StringRequestEntity(mapper.writeValueAsString(postMessage));
    httpPost.setRequestEntity(requestEntity);
    org.apache.commons.httpclient.HttpClient slackClient = new org.apache.commons.httpclient.HttpClient();
    slackClient.executeMethod(httpPost);

} catch (Exception e) {
    e.printStackTrace(System.out);
}*/
// Clean up
jmsSession.close();
connection.close();
System.exit(0);

