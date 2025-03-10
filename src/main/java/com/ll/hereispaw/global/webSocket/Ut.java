package com.ll.hereispaw.global.webSocket;

import java.util.LinkedHashMap;
import java.util.Map;

public class Ut {public static <K, V> Map<K, V> mapOf(Object... args) {
    Map<K, V> map = new LinkedHashMap<>();
    int size = args.length / 2;

    // 인자를 2개씩 묶어서 키-값 쌍으로 처리
    for (int i = 0; i < size; i++) {
        int keyIndex = i * 2;
        int valueIndex = keyIndex + 1;
        K key = (K) args[keyIndex];     // 짝수 인덱스는 키로 사용
        V value = (V) args[valueIndex];  // 홀수 인덱스는 값으로 사용
        map.put(key, value);
    }
    return map;
}
}