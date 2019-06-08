# scrape-async

Scrape websites with chrome & selenium in an asyncronous fashion. 

#### Example:

Do your selenium work inside of `execute` with side effects:

```scala
class GoogleWork extends SeleniumWork {
  override def execute(webDriver: ChromeDriver): Unit = {
    webDriver.get("https://www.google.com")

    val title = webDriver.getTitle
    //...
  }
}
```

Specify how many selenium/chrome instances to use. When you submit the jobs, they will be sent to the workers with round-robin scheduling. 
```scala
object Main {

  def main(args: Array[String]): Unit = {

    //Set chrome driver location in path, as system property, or in ChromeOptions
    //System.setProperty("webdriver.chrome.driver", "...")

    val options = new ChromeOptions()
    options.addArguments("--headless")

    val selAsync = SeleniumScapeAsync(options, seleniumInstances = 10)

    val works = Seq.fill(20)(new GoogleWork())
    val completions: Seq[Future[WorkAttempt]] = selAsync.submitWorks(works)

    //optional status printer
    FutureWorkAttemptPrinter(completions)

    val all = Future.sequence(completions)

    Await.result(all, 10 seconds)
    println("all done")

    selAsync.shutdown()
  }

}

```
