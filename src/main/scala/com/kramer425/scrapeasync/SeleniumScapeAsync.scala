package com.kramer425.scrapeasync

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.kramer425.scrapeasync.actors.selenium.SeleniumSupervisor
import org.openqa.selenium.chrome.ChromeOptions

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{Await, Future}


class SeleniumScapeAsync(options: ChromeOptions, seleniumInstances: Int, private implicit val timeout: Timeout) {

  private val system = ActorSystem("scraping-system")
  private val supervisor = system.actorOf(SeleniumSupervisor.props(options, seleniumInstances), SeleniumSupervisor.Name)


  def submitWork(work: SeleniumWork): Future[WorkAttempt] = {
    (supervisor ? work)(timeout).mapTo[WorkAttempt]
  }

  def submitWorks(work: Seq[SeleniumWork]): Seq[Future[WorkAttempt]] = {
    work.map(work => (supervisor ? work)(timeout)).map(_.mapTo[WorkAttempt])
  }

  def shutdown(): Unit = Await.result(system.terminate(), Duration.Inf)
}

object SeleniumScapeAsync {
  def apply(options: ChromeOptions, seleniumInstances: Int, timeout: Timeout = Timeout(5 minutes)): SeleniumScapeAsync = new SeleniumScapeAsync(options, seleniumInstances, timeout)
}
