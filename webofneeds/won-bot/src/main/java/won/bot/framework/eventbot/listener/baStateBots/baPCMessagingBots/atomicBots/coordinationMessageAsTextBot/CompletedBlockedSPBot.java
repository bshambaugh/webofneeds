package won.bot.framework.eventbot.listener.baStateBots.baPCMessagingBots.atomicBots.coordinationMessageAsTextBot;

import won.bot.framework.eventbot.listener.baStateBots.BATestBotScript;
import won.bot.framework.eventbot.listener.baStateBots.BATestScriptAction;
import won.node.facet.impl.WON_TX;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Danijel
 * Date: 30.4.14.
 */
public class CompletedBlockedSPBot extends BATestBotScript
{

  @Override
  protected List<BATestScriptAction> setupActions() {
    List<BATestScriptAction> actions = new ArrayList();

    actions.add(new BATestScriptAction(false, "MESSAGE_CLOSE", URI.create(WON_TX.STATE_COMPLETED.getURI())));
    actions.add(new BATestScriptAction(true, "MESSAGE_CLOSED", URI.create(WON_TX.STATE_CLOSING.getURI())));
    return actions;
  }
}

