package won.bot.framework.events.listener.baStateBots.baCCMessagingBots.atomicBots.coordinationMessageAsUriBot;

import won.bot.framework.events.listener.baStateBots.BATestBotScript;
import won.bot.framework.events.listener.baStateBots.BATestScriptAction;
import won.bot.framework.events.listener.baStateBots.NopAction;
import won.bot.framework.events.listener.baStateBots.WON_TX;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Danijel
 * Date: 22.4.14.
 */
public class CompletingSPCancelingFAilingUriBot extends BATestBotScript
{          //CompletingSPCancelingFailingUriBot

  @Override
  protected List<BATestScriptAction> setupActions() {
    List<BATestScriptAction> actions = new ArrayList();

    //automatic
    //actions.add(new BATestScriptAction(false, "MESSAGE_CANCEL", URI.create(WON_BA.STATE_CANCELING.getURI()), 3));

    actions.add(new NopAction());
    actions.add(new BATestScriptAction(true, URI.create(WON_TX.MESSAGE_FAIL.getURI()), URI.create(WON_TX.STATE_CANCELING_COMPLETING.getURI())));
    actions.add(new BATestScriptAction(false, URI.create(WON_TX.MESSAGE_FAILED.getURI()), URI.create(WON_TX.STATE_FAILING_ACTIVE_CANCELING_COMPLETING.getURI())));



    return actions;
  }
}