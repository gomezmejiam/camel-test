package org.ingeniods.integration.shared.jsvalidation;

import org.ingeniods.integration.shared.domain.model.ValidationMessageValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MessageService {

    private final Map<String, List<ValidationMessageValue>> map;

    public MessageService(Map<String, List<ValidationMessageValue>> map) {
        this.map = map;
    }

    public Optional<String> getMessage(String entity, String path, String type) {
        List<ValidationMessageValue> list = map.getOrDefault(entity, Collections.emptyList());
        Optional<String> validationMessage =
                list.stream().filter(vm -> vm.getPath().contentEquals(path))
                        .findFirst()
                        .map( x -> x.getMessageDescription(type));
        return validationMessage;
    }

}
