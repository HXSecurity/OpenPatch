package io.dongtai.rasp.engine.plugin.log4j;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

public class Log4jAdviceAdapter extends AdviceAdapter {

    public Log4jAdviceAdapter(int api, MethodVisitor methodVisitor, int access, String name,
            String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
    }

    @Override
    protected void onMethodEnter() {
        push((String) null);
        mv.visitInsn(ARETURN);
    }
}
