import io.scalac.slack._
import io.scalac.slack.bots._
import io.scalac.slack.common._

class CluckBot(override val bus: MessageEventBus) extends IncomingMessageListener {
  def receive: Receive = {
    case bm @ BaseMessage(text, channel, user, dateTime, edited) =>
      BotInfoKeeper.current match {
        case Some(botInfo) => Seq(s"<@${botInfo.id}>: ", s"<@${botInfo.id}> ").find(text startsWith _).map { trigger =>
          val result = text.drop(trigger.length).trim
          if(result.nonEmpty)
            publish(OutboundMessage(channel, result.map(ch => if(scala.util.Random.nextBoolean()) ch.toUpper else ch.toLower)))
        }
        case None =>
      }
  }
}
