import io.scalac.slack._
import io.scalac.slack.bots._
import io.scalac.slack.common._

import scala.util.Random

class CluckBot(override val bus: MessageEventBus) extends IncomingMessageListener {
  def receive: Receive = {
    case bm @ BaseMessage(text, channel, user, dateTime, edited) =>
      BotInfoKeeper.current match {
        case Some(botInfo) =>
          Seq(s"<@${botInfo.id}>: ", s"<@${botInfo.id}> ")
            .find(text startsWith _)
            .map { trigger =>
              val targetText = text.drop(trigger.length).trim

              if(targetText.nonEmpty) {
                val cluckedText = targetText.map(ch => if(Random.nextBoolean) ch.toUpper else ch.toLower)
                publish(OutboundMessage(channel, cluckedText))
              }
            }
        case None =>
      }
  }
}
