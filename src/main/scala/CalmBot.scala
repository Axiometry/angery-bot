import io.scalac.slack._
import io.scalac.slack.bots._
import io.scalac.slack.common._

import play.api.libs.json._
import play.api.libs.ws.ning.NingWSClient
import scala.concurrent.ExecutionContext.Implicits.global

class CalmBot(override val bus: MessageEventBus) extends IncomingMessageListener {
  val textWhitelist = "[\\w\\s:'\\,\\!\\?\"\\.-\\@“”’<>]{15,}"
  val (hepyThreshold, angeryThreshold) = (0.90, 0.05)

  val wsClient = NingWSClient()

  val azureKey = context.system.settings.config.getString("azure-cs-text-key")

  def receive: Receive = {
    case bm @ BaseMessage(text, channel, user, dateTime, edited) =>
      //println(s"<@$user> $text")
      if(text matches textWhitelist) {
        val body = Json.obj(
          "documents" -> Json.arr(
            Json.obj(
              "language" -> "en",
              "id" -> "message",
              "text" -> text
            )
          )
        )

        wsClient
          .url("https://westus.api.cognitive.microsoft.com/text/analytics/v2.0/sentiment")
          .withHeaders("Content-Type" -> "application/json", "Ocp-Apim-Subscription-Key" -> azureKey)
          .post(body)
          .map { response => if((200 to 299).contains(response.status)) {
            val scoreOpt = (response.json \ "documents" \\ "score").headOption.flatMap(_.asOpt[Double])
            println("Received score: " + scoreOpt)
            if(scoreOpt.exists(_ < angeryThreshold)) // VERY ANGERY
              publish(OutboundMessage(channel, s"<@$user> ok"))
            else if(scoreOpt.exists(_ > hepyThreshold)) // VERY HEPY
              publish(OutboundMessage(channel, s"<@$user> good for you"))
          }}
      }
  }
}

