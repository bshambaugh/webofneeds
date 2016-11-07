package won.bot.framework.eventbot.action.impl.mail.send;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import won.bot.framework.eventbot.EventListenerContext;
import won.bot.framework.eventbot.action.BaseEventBotAction;
import won.bot.framework.eventbot.action.EventBotActionUtils;
import won.bot.framework.eventbot.action.impl.mail.model.WonURI;
import won.bot.framework.eventbot.action.impl.mail.model.UriType;
import won.bot.framework.eventbot.action.impl.mail.receive.MailContentExtractor;
import won.bot.framework.eventbot.event.Event;
import won.bot.framework.eventbot.event.impl.wonmessage.ConnectFromOtherNeedEvent;
import won.protocol.model.Connection;

import javax.mail.internet.MimeMessage;
import java.net.URI;

/**
 * Created by fsuda on 03.10.2016.
 */
public class Connect2MailParserAction extends BaseEventBotAction {
    private String uriListName;
    private String uriMimeMessageRelationsName;
    private String mailIdUriRelationsName;
    private MessageChannel sendChannel;
    private EventListenerContext ctx;
    private WonMimeMessageGenerator mailGenerator;

    public Connect2MailParserAction(EventListenerContext eventListenerContext, String uriListName, String uriMimeMessageRelationsName, String mailIdUriRelationsName, MessageChannel sendChannel) {
        super(eventListenerContext);
        this.uriListName = uriListName;
        this.uriMimeMessageRelationsName = uriMimeMessageRelationsName;
        this.mailIdUriRelationsName = mailIdUriRelationsName;
        this.sendChannel = sendChannel;
        this.ctx = eventListenerContext;
        mailGenerator = new WonMimeMessageGenerator("mail-templates/connect-mail.vm");
    }

    @Override
    protected void doRun(Event event) throws Exception {
        if (event instanceof ConnectFromOtherNeedEvent) {
            Connection con = ((ConnectFromOtherNeedEvent) event).getCon();

            URI responseTo = con.getNeedURI();
            URI remoteNeedUri = con.getRemoteNeedURI();

            MimeMessage originalMail = EventBotActionUtils.getMimeMessageForURI(ctx, uriMimeMessageRelationsName, responseTo);
            logger.debug(
              "Someone issued a connect for URI: " + responseTo + " sending a mail to the creator: " + MailContentExtractor
                .getFromAddressString(originalMail));

            WonMimeMessage answerMessage = mailGenerator.createMail(originalMail, remoteNeedUri, null);
            EventBotActionUtils.addMailIdWonURIRelation(ctx, mailIdUriRelationsName, answerMessage.getMessageID(), new WonURI(con.getConnectionURI(), UriType.CONNECTION));

            sendChannel.send(new GenericMessage<>(answerMessage));
        }
    }
}
