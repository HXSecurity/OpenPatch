package io.dongtai.rasp.engine.plugin.log4j;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author owefsad
 */
public class Log4jAdapter extends ClassVisitor {

    public Log4jAdapter(ClassVisitor classVisitor) {
        super(Opcodes.ASM9, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
            String[] exceptions) {
        if ("lookup".equals(name)) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            System.out.println("[io.dongtai.rasp] Fixed log4j2 JndiLookup class.");
            return new Log4jAdviceAdapter(api, mv, access, name, descriptor);
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
