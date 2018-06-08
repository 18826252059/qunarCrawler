import javax.mail.*;

String host = "zimbra.skywidetech.com";
String user = "chris_sms@skywidetech.com";
String password = "86Waterloo16c";

Properties properties = System.getProperties();
javax.mail.Session mailSession = javax.mail.Session.getDefaultInstance(properties);
Store store = mailSession.getStore("imaps");
store.connect(host, user, password);
Folder inbox = store.getFolder("Inbox");
inbox.open(Folder.READ_ONLY);
Message[] messages = inbox.getMessages();
if (messages.length == 0) System.out.println("No messages found.");
for (int i = 0; i < messages.length; i++) {
    String subject = messages[i].getSubject();
    System.out.println("subject=" + subject);
    if (messages[i].getContent() instanceof javax.mail.internet.MimeMultipart) {
        javax.mail.internet.MimeMultipart content = messages[i].getContent();
        System.out.println(content.getBodyPart(0).getContent());
    }
    else {
        String content = messages[i].getContent();
        System.out.println("content=" + content);
    }
}
