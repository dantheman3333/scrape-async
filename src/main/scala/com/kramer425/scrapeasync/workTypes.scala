package com.kramer425.scrapeasync

import org.openqa.selenium.WebDriver

trait SeleniumWork {
  def execute(webDriver: WebDriver)
}

sealed trait WorkAttempt
final case class WorkFinished(work: SeleniumWork) extends WorkAttempt
final case class WorkFailed(work: SeleniumWork, e: Throwable) extends WorkAttempt

