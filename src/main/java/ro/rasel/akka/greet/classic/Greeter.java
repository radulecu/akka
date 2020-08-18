package ro.rasel.akka.greet.classic;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.Objects;
import java.util.StringJoiner;

public class Greeter extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private Greeter() {
    }

    public static Props props() {
        return Props.create(Greeter.class, Greeter::new);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GreetRequest.class, this::onGreet)
                .build();
    }

    private void onGreet(GreetRequest greetRequest) {
        getContext().become(receiveBuilder()
                .match(GreeterBot.GreeterDetailsResponse.class, this::onGreet)
                .build());

        onGreet(greetRequest.getWhom(), greetRequest.getReplyTo());
    }

    private void onGreet(GreeterBot.GreeterDetailsResponse greeterDetailsResponse) {
        onGreet(greeterDetailsResponse.getWhom(), greeterDetailsResponse.getReplyTo());
    }

    private void onGreet(String whom, ActorSelection replyTo) {
        log.info("Hello {}!", whom);
        if (whom != null) {
            replyTo.tell(new GreeterBot.GreetDetailsRequest(whom, getContext().actorSelection(self().path())), null);
        } else {
            getContext().stop(self());
        }
    }

    public static final class GreetRequest {
        private final String whom;
        private final ActorSelection replyTo;

        public GreetRequest(String whom, ActorSelection replyTo) {
            this.whom = whom;
            this.replyTo = replyTo;
        }

        public String getWhom() {
            return whom;
        }

        public ActorSelection getReplyTo() {
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
