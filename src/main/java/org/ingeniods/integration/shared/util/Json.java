package org.ingeniods.integration.shared.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Json {

  private Json() {
  }

  public static final Gson GSON = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
}
