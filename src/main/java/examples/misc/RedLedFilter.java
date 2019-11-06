package examples.misc;

import com.harium.etyl.commons.graphics.Color;
import com.harium.keel.filter.ColorFilter;

public class RedLedFilter extends ColorFilter {

  public RedLedFilter(int w, int h) {
    super(w, h, Color.RED);
  }
}
