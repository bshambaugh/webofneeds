package won.node.camel.processor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies the annotated class as a message processor used to implement the specified
 * facet behavior for the specified type and direction.
 * This implementation is always executed after the corresponding FixedMessageProcessor, if
 * no corresponding FacetMessageProcessor is found.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultFacetMessageProcessor
{
  String direction();
  String messageType();
}
