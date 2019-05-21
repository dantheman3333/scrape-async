package com.kramer425.scrapeasync

import org.openqa.selenium.chrome.ChromeDriver

trait SeleniumWork {
  def execute(webDriver: ChromeDriver)
}

sealed trait WorkAttempt

final case class WorkFinished(work: SeleniumWork) extends WorkAttempt

final case class WorkFailed(work: SeleniumWork, e: Throwable) extends WorkAttempt

