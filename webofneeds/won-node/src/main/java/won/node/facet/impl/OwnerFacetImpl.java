package won.node.facet.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import won.protocol.exception.IllegalMessageForConnectionStateException;
import won.protocol.exception.NoSuchConnectionException;
import won.protocol.exception.WonProtocolException;
import won.protocol.model.ChatMessage;
import won.protocol.model.Connection;
import won.protocol.model.FacetType;
import won.protocol.repository.ChatMessageRepository;
import won.protocol.repository.ConnectionRepository;
import won.protocol.repository.EventRepository;
import won.protocol.repository.FacetRepository;
import won.protocol.service.ConnectionCommunicationService;
import won.protocol.util.DataAccessUtils;

import java.net.URI;
import java.util.Date;
import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: gabriel
 * Date: 16.09.13
 * Time: 18:42
 * To change this template use File | Settings | File Templates.
 */
public class OwnerFacetImpl extends FacetImpl {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ConnectionCommunicationService ownerFacingConnectionClient;

    private ExecutorService executorService;

    @Autowired
    private ConnectionRepository connectionRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private FacetRepository facetRepository;

    @Override
    public FacetType getFacetType() {
        return FacetType.OwnerFacet;
    }

    @Override
    public void textMessage(final URI connectionURI, final String message) throws NoSuchConnectionException, IllegalMessageForConnectionStateException {
        //load connection, checking if it exists
        Connection con = DataAccessUtils.loadConnection(connectionRepository, connectionURI);
        //perform state transit (should not result in state change)
        //ConnectionState nextState = performStateTransit(con, ConnectionEventType.PARTNER_MESSAGE);
        //construct chatMessage object to store in the db
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setCreationDate(new Date());
        chatMessage.setLocalConnectionURI(con.getConnectionURI());
        chatMessage.setMessage(message);
        chatMessage.setOriginatorURI(con.getNeedURI());
        //save in the db
        chatMessageRepository.saveAndFlush(chatMessage);
        //send to the need side
        executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    ownerFacingConnectionClient.textMessage(connectionURI, message);
                } catch (WonProtocolException e) {
                    logger.warn("caught WonProtocolException:", e);
                }
            }
        });
    }

    public void setOwnerFacingConnectionClient(ConnectionCommunicationService ownerFacingConnectionClient) {
        this.ownerFacingConnectionClient = ownerFacingConnectionClient;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
