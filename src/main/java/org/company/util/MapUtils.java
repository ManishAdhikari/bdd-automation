package org.company.util;

import java.util.Map;
import java.util.Objects;

public class MapUtils {

  public static <K, V> boolean isNullOrEmpty(Map<K, V> map) {
    return Objects.isNull(map) || map.isEmpty();
  }
}
