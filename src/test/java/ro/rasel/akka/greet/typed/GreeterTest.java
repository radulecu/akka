package ro.rasel.akka.greet.typed;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import org.junit.ClassRule;
import org.junit.Test;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class GreeterTest {

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Test
    public void shouldSendGreetRequestWhenReceivingGreet() {
        TestProbe<GreeterBot.GreetDetailsRequest> testProbe = testKit.createTestProbe();
        ActorRef<Greeter.GreetRequest> underTest = testKit.spawn(Greeter.create(), "greeter");

        underTest.tell(new Greeter.GreetRequest("Charles", testProbe.getRef()));

        final GreeterBot.GreetDetailsRequest greetDetailsRequest = testProbe.receiveMessage();
        assertThat(greetDetailsRequest.getWhom(), is("Charles"));
        assertNotNull(greetDetailsRequest.getFrom());
    }
}
