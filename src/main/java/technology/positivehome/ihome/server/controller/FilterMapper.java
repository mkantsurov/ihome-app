package technology.positivehome.ihome.server.controller;

import technology.positivehome.ihome.domain.constant.SearchField;
import technology.positivehome.ihome.server.model.SearchParam;

import java.util.*;
import java.util.stream.Collectors;

import static technology.positivehome.ihome.server.model.SearchParam.*;

public class FilterMapper {
    public static List<SearchParam> fromErrorMessageFilter(List<String> filter) {
        List<SearchParam> result = new ArrayList<>();

        Optional.ofNullable(filter).ifPresent(f -> f.forEach(value -> {
            result.add(constructSearchParam(value));
        }));
        return !result.isEmpty() ? result : null;
    }

    private static SearchParam constructSearchParam(String filter) {
        SearchField key;
        String predicate;
        String value;

        // todo add more if needed
        Map<String, Integer> operatorsMap = Map.of(
                PREDICAT_EQ, filter.indexOf(PREDICAT_EQ),
                PREDICAT_NOT_EQ, filter.indexOf(PREDICAT_NOT_EQ),
                PREDICAT_GTE, filter.indexOf(PREDICAT_GTE),
                PREDICAT_LTE, filter.indexOf(PREDICAT_LTE),
                PREDICAT_GT, filter.indexOf(PREDICAT_GT),
                PREDICAT_LT, filter.indexOf(PREDICAT_LT),
                "?", filter.indexOf("?"),
                "!?", filter.indexOf("!?")
        );

        Map<String, Integer> sorted = operatorsMap.entrySet().stream()
                .filter(stringIntegerEntry -> stringIntegerEntry.getValue() > -1)
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<String> finalList = new ArrayList<>();
        int index = -1;
        for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
            if (index  == -1) {
                finalList.add(entry.getKey());
                index = entry.getValue();
            } else if (entry.getValue() == index ){
                finalList.add(entry.getKey());
            }

        }
        if (index < 1) {
            return null;
        }

        finalList.sort(Comparator.comparingInt(String::length));
        if (!finalList.isEmpty()) {
            predicate = finalList.get(0);
        } else {
            return null;
        }
        key = switch(filter.substring(0, index)) {
            case "id" -> SearchField.ERROR_MESSAGE_LOG_ID;
            default -> throw new IllegalStateException("Unexpected value: " + filter.substring(0, index));
        };
        value = filter.substring(filter.indexOf(predicate) + predicate.length());
        return new SearchParam(convertToDbOperator(predicate), key, value);
    }

    public static String convertToDbOperator(String operator) {
        if (operator == null) {
            return PREDICAT_TRUE;
        }
        switch (operator) {
            case "?":
                return PREDICAT_ILIKE;
            case "!?":
                return PREDICAT_NOT_ILIKE;
        }
        return operator;
    }
}
