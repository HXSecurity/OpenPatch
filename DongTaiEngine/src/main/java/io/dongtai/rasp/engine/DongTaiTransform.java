package io.dongtai.rasp.engine;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;

import io.dongtai.rasp.engine.plugin.log4j.Log4jAdapter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * @author owefsad
 */
public class DongTaiTransform implements ClassFileTransformer {

    private final Instrumentation inst;
    private final static String JNDI_LOOKUP_CLASS = " org/apache/logging/log4j/core/lookup/JndiLookup".substring(1);

    public DongTaiTransform(Instrumentation inst) {
        this.inst = inst;
    }

    @Override
    public byte[] transform(ClassLoader classLoader, String internalClassName, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] bytes) {
        if (internalClassName != null && internalClassName.equals(JNDI_LOOKUP_CLASS)) {
            System.out.println("[io.dongtai.rasp] Hit log4j2 JndiLookup class, try to fix.");
            final ClassReader cr = new ClassReader(bytes);
            final ClassWriter cw = new ClassWriter(cr, COMPUTE_FRAMES);
            cr.accept(new Log4jAdapter(cw), ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        }
        return null;
    }

    /**
     * 执行字节码转换
     */
    public void transform() throws UnmodifiableClassException {
        Class<?>[] waitingReTransformClasses = inst.getAllLoadedClasses();

        for (final Class<?> waitingReTransformClass : waitingReTransformClasses) {
            if (inst.isModifiableClass(waitingReTransformClass) && waitingReTransformClass.getName()
                    .equals(JNDI_LOOKUP_CLASS.replace("/", "."))) {
                System.out.println("[io.dongtai.rasp] found vul class for slf4j2");
                inst.retransformClasses(waitingReTransformClass);
            }
        }
    }

}
