package com.kramer425.scrapeasync.actors.selenium

import akka.actor.{Actor, ActorLogging, Kill, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import com.kramer425.scrapeasync.SeleniumWork
import com.kramer425.scrapeasync.actors.selenium.SeleniumActor.DriverInitException
import org.openqa.selenium.chrome.ChromeOptions

class SeleniumSupervisor(driverOptions: ChromeOptions, count: Int) extends Actor with ActorLogging {

  private var router = {
    val routees = Vector.fill(count) {
      val r = context.actorOf(SeleniumActor.props(driverOptions))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def preStart(): Unit = log.debug("Selenium Supervisor started")

  override def postStop(): Unit = log.debug("Selenium Supervisor stopped")

  override def receive = {
    case work: SeleniumWork =>
      router.route(work, sender)
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(SeleniumActor.props(driverOptions))
      context watch r
      router = router.addRoutee(r)
    case e: DriverInitException =>
      log.error(e.cause.getMessage)
      self ! Kill
  }

}

object SeleniumSupervisor {
  final val Name = "selenium-pool-system"

  def props(driverOptions: ChromeOptions, count: Int): Props = Props(new SeleniumSupervisor(driverOptions, count))

  case class Create(driverOptions: ChromeOptions, count: Int)

}