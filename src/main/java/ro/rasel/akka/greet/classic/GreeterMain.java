package ro.rasel.akka.greet.classic;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;

public class GreeterMain extends AbstractActor {

    private GreeterMain() {
    }

    public static Props props() {
        return Props.create(GreeterMain.class, GreeterMain::new);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Hello.class, this::onSayHello)
                .build();
    }

    private void onSayHello(Hello command) {
        ActorSelection replyTo = getContext().actorSelection(
                getContext().getSystem().actorOf(GreeterBot.props(3), command.getName()).path());
        ActorSelection greeter = getContext().actorSelection(
                getContext().getSystem().actorOf(Greeter.props(), "greeter").path());

        greeter.tell(new Greeter.GreetRequest(command.getName(), replyTo), null);
    }

    public static class Hello {
        private final String name;

        public Hello(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
