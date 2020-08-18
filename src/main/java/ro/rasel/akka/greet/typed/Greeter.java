package ro.rasel.akka.greet.typed;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Objects;
import java.util.StringJoiner;

public class Greeter extends AbstractBehavior<Greeter.GreetRequest> {

    private Greeter(ActorContext<GreetRequest> context) {
        super(context);
    }

    public static Behavior<GreetRequest> create() {
        return Behaviors.setup(Greeter::new);
    }

    @Override
    public Receive<GreetRequest> createReceive() {
        return newReceiveBuilder()
                .onMessage(GreetRequest.class, this::onGreet)
                .build();
    }

    private Behavior<GreetRequest> onGreet(GreetRequest command) {
        getContext().getLog().info("Hello {}!", command.whom);

        final ActorRef<GreeterBot.GreeterDetailsResponse> greeterDetailsResponseActorRef =
                getContext().messageAdapter(GreeterBot.GreeterDetailsResponse.class,
                        param -> new GreetRequest(param.getWhom(),
                                param.getReplyTo()));

        if (command.replyTo != null) {
            command.replyTo.tell(new GreeterBot.GreetDetailsRequest(command.whom, greeterDetailsResponseActorRef));
            return this;
        }

        return Behaviors.stopped();
    }

    public static final class GreetRequest {
        private final String whom;
        private final ActorRef<GreeterBot.GreetDetailsRequest> replyTo;

        public GreetRequest(String whom, ActorRef<GreeterBot.GreetDetailsRequest> replyTo) {
            this.whom = whom;
            this.replyTo = replyTo;
        }

        public String getWhom() {
            return whom;
        }

        public ActorRef<GreeterBot.GreetDetailsRequest> getReplyTo() {
            return replyTo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            GreetRequest greetRequest = (GreetRequest) o;
            return Objects.equals(whom, greetRequest.whom) &&
                    Objects.equals(replyTo, greetRequest.replyTo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(whom, replyTo);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", GreetRequest.class.getSimpleName() + "[", "]")
                    .add("whom='" + whom + "'")
                    .add("replyTo=" + replyTo)
                    .toString();
        }
    }
}

