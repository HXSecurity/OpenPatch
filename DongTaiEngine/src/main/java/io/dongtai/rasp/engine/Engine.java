package io.dongtai.rasp.engine;

import io.dongtai.rasp.engine.verify.VerifyTransform;
import java.lang.instrument.Instrumentation;

/**
 * @author owefsad
 */
public class Engine {

    public static void install(Instrumentation inst, String mode) {
        try {
            if ("Log4jPatch".equals(mode)) {
                DongTaiTransform iastClassFileTransformer = new DongTaiTransform(inst);
                inst.addTransformer(iastClassFileTransformer, true);
                iastClassFileTransformer.transform();
            } else if ("Log4jVerify".equals(mode)) {
                VerifyTransform transform = new VerifyTransform(inst);
                inst.addTransformer(transform);
                transform.transform();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
