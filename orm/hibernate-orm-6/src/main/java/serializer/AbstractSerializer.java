package serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import domain.model.DomainEvent;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractSerializer extends Module {

    private final ObjectMapper objectMapper;

    protected AbstractSerializer(final boolean isCompact) {
        this(isCompact, false);
    }

    protected AbstractSerializer(final boolean isCompact, final boolean isPretty) {
        this(new JsonMapper(), isCompact, isPretty);
    }

    protected AbstractSerializer(final ObjectMapper objectMapper, final boolean isCompact, final boolean isPretty) {

        final List<Module> modules = new LinkedList<>();
        modules.add(new JavaTimeModule());
        modules.add(this);

        this.objectMapper = objectMapper;

        this.objectMapper
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)
                .setSerializationInclusion(isCompact ? JsonInclude.Include.NON_ABSENT : JsonInclude.Include.ALWAYS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.INDENT_OUTPUT, isPretty)
                .registerModules(modules);
    }

    protected ObjectMapper objectMapper() {
        return this.objectMapper;
    }

    @Override
    public String getModuleName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(final SetupContext context) {
        // no-op
    }
}
