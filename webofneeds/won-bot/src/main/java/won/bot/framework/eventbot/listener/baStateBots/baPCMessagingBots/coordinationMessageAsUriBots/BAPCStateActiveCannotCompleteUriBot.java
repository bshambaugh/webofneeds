package won.bot.framework.eventbot.listener.baStateBots.baPCMessagingBots.coordinationMessageAsUriBots;

import won.bot.framework.eventbot.listener.baStateBots.BATestBotScript;
import won.bot.framework.eventbot.listener.baStateBots.BATestScriptAction;
import won.node.facet.impl.WON_TX;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Danijel
 * Date: 6.3.14.
 * Time: 16.45
 * To change this template use File | Settings | File Templates.
 */
public class BAPCStateActiveCannotCompleteUriBot extends BATestBotScript {

    @Override
    protected List<BATestScriptAction> setupActions() {
        List<BATestScriptAction> actions = new ArrayList();
        actions.add(new BATestScriptAction(true, URI.create(WON_TX
          .MESSAGE_CANNOTCOMPLETE.getURI()), URI.create(WON_TX.STATE_ACTIVE.getURI())));
        actions.add(new BATestScriptAction(false, URI.create(WON_TX
          .MESSAGE_NOTCOMPLETED.getURI()), URI.create(WON_TX.STATE_NOT_COMPLETING.getURI())));
        return actions;
    }
}