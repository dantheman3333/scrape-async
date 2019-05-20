package com.kramer425.scrapeasync.actors.selenium

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import com.kramer425.scrapeasync.actors.selenium.SeleniumActor.DriverInitException
import com.kramer425.scrapeasync.{SeleniumWork, WorkFailed, WorkFinished}
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.chrome.{ChromeDriver, ChromeDriverService, ChromeOptions}


class SeleniumActor(driverOptions: ChromeOptions) extends Actor with ActorLogging {
  var webDriver: Option[ChromeDriver] = None

  override def preStart(): Unit = {
    super.preStart()

    try {

      val service = new ChromeDriverService.Builder().usingAnyFreePort().build()
      this.webDriver = Some(new ChromeDriver(service, driverOptions))

    } catch {
      case e: WebDriverException =>
        log.error(e.getMessage)
        context.parent ! DriverInitException(cause = e)
        self ! PoisonPill
      case e: IllegalStateException =>
        log.error(e.getMessage)
        context.parent ! DriverInitException(cause = e)
        self ! PoisonPill
    }

  }

  override def postStop(): Unit = {
    this.webDriver.foreach(_.quit())
    log.debug("Selenium Actor stopped")
  }

  override def receive = {
    case work: SeleniumWork => runSeleniumWork(work)
  }

  private def runSeleniumWork(work: SeleniumWork): Unit = {
    try {
      work.execute(this.webDriver.get)
      sender ! WorkFinished(work)
    } catch {
      case e: Throwable =>
        sender ! WorkFailed(work, e)
    }
  }
}

object SeleniumActor {

  def props(driverOptions: ChromeOptions): Props = Props(new SeleniumActor(driverOptions))

  final case class DriverInitException(cause: Exception)

}
