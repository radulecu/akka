package ro.rasel.akka.greet.typed;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class GreeterMain extends AbstractBehavior<GreeterMain.Hello> {

    private GreeterMain(ActorContext<Hello> context) {
        super(context);
    }

    public static Behavior<Hello> create() {
        return Behaviors.setup(GreeterMain::new);
    }

    @Override
    public Receive<Hello> createReceive() {
        return newReceiveBuilder()
                .onMessage(Hello.class, this::onSayHello)
                .build();
    }

    private Behavior<Hello> onSayHello(Hello command) {
        ActorRef<GreeterBot.GreetDetailsRequest> replyTo =
                getContext().spawn(GreeterBot.create(3), command.getName());
        ActorRef<Greeter.GreetRequest> greeter = getContext().spawn(Greeter.create(), "greeter");

        greeter.tell(new Greeter.GreetRequest(command.getName(), replyTo));

        return this;
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
