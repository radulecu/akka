package ro.rasel.akka.greet.classic;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.Objects;
import java.util.StringJoiner;

public class GreeterBot extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final int max;
    private int greetingCounter;

    private GreeterBot(int max) {
        this.max = max;
    }

    public static Props props(int max) {
        return Props.create(GreeterBot.class, () -> new GreeterBot(max));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GreetDetailsRequest.class, this::onGreeted)
                .build();
    }

    private void onGreeted(GreetDetailsRequest message) {
        greetingCounter++;
        log.info("Greeting {} for {}", greetingCounter, message.getWhom());
        if (greetingCounter == max) {
            getContext().stop(getSelf());
        } else {
            message.getFrom().tell(
                    new GreeterDetailsResponse(message.getWhom(), getContext().actorSelection(getSelf().path())), null);
        }
    }

    public static final class GreetDetailsRequest {
        private final String whom;
        private final ActorSelection from;

        public GreetDetailsRequest(String whom, ActorSelection from) {
            this.whom = whom;
            this.from = from;
        }

        public String getWhom() {
            return whom;
        }

        public ActorSelection getFrom() {
            return from;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            GreetDetailsRequest that = (GreetDetailsRequest) o;
            return Objects.equals(whom, that.whom) &&
                    Objects.equals(from, that.from);
        }

        @Override
        public int hashCode() {
            return Objects.hash(whom, from);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", GreetDetailsRequest.class.getSimpleName() + "[", "]")
                    .add("whom='" + whom + "'")
                    .add("from=" + from)
                    .toString();
        }
    }

    public static final class GreeterDetailsResponse {
        final String whom;
        final ActorSelection replyTo;

        public GreeterDetailsResponse(String whom, ActorSelection replyTo) {
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
            GreeterDetailsResponse that = (GreeterDetailsResponse) o;
            return Objects.equals(whom, that.whom) &&
                    Objects.equals(replyTo, that.replyTo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(whom, replyTo);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", GreeterDetailsResponse.class.getSimpleName() + "[", "]")
                    .add("whom='" + whom + "'")
                    .add("replyTo=" + replyTo)
                    .toString();
        }
    }

}
