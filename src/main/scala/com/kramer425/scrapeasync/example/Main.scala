package com.kramer425.scrapeasync.example


import com.kramer425.scrapeasync.util.FutureWorkAttemptPrinter
import com.kramer425.scrapeasync.{SeleniumScapeAsync, SeleniumWork, WorkAttempt}
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Main {

  def main(args: Array[String]): Unit = {

    //Set chrome driver location in path, as system property, or in ChromeOptions
    //System.setProperty("webdriver.chrome.driver", "...")

    val options = new ChromeOptions()
    options.addArguments("--headless")

    val selAsync = SeleniumScapeAsync(options, seleniumInstances = 10)

    val works = Seq.fill(20)(new GoogleWork())
    val completions: Seq[Future[WorkAttempt]] = selAsync.submitWorks(works)

    FutureWorkAttemptPrinter(completions)

    val all = Future.sequence(completions)

    Await.result(all, 10 seconds)
    println("all done")

    selAsync.shutdown()
  }

}

class GoogleWork extends SeleniumWork {
  override def execute(webDriver: WebDriver): Unit = {
    webDriver.get("https://www.google.com")

    val title = webDriver.getTitle

  }
}
