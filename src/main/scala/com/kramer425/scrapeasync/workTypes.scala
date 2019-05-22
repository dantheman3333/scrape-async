package com.kramer425.scrapeasync

import org.openqa.selenium.chrome.ChromeDriver

trait SeleniumWork {
  def execute(webDriver: ChromeDriver)
}

sealed abstract case class WorkAttempt(work: SeleniumWork)

final case class WorkFinished(override val work: SeleniumWork) extends WorkAttempt(work)

final case class WorkFailed(override val work: SeleniumWork, e: Throwable) extends WorkAttempt(work)

