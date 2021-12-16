package io.dongtai.rasp.engine.verify;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LoggerConfigAdapter extends ClassVisitor {

    private ClassLoader classLoader;
    private String dnsLogAddr;

    public LoggerConfigAdapter(ClassVisitor classVisitor, ClassLoader classLoader, String dnsLogAddr) {
        super(Opcodes.ASM9, classVisitor);
        this.classLoader = classLoader;
        this.dnsLogAddr = dnsLogAddr;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
            String[] exceptions) {
        if ("processLogEvent".equals(name)) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            System.out.println("[io.dongtai.rasp] Enter ProcessLogEvent Method.");
            return new LoggerConfigAdviceAdapter(api, mv, access, name, descriptor, classLoader, dnsLogAddr);
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
