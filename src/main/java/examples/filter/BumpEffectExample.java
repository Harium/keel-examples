package examples.filter;

import com.harium.etyl.Etyl;
import com.harium.etyl.commons.context.Application;
import examples.filter.application.BumpEffectApplication;

public class BumpEffectExample extends Etyl {

    public BumpEffectExample() {
        super(1024, 512);
    }

    public static void main(String[] args) {
        BumpEffectExample example = new BumpEffectExample();
        example.init();
    }

    public Application startApplication() {
        return new BumpEffectApplication(w, h);
    }
}
