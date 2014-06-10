package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import scala.concurrent.Future
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.duration._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import com.typesafe.plugin._


object Application extends Controller {
  var x = ""
  final val staticUrl = "http://photos.state.gov/libraries/france/45994/visaenglish/vpending.pdf"
  keepCheckingForLastmodified(staticUrl)
  def index = Action {
    Ok(views.html.index())
  }
  def getLastUpdate(url:String):Future[String] = {
    WS.url(url).get.flatMap { response =>
      val status = response.status
      if (status == 200) {
      val lastModified = response.header("Last-Modified")
      println(lastModified, "lastModified")
      //          Scheduler.schedule(() => println("Do something"), 0L, 5L, TimeUnit.MINUTES)


      Future.successful(lastModified.getOrElse("no lastmodified"))

      } else Future.failed(new Throwable("not 200"))
        //        println("here")
        //        println(url)
      }
    }
  def keepCheckingForLastmodified(url:String) = {
    val lModified = getLastUpdate(url)
    Akka.system.scheduler.schedule(0 millisecond, 15 seconds) {
      getLastUpdate(url).map { l =>
        println(l, "l")
        println(x, "x")
        if (l != x) {

          println("gonna send email")
          try {
            val mail = use[MailerPlugin].email
            val from = "freesko@gmail.com"
            val to = "freesko@gmail.com"
            mail.setSubject("us visa status lastmodified!")
            mail.setRecipient(to)
            mail.setFrom(from)
            mail.sendHtml(l);
            x = l

          } catch {
            case e:Exception => Logger.error("there was an error",e);
          }

        }
      }
    }
  }
  def getLastModified(url:String) = Action {
    val lModified = getLastUpdate(url)
    Async {
      lModified.map(l => {
        if (l == x) Ok(s"$l not modified") else {
          Ok(s"$l modified")
        }
      })
    }


  }


}