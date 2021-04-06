import event.Event
import event.IEvent
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun main() {
    Thread {

        WatchDog.applyConfig(TestCase.TEST_CONFIG_FOR_ANY)


        val eventSourceA1 = Observable.interval(1, 50, TimeUnit.MILLISECONDS).map {
            Event("pageA", "disappear", "1234567890xxx", null)
        }.subscribeOn(Schedulers.newThread())

        val eventSourceA = Observable.interval(1, 50, TimeUnit.MILLISECONDS).map {
            Event("pageA", "disappear", "1234567890xxx", null)
        }.subscribeOn(Schedulers.newThread())

        val eventSourceB = Observable.interval(1, 50, TimeUnit.MILLISECONDS).map {
            Event("pageB", "appear", "1234567890xxx", null)
        }.subscribeOn(Schedulers.newThread())


        eventSourceA.observeOn(Schedulers.single()).subscribe {
            WatchDog.enqueueEvent(
                it.type,
                it.action,
                it.identifier,
                "",
                null
            )
        }

        eventSourceA1.observeOn(Schedulers.single()).subscribe {
            WatchDog.enqueueEvent(
                it.type,
                it.action,
                it.identifier,
                "",
                null
            )
        }

        eventSourceB.observeOn(Schedulers.single()).subscribe {
            WatchDog.enqueueEvent(
                it.type,
                it.action,
                it.identifier,
                "",
                null
            )
        }

    }.start()
    while (true) {}
}